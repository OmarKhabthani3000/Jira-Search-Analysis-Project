package org.hibernate.sample;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestCase {
    private SessionFactory sessionFactory;

    @Before
    public void setUp() throws Exception {
        sessionFactory = new Configuration().configure().buildSessionFactory();

        Calendar now = Calendar.getInstance();

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE));

        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();

        calendar.add(Calendar.DATE, 2);
        Date tomorrow = calendar.getTime();

        Event event1 = new Event();
        event1.setDate(yesterday);

        Event event2 = new Event();
        event2.setDate(tomorrow);

        EventGroup eventGroup = new EventGroup();
        eventGroup.getEvents().add(event1);
        eventGroup.getEvents().add(event2);

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(eventGroup);
        session.save(event1);
        session.save(event2);
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
        List<JoinType> joinTypes = new ArrayList<>(Arrays.asList(JoinType.values()));
        joinTypes.remove(JoinType.NONE);

        for (JoinType joinType : joinTypes) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            Criteria criteria = session.createCriteria(EventGroup.class);
            criteria.createCriteria("events", "event", joinType);
            criteria.add(Restrictions.gt("event.date", new Date()));

            List result = criteria.list();
            assertEquals(1, result.size());

            EventGroup eventGroup = (EventGroup) result.get(0);
            assertEquals(2, eventGroup.getEvents().size());

            session.getTransaction().commit();
            session.close();
        }
    }
}
