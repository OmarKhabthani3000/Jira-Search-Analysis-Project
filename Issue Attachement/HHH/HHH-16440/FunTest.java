package org.hibernate.orm.test.query;

import jakarta.persistence.*;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;


public class FunTest extends BaseNonConfigCoreFunctionalTestCase {


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
        private Collection<Leg> legs;

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

        @ManyToOne()
        private Person person;
    }
}
