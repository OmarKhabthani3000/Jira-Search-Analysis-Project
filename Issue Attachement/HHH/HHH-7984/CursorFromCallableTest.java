import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.Assert;
import org.junit.Test;

public class CursorFromCallableTest {

    @Test
    public void testGetObjectListFromCalleableCursor_X500() throws Exception {
        Properties emfProps = new Properties();
        emfProps.setProperty("hibernate.connection.url", "...");
        emfProps.setProperty("hibernate.connection.username", "...");
        emfProps.setProperty("hibernate.connection.password", "...");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CursorFromCallableTest", emfProps);
        EntityManager context = null;
        try {
            context = emf.createEntityManager();

            for (int i = 0; i < 500; i++) {
                Assert.assertEquals(2, getObjectListFromCalleableCursor(context).size());
            }

        } finally {
            if (context != null) {
                context.close();
            }
            emf.close();
        }
    }

    private List<NumValue> getObjectListFromCalleableCursor(EntityManager context) {
        TypedQuery<NumValue> query = context.createNamedQuery("NumValue.getSomeValues", NumValue.class);
        return query.getResultList();
    }
}
