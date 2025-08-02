
// Very simple code snippet to make criterias work.
Class objectClass = MyEntity.class;
Criteria criteria = session.createCriteria(objectClass);
// ... build criteria ...
CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
final SessionImplementor sessionImplementor = (SessionImplementor) session;
CriteriaLoader loader = new CriteriaLoader(
                (OuterJoinLoadable) sessionImplementor.getFactory().getEntityPersister(objectClass.getName()),
                sessionImplementor.getFactory(),
                criteriaImpl,
                criteriaImpl.getEntityOrClassName(),
                sessionImplementor.getEnabledFilters()
) {
    @Override
    public List list(SessionImplementor session) throws HibernateException {
        sql = "select count(*) as c_counter from (" + sql + ") as cnt";
        return super.list(session);
    }

    @Override
    protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer,
                                          ResultSet rs, SessionImplementor session)
                    throws SQLException, HibernateException {
        final Object[] result;
        result = new Object[1];
        result[0] = Hibernate.INTEGER.nullSafeGet(rs, "c_counter", sessionImplementor, null);
        return result;
    }
};
Object[] result = (Object[]) loader.list(sessionImplementor).get(0);
int count = (Integer) result[0];