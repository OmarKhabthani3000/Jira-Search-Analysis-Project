 package org.hibernate.bugs;

 import jakarta.persistence.Column;
 import jakarta.persistence.Embeddable;
 import jakarta.persistence.EmbeddedId;
 import jakarta.persistence.Entity;
 import jakarta.persistence.EntityManager;
 import jakarta.persistence.EntityManagerFactory;
 import jakarta.persistence.Id;
 import jakarta.persistence.JoinColumn;
 import jakarta.persistence.OneToOne;
 import org.h2.jdbcx.JdbcDataSource;
 import org.hibernate.jpa.HibernatePersistenceProvider;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.testing.orm.junit.JiraKey;
 import org.hibernate.testing.util.jpa.PersistenceUnitInfoAdapter;
 import org.junit.jupiter.api.Test;

 import javax.sql.DataSource;
 import java.io.Serializable;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.List;

 import static org.hamcrest.CoreMatchers.notNullValue;
 import static org.hamcrest.MatcherAssert.assertThat;

 @JiraKey("HHH-17420")
 class BinderHelperJoinColumnOutOfOrderTest extends BaseUnitTestCase {

     @Test
     void testLeftToRight() {
         final Class<?>[] classes = {
                 LeftEntity.class,
                 AnotherLeftEntity.class,
                 MidEntity.class,
                 RightEntity.class};

         try (EntityManagerFactory emf = entityManagerFactory(dataSource(), classes)) {
             EntityManager em = emf.createEntityManager();
             assertThat(em, notNullValue());
         }
     }

     @Test
     void testRightToLeft() {
         final Class<?>[] classes = {
                 RightEntity.class,
                 MidEntity.class,
                 AnotherLeftEntity.class,
                 LeftEntity.class};

         try (EntityManagerFactory emf = entityManagerFactory(dataSource(), classes)) {
             EntityManager em = emf.createEntityManager();
             assertThat(em, notNullValue());
         }
     }

     // ----- DataSource ------------------------------------------------------------

     DataSource dataSource() {
         JdbcDataSource dataSource = new JdbcDataSource();
         dataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
         dataSource.setUser("sa");
         dataSource.setPassword("sa");
         return dataSource;
     }

     // ----- EntityManagerFactory --------------------------------------------------

     EntityManagerFactory entityManagerFactory(final DataSource dataSource, final Class<?>[] classes) {
         return new HibernatePersistenceProvider().createContainerEntityManagerFactory(
                 new PersistenceUnitInfoAdapter() {
                     @Override
                     public DataSource getNonJtaDataSource() {
                         return dataSource;
                     }

                     @Override
                     public List<String> getManagedClassNames() {
                         return Arrays.stream(classes)
                                 .map(Class::getName)
                                 .toList();
                     }
                 },
                 Collections.emptyMap());
     }

     // ----- Entities --------------------------------------------------------------

     @Entity
     static class LeftEntity implements Serializable {

         @Embeddable
         static class Pk implements Serializable {
             @Column
             String id_one;

             @Column
             String id_two;

             @Column
             String id_three;
         }

         @EmbeddedId
         Pk id;

         @OneToOne(mappedBy = "leftEntity")
         MidEntity midEntity;
     }

     @Entity
     static class AnotherLeftEntity implements Serializable {

         @Embeddable
         static class Pk implements Serializable {
             @Column
             String id_one;

             @Column
             String id_two;

             @Column
             String id_three;
         }

         @EmbeddedId
         LeftEntity.Pk id;

         @OneToOne(mappedBy = "anotherLeftEntity")
         MidEntity midEntity;
     }

     @Entity
     static class MidEntity implements Serializable {

         @Id
         @Column
         String id;

         @Column
         String id_one;

         @Column
         String id_two;

         @Column
         String id_three;

         @OneToOne
         @JoinColumn(name = "id_one", referencedColumnName = "id_one", insertable = false, updatable = false)
         @JoinColumn(name = "id_two", referencedColumnName = "id_two", insertable = false, updatable = false)
         @JoinColumn(name = "id_three", referencedColumnName = "id_three", insertable = false, updatable = false)
         LeftEntity leftEntity;

         @OneToOne
         @JoinColumn(name = "id_one", referencedColumnName = "id_one", insertable = false, updatable = false)
         @JoinColumn(name = "id_two", referencedColumnName = "id_two", insertable = false, updatable = false)
         @JoinColumn(name = "id_three", referencedColumnName = "id_three", insertable = false, updatable = false)
         AnotherLeftEntity anotherLeftEntity;

         @OneToOne(mappedBy = "midEntity")
         RightEntity rightEntity;
     }

     @Entity
     static class RightEntity implements Serializable {

         @Embeddable
         class Pk implements Serializable {

             @Column
             String id_one;

             @Column
             String id_two;

             @Column
             String id_other;
         }

         @EmbeddedId
         Pk id;

         @Column
         String id_three;

         @OneToOne
         @JoinColumn(name = "id_one", referencedColumnName = "id_one", insertable = false, updatable = false)
         @JoinColumn(name = "id_two", referencedColumnName = "id_two", insertable = false, updatable = false)
         MidEntity midEntity;
     }
 }