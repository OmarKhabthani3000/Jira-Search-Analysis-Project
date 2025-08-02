import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.sql.JoinType;

import com.renxo.cms.util.CollectionFactory;
import com.renxo.cms.util.JvmSafeHashCodeBuilder;

/**
 * Handles detached criterias with duplicate association paths. Useful when
 * building criterias dynamically from user input.
 *
 */
public class DuplicateAssociationPathDetachedCriteria extends DetachedCriteria {

	private static final long serialVersionUID = -379435566974589894L;

	// ----------------------------------------------------------------------
	// Fields
	// ----------------------------------------------------------------------

	private final Map<Key, DetachedCriteria> associationCache = CollectionFactory
			.newMap();

	private final JoinType defaultJoinType;

	// ----------------------------------------------------------------------
	// Constructors
	// ----------------------------------------------------------------------

	private DuplicateAssociationPathDetachedCriteria(String entityName) {

		super(entityName);
		this.defaultJoinType = JoinType.INNER_JOIN;
	}

	private DuplicateAssociationPathDetachedCriteria(String entityName,
			JoinType defaultJoinType) {

		super(entityName);
		this.defaultJoinType = defaultJoinType;
	}

	private DuplicateAssociationPathDetachedCriteria(String entityName,
			String alias) {

		super(entityName, alias);
		this.defaultJoinType = JoinType.INNER_JOIN;
	}

	private DuplicateAssociationPathDetachedCriteria(String entityName,
			String alias, JoinType defaultJoinType) {

		super(entityName, alias);
		this.defaultJoinType = defaultJoinType;
	}

	private DuplicateAssociationPathDetachedCriteria(CriteriaImpl impl,
			Criteria criteria) {

		super(impl, criteria);
		this.defaultJoinType = JoinType.INNER_JOIN;
	}

	private DuplicateAssociationPathDetachedCriteria(CriteriaImpl impl,
			Criteria criteria, JoinType defaultJoinType) {

		super(impl, criteria);
		this.defaultJoinType = defaultJoinType;
	}

	// ----------------------------------------------------------------------
	// Factory methods
	// ----------------------------------------------------------------------

	/**
	 * Returns a detached criteria for the given entity name.
	 * 
	 * @param entityName
	 *            the entity name.
	 * @return a detached criteria.
	 */
	public static DetachedCriteria forEntityName(String entityName) {
		return new DuplicateAssociationPathDetachedCriteria(entityName);
	}

	/**
	 * Returns a detached criteria for the given entity name.
	 * 
	 * @param entityName
	 *            the entity name.
	 * @param defaultJoinType
	 *            the default join type.
	 * @return a detached criteria.
	 */
	public static DetachedCriteria forEntityName(String entityName,
			JoinType defaultJoinType) {

		return new DuplicateAssociationPathDetachedCriteria(entityName,
				defaultJoinType);
	}

	/**
	 * Returns a detached criteria for the given entity name and alias.
	 * 
	 * @param entityName
	 *            the entity name.
	 * @param alias
	 *            the alias.
	 * @return a detached criteria.
	 */
	public static DetachedCriteria forEntityName(String entityName, String alias) {
		return new DuplicateAssociationPathDetachedCriteria(entityName, alias);
	}

	/**
	 * Returns a detached criteria for the given entity name and alias.
	 * 
	 * @param entityName
	 *            the entity name.
	 * @param alias
	 *            the alias.
	 * @param defaultJoinType
	 *            the default join type.
	 * @return a detached criteria.
	 */
	public static DetachedCriteria forEntityName(String entityName,
			String alias, JoinType defaultJoinType) {

		return new DuplicateAssociationPathDetachedCriteria(entityName, alias,
				defaultJoinType);
	}

	/**
	 * Returns a detached criteria for the given class.
	 * 
	 * @param clazz
	 *            the class.
	 * @return a detached criteria.
	 */
	@SuppressWarnings("unchecked")
	public static DetachedCriteria forClass(Class clazz) {
		return new DuplicateAssociationPathDetachedCriteria(clazz.getName());
	}

	/**
	 * Returns a detached criteria for the given class.
	 * 
	 * @param clazz
	 *            the class.
	 * @param defaultJoinType
	 *            the default join type.
	 * @return a detached criteria.
	 */
	@SuppressWarnings("unchecked")
	public static DetachedCriteria forClass(Class clazz,
			JoinType defaultJoinType) {

		return new DuplicateAssociationPathDetachedCriteria(clazz.getName(),
				defaultJoinType);
	}

	/**
	 * Returns a detached criteria for the given class and alias.
	 * 
	 * @param clazz
	 *            the class.
	 * @param alias
	 *            the alias.
	 * @return a detached criteria.
	 */
	@SuppressWarnings("unchecked")
	public static DetachedCriteria forClass(Class clazz, String alias) {
		return new DuplicateAssociationPathDetachedCriteria(clazz.getName(),
				alias);
	}

	/**
	 * Returns a detached criteria for the given class and alias.
	 * 
	 * @param clazz
	 *            the class.
	 * @param alias
	 *            the alias.
	 * @param defaultJoinType
	 *            the default join type.
	 * @return a detached criteria.
	 */
	@SuppressWarnings("unchecked")
	public static DetachedCriteria forClass(Class clazz, String alias,
			JoinType defaultJoinType) {

		return new DuplicateAssociationPathDetachedCriteria(clazz.getName(),
				alias, defaultJoinType);
	}

	// ----------------------------------------------------------------------
	// Overridden methods
	// ----------------------------------------------------------------------

	@Override
	public DetachedCriteria createCriteria(String associationPath, String alias) {

		Key key = new Key(associationPath, alias, defaultJoinType, null);
		if (associationCache.containsKey(key)) {
			return associationCache.get(key);
		}

		DuplicateAssociationPathDetachedCriteria criteria = new DuplicateAssociationPathDetachedCriteria(
				getImpl(), getCriteria().createCriteria(associationPath, alias));
		associationCache.put(key, criteria);
		return criteria;
	}

	@Override
	public DetachedCriteria createCriteria(String associationPath) {

		Key key = new Key(associationPath, null, defaultJoinType, null);
		if (associationCache.containsKey(key)) {
			return associationCache.get(key);
		}

		DuplicateAssociationPathDetachedCriteria criteria = new DuplicateAssociationPathDetachedCriteria(
				getImpl(), getCriteria().createCriteria(associationPath));
		associationCache.put(key, criteria);
		return criteria;
	}

	@Override
	public DetachedCriteria createCriteria(String associationPath,
			JoinType joinType) {

		Key key = new Key(associationPath, null, joinType, null);
		if (associationCache.containsKey(key)) {
			return associationCache.get(key);
		}

		DuplicateAssociationPathDetachedCriteria criteria = new DuplicateAssociationPathDetachedCriteria(
				getImpl(), getCriteria().createCriteria(associationPath,
						joinType));
		associationCache.put(key, criteria);
		return criteria;
	}

	@Override
	public DetachedCriteria createCriteria(String associationPath,
			String alias, JoinType joinType) {

		Key key = new Key(associationPath, alias, joinType, null);
		if (associationCache.containsKey(key)) {
			return associationCache.get(key);
		}

		DuplicateAssociationPathDetachedCriteria criteria = new DuplicateAssociationPathDetachedCriteria(
				getImpl(), getCriteria().createCriteria(associationPath, alias,
						joinType));
		associationCache.put(key, criteria);
		return criteria;
	}

	@Override
	public DetachedCriteria createCriteria(String associationPath,
			String alias, JoinType joinType, Criterion withClause) {

		Key key = new Key(associationPath, alias, joinType, withClause);
		if (associationCache.containsKey(key)) {
			return associationCache.get(key);
		}

		DuplicateAssociationPathDetachedCriteria criteria = new DuplicateAssociationPathDetachedCriteria(
				getImpl(), getCriteria().createCriteria(associationPath, alias,
						joinType, withClause));
		associationCache.put(key, criteria);
		return criteria;
	}

	// ----------------------------------------------------------------------
	// Private reflection methods
	// ----------------------------------------------------------------------

	private CriteriaImpl getImpl() {
		try {
			Field field = DetachedCriteria.class.getDeclaredField("impl");
			field.setAccessible(true);
			return (CriteriaImpl) field.get(this);
		} catch (Exception e) {
			// Should never happen
			throw new RuntimeException(e);
		}
	}

	private Criteria getCriteria() {
		try {
			Field field = DetachedCriteria.class.getDeclaredField("criteria");
			field.setAccessible(true);
			return (Criteria) field.get(this);
		} catch (Exception e) {
			// Should never happen
			throw new RuntimeException(e);
		}
	}

	// ----------------------------------------------------------------------
	// Inner key class
	// ----------------------------------------------------------------------

	private static final class Key implements Serializable {

		private static final long serialVersionUID = -4091478555983256299L;

		// ----------------------------------------------------------------------
		// Fields
		// ----------------------------------------------------------------------

		private final String associationPath;

		private final String alias;

		private final JoinType joinType;

		private final Criterion withClause;

		// ----------------------------------------------------------------------
		// Constructor
		// ----------------------------------------------------------------------

		private Key(String associationPath, String alias, JoinType joinType,
				Criterion withClause) {

			this.associationPath = associationPath;
			this.alias = alias;
			this.joinType = joinType;
			this.withClause = withClause;
		}

		// ----------------------------------------------------------------------
		// Identity methods
		// ----------------------------------------------------------------------

		@Override
		public boolean equals(Object other) {
			if (other == this) {
				return true;
			} else if (!(other instanceof Key)) {
				return false;
			} else {
				Key rhs = (Key) other;
				EqualsBuilder eb = new EqualsBuilder();
				eb.append(this.associationPath, rhs.associationPath);
				eb.append(this.alias, rhs.alias);
				eb.append(this.joinType, rhs.joinType);
				eb.append(this.withClause, rhs.withClause);
				return eb.isEquals();
			}
		}

		@Override
		public int hashCode() {
			HashCodeBuilder hcb = new JvmSafeHashCodeBuilder();
			hcb.append(this.associationPath);
			hcb.append(this.alias);
			hcb.append(this.joinType);
			hcb.append(this.withClause);
			return hcb.toHashCode();
		}
	}
}
