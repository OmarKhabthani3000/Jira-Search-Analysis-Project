package com.example.demo;

import jakarta.persistence.*;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.JiraKey;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@SessionFactory
@DomainModel(annotatedClasses = {
        JoinColumnOccoursOutOfOrderTest.MasterEntity.class,
        JoinColumnOccoursOutOfOrderTest.ChildTwoEntity.class,
        JoinColumnOccoursOutOfOrderTest.ChildEntityOne.class,
        JoinColumnOccoursOutOfOrderTest.ChildEntityFour.class,
        JoinColumnOccoursOutOfOrderTest.ChildEntityThree.class,
})
@JiraKey("HHH-16263")
public class JoinColumnOccoursOutOfOrderTest {

    @BeforeAll
    public void setUp(SessionFactoryScope scope) {
        scope.inTransaction( session -> {
            var meta = new MasterEntity();
            meta.setId(1l);
            session.persist( meta );
        } );
    }

    @AfterAll
    public void tearDown(SessionFactoryScope scope) {
        scope.inTransaction( session -> {
        } );
    }

    @Test
    public void testOutOffOrder(SessionFactoryScope scope) {
        scope.inTransaction( session -> {
            final var container = session.createQuery(
                    "from MasterEntity container where container.id = :param ",
                    MasterEntity.class
            ).setParameter("param", 1).getSingleResult();
        } );
    }

    @MappedSuperclass
    @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
    public class AbstractChildEntityOne {

        @EmbeddedId
        private ChildEntityOneId id;

        public ChildEntityOneId getId() {
            return id;
        }

        public void setId(ChildEntityOneId id) {
            this.id = id;
        }
    }

    @MappedSuperclass
    @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
    public class AbstractChildEntityThree {

        private static final long serialVersionUID = 1L;

        @EmbeddedId
        private ChildEntityThreeId id;

        public ChildEntityThreeId getId() {
            return id;
        }

        public void setId(ChildEntityThreeId id) {
            this.id = id;
        }
    }

    @MappedSuperclass
    @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
    public abstract class AbstractChildTwoEntity {

        @EmbeddedId
        private Child2EntityId id;

        public Child2EntityId getId() {
            return id;
        }

        public void setId(Child2EntityId id) {
            this.id = id;
        }
    }

    @MappedSuperclass
    @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
    public class AbstractChildEntityFour {

        @Serial
        private static final long serialVersionUID = 1680834466L;

        @EmbeddedId
        private VehicleDealerId id;

        public VehicleDealerId getId() {
            return id;
        }

        public void setId(VehicleDealerId id) {
            this.id = id;
        }
    }

    @MappedSuperclass
    @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
    public abstract class AbstractMasterEntity {

        //NWTFZG_ID_SEQ0
        @Id
        @Column(name = "ID", unique = true, nullable = false)
        private Long id;

        @Column(name = "COLUMN_1", nullable = false, length = 7)
        private String column1;

        @Column(name = "COLUMN_2", nullable = false, length = 4)
        private String column2;

        @Column(name = "COLUMN_3", nullable = false, length = 2)
        private String column3;

        public abstract AbstractChildEntityOne getChildEntityOne();

        public abstract AbstractChildTwoEntity getChild2Entity();

        public abstract List<? extends AbstractChildEntityThree> getChildEntityThree();

        public abstract List<? extends AbstractChildEntityFour> getChildEntityFour();

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getColumn1() {
            return column1;
        }

        public void setColumn1(String column1) {
            this.column1 = column1;
        }

        public String getColumn2() {
            return column2;
        }

        public void setColumn2(String column2) {
            this.column2 = column2;
        }

        public String getColumn3() {
            return column3;
        }

        public void setColumn3(String column3) {
            this.column3 = column3;
        }
    }

    @Embeddable
    public class Child2EntityId implements Serializable {

        private static final long serialVersionUID = 1L;

        @Column(name = "COLUMN_3", length = 2, nullable = false)
        private String column3;
        @Column(name = "COLUMN_2", length = 4, nullable = false)
        private String column2;
        @Column(name = "COLUMN_1", length = 7, nullable = false)
        private String column1;

        public String getColumn3() {
            return column3;
        }

        public void setColumn3(String column3) {
            this.column3 = column3;
        }

        public String getColumn2() {
            return column2;
        }

        public void setColumn2(String column2) {
            this.column2 = column2;
        }

        public String getColumn1() {
            return column1;
        }

        public void setColumn1(String column1) {
            this.column1 = column1;
        }
    }

    @DynamicUpdate
    @Entity
    public class ChildEntityFour extends AbstractChildEntityFour implements Serializable {

        private static final long serialVersionUID = 1L;
    }

    @Entity
    public class ChildEntityOne extends AbstractChildEntityOne implements Serializable {

        private static final long serialVersionUID = 1L;
    }

    @Entity
    @DynamicUpdate
    public class ChildTwoEntity extends AbstractChildTwoEntity implements Serializable {

        private static final long serialVersionUID = 1L;
    }

    @Entity
    public class ChildEntityThree extends AbstractChildEntityThree {

        private static final long serialVersionUID = 1L;
    }

    @Embeddable
    public class ChildEntityOneId implements Serializable {

        private static final long serialVersionUID = 1L;

        @Column(name = "COLUMN_3", nullable = false, length = 2)
        private String column3;

        @Column(name = "COLUMN_2", nullable = false, length = 4)
        private String column2;

        @Column(name = "COLUMN_1", nullable = false, length = 7)
        private String column1;

        public String getColumn3() {
            return column3;
        }

        public void setColumn3(String column3) {
            this.column3 = column3;
        }

        public String getColumn2() {
            return column2;
        }

        public void setColumn2(String column2) {
            this.column2 = column2;
        }

        public String getColumn1() {
            return column1;
        }

        public void setColumn1(String column1) {
            this.column1 = column1;
        }
    }

    @Embeddable
    public class ChildEntityThreeId implements Serializable {

        private static final long serialVersionUID = 1L;

        @Column(name = "COLUMN_1", nullable = false, length = 7)
        private String column1;

        @Column(name = "COLUMN_2", nullable = false, length = 4)
        private String column2;

        @Column(name = "COLUMN_3", nullable = false, length = 2)
        private String column3;

        @Column(name = "COLUMN_4", nullable = false)
        private LocalDateTime creation;

        public String getColumn1() {
            return column1;
        }

        public void setColumn1(String column1) {
            this.column1 = column1;
        }

        public String getColumn2() {
            return column2;
        }

        public void setColumn2(String column2) {
            this.column2 = column2;
        }

        public String getColumn3() {
            return column3;
        }

        public void setColumn3(String column3) {
            this.column3 = column3;
        }

        public LocalDateTime getCreation() {
            return creation;
        }

        public void setCreation(LocalDateTime creation) {
            this.creation = creation;
        }
    }

    @Entity(name = "MasterEntity")
    @DynamicUpdate
    public class MasterEntity extends AbstractMasterEntity implements Serializable {

        private static final long serialVersionUID = 1L;

        @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
        @JoinColumn(name = "COLUMN_3", referencedColumnName = "COLUMN_3", insertable = false, updatable = false)
        @JoinColumn(name = "COLUMN_1", referencedColumnName = "COLUMN_1", insertable = false, updatable = false)
        @JoinColumn(name = "COLUMN_2", referencedColumnName = "COLUMN_2", insertable = false, updatable = false)
        @NotFound(action = NotFoundAction.IGNORE)
        @ToString.Exclude
        private ChildEntityOne childEntityOne;

        @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
        @JoinColumns(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), value = {
                @JoinColumn(name = "COLUMN_3", referencedColumnName = "COLUMN_3", insertable = false, updatable = false),
                @JoinColumn(name = "COLUMN_1", referencedColumnName = "COLUMN_1", insertable = false, updatable = false),
                @JoinColumn(name = "COLUMN_2", referencedColumnName = "COLUMN_2", insertable = false, updatable = false)
        })
        @NotFound(action = NotFoundAction.IGNORE)
        @ToString.Exclude
        private ChildTwoEntity child2Entity;

        @OneToMany(fetch = FetchType.LAZY)
        @JoinColumn(name = "COLUMN_3", referencedColumnName = "COLUMN_3", insertable = false, updatable = false)
        @JoinColumn(name = "COLUMN_1", referencedColumnName = "COLUMN_1", insertable = false, updatable = false)
        @JoinColumn(name = "COLUMN_2", referencedColumnName = "COLUMN_2", insertable = false, updatable = false)
        @ToString.Exclude
        private List<ChildEntityThree> childEntityThree;

        @OneToMany(fetch = FetchType.LAZY)
        @JoinColumn(name = "COLUMN_3", referencedColumnName = "COLUMN_3", insertable = false, updatable = false)
        @JoinColumn(name = "COLUMN_1", referencedColumnName = "COLUMN_1", insertable = false, updatable = false)
        @JoinColumn(name = "COLUMN_2", referencedColumnName = "COLUMN_2", insertable = false, updatable = false)
        @ToString.Exclude
        private List<ChildEntityFour> childEntityFour;

        @Override
        public ChildEntityOne getChildEntityOne() {
            return childEntityOne;
        }

        public void setChildEntityOne(ChildEntityOne childEntityOne) {
            this.childEntityOne = childEntityOne;
        }

        @Override
        public ChildTwoEntity getChild2Entity() {
            return child2Entity;
        }

        public void setChild2Entity(ChildTwoEntity child2Entity) {
            this.child2Entity = child2Entity;
        }

        @Override
        public List<ChildEntityThree> getChildEntityThree() {
            return childEntityThree;
        }

        public void setChildEntityThree(List<ChildEntityThree> childEntityThree) {
            this.childEntityThree = childEntityThree;
        }

        @Override
        public List<ChildEntityFour> getChildEntityFour() {
            return childEntityFour;
        }

        public void setChildEntityFour(List<ChildEntityFour> childEntityFour) {
            this.childEntityFour = childEntityFour;
        }
    }

    @Embeddable
    public class VehicleDealerId implements Serializable {

        private static final long serialVersionUID = 1L;

        @Column(name = "COLUMN_1", length = 7, nullable = false)
        private String column1;

        @Column(name = "COLUMN_2", length = 4, nullable = false)
        private String colum2;

        @Column(name = "COLUMN_3", length = 2, nullable = false)
        private String column3;

        @Column(name = "NON_UNIQUE_ID", nullable = false)
        private Long nonUNiqueId;

        @Column(name = "COLUMN_4", nullable = false)
        private LocalDateTime creation;

        public String getColumn1() {
            return column1;
        }

        public void setColumn1(String column1) {
            this.column1 = column1;
        }

        public String getColum2() {
            return colum2;
        }

        public void setColum2(String colum2) {
            this.colum2 = colum2;
        }

        public String getColumn3() {
            return column3;
        }

        public void setColumn3(String column3) {
            this.column3 = column3;
        }

        public Long getNonUNiqueId() {
            return nonUNiqueId;
        }

        public void setNonUNiqueId(Long nonUNiqueId) {
            this.nonUNiqueId = nonUNiqueId;
        }

        public LocalDateTime getCreation() {
            return creation;
        }

        public void setCreation(LocalDateTime creation) {
            this.creation = creation;
        }
    }


}
