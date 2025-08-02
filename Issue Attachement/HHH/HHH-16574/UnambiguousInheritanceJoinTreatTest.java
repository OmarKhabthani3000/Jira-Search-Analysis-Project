/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.orm.test.query.hql.treat;

import jakarta.persistence.*;
import org.hibernate.testing.orm.junit.EntityManagerFactoryScope;
import org.hibernate.testing.orm.junit.Jpa;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;


@Jpa(
		annotatedClasses = {
				UnambiguousInheritanceJoinTreatTest.Product.class,
				UnambiguousInheritanceJoinTreatTest.SoftwareProduct.class,
				UnambiguousInheritanceJoinTreatTest.IrrelevantProduct.class,
				UnambiguousInheritanceJoinTreatTest.ProductOwner.class,
				UnambiguousInheritanceJoinTreatTest.ProductOwnerExt1.class,
				UnambiguousInheritanceJoinTreatTest.ProductOwnerExtExt1.class,
				UnambiguousInheritanceJoinTreatTest.ProductOwnerExt2.class,
				UnambiguousInheritanceJoinTreatTest.Description.class
		}
)
public class UnambiguousInheritanceJoinTreatTest {


	@BeforeAll
	public void setUp(EntityManagerFactoryScope scope) {
		scope.inTransaction(
				entityManager -> {
					final var name = new Description();
					name.id = 1;
					name.text = "hi";
					Product product1 = new IrrelevantProduct( 1);
					Product product2 = new SoftwareProduct( 2);

					entityManager.persist(name);

					entityManager.persist( product1 );
					entityManager.persist( product2 );

				}
		);
	}

	@Test
	public void treatJoinClassTest0(EntityManagerFactoryScope scope) {

		scope.inTransaction(
				entityManager -> {
					String query = """
            SELECT p
            FROM Product p
            inner JOIN treat (p.owner AS ProductOwnerExt1) as own1 
	        inner join own1.description
	          
	          """;

					 entityManager.createQuery(query )
							.getResultList();

				}
		);
	}



	@Entity(name = "Product")
	public static class Product {

		@Id
		private Integer id;

		@ManyToOne
		public ProductOwner owner;



		public Product() {
		}

		public Product(Integer id) {
			this.id = id;
		}
	}

	@Entity(name = "SoftwareProduct")
	@Inheritance(strategy = InheritanceType.JOINED)
	public static class SoftwareProduct extends Product {



		public SoftwareProduct() {
		}

		public SoftwareProduct(Integer id) {
			super( id);
		}
	}

	@Entity(name = "IrrelevantProduct")
	@Inheritance(strategy = InheritanceType.JOINED)
	public static class IrrelevantProduct extends Product {


		public IrrelevantProduct() {
		}

		public IrrelevantProduct(Integer id) {
			super( id );

		}
	}
	@Entity
	@Inheritance(strategy = InheritanceType.JOINED)
	public static class ProductOwner{
		@Id
		private Integer id;

	}

	@Entity(name = "ProductOwnerExt1")
	@Inheritance(strategy = InheritanceType.JOINED)
	public static class ProductOwnerExt1 extends ProductOwner{
		@ManyToOne
		public Description description;

	}

	@Entity
	@Inheritance(strategy = InheritanceType.JOINED)
	public static class ProductOwnerExt2 extends ProductOwner{
		@ManyToOne
		public Description description;

	}



	@Entity
	@Inheritance(strategy = InheritanceType.JOINED)
	public static class Description {
		public Description() {

		}

		@Id
		public Integer id;
		public String text;
	}

	@Entity
	@Inheritance(strategy = InheritanceType.JOINED)
	public static class ProductOwnerExtExt1 extends ProductOwnerExt1 {
	}
};