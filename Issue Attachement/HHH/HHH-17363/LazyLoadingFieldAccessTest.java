package org.hibernate.orm.test.lazyload;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import org.hibernate.internal.util.MutableLong;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DomainModel(annotatedClasses = {
        LazyLoadingFieldAccessTest.Parent.class,
        LazyLoadingFieldAccessTest.Child.class
})
@SessionFactory
class LazyLoadingFieldAccessTest {
    private long parentId;

    @BeforeEach
    public void prepareTest(SessionFactoryScope scope) {
        scope.inTransaction(session -> {
            Parent p1 = new Parent();
            Parent p2 = new Parent();
            Child c1 = new Child();
            c1.setName("NOT NULL");
            p1.setPreferredChild(c1);
            p2.addPreferringChild(c1);
            session.persist(p1);
            session.persist(p2);
            parentId = p1.getId();
        });
    }

    @Test
    void testDirectFieldAccess(SessionFactoryScope scope) {
        scope.inTransaction(session -> {
            // Using TreeSet instead of e.g. List.sort allows comparator to be called
            Parent p1 = session.load(Parent.class, parentId);
            Set<Child> children1 = new TreeSet<>(Child.NAME_ORDER_USING_DIRECT_ACCESS);
            assertDoesNotThrow(() -> children1.addAll(p1.getPreferredChild().orElseThrow().getPreferredParent().orElseThrow().getPreferringChildren()));
        });
    }
    @Test
    void testGetterAccess(SessionFactoryScope scope) {
        scope.inTransaction(session -> {
            Parent p1 = session.load(Parent.class, parentId);
            Set<Child> children2 = new TreeSet<>(Child.NAME_ORDER_USING_GETTER);
            assertDoesNotThrow(() ->children2.addAll(p1.getPreferredChild().orElseThrow().getPreferredParent().orElseThrow().getPreferringChildren()));
        });
    }

    @Entity
    public static class Parent {
        @Id
        @GeneratedValue
        private long id;

        @OneToOne(fetch = FetchType.LAZY)
        private Child preferredChild;

        @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "preferredParent")
        private Set<Child> preferringChildren = new HashSet<>();

        public long getId() {
            return id;
        }

        public Optional<Child> getPreferredChild() {
            return Optional.ofNullable(preferredChild);
        }

        public void setPreferredChild(Child preferredChild) {
            this.preferredChild = preferredChild;
            preferredChild.setPreferredParent(this);
        }

        public Set<Child> getPreferringChildren() {
            return preferringChildren;
        }

        public void addPreferringChild(Child preferringChild) {
            preferringChildren.add(preferringChild);
            preferringChild.setPreferredParent(this);
        }
    }

    @Entity
    public static class Child {
        public static Comparator<Child> NAME_ORDER_USING_DIRECT_ACCESS = ((o1, o2) -> o1.name.compareToIgnoreCase(o2.name));
        public static Comparator<Child> NAME_ORDER_USING_GETTER = ((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        @Id
        @GeneratedValue
        private long id;
        @NotNull
        @Column
        private String name;

        @ManyToOne(fetch = FetchType.LAZY)
        private Parent preferredParent;
        @OneToOne(fetch = FetchType.LAZY, mappedBy = "preferredChild")
        private Parent preferringParent;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setPreferredParent(Parent preferredParent) {
            this.preferredParent = preferredParent;
        }

        public Optional<Parent> getPreferredParent() {
            return Optional.ofNullable(preferredParent);
        }

        protected void setPreferringParent(Parent preferringParent) {
            this.preferringParent = preferringParent;
        }
    }
}
