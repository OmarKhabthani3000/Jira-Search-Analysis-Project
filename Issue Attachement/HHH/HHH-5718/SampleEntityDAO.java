package org.foo;

import org.hibernate.SessionFactory;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.query.AuditQueryCreator;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.AuditProperty;

public class SampleEntityDAO{


	@Autowired SessionFactory sessionFactory;

   public List<Object> getAllWithoutNullValues(){
	   	
		AuditReader reader = 	   	AuditReaderFactory.get(sessionFactory.getCurrentSession());
   		AuditQueryCreator creator = reader.createQuery();
   		AuditQuery query = creator.forRevisionsOfEntity(SampleEntityDAO.class, true, false);
   		
   		query.add(AuditEntity.property("someValue").isNotNull());
   		
   		return query.getResultList();
   
   }

}