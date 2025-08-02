import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.type.NullableType;
import org.hibernate.type.TypeFactory;
import org.hibernate.usertype.ParameterizedType;

/**
 * Implements a generic enum user type identified by a single identifier / column.
 * <p><ul>
 *    <li>The enum type being represented by the certain user type must be set
 *        by using the 'enumClass' property.</li>
 *    <li>The identifier representing a enum value is retrieved by the identifierMethod.
 *        The name of the identifier method can be specified by the 
 *        'identifierMethod' property and its default is the name() method ('name').</li>
 *    <li>The identifier type is automatically determined by 
 *        the type of the identifierMethod.</li>
 *    <li>The valueOfMethod is the name of the static factory method returning
 *        a enumeration object represented by a given indentifier. The valueOfMethod's
 *        name can be specified by setting the 'valueOfMethod' property. The
 *        default valueOfMethod's name is 'valueOf'.</li>
 * </p> 
 * <p>
 * Example of a enum type represented by an int value:
 * <code>
 * public enum SimpleNumber {
 *   Unknown(-1), Zero(0), One(1), Two(2), Three(3);
 * 
 *   public int toInt() { return value; }
 *   public SimpleNumber fromInt(int value) {
 * 		switch(value) {
 *   	   case 0: return Zero;
 *         case 1: return One;
 *         case 2: return Two;
 *         case 3: return Three;
 *         default:
 *         		return Unknown;
 *     }
 *   }
 * }
 * </code>
 * <p>
 * The Mapping would look like this:
 * <code>
 *    <typedef name="SimpleNumber" class="GenericEnumUserType">
 *		<param name="enumClass">SimpleNumber</param>
 *		<param name="identifierMethod">toInt</param>
 *		<param name="valueOfMethod">fromInt</param>
 *    </typedef>
 *    <class ...>
 *      ...
 *     <property name="number" column="number" type="SimpleNumber"/>
 *    </class>
 * </code>
 * @author Martin Kersten
 * @since 05.05.2005
 */
public class GenericEnumUserType extends AbstractUserType implements ParameterizedType  {
	private Class<? extends Enum> enumClass;
	private Class<?> identifierType;

	private Method identifierMethod;
	private Method valueOfMethod;
	
	private static final String defaultIdentifierMethodName = "name";
	private static final String defaultValueOfMethodName = "valueOf";
	
	private NullableType type;
	private int [] sqlTypes;
	
    public void setParameterValues(Properties parameters) {
        String enumClassName = parameters.getProperty("enumClass");
        try {
            enumClass = Class.forName(enumClassName).asSubclass(Enum.class);
        }
        catch (ClassNotFoundException exception) {
            throw new HibernateException("Enum class not found", exception);
        }
        
		String identifierMethodName = 
			parameters.getProperty("identifierMethod", defaultIdentifierMethodName);

		try {
			identifierMethod = enumClass.getMethod(identifierMethodName, new Class[0]);
			identifierType = identifierMethod.getReturnType();
		}
		catch(Exception exception) {
			throw new HibernateException("Failed to optain identifierMethod", exception);
		}
		
		type = (NullableType)TypeFactory.basic(identifierType.getName());
		
		if(type == null)
			throw new HibernateException("Unsupported identifier type "+identifierType.getName());
		
		sqlTypes = new int [] {type.sqlType()};

		String valueOfMethodName = 
			parameters.getProperty("valueOfMethod", defaultValueOfMethodName);
		
		try {
			valueOfMethod = enumClass.getMethod(
					valueOfMethodName, new Class[] { identifierType });
		} 
		catch(Exception exception) {
			throw new HibernateException("Failed to optain valueOfMethod", exception);
		}
    }
	
	public Class returnedClass() {
		return enumClass;
	}
	
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner) 
						throws HibernateException, SQLException {
		Object identifier=type.get(rs, names[0]);
		try {
			return valueOfMethod.invoke(null, new Object [] {identifier});
		}
		catch(Exception exception) {
			throw new HibernateException(
					"Exception invocing valueOfMethod of enumeration class: ", exception);
		} 
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index) 
			throws HibernateException, SQLException {
		try {
			Object identifier = value != null ? identifierMethod.invoke(value, new Object[0]) : null;
			st.setObject(index, identifier);
		}
		catch(Exception exception) {
			throw new HibernateException(
					"Exception invocing identifierMethod of enumeration class: ", exception);
		} 
	}
	public int[] sqlTypes() {
		//TODO Change this as soon the issue with CustomType and sqlTypes/setParameters is solved
		//return sqlTypes;
		return new int [] {Types.INTEGER};
	}
}