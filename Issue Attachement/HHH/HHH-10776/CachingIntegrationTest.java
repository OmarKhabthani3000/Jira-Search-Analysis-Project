


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:merchant-context-test-ehcache.xml" })
@Transactional
public class CachingIntegrationTest {
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	@Entity
	@Table(name = "my_shops")
	@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region = "merchant")
	public static class Shop {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		@Column(unique = true)
		private String name;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	@Test
	public void testSimple() {
		Long shopId;
		{
			Session currentSession = sessionFactory.getCurrentSession();
			Shop shop1 = new Shop();
			shop1.setName("myshop1");
			currentSession.save(shop1);

			Shop shop2 = new Shop();
			shop2.setName("myshop2");
			currentSession.save(shop2);

			shopId = shop2.getId();

			TestTransaction.flagForCommit();
			TestTransaction.end();
		}
		try {
			TestTransaction.start();
			Session currentSession = sessionFactory.getCurrentSession();
			Shop shop = (Shop) currentSession.get(Shop.class, shopId);
			shop.setName("myshop1");
			TestTransaction.flagForCommit();
			TestTransaction.end();
		} catch (Exception e) {
			e.printStackTrace();
		}

		{
			TestTransaction.start();
			Session currentSession = sessionFactory.getCurrentSession();
			Shop shop = (Shop) currentSession.get(Shop.class, shopId);
			System.out.println(shop.getName());
			TestTransaction.flagForCommit();
			TestTransaction.end();
		}
	}
}
