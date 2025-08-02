package src.test.java.org.hibernate.bugs;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java
 * Persistence API.
 */
public class JPAUnitTestCase
{

  private EntityManagerFactory entityManagerFactory;

  @Before
  public void init()
  {
    entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");
  }

  @After
  public void destroy()
  {
    entityManagerFactory.close();
  }

  // Entities are auto-discovered, so just add them anywhere on class-path
  // Add your tests, using standard JUnit.
  @Test
  public void hhh153Test() throws Exception
  {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();

    // Do stuff...

    String queryString = "SELECT e FROM PostComment e " //
        + "JOIN FETCH e.post ep " //
        + "LEFT OUTER JOIN FETCH ep.hierarchy " //
        + "JOIN e.post t " //
        + "WHERE t.hierarchy.id IN :hier " //
        + "AND e.nr = :nar ";

    Map<String, Object> queryParameters = new HashMap<>();
    queryParameters.put("hier", 1l);
    queryParameters.put("nar", 2);

    TypedQuery<PostComment> query = entityManager.createQuery(queryString, PostComment.class);
    queryParameters.entrySet().forEach(p -> query.setParameter(p.getKey(), p.getValue()));

    // End stuff

    entityManager.getTransaction().commit();
    entityManager.close();
  }

  @Entity(name = "PostComment")
  public static class PostComment
  {

    @Id
    @GeneratedValue
    private Long id;

    private int  nr;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

  }

  @Entity(name = "Post")
  public static class Post
  {

    @Id
    @GeneratedValue
    private Long      id;

    private String    title;

    @OneToOne(targetEntity = Hierarchy.class)
    @NotFound(action = NotFoundAction.IGNORE)
    private Hierarchy hierarchy;

  }

  @Entity(name = "Hierarchy")
  public static class Hierarchy
  {

    @Id
    @GeneratedValue
    private Long id;

  }

}
