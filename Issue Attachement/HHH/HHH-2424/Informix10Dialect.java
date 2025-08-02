package il.org.nite.frm.common.hibernate.dialect;

import org.hibernate.dialect.InformixDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.Hibernate;

import java.sql.Types;

public class Informix10Dialect extends InformixDialect {
    public Informix10Dialect() {
        super();

        // Map the double/float, override on subclasses
        registerColumnType(Types.DOUBLE, "float");
        registerColumnType(Types.FLOAT, "smallfloat");

        // Map second/minute/hour/day/month/year, override on subclasses
		registerFunction("second", new SQLFunctionTemplate(Hibernate.INTEGER, "second(?1)"));
		registerFunction("minute", new SQLFunctionTemplate(Hibernate.INTEGER, "minute(?1)"));
		registerFunction("hour", new SQLFunctionTemplate(Hibernate.INTEGER, "hour(?1)"));
		registerFunction("day", new SQLFunctionTemplate(Hibernate.INTEGER, "day(?1)"));
		registerFunction("month", new SQLFunctionTemplate(Hibernate.INTEGER, "month(?1)"));
		registerFunction("year", new SQLFunctionTemplate(Hibernate.INTEGER, "year(?1)"));
    }
}
