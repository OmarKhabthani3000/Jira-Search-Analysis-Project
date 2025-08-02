package foo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.Test;

public class CriteriaApiTest {

    @Test
    public void embeddableInPath() {

        EntityManagerFactory entityManagerFactory = Persistence
                .createEntityManagerFactory( "TestJPA" );
        EntityManager em = entityManagerFactory.createEntityManager();

        Client client = new Client();
        client.id = 111;
        client.name = new Name();
        client.name.firstName = "foo";
        client.name.lastName = "bar";

        em.persist( client );
        
        // This passes.
        buildJPQLQuery( em ).getResultList();

        // This fails!
        buildCriteriaQuery( em ).getResultList();

        em.close();
        entityManagerFactory.close();
    }

    private TypedQuery< Client > buildCriteriaQuery( EntityManager em ) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery< Client > cq = cb.createQuery( Client.class );
        Root< Client > root = cq.from( Client.class );

        cq.where( cb.equal( root.join( "name" ).get( "firstName" ), "foo" ) );
        return em.createQuery( cq );
    }

    private TypedQuery< Client > buildJPQLQuery( EntityManager em ) {

        TypedQuery< Client > q = em
                .createQuery(
                        "SELECT c FROM Client c JOIN c.name n WHERE n.firstName = 'foo'",
                        Client.class );

        return q;
    }
}