import java.io.Serializable;


public class PooledOptimizerTest {

    private long hiValue=-1;
    private long value;
    private int incrementSize=2;

    private Object sync;
    private boolean wait;
    
    public PooledOptimizerTest(Object sync, boolean wait){
        this.sync=sync;
        this.wait=wait;
    }
    
    public synchronized Serializable generate(AccessCallback callback) {
        if ( hiValue < 0 ) {
            value = callback.getNextValue();
            if ( value < 1 ) {
                // unfortunately not really safe to normalize this
                // to 1 as an initial value like we do the others
                // because we would not be able to control this if
                // we are using a sequence...
//                log.info( "pooled optimizer source reported [" + value + "] as the initial value; use of 1 or greater highly recommended" );
            }
            
            // The intercept method is used to simulate the concurrent
            // access to the DB sequence by different JVMs.
            intercept();
            hiValue = callback.getNextValue();
        }
        else if ( value >= hiValue ) {
            hiValue = callback.getNextValue();
            value = hiValue - incrementSize;
        }
        return make(value++);
    }
    
    public synchronized Serializable generateGood(AccessCallback callback) {
        if ( hiValue < 0 ) {
            value = callback.getNextValue();
            hiValue = value + incrementSize;
        }
        else if ( value >= hiValue ) {
            value = callback.getNextValue();
            hiValue = value + incrementSize;
        }
        return make(value++);
    }

    
    protected void intercept(){
        synchronized (sync) {
            if(wait){
                try {
//                    System.out.println("will wait (Thread "+ Thread.currentThread()+ ")");
                    sync.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else{
                sync.notify();
            }
        }
    }
    private Long make(long value){
        return new Long(value);
    }

    public interface AccessCallback {
        /**
         * Retrieve the next value from the underlying source.
         *
         * @return The next value.
         */
        public long getNextValue();
    }
    
    public static void main(String[] args)throws Exception{
        
        // Dummy sequence generator
        final AccessCallback callback = new AccessCallback() {
            long seqVal = 0;
            int increment = 2;
            public synchronized long getNextValue() {
                long res = seqVal;
                seqVal+=increment;
//                System.out.println(">>>nextVal: "+res);
                return res;
            }
        };

        final Object sync = new Object();
        
        Runnable r1 = new Runnable() {
            public void run() {
                PooledOptimizerTest test = new PooledOptimizerTest(sync, true);
                for (int i = 0; i < 10; i++) {
                    Object id = test.generate(callback);
//                    Object id = test.generateGood(callback);
                    System.out.println("Thread " + Thread.currentThread() + " index " + i + ": " + id);
                }
            }
        };
        Runnable r2 = new Runnable() {
            public void run() {
                PooledOptimizerTest test = new PooledOptimizerTest(sync, false);
                for (int i = 0; i < 10; i++) {
                    Object id = test.generate(callback);
//                    Object id = test.generateGood(callback);
                    System.out.println("Thread " + Thread.currentThread() + " index " + i + ": " + id);
                }
            }
        };
        Thread t1 = new Thread(r1);
        Thread t2 = new Thread(r2);
        t1.start();
        t2.start();
    }
    
    // Output of this test
//    Thread Thread[Thread-0,5,main] index 0: 0
//    Thread Thread[Thread-0,5,main] index 1: 1
//    Thread Thread[Thread-1,5,main] index 0: 2
//    Thread Thread[Thread-1,5,main] index 1: 3
//    Thread Thread[Thread-1,5,main] index 2: 6
//    Thread Thread[Thread-0,5,main] index 2: 2
//    Thread Thread[Thread-0,5,main] index 3: 3
//    Thread Thread[Thread-0,5,main] index 4: 4
//    Thread Thread[Thread-0,5,main] index 5: 5
//    Thread Thread[Thread-0,5,main] index 6: 8
//    Thread Thread[Thread-0,5,main] index 7: 9
//    Thread Thread[Thread-0,5,main] index 8: 10
//    Thread Thread[Thread-0,5,main] index 9: 11
//    Thread Thread[Thread-1,5,main] index 3: 7
//    Thread Thread[Thread-1,5,main] index 4: 12
//    Thread Thread[Thread-1,5,main] index 5: 13
//    Thread Thread[Thread-1,5,main] index 6: 14
//    Thread Thread[Thread-1,5,main] index 7: 15
//    Thread Thread[Thread-1,5,main] index 8: 16
//    Thread Thread[Thread-1,5,main] index 9: 17
    
}
