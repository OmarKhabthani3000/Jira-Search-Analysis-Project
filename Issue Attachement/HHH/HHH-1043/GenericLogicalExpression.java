/**
 * 
 */
package org.wfp.rita.db;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;

public class GenericLogicalExpression extends LogicalExpression
{
    public GenericLogicalExpression(Criterion lhs, String op, Criterion rhs)
    {
        super(lhs, rhs, op);
    }
}