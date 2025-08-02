package org.hibernate.orm.test.jpa.criteria;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.query.sqm.NodeBuilder;
import org.hibernate.query.sqm.tree.SqmCopyContext;
import org.hibernate.query.sqm.tree.select.SqmSelectStatement;
import org.hibernate.testing.jdbc.SQLStatementInspector;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.RequiresDialect;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DomainModel(
		annotatedClasses = {
				JpaBugTest.Thing.class,
				JpaBugTest.FancyItem.class,
				JpaBugTest.Item.class,
				JpaBugTest.Owner.class
		}
)
@SessionFactory(useCollectingStatementInspector = true)
// Run only on H2 to avoid dealing with SQL dialect differences
@RequiresDialect( H2Dialect.class )
public class JpaBugTest {

	@Test
	public void testQueryChildUseParentWithSqmCriteria(SessionFactoryScope scope) {
		SQLStatementInspector sqlStatementInterceptor = scope.getCollectingStatementInspector();
		scope.inTransaction(
				entityManager -> {
					NodeBuilder builder = scope.getSessionFactory().getQueryEngine().getCriteriaBuilder();
					SqmSelectStatement<Tuple> criteria = builder.createTupleQuery();

					Root<FancyItem> root = criteria.from(FancyItem.class);
					Join<FancyItem, Owner> templateJoin = root.join("owner", JoinType.LEFT);
					criteria = criteria.multiselect(root.get("id"))
							.where(
									builder.and(
											builder.and(
													builder.isTrue(templateJoin.get("alive")),
													builder.equal(root.get("serialNumber"), 3)
											),
											builder.equal(root.get("field1"), "value")
									)
							);

					// Using a copy of the criteria leads to failure of this test
					// The copied sqm path is: BasicSqmPathSource(id : Long)
					// Rather than the expected: SingularAttributeImpl$Identifier: org.hibernate.orm.test.jpa.criteria.JpaBugTest$Thing#id(BASIC)
					// This causes the generated SQL to omit a join to the base (Thing) class where the identifier is defined
					//
					// If this call to copy() is omitted, the generated SQL is as expected
					criteria = criteria.copy( SqmCopyContext.simpleContext() );
					TypedQuery<Tuple> query = entityManager.createQuery( criteria );
					sqlStatementInterceptor.clear();
					query.getResultList();
					sqlStatementInterceptor.assertExecutedCount( 1 );
					assertEquals(
							"select " +
									"f1_0.\"id\" " +
									"from \"JpaBugTest$FancyItem\" f1_0 " +
									"join \"JpaBugTest$Item\" f1_1 on f1_0.\"id\"=f1_1.id " +
									"left join \"JpaBugTest$Owner\" o1_0 on o1_0.id=f1_0.\"owner_id\" " +
									"where o1_0.alive and f1_0.serialNumber=? and f1_1.field1=?",
							sqlStatementInterceptor.getSqlQueries().get( 0 )
					);
				}
		);
	}

	@MappedSuperclass
	abstract public class Thing {
		@Id
		private Long id;

		public Thing() {
		}

		public Thing(Long id) {
			this.id = id;
		}
	}

	@Entity
	@Inheritance(strategy = InheritanceType.JOINED)
	public class Item extends Thing {

		private String field1;
		private Integer field2;

		public Item() {
		}
	}


	@Entity
	public class FancyItem extends Item {

		private Integer serialNumber;

		@ManyToOne
		@JoinColumn
		private Owner owner;

		public FancyItem() {
		}
	}

	@Entity
	public class Owner extends Thing {

		private Boolean alive;

		public Owner() {
		}
	}
}
