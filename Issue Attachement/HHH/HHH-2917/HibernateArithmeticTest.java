import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Test;

/**
 * 
 * 
 * @author Sven Rienstra
 */

public class HibernateArithmeticTest
{

	@Entity
	class Example
	{
		@Id
		public int id;

		public int intValue;
	}

	public static class HSQLSequenceDialect extends HSQLDialect
	{
		/**
		 * The class (which implements {@link org.hibernate.id.IdentifierGenerator}) which acts as this dialects native
		 * generation strategy.
		 * <p/>
		 * Comes into play whenever the user specifies the native generator.
		 * 
		 * @return The native generator class.
		 */
		@Override
		public Class<?> getNativeIdentifierGeneratorClass()
		{
			return SequenceGenerator.class;
		}
	}

	@Test
	public void testHibernateArithmetic() throws Exception
	{
		AnnotationConfiguration conf = new AnnotationConfiguration();
		conf.addAnnotatedClass(Example.class);
		conf.setProperty(Environment.DRIVER, "org.hsqldb.jdbcDriver");
		conf.setProperty(Environment.URL, "jdbc:hsqldb:mem:hibernateDaoTest");
		conf.setProperty(Environment.USER, "sa");
		conf.setProperty(Environment.DIALECT, HSQLSequenceDialect.class.getName());
		conf.setProperty(Environment.HBM2DDL_AUTO, "create-drop");
		conf.setProperty(Environment.SHOW_SQL, "true");
		conf.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "false");
		conf.setProperty(Environment.MAX_FETCH_DEPTH, "2");
		SessionFactory fact = conf.buildSessionFactory();

		SchemaExport exporter = new SchemaExport(conf, ((SessionFactoryImpl) fact).getSettings());
		exporter.setHaltOnError(true);
		exporter.create(true, true);

		for (Object e : exporter.getExceptions())
		{
			throw (Exception) e;
		}

		Session session = fact.openSession();

		StringBuilder hql = new StringBuilder();
		hql.append("select example ");
		hql.append(" from ");
		hql.append(Example.class.getName());
		hql.append(" AS example ");
		hql.append(" where ");

		hql.append(" (( select count(example.id) from ");
		hql.append(Example.class.getName());
		hql.append(" AS example )");
		hql.append(" + ( select count(example.id) from ");
		hql.append(Example.class.getName());
		hql.append(" AS example )) > 1 ");

		session.createQuery(hql.toString()).list();
	}
}
