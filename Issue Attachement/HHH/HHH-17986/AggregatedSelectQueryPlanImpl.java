/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.query.sqm.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.LockOptions;
import org.hibernate.ScrollMode;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.graph.spi.AppliedGraph;
import org.hibernate.internal.EmptyScrollableResults;
import org.hibernate.query.ResultListTransformer;
import org.hibernate.query.TupleTransformer;
import org.hibernate.query.spi.DomainQueryExecutionContext;
import org.hibernate.query.spi.Limit;
import org.hibernate.query.spi.QueryOptions;
import org.hibernate.query.spi.QueryParameterBindings;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.query.spi.SelectQueryPlan;
import org.hibernate.sql.exec.spi.Callback;
import org.hibernate.sql.results.spi.ListResultsConsumer.UniqueSemantic;
import org.hibernate.sql.results.spi.ResultsConsumer;

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;

/**
 * @author Steve Ebersole
 */
public class AggregatedSelectQueryPlanImpl<R> implements SelectQueryPlan<R> {
	private final SelectQueryPlan<R>[] aggregatedQueryPlans;

	public AggregatedSelectQueryPlanImpl(SelectQueryPlan<R>[] aggregatedQueryPlans) {
		this.aggregatedQueryPlans = aggregatedQueryPlans;
	}

	@Override
	public <T> T executeQuery(DomainQueryExecutionContext executionContext, ResultsConsumer<T, R> resultsConsumer) {
		throw new UnsupportedOperationException();
	}


	@Override
	public List<R> performList(DomainQueryExecutionContext executionContext) {
		// FIX: override context with no-limit options
		DomainQueryExecutionContext noLimitExecutionContext = new NoLimitDomainQueryExecutionContext(executionContext);

		final Limit effectiveLimit = executionContext.getQueryOptions().getEffectiveLimit();
		final int maxRowsJpa = effectiveLimit.getMaxRowsJpa();
		if (maxRowsJpa == 0) {
			return Collections.emptyList();
		}
		int elementsToSkip = effectiveLimit.getFirstRowJpa();
		final List<R> overallResults = new ArrayList<>();

		for (SelectQueryPlan<R> aggregatedQueryPlan : aggregatedQueryPlans) {
			// FIX: using the noLimitExecutionContext instead of original executionContext
			final List<R> list = aggregatedQueryPlan.performList(noLimitExecutionContext);
			final int size = list.size();
			if (size <= elementsToSkip) {
				// More elements to skip than the collection size
				elementsToSkip -= size;
				continue;
			}
			final int availableElements = size - elementsToSkip;
			if (overallResults.size() + availableElements >= maxRowsJpa) {
				// This result list is the last one i.e. fulfills the limit
				final int end = elementsToSkip + (maxRowsJpa - overallResults.size());
				for (int i = elementsToSkip; i < end; i++) {
					overallResults.add(list.get(i));
				}
				break;
			} else if (elementsToSkip > 0) {
				// We can skip a part of this result list
				// FIX: start from "elementsToSkip" instead of original "availableElements"
				for (int i = elementsToSkip; i < size; i++) {
					overallResults.add(list.get(i));
				}
				elementsToSkip = 0;
			} else {
				overallResults.addAll(list);
			}
		}

		return overallResults;
	}

	@Override
	public ScrollableResultsImplementor<R> performScroll(ScrollMode scrollMode, DomainQueryExecutionContext executionContext) {
		if (executionContext.getQueryOptions().getEffectiveLimit().getMaxRowsJpa() == 0) {
			return EmptyScrollableResults.INSTANCE;
		}
		throw new UnsupportedOperationException();
	}

	// FIX: new classes
	private static class NoLimitDomainQueryExecutionContext implements DomainQueryExecutionContext {

		private final DomainQueryExecutionContext delegate;
		private final NoLimitQueryOptions noLimitOptions;

		public NoLimitDomainQueryExecutionContext(DomainQueryExecutionContext executionContext) {
			this.delegate = executionContext;
			this.noLimitOptions = new NoLimitQueryOptions(delegate.getQueryOptions());
		}

		// @formatter:off
		@Override public QueryOptions getQueryOptions() { return noLimitOptions; }

		@Override public QueryParameterBindings getQueryParameterBindings() { return delegate.getQueryParameterBindings(); }
		@Override public Callback getCallback() { return delegate.getCallback(); }
		@Override public boolean hasCallbackActions() { return delegate.hasCallbackActions(); }
		@Override public SharedSessionContractImplementor getSession() { return delegate.getSession(); }
		// @formatter:on
	}

	private static class NoLimitQueryOptions implements QueryOptions {

		private final QueryOptions delegate;

		public NoLimitQueryOptions(QueryOptions queryOptions) {
			this.delegate = queryOptions;
		}

		// @formatter:off
		@Override public Limit getLimit() { return Limit.NONE; }

		@Override public Integer getTimeout() { return delegate.getTimeout(); }
		@Override public FlushMode getFlushMode() { return delegate.getFlushMode(); }
		@Override public Boolean isReadOnly() { return delegate.isReadOnly(); }
		@Override public AppliedGraph getAppliedGraph() { return delegate.getAppliedGraph(); }
		@Override public TupleTransformer<?> getTupleTransformer() { return delegate.getTupleTransformer(); }
		@Override public ResultListTransformer<?> getResultListTransformer() { return delegate.getResultListTransformer(); }
		@Override public Boolean isResultCachingEnabled() { return delegate.isResultCachingEnabled(); }
		@Override public CacheRetrieveMode getCacheRetrieveMode() { return delegate.getCacheRetrieveMode(); }
		@Override public CacheStoreMode getCacheStoreMode() { return delegate.getCacheStoreMode(); }
		@Override public CacheMode getCacheMode() { return delegate.getCacheMode(); }
		@Override public String getResultCacheRegionName() { return delegate.getResultCacheRegionName(); }
		@Override public Boolean getQueryPlanCachingEnabled() { return delegate.getQueryPlanCachingEnabled(); }
		@Override public Set<String> getEnabledFetchProfiles() { return delegate.getEnabledFetchProfiles(); }
		@Override public Set<String> getDisabledFetchProfiles() { return delegate.getDisabledFetchProfiles(); }
		@Override public LockOptions getLockOptions() { return delegate.getLockOptions(); }
		@Override public String getComment() { return delegate.getComment(); }
		@Override public List<String> getDatabaseHints() { return delegate.getDatabaseHints(); }
		@Override public Integer getFetchSize() { return delegate.getFetchSize(); }
		@Override public Integer getFirstRow() { return delegate.getFirstRow(); }
		@Override public Integer getMaxRows() { return delegate.getMaxRows(); }
		@Override public Limit getEffectiveLimit() { return delegate.getEffectiveLimit(); }
		@Override public boolean hasLimit() { return delegate.hasLimit(); }
		@Override public UniqueSemantic getUniqueSemantic() { return delegate.getUniqueSemantic(); }
		// @formatter:on
	}
}

