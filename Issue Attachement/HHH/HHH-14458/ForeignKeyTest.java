import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ForeignKeyTest {

  @Test
  public void testForeignKeyShouldNotBeCreated() {
    StandardServiceRegistryBuilder srb = new StandardServiceRegistryBuilder()
        .applySetting("hibernate.show_sql", "true").applySetting("hibernate.hbm2ddl.auto", "create-drop")
        .addInitiator(new SchemaManagementToolInitiator());
    Metadata metadata = new MetadataSources(srb.build()).addAnnotatedClass(Entity1.class).addAnnotatedClass(Entity2.class).buildMetadata();
    metadata.buildSessionFactory();
  }

  @Entity
  public static class Entity1 {

    @Id
    private long id;

    @OneToMany(mappedBy = "e1")
    private List<Entity2> e2s;
  }

  @Entity
  public static class Entity2 {

    @Id
    private long id;

    
    @ManyToOne
    @JoinTable(
        name = "e1_e2",
        joinColumns = @JoinColumn(name = "e2_id", foreignKey = @ForeignKey(name = "fk_custom_1")),
        inverseJoinColumns = @JoinColumn(name = "e1_id", foreignKey = @ForeignKey(name = "fk_custom_2"))
    )
    private Entity1 e1;
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
              Table joinTable = null;
              for (Table table : namespace.getTables()) {
                if (table.getName().equals("e1_e2")) {
                  joinTable = table;
                  break;
                }
              }
              assertNotNull(joinTable);
              assertEquals("Creates 2 foreign keys", 2, joinTable.getForeignKeys().size());
              for (org.hibernate.mapping.ForeignKey fk : joinTable.getForeignKeys().values()) {
                MatcherAssert.assertThat("Custom foreign key name is used.", fk.getName(), Matchers.startsWith("fk_custom_"));
              }
            }
            sc.doCreation(metadata, executionOptions, sourceDescriptor, targetDescriptor);
          };
        }
      };
    }

  }

  
}
