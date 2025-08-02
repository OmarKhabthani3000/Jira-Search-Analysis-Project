package foo;

import static junit.framework.Assert.assertEquals;
import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Deady
 * Date: 24.06.2009
 * Time: 11:47:38
 */
public class JoinTest {

    private SessionFactory sessionFactory;
    private BasicDataSource dataSource;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public static void main(String[] args) throws SQLException {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("jointest.xml");
        JoinTest client = (JoinTest) ctx.getBean("JoinTest");
        client.test();
    }

    private void test() throws SQLException {
        String q = "select log.from_number, log.to_number, entries_from.name, entries_to.name\n" +
                "from test_log log\n" +
                "left join test_entries entries_from\n" +
                " on log.from_number=entries_from.number\n" +
                "left join test_entries entries_to\n" +
                " on log.to_number=entries_to.number";

        //fetch hibernate result
        Session s = sessionFactory.openSession();
        Query qq = s.createSQLQuery(q);
        List<Object[]> resHibernate = qq.list();


        //fetch pure jdbc result
        Connection conn = dataSource.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(q);
        List<Object[]> resJdbc = new ArrayList<Object[]>();
        while (rs.next()) {
            Object[] row = new Object[4];
            row[0] = rs.getString(1);
            row[1] = rs.getString(2);
            row[2] = rs.getString(3);
            row[3] = rs.getString(4);
            resJdbc.add(row);
        }
        rs.close();
        st.close();
        conn.close();

        //fully compare results
        compare(resJdbc, resHibernate);
    }


    private void compare(List<Object[]> l1, List<Object[]> l2) {
        for (int i = 0; i< l1.size(); i++) {
            Object[] row1 = l1.get(i);
            Object[] row2 = l2.get(i);

            for (int j = 0; j < row1.length; j++) {
                assertEquals(row1[j], row2[j]);
            }
        }
    }


    public void setDataSource(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public BasicDataSource getDataSource() {
        return dataSource;
    }
}
