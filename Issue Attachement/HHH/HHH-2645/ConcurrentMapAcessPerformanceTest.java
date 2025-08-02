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

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import EDU.oswego.cs.dl.util.concurrent.CountDown;
import EDU.oswego.cs.dl.util.concurrent.Latch;
import junit.framework.TestCase;

/**
 * ConcurrentMapAcessPerformanceTest implementation
 *
 * @author Steve Ebersole
 */
public class ConcurrentMapAcessPerformanceTest extends TestCase {
	final static int DRY_RUN_THREADS = 20;
	final static int DRY_RUN_ITERATIONS_PER_THREAD = 1000;
	final static int LIVE_RUN_THREADS = 80;
	final static int LIVE_RUN_ITERATIONS_PER_THREAD = 10000;

	final static int GUESS_ENTITY_MODES_PER_ITERATION = 1;
	final static int GET_TUPLIZERS_PER_ITERATION = 20;

	public void testConcurrencyComparison() {
		Tuplizer tuplizer1 = new Tuplizer( 'D' );
		Tuplizer tuplizer2 = new Tuplizer( 'P' );

		Map baselineMap = Collections.synchronizedMap( new org.apache.commons.collections.SequencedHashMap() );
		baselineMap.put( EntityMode.DOM4J, tuplizer1 );
		baselineMap.put( EntityMode.POJO, tuplizer2 );

		Map fastMap = new org.apache.commons.collections.FastHashMap();
		fastMap.put( EntityMode.DOM4J, tuplizer1 );
		fastMap.put( EntityMode.POJO, tuplizer2 );

		Map jdkConcurrentMap = new java.util.concurrent.ConcurrentHashMap();
		jdkConcurrentMap.put( EntityMode.DOM4J, tuplizer1 );
		jdkConcurrentMap.put( EntityMode.POJO, tuplizer2 );

		Map dlConcurrentMap = new EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap();
		dlConcurrentMap.put( EntityMode.DOM4J, tuplizer1 );
		dlConcurrentMap.put( EntityMode.POJO, tuplizer2 );

		Map dlConcurrentReaderMap = new EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap();
		dlConcurrentReaderMap.put( EntityMode.DOM4J, tuplizer1 );
		dlConcurrentReaderMap.put( EntityMode.POJO, tuplizer2 );


		dryRun( baselineMap );
		dryRun( fastMap );
		dryRun( jdkConcurrentMap );
		dryRun( dlConcurrentMap );
		dryRun( dlConcurrentReaderMap );

		long baseline = liveRun( baselineMap );
		System.out.println( "baseline : " + baseline + " ms" );

		long duration = liveRun( fastMap );
		reportRunResults( duration, baseline, "FastHashMap" );

		duration = liveRun( jdkConcurrentMap );
		reportRunResults( duration, baseline, "ConcurrentHashMap (jdk)" );

		duration = liveRun( dlConcurrentMap );
		reportRunResults( duration, baseline, "ConcurrentHashMap (dl)" );

		duration = liveRun( dlConcurrentReaderMap );
		reportRunResults( duration, baseline, "ConcurrentReaderHashMap (dl)" );
	}

	private void reportRunResults(long duration, long baseline, String name) {
		System.out.println( name + " : " + duration + " ms (ratio=" + ( ( (float) duration )/baseline ) + ")" );
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

		EntityModeToTuplizerMapping mapping = new EntityModeToTuplizerMapping( map );

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
		while ( dumb.get() != null) {
			try {
				Thread.sleep( 500 );
			}
			catch ( InterruptedException e ) {
				throw new RuntimeException( "interrupted sleep during doGC()" );
			}
		}
	}


	private static final class Worker implements Runnable {
		private final EntityModeToTuplizerMapping mapping;
		private final Latch startLatch;
		private final CountDown finishLatch;
		private final int iterationsPerThread;

		private static final char[] TUPLIZERS = { 'D', 'P' };
		private static final EntityMode[] ENTITY_MODES = { EntityMode.DOM4J, EntityMode.POJO, EntityMode.MAP };

		private Worker(
				EntityModeToTuplizerMapping mapping,
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

	public static class Tuplizer {
		private char match;

		public Tuplizer(char match) {
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

	public static class EntityModeToTuplizerMapping {
		private final Map tuplizers;

		public EntityModeToTuplizerMapping(Map tuplizers) {
			this.tuplizers = tuplizers;
		}

		protected void addTuplizer(EntityMode entityMode, Tuplizer tuplizer) {
			tuplizers.put( entityMode, tuplizer );
		}

		/**
		 * Given a supposed instance of an entity/component, guess its entity mode.
		 *
		 * @param object The supposed instance of the entity/component.
		 *
		 * @return The guessed entity mode.
		 */
		public EntityMode guessEntityMode(Object object) {
			Iterator itr = tuplizers.entrySet().iterator();
			while ( itr.hasNext() ) {
				Map.Entry entry = ( Map.Entry ) itr.next();
				Tuplizer tuplizer = ( Tuplizer ) entry.getValue();
				if ( tuplizer.isInstance( object ) ) {
					return ( EntityMode ) entry.getKey();
				}
			}
			return null;
		}

		/**
		 * Locate the contained tuplizer responsible for the given entity-mode.  If
		 * no such tuplizer is defined on this mapping, then return null.
		 *
		 * @param entityMode The entity-mode for which the caller wants a tuplizer.
		 *
		 * @return The tuplizer, or null if not found.
		 */
		public Tuplizer getTuplizerOrNull(EntityMode entityMode) {
			return ( Tuplizer ) tuplizers.get( entityMode );
		}
	}

	public static class EntityMode implements Serializable {

		private static final Map INSTANCES = new HashMap();

		public static final EntityMode POJO = new EntityMode( "pojo" );
		public static final EntityMode DOM4J = new EntityMode( "dom4j" );
		public static final EntityMode MAP = new EntityMode( "dynamic-map" );

		static {
			INSTANCES.put( POJO.name, POJO );
			INSTANCES.put( DOM4J.name, DOM4J );
			INSTANCES.put( MAP.name, MAP );
		}

		private final String name;

		public EntityMode(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}

		private Object readResolve() {
			return INSTANCES.get( name );
		}

		public static EntityMode parse(String name) {
			EntityMode rtn = ( EntityMode ) INSTANCES.get( name );
			if ( rtn == null ) {
				// default is POJO
				rtn = POJO;
			}
			return rtn;
		}
	}
}
