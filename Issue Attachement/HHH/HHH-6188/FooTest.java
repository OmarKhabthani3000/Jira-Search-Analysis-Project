
package de.hhla.zeus.domain.model;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration("classpath:META-INF/spring/zeus-integrationtest-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class FooTest {

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  @Rollback(false)
  @Test
  public void testGetId() {
    UUID id = UUID.randomUUID();
    Foo foo =
        new Foo(id.toString().replaceAll("-", "").toUpperCase(), id);
    entityManager.persist(foo);
  }

}
