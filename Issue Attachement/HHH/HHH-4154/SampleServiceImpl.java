package org.frecklepuppy.bb.service.impl;

import java.util.Collection;
import java.util.LinkedList;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.frecklepuppy.bb.model.SampleDerived;
import org.frecklepuppy.bb.service.SampleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("sample")
public class SampleServiceImpl implements SampleService
{
	private static final Log log = LogFactory.getLog(SampleServiceImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional
	public Long createSample()
	{
		log.debug("createSample()");
		SampleDerived x = new SampleDerived();
		entityManager.persist(x);
		for (int i = 0; i < 3; i++)
		{
			SampleDerived y = new SampleDerived();
			x.getChildren().add(y);
			entityManager.persist(y);
		}
		entityManager.flush();
		entityManager.clear();
		log.debug("id=" + x.getId());
		return x.getId();
	}

	@Override
	@Transactional(readOnly=true)
	public Collection<SampleDerived> findChildren(Long id)
	{
		log.debug("findChildren(" + id + ")");
		SampleDerived parent = entityManager.find(SampleDerived.class, id);
		if (parent == null) return new LinkedList<SampleDerived>();
		log.debug("children:");
		for (SampleDerived c : parent.getChildren())
			log.debug(c.getId());
		return parent.getChildren();
	}

	public void setEntityManager(EntityManager entityManager)
    {
    	this.entityManager = entityManager;
    }

}
