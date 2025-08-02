import java.io.*;
import java.util.*;
import org.hibernate.*;
import org.hibernate.cfg.*;

abstract class AbstractObject {
    AbstractObject() {}
    Integer id;
    Map properties;
}

//properties - map of Strings
class ObjectA extends AbstractObject {
    ObjectA() {}
}

//properties - map of Properties
class ObjectB extends AbstractObject {
    ObjectB() {}
}

class Property {
    Property() {}
    static class Id implements Serializable {
        Id() {}
        Integer object;
        String name;
    }
    Id id;
    String value;
}

class Test {

    static final SessionFactory sessionFactory =
        new Configuration().configure().buildSessionFactory();

    public static void main(String[] args) throws Exception {
        Session session;

        Integer objectId = new Integer(1);
        String name = "test";
        AbstractObject object = null;
        Object property = null;

    //  CREATE
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        //  creating object as ObjectA will not persist properties !!!
        object = new ObjectB(); // new ObjectA();
        object.id = objectId;
        object.properties = new HashMap();

        if (object instanceof ObjectA) {
            property = "abc";
        }
        else
        if (object instanceof ObjectB) {
            Property p = new Property();
            p.id = new Property.Id();
            p.id.object = objectId;
            p.id.name = name;
            p.value = "abc";
            property = p;
        }
        object.properties.put(name, property);
        session.save(object);
        session.getTransaction().commit();

    //  UPDATE
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        //  getting object as ObjectA will not persist changes !!!
        object = (AbstractObject)session.get(ObjectB.class, objectId); // get(ObjectA.class, objectId);
        property = object.properties.get(name);

        System.out.print("old value: ");
        if (object instanceof ObjectA) {
            System.out.println(property);
            property = "xyz";
        }
        else
        if (object instanceof ObjectB) {
            System.out.println(((Property)property).value);
            ((Property)property).value = "xyz";
        }
        object.properties.put(name, property);
        session.save(object);
        session.getTransaction().commit();

    //  SELECT
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        //  it does not matter if ObjectA or ObjectB is retrieved
        object = (AbstractObject)session.get(ObjectA.class, objectId);
        property = object.properties.get(name);
        System.out.print("new value: ");
        if (object instanceof ObjectA) {
            System.out.println(property);
        }
        else
        if (object instanceof ObjectB) {
            System.out.println(((Property)property).value);
        }
        session.getTransaction().commit();
    }
}
