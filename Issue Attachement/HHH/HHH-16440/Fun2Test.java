package org.hibernate.orm.test.query;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;

import java.util.Collection;
import java.util.List;


public class Fun2Test extends BaseNonConfigCoreFunctionalTestCase {


    @Override
    protected Class[] getAnnotatedClasses() {
        return List.of(Person.class, Leg.class).toArray(Class[]::new);
    }


    @Test
    public void leak_One() {
        for (int i = 0; i < 10_0000; i++) {
            rebuildSessionFactory();
        }
    }

    @Entity()
    public static class Person {
        @Id
        private Long id;

        @OneToMany(mappedBy = "person")
        public Collection<Leg> legs;

        public void setId(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }
    }

    @Entity
    public static class Leg {
        @Id
        private Long id;

        public void setId(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }

        @ManyToOne
        public Person person;
    }
}
