package org.hibernate.jpa.test.refresh;

import org.hibernate.annotations.Formula;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.persistence.*;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;
import static org.junit.Assert.assertEquals;

@RunWith( BytecodeEnhancerRunner.class )
public class RefreshByteCodeInstrumentedEntityWithLazyPropertyTest
        extends BaseEntityManagerFunctionalTestCase {

    private Long personId;

    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class<?>[] { Person.class };
    }

    @Before
    public void setUp() {
        doInJPA( this::entityManagerFactory, entityManager -> {
            Person p = new Person("John", "Doe");
            entityManager.persist(p);
            personId = p.getId();
        });
    }

    @Test
    public void testRefreshOfLazyField() {
        doInJPA(this::entityManagerFactory, entityManager -> {
            Person p = entityManager.find(Person.class, personId);
            assertEquals("Doe", p.getLastName());

            entityManager.createQuery(
        "update Person p " +
                "set p.lastName = 'Johnson' " +
                "where p.id = :id"
            )
            .setParameter("id", personId).executeUpdate();

            entityManager.refresh(p);
            assertEquals("stale lazy field found after refresh", "Johnson", p.getLastName());
        });

    }

    @Test
    public void testRefreshOfLazyFormula() {
        doInJPA(this::entityManagerFactory, entityManager -> {
            Person p = entityManager.find(Person.class, personId);
            assertEquals("John Doe", p.getFullName());

            p.setLastName("Johnson");
            entityManager.flush();
            entityManager.refresh(p);
            assertEquals("stale lazy formula found after refresh", "John Johnson", p.getFullName());
        });

    }

    @Entity(name = "Person")
    public static class Person {

        @Id
        @GeneratedValue()
        private Long id;
        public Long getId() {
            return id;
        }

        @Basic
        private String firstName;
        public String getFirstName() {
            return firstName;
        }
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        @Basic(fetch = FetchType.LAZY)
        private String lastName;
        public String getLastName() {
            return lastName;
        }
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        @Basic(fetch = FetchType.LAZY)
        @Formula("firstName || ' ' || lastName")
        private String fullName;
        public String getFullName() { return fullName; }


        protected Person() {}
        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

}
