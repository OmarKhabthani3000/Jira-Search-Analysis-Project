package org.hibernate.bugs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.entity.Product;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class JPAUnitTestCase {

    private EntityManagerFactory entityManagerFactory;

    @Before
    public void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");
    }

    @After
    public void destroy() {
        entityManagerFactory.close();
    }

    @Test
    public void updateWithNativeQueryTest() throws Exception {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Product product1 = new Product(1L,"product1");
        Product product2 = new Product(2L,"product2");
        entityManager.persist(product1);
        entityManager.persist(product2);

        product1.setName("{product1");
        product2.setName("{product2");

        entityManager.createNativeQuery(buildUpdateQuery(List.of(product1, product2)))
                .executeUpdate();

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private String buildUpdateQuery(List<Product> products) {
        String values = products.stream()
                .map(p -> "(" + p.getId() + "," + quote(p.getName()) + ")")
                .collect(Collectors.joining(","));

        String query = "update product set name = temp.name from (values %s) as temp (id, name) where product.id = temp.id"
                .formatted(values);
        return query;
    }

    private String quote(String text) {
        return "'" + text + "'";
    }
}
