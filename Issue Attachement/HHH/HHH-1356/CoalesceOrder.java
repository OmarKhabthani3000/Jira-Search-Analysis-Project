/*
 * CoalesceOrder.java
 */

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Order;
import org.hibernate.dialect.function.SQLFunction;

/**
 * This is an Order inplementation using coalesce function as order.
 *
 * @author Cserveny Tamas
 */
public class CoalesceOrder extends Order
{
    /**
     * List of properties in order of relevance.
     */
    private List _order;
    
    /**
     * Direction. True=  asc, False= desc
     */
    private boolean   _asc;
    
    /**
     * Creates a new instance of CoalesceOrder.
     */
    public CoalesceOrder(String[] order, boolean asc)
    {
        super( null, asc );
        _order  = Arrays.asList(order);
        _asc    = asc;
    }

    /**
     * Renders the order element.
     */
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException
    {
        SQLFunction func = (SQLFunction)criteriaQuery.
                getFactory().
                getDialect().
                getFunctions().
                get("coalesce");
        
        List xlat = new LinkedList();
        
        for ( Iterator i = _order.iterator() ; i.hasNext() ; ) {
            String s = (String)i.next();
            
            xlat.addAll( 
                    Arrays.asList( 
                    criteriaQuery.getColumnsUsingProjection( criteria, s ) ) );
        }
        
        
        return func.render( xlat, criteriaQuery.getFactory() ) + (_asc ? " asc " : " desc ");
    }
    
    public static Order asc( String[] order ) {
        return new CoalesceOrder( order , true );
    }
    
    public static Order desc( String[] order ) {
        return new CoalesceOrder( order , false );
    }
    
}
