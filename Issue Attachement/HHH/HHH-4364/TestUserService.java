/**
 *
 */
package edu.upmc.ccweb.dosimetry.hibtest;

import java.util.List;

import org.hibernate.SessionFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import test.edu.upmc.ccweb.dosimetry.TestFixture;

public class TestUserService extends TestFixture {

	private UserService userService;


	@BeforeTest
	public void getUserService()  {
		this.userService = new UserService();
		userService.setSessionFactory(getSessionFactoryFromSomewhere());
	}

	private SessionFactory getSessionFactoryFromSomewhere() {
		// not sure how you guys do this
		return null;
	}


	@Test
	public void findByFirstName()  {
		List<User> firstNameResults = userService.findByFirstName("asdfasdfsdfa");
		assertEquals(0, firstNameResults.size());
	}

	@Test
	public void findByLoginName()  {
		List<User> loginNameResults = userService.findByLoginName("asdfasdfdf");
		assertEquals(0, loginNameResults.size());
	}
}
/*

Hibernate:
    select
        user0_.id as id6_,
        user0_.loginName as loginName6_,
        user0_.firstName as firstName6_,
        user0_.lastName as lastName6_
    from
        User user0_
    where
        user0_.firstName=?
Creating C:\projects\dosimetry\test-output\dosimetry\edu.upmc.ccweb.dosimetry.hibtest.TestUserService.html
PASSED: findByFirstName
FAILED: findByLoginName
org.hibernate.MappingException: Named query not known: User.findByLoginName
	at org.hibernate.impl.AbstractSessionImpl.getNamedQuery(AbstractSessionImpl.java:70)
	at org.hibernate.impl.SessionImpl.getNamedQuery(SessionImpl.java:1260)
	at edu.upmc.ccweb.dosimetry.hibtest.UserService.findByLoginName(UserService.java:25)
	at edu.upmc.ccweb.dosimetry.hibtest.TestUserService.findByLoginName(TestUserService.java:39)
... Removed 17 stack frames

===============================================
    edu.upmc.ccweb.dosimetry.hibtest.TestUserService
    Tests run: 2, Failures: 1, Skips: 0
===============================================
*/