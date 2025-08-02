package org.hibernate.id;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.DialectChecks;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

@RequiresDialectFeature(DialectChecks.SupportsIdentityColumns.class)
public class CreateDeleteTest extends BaseCoreFunctionalTestCase {
   @Test
   public void test() {
      Session session = openSession();

      Transaction tx = session.beginTransaction();
      session.setHibernateFlushMode(FlushMode.COMMIT);
      RootEntity entity = new RootEntity();
      session.persist(entity);
      session.delete(entity);
      tx.commit();
      session.close();
   }

   @Override
   public Class[] getAnnotatedClasses() {
      return new Class[]{
         RootEntity.class,
         RelatedEntity.class,
      };
   }
}
