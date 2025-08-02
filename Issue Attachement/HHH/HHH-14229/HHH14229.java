/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.foreignkeys.disabled;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.StreamSupport;

import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.mapping.Table;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;

/**
 * {@inheritDoc}
 *
 * @author Yanming Zhou
 */
@TestForIssue(jiraKey = "HHH-14229")
public class HHH14229 {

	private static final String TABLE_NAME = "tree_entity";

	@Test
	public void test() {
		Metadata metadata = new MetadataSources(new StandardServiceRegistryBuilder().build())
				.addAnnotatedClass(TreeEntity.class).buildMetadata();
		Table table = StreamSupport.stream(metadata.getDatabase().getNamespaces().spliterator(), false)
				.flatMap(namespace -> namespace.getTables().stream()).filter(t -> t.getName().equals(TABLE_NAME))
				.findFirst().orElse(null);
		assertNotNull(table);
		assertTrue("Foreign key should not be created", table.getForeignKeys().isEmpty());
	}

	@Entity
	@javax.persistence.Table(name = TABLE_NAME)
	public static class TreeEntity {

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		private Long id;

		@ManyToOne
		@JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
		private TreeEntity parent;

		@OneToMany(mappedBy = "parent")
		// workaround
		// @org.hibernate.annotations.ForeignKey(name = "none")
		private Collection<TreeEntity> children = new ArrayList<>(0);

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public TreeEntity getParent() {
			return parent;
		}

		public void setParent(TreeEntity parent) {
			this.parent = parent;
		}

		public Collection<TreeEntity> getChildren() {
			return children;
		}

		public void setChildren(Collection<TreeEntity> children) {
			this.children = children;
		}

	}

}
