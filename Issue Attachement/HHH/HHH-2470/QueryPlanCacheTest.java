package org.hibernate.engine.query;

import org.hibernate.engine.query.sql.*;
import org.hibernate.type.IntegerType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class QueryPlanCacheTest {
	
	private QueryPlanCache cache = new QueryPlanCache(null);
	
	@Test
	public void testGetNativeSQLQueryPlan() {
		NativeSQLQuerySpecification firstSpec = createSpec();

		NativeSQLQuerySpecification secondSpec = createSpec();
		
		NativeSQLQueryPlan firstPlan = cache.getNativeSQLQueryPlan(firstSpec);
		NativeSQLQueryPlan secondPlan = cache.getNativeSQLQueryPlan(secondSpec);
		
		Assert.assertEquals(firstPlan, secondPlan);
		
	}

	private NativeSQLQuerySpecification createSpec() {
		String blah = "blah";
		String select = "select blah from blah";
		NativeSQLQueryReturn[] queryReturns = 
			new NativeSQLQueryScalarReturn[] {new NativeSQLQueryScalarReturn(blah, new IntegerType())};
		NativeSQLQuerySpecification spec = new NativeSQLQuerySpecification(select,
				queryReturns, null);
		return spec;
	}

}

