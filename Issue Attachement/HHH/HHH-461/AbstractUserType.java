import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * Base implementation stub for single column user types.
 *
 * @author Martin Kersten
 * @since 05.05.2005
 */
public abstract class AbstractUserType implements UserType {

	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable)value;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		return x==y;
	}

	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	public boolean isMutable() {
		return false;
	}

	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}
}