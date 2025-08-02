package testcase;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Singleton
@Startup
public class TestEJB {
	
	@PersistenceContext
	EntityManager em;
	
	public TestEJB(){};
	
	@PostConstruct
	public void startup(){
		System.out.println("starting");
		System.out.println("UserEntity 1:" + em.find(UserEntity.class, 1));
		System.out.println("UserDetail 1:" + em.find(UserDetail.class, 1));

		System.out.println("UserDetail.user.id by em.find:" + em.find(UserDetail.class, 1).getUser().getId());
		
		TypedQuery<UserDetail> q1 = em.createNamedQuery("test1", UserDetail.class);
		UserDetail ud = q1.getSingleResult();
		
		System.out.println("UserDetail.user by query:" + ud.getUser());
		System.out.println("UserDetail.user.id by query:" + ud.getUser().getId());

	}

}
