package org.hibernate.orm.test.annotations.naturalid;

import jakarta.persistence.*;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Objects;

@DomainModel(
        annotatedClasses = {
                EqualsNaturalID3Test.State.class,
                EqualsNaturalID3Test.Citizen.class,
                EqualsNaturalID3Test.NaturalIdOnManyToOne.class,
        }
)
@SessionFactory
public class EqualsNaturalID3Test {

    @Test
    void name(SessionFactoryScope scope) {
        scope.inTransaction(s -> {
            create(s, 1, "Aa");
            create(s, 2, "BB");
            create(s, 3, "CC");
        });
        scope.inSession(s -> {
            final var object = s.createQuery("select c from NaturalIdOnManyToOne c where c.id=1", NaturalIdOnManyToOne.class).uniqueResult();
            Assertions.assertFalse(Hibernate.isInitialized(object.getCitizen()));
            Assertions.assertEquals(1, object.getCitizen().getId());
            final var citizen = s.createQuery("select  c from Citizen c  where c.id=1 ", Citizen.class).uniqueResult();
            Assertions.assertEquals(object.getCitizen(), citizen);
        });
    }

    @Test
    void hashColision(SessionFactoryScope scope) {
        scope.inTransaction(s -> {
            final var object = new State();
            object.setName("Aa");
            object.setId(1);
            s.persist(object);
            final var object1 = new State();
            object1.setName("Bb");
            object1.setId(2);
            s.persist(object1);
            final var map = new HashMap<Integer, NaturalIdOnManyToOne>();
            final var map2 = new HashMap<Integer, Citizen>();
            Citizen citizen = null;
            Citizen citizen1 = null;
            while (true) {
                citizen = new Citizen();
                citizen1 = map2.put(System.identityHashCode(citizen), citizen);
                if (citizen1 != null) {
                    citizen.setSsn("");
                    citizen.setState(object);
                    s.persist(citizen);
                    citizen1.setSsn("");
                    citizen1.setState(object1);
                    s.persist(citizen1);
                    break;
                }
            }
            while (true) {
                final var x = new NaturalIdOnManyToOne();
                final var put = map.put(System.identityHashCode(x), x);
                if (put != null) {
                    put.setCitizen(citizen);
                    x.setCitizen(citizen1);
                    s.persist(put);
                    s.persist(x);
                    break;
                }
            }
            MatcherAssert.assertThat(s.createQuery("select c from NaturalIdOnManyToOne c ", NaturalIdOnManyToOne.class).list(), Matchers.hasSize(2));
        });
        scope.inSession(s -> {
            final var object = s.createQuery("select c from NaturalIdOnManyToOne c ", NaturalIdOnManyToOne.class).list();
            MatcherAssert.assertThat(object, Matchers.hasSize(2));
        });
    }

    private static void create(SessionImplementor s, final int id, final String name) {
        final var object = new State();
        object.setName(name);
        object.setId(id);
        s.persist(object);
        final var citizen = new Citizen();
        citizen.setSsn("");
        citizen.setState(object);
        s.persist(citizen);
        final var naturalIdOnManyToOne = new NaturalIdOnManyToOne();
        naturalIdOnManyToOne.setCitizen(citizen);
        s.persist(naturalIdOnManyToOne);
    }

    @Entity(name = "State")
    public static class State {
        @Id
        private Integer id;
        @NaturalId
        private String name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof State state) {

                return Objects.equals(name, state.name);
            }
            return false;
        }

        @Override
        public int hashCode() {
            // use an unoptimized hashCode to make this break realiable
            return 1;
        }
    }

    @Entity(name = "Citizen")
    @NaturalIdCache
    public static class Citizen {
        @Id
        @GeneratedValue
        private Integer id;
        @NaturalId
        @ManyToOne(fetch = FetchType.LAZY)
        private State state;
        @NaturalId
        private String ssn;


        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }


        public State getState() {
            return state;
        }

        public void setState(State state) {
            this.state = state;
        }

        public String getSsn() {
            return ssn;
        }

        public void setSsn(String ssn) {
            this.ssn = ssn;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Citizen citizen)) {
                return false;
            }
            return Objects.equals(state, citizen.state) && Objects.equals(ssn, citizen.ssn);
        }

        @Override
        public int hashCode() {
            // use an unoptimized hashCode to make this break realiable
            return 1;
        }
    }

    @Entity(name = "NaturalIdOnManyToOne")
    @NaturalIdCache
    public static class NaturalIdOnManyToOne {

        @Id
        @GeneratedValue
        int id;

        @NaturalId
        @ManyToOne(fetch = FetchType.LAZY)
        Citizen citizen;

        @NaturalId
        String dummy = "dummy";

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Citizen getCitizen() {
            return citizen;
        }

        public void setCitizen(Citizen citizen) {
            this.citizen = citizen;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof NaturalIdOnManyToOne that)) {
                return false;
            }
            return Objects.equals(citizen, that.citizen) && Objects.equals(dummy, that.dummy);
        }
    }

    @Override
    public int hashCode() {
        return 1;
    }
}

