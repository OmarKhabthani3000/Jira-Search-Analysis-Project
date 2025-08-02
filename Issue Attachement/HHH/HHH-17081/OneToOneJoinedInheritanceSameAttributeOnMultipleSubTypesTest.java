/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.inheritance;

import jakarta.persistence.*;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.ServiceRegistry;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DomainModel(
        annotatedClasses = {
                OneToOneJoinedInheritanceSameAttributeOnMultipleSubTypesTest.Parent.class,
                OneToOneJoinedInheritanceSameAttributeOnMultipleSubTypesTest.Child.class,
                OneToOneJoinedInheritanceSameAttributeOnMultipleSubTypesTest.ChildA.class,
                OneToOneJoinedInheritanceSameAttributeOnMultipleSubTypesTest.ChildB.class,
                OneToOneJoinedInheritanceSameAttributeOnMultipleSubTypesTest.Something.class
        }
)
@SessionFactory
@ServiceRegistry
public class OneToOneJoinedInheritanceSameAttributeOnMultipleSubTypesTest {

    @BeforeAll
    void setUp(SessionFactoryScope scope) {
        scope.inTransaction((session) -> {
            Parent parent = new Parent();
            parent.id = 1L;

            session.persist(parent);
        });
    }

    @Test
    public void testFindParentById(SessionFactoryScope scope) {
        scope.inTransaction((session) -> {
            Parent foundParent = session.find(Parent.class, 1L);

            assertThat(foundParent).isNotNull();
        });
    }

    @Test
    public void testQueryParentById(SessionFactoryScope scope) {
        scope.inTransaction((session) -> {
            Parent parent = session.createQuery("select p from Parent p where p.id = :id", Parent.class)
                    .setParameter("id", 1L)
                    .getSingleResult();

            assertThat(parent).isNotNull();
        });
    }

    @Entity(name = "Parent")
    public static class Parent {

        @Id
        Long id;
        
        @OneToOne
        Child child;

        // Getter and setters omitted for brevity

    }

    @Entity(name = "Child")
    @Inheritance(strategy = InheritanceType.JOINED)
    public static class Child {

        @Id
        Long id;

        // Getter and setters omitted for brevity

    }

    @Entity(name = "ChildA")
    public static class ChildA extends Child {

        @OneToOne
        Something something;

        // Getter and setters omitted for brevity

    }

    @Entity(name = "ChildB")
    public static class ChildB extends Child {

        @OneToOne
        Something something;

        // Getter and setters omitted for brevity

    }

    @Entity(name = "Something")
    public static class Something {

        @Id
        Long id;
        
        // Getter and setters omitted for brevity

    }

}
