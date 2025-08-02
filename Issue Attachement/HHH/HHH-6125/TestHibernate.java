package testhibernate;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.QueryException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.NaturalId;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.classic.Session;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Iterator;
import java.util.Map;

public class TestHibernate {

	public static void main(String args[]) {

		TestHibernate test = new TestHibernate();
		test.testLockMode( args[0], args[1], args[2], MySqlInnoDBDialectPatch.class.getName());
		test.testLockMode( args[0], args[1], args[2], MySQL5InnoDBDialect.class.getName());
	}


	/**
	 *
	 */
	public void testLockMode( String user, String password, String url, String dialect ) {
		
		Configuration conf = new Configuration();
		conf.setProperty( Environment.USER, user );
		conf.setProperty( Environment.PASS, password );
		conf.setProperty( Environment.URL, url );
		conf.setProperty( Environment.DIALECT, dialect );
		conf.setProperty( Environment.DRIVER, com.mysql.jdbc.Driver.class.getName() );
		conf.setProperty( Environment.HBM2DDL_AUTO, Boolean.FALSE.toString() );
		conf.setProperty( Environment.SHOW_SQL, Boolean.TRUE.toString() );
		conf.setProperty( Environment.FORMAT_SQL, Boolean.TRUE.toString() );
		conf.setProperty( Environment.AUTOCOMMIT, Boolean.FALSE.toString() );

		conf.addAnnotatedClass( SampleEntity.class );

		SchemaExport schemaExport = new SchemaExport( conf );
		schemaExport.drop( true, true );
		schemaExport.create( true, true );

		SessionFactory sessionFactory = null;
		Session session = null;
		try {
			sessionFactory = conf.buildSessionFactory();

			long id = createSampleEntity( sessionFactory );


			lockEntity1( sessionFactory, id );
			lockEntity2( sessionFactory );


		} finally {
			if ( session != null ) {
				session.close();
			}
			if ( session != null ) {
				sessionFactory.close();
			}
		}
	}


	// Create a sample entity
	public long createSampleEntity(SessionFactory fact ) {
		Session s = fact.openSession();
		Transaction trx = s.beginTransaction();
		try {
			SampleEntity sample = new SampleEntity();

			sample.setCode("ABC");
			sample.setDescription("Description");

			s.save( sample );


			trx.commit();

			return sample.getId();
		} finally {
			if (trx.isActive()) trx.rollback();
		}
	}

	// Check locks that works
	public void lockEntity1(SessionFactory fact, long id ) {
		Session s = fact.openSession();
		Transaction trx = s.beginTransaction();
		try {
			
			// This Works
			SampleEntity e2 = (SampleEntity) s.load( SampleEntity.class, id, LockOptions.UPGRADE );

			// This Works
			s.refresh( e2, LockOptions.UPGRADE );




			trx.commit();
		} finally {
			if (trx.isActive()) trx.rollback();
		}
	}

	// Check locks that doesn't works
	public void lockEntity2(SessionFactory fact ) {
		Session s = fact.openSession();
		Transaction trx = s.beginTransaction();
		try {

			// This fails
			Criteria c = s.createCriteria(SampleEntity.class);
			c.setLockMode( LockMode.PESSIMISTIC_WRITE );
			SampleEntity e = (SampleEntity) c.uniqueResult();

			trx.commit();
		} finally {
			if (trx.isActive()) trx.rollback();
		}
	}

	/**
	 *
	 */
	@Entity
	public static class SampleEntity {

		protected Long id;

		protected String code;

		protected String description;

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		public Long getId() {
			return id;
		}

		public void setId( Long id ) {
			this.id = id;
		}

		@NaturalId
		public String getCode() {
			return code;
		}

		public void setCode( String code ) {
			this.code = code;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription( String description ) {
			this.description = description;
		}
	}

	/**
	 *
	 */
	public static class MySqlInnoDBDialectPatch extends MySQL5InnoDBDialect {


		public String getForUpdateString(LockOptions lockOptions) {

			LockMode baseMode = lockOptions.getLockMode();

			LockMode aliasMode = null;
			if ( lockOptions.getAliasLockCount() > 0 ) {
				Iterator i = lockOptions.getAliasLockIterator();
				while (i.hasNext()) {
					Map.Entry modeEntry = (Map.Entry)i.next();
					LockMode mode = (LockMode)modeEntry.getValue();

					if (aliasMode == null) {
						aliasMode = mode;
					} else if (!mode.equals( aliasMode )) {
						throw new QueryException("Conflicting lock mode in query.");
					}
				 }
			}

			if ( aliasMode != null ) {
				if (baseMode == null) {
					baseMode = aliasMode;
				} else {
					if (baseMode.greaterThan( LockMode.NONE )) {
						if (!aliasMode.equals( baseMode )) {
							throw new QueryException("Conflicting lock mode in query.");
						}
					} else {
						baseMode = aliasMode;
					}
				}
			}


			return super.getForUpdateString( new LockOptions( baseMode ) );
		}
	}
}
