package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class TestService
{
    @PersistenceContext
    protected EntityManager em;

    @Autowired
    TestRepository          testRepository;

    @PostConstruct
    public void onApplicationReady()
    {
        var list = em.createQuery("select distinct a from TestData a order by a.expTimest", TestData.class)
                .setFirstResult(0).setMaxResults(2).getResultList();

        testRepository.findDistinctByTypeOrderByExpTimestDesc(0, PageRequest.of(0, 25));
    }

}
