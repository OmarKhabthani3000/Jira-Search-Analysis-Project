/*
 * Copyright (c) 2007, Red Hat Middleware, LLC. All rights reserved.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, v. 2.1. This program is distributed in the
 * hope that it will be useful, but WITHOUT A WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License, v.2.1 along with this
 * distribution; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Red Hat Author(s): Steve Ebersole
 */
package org.hibernate.test.perf.map;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;

import EDU.oswego.cs.dl.util.concurrent.CountDown;
import EDU.oswego.cs.dl.util.concurrent.Latch;
import junit.framework.TestCase;

import org.hibernate.EntityMode;
import org.hibernate.tuple.EntityModeToTuplizerMapping;
import org.hibernate.tuple.Tuplizer;

/**
 * ConcurrentMapAcessPerformanceTest implementation
 *
 * @author Steve Ebersole
 */
public class ConcurrentMapAccessPerformanceTest extends TestCase {
	final static int DRY_RUN_THREADS = 20;
	final static int DRY_RUN_ITERATIONS_PER_THREAD = 1000;
	final static int LIVE_RUN_THREADS = 80;
	final static int LIVE_RUN_ITERATIONS_PER_THREAD = 10000;

	final static int GUESS_ENTITY_MODES_PER_ITERATION = 1;
	final static int GET_TUPLIZERS_PER_ITERATION = 20;

	public void testConcurrencyComparison() {
		Map syncSequencedMap = Collections.synchronizedMap( new org.apache.commons.collections.SequencedHashMap() );
		Map syncLinkedMap = Collections.synchronizedMap( new java.util.LinkedHashMap() );
		org.apache.commons.collections.FastHashMap apacheFastMap = new org.apache.commons.collections.FastHashMap();
		apacheFastMap.setFast( true );
		Map hibernateFastMap = new org.hibernate.util.FastHashMap();
		Map jdkConcurrentMap = new java.util.concurrent.ConcurrentHashMap();
		Map dlConcurrentMap = new EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap();
		Map dlConcurrentReaderMap = new EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap();

		dryRun( null );
		dryRun( syncSequencedMap );
		dryRun( syncLinkedMap );
		dryRun( apacheFastMap );
		dryRun( hibernateFastMap );
		dryRun( jdkConcurrentMap );
		dryRun( dlConcurrentMap );
		dryRun( dlConcurrentReaderMap );

		long defaultTime = liveRun( null );
		System.out.println( "default : " + defaultTime + " ms" );

		long duration = liveRun( syncSequencedMap );
		reportRunResults( duration, defaultTime, "Synchronized SequencedHashMap (apache)" );

		duration = liveRun( syncLinkedMap );
		reportRunResults( duration, defaultTime, "Synchronized LinkedHashMap (jdk)" );

		duration = liveRun( apacheFastMap );
		reportRunResults( duration, defaultTime, "FastHashMap (apache)" );

		duration = liveRun( hibernateFastMap );
		reportRunResults( duration, defaultTime, "FastHashMap (hibernate)" );

		duration = liveRun( jdkConcurrentMap );
		reportRunResults( duration, defaultTime, "ConcurrentHashMap (jdk)" );

		duration = liveRun( dlConcurrentMap );
		reportRunResults( duration, defaultTime, "ConcurrentHashMap (dl)" );

		duration = liveRun( dlConcurrentReaderMap );
		reportRunResults( duration, defaultTime, "ConcurrentReaderHashMap (dl)" );
	}

	private void reportRunResults(long duration, long baseline, String name) {
		System.out.println( name + " : " + duration + " ms (ratio=" + ( ( ( float ) duration ) / baseline ) + ")" );
	}

	private long dryRun(Map map) {
		return runTest( map, DRY_RUN_THREADS, DRY_RUN_ITERATIONS_PER_THREAD );
	}

	private long liveRun(Map map) {
		return runTest( map, LIVE_RUN_THREADS, LIVE_RUN_ITERATIONS_PER_THREAD );
	}

	private long runTest(
			Map map,
			int threads,
			final int iterationsPerThread) {
		doGC();

		Mapping mapping =
				( map == null ? new Mapping() : new Mapping( map ) );

		final Latch startLatch = new Latch();
		final CountDown finishLatch = new CountDown( threads );

		long start = System.currentTimeMillis();

		for ( int i = 0; i < threads; i++ ) {
			new Thread( new Worker( mapping, startLatch, finishLatch, iterationsPerThread ) ).start();
		}

		startLatch.release();
		try {
			finishLatch.acquire();
		}
		catch ( InterruptedException e ) {
			throw new RuntimeException( e );
		}

		return System.currentTimeMillis() - start;
	}

	private void doGC() {
		WeakReference dumb = new WeakReference( new Object() );
		System.gc();
		System.gc();
		while ( dumb.get() != null ) {
			try {
				Thread.sleep( 500 );
			}
			catch ( InterruptedException e ) {
				throw new RuntimeException( "interrupted sleep during doGC()" );
			}
		}
	}


	private static final class Worker implements Runnable {
		private final Mapping mapping;
		private final Latch startLatch;
		private final CountDown finishLatch;
		private final int iterationsPerThread;

		private static final char[] TUPLIZERS = { 'D', 'P' };
		private static final EntityMode[] ENTITY_MODES = { EntityMode.DOM4J, EntityMode.POJO, EntityMode.MAP };

		private Worker(
				Mapping mapping,
				Latch startLatch,
				CountDown finishLatch,
				int iterationsPerThread) {
			this.mapping = mapping;
			this.startLatch = startLatch;
			this.finishLatch = finishLatch;
			this.iterationsPerThread = iterationsPerThread;
		}

		public void run() {
			try {
				startLatch.acquire();
			}
			catch ( InterruptedException e ) {
				throw new RuntimeException( e );
			}

			for ( int i = 0; i < iterationsPerThread; i++ ) {
				for ( int j = 0; j < GUESS_ENTITY_MODES_PER_ITERATION; j++ ) {
					mapping.guessEntityMode( TUPLIZERS[j % TUPLIZERS.length] );
				}

				for ( int j = 0; j < GET_TUPLIZERS_PER_ITERATION; j++ ) {
					mapping.getTuplizerOrNull( ENTITY_MODES[j % ENTITY_MODES.length] );
				}
			}

			finishLatch.release();
		}
	}


	public static class TuplizerForMapping implements Tuplizer {
		private char match;

		public TuplizerForMapping(char match) {
			this.match = match;
		}

		public Class getMappedClass() {
			return Character.class;
		}

		public Object getPropertyValue(Object entity, int i) {
			return null;
		}

		public Object[] getPropertyValues(Object entity) {
			return null;
		}

		public Object instantiate() {
			return null;
		}

		public boolean isInstance(Object object) {
			return ( ( Character ) object ).charValue() == match;
		}

		public void setPropertyValues(Object entity, Object[] values) {
		}

	}

	public static class Mapping extends EntityModeToTuplizerMapping {
		private static final Tuplizer tuplizer1 = new TuplizerForMapping( 'D' );
		private static final Tuplizer tuplizer2 = new TuplizerForMapping( 'P' );


		public Mapping() {
			super();
			addTuplizers();
		}

		public Mapping(Map map) {
			super( map );
			addTuplizers();
		}

		protected void addTuplizers() {
			addTuplizer( EntityMode.DOM4J, tuplizer1 );
			addTuplizer( EntityMode.POJO, tuplizer2 );
		}
	}
}

