package org.hibernate.persister.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.id.PostInsertIdentifierGenerator;
import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.metamodel.binding.EntityBinding;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.sql.Insert;

public class BlobLastSingleTableEntityPersister extends SingleTableEntityPersister {

	private List<Integer> lobProperties;
	private PersistentClass persistentClass;

	public BlobLastSingleTableEntityPersister(PersistentClass persistentClass, EntityRegionAccessStrategy cacheAccessStrategy,
			NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy, SessionFactoryImplementor factory, Mapping mapping)
			throws HibernateException {
		super(persistentClass, cacheAccessStrategy, naturalIdRegionAccessStrategy, factory, mapping);
		lazyPostConstruct(persistentClass, mapping);
	}

	public BlobLastSingleTableEntityPersister(EntityBinding entityBinding, EntityRegionAccessStrategy cacheAccessStrategy,
			NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy, SessionFactoryImplementor factory, Mapping mapping)
			throws HibernateException {
		super(entityBinding, cacheAccessStrategy, naturalIdRegionAccessStrategy, factory, mapping);
	}

	@Override
	protected String generateIdentityInsertString(boolean[] includeProperty) {
		InsertGeneratedIdentifierDelegate identityDelegate = ((PostInsertIdentifierGenerator) getIdentifierGenerator())
				.getInsertGeneratedIdentifierDelegate(this, getFactory().getDialect(), useGetGeneratedKeys());
		Insert insert = identityDelegate.prepareIdentifierGeneratingInsert();
		insert.setTableName(getTableName(0));

		// add normal properties
		for (int i = 0; i < getEntityMetamodel().getPropertySpan(); i++) {
			if (includeProperty[i] && isPropertyOfTable(i, 0) && !getLobProperties().contains(i)) {
				// this property belongs on the table and is to be inserted
				insert.addColumns(getPropertyColumnNames(i), getPropertyColumnInsertable()[i], getPropertyColumnWriters(i));
			}
		}

		// add the discriminator
		addDiscriminatorToInsert(insert);

		// delegate already handles PK columns

		if (getFactory().getSettings().isCommentsEnabled()) {
			insert.setComment("insert " + getEntityName());
		}

		// HHH-4635
		// Oracle expects all Lob properties to be last in inserts
		// and updates. Insert them at the end.
		for (int i : getLobProperties()) {
			if (includeProperty[i] && isPropertyOfTable(i, 0)) {
				// this property belongs on the table and is to be inserted
				insert.addColumns(getPropertyColumnNames(i), getPropertyColumnInsertable()[i], getPropertyColumnWriters(i));
			}
		}

		return insert.toStatementString();
	}

	public List<Integer> getLobProperties() {
		if (lobProperties == null) {
			lobProperties = new ArrayList<Integer>();
			@SuppressWarnings("rawtypes")
			Iterator iter = persistentClass.getPropertyClosureIterator();
			int i = 0;
			while (iter.hasNext()) {
				Property prop = (Property) iter.next();
				if (prop.isLob() && getFactory().getDialect().forceLobAsLastValue()) {
					lobProperties.add(i);
				}
				i++;
			}
		}
		return lobProperties;
	}

	@Override
	protected void postConstruct(Mapping mapping) throws MappingException {

	}

	protected void lazyPostConstruct(PersistentClass persistentClass, Mapping mapping) throws MappingException {
		this.persistentClass = persistentClass;
		super.postConstruct(mapping);
	}

}
