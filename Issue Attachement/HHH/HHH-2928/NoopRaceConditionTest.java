package org.hibernate.test.idgen.enhanced.sequence;

import junit.framework.Test;

import org.hibernate.junit.functional.FunctionalTestClassTestSuite;
import org.hibernate.junit.functional.FunctionalTestCase ;
import org.hibernate.Session;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.id.enhanced.OptimizerFactory;
import org.hibernate.id.enhanced.AccessCallback;
import org.hibernate.persister.entity.EntityPersister ;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;


public class NoopRaceConditionTest extends FunctionalTestCase {

    public NoopRaceConditionTest(String string) {
        super(string);
    }


    protected void prepareTest() throws Exception {
        super.prepareTest();    
    }

    public String[] getMappings() {
        return new String[]{"idgen/enhanced/sequence/Basic.hbm.xml"};
    }

    public static Test suite() {
        return new FunctionalTestClassTestSuite(NoopRaceConditionTest.class);
    }

    public void testInsert() {
        int threadCount = 5;
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(threadCount + 1);
        for (int j = 0; j < threadCount; j++)
            InsertThread(cyclicBarrier).start();
        await(cyclicBarrier);
        if (!cyclicBarrier.isBroken())
            await(cyclicBarrier);
        assertNull("exception caught: " + _exception, _exception);
    }

    private Exception _exception;

    private Thread InsertThread(final CyclicBarrier cyclicBarrier) {
        return new MyThread(cyclicBarrier);

    }

    private void await(CyclicBarrier cyclicBarrier) {
        try {
            cyclicBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();  
        }
    }

    private class MyThread extends Thread {

        private final CyclicBarrier _cyclicBarrier;

        public MyThread(CyclicBarrier cyclicBarrier) {
            _cyclicBarrier = cyclicBarrier;
        }

        private synchronized void setException(Exception e) {
            _exception = e;
        }

        public void run() {
            await(_cyclicBarrier);

            try {
                for (int i = 0; i < 100 && noExceptionThrown(); i++) {
                    Session s = openSession();
                    s.beginTransaction();
                    Entity entity = new Entity("");
                    s.save(entity);
                    s.getTransaction().commit();
                    s.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                setException(e);
            } finally {
                await(_cyclicBarrier);
            }
        }

        private synchronized boolean noExceptionThrown() {
            return _exception == null;
        }
    }
}
