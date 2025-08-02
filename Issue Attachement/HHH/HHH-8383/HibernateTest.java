import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.hibernate.cfg.Environment;

public class HibernateTest {
    public static void main(String[] args) {
	final int threadCount = 10;
	final int iterations = 1000000;

	final AtomicBoolean alreadyRun = new AtomicBoolean(false);
	ExecutorService pool = Executors.newFixedThreadPool(threadCount);

	// start a number of threads that change the System properties
	for (int i = 0; i < threadCount; i++) {
	    final int nr = i;
	    pool.submit(new Runnable() {
		@Override
		public void run() {
		    for (int j = 0; j < iterations; j++) {
			System.setProperty("Test" + j, "" + nr);
			if (nr == 0 && j == 1000) {
			    synchronized (alreadyRun) {
				alreadyRun.set(true);
				alreadyRun.notifyAll();
			    }
			}
		    }
		}
	    });
	}

	// wait if the threads have not started yet
	synchronized (alreadyRun) {
	    try {
		if (!alreadyRun.get()) {
		    alreadyRun.wait();
		} else {
		    System.out.println("Already finished.");
		}
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}

	// call getProperties which should call the Environment class
	// initialization
	Environment.getProperties();

	List<Runnable> remainingTasks = pool.shutdownNow();
	if (remainingTasks.isEmpty()) {
	    System.out.println("There were no remaining tasks");
	}
    }
}
