package your.package.name.here;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;
import java.util.Objects;

import static java.sql.Types.NVARCHAR;

/**
 * Hibernate UserType for nvarchar columns used in history tables <br><br>
 *
 * This class is used as a workaround for bug: <a href="https://hibernate.atlassian.net/browse/HHH-17886">HHH-17886</a> <br>
 * Which is manifested for history tables which contain nvarchar columns. <br><br>
 *
 * To use this class, add the following annotation to a String field from an entity: <br>
 * <pre>    @Type(NvarcharUserType.class)</pre>
 *
 * @author Alexandru Severin
 */
public class NvarcharUserType implements UserType<String> {

    @Override
    public int getSqlType() {
        return NVARCHAR;
    }

    @Override
    public Class<String> returnedClass() {
        return String.class;
    }

    @Override
    public boolean equals(String x, String y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(String x) {
        return x.hashCode();
    }

    @Override
    public String nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        return rs.getString(position);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, String value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, NVARCHAR);
        }
        else {
            st.setNString(index, value);
        }
    }

    @Override
    public String deepCopy(String value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(String value) {
        return deepCopy(value);
    }

    @Override
    public String assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy((String) cached);
    }

}