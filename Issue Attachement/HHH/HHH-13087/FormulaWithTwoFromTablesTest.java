package org.hibernate.bugs;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.function.SQLFunctionRegistry;
import org.hibernate.mapping.Formula;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;

public class FormulaWithTwoFromTablesTest extends BaseUnitTestCase {
    
    private static final String FORMULA_STRING = "(SELECT greater(A.foo, B.foo) FROM A, B WHERE A.id = id AND B.id = id)";
    private static final String EXPECTED_TEMPLATE = "(SELECT greater(A.foo, B.foo) FROM A, B WHERE A.id = $PlaceHolder$.id AND B.id = $PlaceHolder$.id)";
    
    private static final Dialect DIALECT = new HSQLDialect();
    private static final SQLFunctionRegistry FUNCTION_REGISTRY = new SQLFunctionRegistry( DIALECT, Collections.EMPTY_MAP );

    @Test
    public void testFormulaWithTwoFromTablesTemplate()
    {
        Formula formula = new Formula(FORMULA_STRING);
        String template = formula.getTemplate(DIALECT, FUNCTION_REGISTRY);
        assertEquals(EXPECTED_TEMPLATE, template);
    }
}
