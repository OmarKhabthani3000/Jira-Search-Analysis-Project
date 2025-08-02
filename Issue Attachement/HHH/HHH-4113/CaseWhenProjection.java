package books.util.hibernate;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;
import org.hibernate.engine.TypedValue;
import org.hibernate.type.Type;

/**
 * case when ... then ... else ... project.
 * 
 * Makes it possible to create more complex queries dynamically and
 * using the criteria API.
 * 
 * Note that this projection may need to be added as a projection AND a criterion, and it should be
 * added to the criterion early in the list so that the parameters line up correctly (projections
 * are emitted into the SQL before conditionals).  It will need to be added if there are any
 * constant parameters passed used in the condition, since projections cannot add any parameters
 * to the SQL query, we need to run as a criterion to add those parameters.
 *
 * A useful companion for this class is the ConstantProjection, which would allow either your
 * trueValue or falseValue to be a constant (a very typical use case for this construct). When used
 * this way, be sure to add the case when criterion and then the constant's criterion.
 */
public class CaseWhenProjection implements Projection {
	private static final long serialVersionUID = 1L;
	
	private Criterion condition;
	private Projection trueValue;
	private Projection falseValue;
	private String groupAlias;
	
	/**
	 * Main constructor.
	 * 
	 * @param condition Criterion condition to test
	 * @param trueValue Projection to use if the condition evaluates to true 
	 * @param falseValue Projection to use if the condition evaluates to false
	 * @param groupAlias (optional) If non-null, the projection is assigned an alias for grouping purposes and that alias is put into the group by clause
	 */
	public CaseWhenProjection(Criterion condition, Projection trueValue,
			Projection falseValue, String groupAlias) {
		super();
		this.condition = condition;
		this.trueValue = trueValue;
		this.falseValue = falseValue;
		this.groupAlias = groupAlias;
	}
	
	public boolean isGrouped() {
		return groupAlias!=null;
	}

	public Criterion getCondition() {
		return condition;
	}


	public void setCondition(Criterion condition) {
		this.condition = condition;
	}


	public Projection getTrueValue() {
		return trueValue;
	}


	public void setTrueValue(Projection trueValue) {
		this.trueValue = trueValue;
	}


	public Projection getFalseValue() {
		return falseValue;
	}


	public void setFalseValue(Projection falseValue) {
		this.falseValue = falseValue;
	}

	public String getGroupAlias() {
		return groupAlias;
	}

	public void setGroupAlias(String groupAlias) {
		this.groupAlias = groupAlias;
	}


	@Override
	public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery)
			throws HibernateException {
		return trueValue.getTypes(criteria, criteriaQuery);
	}

	@Override
	public Type[] getTypes(String alias, Criteria criteria,
			CriteriaQuery criteriaQuery) throws HibernateException {
		return trueValue.getTypes(alias, criteria, criteriaQuery);
	}

	@Override
	public String toSqlString(Criteria criteria, int position,
			CriteriaQuery criteriaQuery) throws HibernateException {
		StringBuffer sb = new StringBuffer();
		sb.append("(case when ");
		sb.append(condition.toSqlString(criteria, criteriaQuery));
		sb.append(" then ");
		sb.append(toSqlString(trueValue, criteria, position, criteriaQuery));
		sb.append(" else ");
		sb.append(toSqlString(falseValue, criteria, position, criteriaQuery));
		sb.append(" end)");
		if(groupAlias != null)
			sb.append(" as ").append(groupAlias);
		return sb.toString();
	}

	/**
	 * Bit of a hack here to strip out all the aliases - the projections
	 * typically add an "as {alias}" to the end but that's only valid at
	 * the top level - in a nested expression like this that is not
	 * allowed.
	 */
	public static String toSqlString(Projection p, Criteria criteria,
			int position, CriteriaQuery criteriaQuery) {
		// Have to strip out all the aliases
		String[] aliases = p.getColumnAliases(position);
		String sql = p.toSqlString(criteria, position, criteriaQuery);
		for(String alias : aliases) {
			sql = sql.replaceFirst(" as "+alias, "");
		}
		return sql;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("case when ");
		sb.append(condition.toString());
		sb.append(" then ");
		sb.append(trueValue.toString());
		sb.append(" else ");
		sb.append(falseValue.toString());
		sb.append(" end");
		if(groupAlias != null)
			sb.append(" as ").append(groupAlias);
		return sb.toString();
	}

	public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) 
	throws HibernateException {
		if (groupAlias == null) {
			throw new UnsupportedOperationException("not a grouping projection");
		}
		else {
			return groupAlias;
		}
	}
	
	public Criterion criterion() {
		return new Criterion() {
			private static final long serialVersionUID = 1L;

			@Override
			public TypedValue[] getTypedValues(Criteria criteria,
					CriteriaQuery criteriaQuery) throws HibernateException {
				return condition.getTypedValues(criteria, criteriaQuery);
			}

			@Override
			public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
					throws HibernateException {
				return "1=1";
			}
		};
	}

	@Override
	public String[] getAliases() {
		return new String[1];
	}

	@Override
	public String[] getColumnAliases(int position) {
		return new String[] { groupAlias != null ? groupAlias : "y"+position+"_" };
	}

	@Override
	public String[] getColumnAliases(String alias, int position) {
		return null;
	}
	
}
