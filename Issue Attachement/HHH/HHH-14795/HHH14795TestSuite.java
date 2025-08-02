package org.hibernate.orm.test;

import junit.framework.TestSuite;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import javax.persistence.*;
import java.util.function.Supplier;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        HHH14795TestSuite.OneToOneDeclaredAsClassWithMapsIdTest.class,
        HHH14795TestSuite.OneToOneDeclaredAsInterfaceWithoutMapsIdTest.class,
        HHH14795TestSuite.OneToOneDeclaredAsInterfaceWithMapsIdTest.class,
})
public class HHH14795TestSuite extends TestSuite {

    public interface IParent {
    }

    @Entity
    public static class Parent implements IParent {

        @Id
        @GeneratedValue
        Long id;
    }

    public interface IChild {

        void setParent(Parent parent);
    }

    public static class OneToOneDeclaredAsClassWithMapsIdTest extends AbstractTest {

        @Entity
        public static class Child implements IChild {

            @Id
            Long id;

            @OneToOne
            @MapsId
            Parent parent; // declared as class

            @Override
            public void setParent(Parent parent) {
                this.parent = parent;
            }
        }

        public OneToOneDeclaredAsClassWithMapsIdTest() {
            super(Child.class, Child::new);
        }
    }

    public static class OneToOneDeclaredAsInterfaceWithoutMapsIdTest extends AbstractTest {

        @Entity
        public static class Child implements IChild {

            @Id
            @GeneratedValue
            Long id;

            @OneToOne(targetEntity = Parent.class)
            IParent parent; // declared as interface

            @Override
            public void setParent(Parent parent) {
                this.parent = parent;
            }
        }

        public OneToOneDeclaredAsInterfaceWithoutMapsIdTest() {
            super(Child.class, Child::new);
        }
    }

    public static class OneToOneDeclaredAsInterfaceWithMapsIdTest extends AbstractTest {

        @Entity
        public static class Child implements IChild {

            @Id
            Long id;

            @OneToOne(targetEntity = Parent.class)
            @MapsId
            IParent parent; // declared as interface

            @Override
            public void setParent(Parent parent) {
                this.parent = parent;
            }
        }

        public OneToOneDeclaredAsInterfaceWithMapsIdTest() {
            super(Child.class, Child::new);
        }
    }

    public static abstract class AbstractTest extends BaseNonConfigCoreFunctionalTestCase {

        private final Class<? extends IChild> entityClass;
        private final Supplier<? extends IChild> constructor;

        public AbstractTest(Class<? extends IChild> entityClass, Supplier<? extends IChild> constructor) {
            this.entityClass = entityClass;
            this.constructor = constructor;
        }

        @Test
        public void test() {
            inTransaction(session -> {
                Parent parent = new Parent();
                session.persist(parent);
                IChild child = constructor.get();
                child.setParent(parent);
                session.persist(child);
            });
        }

        @Override
        protected Class<?>[] getAnnotatedClasses() {
            return new Class<?>[]{Parent.class, entityClass};
        }
    }
}
