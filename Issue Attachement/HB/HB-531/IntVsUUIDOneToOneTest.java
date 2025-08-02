package test;

import net.sf.hibernate.*;
import net.sf.hibernate.cfg.*;

import java.util.*;

public class IntVsUUIDOneToOneTest
{

    public static void main(String[] args) throws Exception
    {
        SessionFactory factory = new Configuration().configure().buildSessionFactory();
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        Thing thing = new Thing();
        IntThing intThing = new IntThing();
        UUIDThing uuidThing = new UUIDThing();
        
        intThing.setThing(thing);
        thing.setIntThing(intThing);

        //uuidThing.setThing(thing);
        //thing.setUUIDThing(uuidThing);

        session.save(thing);
        transaction.commit();
        session.flush();
        session.close();
    }
}

