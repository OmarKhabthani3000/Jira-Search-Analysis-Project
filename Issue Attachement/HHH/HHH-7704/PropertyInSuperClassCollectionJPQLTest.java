package se.databyran.prosang.model.donor.search;

import com.markatta.jee5unit.framework.EntityTestCase;
import com.markatta.jee5unit.runners.TxRollbackRunner;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

/**
 *
 * @author mats
 */
@RunWith(TxRollbackRunner.class)
public class PropertyInSuperClassCollectionJPQLTest extends EntityTestCase {

    private EntityManager em;

    @Before
    public void setup() {
        em = getEntityManager();
    }

    @After
    public void tearDown() {
        Logger.getLogger("org.hibernate.SQL").setLevel(Level.OFF);
    }

    @Test
    public void problemWhenPropertyIsInSuperClass() {
        Logger.getLogger("org.hibernate.SQL").setLevel(Level.TRACE);
        Cat cat = new Cat();
        CatPlace place = new CatPlace();
        cat.setPlace(place);
        place.getCats().add(cat);
        cat.setAlive(true);
        em.persist(cat);
        em.flush();
        em.clear();

        Query query = em.createQuery("select p from " + CatPlace.class.getName() + " p where exists(select c from p.cats c where c.alive=:true)");
        query.setParameter("true", true);
        assertEquals(1, query.getResultList().size());
    }

    @Test
    public void noProbsWhenPropertyIsInSameClass() {
        Cat cat = new Cat();
        CatPlace place = new CatPlace();
        cat.setPlace(place);
        place.getCats().add(cat);
        cat.setTail(true);
        em.persist(cat);
        em.flush();
        em.clear();

        Query query = em.createQuery("select p from " + CatPlace.class.getName() + " p where exists(select c from p.cats c where c.tail=:true)");
        query.setParameter("true", true);

        assertEquals(1, query.getResultList().size());
    }

    @Entity
    @Table(name = "CAT_PLACE")
    public static class CatPlace {

        @Id
        @Column(name = "ID")
        @GeneratedValue(strategy = GenerationType.AUTO)
        private long id;

        @OneToMany(mappedBy = "place", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        private List<Cat> cats = new ArrayList();

        public List<Cat> getCats() {
            return cats;
        }

        public void setCats(List<Cat> cats) {
            this.cats = cats;
        }
        
    }

    @Entity
    @Table(name = "ANIMAL")
    @DiscriminatorColumn
    @Inheritance(strategy = InheritanceType.JOINED)
    public static abstract class Animal implements Serializable {

        public final static long serialVersionUID = 1L;

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private long id;

        private boolean alive;

        @Column(name="ALIVE")
        public boolean isAlive() {
            return alive;
        }

        public void setAlive(boolean alive) {
            this.alive = alive;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    @Entity
    @Table(name = "CAT")
    @DiscriminatorValue(value = "CAT")
    public static class Cat extends Animal {

        private boolean tail;

        @ManyToOne(cascade = CascadeType.PERSIST)
        private CatPlace place;

        public CatPlace getPlace() {
            return place;
        }

        public void setPlace(CatPlace place) {
            this.place = place;
        }

        public boolean isTail() {
            return tail;
        }

        public void setTail(boolean tail) {
            this.tail = tail;
        }
    }
}
