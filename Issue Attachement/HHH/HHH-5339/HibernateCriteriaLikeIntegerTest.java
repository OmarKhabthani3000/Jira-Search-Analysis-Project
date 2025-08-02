package org.wfp.rita.test.hibernate;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.IlikeExpression;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.TypedValue;
import org.wfp.rita.test.base.HibernateTestBase;

/**
 * Hibernate doesn't like using {@link Restrictions#ilike} on integer columns.
 * <p>
 * It uses the SQL lowercase function on the column, the result of which
 * must be a character type (CHAR or VARCHAR) because:
 * <blockquote>LOWER Converts a string to all lowercase characters</blockquote>
 * (<a href="http://oreilly.com/catalog/sqlnut/chapter/ch04.html">SQL
 * in a Nutshell</a>).
 * <p>
 * But {@link IlikeExpression#getTypedValues} asks the criteria query
 * for the type of the typed values, which returns the underlying type,
 * which in this case is an integer type.
 * <p>
 * Hibernate then throws a {@link ClassCastException}:
 * <ul>
 * <li>java.lang.ClassCastException: java.lang.String cannot be cast to java.lang.Integer
 * <li>at org.hibernate.type.IntegerType.set(IntegerType.java:64)
 * <li>at org.hibernate.type.NullableType.nullSafeSet(NullableType.java:154)
 * <li>at org.hibernate.type.NullableType.nullSafeSet(NullableType.java:136)
 * <li>at org.hibernate.loader.Loader.bindPositionalParameters(Loader.java:1732)
 * <li>at org.hibernate.loader.Loader.bindParameterValues(Loader.java:1703)
 * <li>at org.hibernate.loader.Loader.prepareQueryStatement(Loader.java:1593)
 * <li>at org.hibernate.loader.Loader.doQuery(Loader.java:696)
 * <li>at org.hibernate.loader.Loader.doQueryAndInitializeNonLazyCollections(Loader.java:259)
 * <li>at org.hibernate.loader.Loader.doList(Loader.java:2232)
 * <li>at org.hibernate.loader.Loader.listIgnoreQueryCache(Loader.java:2129)
 * <li>at org.hibernate.loader.Loader.list(Loader.java:2124)
 * <li>at org.hibernate.loader.criteria.CriteriaLoader.list(CriteriaLoader.java:118)
 * <li>at org.hibernate.impl.SessionImpl.list(SessionImpl.java:1597)
 * <li>at org.hibernate.impl.CriteriaImpl.list(CriteriaImpl.java:306)
 * <li>at org.wfp.rita.datafacade.RequestDao.listRequests(RequestDao.java:736)
 * </ul>
 * @see <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-5339">HHH-5339</a>
 * @author Chris Wilson <chris+rita@aptivate.org>
 */
public class HibernateCriteriaLikeIntegerTest extends HibernateTestBase
{
    @Entity
    @Table(name="product")
    private static class Product
    {
        @Id
        public Integer id;
        public Product(int id) { this.id = id; }
        public Product() { }
    }
    
    protected Class[] getMappings()
    {
        return new Class[]{Product.class};
    }
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        Session session = openSession();
        Transaction tx = session.beginTransaction();
        session.save(new Product(1));
        session.save(new Product(2));
        session.save(new Product(11));
        session.save(new Product(12));
        tx.commit();
        session.close();
    }
    
    private static class FixedIlikeExpression extends IlikeExpression
    {
    	private Object value;
    	
		public FixedIlikeExpression(String propertyName, Object value)
		{
			super(propertyName, value);
			this.value = value;
		}

		@Override
		public TypedValue[] getTypedValues(Criteria criteria,
				CriteriaQuery criteriaQuery) throws HibernateException
		{
			return new TypedValue[] { 
					new TypedValue(Hibernate.STRING, value, EntityMode.POJO)
			};
		}
    }
    
    private void assertCriteria(Criterion r)
    {
        Session session = openSession();
        Criteria c = session.createCriteria(Product.class);
        c.add(r);
        List<Product> products = c.list();
        assertEquals("1", products.get(0).id + "");
        assertEquals("11", products.get(1).id + "");
        assertEquals("12", products.get(2).id + "");
        session.close();
    }
    
    public void testFailing()
    {
        assertCriteria(Restrictions.ilike("id", "1%"));
    }
    
    public void testWorkaround()
    {
        assertCriteria(new FixedIlikeExpression("id", "1%"));
    }    
}
