import java.util.List;
import java.util.Map;

import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.persistence.QueryParser;

/**
 * Custom extension of EntityQuery
 *
 * @author Nikolai Gagov (13.01.2009)
 *
 * <br><b>History:</b> <br>
 * 13.01.2009 Nikolai Gagov created <br>
 * @param <E>
 */
public class CustomEntityQuery<E> extends EntityQuery<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected javax.persistence.Query createQuery()
	   {
	      parseEjbql();
	      
	      evaluateAllParameters();
	      
	      joinTransaction();
	      
	      javax.persistence.Query query = getEntityManager().createQuery( getRenderedEjbql() );
	      setParameters( query, getQueryParameterValues(), 0 );
	      setParameters( query, getRestrictionParameterValues(), getQueryParameterValues().size() );
	      if ( getFirstResult()!=null) query.setFirstResult( getFirstResult() );
	      if ( getMaxResults()!=null) query.setMaxResults( getMaxResults()+1 ); //add one, so we can tell if there is another page
	      if ( getHints()!=null ) {
	         for ( Map.Entry<String, String> me: getHints().entrySet() ) {
	        	 //hibernate expects Boolean value -> avoid ClassCastException
	        	 if ("org.hibernate.cacheable".equalsIgnoreCase(me.getKey())) {
	        		 query.setHint(me.getKey(), Boolean.valueOf(me.getValue()));
	        	 } else {
	        		 query.setHint(me.getKey(), me.getValue());
	        	 }
	         }
	      }
	      return query;
	   }
	   
	   private void setParameters(javax.persistence.Query query, List<Object> parameters, int start)
	   {
	      for (int i=0; i<parameters.size(); i++)
	      {
	         Object parameterValue = parameters.get(i);
	         if ( isRestrictionParameterSet(parameterValue) )
	         {
	            query.setParameter( QueryParser.getParameterName(start + i), parameterValue );
	         }
	      }
	   }
}
