/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.annotations.embeddables.collection;

import java.util.Objects;
import java.util.Set;

import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Test;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import static org.assertj.core.api.Assertions.assertThat;

@DomainModel(
		annotatedClasses = {
				ElementCollectionWithNullableColumnsTest.Product.class
		}
)
@SessionFactory
@TestForIssue(jiraKey = "HHH-15453")
public class ElementCollectionWithNullableColumnsTest {

	@Test
	public void testMerge(SessionFactoryScope scope) {
		scope.inTransaction(
				session -> {
					Product product = new Product( 1L, "Xbox", Set.of( new ProductVariant( "black", "slim" ), new ProductVariant( "black", null ) ) );
					session.persist( product );
				}
		);

		scope.inTransaction(
				session -> {
					Product product = session.get( Product.class, 1L );
					assertThat( product ).isNotNull();
					assertThat( product.getVariants() ).hasSize( 2 );

					product.getVariants().removeIf( v -> v.size == null );
				}
		);

		scope.inTransaction(
				session -> {
					Product product = session.get( Product.class, 1L );
					assertThat( product ).isNotNull();
					assertThat( product.getVariants() ).hasSize( 1 );
				}
		);
	}

	@Entity(name = "Product")
	public static class Product {
		@Id
		private Long id;

		private String name;

		@ElementCollection
		private Set<ProductVariant> variants;

		public Product() {
		}

		public Product(Long id, String name, Set<ProductVariant> variants) {
			this.id = id;
			this.name = name;
			this.variants = variants;
		}

		public Long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public Set<ProductVariant> getVariants() {
			return variants;
		}
	}

	@Embeddable
	public static class ProductVariant {

		String color;
		String size;

		public ProductVariant() {
		}

		public ProductVariant(String color, String size) {
			this.color = color;
			this.size = size;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

		public String getSize() {
			return size;
		}

		public void setSize(String size) {
			this.size = size;
		}

		@Override
		public boolean equals(Object o) {
			if ( this == o ) {
				return true;
			}
			if ( o == null || getClass() != o.getClass() ) {
				return false;
			}

			ProductVariant that = (ProductVariant) o;

			if ( !Objects.equals( color, that.color ) ) {
				return false;
			}
			return Objects.equals( size, that.size );
		}

		@Override
		public int hashCode() {
			int result = color != null ? color.hashCode() : 0;
			result = 31 * result + ( size != null ? size.hashCode() : 0 );
			return result;
		}
	}

}
