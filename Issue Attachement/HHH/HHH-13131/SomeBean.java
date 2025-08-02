package com.example.querytest;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

@Service
public class SomeBean {

    private final MyEntityDao dao;
    private final EntityManagerFactory emf;

    SomeBean(MyEntityDao dao, EntityManagerFactory emf) {
        this.dao = dao;
        this.emf = emf;
    }

    @PostConstruct
    void findEntity() {
        // dao.findByQ2(null);
        EntityManager em = emf.createEntityManager();
        Query correct = em.createQuery("select m from MyEntity m where (:e is null or :e=m.something)");
        correct.setParameter("e", null);
        correct.getResultList();

        Query query = em.createQuery("select m from MyEntity m where (?1 is null or ?1=m.something)");
        query.setParameter(1, null);
        query.getResultList();


    }
}
