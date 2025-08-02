package bug;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static junit.framework.Assert.assertTrue;

/**
 * @author Mohd Adnan (adnan@spmsoftware.com)
 */
public class Test {

    @PersistenceContext
    private EntityManager entityManager;

    @org.junit.Test
        public void testName() throws Exception {
            Parent parent = entityManager.find(Parent.class, 122);
            assertTrue(parent != null);
        }
}
