import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.EntityMode;
import org.hibernate.EntityNameResolver;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.w3c.dom.Document;

public class HHH4070 {
	public static void main(String[] args) {
		Parent object = new Parent();
		
		Configuration configuration = new Configuration();
		setupDatabase(configuration);
		configuration.addDocument(createXML());
		SessionFactory sessionFactory = configuration.buildSessionFactory();
		
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		session.save(object);
		transaction.commit();
		session.close();
		
		session = sessionFactory.openSession();
		Query query = session.createQuery("from " + Parent.class.getName());
		List list = query.list();
		System.out.println(list);
		session.close();
	}
	
	private static void setupDatabase(Configuration configuration) {
		configuration.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
		configuration.setProperty("hibernate.connection.url", "jdbc:hsqldb:file:testdb");
		configuration.setProperty("hibernate.connection.username", "sa");
		configuration.setProperty("hibernate.connection.password", "");
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
	}
	
	private static Document createXML() {
		InputStream stream = HHH4070.class.getClassLoader().getResourceAsStream("HHH4070.mapping");
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static class Parent {
		public Child child = new Child();
	}
	
	public static class Child {
		public String name = "Hello";		
	}
	
	private static class BugReportTuplizer implements EntityTuplizer {
		private EntityMetamodel entityMetamodel;
		private PersistentClass mappingInfo;

		public BugReportTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappingInfo) {
			this.entityMetamodel = entityMetamodel;
			this.mappingInfo = mappingInfo;
		}

		public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
		}

		public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
			throw new UnsupportedOperationException("Does not support proxies");
		}

		public Class getConcreteProxyClass() {
			throw new UnsupportedOperationException("Does not support proxies");
		}

		public Serializable getIdentifier(Object entity) throws HibernateException {
			return 1;
		}

		public Object getPropertyValue(Object entity, String propertyName) throws HibernateException {
			try {
				Field field = entity.getClass().getField(propertyName);
				return field.get(entity);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session)
				throws HibernateException {
			return getPropertyValues(entity);
		}

		public Object getVersion(Object entity) throws HibernateException {
			if (!entityMetamodel.isVersioned())
				return null;
			throw new UnsupportedOperationException("Does not support versioning");
		}

		public boolean hasProxy() {
			return false;
		}

		public boolean hasUninitializedLazyProperties(Object entity) {
			return false;
		}

		public Object instantiate(Serializable id) throws HibernateException {
			return instantiate();
		}

		public boolean isInstrumented() {
			return false;
		}

		public boolean isLifecycleImplementor() {
			return false;
		}

		public boolean isValidatableImplementor() {
			return false;
		}

		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion) {
		}

		public void setIdentifier(Object entity, Serializable id) throws HibernateException {
		}

		public void setPropertyValue(Object entity, int i, Object value) throws HibernateException {
			setPropertyValue(entity, entityMetamodel.getPropertyNames()[i], value);
		}

		public void setPropertyValue(Object entity, String propertyName, Object value) throws HibernateException {
		}

		public Class getMappedClass() {
			return mappingInfo.getMappedClass();
		}

		public Object getPropertyValue(Object entity, int i) throws HibernateException {
			return getPropertyValue(entity, entityMetamodel.getPropertyNames()[i]);
		}

		public Object[] getPropertyValues(Object entity) throws HibernateException {
			Object[] objects = new Object[entityMetamodel.getPropertySpan()];
			for (int x = 0; x < objects.length; x++) {
				objects[x] = getPropertyValue(entity, x);
			}

			return objects;
		}

		public Object instantiate() throws HibernateException {
			try {
				return getClass().getClassLoader().loadClass(mappingInfo.getClassName()).newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public boolean isInstance(Object object) {
			return mappingInfo.getEntityName().equals(object.getClass().getName());
		}

		public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
			for (int x = 0; x < entityMetamodel.getPropertySpan(); x++) {
				setPropertyValue(entity, x, values[x]);
			}
		}

		public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
			return entityInstance.getClass().getName();
		}

		public EntityMode getEntityMode() {
			return EntityMode.MAP;
		}

		public EntityNameResolver[] getEntityNameResolvers() {
			return new EntityNameResolver[] { new EntityNameResolver(){
				public String resolveEntityName(Object entity) {
					return entity.getClass().getName();
				}
			} };
		}
	}
}
