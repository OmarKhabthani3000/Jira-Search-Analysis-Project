package org.hibernate.test.idgen.enhanced.sequence;

import junit.framework.TestCase;
import org.hibernate.id.enhanced.AccessCallback;
import org.hibernate.id.enhanced.OptimizerFactory;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;


public class NoopOptimizerTest extends TestCase {

    private AtomicLong _atomicLong = new AtomicLong(0);
    private ConcurrentMap<Serializable, Serializable> _ids = new ConcurrentHashMap<Serializable, Serializable>();
    private OptimizerFactory.NoopOptimizer _optimizer = new OptimizerFactory.NoopOptimizer(Long.class, 1);
    private AccessCallback _callback = new AccessCallback() {
        public long getNextValue() {
            return _atomicLong.getAndIncrement();
        }
    };
    private Serializable _foundDuplicate;

    public void testInsert() {
        int threadCount = 20;
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(threadCount + 1);
        for (int j = 0; j < threadCount; j++)
            new MyThread(cyclicBarrier).start();
        await(cyclicBarrier);
        await(cyclicBarrier);
        assertNull("found duplicate: " + _foundDuplicate, _foundDuplicate);
    }

    private class MyThread extends Thread {

        private final CyclicBarrier _cyclicBarrier;

        public MyThread(CyclicBarrier cyclicBarrier) {
            _cyclicBarrier = cyclicBarrier;
        }

        public void run() {
            await(_cyclicBarrier);

            for (int i = 0; i < 1000 && noDuplicateFound(); i++) {
                Serializable serializable = _optimizer.generate(_callback);
                Serializable found = _ids.putIfAbsent(serializable, serializable);
                if (found != null)
                    setFoundDuplicate(serializable);
            }
            await(_cyclicBarrier);

        }

        private synchronized void setFoundDuplicate(Serializable foundDuplicate) {
            _foundDuplicate = foundDuplicate;
        }

        private synchronized boolean noDuplicateFound() {
            return _foundDuplicate == null;
        }
    }

    private void await(CyclicBarrier cyclicBarrier) {
        try {
            cyclicBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
