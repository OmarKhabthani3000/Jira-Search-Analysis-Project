package org.hibernate.bugs;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.hibernate.SessionFactory;
import org.hibernate.bugs.EntityExample;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
import org.hibernate.hql.spi.NamedParameterInformation;
import org.hibernate.hql.spi.ParameterTranslations;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.hql.spi.QueryTranslatorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.hibernate.internal.util.collections.CollectionHelper.EMPTY_MAP;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

  private EntityManagerFactory entityManagerFactory;

  @Before
  public void init() {
    entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
  }

  @After
  public void destroy() {
    entityManagerFactory.close();
  }

  // Entities are auto-discovered, so just add them anywhere on class-path
  // Add your tests, using standard JUnit.
  @Test
  public void hhh123Test() throws Exception {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Tuple> cq = cb.createTupleQuery();
    Root<EntityExample> root = cq.from(EntityExample);

    // generated
    List<Selection<?>> selections = new ArrayList<>();
    selections.add(root.get("createdOn"));
    selections.add(root.get("description"));

    cq.multiselect(selections).distinct(true);

    TypedQuery<Tuple> typedQuery = entityManager.createQuery(cq);
    String hqlQueryText = typedQuery.unwrap(org.hibernate.Query.class).getQueryString();

    QueryTranslatorFactory translatorFactory = new ASTQueryTranslatorFactory();

    QueryTranslator translator = translatorFactory.createQueryTranslator(
        hqlQueryText, hqlQueryText,
        EMPTY_MAP,
        (SessionFactoryImplementor) entityManager.getEntityManagerFactory().unwrap(SessionFactory.class),
        null
    );
    translator.compile(EMPTY_MAP, false);

    Query nativeCountQuery = entityManager.createNativeQuery(
        "select count(*) from (" +
            translator.getSQLString() +
            ")"
    );
    ParameterTranslations parameterTranslations = translator.getParameterTranslations();

    for (Parameter<?> parameter : typedQuery.getParameters()) {
      for (String name : parameterTranslations.getNamedParameterInformationMap().keySet()) {
        int position = parameterTranslations.getNamedParameterInformation(name).getSourceLocations()[0]; // could do a loop; uninteresting
        nativeCountQuery.setParameter(
            position + 1,
            typedQuery.getParameterValue(name) // this fails but shouldn't; the parameters should be in the explicit parameter list
        );
      }
    }

    // this is the value of interest
    System.out.println(((BigDecimal)nativeCountQuery.getSingleResult()).longValue());

    entityManager.getTransaction().commit();
    entityManager.close();
  }
}