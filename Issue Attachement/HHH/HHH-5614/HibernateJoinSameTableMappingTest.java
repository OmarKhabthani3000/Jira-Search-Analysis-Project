package org.wfp.rita.test.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import junit.framework.TestCase;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.dialect.MySQLDialect;

/**
 * Trying to join Movement back to itself in order to find the pair of a
 * given movement, I found an unexpected MappingException. Seems related
 * to <a href="http://opensource.atlassian.com/projects/hibernate/browse/ANN-509">ANN-509</a>.
 * 
 * @see <a href="http://opensource.atlassian.com/projects/hibernate/browse/ANN-509">ANN-509</a>
 * @see <a href="http://opensource.atlassian.com/projects/hibernate/browse/ANN-791">ANN-791</a>
 * 
 * @author Chris Wilson <chris+rita@aptivate.org>
 */
public class HibernateJoinSameTableMappingTest extends TestCase
{
	@Entity
	private static class SelfJoinFails
	{
		@Id
	    public Integer id;
		
		@Column(name="move_site_id")
		public Integer moveSiteId;
	    
	    @ManyToOne(optional=true)
        @JoinColumn(name="shipment_id", referencedColumnName="id")
	    private Shipment shipment;

	    /**
	     * Private copy for querying the matching pair of this movement.
	     * Used by {@link org.wfp.rita.datafacade.RequestDao#listRequests} and 
	     * {@link RequestDao.RequestFilter#requestLineStateOnShipmentDestination}.
	     * 
	     * <p>TODO check the pairing conditions, not all are catered for?
	     */
	    @OneToOne(fetch=FetchType.LAZY)
	    @JoinColumns({
	        @JoinColumn(name="move_site_id", referencedColumnName="move_site_id",
	        	insertable=false, updatable=false),
	        @JoinColumn(name="shipment_id", referencedColumnName="shipment_id",
	        	insertable=false, updatable=false),
	    })
	    private SelfJoinFails pair;
	}
	
	@Entity
	private static class Shipment
	{
		@Id
		public Integer id;
		
		@Column(name="shipment_site_id")
		public Integer shipmentSiteId;
	}

    public void testFailing()
    {
    	AnnotationConfiguration cfg = new AnnotationConfiguration();
    	cfg.addAnnotatedClass(SelfJoinFails.class);
    	cfg.addAnnotatedClass(Shipment.class);
    	cfg.setProperty("hibernate.dialect", MySQLDialect.class.getName());
    	cfg.buildSessionFactory().close();
    }
    
    @Entity
	private static class SelfJoinSucceedsWithDifferentColumnOrder
	{
		@Id
	    public Integer id;
		
		@Column(name="move_site_id")
		public Integer moveSiteId;
	    
	    @ManyToOne(optional=true)
	    @JoinColumn(name="shipment_id", referencedColumnName="id")
	    private Shipment shipment;
	
	    /**
	     * Private copy for querying the matching pair of this movement.
	     * Used by {@link RequestDao#listRequests} and 
	     * {@link RequestDao.RequestFilter#requestLineStateOnShipmentDestination}.
	     * 
	     * <p>TODO check the pairing conditions, not all are catered for?
	     */
	    @OneToOne(fetch=FetchType.LAZY)
	    @JoinColumns({
	        @JoinColumn(name="shipment_id", referencedColumnName="shipment_id",
	        	insertable=false, updatable=false),
	        @JoinColumn(name="move_site_id", referencedColumnName="move_site_id",
	        	insertable=false, updatable=false),
	    })
	    private SelfJoinSucceedsWithDifferentColumnOrder pair;
	}

	/**
	 * Changing the column order in the join is enough to make the test pass.
     */
    public void testWorkaround()
    {
    	AnnotationConfiguration cfg = new AnnotationConfiguration();
    	cfg.addAnnotatedClass(SelfJoinSucceedsWithDifferentColumnOrder.class);
    	cfg.addAnnotatedClass(Shipment.class);
    	cfg.setProperty("hibernate.dialect", MySQLDialect.class.getName());
    	cfg.buildSessionFactory().close();
    }
}
