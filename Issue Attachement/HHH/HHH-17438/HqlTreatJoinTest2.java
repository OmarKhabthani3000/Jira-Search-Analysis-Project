package org.hibernate.orm.test.query.hql.treat;

import jakarta.persistence.*;
import org.hibernate.query.spi.QueryImplementor;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DomainModel(
		annotatedClasses = {
				HqlTreatJoinTest2.ParentEntity.class,
				HqlTreatJoinTest2.AbstractChildEntity.class,
				HqlTreatJoinTest2.A_ChildEntity.class,
				HqlTreatJoinTest2.B_ChildEntity.class
		}
)
@SessionFactory
public class HqlTreatJoinTest2 {

	@BeforeAll
	public void setUp(SessionFactoryScope scope) {
		scope.inTransaction(
				session -> {
					ParentEntity parentA = new ParentEntity( 101, "parent_a");
					parentA.addChild(new A_ChildEntity( 1, "child_a", "a" ));

					ParentEntity parentB = new ParentEntity( 102, "parent_b");
					parentB.addChild(new B_ChildEntity( 2, "child_b", "b" ));

					session.persist(parentA);
					session.persist(parentB);
				}
		);

	}

	@Test
	public void testTreatQuery(SessionFactoryScope scope) {
		scope.inTransaction(
				session -> {
					QueryImplementor<ParentEntity> query = session.createQuery(
							"SELECT parent FROM ParentEntity parent " +
									   "JOIN parent.children child " +
									   "WHERE (child.name = 'child_a' OR child.name = 'child_b') " +
									   "   OR (TYPE(child) = B_ChildEntity AND TREAT(child AS B_ChildEntity).b = 'b')" // adding OR clause with TREAT causes data set to be restricted
							,
							ParentEntity.class
					);
					List<ParentEntity> result = query.list();
					assertThat( result.size() ).isEqualTo( 2 );
				}
		);
	}

	@Entity(name = "ParentEntity")
	public static class ParentEntity {
		@Id
		private long id;

		private String name;

		@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
		private List<AbstractChildEntity> children = new ArrayList<>();

		public ParentEntity() {
		}

		public ParentEntity(long id, String name) {
			this.id = id;
			this.name = name;
		}

		public long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public void addChild(AbstractChildEntity child) {
			children.add(child);
			child.setParent(this);
		}
	}

	@Entity(name = "AbstractChildEntity")
	@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
	@DiscriminatorColumn( name = "discriminator", discriminatorType = DiscriminatorType.CHAR )
	@DiscriminatorValue( "X" )
	public static abstract class AbstractChildEntity {

		@Id
		private long id;

		private String name;

		@ManyToOne
		private ParentEntity parent;

		public AbstractChildEntity() {
		}

		public AbstractChildEntity(long id, String name) {
			this.id = id;
			this.name = name;
		}

		public long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public void setParent(ParentEntity parent) {
			this.parent = parent;
		}
	}

	@Entity(name = "A_ChildEntity")
	@DiscriminatorValue( "A" )
	public static class A_ChildEntity extends AbstractChildEntity {
		private String a;

		public A_ChildEntity() {
		}

		public A_ChildEntity(long id, String name, String a) {
			super(id, name);
			this.a = a;
		}

		public String getA() {
			return a;
		}
	}

	@Entity(name = "B_ChildEntity")
	@DiscriminatorValue( "B" )
	public static class B_ChildEntity extends AbstractChildEntity {
		private String b;

		public B_ChildEntity() {
		}

		public B_ChildEntity(long id, String name, String b) {
			super(id, name);
			this.b = b;
		}

		public String getB() {
			return b;
		}
	}
}
