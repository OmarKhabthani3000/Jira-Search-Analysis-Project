/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

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
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.mapping.Table;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.spi.SchemaCreator;
import org.hibernate.tool.schema.spi.SchemaManagementTool;
import org.hibernate.tool.schema.spi.SchemaMigrator;
import org.junit.Test;

/**
 * {@inheritDoc}
 *
 * @author Yanming Zhou
 */
public class ForeignKeyTest {

	@Test
	public void testForeignKeyShouldNotBeCreated() {
		StandardServiceRegistryBuilder srb = new StandardServiceRegistryBuilder()
				.applySetting("hibernate.show_sql", "true").applySetting("hibernate.hbm2ddl.auto", "create-drop")
				.addInitiator(new SchemaManagementToolInitiator());
		Metadata metadata = new MetadataSources(srb.build()).addAnnotatedClass(TreeEntity.class).buildMetadata();
		metadata.buildSessionFactory();
	}

	@Entity
	@javax.persistence.Table(name = "tree_entity")
	public static class TreeEntity {

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		private Long id;

		@ManyToOne
		@JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
		private TreeEntity parent;

		@OneToMany(mappedBy = "parent")
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

	@SuppressWarnings("rawtypes")
	public class SchemaManagementToolInitiator implements StandardServiceInitiator<SchemaManagementTool> {

		@Override
		public Class<SchemaManagementTool> getServiceInitiated() {
			return SchemaManagementTool.class;
		}

		@Override
		public SchemaManagementTool initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
			return new HibernateSchemaManagementTool() {
				private static final long serialVersionUID = 1L;

				@Override
				public SchemaMigrator getSchemaMigrator(Map options) {
					return super.getSchemaMigrator(options);
				}

				@Override
				public SchemaCreator getSchemaCreator(Map options) {
					SchemaCreator sc = super.getSchemaCreator(options);
					return (metadata, executionOptions, sourceDescriptor, targetDescriptor) -> {
						for (Namespace namespace : metadata.getDatabase().getNamespaces()) {
							Table treeEntityTable = null;
							for (Table table : namespace.getTables()) {
								if (table.getName().equals("tree_entity")) {
									treeEntityTable = table;
									break;
								}
							}
							assertNotNull(treeEntityTable);
							assertEquals("Foreign key should not be created", 0,
									treeEntityTable.getForeignKeys().size());
						}
						sc.doCreation(metadata, executionOptions, sourceDescriptor, targetDescriptor);
					};
				}
			};
		}

	}

}
