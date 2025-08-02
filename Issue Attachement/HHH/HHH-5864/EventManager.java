package examples.hibernate;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.hibernate.Session;

public class EventManager {

    public static void main(String args[]) {
        EventManager mgr = new EventManager();

        mgr.createAndStorePersonEvents(new Long(11));

        /*mgr.createAndStoreEvent("My Event", new Date());*/
        List result = mgr.listEvents();
        examples.hibernate.Event event = null;
        for (int i = 0; i < result.size(); i++) {
            event = (examples.hibernate.Event) result.get(i);
            System.out.println(event.toString());
        }
        /*Person person = new Person();
        person.setAge(6);
        person.setFirstName("Kundan");
        person.setLastName("Jain");
        mgr.createAndStorePerson(person);
        HibernateUtil.getSessionFactory().close();*/
    }

    private void createAndStoreEvent(String title, Date eventDate) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Event event = new Event();
        event.setTitle(title);
        event.setDate(eventDate);
        Person person = (Person) session.load(Person.class, new Long(11));
        Set persons = new HashSet();
        persons.add(person);
        event.setPersons(persons);
        session.save(event);
        session.getTransaction().commit();
    }

    private List listEvents() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List result = session.createQuery("from Event").list();
        session.getTransaction().commit();
        return result;
    }

    private void createAndStorePerson(Person person) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.save(person);
        System.out.println("Person ID \t" + person.getId());
        Event event = (Event) session.load(Event.class, new Long(1));
        Set events = new HashSet();
        events.add(event);
        person.setEvents(events);
        session.getTransaction().commit();
    }

    private void createAndStorePersonEvents(Long personId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Person person = (Person) session.load(Person.class, personId);
        Set events = person.getEvents();
        Set eventsRemoved = new HashSet();
        Iterator itr = events.iterator();
        Event event = null;
        
        while (itr.hasNext()) {
            event = (Event) itr.next();
            if (event.getId().longValue() % 2 == 0) {
                eventsRemoved.add(event);
            }
        }
        events.removeAll(eventsRemoved);
        session.save(person);
        session.getTransaction().commit();
    }

    private void addPersonToEvent(){

    }
}
