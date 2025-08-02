import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.AggregateProjection;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.type.Type;

/**
 * 
 * @author jwinteregg
 */
public class SqlFunctionProjection extends AggregateProjection {

    
    protected final String propertyName;
    private final String aggregate;
    private List<String> functionParams;
    
    public SqlFunctionProjection(String aggregate, String propertyName, List<String> functionParams) {
        super(aggregate, propertyName);
        this.aggregate = aggregate;
        this.propertyName = propertyName;
        this.functionParams = functionParams;
    }
    
    public SqlFunctionProjection(String aggregate, String propertyName, String functionParam) {
        super(aggregate, propertyName);
        this.aggregate = aggregate;
        this.propertyName = propertyName;
        this.functionParams = new ArrayList<String>();
        this.functionParams.add(functionParam);
    }

    public String toString() {
        return aggregate + "(" + propertyName + ')';
    }

    public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return new Type[] { criteriaQuery.getType(criteria, propertyName) };
    }

    public String toSqlString(Criteria criteria, int loc, CriteriaQuery criteriaQuery) throws HibernateException {
        SQLFunction function = null;
        if((function = criteriaQuery.getFactory().getSqlFunctionRegistry().findSQLFunction(aggregate)) == null)
                throw new HibernateException("Function "+aggregate+" not found in Dialect");
        
        StringBuffer output = new StringBuffer()
        .append(aggregate)
        .append("(");
        if(function.hasArguments() && functionParams != null && functionParams.size() != 0){
            for(String param : functionParams){
                output.append("'"+param+"', ");
            }
        }else{
            throw new HibernateException("function "+aggregate+" needs parameter(s)");
        }
        output.append(criteriaQuery.getColumn(criteria, propertyName))
        .append(") as y")
        .append(loc)
        .append('_');
        return output.toString();
    }

}
