package org.mydom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mydom.entity.Feature;
import org.mydom.entity.Product;


public class Test {

    private static Logger log = LoggerFactory.getLogger(Test.class);

    @Test
    private void orphanRemovalTest() {
        
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Name-PU");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //1. Persist inital product
		entityManager.getTransaction().begin();
		Product product = new Product();
		List<Feature> features = new ArrayList<Feature>();
		Feature newFeature = new Feature(product);
		newFeature.setName("Feature 1 that should be deleted");
		entityManager.persist(newFeature);
		features.add(newFeature);
		product.setFeatures(features);
		entityManager.persist(product);
		entityManager.flush();
		entityManager.getTransaction().commit();
		entityManager.clear();

        //2. Changing product's features: first product "should be" deleted and a new one added.
		entityManager.getTransaction().begin();
		Product productFound = entityManager.find(Product.class, product.getId());
		log.info("found=" + productFound +" class "+productFound.getClass());
		productFound.getFeatures().clear();
		productFound.setName("Name changed");
		
		Feature newFeature2 =  new Feature(productFound);
		newFeature2.setName("Feature 2 that should remain");
		entityManager.persist(newFeature2);
		productFound.getFeatures().add(newFeature2);
		log.info("Product entity is managed: " + entityManager.contains(productFound));

		log.info("Updating product");
		entityManager.merge(productFound);
		
		entityManager.getTransaction().commit();
		  
	//3. The test itself
		entityManager.clear();
		Product productTest = entityManager.find(Product.class, product.getId());
		log.info("Number of Product features " + productTest.getFeatures().size());
		assert(productTest.getFeatures.size()==1);
    }

}

