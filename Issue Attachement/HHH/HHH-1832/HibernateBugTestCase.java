import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

import org.dom4j.Node;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.InExpression;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.CascadeStyle;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.engine.TypedValue;
import org.hibernate.type.AbstractComponentType;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.Type;
import org.junit.Test;

public class HibernateBugTestCase {

	@Test
	public void testInExpression() {
		ValuePair pair1 = new ValuePair("pair1_A", "pair1_B");
		ValuePair pair2 = new ValuePair("pair2_A", "pair2_B");
		ValuePair pair3 = new ValuePair("pair3_A", "pair3_B");

		InExpression in = (InExpression) Restrictions.in("property", Arrays.asList(pair1, pair2,
				pair3));

		TypedValue[] values = in.getTypedValues(null, new MockCriteriaQuery());
		assertEquals(6, values.length);
		assertEquals("pair1_A", values[0].getValue());
		assertEquals("pair1_B", values[1].getValue());
		assertEquals("pair2_A", values[2].getValue());
		assertEquals("pair2_B", values[3].getValue());
		assertEquals("pair3_A", values[4].getValue());
		assertEquals("pair3_B", values[5].getValue());
	}

	class ValuePair {
		private String value1;
		private String value2;

		public ValuePair(String value1, String value2) {
			this.value1 = value1;
			this.value2 = value2;
		}

		public String getValue1() {
			return value1;
		}

		public String getValue2() {
			return value2;
		}
	}

	class MockCriteriaQuery implements CriteriaQuery {

		public String generateSQLAlias() {
			return null;
		}

		public String getColumn(Criteria criteria, String propertyPath) throws HibernateException {
			return null;
		}

		public String[] getColumnsUsingProjection(Criteria criteria, String propertyPath)
				throws HibernateException {
			return null;
		}

		public String getEntityName(Criteria criteria) {
			return null;
		}

		public String getEntityName(Criteria criteria, String propertyPath) {
			return null;
		}

		public SessionFactoryImplementor getFactory() {
			return null;
		}

		public String[] getIdentifierColumns(Criteria subcriteria) {
			return null;
		}

		public Type getIdentifierType(Criteria subcriteria) {
			return null;
		}

		public String getPropertyName(String propertyName) {
			return null;
		}

		public String getSQLAlias(Criteria subcriteria) {
			return null;
		}

		public String getSQLAlias(Criteria criteria, String propertyPath) {
			return null;
		}

		public Type getType(Criteria criteria, String propertyPath) throws HibernateException {
			return null;
		}

		public Type getTypeUsingProjection(Criteria criteria, String propertyPath)
				throws HibernateException {
			return new MockComponentType();
		}

		public TypedValue getTypedIdentifierValue(Criteria subcriteria, Object value) {
			return null;
		}

		public TypedValue getTypedValue(Criteria criteria, String propertyPath, Object value)
				throws HibernateException {
			return null;
		}
	}

	class MockComponentType implements AbstractComponentType {

		public CascadeStyle getCascadeStyle(int i) {
			return null;
		}

		public FetchMode getFetchMode(int i) {
			return null;
		}

		public String[] getPropertyNames() {
			return null;
		}

		public boolean[] getPropertyNullability() {
			return null;
		}

		public Object getPropertyValue(Object component, int i, SessionImplementor session)
				throws HibernateException {
			return null;
		}

		public Object[] getPropertyValues(Object component, SessionImplementor session)
				throws HibernateException {
			return null;
		}

		public Object[] getPropertyValues(Object component, EntityMode entityMode)
				throws HibernateException {
			// component coming in, returning values
			ValuePair valuePair = (ValuePair) component;
			return new String[] { valuePair.getValue1(), valuePair.getValue2() };
		}

		public Type[] getSubtypes() {
			return new Type[] { Hibernate.STRING, Hibernate.STRING };
		}

		public boolean isEmbedded() {
			return false;
		}

		public boolean isMethodOf(Method method) {
			return false;
		}

		public void setPropertyValues(Object component, Object[] values, EntityMode entityMode)
				throws HibernateException {}

		public Object assemble(Serializable cached, SessionImplementor session, Object owner)
				throws HibernateException {
			return null;
		}

		public void beforeAssemble(Serializable cached, SessionImplementor session) {}

		public int compare(Object x, Object y, EntityMode entityMode) {
			return 0;
		}

		public Object deepCopy(Object value, EntityMode entityMode,
				SessionFactoryImplementor factory) throws HibernateException {
			return null;
		}

		public Serializable disassemble(Object value, SessionImplementor session, Object owner)
				throws HibernateException {
			return null;
		}

		public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
			return null;
		}

		public int getColumnSpan(Mapping mapping) throws MappingException {
			return 0;
		}

		public int getHashCode(Object x, EntityMode entityMode) throws HibernateException {
			return 0;
		}

		public int getHashCode(Object x, EntityMode entityMode, SessionFactoryImplementor factory)
				throws HibernateException {
			return 0;
		}

		public String getName() {
			return null;
		}

		public Class getReturnedClass() {
			return null;
		}

		public Type getSemiResolvedType(SessionFactoryImplementor factory) {
			return null;
		}

		public Object hydrate(ResultSet rs, String[] names, SessionImplementor session, Object owner)
				throws HibernateException, SQLException {
			return null;
		}

		public boolean isAnyType() {
			return false;
		}

		public boolean isAssociationType() {
			return false;
		}

		public boolean isCollectionType() {
			return false;
		}

		public boolean isComponentType() {
			return true; // we want component type
		}

		public boolean isDirty(Object old, Object current, SessionImplementor session)
				throws HibernateException {
			return false;
		}

		public boolean isDirty(Object old, Object current, boolean[] checkable,
				SessionImplementor session) throws HibernateException {
			return false;
		}

		public boolean isEntityType() {
			return false;
		}

		public boolean isEqual(Object x, Object y, EntityMode entityMode) throws HibernateException {
			return false;
		}

		public boolean isEqual(Object x, Object y, EntityMode entityMode,
				SessionFactoryImplementor factory) throws HibernateException {
			return false;
		}

		public boolean isModified(Object oldHydratedState, Object currentState,
				boolean[] checkable, SessionImplementor session) throws HibernateException {
			return false;
		}

		public boolean isMutable() {
			return false;
		}

		public boolean isSame(Object x, Object y, EntityMode entityMode) throws HibernateException {
			return false;
		}

		public boolean isXMLElement() {
			return false;
		}

		public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session,
				Object owner) throws HibernateException, SQLException {
			return null;
		}

		public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session,
				Object owner) throws HibernateException, SQLException {
			return null;
		}

		public void nullSafeSet(PreparedStatement st, Object value, int index,
				SessionImplementor session) throws HibernateException, SQLException {}

		public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable,
				SessionImplementor session) throws HibernateException, SQLException {}

		public Object replace(Object original, Object target, SessionImplementor session,
				Object owner, Map copyCache) throws HibernateException {
			return null;
		}

		public Object replace(Object original, Object target, SessionImplementor session,
				Object owner, Map copyCache, ForeignKeyDirection foreignKeyDirection)
				throws HibernateException {
			return null;
		}

		public Object resolve(Object value, SessionImplementor session, Object owner)
				throws HibernateException {
			return null;
		}

		public Object semiResolve(Object value, SessionImplementor session, Object owner)
				throws HibernateException {
			return null;
		}

		public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory)
				throws HibernateException {}

		public int[] sqlTypes(Mapping mapping) throws MappingException {
			return null;
		}

		public boolean[] toColumnNullness(Object value, Mapping mapping) {
			return null;
		}

		public String toLoggableString(Object value, SessionFactoryImplementor factory)
				throws HibernateException {
			return null;
		}

	}
}
