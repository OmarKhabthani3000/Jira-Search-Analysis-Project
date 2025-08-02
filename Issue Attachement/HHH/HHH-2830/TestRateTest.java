package nl.mendesgans.netting.repository.hibernate;

import java.math.BigDecimal;

import nl.test.domain.TestExchangeRate;
import nl.test.domain.TestRateOwner;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * 
 * @version $Id: $
 */
public class TestRateTest extends AbstractDependencyInjectionSpringContextTests {
    
    private SessionFactory sessionFactory;
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "applicationContext-test-core.xml" };
    }
    protected Session getSession() {
        return SessionFactoryUtils.getSession(sessionFactory, true);
    }
    public void testInsertFailure() {
        System.out.println("Inserting new owner and rate");
        TestRateOwner owner = new TestRateOwner();
        owner.setNaam("Test japie");
        TestExchangeRate rate = new TestExchangeRate();
        rate.setRate(new BigDecimal("0.12345678910111213"));
        owner.getRates().add(rate);
        rate.setOwner(owner);
        rate = new TestExchangeRate();
        rate.setRate(new BigDecimal("0.234567891011121314"));
        owner.getRates().add(rate);
        rate.setOwner(owner);
        getSession().saveOrUpdate(owner);
        getSession().flush();
        System.out.println("Done inserting new owner and rate");
    }
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }    
}
