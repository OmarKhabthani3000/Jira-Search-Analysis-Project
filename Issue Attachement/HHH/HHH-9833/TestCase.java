package org.hibernate.sample;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestCase {
    private SessionFactory sessionFactory;

    @Before
    public void setUp() throws Exception {
        sessionFactory = new Configuration().configure().buildSessionFactory();

        Event event1 = new Event(new CompositeId(1L, 1L));

        Event event2 = new Event(new CompositeId(1L, 2L));

        EventGroup eventGroup1 = new EventGroup(new CompositeId(1L, 1L));
        eventGroup1.getEvents().add(event1);
        eventGroup1.getEvents().add(event2);

        Event event3 = new Event(new CompositeId(1L, 3L));

        EventGroup eventGroup2 = new EventGroup(new CompositeId(1L, 2L));
        eventGroup2.getEvents().add(event3);

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(eventGroup1);
        session.save(eventGroup2);
        session.getTransaction().commit();
        session.close();
    }

    @After
    public void tearDown() throws Exception {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    public void test() throws Exception {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        List result = session.createCriteria(EventGroup.class).list();
        assertEquals(2, result.size());

        EventGroup eventGroup = (EventGroup) result.get(0);
        assertEquals(2, eventGroup.getEvents().size());

        eventGroup = (EventGroup) result.get(1);
        assertEquals(1, eventGroup.getEvents().size());

        session.getTransaction().commit();
        session.close();
    }
}
