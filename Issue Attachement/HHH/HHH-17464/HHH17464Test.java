package org.hibernate.orm.test.eviction;

import org.hibernate.Session;
import org.hibernate.engine.internal.ManagedTypeHelper;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.orm.junit.JiraKey;

import org.junit.Test;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@JiraKey("HHH-17464")
public class HHH17464Test
    extends BaseCoreFunctionalTestCase
{

    @Override
    protected Class<?>[] getAnnotatedClasses()
    {
        return new Class<?>[]{A.class, B.class};
    }

    @Test
    public void evictLoadedProxyDepthFirst()
    {
        // Create objects
        {
            Session session = openSession();
            session.beginTransaction();

            B b = new B();
            A a = new A();

            a.id = 1L;
            a.b  = b;
            b.id = 1L;

            session.persist(a);
            session.getTransaction()
                   .commit();
            session.close();
        }

        // Run the test two different ways: first one does not load the proxy; the second does load the proxy

        // Don't load a.b before evicting a.b then evicting a; this works
        loadProxyThenEvictDepthFirst(false);

        /*
         load a.b before evicting a.b then evicting a; this throws an NPE in org.hibernate.event.internal.DefaultEvictEventListener.onEvict:

         java.lang.NullPointerException: Cannot invoke "org.hibernate.engine.spi.EntityHolder.getEntity()" because "holder" is null
        	at org.hibernate.event.internal.DefaultEvictEventListener.onEvict(DefaultEvictEventListener.java:63)
        	at org.hibernate.event.service.internal.EventListenerGroupImpl.fireEventOnEachListener(EventListenerGroupImpl.java:127)
        	at org.hibernate.internal.SessionImpl.evict(SessionImpl.java:1373)
        	at org.hibernate.engine.spi.CascadingActions$4.cascade(CascadingActions.java:170)
        	at org.hibernate.engine.spi.CascadingActions$4.cascade(CascadingActions.java:160)
        	at org.hibernate.engine.internal.Cascade.cascadeToOne(Cascade.java:517)
        	at org.hibernate.engine.internal.Cascade.cascadeAssociation(Cascade.java:439)
        	at org.hibernate.engine.internal.Cascade.cascadeProperty(Cascade.java:224)
        	at org.hibernate.engine.internal.Cascade.cascade(Cascade.java:157)
        	at org.hibernate.engine.internal.Cascade.cascade(Cascade.java:64)
        	at org.hibernate.event.internal.DefaultEvictEventListener.doEvict(DefaultEvictEventListener.java:132)
        	at org.hibernate.event.internal.DefaultEvictEventListener.onEvict(DefaultEvictEventListener.java:74)
        	at org.hibernate.event.service.internal.EventListenerGroupImpl.fireEventOnEachListener(EventListenerGroupImpl.java:127)
        	at org.hibernate.internal.SessionImpl.evict(SessionImpl.java:1373)
        	at org.hibernate.engine.spi.CascadingActions$4.cascade(CascadingActions.java:170)
        	at org.hibernate.engine.spi.CascadingActions$4.cascade(CascadingActions.java:160)
        	at org.hibernate.engine.internal.Cascade.cascadeToOne(Cascade.java:517)
        	at org.hibernate.engine.internal.Cascade.cascadeAssociation(Cascade.java:439)
        	at org.hibernate.engine.internal.Cascade.cascadeProperty(Cascade.java:224)
        	at org.hibernate.engine.internal.Cascade.cascade(Cascade.java:157)
        	at org.hibernate.engine.internal.Cascade.cascade(Cascade.java:64)
        	at org.hibernate.event.internal.DefaultEvictEventListener.doEvict(DefaultEvictEventListener.java:132)
        	at org.hibernate.event.internal.DefaultEvictEventListener.onEvict(DefaultEvictEventListener.java:74)
        	at org.hibernate.event.service.internal.EventListenerGroupImpl.fireEventOnEachListener(EventListenerGroupImpl.java:127)
        	at org.hibernate.internal.SessionImpl.evict(SessionImpl.java:1373)

        	It looks like the EntityHolder code assumes that the PersistenceContext has an EntityHolder object for any
        	previously _loaded_ proxy (i.e. LazyInitializer.uninitialized is false). This isn't the case for an evicted
            initialized proxy; the LazyInitailizer still has the target reference but the EntityHolder has been removed
            from the PersistenceContext.
        */
        loadProxyThenEvictDepthFirst(true);

        //
        Session session = openSession();

        session.beginTransaction();
        session.remove(session.get(A.class, 1L));
        session.getTransaction()
               .commit();
        session.close();

    }

    private void loadProxyThenEvictDepthFirst(
        boolean loadProxy)
    {
        Session session = openSession();

        session.beginTransaction();

        // Load a by id, then trigger load of a.b, if requested
        A a = session.get(A.class, 1L);

        if (loadProxy) {
            session.get(B.class, 1L);
        }

        assertTrue(ManagedTypeHelper.isHibernateProxy(a.b));
        assertNotEquals(loadProxy, ManagedTypeHelper.asHibernateProxy(a.b)
                                                    .getHibernateLazyInitializer()
                                                    .isUninitialized());

        // Evict a.b then evict a
        session.evict(a.b);
        session.evict(a);
        session.getTransaction()
               .commit();
        session.close();
    }

    @Entity
    private static class A
    {

        @Id Long id;

        // Lazy OneToOne
        @OneToOne(
            fetch = LAZY,
            cascade = ALL,
            orphanRemoval = true
        ) B b;
    }

    @Entity
    private static class B
    {

        @Id Long id;

        public B()
        {
        }
    }

}

