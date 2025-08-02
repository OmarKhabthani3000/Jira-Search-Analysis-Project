package com.iampfac.demo.app;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.iampfac.demo.config.ApplicationConfiguration;
import com.iampfac.demo.core.user.User;
import com.iampfac.demo.data.jpa.JpaProxyUserRepository;
import com.iampfac.demo.data.jpa.UserJpaEntity;
import com.iampfac.demo.data.jpa.UserJpaRepository;

public class MainApp {
	
	public static void main(String[] args) {
		ApplicationContext ctx = null;
		
		try {
			ctx = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
			System.out.println("main() LocalContainerEntityManagerFactoryBean ctx: " + ctx);
			//LocalContainerEntityManagerFactoryBean lcemfb = (LocalContainerEntityManagerFactoryBean)ctx.getBean("entityManagerFactory", LocalContainerEntityManagerFactoryBean.class);
			LocalContainerEntityManagerFactoryBean lcemfb = ctx.getBean( LocalContainerEntityManagerFactoryBean.class );
			EntityManagerFactory emf = lcemfb.getObject();
			EntityManager em = emf.createEntityManager();
			System.out.println("main() LocalContainerEntityManagerFactoryBean lcemfb: " + lcemfb);
			System.out.println("main() LocalContainerEntityManagerFactoryBean EntityManagerFactory emf: " + emf);
			System.out.println("main() LocalContainerEntityManagerFactoryBean EntityManager em: " + em);
			
			DemoJpaService service = ctx.getBean( DemoJpaService.class );
			
			
			// get user repository bean
			UserJpaRepository userrepository =  service.getUserJpaRepository();
			
			UserJpaEntity dave = new UserJpaEntity("Dave", "Mathews");
			dave = userrepository.save(dave);
			System.out.println("main() dave: " + dave);

			UserJpaEntity carter = new UserJpaEntity("Carter", "Beauford");
			carter = userrepository.save(carter);
			System.out.println("main() carter: " + carter);

			List<UserJpaEntity> daveResults = userrepository.findByName( dave.getFirstname() );
			System.out.println("main() daveResults: " + daveResults);

			List<UserJpaEntity> carterResults = userrepository.findByName( carter.getFirstname() );
			System.out.println("main() carterResults: " + carterResults);
			
			// get proxy user repository bean
			JpaProxyUserRepository proxyuserrepository = service.getProxyUserJpaRepository();
			UserJpaRepository repository = proxyuserrepository.getJpaRepository();
			
			User dave_r = new User( "Dave", "Mathews" );
			dave_r = proxyuserrepository.save( dave_r );

			User carter_r = new User( "Carter", "Beauford" );
			carter_r = proxyuserrepository.save( carter_r );

			List<User> daveResults_r = proxyuserrepository.byName( dave_r.getFirstName() );
		
			List<User> carterResults_r = proxyuserrepository.byName( carter_r.getFirstName() );
			
		} catch (BeansException e) {
			e.printStackTrace();
		}
	}

}
