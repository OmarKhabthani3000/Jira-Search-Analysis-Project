import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.AnnotationException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Test;

// Schema export does not consistently follow naming strategy for unique constraints
public class UniqueConstraintTest {
  @Entity
  public static class Name {
    @Id
    Long id;
  }

  @Entity
  // Valid constraint
  @Table(uniqueConstraints = @UniqueConstraint(columnNames = { "first_name", "lastName" }))
  public static class Foo {
    @Id
    Long id;
    @OneToOne
    private Name firstName;
    private String lastName;
  }

  @Entity
  // Invalid constraint
  @Table(uniqueConstraints = @UniqueConstraint(columnNames = { "first_name", "last_name" }))
  public static class Bar {
    @Id
    Long id;
    @OneToOne
    private Name firstName;
    private String lastName;
  }

  private static AnnotationConfiguration config(Class<?> clazz) {
    return new AnnotationConfiguration().addAnnotatedClass(Name.class).addAnnotatedClass(clazz)
        .setNamingStrategy(ImprovedNamingStrategy.INSTANCE).setProperty("hibernate.dialect",
            "org.hibernate.dialect.HSQLDialect");
  }

  @Test
  public void testFoo() {
    new SchemaExport(config(Foo.class));
  }

  @Test(expected = AnnotationException.class)
  public void testBar() {
    new SchemaExport(config(Bar.class));
  }
}
