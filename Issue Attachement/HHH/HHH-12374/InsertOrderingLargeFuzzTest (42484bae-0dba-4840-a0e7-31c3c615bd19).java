/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.insertordering;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.cfg.Environment;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;

import org.jboss.logging.Logger;

import org.junit.Test;

import static java.time.temporal.ChronoField.EPOCH_DAY;

import static org.hibernate.testing.transaction.TransactionUtil.doInHibernate;

/**
 * @author  Roger Dearnaley
 */
@TestForIssue(jiraKey = "HHH-12374")
public class InsertOrderingLargeFuzzTest
	extends BaseNonConfigCoreFunctionalTestCase {

	//~ Static fields/initializers ---------------------------------------------

	private static final Logger log = Logger.getLogger(
			InsertOrderingLargeFuzzTest.class);

	//~ Methods ----------------------------------------------------------------

	@Test public void testFuzzSetOfEight() {
		Random rand = new Random(LocalDate.now()
										  .getLong(EPOCH_DAY)); // Use a repeatable seed that varies slowly

		for (int i = 0; i < 900; i++) {
			fuzzTest(8, rand);
		}
	}

	@Test public void testFuzzSetOfSixteen() {
		Random rand = new Random(LocalDate.now()
										  .getLong(EPOCH_DAY)); // Use a repeatable seed that varies slowly

		for (int i = 0; i < 900; i++) {
			fuzzTest(16, rand);
		}
	}

	@Test public void testFuzzSetOfThirtytwo() {
		Random rand = new Random(LocalDate.now()
										  .getLong(EPOCH_DAY)); // Use a repeatable seed that varies slowly

		for (int i = 0; i < 900; i++) {
			fuzzTest(32, rand);
		}
	}

	@Override protected void addSettings(Map settings) {
		settings.put(Environment.ORDER_INSERTS, "true");
		settings.put(Environment.STATEMENT_BATCH_SIZE, "64");
	}

	// Generate a random class hierarchy (of instantiated relationships) in a
	// (not necessarily connected) non-cyclic directed graph with setSize nodes,
	// for the insert ordering code to sort
	private void fuzzTest(
			int	setSize,
			Random rand) {
		assert (setSize >= 4);
		assert (setSize <= 32);
		doInHibernate(this::sessionFactory,
			session -> {
				session.flush();

				// Instantiate all setSize classes
				Object[] classObject = new Object[32];
				Object[] classSet	= new Object[32];

				classObject[0] = new Class00();
				classSet[0]	= new HashSet<Class00>();

				((HashSet<Class00>) classSet[0]).add((Class00) classObject[0]);
				classObject[1] = new Class01();
				classSet[1]	= new HashSet<Class01>();

				((HashSet<Class01>) classSet[1]).add((Class01) classObject[1]);
				classObject[2] = new Class02();
				classSet[2]	= new HashSet<Class02>();

				((HashSet<Class02>) classSet[2]).add((Class02) classObject[2]);
				classObject[3] = new Class03();
				classSet[3]	= new HashSet<Class03>();

				((HashSet<Class03>) classSet[3]).add((Class03) classObject[3]);
				classObject[4] = (setSize >= 5)
					? new Class04()
					: null;
				classSet[4]	= (setSize >= 5)
					? new HashSet<Class04>()
					: null;

				if (classSet[4] != null) {
					((HashSet<Class04>) classSet[4]).add(
						(Class04) classObject[4]);
				}

				classObject[5] = (setSize >= 6)
					? new Class05()
					: null;
				classSet[5]	= (setSize >= 6)
					? new HashSet<Class05>()
					: null;

				if (classSet[5] != null) {
					((HashSet<Class05>) classSet[5]).add(
						(Class05) classObject[5]);
				}

				classObject[6] = (setSize >= 7)
					? new Class06()
					: null;
				classSet[6]	= (setSize >= 7)
					? new HashSet<Class06>()
					: null;

				if (classSet[6] != null) {
					((HashSet<Class06>) classSet[6]).add(
						(Class06) classObject[6]);
				}

				classObject[7] = (setSize >= 8)
					? new Class07()
					: null;
				classSet[7]	= (setSize >= 8)
					? new HashSet<Class07>()
					: null;

				if (classSet[7] != null) {
					((HashSet<Class07>) classSet[7]).add(
						(Class07) classObject[7]);
				}

				classObject[8] = (setSize >= 9)
					? new Class08()
					: null;
				classSet[8]	= (setSize >= 9)
					? new HashSet<Class08>()
					: null;

				if (classSet[8] != null) {
					((HashSet<Class08>) classSet[8]).add(
						(Class08) classObject[8]);
				}

				classObject[9] = (setSize >= 10)
					? new Class09()
					: null;
				classSet[9]	= (setSize >= 10)
					? new HashSet<Class09>()
					: null;

				if (classSet[9] != null) {
					((HashSet<Class09>) classSet[9]).add(
						(Class09) classObject[9]);
				}

				classObject[10] = (setSize >= 11)
					? new Class10()
					: null;
				classSet[10]	= (setSize >= 11)
					? new HashSet<Class10>()
					: null;

				if (classSet[10] != null) {
					((HashSet<Class10>) classSet[10]).add(
						(Class10) classObject[10]);
				}

				classObject[11] = (setSize >= 12)
					? new Class11()
					: null;
				classSet[11]	= (setSize >= 12)
					? new HashSet<Class11>()
					: null;

				if (classSet[11] != null) {
					((HashSet<Class11>) classSet[11]).add(
						(Class11) classObject[11]);
				}

				classObject[12] = (setSize >= 13)
					? new Class12()
					: null;
				classSet[12]	= (setSize >= 13)
					? new HashSet<Class12>()
					: null;

				if (classSet[12] != null) {
					((HashSet<Class12>) classSet[12]).add(
						(Class12) classObject[12]);
				}

				classObject[13] = (setSize >= 14)
					? new Class13()
					: null;
				classSet[13]	= (setSize >= 14)
					? new HashSet<Class13>()
					: null;

				if (classSet[13] != null) {
					((HashSet<Class13>) classSet[13]).add(
						(Class13) classObject[13]);
				}

				classObject[14] = (setSize >= 15)
					? new Class14()
					: null;
				classSet[14]	= (setSize >= 15)
					? new HashSet<Class14>()
					: null;

				if (classSet[14] != null) {
					((HashSet<Class14>) classSet[14]).add(
						(Class14) classObject[14]);
				}

				classObject[15] = (setSize >= 16)
					? new Class15()
					: null;
				classSet[15]	= (setSize >= 16)
					? new HashSet<Class15>()
					: null;

				if (classSet[15] != null) {
					((HashSet<Class15>) classSet[15]).add(
						(Class15) classObject[15]);
				}

				classObject[16] = (setSize >= 17)
					? new Class16()
					: null;
				classSet[16]	= (setSize >= 17)
					? new HashSet<Class16>()
					: null;

				if (classSet[16] != null) {
					((HashSet<Class16>) classSet[16]).add(
						(Class16) classObject[16]);
				}

				classObject[17] = (setSize >= 18)
					? new Class17()
					: null;
				classSet[17]	= (setSize >= 18)
					? new HashSet<Class17>()
					: null;

				if (classSet[17] != null) {
					((HashSet<Class17>) classSet[17]).add(
						(Class17) classObject[17]);
				}

				classObject[18] = (setSize >= 19)
					? new Class18()
					: null;
				classSet[18]	= (setSize >= 19)
					? new HashSet<Class18>()
					: null;

				if (classSet[18] != null) {
					((HashSet<Class18>) classSet[18]).add(
						(Class18) classObject[18]);
				}

				classObject[19] = (setSize >= 20)
					? new Class19()
					: null;
				classSet[19]	= (setSize >= 20)
					? new HashSet<Class19>()
					: null;

				if (classSet[19] != null) {
					((HashSet<Class19>) classSet[19]).add(
						(Class19) classObject[19]);
				}

				classObject[20] = (setSize >= 21)
					? new Class20()
					: null;
				classSet[20]	= (setSize >= 21)
					? new HashSet<Class20>()
					: null;

				if (classSet[20] != null) {
					((HashSet<Class20>) classSet[20]).add(
						(Class20) classObject[20]);
				}

				classObject[21] = (setSize >= 22)
					? new Class21()
					: null;
				classSet[21]	= (setSize >= 22)
					? new HashSet<Class21>()
					: null;

				if (classSet[21] != null) {
					((HashSet<Class21>) classSet[21]).add(
						(Class21) classObject[21]);
				}

				classObject[22] = (setSize >= 23)
					? new Class22()
					: null;
				classSet[22]	= (setSize >= 23)
					? new HashSet<Class22>()
					: null;

				if (classSet[22] != null) {
					((HashSet<Class22>) classSet[22]).add(
						(Class22) classObject[22]);
				}

				classObject[23] = (setSize >= 24)
					? new Class23()
					: null;
				classSet[23]	= (setSize >= 24)
					? new HashSet<Class23>()
					: null;

				if (classSet[23] != null) {
					((HashSet<Class23>) classSet[23]).add(
						(Class23) classObject[23]);
				}

				classObject[24] = (setSize >= 25)
					? new Class24()
					: null;
				classSet[24]	= (setSize >= 25)
					? new HashSet<Class24>()
					: null;

				if (classSet[24] != null) {
					((HashSet<Class24>) classSet[24]).add(
						(Class24) classObject[24]);
				}

				classObject[25] = (setSize >= 26)
					? new Class25()
					: null;
				classSet[25]	= (setSize >= 26)
					? new HashSet<Class25>()
					: null;

				if (classSet[25] != null) {
					((HashSet<Class25>) classSet[25]).add(
						(Class25) classObject[25]);
				}

				classObject[26] = (setSize >= 27)
					? new Class26()
					: null;
				classSet[26]	= (setSize >= 27)
					? new HashSet<Class26>()
					: null;

				if (classSet[26] != null) {
					((HashSet<Class26>) classSet[26]).add(
						(Class26) classObject[26]);
				}

				classObject[27] = (setSize >= 28)
					? new Class27()
					: null;
				classSet[27]	= (setSize >= 28)
					? new HashSet<Class27>()
					: null;

				if (classSet[27] != null) {
					((HashSet<Class27>) classSet[27]).add(
						(Class27) classObject[27]);
				}

				classObject[28] = (setSize >= 29)
					? new Class28()
					: null;
				classSet[28]	= (setSize >= 29)
					? new HashSet<Class28>()
					: null;

				if (classSet[28] != null) {
					((HashSet<Class28>) classSet[28]).add(
						(Class28) classObject[28]);
				}

				classObject[29] = (setSize >= 30)
					? new Class29()
					: null;
				classSet[29]	= (setSize >= 30)
					? new HashSet<Class29>()
					: null;

				if (classSet[29] != null) {
					((HashSet<Class29>) classSet[29]).add(
						(Class29) classObject[29]);
				}

				classObject[30] = (setSize >= 31)
					? new Class30()
					: null;
				classSet[30]	= (setSize >= 31)
					? new HashSet<Class30>()
					: null;

				if (classSet[30] != null) {
					((HashSet<Class30>) classSet[30]).add(
						(Class30) classObject[30]);
				}

				classObject[31] = (setSize >= 32)
					? new Class31()
					: null;
				classSet[31]	= (setSize >= 32)
					? new HashSet<Class31>()
					: null;

				if (classSet[31] != null) {
					((HashSet<Class31>) classSet[31]).add(
						(Class31) classObject[31]);
				}

				// Add some random cross-linking to somewhat constrain the sort
				// order output by the ActionQueue sort code Average across
				// links O(nodes), O(nodes ln(nodes)), and O(nodes^2), with
				// contributions balanced for n=8
				int numLinks = setSize - 1;

				switch (rand.nextInt(3)) {

					case 0:
						numLinks = (setSize - 1) + rand.nextInt(setSize);

					case 1:
						numLinks = (setSize - 1)
								+ rand.nextInt(
									(int) ((Math.log(setSize) - 1) * setSize
										* 0.92));

					case 2:
					default:
						numLinks = (setSize - 1)
								+ rand.nextInt(setSize * setSize / 8);
				}

				for (int i = 0; i < numLinks; i++) {
					int x;
					int y;

					do {

						// Generate pair of uniformly distributed random numbers
						// 0 <= x < y <= setSize-1
						x = rand.nextInt(setSize);
						y = rand.nextInt(setSize - 1);

						// Ensure x != y
						if (y >= x) {
							y++;
						}

						// Ensure x < y
						if (y < x) {
							int t = x;

							x = y;
							y = t;
						}

						// Bias sample so that y tends to be close to x
						// for a more realistic random class hierarchy
					}
					while (y - x > rand.nextInt(setSize) + 1);

					// Enforce that class<x> must be a parent of class<y> (where
					// x < y): Use reflection to call
					// classObject[x].setClass<y>(classObject[y])
					try {
						Method method = classObject[x].getClass()
													  .getDeclaredMethod(
														  String.format(
															  "setClass%02d",
															  y),
														  Class.forName(
															  "java.util.Set"));

						method.invoke(classObject[x], classSet[y]);
					}
					catch (IllegalAccessException e) {
						assert (false); // This should never happen
					}
					catch (NoSuchMethodException e) {
						assert (false); // This should never happen
					}
					catch (InvocationTargetException e) {
						assert (false); // This should never happen
					}
					catch (ClassNotFoundException e) {
						assert (false); // This should never happen
					}
				}

				// Persist all the entities in a semi-random order that is sometimes
				// biased towards increasing class order so that the ActionQueue
				// sort code will generally need to reverse them (to try to
				// maximize the work it has to do)
				int	   sortRandomness = setSize / ((rand.nextInt(2) == 1) ? 1 : 4);
				boolean[] done		   = new boolean[setSize];

				for (int i = 0; i < setSize; i++) {
					done[i] = false;
				}

				for (int i = 0; i < setSize; i++) {
					int x = (rand.nextInt(sortRandomness) + i + setSize
								- (sortRandomness / 2)) % setSize;

					while (done[x]) {
						x = (++x) % setSize;
					}

					done[x] = true;
					session.persist(classObject[x]);
				}

				session.getTransaction()
					   .commit();
				session.beginTransaction();
			});
	}

	@Override protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] {
			Class00.class,
			Class01.class,
			Class02.class,
			Class03.class,
			Class04.class,
			Class05.class,
			Class06.class,
			Class07.class,
			Class08.class,
			Class09.class,
			Class10.class,
			Class11.class,
			Class12.class,
			Class13.class,
			Class14.class,
			Class15.class,
			Class16.class,
			Class17.class,
			Class18.class,
			Class19.class,
			Class20.class,
			Class21.class,
			Class22.class,
			Class23.class,
			Class24.class,
			Class25.class,
			Class26.class,
			Class27.class,
			Class28.class,
			Class29.class,
			Class30.class,
			Class31.class
		};
	}

	//~ Inner Classes ----------------------------------------------------------

	// class00 through class31: classX is optionally the (one-to-many,
	// join-column) parent of all classes classY where Y > X
	@Entity(name = "class00")
	public static class Class00 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class01.class
		)
		private Set<Class01>		class01;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class02.class
		)
		private Set<Class02>		class02;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class03.class
		)
		private Set<Class03>		class03;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class04.class
		)
		private Set<Class04>		class04;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class05.class
		)
		private Set<Class05>		class05;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class06.class
		)
		private Set<Class06>		class06;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class07.class
		)
		private Set<Class07>		class07;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class08.class
		)
		private Set<Class08>		class08;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class09.class
		)
		private Set<Class09>		class09;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class10.class
		)
		private Set<Class10>		class10;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class11.class
		)
		private Set<Class11>		class11;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class12.class
		)
		private Set<Class12>		class12;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class13.class
		)
		private Set<Class13>		class13;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class14.class
		)
		private Set<Class14>		class14;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class15.class
		)
		private Set<Class15>		class15;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class16.class
		)
		private Set<Class16>		class16;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class17.class
		)
		private Set<Class17>		class17;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class18.class
		)
		private Set<Class18>		class18;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class19.class
		)
		private Set<Class19>		class19;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class20.class
		)
		private Set<Class20>		class20;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class00",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class00() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass01(Class01 class01) {
			this.class01.add(class01);
			class01.setClass00(this);
		}

		public void addClass02(Class02 class02) {
			this.class02.add(class02);
			class02.setClass00(this);
		}

		public void addClass03(Class03 class03) {
			this.class03.add(class03);
			class03.setClass00(this);
		}

		public void addClass04(Class04 class04) {
			this.class04.add(class04);
			class04.setClass00(this);
		}

		public void addClass05(Class05 class05) {
			this.class05.add(class05);
			class05.setClass00(this);
		}

		public void addClass06(Class06 class06) {
			this.class06.add(class06);
			class06.setClass00(this);
		}

		public void addClass07(Class07 class07) {
			this.class07.add(class07);
			class07.setClass00(this);
		}

		public void addClass08(Class08 class08) {
			this.class08.add(class08);
			class08.setClass00(this);
		}

		public void addClass09(Class09 class09) {
			this.class09.add(class09);
			class09.setClass00(this);
		}

		public void addClass10(Class10 class10) {
			this.class10.add(class10);
			class10.setClass00(this);
		}

		public void addClass11(Class11 class11) {
			this.class11.add(class11);
			class11.setClass00(this);
		}

		public void addClass12(Class12 class12) {
			this.class12.add(class12);
			class12.setClass00(this);
		}

		public void addClass13(Class13 class13) {
			this.class13.add(class13);
			class13.setClass00(this);
		}

		public void addClass14(Class14 class14) {
			this.class14.add(class14);
			class14.setClass00(this);
		}

		public void addClass15(Class15 class15) {
			this.class15.add(class15);
			class15.setClass00(this);
		}

		public void addClass16(Class16 class16) {
			this.class16.add(class16);
			class16.setClass00(this);
		}

		public void addClass17(Class17 class17) {
			this.class17.add(class17);
			class17.setClass00(this);
		}

		public void addClass18(Class18 class18) {
			this.class18.add(class18);
			class18.setClass00(this);
		}

		public void addClass19(Class19 class19) {
			this.class19.add(class19);
			class19.setClass00(this);
		}

		public void addClass20(Class20 class20) {
			this.class20.add(class20);
			class20.setClass00(this);
		}

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass00(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass00(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass00(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass00(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass00(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass00(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass00(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass00(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass00(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass00(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass00(this);
		}

		public void removeClass01(Class01 class01) {
			this.class01.remove(class01);
			class01.setClass00(null);
		}

		public void removeClass02(Class02 class02) {
			this.class02.remove(class02);
			class02.setClass00(null);
		}

		public void removeClass03(Class03 class03) {
			this.class03.remove(class03);
			class03.setClass00(null);
		}

		public void removeClass04(Class04 class04) {
			this.class04.remove(class04);
			class04.setClass00(null);
		}

		public void removeClass05(Class05 class05) {
			this.class05.remove(class05);
			class05.setClass00(null);
		}

		public void removeClass06(Class06 class06) {
			this.class06.remove(class06);
			class06.setClass00(null);
		}

		public void removeClass07(Class07 class07) {
			this.class07.remove(class07);
			class07.setClass00(null);
		}

		public void removeClass08(Class08 class08) {
			this.class08.remove(class08);
			class08.setClass00(null);
		}

		public void removeClass09(Class09 class09) {
			this.class09.remove(class09);
			class09.setClass00(null);
		}

		public void removeClass10(Class10 class10) {
			this.class10.remove(class10);
			class10.setClass00(null);
		}

		public void removeClass11(Class11 class11) {
			this.class11.remove(class11);
			class11.setClass00(null);
		}

		public void removeClass12(Class12 class12) {
			this.class12.remove(class12);
			class12.setClass00(null);
		}

		public void removeClass13(Class13 class13) {
			this.class13.remove(class13);
			class13.setClass00(null);
		}

		public void removeClass14(Class14 class14) {
			this.class14.remove(class14);
			class14.setClass00(null);
		}

		public void removeClass15(Class15 class15) {
			this.class15.remove(class15);
			class15.setClass00(null);
		}

		public void removeClass16(Class16 class16) {
			this.class16.remove(class16);
			class16.setClass00(null);
		}

		public void removeClass17(Class17 class17) {
			this.class17.remove(class17);
			class17.setClass00(null);
		}

		public void removeClass18(Class18 class18) {
			this.class18.remove(class18);
			class18.setClass00(null);
		}

		public void removeClass19(Class19 class19) {
			this.class19.remove(class19);
			class19.setClass00(null);
		}

		public void removeClass20(Class20 class20) {
			this.class20.remove(class20);
			class20.setClass00(null);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass00(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass00(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass00(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass00(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass00(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass00(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass00(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass00(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass00(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass00(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass00(null);
		}

		public Set<Class01> getClass01() {
			return this.class01;
		}

		public Set<Class02> getClass02() {
			return this.class02;
		}

		public Set<Class03> getClass03() {
			return this.class03;
		}

		public Set<Class04> getClass04() {
			return this.class04;
		}

		public Set<Class05> getClass05() {
			return this.class05;
		}

		public Set<Class06> getClass06() {
			return this.class06;
		}

		public Set<Class07> getClass07() {
			return this.class07;
		}

		public Set<Class08> getClass08() {
			return this.class08;
		}

		public Set<Class09> getClass09() {
			return this.class09;
		}

		public Set<Class10> getClass10() {
			return this.class10;
		}

		public Set<Class11> getClass11() {
			return this.class11;
		}

		public Set<Class12> getClass12() {
			return this.class12;
		}

		public Set<Class13> getClass13() {
			return this.class13;
		}

		public Set<Class14> getClass14() {
			return this.class14;
		}

		public Set<Class15> getClass15() {
			return this.class15;
		}

		public Set<Class16> getClass16() {
			return this.class16;
		}

		public Set<Class17> getClass17() {
			return this.class17;
		}

		public Set<Class18> getClass18() {
			return this.class18;
		}

		public Set<Class19> getClass19() {
			return this.class19;
		}

		public Set<Class20> getClass20() {
			return this.class20;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass01(Set<Class01> class01) {

			if (this.class01 != null) {

				for (Class01 lclass01 : this.class01) {
					lclass01.setClass00(null);
				}
			}

			this.class01 = class01;

			for (Class01 lclass01 : class01) {
				lclass01.setClass00(this);
			}
		}

		public void setClass02(Set<Class02> class02) {

			if (this.class02 != null) {

				for (Class02 lclass02 : this.class02) {
					lclass02.setClass00(null);
				}
			}

			this.class02 = class02;

			for (Class02 lclass02 : class02) {
				lclass02.setClass00(this);
			}
		}

		public void setClass03(Set<Class03> class03) {

			if (this.class03 != null) {

				for (Class03 lclass03 : this.class03) {
					lclass03.setClass00(null);
				}
			}

			this.class03 = class03;

			for (Class03 lclass03 : class03) {
				lclass03.setClass00(this);
			}
		}

		public void setClass04(Set<Class04> class04) {

			if (this.class04 != null) {

				for (Class04 lclass04 : this.class04) {
					lclass04.setClass00(null);
				}
			}

			this.class04 = class04;

			for (Class04 lclass04 : class04) {
				lclass04.setClass00(this);
			}
		}

		public void setClass05(Set<Class05> class05) {

			if (this.class05 != null) {

				for (Class05 lclass05 : this.class05) {
					lclass05.setClass00(null);
				}
			}

			this.class05 = class05;

			for (Class05 lclass05 : class05) {
				lclass05.setClass00(this);
			}
		}

		public void setClass06(Set<Class06> class06) {

			if (this.class06 != null) {

				for (Class06 lclass06 : this.class06) {
					lclass06.setClass00(null);
				}
			}

			this.class06 = class06;

			for (Class06 lclass06 : class06) {
				lclass06.setClass00(this);
			}
		}

		public void setClass07(Set<Class07> class07) {

			if (this.class07 != null) {

				for (Class07 lclass07 : this.class07) {
					lclass07.setClass00(null);
				}
			}

			this.class07 = class07;

			for (Class07 lclass07 : class07) {
				lclass07.setClass00(this);
			}
		}

		public void setClass08(Set<Class08> class08) {

			if (this.class08 != null) {

				for (Class08 lclass08 : this.class08) {
					lclass08.setClass00(null);
				}
			}

			this.class08 = class08;

			for (Class08 lclass08 : class08) {
				lclass08.setClass00(this);
			}
		}

		public void setClass09(Set<Class09> class09) {

			if (this.class09 != null) {

				for (Class09 lclass09 : this.class09) {
					lclass09.setClass00(null);
				}
			}

			this.class09 = class09;

			for (Class09 lclass09 : class09) {
				lclass09.setClass00(this);
			}
		}

		public void setClass10(Set<Class10> class10) {

			if (this.class10 != null) {

				for (Class10 lclass10 : this.class10) {
					lclass10.setClass00(null);
				}
			}

			this.class10 = class10;

			for (Class10 lclass10 : class10) {
				lclass10.setClass00(this);
			}
		}

		public void setClass11(Set<Class11> class11) {

			if (this.class11 != null) {

				for (Class11 lclass11 : this.class11) {
					lclass11.setClass00(null);
				}
			}

			this.class11 = class11;

			for (Class11 lclass11 : class11) {
				lclass11.setClass00(this);
			}
		}

		public void setClass12(Set<Class12> class12) {

			if (this.class12 != null) {

				for (Class12 lclass12 : this.class12) {
					lclass12.setClass00(null);
				}
			}

			this.class12 = class12;

			for (Class12 lclass12 : class12) {
				lclass12.setClass00(this);
			}
		}

		public void setClass13(Set<Class13> class13) {

			if (this.class13 != null) {

				for (Class13 lclass13 : this.class13) {
					lclass13.setClass00(null);
				}
			}

			this.class13 = class13;

			for (Class13 lclass13 : class13) {
				lclass13.setClass00(this);
			}
		}

		public void setClass14(Set<Class14> class14) {

			if (this.class14 != null) {

				for (Class14 lclass14 : this.class14) {
					lclass14.setClass00(null);
				}
			}

			this.class14 = class14;

			for (Class14 lclass14 : class14) {
				lclass14.setClass00(this);
			}
		}

		public void setClass15(Set<Class15> class15) {

			if (this.class15 != null) {

				for (Class15 lclass15 : this.class15) {
					lclass15.setClass00(null);
				}
			}

			this.class15 = class15;

			for (Class15 lclass15 : class15) {
				lclass15.setClass00(this);
			}
		}

		public void setClass16(Set<Class16> class16) {

			if (this.class16 != null) {

				for (Class16 lclass16 : this.class16) {
					lclass16.setClass00(null);
				}
			}

			this.class16 = class16;

			for (Class16 lclass16 : class16) {
				lclass16.setClass00(this);
			}
		}

		public void setClass17(Set<Class17> class17) {

			if (this.class17 != null) {

				for (Class17 lclass17 : this.class17) {
					lclass17.setClass00(null);
				}
			}

			this.class17 = class17;

			for (Class17 lclass17 : class17) {
				lclass17.setClass00(this);
			}
		}

		public void setClass18(Set<Class18> class18) {

			if (this.class18 != null) {

				for (Class18 lclass18 : this.class18) {
					lclass18.setClass00(null);
				}
			}

			this.class18 = class18;

			for (Class18 lclass18 : class18) {
				lclass18.setClass00(this);
			}
		}

		public void setClass19(Set<Class19> class19) {

			if (this.class19 != null) {

				for (Class19 lclass19 : this.class19) {
					lclass19.setClass00(null);
				}
			}

			this.class19 = class19;

			for (Class19 lclass19 : class19) {
				lclass19.setClass00(this);
			}
		}

		public void setClass20(Set<Class20> class20) {

			if (this.class20 != null) {

				for (Class20 lclass20 : this.class20) {
					lclass20.setClass00(null);
				}
			}

			this.class20 = class20;

			for (Class20 lclass20 : class20) {
				lclass20.setClass00(this);
			}
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass00(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass00(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass00(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass00(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass00(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass00(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass00(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass00(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass00(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass00(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass00(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass00(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass00(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass00(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass00(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass00(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass00(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass00(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass00(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass00(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass00(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass00(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class01")
	public static class Class01 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class02.class
		)
		private Set<Class02>		class02;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class03.class
		)
		private Set<Class03>		class03;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class04.class
		)
		private Set<Class04>		class04;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class05.class
		)
		private Set<Class05>		class05;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class06.class
		)
		private Set<Class06>		class06;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class07.class
		)
		private Set<Class07>		class07;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class08.class
		)
		private Set<Class08>		class08;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class09.class
		)
		private Set<Class09>		class09;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class10.class
		)
		private Set<Class10>		class10;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class11.class
		)
		private Set<Class11>		class11;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class12.class
		)
		private Set<Class12>		class12;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class13.class
		)
		private Set<Class13>		class13;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class14.class
		)
		private Set<Class14>		class14;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class15.class
		)
		private Set<Class15>		class15;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class16.class
		)
		private Set<Class16>		class16;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class17.class
		)
		private Set<Class17>		class17;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class18.class
		)
		private Set<Class18>		class18;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class19.class
		)
		private Set<Class19>		class19;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class20.class
		)
		private Set<Class20>		class20;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class01",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class01() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass02(Class02 class02) {
			this.class02.add(class02);
			class02.setClass01(this);
		}

		public void addClass03(Class03 class03) {
			this.class03.add(class03);
			class03.setClass01(this);
		}

		public void addClass04(Class04 class04) {
			this.class04.add(class04);
			class04.setClass01(this);
		}

		public void addClass05(Class05 class05) {
			this.class05.add(class05);
			class05.setClass01(this);
		}

		public void addClass06(Class06 class06) {
			this.class06.add(class06);
			class06.setClass01(this);
		}

		public void addClass07(Class07 class07) {
			this.class07.add(class07);
			class07.setClass01(this);
		}

		public void addClass08(Class08 class08) {
			this.class08.add(class08);
			class08.setClass01(this);
		}

		public void addClass09(Class09 class09) {
			this.class09.add(class09);
			class09.setClass01(this);
		}

		public void addClass10(Class10 class10) {
			this.class10.add(class10);
			class10.setClass01(this);
		}

		public void addClass11(Class11 class11) {
			this.class11.add(class11);
			class11.setClass01(this);
		}

		public void addClass12(Class12 class12) {
			this.class12.add(class12);
			class12.setClass01(this);
		}

		public void addClass13(Class13 class13) {
			this.class13.add(class13);
			class13.setClass01(this);
		}

		public void addClass14(Class14 class14) {
			this.class14.add(class14);
			class14.setClass01(this);
		}

		public void addClass15(Class15 class15) {
			this.class15.add(class15);
			class15.setClass01(this);
		}

		public void addClass16(Class16 class16) {
			this.class16.add(class16);
			class16.setClass01(this);
		}

		public void addClass17(Class17 class17) {
			this.class17.add(class17);
			class17.setClass01(this);
		}

		public void addClass18(Class18 class18) {
			this.class18.add(class18);
			class18.setClass01(this);
		}

		public void addClass19(Class19 class19) {
			this.class19.add(class19);
			class19.setClass01(this);
		}

		public void addClass20(Class20 class20) {
			this.class20.add(class20);
			class20.setClass01(this);
		}

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass01(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass01(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass01(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass01(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass01(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass01(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass01(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass01(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass01(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass01(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass01(this);
		}

		public void removeClass02(Class02 class02) {
			this.class02.remove(class02);
			class02.setClass01(null);
		}

		public void removeClass03(Class03 class03) {
			this.class03.remove(class03);
			class03.setClass01(null);
		}

		public void removeClass04(Class04 class04) {
			this.class04.remove(class04);
			class04.setClass01(null);
		}

		public void removeClass05(Class05 class05) {
			this.class05.remove(class05);
			class05.setClass01(null);
		}

		public void removeClass06(Class06 class06) {
			this.class06.remove(class06);
			class06.setClass01(null);
		}

		public void removeClass07(Class07 class07) {
			this.class07.remove(class07);
			class07.setClass01(null);
		}

		public void removeClass08(Class08 class08) {
			this.class08.remove(class08);
			class08.setClass01(null);
		}

		public void removeClass09(Class09 class09) {
			this.class09.remove(class09);
			class09.setClass01(null);
		}

		public void removeClass10(Class10 class10) {
			this.class10.remove(class10);
			class10.setClass01(null);
		}

		public void removeClass11(Class11 class11) {
			this.class11.remove(class11);
			class11.setClass01(null);
		}

		public void removeClass12(Class12 class12) {
			this.class12.remove(class12);
			class12.setClass01(null);
		}

		public void removeClass13(Class13 class13) {
			this.class13.remove(class13);
			class13.setClass01(null);
		}

		public void removeClass14(Class14 class14) {
			this.class14.remove(class14);
			class14.setClass01(null);
		}

		public void removeClass15(Class15 class15) {
			this.class15.remove(class15);
			class15.setClass01(null);
		}

		public void removeClass16(Class16 class16) {
			this.class16.remove(class16);
			class16.setClass01(null);
		}

		public void removeClass17(Class17 class17) {
			this.class17.remove(class17);
			class17.setClass01(null);
		}

		public void removeClass18(Class18 class18) {
			this.class18.remove(class18);
			class18.setClass01(null);
		}

		public void removeClass19(Class19 class19) {
			this.class19.remove(class19);
			class19.setClass01(null);
		}

		public void removeClass20(Class20 class20) {
			this.class20.remove(class20);
			class20.setClass01(null);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass01(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass01(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass01(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass01(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass01(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass01(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass01(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass01(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass01(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass01(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass01(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Set<Class02> getClass02() {
			return this.class02;
		}

		public Set<Class03> getClass03() {
			return this.class03;
		}

		public Set<Class04> getClass04() {
			return this.class04;
		}

		public Set<Class05> getClass05() {
			return this.class05;
		}

		public Set<Class06> getClass06() {
			return this.class06;
		}

		public Set<Class07> getClass07() {
			return this.class07;
		}

		public Set<Class08> getClass08() {
			return this.class08;
		}

		public Set<Class09> getClass09() {
			return this.class09;
		}

		public Set<Class10> getClass10() {
			return this.class10;
		}

		public Set<Class11> getClass11() {
			return this.class11;
		}

		public Set<Class12> getClass12() {
			return this.class12;
		}

		public Set<Class13> getClass13() {
			return this.class13;
		}

		public Set<Class14> getClass14() {
			return this.class14;
		}

		public Set<Class15> getClass15() {
			return this.class15;
		}

		public Set<Class16> getClass16() {
			return this.class16;
		}

		public Set<Class17> getClass17() {
			return this.class17;
		}

		public Set<Class18> getClass18() {
			return this.class18;
		}

		public Set<Class19> getClass19() {
			return this.class19;
		}

		public Set<Class20> getClass20() {
			return this.class20;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass02(Set<Class02> class02) {

			if (this.class02 != null) {

				for (Class02 lclass02 : this.class02) {
					lclass02.setClass01(null);
				}
			}

			this.class02 = class02;

			for (Class02 lclass02 : class02) {
				lclass02.setClass01(this);
			}
		}

		public void setClass03(Set<Class03> class03) {

			if (this.class03 != null) {

				for (Class03 lclass03 : this.class03) {
					lclass03.setClass01(null);
				}
			}

			this.class03 = class03;

			for (Class03 lclass03 : class03) {
				lclass03.setClass01(this);
			}
		}

		public void setClass04(Set<Class04> class04) {

			if (this.class04 != null) {

				for (Class04 lclass04 : this.class04) {
					lclass04.setClass01(null);
				}
			}

			this.class04 = class04;

			for (Class04 lclass04 : class04) {
				lclass04.setClass01(this);
			}
		}

		public void setClass05(Set<Class05> class05) {

			if (this.class05 != null) {

				for (Class05 lclass05 : this.class05) {
					lclass05.setClass01(null);
				}
			}

			this.class05 = class05;

			for (Class05 lclass05 : class05) {
				lclass05.setClass01(this);
			}
		}

		public void setClass06(Set<Class06> class06) {

			if (this.class06 != null) {

				for (Class06 lclass06 : this.class06) {
					lclass06.setClass01(null);
				}
			}

			this.class06 = class06;

			for (Class06 lclass06 : class06) {
				lclass06.setClass01(this);
			}
		}

		public void setClass07(Set<Class07> class07) {

			if (this.class07 != null) {

				for (Class07 lclass07 : this.class07) {
					lclass07.setClass01(null);
				}
			}

			this.class07 = class07;

			for (Class07 lclass07 : class07) {
				lclass07.setClass01(this);
			}
		}

		public void setClass08(Set<Class08> class08) {

			if (this.class08 != null) {

				for (Class08 lclass08 : this.class08) {
					lclass08.setClass01(null);
				}
			}

			this.class08 = class08;

			for (Class08 lclass08 : class08) {
				lclass08.setClass01(this);
			}
		}

		public void setClass09(Set<Class09> class09) {

			if (this.class09 != null) {

				for (Class09 lclass09 : this.class09) {
					lclass09.setClass01(null);
				}
			}

			this.class09 = class09;

			for (Class09 lclass09 : class09) {
				lclass09.setClass01(this);
			}
		}

		public void setClass10(Set<Class10> class10) {

			if (this.class10 != null) {

				for (Class10 lclass10 : this.class10) {
					lclass10.setClass01(null);
				}
			}

			this.class10 = class10;

			for (Class10 lclass10 : class10) {
				lclass10.setClass01(this);
			}
		}

		public void setClass11(Set<Class11> class11) {

			if (this.class11 != null) {

				for (Class11 lclass11 : this.class11) {
					lclass11.setClass01(null);
				}
			}

			this.class11 = class11;

			for (Class11 lclass11 : class11) {
				lclass11.setClass01(this);
			}
		}

		public void setClass12(Set<Class12> class12) {

			if (this.class12 != null) {

				for (Class12 lclass12 : this.class12) {
					lclass12.setClass01(null);
				}
			}

			this.class12 = class12;

			for (Class12 lclass12 : class12) {
				lclass12.setClass01(this);
			}
		}

		public void setClass13(Set<Class13> class13) {

			if (this.class13 != null) {

				for (Class13 lclass13 : this.class13) {
					lclass13.setClass01(null);
				}
			}

			this.class13 = class13;

			for (Class13 lclass13 : class13) {
				lclass13.setClass01(this);
			}
		}

		public void setClass14(Set<Class14> class14) {

			if (this.class14 != null) {

				for (Class14 lclass14 : this.class14) {
					lclass14.setClass01(null);
				}
			}

			this.class14 = class14;

			for (Class14 lclass14 : class14) {
				lclass14.setClass01(this);
			}
		}

		public void setClass15(Set<Class15> class15) {

			if (this.class15 != null) {

				for (Class15 lclass15 : this.class15) {
					lclass15.setClass01(null);
				}
			}

			this.class15 = class15;

			for (Class15 lclass15 : class15) {
				lclass15.setClass01(this);
			}
		}

		public void setClass16(Set<Class16> class16) {

			if (this.class16 != null) {

				for (Class16 lclass16 : this.class16) {
					lclass16.setClass01(null);
				}
			}

			this.class16 = class16;

			for (Class16 lclass16 : class16) {
				lclass16.setClass01(this);
			}
		}

		public void setClass17(Set<Class17> class17) {

			if (this.class17 != null) {

				for (Class17 lclass17 : this.class17) {
					lclass17.setClass01(null);
				}
			}

			this.class17 = class17;

			for (Class17 lclass17 : class17) {
				lclass17.setClass01(this);
			}
		}

		public void setClass18(Set<Class18> class18) {

			if (this.class18 != null) {

				for (Class18 lclass18 : this.class18) {
					lclass18.setClass01(null);
				}
			}

			this.class18 = class18;

			for (Class18 lclass18 : class18) {
				lclass18.setClass01(this);
			}
		}

		public void setClass19(Set<Class19> class19) {

			if (this.class19 != null) {

				for (Class19 lclass19 : this.class19) {
					lclass19.setClass01(null);
				}
			}

			this.class19 = class19;

			for (Class19 lclass19 : class19) {
				lclass19.setClass01(this);
			}
		}

		public void setClass20(Set<Class20> class20) {

			if (this.class20 != null) {

				for (Class20 lclass20 : this.class20) {
					lclass20.setClass01(null);
				}
			}

			this.class20 = class20;

			for (Class20 lclass20 : class20) {
				lclass20.setClass01(this);
			}
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass01(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass01(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass01(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass01(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass01(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass01(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass01(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass01(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass01(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass01(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass01(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass01(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass01(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass01(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass01(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass01(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass01(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass01(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass01(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass01(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass01(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass01(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class02")
	public static class Class02 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class03.class
		)
		private Set<Class03>		class03;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class04.class
		)
		private Set<Class04>		class04;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class05.class
		)
		private Set<Class05>		class05;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class06.class
		)
		private Set<Class06>		class06;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class07.class
		)
		private Set<Class07>		class07;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class08.class
		)
		private Set<Class08>		class08;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class09.class
		)
		private Set<Class09>		class09;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class10.class
		)
		private Set<Class10>		class10;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class11.class
		)
		private Set<Class11>		class11;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class12.class
		)
		private Set<Class12>		class12;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class13.class
		)
		private Set<Class13>		class13;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class14.class
		)
		private Set<Class14>		class14;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class15.class
		)
		private Set<Class15>		class15;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class16.class
		)
		private Set<Class16>		class16;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class17.class
		)
		private Set<Class17>		class17;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class18.class
		)
		private Set<Class18>		class18;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class19.class
		)
		private Set<Class19>		class19;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class20.class
		)
		private Set<Class20>		class20;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class02",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class02() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass03(Class03 class03) {
			this.class03.add(class03);
			class03.setClass02(this);
		}

		public void addClass04(Class04 class04) {
			this.class04.add(class04);
			class04.setClass02(this);
		}

		public void addClass05(Class05 class05) {
			this.class05.add(class05);
			class05.setClass02(this);
		}

		public void addClass06(Class06 class06) {
			this.class06.add(class06);
			class06.setClass02(this);
		}

		public void addClass07(Class07 class07) {
			this.class07.add(class07);
			class07.setClass02(this);
		}

		public void addClass08(Class08 class08) {
			this.class08.add(class08);
			class08.setClass02(this);
		}

		public void addClass09(Class09 class09) {
			this.class09.add(class09);
			class09.setClass02(this);
		}

		public void addClass10(Class10 class10) {
			this.class10.add(class10);
			class10.setClass02(this);
		}

		public void addClass11(Class11 class11) {
			this.class11.add(class11);
			class11.setClass02(this);
		}

		public void addClass12(Class12 class12) {
			this.class12.add(class12);
			class12.setClass02(this);
		}

		public void addClass13(Class13 class13) {
			this.class13.add(class13);
			class13.setClass02(this);
		}

		public void addClass14(Class14 class14) {
			this.class14.add(class14);
			class14.setClass02(this);
		}

		public void addClass15(Class15 class15) {
			this.class15.add(class15);
			class15.setClass02(this);
		}

		public void addClass16(Class16 class16) {
			this.class16.add(class16);
			class16.setClass02(this);
		}

		public void addClass17(Class17 class17) {
			this.class17.add(class17);
			class17.setClass02(this);
		}

		public void addClass18(Class18 class18) {
			this.class18.add(class18);
			class18.setClass02(this);
		}

		public void addClass19(Class19 class19) {
			this.class19.add(class19);
			class19.setClass02(this);
		}

		public void addClass20(Class20 class20) {
			this.class20.add(class20);
			class20.setClass02(this);
		}

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass02(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass02(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass02(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass02(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass02(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass02(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass02(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass02(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass02(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass02(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass02(this);
		}

		public void removeClass03(Class03 class03) {
			this.class03.remove(class03);
			class03.setClass02(null);
		}

		public void removeClass04(Class04 class04) {
			this.class04.remove(class04);
			class04.setClass02(null);
		}

		public void removeClass05(Class05 class05) {
			this.class05.remove(class05);
			class05.setClass02(null);
		}

		public void removeClass06(Class06 class06) {
			this.class06.remove(class06);
			class06.setClass02(null);
		}

		public void removeClass07(Class07 class07) {
			this.class07.remove(class07);
			class07.setClass02(null);
		}

		public void removeClass08(Class08 class08) {
			this.class08.remove(class08);
			class08.setClass02(null);
		}

		public void removeClass09(Class09 class09) {
			this.class09.remove(class09);
			class09.setClass02(null);
		}

		public void removeClass10(Class10 class10) {
			this.class10.remove(class10);
			class10.setClass02(null);
		}

		public void removeClass11(Class11 class11) {
			this.class11.remove(class11);
			class11.setClass02(null);
		}

		public void removeClass12(Class12 class12) {
			this.class12.remove(class12);
			class12.setClass02(null);
		}

		public void removeClass13(Class13 class13) {
			this.class13.remove(class13);
			class13.setClass02(null);
		}

		public void removeClass14(Class14 class14) {
			this.class14.remove(class14);
			class14.setClass02(null);
		}

		public void removeClass15(Class15 class15) {
			this.class15.remove(class15);
			class15.setClass02(null);
		}

		public void removeClass16(Class16 class16) {
			this.class16.remove(class16);
			class16.setClass02(null);
		}

		public void removeClass17(Class17 class17) {
			this.class17.remove(class17);
			class17.setClass02(null);
		}

		public void removeClass18(Class18 class18) {
			this.class18.remove(class18);
			class18.setClass02(null);
		}

		public void removeClass19(Class19 class19) {
			this.class19.remove(class19);
			class19.setClass02(null);
		}

		public void removeClass20(Class20 class20) {
			this.class20.remove(class20);
			class20.setClass02(null);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass02(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass02(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass02(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass02(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass02(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass02(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass02(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass02(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass02(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass02(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass02(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Set<Class03> getClass03() {
			return this.class03;
		}

		public Set<Class04> getClass04() {
			return this.class04;
		}

		public Set<Class05> getClass05() {
			return this.class05;
		}

		public Set<Class06> getClass06() {
			return this.class06;
		}

		public Set<Class07> getClass07() {
			return this.class07;
		}

		public Set<Class08> getClass08() {
			return this.class08;
		}

		public Set<Class09> getClass09() {
			return this.class09;
		}

		public Set<Class10> getClass10() {
			return this.class10;
		}

		public Set<Class11> getClass11() {
			return this.class11;
		}

		public Set<Class12> getClass12() {
			return this.class12;
		}

		public Set<Class13> getClass13() {
			return this.class13;
		}

		public Set<Class14> getClass14() {
			return this.class14;
		}

		public Set<Class15> getClass15() {
			return this.class15;
		}

		public Set<Class16> getClass16() {
			return this.class16;
		}

		public Set<Class17> getClass17() {
			return this.class17;
		}

		public Set<Class18> getClass18() {
			return this.class18;
		}

		public Set<Class19> getClass19() {
			return this.class19;
		}

		public Set<Class20> getClass20() {
			return this.class20;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass03(Set<Class03> class03) {

			if (this.class03 != null) {

				for (Class03 lclass03 : this.class03) {
					lclass03.setClass02(null);
				}
			}

			this.class03 = class03;

			for (Class03 lclass03 : class03) {
				lclass03.setClass02(this);
			}
		}

		public void setClass04(Set<Class04> class04) {

			if (this.class04 != null) {

				for (Class04 lclass04 : this.class04) {
					lclass04.setClass02(null);
				}
			}

			this.class04 = class04;

			for (Class04 lclass04 : class04) {
				lclass04.setClass02(this);
			}
		}

		public void setClass05(Set<Class05> class05) {

			if (this.class05 != null) {

				for (Class05 lclass05 : this.class05) {
					lclass05.setClass02(null);
				}
			}

			this.class05 = class05;

			for (Class05 lclass05 : class05) {
				lclass05.setClass02(this);
			}
		}

		public void setClass06(Set<Class06> class06) {

			if (this.class06 != null) {

				for (Class06 lclass06 : this.class06) {
					lclass06.setClass02(null);
				}
			}

			this.class06 = class06;

			for (Class06 lclass06 : class06) {
				lclass06.setClass02(this);
			}
		}

		public void setClass07(Set<Class07> class07) {

			if (this.class07 != null) {

				for (Class07 lclass07 : this.class07) {
					lclass07.setClass02(null);
				}
			}

			this.class07 = class07;

			for (Class07 lclass07 : class07) {
				lclass07.setClass02(this);
			}
		}

		public void setClass08(Set<Class08> class08) {

			if (this.class08 != null) {

				for (Class08 lclass08 : this.class08) {
					lclass08.setClass02(null);
				}
			}

			this.class08 = class08;

			for (Class08 lclass08 : class08) {
				lclass08.setClass02(this);
			}
		}

		public void setClass09(Set<Class09> class09) {

			if (this.class09 != null) {

				for (Class09 lclass09 : this.class09) {
					lclass09.setClass02(null);
				}
			}

			this.class09 = class09;

			for (Class09 lclass09 : class09) {
				lclass09.setClass02(this);
			}
		}

		public void setClass10(Set<Class10> class10) {

			if (this.class10 != null) {

				for (Class10 lclass10 : this.class10) {
					lclass10.setClass02(null);
				}
			}

			this.class10 = class10;

			for (Class10 lclass10 : class10) {
				lclass10.setClass02(this);
			}
		}

		public void setClass11(Set<Class11> class11) {

			if (this.class11 != null) {

				for (Class11 lclass11 : this.class11) {
					lclass11.setClass02(null);
				}
			}

			this.class11 = class11;

			for (Class11 lclass11 : class11) {
				lclass11.setClass02(this);
			}
		}

		public void setClass12(Set<Class12> class12) {

			if (this.class12 != null) {

				for (Class12 lclass12 : this.class12) {
					lclass12.setClass02(null);
				}
			}

			this.class12 = class12;

			for (Class12 lclass12 : class12) {
				lclass12.setClass02(this);
			}
		}

		public void setClass13(Set<Class13> class13) {

			if (this.class13 != null) {

				for (Class13 lclass13 : this.class13) {
					lclass13.setClass02(null);
				}
			}

			this.class13 = class13;

			for (Class13 lclass13 : class13) {
				lclass13.setClass02(this);
			}
		}

		public void setClass14(Set<Class14> class14) {

			if (this.class14 != null) {

				for (Class14 lclass14 : this.class14) {
					lclass14.setClass02(null);
				}
			}

			this.class14 = class14;

			for (Class14 lclass14 : class14) {
				lclass14.setClass02(this);
			}
		}

		public void setClass15(Set<Class15> class15) {

			if (this.class15 != null) {

				for (Class15 lclass15 : this.class15) {
					lclass15.setClass02(null);
				}
			}

			this.class15 = class15;

			for (Class15 lclass15 : class15) {
				lclass15.setClass02(this);
			}
		}

		public void setClass16(Set<Class16> class16) {

			if (this.class16 != null) {

				for (Class16 lclass16 : this.class16) {
					lclass16.setClass02(null);
				}
			}

			this.class16 = class16;

			for (Class16 lclass16 : class16) {
				lclass16.setClass02(this);
			}
		}

		public void setClass17(Set<Class17> class17) {

			if (this.class17 != null) {

				for (Class17 lclass17 : this.class17) {
					lclass17.setClass02(null);
				}
			}

			this.class17 = class17;

			for (Class17 lclass17 : class17) {
				lclass17.setClass02(this);
			}
		}

		public void setClass18(Set<Class18> class18) {

			if (this.class18 != null) {

				for (Class18 lclass18 : this.class18) {
					lclass18.setClass02(null);
				}
			}

			this.class18 = class18;

			for (Class18 lclass18 : class18) {
				lclass18.setClass02(this);
			}
		}

		public void setClass19(Set<Class19> class19) {

			if (this.class19 != null) {

				for (Class19 lclass19 : this.class19) {
					lclass19.setClass02(null);
				}
			}

			this.class19 = class19;

			for (Class19 lclass19 : class19) {
				lclass19.setClass02(this);
			}
		}

		public void setClass20(Set<Class20> class20) {

			if (this.class20 != null) {

				for (Class20 lclass20 : this.class20) {
					lclass20.setClass02(null);
				}
			}

			this.class20 = class20;

			for (Class20 lclass20 : class20) {
				lclass20.setClass02(this);
			}
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass02(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass02(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass02(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass02(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass02(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass02(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass02(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass02(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass02(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass02(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass02(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass02(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass02(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass02(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass02(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass02(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass02(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass02(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass02(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass02(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass02(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass02(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class03")
	public static class Class03 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class04.class
		)
		private Set<Class04>		class04;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class05.class
		)
		private Set<Class05>		class05;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class06.class
		)
		private Set<Class06>		class06;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class07.class
		)
		private Set<Class07>		class07;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class08.class
		)
		private Set<Class08>		class08;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class09.class
		)
		private Set<Class09>		class09;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class10.class
		)
		private Set<Class10>		class10;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class11.class
		)
		private Set<Class11>		class11;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class12.class
		)
		private Set<Class12>		class12;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class13.class
		)
		private Set<Class13>		class13;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class14.class
		)
		private Set<Class14>		class14;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class15.class
		)
		private Set<Class15>		class15;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class16.class
		)
		private Set<Class16>		class16;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class17.class
		)
		private Set<Class17>		class17;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class18.class
		)
		private Set<Class18>		class18;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class19.class
		)
		private Set<Class19>		class19;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class20.class
		)
		private Set<Class20>		class20;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class03",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class03() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass04(Class04 class04) {
			this.class04.add(class04);
			class04.setClass03(this);
		}

		public void addClass05(Class05 class05) {
			this.class05.add(class05);
			class05.setClass03(this);
		}

		public void addClass06(Class06 class06) {
			this.class06.add(class06);
			class06.setClass03(this);
		}

		public void addClass07(Class07 class07) {
			this.class07.add(class07);
			class07.setClass03(this);
		}

		public void addClass08(Class08 class08) {
			this.class08.add(class08);
			class08.setClass03(this);
		}

		public void addClass09(Class09 class09) {
			this.class09.add(class09);
			class09.setClass03(this);
		}

		public void addClass10(Class10 class10) {
			this.class10.add(class10);
			class10.setClass03(this);
		}

		public void addClass11(Class11 class11) {
			this.class11.add(class11);
			class11.setClass03(this);
		}

		public void addClass12(Class12 class12) {
			this.class12.add(class12);
			class12.setClass03(this);
		}

		public void addClass13(Class13 class13) {
			this.class13.add(class13);
			class13.setClass03(this);
		}

		public void addClass14(Class14 class14) {
			this.class14.add(class14);
			class14.setClass03(this);
		}

		public void addClass15(Class15 class15) {
			this.class15.add(class15);
			class15.setClass03(this);
		}

		public void addClass16(Class16 class16) {
			this.class16.add(class16);
			class16.setClass03(this);
		}

		public void addClass17(Class17 class17) {
			this.class17.add(class17);
			class17.setClass03(this);
		}

		public void addClass18(Class18 class18) {
			this.class18.add(class18);
			class18.setClass03(this);
		}

		public void addClass19(Class19 class19) {
			this.class19.add(class19);
			class19.setClass03(this);
		}

		public void addClass20(Class20 class20) {
			this.class20.add(class20);
			class20.setClass03(this);
		}

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass03(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass03(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass03(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass03(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass03(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass03(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass03(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass03(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass03(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass03(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass03(this);
		}

		public void removeClass04(Class04 class04) {
			this.class04.remove(class04);
			class04.setClass03(null);
		}

		public void removeClass05(Class05 class05) {
			this.class05.remove(class05);
			class05.setClass03(null);
		}

		public void removeClass06(Class06 class06) {
			this.class06.remove(class06);
			class06.setClass03(null);
		}

		public void removeClass07(Class07 class07) {
			this.class07.remove(class07);
			class07.setClass03(null);
		}

		public void removeClass08(Class08 class08) {
			this.class08.remove(class08);
			class08.setClass03(null);
		}

		public void removeClass09(Class09 class09) {
			this.class09.remove(class09);
			class09.setClass03(null);
		}

		public void removeClass10(Class10 class10) {
			this.class10.remove(class10);
			class10.setClass03(null);
		}

		public void removeClass11(Class11 class11) {
			this.class11.remove(class11);
			class11.setClass03(null);
		}

		public void removeClass12(Class12 class12) {
			this.class12.remove(class12);
			class12.setClass03(null);
		}

		public void removeClass13(Class13 class13) {
			this.class13.remove(class13);
			class13.setClass03(null);
		}

		public void removeClass14(Class14 class14) {
			this.class14.remove(class14);
			class14.setClass03(null);
		}

		public void removeClass15(Class15 class15) {
			this.class15.remove(class15);
			class15.setClass03(null);
		}

		public void removeClass16(Class16 class16) {
			this.class16.remove(class16);
			class16.setClass03(null);
		}

		public void removeClass17(Class17 class17) {
			this.class17.remove(class17);
			class17.setClass03(null);
		}

		public void removeClass18(Class18 class18) {
			this.class18.remove(class18);
			class18.setClass03(null);
		}

		public void removeClass19(Class19 class19) {
			this.class19.remove(class19);
			class19.setClass03(null);
		}

		public void removeClass20(Class20 class20) {
			this.class20.remove(class20);
			class20.setClass03(null);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass03(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass03(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass03(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass03(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass03(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass03(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass03(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass03(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass03(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass03(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass03(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Set<Class04> getClass04() {
			return this.class04;
		}

		public Set<Class05> getClass05() {
			return this.class05;
		}

		public Set<Class06> getClass06() {
			return this.class06;
		}

		public Set<Class07> getClass07() {
			return this.class07;
		}

		public Set<Class08> getClass08() {
			return this.class08;
		}

		public Set<Class09> getClass09() {
			return this.class09;
		}

		public Set<Class10> getClass10() {
			return this.class10;
		}

		public Set<Class11> getClass11() {
			return this.class11;
		}

		public Set<Class12> getClass12() {
			return this.class12;
		}

		public Set<Class13> getClass13() {
			return this.class13;
		}

		public Set<Class14> getClass14() {
			return this.class14;
		}

		public Set<Class15> getClass15() {
			return this.class15;
		}

		public Set<Class16> getClass16() {
			return this.class16;
		}

		public Set<Class17> getClass17() {
			return this.class17;
		}

		public Set<Class18> getClass18() {
			return this.class18;
		}

		public Set<Class19> getClass19() {
			return this.class19;
		}

		public Set<Class20> getClass20() {
			return this.class20;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass04(Set<Class04> class04) {

			if (this.class04 != null) {

				for (Class04 lclass04 : this.class04) {
					lclass04.setClass03(null);
				}
			}

			this.class04 = class04;

			for (Class04 lclass04 : class04) {
				lclass04.setClass03(this);
			}
		}

		public void setClass05(Set<Class05> class05) {

			if (this.class05 != null) {

				for (Class05 lclass05 : this.class05) {
					lclass05.setClass03(null);
				}
			}

			this.class05 = class05;

			for (Class05 lclass05 : class05) {
				lclass05.setClass03(this);
			}
		}

		public void setClass06(Set<Class06> class06) {

			if (this.class06 != null) {

				for (Class06 lclass06 : this.class06) {
					lclass06.setClass03(null);
				}
			}

			this.class06 = class06;

			for (Class06 lclass06 : class06) {
				lclass06.setClass03(this);
			}
		}

		public void setClass07(Set<Class07> class07) {

			if (this.class07 != null) {

				for (Class07 lclass07 : this.class07) {
					lclass07.setClass03(null);
				}
			}

			this.class07 = class07;

			for (Class07 lclass07 : class07) {
				lclass07.setClass03(this);
			}
		}

		public void setClass08(Set<Class08> class08) {

			if (this.class08 != null) {

				for (Class08 lclass08 : this.class08) {
					lclass08.setClass03(null);
				}
			}

			this.class08 = class08;

			for (Class08 lclass08 : class08) {
				lclass08.setClass03(this);
			}
		}

		public void setClass09(Set<Class09> class09) {

			if (this.class09 != null) {

				for (Class09 lclass09 : this.class09) {
					lclass09.setClass03(null);
				}
			}

			this.class09 = class09;

			for (Class09 lclass09 : class09) {
				lclass09.setClass03(this);
			}
		}

		public void setClass10(Set<Class10> class10) {

			if (this.class10 != null) {

				for (Class10 lclass10 : this.class10) {
					lclass10.setClass03(null);
				}
			}

			this.class10 = class10;

			for (Class10 lclass10 : class10) {
				lclass10.setClass03(this);
			}
		}

		public void setClass11(Set<Class11> class11) {

			if (this.class11 != null) {

				for (Class11 lclass11 : this.class11) {
					lclass11.setClass03(null);
				}
			}

			this.class11 = class11;

			for (Class11 lclass11 : class11) {
				lclass11.setClass03(this);
			}
		}

		public void setClass12(Set<Class12> class12) {

			if (this.class12 != null) {

				for (Class12 lclass12 : this.class12) {
					lclass12.setClass03(null);
				}
			}

			this.class12 = class12;

			for (Class12 lclass12 : class12) {
				lclass12.setClass03(this);
			}
		}

		public void setClass13(Set<Class13> class13) {

			if (this.class13 != null) {

				for (Class13 lclass13 : this.class13) {
					lclass13.setClass03(null);
				}
			}

			this.class13 = class13;

			for (Class13 lclass13 : class13) {
				lclass13.setClass03(this);
			}
		}

		public void setClass14(Set<Class14> class14) {

			if (this.class14 != null) {

				for (Class14 lclass14 : this.class14) {
					lclass14.setClass03(null);
				}
			}

			this.class14 = class14;

			for (Class14 lclass14 : class14) {
				lclass14.setClass03(this);
			}
		}

		public void setClass15(Set<Class15> class15) {

			if (this.class15 != null) {

				for (Class15 lclass15 : this.class15) {
					lclass15.setClass03(null);
				}
			}

			this.class15 = class15;

			for (Class15 lclass15 : class15) {
				lclass15.setClass03(this);
			}
		}

		public void setClass16(Set<Class16> class16) {

			if (this.class16 != null) {

				for (Class16 lclass16 : this.class16) {
					lclass16.setClass03(null);
				}
			}

			this.class16 = class16;

			for (Class16 lclass16 : class16) {
				lclass16.setClass03(this);
			}
		}

		public void setClass17(Set<Class17> class17) {

			if (this.class17 != null) {

				for (Class17 lclass17 : this.class17) {
					lclass17.setClass03(null);
				}
			}

			this.class17 = class17;

			for (Class17 lclass17 : class17) {
				lclass17.setClass03(this);
			}
		}

		public void setClass18(Set<Class18> class18) {

			if (this.class18 != null) {

				for (Class18 lclass18 : this.class18) {
					lclass18.setClass03(null);
				}
			}

			this.class18 = class18;

			for (Class18 lclass18 : class18) {
				lclass18.setClass03(this);
			}
		}

		public void setClass19(Set<Class19> class19) {

			if (this.class19 != null) {

				for (Class19 lclass19 : this.class19) {
					lclass19.setClass03(null);
				}
			}

			this.class19 = class19;

			for (Class19 lclass19 : class19) {
				lclass19.setClass03(this);
			}
		}

		public void setClass20(Set<Class20> class20) {

			if (this.class20 != null) {

				for (Class20 lclass20 : this.class20) {
					lclass20.setClass03(null);
				}
			}

			this.class20 = class20;

			for (Class20 lclass20 : class20) {
				lclass20.setClass03(this);
			}
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass03(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass03(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass03(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass03(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass03(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass03(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass03(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass03(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass03(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass03(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass03(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass03(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass03(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass03(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass03(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass03(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass03(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass03(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass03(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass03(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass03(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass03(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class04")
	public static class Class04 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class05.class
		)
		private Set<Class05>		class05;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class06.class
		)
		private Set<Class06>		class06;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class07.class
		)
		private Set<Class07>		class07;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class08.class
		)
		private Set<Class08>		class08;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class09.class
		)
		private Set<Class09>		class09;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class10.class
		)
		private Set<Class10>		class10;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class11.class
		)
		private Set<Class11>		class11;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class12.class
		)
		private Set<Class12>		class12;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class13.class
		)
		private Set<Class13>		class13;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class14.class
		)
		private Set<Class14>		class14;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class15.class
		)
		private Set<Class15>		class15;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class16.class
		)
		private Set<Class16>		class16;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class17.class
		)
		private Set<Class17>		class17;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class18.class
		)
		private Set<Class18>		class18;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class19.class
		)
		private Set<Class19>		class19;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class20.class
		)
		private Set<Class20>		class20;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class04",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class04() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass05(Class05 class05) {
			this.class05.add(class05);
			class05.setClass04(this);
		}

		public void addClass06(Class06 class06) {
			this.class06.add(class06);
			class06.setClass04(this);
		}

		public void addClass07(Class07 class07) {
			this.class07.add(class07);
			class07.setClass04(this);
		}

		public void addClass08(Class08 class08) {
			this.class08.add(class08);
			class08.setClass04(this);
		}

		public void addClass09(Class09 class09) {
			this.class09.add(class09);
			class09.setClass04(this);
		}

		public void addClass10(Class10 class10) {
			this.class10.add(class10);
			class10.setClass04(this);
		}

		public void addClass11(Class11 class11) {
			this.class11.add(class11);
			class11.setClass04(this);
		}

		public void addClass12(Class12 class12) {
			this.class12.add(class12);
			class12.setClass04(this);
		}

		public void addClass13(Class13 class13) {
			this.class13.add(class13);
			class13.setClass04(this);
		}

		public void addClass14(Class14 class14) {
			this.class14.add(class14);
			class14.setClass04(this);
		}

		public void addClass15(Class15 class15) {
			this.class15.add(class15);
			class15.setClass04(this);
		}

		public void addClass16(Class16 class16) {
			this.class16.add(class16);
			class16.setClass04(this);
		}

		public void addClass17(Class17 class17) {
			this.class17.add(class17);
			class17.setClass04(this);
		}

		public void addClass18(Class18 class18) {
			this.class18.add(class18);
			class18.setClass04(this);
		}

		public void addClass19(Class19 class19) {
			this.class19.add(class19);
			class19.setClass04(this);
		}

		public void addClass20(Class20 class20) {
			this.class20.add(class20);
			class20.setClass04(this);
		}

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass04(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass04(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass04(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass04(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass04(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass04(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass04(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass04(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass04(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass04(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass04(this);
		}

		public void removeClass05(Class05 class05) {
			this.class05.remove(class05);
			class05.setClass04(null);
		}

		public void removeClass06(Class06 class06) {
			this.class06.remove(class06);
			class06.setClass04(null);
		}

		public void removeClass07(Class07 class07) {
			this.class07.remove(class07);
			class07.setClass04(null);
		}

		public void removeClass08(Class08 class08) {
			this.class08.remove(class08);
			class08.setClass04(null);
		}

		public void removeClass09(Class09 class09) {
			this.class09.remove(class09);
			class09.setClass04(null);
		}

		public void removeClass10(Class10 class10) {
			this.class10.remove(class10);
			class10.setClass04(null);
		}

		public void removeClass11(Class11 class11) {
			this.class11.remove(class11);
			class11.setClass04(null);
		}

		public void removeClass12(Class12 class12) {
			this.class12.remove(class12);
			class12.setClass04(null);
		}

		public void removeClass13(Class13 class13) {
			this.class13.remove(class13);
			class13.setClass04(null);
		}

		public void removeClass14(Class14 class14) {
			this.class14.remove(class14);
			class14.setClass04(null);
		}

		public void removeClass15(Class15 class15) {
			this.class15.remove(class15);
			class15.setClass04(null);
		}

		public void removeClass16(Class16 class16) {
			this.class16.remove(class16);
			class16.setClass04(null);
		}

		public void removeClass17(Class17 class17) {
			this.class17.remove(class17);
			class17.setClass04(null);
		}

		public void removeClass18(Class18 class18) {
			this.class18.remove(class18);
			class18.setClass04(null);
		}

		public void removeClass19(Class19 class19) {
			this.class19.remove(class19);
			class19.setClass04(null);
		}

		public void removeClass20(Class20 class20) {
			this.class20.remove(class20);
			class20.setClass04(null);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass04(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass04(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass04(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass04(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass04(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass04(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass04(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass04(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass04(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass04(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass04(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Set<Class05> getClass05() {
			return this.class05;
		}

		public Set<Class06> getClass06() {
			return this.class06;
		}

		public Set<Class07> getClass07() {
			return this.class07;
		}

		public Set<Class08> getClass08() {
			return this.class08;
		}

		public Set<Class09> getClass09() {
			return this.class09;
		}

		public Set<Class10> getClass10() {
			return this.class10;
		}

		public Set<Class11> getClass11() {
			return this.class11;
		}

		public Set<Class12> getClass12() {
			return this.class12;
		}

		public Set<Class13> getClass13() {
			return this.class13;
		}

		public Set<Class14> getClass14() {
			return this.class14;
		}

		public Set<Class15> getClass15() {
			return this.class15;
		}

		public Set<Class16> getClass16() {
			return this.class16;
		}

		public Set<Class17> getClass17() {
			return this.class17;
		}

		public Set<Class18> getClass18() {
			return this.class18;
		}

		public Set<Class19> getClass19() {
			return this.class19;
		}

		public Set<Class20> getClass20() {
			return this.class20;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass05(Set<Class05> class05) {

			if (this.class05 != null) {

				for (Class05 lclass05 : this.class05) {
					lclass05.setClass04(null);
				}
			}

			this.class05 = class05;

			for (Class05 lclass05 : class05) {
				lclass05.setClass04(this);
			}
		}

		public void setClass06(Set<Class06> class06) {

			if (this.class06 != null) {

				for (Class06 lclass06 : this.class06) {
					lclass06.setClass04(null);
				}
			}

			this.class06 = class06;

			for (Class06 lclass06 : class06) {
				lclass06.setClass04(this);
			}
		}

		public void setClass07(Set<Class07> class07) {

			if (this.class07 != null) {

				for (Class07 lclass07 : this.class07) {
					lclass07.setClass04(null);
				}
			}

			this.class07 = class07;

			for (Class07 lclass07 : class07) {
				lclass07.setClass04(this);
			}
		}

		public void setClass08(Set<Class08> class08) {

			if (this.class08 != null) {

				for (Class08 lclass08 : this.class08) {
					lclass08.setClass04(null);
				}
			}

			this.class08 = class08;

			for (Class08 lclass08 : class08) {
				lclass08.setClass04(this);
			}
		}

		public void setClass09(Set<Class09> class09) {

			if (this.class09 != null) {

				for (Class09 lclass09 : this.class09) {
					lclass09.setClass04(null);
				}
			}

			this.class09 = class09;

			for (Class09 lclass09 : class09) {
				lclass09.setClass04(this);
			}
		}

		public void setClass10(Set<Class10> class10) {

			if (this.class10 != null) {

				for (Class10 lclass10 : this.class10) {
					lclass10.setClass04(null);
				}
			}

			this.class10 = class10;

			for (Class10 lclass10 : class10) {
				lclass10.setClass04(this);
			}
		}

		public void setClass11(Set<Class11> class11) {

			if (this.class11 != null) {

				for (Class11 lclass11 : this.class11) {
					lclass11.setClass04(null);
				}
			}

			this.class11 = class11;

			for (Class11 lclass11 : class11) {
				lclass11.setClass04(this);
			}
		}

		public void setClass12(Set<Class12> class12) {

			if (this.class12 != null) {

				for (Class12 lclass12 : this.class12) {
					lclass12.setClass04(null);
				}
			}

			this.class12 = class12;

			for (Class12 lclass12 : class12) {
				lclass12.setClass04(this);
			}
		}

		public void setClass13(Set<Class13> class13) {

			if (this.class13 != null) {

				for (Class13 lclass13 : this.class13) {
					lclass13.setClass04(null);
				}
			}

			this.class13 = class13;

			for (Class13 lclass13 : class13) {
				lclass13.setClass04(this);
			}
		}

		public void setClass14(Set<Class14> class14) {

			if (this.class14 != null) {

				for (Class14 lclass14 : this.class14) {
					lclass14.setClass04(null);
				}
			}

			this.class14 = class14;

			for (Class14 lclass14 : class14) {
				lclass14.setClass04(this);
			}
		}

		public void setClass15(Set<Class15> class15) {

			if (this.class15 != null) {

				for (Class15 lclass15 : this.class15) {
					lclass15.setClass04(null);
				}
			}

			this.class15 = class15;

			for (Class15 lclass15 : class15) {
				lclass15.setClass04(this);
			}
		}

		public void setClass16(Set<Class16> class16) {

			if (this.class16 != null) {

				for (Class16 lclass16 : this.class16) {
					lclass16.setClass04(null);
				}
			}

			this.class16 = class16;

			for (Class16 lclass16 : class16) {
				lclass16.setClass04(this);
			}
		}

		public void setClass17(Set<Class17> class17) {

			if (this.class17 != null) {

				for (Class17 lclass17 : this.class17) {
					lclass17.setClass04(null);
				}
			}

			this.class17 = class17;

			for (Class17 lclass17 : class17) {
				lclass17.setClass04(this);
			}
		}

		public void setClass18(Set<Class18> class18) {

			if (this.class18 != null) {

				for (Class18 lclass18 : this.class18) {
					lclass18.setClass04(null);
				}
			}

			this.class18 = class18;

			for (Class18 lclass18 : class18) {
				lclass18.setClass04(this);
			}
		}

		public void setClass19(Set<Class19> class19) {

			if (this.class19 != null) {

				for (Class19 lclass19 : this.class19) {
					lclass19.setClass04(null);
				}
			}

			this.class19 = class19;

			for (Class19 lclass19 : class19) {
				lclass19.setClass04(this);
			}
		}

		public void setClass20(Set<Class20> class20) {

			if (this.class20 != null) {

				for (Class20 lclass20 : this.class20) {
					lclass20.setClass04(null);
				}
			}

			this.class20 = class20;

			for (Class20 lclass20 : class20) {
				lclass20.setClass04(this);
			}
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass04(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass04(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass04(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass04(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass04(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass04(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass04(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass04(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass04(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass04(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass04(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass04(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass04(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass04(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass04(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass04(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass04(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass04(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass04(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass04(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass04(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass04(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class05")
	public static class Class05 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class06.class
		)
		private Set<Class06>		class06;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class07.class
		)
		private Set<Class07>		class07;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class08.class
		)
		private Set<Class08>		class08;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class09.class
		)
		private Set<Class09>		class09;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class10.class
		)
		private Set<Class10>		class10;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class11.class
		)
		private Set<Class11>		class11;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class12.class
		)
		private Set<Class12>		class12;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class13.class
		)
		private Set<Class13>		class13;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class14.class
		)
		private Set<Class14>		class14;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class15.class
		)
		private Set<Class15>		class15;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class16.class
		)
		private Set<Class16>		class16;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class17.class
		)
		private Set<Class17>		class17;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class18.class
		)
		private Set<Class18>		class18;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class19.class
		)
		private Set<Class19>		class19;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class20.class
		)
		private Set<Class20>		class20;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class05",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class05() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass06(Class06 class06) {
			this.class06.add(class06);
			class06.setClass05(this);
		}

		public void addClass07(Class07 class07) {
			this.class07.add(class07);
			class07.setClass05(this);
		}

		public void addClass08(Class08 class08) {
			this.class08.add(class08);
			class08.setClass05(this);
		}

		public void addClass09(Class09 class09) {
			this.class09.add(class09);
			class09.setClass05(this);
		}

		public void addClass10(Class10 class10) {
			this.class10.add(class10);
			class10.setClass05(this);
		}

		public void addClass11(Class11 class11) {
			this.class11.add(class11);
			class11.setClass05(this);
		}

		public void addClass12(Class12 class12) {
			this.class12.add(class12);
			class12.setClass05(this);
		}

		public void addClass13(Class13 class13) {
			this.class13.add(class13);
			class13.setClass05(this);
		}

		public void addClass14(Class14 class14) {
			this.class14.add(class14);
			class14.setClass05(this);
		}

		public void addClass15(Class15 class15) {
			this.class15.add(class15);
			class15.setClass05(this);
		}

		public void addClass16(Class16 class16) {
			this.class16.add(class16);
			class16.setClass05(this);
		}

		public void addClass17(Class17 class17) {
			this.class17.add(class17);
			class17.setClass05(this);
		}

		public void addClass18(Class18 class18) {
			this.class18.add(class18);
			class18.setClass05(this);
		}

		public void addClass19(Class19 class19) {
			this.class19.add(class19);
			class19.setClass05(this);
		}

		public void addClass20(Class20 class20) {
			this.class20.add(class20);
			class20.setClass05(this);
		}

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass05(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass05(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass05(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass05(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass05(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass05(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass05(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass05(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass05(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass05(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass05(this);
		}

		public void removeClass06(Class06 class06) {
			this.class06.remove(class06);
			class06.setClass05(null);
		}

		public void removeClass07(Class07 class07) {
			this.class07.remove(class07);
			class07.setClass05(null);
		}

		public void removeClass08(Class08 class08) {
			this.class08.remove(class08);
			class08.setClass05(null);
		}

		public void removeClass09(Class09 class09) {
			this.class09.remove(class09);
			class09.setClass05(null);
		}

		public void removeClass10(Class10 class10) {
			this.class10.remove(class10);
			class10.setClass05(null);
		}

		public void removeClass11(Class11 class11) {
			this.class11.remove(class11);
			class11.setClass05(null);
		}

		public void removeClass12(Class12 class12) {
			this.class12.remove(class12);
			class12.setClass05(null);
		}

		public void removeClass13(Class13 class13) {
			this.class13.remove(class13);
			class13.setClass05(null);
		}

		public void removeClass14(Class14 class14) {
			this.class14.remove(class14);
			class14.setClass05(null);
		}

		public void removeClass15(Class15 class15) {
			this.class15.remove(class15);
			class15.setClass05(null);
		}

		public void removeClass16(Class16 class16) {
			this.class16.remove(class16);
			class16.setClass05(null);
		}

		public void removeClass17(Class17 class17) {
			this.class17.remove(class17);
			class17.setClass05(null);
		}

		public void removeClass18(Class18 class18) {
			this.class18.remove(class18);
			class18.setClass05(null);
		}

		public void removeClass19(Class19 class19) {
			this.class19.remove(class19);
			class19.setClass05(null);
		}

		public void removeClass20(Class20 class20) {
			this.class20.remove(class20);
			class20.setClass05(null);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass05(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass05(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass05(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass05(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass05(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass05(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass05(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass05(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass05(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass05(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass05(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Set<Class06> getClass06() {
			return this.class06;
		}

		public Set<Class07> getClass07() {
			return this.class07;
		}

		public Set<Class08> getClass08() {
			return this.class08;
		}

		public Set<Class09> getClass09() {
			return this.class09;
		}

		public Set<Class10> getClass10() {
			return this.class10;
		}

		public Set<Class11> getClass11() {
			return this.class11;
		}

		public Set<Class12> getClass12() {
			return this.class12;
		}

		public Set<Class13> getClass13() {
			return this.class13;
		}

		public Set<Class14> getClass14() {
			return this.class14;
		}

		public Set<Class15> getClass15() {
			return this.class15;
		}

		public Set<Class16> getClass16() {
			return this.class16;
		}

		public Set<Class17> getClass17() {
			return this.class17;
		}

		public Set<Class18> getClass18() {
			return this.class18;
		}

		public Set<Class19> getClass19() {
			return this.class19;
		}

		public Set<Class20> getClass20() {
			return this.class20;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass06(Set<Class06> class06) {

			if (this.class06 != null) {

				for (Class06 lclass06 : this.class06) {
					lclass06.setClass05(null);
				}
			}

			this.class06 = class06;

			for (Class06 lclass06 : class06) {
				lclass06.setClass05(this);
			}
		}

		public void setClass07(Set<Class07> class07) {

			if (this.class07 != null) {

				for (Class07 lclass07 : this.class07) {
					lclass07.setClass05(null);
				}
			}

			this.class07 = class07;

			for (Class07 lclass07 : class07) {
				lclass07.setClass05(this);
			}
		}

		public void setClass08(Set<Class08> class08) {

			if (this.class08 != null) {

				for (Class08 lclass08 : this.class08) {
					lclass08.setClass05(null);
				}
			}

			this.class08 = class08;

			for (Class08 lclass08 : class08) {
				lclass08.setClass05(this);
			}
		}

		public void setClass09(Set<Class09> class09) {

			if (this.class09 != null) {

				for (Class09 lclass09 : this.class09) {
					lclass09.setClass05(null);
				}
			}

			this.class09 = class09;

			for (Class09 lclass09 : class09) {
				lclass09.setClass05(this);
			}
		}

		public void setClass10(Set<Class10> class10) {

			if (this.class10 != null) {

				for (Class10 lclass10 : this.class10) {
					lclass10.setClass05(null);
				}
			}

			this.class10 = class10;

			for (Class10 lclass10 : class10) {
				lclass10.setClass05(this);
			}
		}

		public void setClass11(Set<Class11> class11) {

			if (this.class11 != null) {

				for (Class11 lclass11 : this.class11) {
					lclass11.setClass05(null);
				}
			}

			this.class11 = class11;

			for (Class11 lclass11 : class11) {
				lclass11.setClass05(this);
			}
		}

		public void setClass12(Set<Class12> class12) {

			if (this.class12 != null) {

				for (Class12 lclass12 : this.class12) {
					lclass12.setClass05(null);
				}
			}

			this.class12 = class12;

			for (Class12 lclass12 : class12) {
				lclass12.setClass05(this);
			}
		}

		public void setClass13(Set<Class13> class13) {

			if (this.class13 != null) {

				for (Class13 lclass13 : this.class13) {
					lclass13.setClass05(null);
				}
			}

			this.class13 = class13;

			for (Class13 lclass13 : class13) {
				lclass13.setClass05(this);
			}
		}

		public void setClass14(Set<Class14> class14) {

			if (this.class14 != null) {

				for (Class14 lclass14 : this.class14) {
					lclass14.setClass05(null);
				}
			}

			this.class14 = class14;

			for (Class14 lclass14 : class14) {
				lclass14.setClass05(this);
			}
		}

		public void setClass15(Set<Class15> class15) {

			if (this.class15 != null) {

				for (Class15 lclass15 : this.class15) {
					lclass15.setClass05(null);
				}
			}

			this.class15 = class15;

			for (Class15 lclass15 : class15) {
				lclass15.setClass05(this);
			}
		}

		public void setClass16(Set<Class16> class16) {

			if (this.class16 != null) {

				for (Class16 lclass16 : this.class16) {
					lclass16.setClass05(null);
				}
			}

			this.class16 = class16;

			for (Class16 lclass16 : class16) {
				lclass16.setClass05(this);
			}
		}

		public void setClass17(Set<Class17> class17) {

			if (this.class17 != null) {

				for (Class17 lclass17 : this.class17) {
					lclass17.setClass05(null);
				}
			}

			this.class17 = class17;

			for (Class17 lclass17 : class17) {
				lclass17.setClass05(this);
			}
		}

		public void setClass18(Set<Class18> class18) {

			if (this.class18 != null) {

				for (Class18 lclass18 : this.class18) {
					lclass18.setClass05(null);
				}
			}

			this.class18 = class18;

			for (Class18 lclass18 : class18) {
				lclass18.setClass05(this);
			}
		}

		public void setClass19(Set<Class19> class19) {

			if (this.class19 != null) {

				for (Class19 lclass19 : this.class19) {
					lclass19.setClass05(null);
				}
			}

			this.class19 = class19;

			for (Class19 lclass19 : class19) {
				lclass19.setClass05(this);
			}
		}

		public void setClass20(Set<Class20> class20) {

			if (this.class20 != null) {

				for (Class20 lclass20 : this.class20) {
					lclass20.setClass05(null);
				}
			}

			this.class20 = class20;

			for (Class20 lclass20 : class20) {
				lclass20.setClass05(this);
			}
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass05(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass05(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass05(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass05(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass05(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass05(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass05(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass05(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass05(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass05(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass05(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass05(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass05(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass05(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass05(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass05(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass05(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass05(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass05(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass05(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass05(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass05(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class06")
	public static class Class06 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class07.class
		)
		private Set<Class07>		class07;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class08.class
		)
		private Set<Class08>		class08;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class09.class
		)
		private Set<Class09>		class09;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class10.class
		)
		private Set<Class10>		class10;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class11.class
		)
		private Set<Class11>		class11;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class12.class
		)
		private Set<Class12>		class12;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class13.class
		)
		private Set<Class13>		class13;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class14.class
		)
		private Set<Class14>		class14;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class15.class
		)
		private Set<Class15>		class15;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class16.class
		)
		private Set<Class16>		class16;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class17.class
		)
		private Set<Class17>		class17;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class18.class
		)
		private Set<Class18>		class18;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class19.class
		)
		private Set<Class19>		class19;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class20.class
		)
		private Set<Class20>		class20;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class06",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class06() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass07(Class07 class07) {
			this.class07.add(class07);
			class07.setClass06(this);
		}

		public void addClass08(Class08 class08) {
			this.class08.add(class08);
			class08.setClass06(this);
		}

		public void addClass09(Class09 class09) {
			this.class09.add(class09);
			class09.setClass06(this);
		}

		public void addClass10(Class10 class10) {
			this.class10.add(class10);
			class10.setClass06(this);
		}

		public void addClass11(Class11 class11) {
			this.class11.add(class11);
			class11.setClass06(this);
		}

		public void addClass12(Class12 class12) {
			this.class12.add(class12);
			class12.setClass06(this);
		}

		public void addClass13(Class13 class13) {
			this.class13.add(class13);
			class13.setClass06(this);
		}

		public void addClass14(Class14 class14) {
			this.class14.add(class14);
			class14.setClass06(this);
		}

		public void addClass15(Class15 class15) {
			this.class15.add(class15);
			class15.setClass06(this);
		}

		public void addClass16(Class16 class16) {
			this.class16.add(class16);
			class16.setClass06(this);
		}

		public void addClass17(Class17 class17) {
			this.class17.add(class17);
			class17.setClass06(this);
		}

		public void addClass18(Class18 class18) {
			this.class18.add(class18);
			class18.setClass06(this);
		}

		public void addClass19(Class19 class19) {
			this.class19.add(class19);
			class19.setClass06(this);
		}

		public void addClass20(Class20 class20) {
			this.class20.add(class20);
			class20.setClass06(this);
		}

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass06(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass06(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass06(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass06(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass06(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass06(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass06(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass06(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass06(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass06(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass06(this);
		}

		public void removeClass07(Class07 class07) {
			this.class07.remove(class07);
			class07.setClass06(null);
		}

		public void removeClass08(Class08 class08) {
			this.class08.remove(class08);
			class08.setClass06(null);
		}

		public void removeClass09(Class09 class09) {
			this.class09.remove(class09);
			class09.setClass06(null);
		}

		public void removeClass10(Class10 class10) {
			this.class10.remove(class10);
			class10.setClass06(null);
		}

		public void removeClass11(Class11 class11) {
			this.class11.remove(class11);
			class11.setClass06(null);
		}

		public void removeClass12(Class12 class12) {
			this.class12.remove(class12);
			class12.setClass06(null);
		}

		public void removeClass13(Class13 class13) {
			this.class13.remove(class13);
			class13.setClass06(null);
		}

		public void removeClass14(Class14 class14) {
			this.class14.remove(class14);
			class14.setClass06(null);
		}

		public void removeClass15(Class15 class15) {
			this.class15.remove(class15);
			class15.setClass06(null);
		}

		public void removeClass16(Class16 class16) {
			this.class16.remove(class16);
			class16.setClass06(null);
		}

		public void removeClass17(Class17 class17) {
			this.class17.remove(class17);
			class17.setClass06(null);
		}

		public void removeClass18(Class18 class18) {
			this.class18.remove(class18);
			class18.setClass06(null);
		}

		public void removeClass19(Class19 class19) {
			this.class19.remove(class19);
			class19.setClass06(null);
		}

		public void removeClass20(Class20 class20) {
			this.class20.remove(class20);
			class20.setClass06(null);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass06(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass06(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass06(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass06(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass06(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass06(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass06(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass06(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass06(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass06(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass06(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Set<Class07> getClass07() {
			return this.class07;
		}

		public Set<Class08> getClass08() {
			return this.class08;
		}

		public Set<Class09> getClass09() {
			return this.class09;
		}

		public Set<Class10> getClass10() {
			return this.class10;
		}

		public Set<Class11> getClass11() {
			return this.class11;
		}

		public Set<Class12> getClass12() {
			return this.class12;
		}

		public Set<Class13> getClass13() {
			return this.class13;
		}

		public Set<Class14> getClass14() {
			return this.class14;
		}

		public Set<Class15> getClass15() {
			return this.class15;
		}

		public Set<Class16> getClass16() {
			return this.class16;
		}

		public Set<Class17> getClass17() {
			return this.class17;
		}

		public Set<Class18> getClass18() {
			return this.class18;
		}

		public Set<Class19> getClass19() {
			return this.class19;
		}

		public Set<Class20> getClass20() {
			return this.class20;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass07(Set<Class07> class07) {

			if (this.class07 != null) {

				for (Class07 lclass07 : this.class07) {
					lclass07.setClass06(null);
				}
			}

			this.class07 = class07;

			for (Class07 lclass07 : class07) {
				lclass07.setClass06(this);
			}
		}

		public void setClass08(Set<Class08> class08) {

			if (this.class08 != null) {

				for (Class08 lclass08 : this.class08) {
					lclass08.setClass06(null);
				}
			}

			this.class08 = class08;

			for (Class08 lclass08 : class08) {
				lclass08.setClass06(this);
			}
		}

		public void setClass09(Set<Class09> class09) {

			if (this.class09 != null) {

				for (Class09 lclass09 : this.class09) {
					lclass09.setClass06(null);
				}
			}

			this.class09 = class09;

			for (Class09 lclass09 : class09) {
				lclass09.setClass06(this);
			}
		}

		public void setClass10(Set<Class10> class10) {

			if (this.class10 != null) {

				for (Class10 lclass10 : this.class10) {
					lclass10.setClass06(null);
				}
			}

			this.class10 = class10;

			for (Class10 lclass10 : class10) {
				lclass10.setClass06(this);
			}
		}

		public void setClass11(Set<Class11> class11) {

			if (this.class11 != null) {

				for (Class11 lclass11 : this.class11) {
					lclass11.setClass06(null);
				}
			}

			this.class11 = class11;

			for (Class11 lclass11 : class11) {
				lclass11.setClass06(this);
			}
		}

		public void setClass12(Set<Class12> class12) {

			if (this.class12 != null) {

				for (Class12 lclass12 : this.class12) {
					lclass12.setClass06(null);
				}
			}

			this.class12 = class12;

			for (Class12 lclass12 : class12) {
				lclass12.setClass06(this);
			}
		}

		public void setClass13(Set<Class13> class13) {

			if (this.class13 != null) {

				for (Class13 lclass13 : this.class13) {
					lclass13.setClass06(null);
				}
			}

			this.class13 = class13;

			for (Class13 lclass13 : class13) {
				lclass13.setClass06(this);
			}
		}

		public void setClass14(Set<Class14> class14) {

			if (this.class14 != null) {

				for (Class14 lclass14 : this.class14) {
					lclass14.setClass06(null);
				}
			}

			this.class14 = class14;

			for (Class14 lclass14 : class14) {
				lclass14.setClass06(this);
			}
		}

		public void setClass15(Set<Class15> class15) {

			if (this.class15 != null) {

				for (Class15 lclass15 : this.class15) {
					lclass15.setClass06(null);
				}
			}

			this.class15 = class15;

			for (Class15 lclass15 : class15) {
				lclass15.setClass06(this);
			}
		}

		public void setClass16(Set<Class16> class16) {

			if (this.class16 != null) {

				for (Class16 lclass16 : this.class16) {
					lclass16.setClass06(null);
				}
			}

			this.class16 = class16;

			for (Class16 lclass16 : class16) {
				lclass16.setClass06(this);
			}
		}

		public void setClass17(Set<Class17> class17) {

			if (this.class17 != null) {

				for (Class17 lclass17 : this.class17) {
					lclass17.setClass06(null);
				}
			}

			this.class17 = class17;

			for (Class17 lclass17 : class17) {
				lclass17.setClass06(this);
			}
		}

		public void setClass18(Set<Class18> class18) {

			if (this.class18 != null) {

				for (Class18 lclass18 : this.class18) {
					lclass18.setClass06(null);
				}
			}

			this.class18 = class18;

			for (Class18 lclass18 : class18) {
				lclass18.setClass06(this);
			}
		}

		public void setClass19(Set<Class19> class19) {

			if (this.class19 != null) {

				for (Class19 lclass19 : this.class19) {
					lclass19.setClass06(null);
				}
			}

			this.class19 = class19;

			for (Class19 lclass19 : class19) {
				lclass19.setClass06(this);
			}
		}

		public void setClass20(Set<Class20> class20) {

			if (this.class20 != null) {

				for (Class20 lclass20 : this.class20) {
					lclass20.setClass06(null);
				}
			}

			this.class20 = class20;

			for (Class20 lclass20 : class20) {
				lclass20.setClass06(this);
			}
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass06(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass06(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass06(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass06(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass06(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass06(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass06(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass06(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass06(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass06(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass06(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass06(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass06(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass06(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass06(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass06(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass06(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass06(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass06(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass06(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass06(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass06(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class07")
	public static class Class07 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class08.class
		)
		private Set<Class08>		class08;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class09.class
		)
		private Set<Class09>		class09;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class10.class
		)
		private Set<Class10>		class10;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class11.class
		)
		private Set<Class11>		class11;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class12.class
		)
		private Set<Class12>		class12;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class13.class
		)
		private Set<Class13>		class13;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class14.class
		)
		private Set<Class14>		class14;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class15.class
		)
		private Set<Class15>		class15;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class16.class
		)
		private Set<Class16>		class16;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class17.class
		)
		private Set<Class17>		class17;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class18.class
		)
		private Set<Class18>		class18;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class19.class
		)
		private Set<Class19>		class19;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class20.class
		)
		private Set<Class20>		class20;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class07",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class07() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass08(Class08 class08) {
			this.class08.add(class08);
			class08.setClass07(this);
		}

		public void addClass09(Class09 class09) {
			this.class09.add(class09);
			class09.setClass07(this);
		}

		public void addClass10(Class10 class10) {
			this.class10.add(class10);
			class10.setClass07(this);
		}

		public void addClass11(Class11 class11) {
			this.class11.add(class11);
			class11.setClass07(this);
		}

		public void addClass12(Class12 class12) {
			this.class12.add(class12);
			class12.setClass07(this);
		}

		public void addClass13(Class13 class13) {
			this.class13.add(class13);
			class13.setClass07(this);
		}

		public void addClass14(Class14 class14) {
			this.class14.add(class14);
			class14.setClass07(this);
		}

		public void addClass15(Class15 class15) {
			this.class15.add(class15);
			class15.setClass07(this);
		}

		public void addClass16(Class16 class16) {
			this.class16.add(class16);
			class16.setClass07(this);
		}

		public void addClass17(Class17 class17) {
			this.class17.add(class17);
			class17.setClass07(this);
		}

		public void addClass18(Class18 class18) {
			this.class18.add(class18);
			class18.setClass07(this);
		}

		public void addClass19(Class19 class19) {
			this.class19.add(class19);
			class19.setClass07(this);
		}

		public void addClass20(Class20 class20) {
			this.class20.add(class20);
			class20.setClass07(this);
		}

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass07(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass07(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass07(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass07(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass07(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass07(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass07(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass07(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass07(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass07(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass07(this);
		}

		public void removeClass08(Class08 class08) {
			this.class08.remove(class08);
			class08.setClass07(null);
		}

		public void removeClass09(Class09 class09) {
			this.class09.remove(class09);
			class09.setClass07(null);
		}

		public void removeClass10(Class10 class10) {
			this.class10.remove(class10);
			class10.setClass07(null);
		}

		public void removeClass11(Class11 class11) {
			this.class11.remove(class11);
			class11.setClass07(null);
		}

		public void removeClass12(Class12 class12) {
			this.class12.remove(class12);
			class12.setClass07(null);
		}

		public void removeClass13(Class13 class13) {
			this.class13.remove(class13);
			class13.setClass07(null);
		}

		public void removeClass14(Class14 class14) {
			this.class14.remove(class14);
			class14.setClass07(null);
		}

		public void removeClass15(Class15 class15) {
			this.class15.remove(class15);
			class15.setClass07(null);
		}

		public void removeClass16(Class16 class16) {
			this.class16.remove(class16);
			class16.setClass07(null);
		}

		public void removeClass17(Class17 class17) {
			this.class17.remove(class17);
			class17.setClass07(null);
		}

		public void removeClass18(Class18 class18) {
			this.class18.remove(class18);
			class18.setClass07(null);
		}

		public void removeClass19(Class19 class19) {
			this.class19.remove(class19);
			class19.setClass07(null);
		}

		public void removeClass20(Class20 class20) {
			this.class20.remove(class20);
			class20.setClass07(null);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass07(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass07(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass07(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass07(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass07(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass07(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass07(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass07(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass07(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass07(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass07(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Set<Class08> getClass08() {
			return this.class08;
		}

		public Set<Class09> getClass09() {
			return this.class09;
		}

		public Set<Class10> getClass10() {
			return this.class10;
		}

		public Set<Class11> getClass11() {
			return this.class11;
		}

		public Set<Class12> getClass12() {
			return this.class12;
		}

		public Set<Class13> getClass13() {
			return this.class13;
		}

		public Set<Class14> getClass14() {
			return this.class14;
		}

		public Set<Class15> getClass15() {
			return this.class15;
		}

		public Set<Class16> getClass16() {
			return this.class16;
		}

		public Set<Class17> getClass17() {
			return this.class17;
		}

		public Set<Class18> getClass18() {
			return this.class18;
		}

		public Set<Class19> getClass19() {
			return this.class19;
		}

		public Set<Class20> getClass20() {
			return this.class20;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass08(Set<Class08> class08) {

			if (this.class08 != null) {

				for (Class08 lclass08 : this.class08) {
					lclass08.setClass07(null);
				}
			}

			this.class08 = class08;

			for (Class08 lclass08 : class08) {
				lclass08.setClass07(this);
			}
		}

		public void setClass09(Set<Class09> class09) {

			if (this.class09 != null) {

				for (Class09 lclass09 : this.class09) {
					lclass09.setClass07(null);
				}
			}

			this.class09 = class09;

			for (Class09 lclass09 : class09) {
				lclass09.setClass07(this);
			}
		}

		public void setClass10(Set<Class10> class10) {

			if (this.class10 != null) {

				for (Class10 lclass10 : this.class10) {
					lclass10.setClass07(null);
				}
			}

			this.class10 = class10;

			for (Class10 lclass10 : class10) {
				lclass10.setClass07(this);
			}
		}

		public void setClass11(Set<Class11> class11) {

			if (this.class11 != null) {

				for (Class11 lclass11 : this.class11) {
					lclass11.setClass07(null);
				}
			}

			this.class11 = class11;

			for (Class11 lclass11 : class11) {
				lclass11.setClass07(this);
			}
		}

		public void setClass12(Set<Class12> class12) {

			if (this.class12 != null) {

				for (Class12 lclass12 : this.class12) {
					lclass12.setClass07(null);
				}
			}

			this.class12 = class12;

			for (Class12 lclass12 : class12) {
				lclass12.setClass07(this);
			}
		}

		public void setClass13(Set<Class13> class13) {

			if (this.class13 != null) {

				for (Class13 lclass13 : this.class13) {
					lclass13.setClass07(null);
				}
			}

			this.class13 = class13;

			for (Class13 lclass13 : class13) {
				lclass13.setClass07(this);
			}
		}

		public void setClass14(Set<Class14> class14) {

			if (this.class14 != null) {

				for (Class14 lclass14 : this.class14) {
					lclass14.setClass07(null);
				}
			}

			this.class14 = class14;

			for (Class14 lclass14 : class14) {
				lclass14.setClass07(this);
			}
		}

		public void setClass15(Set<Class15> class15) {

			if (this.class15 != null) {

				for (Class15 lclass15 : this.class15) {
					lclass15.setClass07(null);
				}
			}

			this.class15 = class15;

			for (Class15 lclass15 : class15) {
				lclass15.setClass07(this);
			}
		}

		public void setClass16(Set<Class16> class16) {

			if (this.class16 != null) {

				for (Class16 lclass16 : this.class16) {
					lclass16.setClass07(null);
				}
			}

			this.class16 = class16;

			for (Class16 lclass16 : class16) {
				lclass16.setClass07(this);
			}
		}

		public void setClass17(Set<Class17> class17) {

			if (this.class17 != null) {

				for (Class17 lclass17 : this.class17) {
					lclass17.setClass07(null);
				}
			}

			this.class17 = class17;

			for (Class17 lclass17 : class17) {
				lclass17.setClass07(this);
			}
		}

		public void setClass18(Set<Class18> class18) {

			if (this.class18 != null) {

				for (Class18 lclass18 : this.class18) {
					lclass18.setClass07(null);
				}
			}

			this.class18 = class18;

			for (Class18 lclass18 : class18) {
				lclass18.setClass07(this);
			}
		}

		public void setClass19(Set<Class19> class19) {

			if (this.class19 != null) {

				for (Class19 lclass19 : this.class19) {
					lclass19.setClass07(null);
				}
			}

			this.class19 = class19;

			for (Class19 lclass19 : class19) {
				lclass19.setClass07(this);
			}
		}

		public void setClass20(Set<Class20> class20) {

			if (this.class20 != null) {

				for (Class20 lclass20 : this.class20) {
					lclass20.setClass07(null);
				}
			}

			this.class20 = class20;

			for (Class20 lclass20 : class20) {
				lclass20.setClass07(this);
			}
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass07(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass07(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass07(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass07(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass07(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass07(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass07(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass07(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass07(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass07(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass07(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass07(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass07(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass07(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass07(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass07(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass07(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass07(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass07(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass07(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass07(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass07(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class08")
	public static class Class08 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class09.class
		)
		private Set<Class09>		class09;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class10.class
		)
		private Set<Class10>		class10;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class11.class
		)
		private Set<Class11>		class11;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class12.class
		)
		private Set<Class12>		class12;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class13.class
		)
		private Set<Class13>		class13;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class14.class
		)
		private Set<Class14>		class14;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class15.class
		)
		private Set<Class15>		class15;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class16.class
		)
		private Set<Class16>		class16;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class17.class
		)
		private Set<Class17>		class17;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class18.class
		)
		private Set<Class18>		class18;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class19.class
		)
		private Set<Class19>		class19;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class20.class
		)
		private Set<Class20>		class20;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class08",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class08() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass09(Class09 class09) {
			this.class09.add(class09);
			class09.setClass08(this);
		}

		public void addClass10(Class10 class10) {
			this.class10.add(class10);
			class10.setClass08(this);
		}

		public void addClass11(Class11 class11) {
			this.class11.add(class11);
			class11.setClass08(this);
		}

		public void addClass12(Class12 class12) {
			this.class12.add(class12);
			class12.setClass08(this);
		}

		public void addClass13(Class13 class13) {
			this.class13.add(class13);
			class13.setClass08(this);
		}

		public void addClass14(Class14 class14) {
			this.class14.add(class14);
			class14.setClass08(this);
		}

		public void addClass15(Class15 class15) {
			this.class15.add(class15);
			class15.setClass08(this);
		}

		public void addClass16(Class16 class16) {
			this.class16.add(class16);
			class16.setClass08(this);
		}

		public void addClass17(Class17 class17) {
			this.class17.add(class17);
			class17.setClass08(this);
		}

		public void addClass18(Class18 class18) {
			this.class18.add(class18);
			class18.setClass08(this);
		}

		public void addClass19(Class19 class19) {
			this.class19.add(class19);
			class19.setClass08(this);
		}

		public void addClass20(Class20 class20) {
			this.class20.add(class20);
			class20.setClass08(this);
		}

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass08(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass08(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass08(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass08(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass08(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass08(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass08(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass08(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass08(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass08(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass08(this);
		}

		public void removeClass09(Class09 class09) {
			this.class09.remove(class09);
			class09.setClass08(null);
		}

		public void removeClass10(Class10 class10) {
			this.class10.remove(class10);
			class10.setClass08(null);
		}

		public void removeClass11(Class11 class11) {
			this.class11.remove(class11);
			class11.setClass08(null);
		}

		public void removeClass12(Class12 class12) {
			this.class12.remove(class12);
			class12.setClass08(null);
		}

		public void removeClass13(Class13 class13) {
			this.class13.remove(class13);
			class13.setClass08(null);
		}

		public void removeClass14(Class14 class14) {
			this.class14.remove(class14);
			class14.setClass08(null);
		}

		public void removeClass15(Class15 class15) {
			this.class15.remove(class15);
			class15.setClass08(null);
		}

		public void removeClass16(Class16 class16) {
			this.class16.remove(class16);
			class16.setClass08(null);
		}

		public void removeClass17(Class17 class17) {
			this.class17.remove(class17);
			class17.setClass08(null);
		}

		public void removeClass18(Class18 class18) {
			this.class18.remove(class18);
			class18.setClass08(null);
		}

		public void removeClass19(Class19 class19) {
			this.class19.remove(class19);
			class19.setClass08(null);
		}

		public void removeClass20(Class20 class20) {
			this.class20.remove(class20);
			class20.setClass08(null);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass08(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass08(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass08(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass08(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass08(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass08(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass08(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass08(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass08(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass08(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass08(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Set<Class09> getClass09() {
			return this.class09;
		}

		public Set<Class10> getClass10() {
			return this.class10;
		}

		public Set<Class11> getClass11() {
			return this.class11;
		}

		public Set<Class12> getClass12() {
			return this.class12;
		}

		public Set<Class13> getClass13() {
			return this.class13;
		}

		public Set<Class14> getClass14() {
			return this.class14;
		}

		public Set<Class15> getClass15() {
			return this.class15;
		}

		public Set<Class16> getClass16() {
			return this.class16;
		}

		public Set<Class17> getClass17() {
			return this.class17;
		}

		public Set<Class18> getClass18() {
			return this.class18;
		}

		public Set<Class19> getClass19() {
			return this.class19;
		}

		public Set<Class20> getClass20() {
			return this.class20;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass09(Set<Class09> class09) {

			if (this.class09 != null) {

				for (Class09 lclass09 : this.class09) {
					lclass09.setClass08(null);
				}
			}

			this.class09 = class09;

			for (Class09 lclass09 : class09) {
				lclass09.setClass08(this);
			}
		}

		public void setClass10(Set<Class10> class10) {

			if (this.class10 != null) {

				for (Class10 lclass10 : this.class10) {
					lclass10.setClass08(null);
				}
			}

			this.class10 = class10;

			for (Class10 lclass10 : class10) {
				lclass10.setClass08(this);
			}
		}

		public void setClass11(Set<Class11> class11) {

			if (this.class11 != null) {

				for (Class11 lclass11 : this.class11) {
					lclass11.setClass08(null);
				}
			}

			this.class11 = class11;

			for (Class11 lclass11 : class11) {
				lclass11.setClass08(this);
			}
		}

		public void setClass12(Set<Class12> class12) {

			if (this.class12 != null) {

				for (Class12 lclass12 : this.class12) {
					lclass12.setClass08(null);
				}
			}

			this.class12 = class12;

			for (Class12 lclass12 : class12) {
				lclass12.setClass08(this);
			}
		}

		public void setClass13(Set<Class13> class13) {

			if (this.class13 != null) {

				for (Class13 lclass13 : this.class13) {
					lclass13.setClass08(null);
				}
			}

			this.class13 = class13;

			for (Class13 lclass13 : class13) {
				lclass13.setClass08(this);
			}
		}

		public void setClass14(Set<Class14> class14) {

			if (this.class14 != null) {

				for (Class14 lclass14 : this.class14) {
					lclass14.setClass08(null);
				}
			}

			this.class14 = class14;

			for (Class14 lclass14 : class14) {
				lclass14.setClass08(this);
			}
		}

		public void setClass15(Set<Class15> class15) {

			if (this.class15 != null) {

				for (Class15 lclass15 : this.class15) {
					lclass15.setClass08(null);
				}
			}

			this.class15 = class15;

			for (Class15 lclass15 : class15) {
				lclass15.setClass08(this);
			}
		}

		public void setClass16(Set<Class16> class16) {

			if (this.class16 != null) {

				for (Class16 lclass16 : this.class16) {
					lclass16.setClass08(null);
				}
			}

			this.class16 = class16;

			for (Class16 lclass16 : class16) {
				lclass16.setClass08(this);
			}
		}

		public void setClass17(Set<Class17> class17) {

			if (this.class17 != null) {

				for (Class17 lclass17 : this.class17) {
					lclass17.setClass08(null);
				}
			}

			this.class17 = class17;

			for (Class17 lclass17 : class17) {
				lclass17.setClass08(this);
			}
		}

		public void setClass18(Set<Class18> class18) {

			if (this.class18 != null) {

				for (Class18 lclass18 : this.class18) {
					lclass18.setClass08(null);
				}
			}

			this.class18 = class18;

			for (Class18 lclass18 : class18) {
				lclass18.setClass08(this);
			}
		}

		public void setClass19(Set<Class19> class19) {

			if (this.class19 != null) {

				for (Class19 lclass19 : this.class19) {
					lclass19.setClass08(null);
				}
			}

			this.class19 = class19;

			for (Class19 lclass19 : class19) {
				lclass19.setClass08(this);
			}
		}

		public void setClass20(Set<Class20> class20) {

			if (this.class20 != null) {

				for (Class20 lclass20 : this.class20) {
					lclass20.setClass08(null);
				}
			}

			this.class20 = class20;

			for (Class20 lclass20 : class20) {
				lclass20.setClass08(this);
			}
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass08(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass08(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass08(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass08(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass08(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass08(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass08(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass08(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass08(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass08(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass08(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass08(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass08(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass08(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass08(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass08(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass08(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass08(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass08(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass08(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass08(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass08(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class09")
	public static class Class09 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class10.class
		)
		private Set<Class10>		class10;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class11.class
		)
		private Set<Class11>		class11;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class12.class
		)
		private Set<Class12>		class12;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class13.class
		)
		private Set<Class13>		class13;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class14.class
		)
		private Set<Class14>		class14;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class15.class
		)
		private Set<Class15>		class15;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class16.class
		)
		private Set<Class16>		class16;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class17.class
		)
		private Set<Class17>		class17;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class18.class
		)
		private Set<Class18>		class18;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class19.class
		)
		private Set<Class19>		class19;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class20.class
		)
		private Set<Class20>		class20;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class09",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class09() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass10(Class10 class10) {
			this.class10.add(class10);
			class10.setClass09(this);
		}

		public void addClass11(Class11 class11) {
			this.class11.add(class11);
			class11.setClass09(this);
		}

		public void addClass12(Class12 class12) {
			this.class12.add(class12);
			class12.setClass09(this);
		}

		public void addClass13(Class13 class13) {
			this.class13.add(class13);
			class13.setClass09(this);
		}

		public void addClass14(Class14 class14) {
			this.class14.add(class14);
			class14.setClass09(this);
		}

		public void addClass15(Class15 class15) {
			this.class15.add(class15);
			class15.setClass09(this);
		}

		public void addClass16(Class16 class16) {
			this.class16.add(class16);
			class16.setClass09(this);
		}

		public void addClass17(Class17 class17) {
			this.class17.add(class17);
			class17.setClass09(this);
		}

		public void addClass18(Class18 class18) {
			this.class18.add(class18);
			class18.setClass09(this);
		}

		public void addClass19(Class19 class19) {
			this.class19.add(class19);
			class19.setClass09(this);
		}

		public void addClass20(Class20 class20) {
			this.class20.add(class20);
			class20.setClass09(this);
		}

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass09(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass09(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass09(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass09(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass09(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass09(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass09(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass09(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass09(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass09(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass09(this);
		}

		public void removeClass10(Class10 class10) {
			this.class10.remove(class10);
			class10.setClass09(null);
		}

		public void removeClass11(Class11 class11) {
			this.class11.remove(class11);
			class11.setClass09(null);
		}

		public void removeClass12(Class12 class12) {
			this.class12.remove(class12);
			class12.setClass09(null);
		}

		public void removeClass13(Class13 class13) {
			this.class13.remove(class13);
			class13.setClass09(null);
		}

		public void removeClass14(Class14 class14) {
			this.class14.remove(class14);
			class14.setClass09(null);
		}

		public void removeClass15(Class15 class15) {
			this.class15.remove(class15);
			class15.setClass09(null);
		}

		public void removeClass16(Class16 class16) {
			this.class16.remove(class16);
			class16.setClass09(null);
		}

		public void removeClass17(Class17 class17) {
			this.class17.remove(class17);
			class17.setClass09(null);
		}

		public void removeClass18(Class18 class18) {
			this.class18.remove(class18);
			class18.setClass09(null);
		}

		public void removeClass19(Class19 class19) {
			this.class19.remove(class19);
			class19.setClass09(null);
		}

		public void removeClass20(Class20 class20) {
			this.class20.remove(class20);
			class20.setClass09(null);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass09(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass09(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass09(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass09(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass09(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass09(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass09(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass09(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass09(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass09(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass09(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Set<Class10> getClass10() {
			return this.class10;
		}

		public Set<Class11> getClass11() {
			return this.class11;
		}

		public Set<Class12> getClass12() {
			return this.class12;
		}

		public Set<Class13> getClass13() {
			return this.class13;
		}

		public Set<Class14> getClass14() {
			return this.class14;
		}

		public Set<Class15> getClass15() {
			return this.class15;
		}

		public Set<Class16> getClass16() {
			return this.class16;
		}

		public Set<Class17> getClass17() {
			return this.class17;
		}

		public Set<Class18> getClass18() {
			return this.class18;
		}

		public Set<Class19> getClass19() {
			return this.class19;
		}

		public Set<Class20> getClass20() {
			return this.class20;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass10(Set<Class10> class10) {

			if (this.class10 != null) {

				for (Class10 lclass10 : this.class10) {
					lclass10.setClass09(null);
				}
			}

			this.class10 = class10;

			for (Class10 lclass10 : class10) {
				lclass10.setClass09(this);
			}
		}

		public void setClass11(Set<Class11> class11) {

			if (this.class11 != null) {

				for (Class11 lclass11 : this.class11) {
					lclass11.setClass09(null);
				}
			}

			this.class11 = class11;

			for (Class11 lclass11 : class11) {
				lclass11.setClass09(this);
			}
		}

		public void setClass12(Set<Class12> class12) {

			if (this.class12 != null) {

				for (Class12 lclass12 : this.class12) {
					lclass12.setClass09(null);
				}
			}

			this.class12 = class12;

			for (Class12 lclass12 : class12) {
				lclass12.setClass09(this);
			}
		}

		public void setClass13(Set<Class13> class13) {

			if (this.class13 != null) {

				for (Class13 lclass13 : this.class13) {
					lclass13.setClass09(null);
				}
			}

			this.class13 = class13;

			for (Class13 lclass13 : class13) {
				lclass13.setClass09(this);
			}
		}

		public void setClass14(Set<Class14> class14) {

			if (this.class14 != null) {

				for (Class14 lclass14 : this.class14) {
					lclass14.setClass09(null);
				}
			}

			this.class14 = class14;

			for (Class14 lclass14 : class14) {
				lclass14.setClass09(this);
			}
		}

		public void setClass15(Set<Class15> class15) {

			if (this.class15 != null) {

				for (Class15 lclass15 : this.class15) {
					lclass15.setClass09(null);
				}
			}

			this.class15 = class15;

			for (Class15 lclass15 : class15) {
				lclass15.setClass09(this);
			}
		}

		public void setClass16(Set<Class16> class16) {

			if (this.class16 != null) {

				for (Class16 lclass16 : this.class16) {
					lclass16.setClass09(null);
				}
			}

			this.class16 = class16;

			for (Class16 lclass16 : class16) {
				lclass16.setClass09(this);
			}
		}

		public void setClass17(Set<Class17> class17) {

			if (this.class17 != null) {

				for (Class17 lclass17 : this.class17) {
					lclass17.setClass09(null);
				}
			}

			this.class17 = class17;

			for (Class17 lclass17 : class17) {
				lclass17.setClass09(this);
			}
		}

		public void setClass18(Set<Class18> class18) {

			if (this.class18 != null) {

				for (Class18 lclass18 : this.class18) {
					lclass18.setClass09(null);
				}
			}

			this.class18 = class18;

			for (Class18 lclass18 : class18) {
				lclass18.setClass09(this);
			}
		}

		public void setClass19(Set<Class19> class19) {

			if (this.class19 != null) {

				for (Class19 lclass19 : this.class19) {
					lclass19.setClass09(null);
				}
			}

			this.class19 = class19;

			for (Class19 lclass19 : class19) {
				lclass19.setClass09(this);
			}
		}

		public void setClass20(Set<Class20> class20) {

			if (this.class20 != null) {

				for (Class20 lclass20 : this.class20) {
					lclass20.setClass09(null);
				}
			}

			this.class20 = class20;

			for (Class20 lclass20 : class20) {
				lclass20.setClass09(this);
			}
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass09(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass09(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass09(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass09(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass09(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass09(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass09(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass09(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass09(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass09(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass09(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass09(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass09(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass09(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass09(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass09(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass09(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass09(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass09(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass09(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass09(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass09(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class10")
	public static class Class10 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class11.class
		)
		private Set<Class11>		class11;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class12.class
		)
		private Set<Class12>		class12;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class13.class
		)
		private Set<Class13>		class13;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class14.class
		)
		private Set<Class14>		class14;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class15.class
		)
		private Set<Class15>		class15;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class16.class
		)
		private Set<Class16>		class16;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class17.class
		)
		private Set<Class17>		class17;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class18.class
		)
		private Set<Class18>		class18;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class19.class
		)
		private Set<Class19>		class19;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class20.class
		)
		private Set<Class20>		class20;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class10",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class10() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass11(Class11 class11) {
			this.class11.add(class11);
			class11.setClass10(this);
		}

		public void addClass12(Class12 class12) {
			this.class12.add(class12);
			class12.setClass10(this);
		}

		public void addClass13(Class13 class13) {
			this.class13.add(class13);
			class13.setClass10(this);
		}

		public void addClass14(Class14 class14) {
			this.class14.add(class14);
			class14.setClass10(this);
		}

		public void addClass15(Class15 class15) {
			this.class15.add(class15);
			class15.setClass10(this);
		}

		public void addClass16(Class16 class16) {
			this.class16.add(class16);
			class16.setClass10(this);
		}

		public void addClass17(Class17 class17) {
			this.class17.add(class17);
			class17.setClass10(this);
		}

		public void addClass18(Class18 class18) {
			this.class18.add(class18);
			class18.setClass10(this);
		}

		public void addClass19(Class19 class19) {
			this.class19.add(class19);
			class19.setClass10(this);
		}

		public void addClass20(Class20 class20) {
			this.class20.add(class20);
			class20.setClass10(this);
		}

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass10(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass10(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass10(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass10(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass10(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass10(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass10(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass10(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass10(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass10(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass10(this);
		}

		public void removeClass11(Class11 class11) {
			this.class11.remove(class11);
			class11.setClass10(null);
		}

		public void removeClass12(Class12 class12) {
			this.class12.remove(class12);
			class12.setClass10(null);
		}

		public void removeClass13(Class13 class13) {
			this.class13.remove(class13);
			class13.setClass10(null);
		}

		public void removeClass14(Class14 class14) {
			this.class14.remove(class14);
			class14.setClass10(null);
		}

		public void removeClass15(Class15 class15) {
			this.class15.remove(class15);
			class15.setClass10(null);
		}

		public void removeClass16(Class16 class16) {
			this.class16.remove(class16);
			class16.setClass10(null);
		}

		public void removeClass17(Class17 class17) {
			this.class17.remove(class17);
			class17.setClass10(null);
		}

		public void removeClass18(Class18 class18) {
			this.class18.remove(class18);
			class18.setClass10(null);
		}

		public void removeClass19(Class19 class19) {
			this.class19.remove(class19);
			class19.setClass10(null);
		}

		public void removeClass20(Class20 class20) {
			this.class20.remove(class20);
			class20.setClass10(null);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass10(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass10(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass10(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass10(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass10(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass10(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass10(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass10(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass10(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass10(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass10(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Set<Class11> getClass11() {
			return this.class11;
		}

		public Set<Class12> getClass12() {
			return this.class12;
		}

		public Set<Class13> getClass13() {
			return this.class13;
		}

		public Set<Class14> getClass14() {
			return this.class14;
		}

		public Set<Class15> getClass15() {
			return this.class15;
		}

		public Set<Class16> getClass16() {
			return this.class16;
		}

		public Set<Class17> getClass17() {
			return this.class17;
		}

		public Set<Class18> getClass18() {
			return this.class18;
		}

		public Set<Class19> getClass19() {
			return this.class19;
		}

		public Set<Class20> getClass20() {
			return this.class20;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass11(Set<Class11> class11) {

			if (this.class11 != null) {

				for (Class11 lclass11 : this.class11) {
					lclass11.setClass10(null);
				}
			}

			this.class11 = class11;

			for (Class11 lclass11 : class11) {
				lclass11.setClass10(this);
			}
		}

		public void setClass12(Set<Class12> class12) {

			if (this.class12 != null) {

				for (Class12 lclass12 : this.class12) {
					lclass12.setClass10(null);
				}
			}

			this.class12 = class12;

			for (Class12 lclass12 : class12) {
				lclass12.setClass10(this);
			}
		}

		public void setClass13(Set<Class13> class13) {

			if (this.class13 != null) {

				for (Class13 lclass13 : this.class13) {
					lclass13.setClass10(null);
				}
			}

			this.class13 = class13;

			for (Class13 lclass13 : class13) {
				lclass13.setClass10(this);
			}
		}

		public void setClass14(Set<Class14> class14) {

			if (this.class14 != null) {

				for (Class14 lclass14 : this.class14) {
					lclass14.setClass10(null);
				}
			}

			this.class14 = class14;

			for (Class14 lclass14 : class14) {
				lclass14.setClass10(this);
			}
		}

		public void setClass15(Set<Class15> class15) {

			if (this.class15 != null) {

				for (Class15 lclass15 : this.class15) {
					lclass15.setClass10(null);
				}
			}

			this.class15 = class15;

			for (Class15 lclass15 : class15) {
				lclass15.setClass10(this);
			}
		}

		public void setClass16(Set<Class16> class16) {

			if (this.class16 != null) {

				for (Class16 lclass16 : this.class16) {
					lclass16.setClass10(null);
				}
			}

			this.class16 = class16;

			for (Class16 lclass16 : class16) {
				lclass16.setClass10(this);
			}
		}

		public void setClass17(Set<Class17> class17) {

			if (this.class17 != null) {

				for (Class17 lclass17 : this.class17) {
					lclass17.setClass10(null);
				}
			}

			this.class17 = class17;

			for (Class17 lclass17 : class17) {
				lclass17.setClass10(this);
			}
		}

		public void setClass18(Set<Class18> class18) {

			if (this.class18 != null) {

				for (Class18 lclass18 : this.class18) {
					lclass18.setClass10(null);
				}
			}

			this.class18 = class18;

			for (Class18 lclass18 : class18) {
				lclass18.setClass10(this);
			}
		}

		public void setClass19(Set<Class19> class19) {

			if (this.class19 != null) {

				for (Class19 lclass19 : this.class19) {
					lclass19.setClass10(null);
				}
			}

			this.class19 = class19;

			for (Class19 lclass19 : class19) {
				lclass19.setClass10(this);
			}
		}

		public void setClass20(Set<Class20> class20) {

			if (this.class20 != null) {

				for (Class20 lclass20 : this.class20) {
					lclass20.setClass10(null);
				}
			}

			this.class20 = class20;

			for (Class20 lclass20 : class20) {
				lclass20.setClass10(this);
			}
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass10(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass10(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass10(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass10(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass10(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass10(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass10(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass10(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass10(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass10(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass10(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass10(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass10(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass10(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass10(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass10(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass10(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass10(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass10(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass10(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass10(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass10(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class11")
	public static class Class11 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@OneToMany(
			mappedBy	 = "class11",
			targetEntity = Class12.class
		)
		private Set<Class12>		class12;
		@OneToMany(
			mappedBy	 = "class11",
			targetEntity = Class13.class
		)
		private Set<Class13>		class13;
		@OneToMany(
			mappedBy	 = "class11",
			targetEntity = Class14.class
		)
		private Set<Class14>		class14;
		@OneToMany(
			mappedBy	 = "class11",
			targetEntity = Class15.class
		)
		private Set<Class15>		class15;
		@OneToMany(
			mappedBy	 = "class11",
			targetEntity = Class16.class
		)
		private Set<Class16>		class16;
		@OneToMany(
			mappedBy	 = "class11",
			targetEntity = Class17.class
		)
		private Set<Class17>		class17;
		@OneToMany(
			mappedBy	 = "class11",
			targetEntity = Class18.class
		)
		private Set<Class18>		class18;
		@OneToMany(
			mappedBy	 = "class11",
			targetEntity = Class19.class
		)
		private Set<Class19>		class19;
		@OneToMany(
			mappedBy	 = "class11",
			targetEntity = Class20.class
		)
		private Set<Class20>		class20;
		@OneToMany(
			mappedBy	 = "class11",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class11",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class11",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class11",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class11",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class11",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class11",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class11",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class11",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class11",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class11",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class11() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass12(Class12 class12) {
			this.class12.add(class12);
			class12.setClass11(this);
		}

		public void addClass13(Class13 class13) {
			this.class13.add(class13);
			class13.setClass11(this);
		}

		public void addClass14(Class14 class14) {
			this.class14.add(class14);
			class14.setClass11(this);
		}

		public void addClass15(Class15 class15) {
			this.class15.add(class15);
			class15.setClass11(this);
		}

		public void addClass16(Class16 class16) {
			this.class16.add(class16);
			class16.setClass11(this);
		}

		public void addClass17(Class17 class17) {
			this.class17.add(class17);
			class17.setClass11(this);
		}

		public void addClass18(Class18 class18) {
			this.class18.add(class18);
			class18.setClass11(this);
		}

		public void addClass19(Class19 class19) {
			this.class19.add(class19);
			class19.setClass11(this);
		}

		public void addClass20(Class20 class20) {
			this.class20.add(class20);
			class20.setClass11(this);
		}

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass11(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass11(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass11(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass11(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass11(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass11(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass11(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass11(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass11(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass11(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass11(this);
		}

		public void removeClass12(Class12 class12) {
			this.class12.remove(class12);
			class12.setClass11(null);
		}

		public void removeClass13(Class13 class13) {
			this.class13.remove(class13);
			class13.setClass11(null);
		}

		public void removeClass14(Class14 class14) {
			this.class14.remove(class14);
			class14.setClass11(null);
		}

		public void removeClass15(Class15 class15) {
			this.class15.remove(class15);
			class15.setClass11(null);
		}

		public void removeClass16(Class16 class16) {
			this.class16.remove(class16);
			class16.setClass11(null);
		}

		public void removeClass17(Class17 class17) {
			this.class17.remove(class17);
			class17.setClass11(null);
		}

		public void removeClass18(Class18 class18) {
			this.class18.remove(class18);
			class18.setClass11(null);
		}

		public void removeClass19(Class19 class19) {
			this.class19.remove(class19);
			class19.setClass11(null);
		}

		public void removeClass20(Class20 class20) {
			this.class20.remove(class20);
			class20.setClass11(null);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass11(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass11(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass11(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass11(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass11(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass11(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass11(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass11(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass11(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass11(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass11(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Set<Class12> getClass12() {
			return this.class12;
		}

		public Set<Class13> getClass13() {
			return this.class13;
		}

		public Set<Class14> getClass14() {
			return this.class14;
		}

		public Set<Class15> getClass15() {
			return this.class15;
		}

		public Set<Class16> getClass16() {
			return this.class16;
		}

		public Set<Class17> getClass17() {
			return this.class17;
		}

		public Set<Class18> getClass18() {
			return this.class18;
		}

		public Set<Class19> getClass19() {
			return this.class19;
		}

		public Set<Class20> getClass20() {
			return this.class20;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass12(Set<Class12> class12) {

			if (this.class12 != null) {

				for (Class12 lclass12 : this.class12) {
					lclass12.setClass11(null);
				}
			}

			this.class12 = class12;

			for (Class12 lclass12 : class12) {
				lclass12.setClass11(this);
			}
		}

		public void setClass13(Set<Class13> class13) {

			if (this.class13 != null) {

				for (Class13 lclass13 : this.class13) {
					lclass13.setClass11(null);
				}
			}

			this.class13 = class13;

			for (Class13 lclass13 : class13) {
				lclass13.setClass11(this);
			}
		}

		public void setClass14(Set<Class14> class14) {

			if (this.class14 != null) {

				for (Class14 lclass14 : this.class14) {
					lclass14.setClass11(null);
				}
			}

			this.class14 = class14;

			for (Class14 lclass14 : class14) {
				lclass14.setClass11(this);
			}
		}

		public void setClass15(Set<Class15> class15) {

			if (this.class15 != null) {

				for (Class15 lclass15 : this.class15) {
					lclass15.setClass11(null);
				}
			}

			this.class15 = class15;

			for (Class15 lclass15 : class15) {
				lclass15.setClass11(this);
			}
		}

		public void setClass16(Set<Class16> class16) {

			if (this.class16 != null) {

				for (Class16 lclass16 : this.class16) {
					lclass16.setClass11(null);
				}
			}

			this.class16 = class16;

			for (Class16 lclass16 : class16) {
				lclass16.setClass11(this);
			}
		}

		public void setClass17(Set<Class17> class17) {

			if (this.class17 != null) {

				for (Class17 lclass17 : this.class17) {
					lclass17.setClass11(null);
				}
			}

			this.class17 = class17;

			for (Class17 lclass17 : class17) {
				lclass17.setClass11(this);
			}
		}

		public void setClass18(Set<Class18> class18) {

			if (this.class18 != null) {

				for (Class18 lclass18 : this.class18) {
					lclass18.setClass11(null);
				}
			}

			this.class18 = class18;

			for (Class18 lclass18 : class18) {
				lclass18.setClass11(this);
			}
		}

		public void setClass19(Set<Class19> class19) {

			if (this.class19 != null) {

				for (Class19 lclass19 : this.class19) {
					lclass19.setClass11(null);
				}
			}

			this.class19 = class19;

			for (Class19 lclass19 : class19) {
				lclass19.setClass11(this);
			}
		}

		public void setClass20(Set<Class20> class20) {

			if (this.class20 != null) {

				for (Class20 lclass20 : this.class20) {
					lclass20.setClass11(null);
				}
			}

			this.class20 = class20;

			for (Class20 lclass20 : class20) {
				lclass20.setClass11(this);
			}
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass11(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass11(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass11(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass11(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass11(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass11(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass11(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass11(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass11(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass11(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass11(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass11(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass11(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass11(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass11(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass11(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass11(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass11(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass11(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass11(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass11(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass11(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class12")
	public static class Class12 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@JoinColumn(name = "class11")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class11			 class11;
		@OneToMany(
			mappedBy	 = "class12",
			targetEntity = Class13.class
		)
		private Set<Class13>		class13;
		@OneToMany(
			mappedBy	 = "class12",
			targetEntity = Class14.class
		)
		private Set<Class14>		class14;
		@OneToMany(
			mappedBy	 = "class12",
			targetEntity = Class15.class
		)
		private Set<Class15>		class15;
		@OneToMany(
			mappedBy	 = "class12",
			targetEntity = Class16.class
		)
		private Set<Class16>		class16;
		@OneToMany(
			mappedBy	 = "class12",
			targetEntity = Class17.class
		)
		private Set<Class17>		class17;
		@OneToMany(
			mappedBy	 = "class12",
			targetEntity = Class18.class
		)
		private Set<Class18>		class18;
		@OneToMany(
			mappedBy	 = "class12",
			targetEntity = Class19.class
		)
		private Set<Class19>		class19;
		@OneToMany(
			mappedBy	 = "class12",
			targetEntity = Class20.class
		)
		private Set<Class20>		class20;
		@OneToMany(
			mappedBy	 = "class12",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class12",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class12",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class12",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class12",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class12",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class12",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class12",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class12",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class12",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class12",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class12() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass13(Class13 class13) {
			this.class13.add(class13);
			class13.setClass12(this);
		}

		public void addClass14(Class14 class14) {
			this.class14.add(class14);
			class14.setClass12(this);
		}

		public void addClass15(Class15 class15) {
			this.class15.add(class15);
			class15.setClass12(this);
		}

		public void addClass16(Class16 class16) {
			this.class16.add(class16);
			class16.setClass12(this);
		}

		public void addClass17(Class17 class17) {
			this.class17.add(class17);
			class17.setClass12(this);
		}

		public void addClass18(Class18 class18) {
			this.class18.add(class18);
			class18.setClass12(this);
		}

		public void addClass19(Class19 class19) {
			this.class19.add(class19);
			class19.setClass12(this);
		}

		public void addClass20(Class20 class20) {
			this.class20.add(class20);
			class20.setClass12(this);
		}

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass12(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass12(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass12(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass12(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass12(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass12(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass12(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass12(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass12(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass12(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass12(this);
		}

		public void removeClass13(Class13 class13) {
			this.class13.remove(class13);
			class13.setClass12(null);
		}

		public void removeClass14(Class14 class14) {
			this.class14.remove(class14);
			class14.setClass12(null);
		}

		public void removeClass15(Class15 class15) {
			this.class15.remove(class15);
			class15.setClass12(null);
		}

		public void removeClass16(Class16 class16) {
			this.class16.remove(class16);
			class16.setClass12(null);
		}

		public void removeClass17(Class17 class17) {
			this.class17.remove(class17);
			class17.setClass12(null);
		}

		public void removeClass18(Class18 class18) {
			this.class18.remove(class18);
			class18.setClass12(null);
		}

		public void removeClass19(Class19 class19) {
			this.class19.remove(class19);
			class19.setClass12(null);
		}

		public void removeClass20(Class20 class20) {
			this.class20.remove(class20);
			class20.setClass12(null);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass12(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass12(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass12(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass12(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass12(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass12(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass12(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass12(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass12(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass12(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass12(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Class11 getClass11() {
			return this.class11;
		}

		public Set<Class13> getClass13() {
			return this.class13;
		}

		public Set<Class14> getClass14() {
			return this.class14;
		}

		public Set<Class15> getClass15() {
			return this.class15;
		}

		public Set<Class16> getClass16() {
			return this.class16;
		}

		public Set<Class17> getClass17() {
			return this.class17;
		}

		public Set<Class18> getClass18() {
			return this.class18;
		}

		public Set<Class19> getClass19() {
			return this.class19;
		}

		public Set<Class20> getClass20() {
			return this.class20;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass11(Class11 class11) {
			this.class11 = class11;
		}

		public void setClass13(Set<Class13> class13) {

			if (this.class13 != null) {

				for (Class13 lclass13 : this.class13) {
					lclass13.setClass12(null);
				}
			}

			this.class13 = class13;

			for (Class13 lclass13 : class13) {
				lclass13.setClass12(this);
			}
		}

		public void setClass14(Set<Class14> class14) {

			if (this.class14 != null) {

				for (Class14 lclass14 : this.class14) {
					lclass14.setClass12(null);
				}
			}

			this.class14 = class14;

			for (Class14 lclass14 : class14) {
				lclass14.setClass12(this);
			}
		}

		public void setClass15(Set<Class15> class15) {

			if (this.class15 != null) {

				for (Class15 lclass15 : this.class15) {
					lclass15.setClass12(null);
				}
			}

			this.class15 = class15;

			for (Class15 lclass15 : class15) {
				lclass15.setClass12(this);
			}
		}

		public void setClass16(Set<Class16> class16) {

			if (this.class16 != null) {

				for (Class16 lclass16 : this.class16) {
					lclass16.setClass12(null);
				}
			}

			this.class16 = class16;

			for (Class16 lclass16 : class16) {
				lclass16.setClass12(this);
			}
		}

		public void setClass17(Set<Class17> class17) {

			if (this.class17 != null) {

				for (Class17 lclass17 : this.class17) {
					lclass17.setClass12(null);
				}
			}

			this.class17 = class17;

			for (Class17 lclass17 : class17) {
				lclass17.setClass12(this);
			}
		}

		public void setClass18(Set<Class18> class18) {

			if (this.class18 != null) {

				for (Class18 lclass18 : this.class18) {
					lclass18.setClass12(null);
				}
			}

			this.class18 = class18;

			for (Class18 lclass18 : class18) {
				lclass18.setClass12(this);
			}
		}

		public void setClass19(Set<Class19> class19) {

			if (this.class19 != null) {

				for (Class19 lclass19 : this.class19) {
					lclass19.setClass12(null);
				}
			}

			this.class19 = class19;

			for (Class19 lclass19 : class19) {
				lclass19.setClass12(this);
			}
		}

		public void setClass20(Set<Class20> class20) {

			if (this.class20 != null) {

				for (Class20 lclass20 : this.class20) {
					lclass20.setClass12(null);
				}
			}

			this.class20 = class20;

			for (Class20 lclass20 : class20) {
				lclass20.setClass12(this);
			}
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass12(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass12(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass12(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass12(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass12(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass12(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass12(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass12(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass12(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass12(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass12(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass12(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass12(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass12(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass12(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass12(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass12(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass12(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass12(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass12(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass12(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass12(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class13")
	public static class Class13 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@JoinColumn(name = "class11")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class11			 class11;
		@JoinColumn(name = "class12")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class12			 class12;
		@OneToMany(
			mappedBy	 = "class13",
			targetEntity = Class14.class
		)
		private Set<Class14>		class14;
		@OneToMany(
			mappedBy	 = "class13",
			targetEntity = Class15.class
		)
		private Set<Class15>		class15;
		@OneToMany(
			mappedBy	 = "class13",
			targetEntity = Class16.class
		)
		private Set<Class16>		class16;
		@OneToMany(
			mappedBy	 = "class13",
			targetEntity = Class17.class
		)
		private Set<Class17>		class17;
		@OneToMany(
			mappedBy	 = "class13",
			targetEntity = Class18.class
		)
		private Set<Class18>		class18;
		@OneToMany(
			mappedBy	 = "class13",
			targetEntity = Class19.class
		)
		private Set<Class19>		class19;
		@OneToMany(
			mappedBy	 = "class13",
			targetEntity = Class20.class
		)
		private Set<Class20>		class20;
		@OneToMany(
			mappedBy	 = "class13",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class13",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class13",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class13",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class13",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class13",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class13",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class13",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class13",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class13",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class13",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class13() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass14(Class14 class14) {
			this.class14.add(class14);
			class14.setClass13(this);
		}

		public void addClass15(Class15 class15) {
			this.class15.add(class15);
			class15.setClass13(this);
		}

		public void addClass16(Class16 class16) {
			this.class16.add(class16);
			class16.setClass13(this);
		}

		public void addClass17(Class17 class17) {
			this.class17.add(class17);
			class17.setClass13(this);
		}

		public void addClass18(Class18 class18) {
			this.class18.add(class18);
			class18.setClass13(this);
		}

		public void addClass19(Class19 class19) {
			this.class19.add(class19);
			class19.setClass13(this);
		}

		public void addClass20(Class20 class20) {
			this.class20.add(class20);
			class20.setClass13(this);
		}

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass13(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass13(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass13(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass13(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass13(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass13(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass13(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass13(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass13(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass13(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass13(this);
		}

		public void removeClass14(Class14 class14) {
			this.class14.remove(class14);
			class14.setClass13(null);
		}

		public void removeClass15(Class15 class15) {
			this.class15.remove(class15);
			class15.setClass13(null);
		}

		public void removeClass16(Class16 class16) {
			this.class16.remove(class16);
			class16.setClass13(null);
		}

		public void removeClass17(Class17 class17) {
			this.class17.remove(class17);
			class17.setClass13(null);
		}

		public void removeClass18(Class18 class18) {
			this.class18.remove(class18);
			class18.setClass13(null);
		}

		public void removeClass19(Class19 class19) {
			this.class19.remove(class19);
			class19.setClass13(null);
		}

		public void removeClass20(Class20 class20) {
			this.class20.remove(class20);
			class20.setClass13(null);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass13(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass13(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass13(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass13(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass13(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass13(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass13(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass13(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass13(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass13(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass13(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Class11 getClass11() {
			return this.class11;
		}

		public Class12 getClass12() {
			return this.class12;
		}

		public Set<Class14> getClass14() {
			return this.class14;
		}

		public Set<Class15> getClass15() {
			return this.class15;
		}

		public Set<Class16> getClass16() {
			return this.class16;
		}

		public Set<Class17> getClass17() {
			return this.class17;
		}

		public Set<Class18> getClass18() {
			return this.class18;
		}

		public Set<Class19> getClass19() {
			return this.class19;
		}

		public Set<Class20> getClass20() {
			return this.class20;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass11(Class11 class11) {
			this.class11 = class11;
		}

		public void setClass12(Class12 class12) {
			this.class12 = class12;
		}

		public void setClass14(Set<Class14> class14) {

			if (this.class14 != null) {

				for (Class14 lclass14 : this.class14) {
					lclass14.setClass13(null);
				}
			}

			this.class14 = class14;

			for (Class14 lclass14 : class14) {
				lclass14.setClass13(this);
			}
		}

		public void setClass15(Set<Class15> class15) {

			if (this.class15 != null) {

				for (Class15 lclass15 : this.class15) {
					lclass15.setClass13(null);
				}
			}

			this.class15 = class15;

			for (Class15 lclass15 : class15) {
				lclass15.setClass13(this);
			}
		}

		public void setClass16(Set<Class16> class16) {

			if (this.class16 != null) {

				for (Class16 lclass16 : this.class16) {
					lclass16.setClass13(null);
				}
			}

			this.class16 = class16;

			for (Class16 lclass16 : class16) {
				lclass16.setClass13(this);
			}
		}

		public void setClass17(Set<Class17> class17) {

			if (this.class17 != null) {

				for (Class17 lclass17 : this.class17) {
					lclass17.setClass13(null);
				}
			}

			this.class17 = class17;

			for (Class17 lclass17 : class17) {
				lclass17.setClass13(this);
			}
		}

		public void setClass18(Set<Class18> class18) {

			if (this.class18 != null) {

				for (Class18 lclass18 : this.class18) {
					lclass18.setClass13(null);
				}
			}

			this.class18 = class18;

			for (Class18 lclass18 : class18) {
				lclass18.setClass13(this);
			}
		}

		public void setClass19(Set<Class19> class19) {

			if (this.class19 != null) {

				for (Class19 lclass19 : this.class19) {
					lclass19.setClass13(null);
				}
			}

			this.class19 = class19;

			for (Class19 lclass19 : class19) {
				lclass19.setClass13(this);
			}
		}

		public void setClass20(Set<Class20> class20) {

			if (this.class20 != null) {

				for (Class20 lclass20 : this.class20) {
					lclass20.setClass13(null);
				}
			}

			this.class20 = class20;

			for (Class20 lclass20 : class20) {
				lclass20.setClass13(this);
			}
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass13(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass13(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass13(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass13(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass13(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass13(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass13(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass13(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass13(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass13(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass13(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass13(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass13(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass13(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass13(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass13(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass13(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass13(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass13(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass13(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass13(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass13(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class14")
	public static class Class14 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@JoinColumn(name = "class11")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class11			 class11;
		@JoinColumn(name = "class12")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class12			 class12;
		@JoinColumn(name = "class13")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class13			 class13;
		@OneToMany(
			mappedBy	 = "class14",
			targetEntity = Class15.class
		)
		private Set<Class15>		class15;
		@OneToMany(
			mappedBy	 = "class14",
			targetEntity = Class16.class
		)
		private Set<Class16>		class16;
		@OneToMany(
			mappedBy	 = "class14",
			targetEntity = Class17.class
		)
		private Set<Class17>		class17;
		@OneToMany(
			mappedBy	 = "class14",
			targetEntity = Class18.class
		)
		private Set<Class18>		class18;
		@OneToMany(
			mappedBy	 = "class14",
			targetEntity = Class19.class
		)
		private Set<Class19>		class19;
		@OneToMany(
			mappedBy	 = "class14",
			targetEntity = Class20.class
		)
		private Set<Class20>		class20;
		@OneToMany(
			mappedBy	 = "class14",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class14",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class14",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class14",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class14",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class14",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class14",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class14",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class14",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class14",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class14",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class14() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass15(Class15 class15) {
			this.class15.add(class15);
			class15.setClass14(this);
		}

		public void addClass16(Class16 class16) {
			this.class16.add(class16);
			class16.setClass14(this);
		}

		public void addClass17(Class17 class17) {
			this.class17.add(class17);
			class17.setClass14(this);
		}

		public void addClass18(Class18 class18) {
			this.class18.add(class18);
			class18.setClass14(this);
		}

		public void addClass19(Class19 class19) {
			this.class19.add(class19);
			class19.setClass14(this);
		}

		public void addClass20(Class20 class20) {
			this.class20.add(class20);
			class20.setClass14(this);
		}

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass14(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass14(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass14(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass14(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass14(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass14(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass14(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass14(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass14(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass14(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass14(this);
		}

		public void removeClass15(Class15 class15) {
			this.class15.remove(class15);
			class15.setClass14(null);
		}

		public void removeClass16(Class16 class16) {
			this.class16.remove(class16);
			class16.setClass14(null);
		}

		public void removeClass17(Class17 class17) {
			this.class17.remove(class17);
			class17.setClass14(null);
		}

		public void removeClass18(Class18 class18) {
			this.class18.remove(class18);
			class18.setClass14(null);
		}

		public void removeClass19(Class19 class19) {
			this.class19.remove(class19);
			class19.setClass14(null);
		}

		public void removeClass20(Class20 class20) {
			this.class20.remove(class20);
			class20.setClass14(null);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass14(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass14(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass14(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass14(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass14(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass14(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass14(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass14(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass14(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass14(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass14(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Class11 getClass11() {
			return this.class11;
		}

		public Class12 getClass12() {
			return this.class12;
		}

		public Class13 getClass13() {
			return this.class13;
		}

		public Set<Class15> getClass15() {
			return this.class15;
		}

		public Set<Class16> getClass16() {
			return this.class16;
		}

		public Set<Class17> getClass17() {
			return this.class17;
		}

		public Set<Class18> getClass18() {
			return this.class18;
		}

		public Set<Class19> getClass19() {
			return this.class19;
		}

		public Set<Class20> getClass20() {
			return this.class20;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass11(Class11 class11) {
			this.class11 = class11;
		}

		public void setClass12(Class12 class12) {
			this.class12 = class12;
		}

		public void setClass13(Class13 class13) {
			this.class13 = class13;
		}

		public void setClass15(Set<Class15> class15) {

			if (this.class15 != null) {

				for (Class15 lclass15 : this.class15) {
					lclass15.setClass14(null);
				}
			}

			this.class15 = class15;

			for (Class15 lclass15 : class15) {
				lclass15.setClass14(this);
			}
		}

		public void setClass16(Set<Class16> class16) {

			if (this.class16 != null) {

				for (Class16 lclass16 : this.class16) {
					lclass16.setClass14(null);
				}
			}

			this.class16 = class16;

			for (Class16 lclass16 : class16) {
				lclass16.setClass14(this);
			}
		}

		public void setClass17(Set<Class17> class17) {

			if (this.class17 != null) {

				for (Class17 lclass17 : this.class17) {
					lclass17.setClass14(null);
				}
			}

			this.class17 = class17;

			for (Class17 lclass17 : class17) {
				lclass17.setClass14(this);
			}
		}

		public void setClass18(Set<Class18> class18) {

			if (this.class18 != null) {

				for (Class18 lclass18 : this.class18) {
					lclass18.setClass14(null);
				}
			}

			this.class18 = class18;

			for (Class18 lclass18 : class18) {
				lclass18.setClass14(this);
			}
		}

		public void setClass19(Set<Class19> class19) {

			if (this.class19 != null) {

				for (Class19 lclass19 : this.class19) {
					lclass19.setClass14(null);
				}
			}

			this.class19 = class19;

			for (Class19 lclass19 : class19) {
				lclass19.setClass14(this);
			}
		}

		public void setClass20(Set<Class20> class20) {

			if (this.class20 != null) {

				for (Class20 lclass20 : this.class20) {
					lclass20.setClass14(null);
				}
			}

			this.class20 = class20;

			for (Class20 lclass20 : class20) {
				lclass20.setClass14(this);
			}
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass14(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass14(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass14(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass14(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass14(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass14(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass14(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass14(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass14(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass14(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass14(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass14(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass14(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass14(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass14(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass14(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass14(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass14(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass14(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass14(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass14(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass14(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class15")
	public static class Class15 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@JoinColumn(name = "class11")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class11			 class11;
		@JoinColumn(name = "class12")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class12			 class12;
		@JoinColumn(name = "class13")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class13			 class13;
		@JoinColumn(name = "class14")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class14			 class14;
		@OneToMany(
			mappedBy	 = "class15",
			targetEntity = Class16.class
		)
		private Set<Class16>		class16;
		@OneToMany(
			mappedBy	 = "class15",
			targetEntity = Class17.class
		)
		private Set<Class17>		class17;
		@OneToMany(
			mappedBy	 = "class15",
			targetEntity = Class18.class
		)
		private Set<Class18>		class18;
		@OneToMany(
			mappedBy	 = "class15",
			targetEntity = Class19.class
		)
		private Set<Class19>		class19;
		@OneToMany(
			mappedBy	 = "class15",
			targetEntity = Class20.class
		)
		private Set<Class20>		class20;
		@OneToMany(
			mappedBy	 = "class15",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class15",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class15",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class15",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class15",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class15",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class15",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class15",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class15",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class15",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class15",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class15() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass16(Class16 class16) {
			this.class16.add(class16);
			class16.setClass15(this);
		}

		public void addClass17(Class17 class17) {
			this.class17.add(class17);
			class17.setClass15(this);
		}

		public void addClass18(Class18 class18) {
			this.class18.add(class18);
			class18.setClass15(this);
		}

		public void addClass19(Class19 class19) {
			this.class19.add(class19);
			class19.setClass15(this);
		}

		public void addClass20(Class20 class20) {
			this.class20.add(class20);
			class20.setClass15(this);
		}

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass15(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass15(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass15(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass15(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass15(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass15(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass15(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass15(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass15(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass15(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass15(this);
		}

		public void removeClass16(Class16 class16) {
			this.class16.remove(class16);
			class16.setClass15(null);
		}

		public void removeClass17(Class17 class17) {
			this.class17.remove(class17);
			class17.setClass15(null);
		}

		public void removeClass18(Class18 class18) {
			this.class18.remove(class18);
			class18.setClass15(null);
		}

		public void removeClass19(Class19 class19) {
			this.class19.remove(class19);
			class19.setClass15(null);
		}

		public void removeClass20(Class20 class20) {
			this.class20.remove(class20);
			class20.setClass15(null);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass15(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass15(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass15(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass15(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass15(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass15(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass15(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass15(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass15(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass15(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass15(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Class11 getClass11() {
			return this.class11;
		}

		public Class12 getClass12() {
			return this.class12;
		}

		public Class13 getClass13() {
			return this.class13;
		}

		public Class14 getClass14() {
			return this.class14;
		}

		public Set<Class16> getClass16() {
			return this.class16;
		}

		public Set<Class17> getClass17() {
			return this.class17;
		}

		public Set<Class18> getClass18() {
			return this.class18;
		}

		public Set<Class19> getClass19() {
			return this.class19;
		}

		public Set<Class20> getClass20() {
			return this.class20;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass11(Class11 class11) {
			this.class11 = class11;
		}

		public void setClass12(Class12 class12) {
			this.class12 = class12;
		}

		public void setClass13(Class13 class13) {
			this.class13 = class13;
		}

		public void setClass14(Class14 class14) {
			this.class14 = class14;
		}

		public void setClass16(Set<Class16> class16) {

			if (this.class16 != null) {

				for (Class16 lclass16 : this.class16) {
					lclass16.setClass15(null);
				}
			}

			this.class16 = class16;

			for (Class16 lclass16 : class16) {
				lclass16.setClass15(this);
			}
		}

		public void setClass17(Set<Class17> class17) {

			if (this.class17 != null) {

				for (Class17 lclass17 : this.class17) {
					lclass17.setClass15(null);
				}
			}

			this.class17 = class17;

			for (Class17 lclass17 : class17) {
				lclass17.setClass15(this);
			}
		}

		public void setClass18(Set<Class18> class18) {

			if (this.class18 != null) {

				for (Class18 lclass18 : this.class18) {
					lclass18.setClass15(null);
				}
			}

			this.class18 = class18;

			for (Class18 lclass18 : class18) {
				lclass18.setClass15(this);
			}
		}

		public void setClass19(Set<Class19> class19) {

			if (this.class19 != null) {

				for (Class19 lclass19 : this.class19) {
					lclass19.setClass15(null);
				}
			}

			this.class19 = class19;

			for (Class19 lclass19 : class19) {
				lclass19.setClass15(this);
			}
		}

		public void setClass20(Set<Class20> class20) {

			if (this.class20 != null) {

				for (Class20 lclass20 : this.class20) {
					lclass20.setClass15(null);
				}
			}

			this.class20 = class20;

			for (Class20 lclass20 : class20) {
				lclass20.setClass15(this);
			}
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass15(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass15(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass15(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass15(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass15(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass15(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass15(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass15(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass15(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass15(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass15(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass15(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass15(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass15(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass15(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass15(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass15(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass15(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass15(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass15(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass15(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass15(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class16")
	public static class Class16 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@JoinColumn(name = "class11")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class11			 class11;
		@JoinColumn(name = "class12")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class12			 class12;
		@JoinColumn(name = "class13")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class13			 class13;
		@JoinColumn(name = "class14")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class14			 class14;
		@JoinColumn(name = "class15")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class15			 class15;
		@OneToMany(
			mappedBy	 = "class16",
			targetEntity = Class17.class
		)
		private Set<Class17>		class17;
		@OneToMany(
			mappedBy	 = "class16",
			targetEntity = Class18.class
		)
		private Set<Class18>		class18;
		@OneToMany(
			mappedBy	 = "class16",
			targetEntity = Class19.class
		)
		private Set<Class19>		class19;
		@OneToMany(
			mappedBy	 = "class16",
			targetEntity = Class20.class
		)
		private Set<Class20>		class20;
		@OneToMany(
			mappedBy	 = "class16",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class16",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class16",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class16",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class16",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class16",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class16",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class16",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class16",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class16",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class16",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class16() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass17(Class17 class17) {
			this.class17.add(class17);
			class17.setClass16(this);
		}

		public void addClass18(Class18 class18) {
			this.class18.add(class18);
			class18.setClass16(this);
		}

		public void addClass19(Class19 class19) {
			this.class19.add(class19);
			class19.setClass16(this);
		}

		public void addClass20(Class20 class20) {
			this.class20.add(class20);
			class20.setClass16(this);
		}

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass16(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass16(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass16(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass16(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass16(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass16(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass16(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass16(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass16(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass16(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass16(this);
		}

		public void removeClass17(Class17 class17) {
			this.class17.remove(class17);
			class17.setClass16(null);
		}

		public void removeClass18(Class18 class18) {
			this.class18.remove(class18);
			class18.setClass16(null);
		}

		public void removeClass19(Class19 class19) {
			this.class19.remove(class19);
			class19.setClass16(null);
		}

		public void removeClass20(Class20 class20) {
			this.class20.remove(class20);
			class20.setClass16(null);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass16(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass16(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass16(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass16(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass16(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass16(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass16(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass16(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass16(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass16(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass16(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Class11 getClass11() {
			return this.class11;
		}

		public Class12 getClass12() {
			return this.class12;
		}

		public Class13 getClass13() {
			return this.class13;
		}

		public Class14 getClass14() {
			return this.class14;
		}

		public Class15 getClass15() {
			return this.class15;
		}

		public Set<Class17> getClass17() {
			return this.class17;
		}

		public Set<Class18> getClass18() {
			return this.class18;
		}

		public Set<Class19> getClass19() {
			return this.class19;
		}

		public Set<Class20> getClass20() {
			return this.class20;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass11(Class11 class11) {
			this.class11 = class11;
		}

		public void setClass12(Class12 class12) {
			this.class12 = class12;
		}

		public void setClass13(Class13 class13) {
			this.class13 = class13;
		}

		public void setClass14(Class14 class14) {
			this.class14 = class14;
		}

		public void setClass15(Class15 class15) {
			this.class15 = class15;
		}

		public void setClass17(Set<Class17> class17) {

			if (this.class17 != null) {

				for (Class17 lclass17 : this.class17) {
					lclass17.setClass16(null);
				}
			}

			this.class17 = class17;

			for (Class17 lclass17 : class17) {
				lclass17.setClass16(this);
			}
		}

		public void setClass18(Set<Class18> class18) {

			if (this.class18 != null) {

				for (Class18 lclass18 : this.class18) {
					lclass18.setClass16(null);
				}
			}

			this.class18 = class18;

			for (Class18 lclass18 : class18) {
				lclass18.setClass16(this);
			}
		}

		public void setClass19(Set<Class19> class19) {

			if (this.class19 != null) {

				for (Class19 lclass19 : this.class19) {
					lclass19.setClass16(null);
				}
			}

			this.class19 = class19;

			for (Class19 lclass19 : class19) {
				lclass19.setClass16(this);
			}
		}

		public void setClass20(Set<Class20> class20) {

			if (this.class20 != null) {

				for (Class20 lclass20 : this.class20) {
					lclass20.setClass16(null);
				}
			}

			this.class20 = class20;

			for (Class20 lclass20 : class20) {
				lclass20.setClass16(this);
			}
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass16(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass16(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass16(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass16(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass16(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass16(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass16(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass16(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass16(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass16(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass16(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass16(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass16(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass16(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass16(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass16(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass16(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass16(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass16(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass16(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass16(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass16(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class17")
	public static class Class17 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@JoinColumn(name = "class11")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class11			 class11;
		@JoinColumn(name = "class12")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class12			 class12;
		@JoinColumn(name = "class13")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class13			 class13;
		@JoinColumn(name = "class14")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class14			 class14;
		@JoinColumn(name = "class15")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class15			 class15;
		@JoinColumn(name = "class16")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class16			 class16;
		@OneToMany(
			mappedBy	 = "class17",
			targetEntity = Class18.class
		)
		private Set<Class18>		class18;
		@OneToMany(
			mappedBy	 = "class17",
			targetEntity = Class19.class
		)
		private Set<Class19>		class19;
		@OneToMany(
			mappedBy	 = "class17",
			targetEntity = Class20.class
		)
		private Set<Class20>		class20;
		@OneToMany(
			mappedBy	 = "class17",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class17",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class17",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class17",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class17",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class17",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class17",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class17",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class17",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class17",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class17",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class17() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass18(Class18 class18) {
			this.class18.add(class18);
			class18.setClass17(this);
		}

		public void addClass19(Class19 class19) {
			this.class19.add(class19);
			class19.setClass17(this);
		}

		public void addClass20(Class20 class20) {
			this.class20.add(class20);
			class20.setClass17(this);
		}

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass17(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass17(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass17(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass17(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass17(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass17(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass17(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass17(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass17(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass17(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass17(this);
		}

		public void removeClass18(Class18 class18) {
			this.class18.remove(class18);
			class18.setClass17(null);
		}

		public void removeClass19(Class19 class19) {
			this.class19.remove(class19);
			class19.setClass17(null);
		}

		public void removeClass20(Class20 class20) {
			this.class20.remove(class20);
			class20.setClass17(null);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass17(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass17(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass17(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass17(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass17(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass17(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass17(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass17(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass17(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass17(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass17(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Class11 getClass11() {
			return this.class11;
		}

		public Class12 getClass12() {
			return this.class12;
		}

		public Class13 getClass13() {
			return this.class13;
		}

		public Class14 getClass14() {
			return this.class14;
		}

		public Class15 getClass15() {
			return this.class15;
		}

		public Class16 getClass16() {
			return this.class16;
		}

		public Set<Class18> getClass18() {
			return this.class18;
		}

		public Set<Class19> getClass19() {
			return this.class19;
		}

		public Set<Class20> getClass20() {
			return this.class20;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass11(Class11 class11) {
			this.class11 = class11;
		}

		public void setClass12(Class12 class12) {
			this.class12 = class12;
		}

		public void setClass13(Class13 class13) {
			this.class13 = class13;
		}

		public void setClass14(Class14 class14) {
			this.class14 = class14;
		}

		public void setClass15(Class15 class15) {
			this.class15 = class15;
		}

		public void setClass16(Class16 class16) {
			this.class16 = class16;
		}

		public void setClass18(Set<Class18> class18) {

			if (this.class18 != null) {

				for (Class18 lclass18 : this.class18) {
					lclass18.setClass17(null);
				}
			}

			this.class18 = class18;

			for (Class18 lclass18 : class18) {
				lclass18.setClass17(this);
			}
		}

		public void setClass19(Set<Class19> class19) {

			if (this.class19 != null) {

				for (Class19 lclass19 : this.class19) {
					lclass19.setClass17(null);
				}
			}

			this.class19 = class19;

			for (Class19 lclass19 : class19) {
				lclass19.setClass17(this);
			}
		}

		public void setClass20(Set<Class20> class20) {

			if (this.class20 != null) {

				for (Class20 lclass20 : this.class20) {
					lclass20.setClass17(null);
				}
			}

			this.class20 = class20;

			for (Class20 lclass20 : class20) {
				lclass20.setClass17(this);
			}
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass17(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass17(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass17(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass17(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass17(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass17(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass17(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass17(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass17(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass17(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass17(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass17(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass17(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass17(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass17(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass17(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass17(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass17(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass17(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass17(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass17(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass17(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class18")
	public static class Class18 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@JoinColumn(name = "class11")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class11			 class11;
		@JoinColumn(name = "class12")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class12			 class12;
		@JoinColumn(name = "class13")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class13			 class13;
		@JoinColumn(name = "class14")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class14			 class14;
		@JoinColumn(name = "class15")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class15			 class15;
		@JoinColumn(name = "class16")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class16			 class16;
		@JoinColumn(name = "class17")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class17			 class17;
		@OneToMany(
			mappedBy	 = "class18",
			targetEntity = Class19.class
		)
		private Set<Class19>		class19;
		@OneToMany(
			mappedBy	 = "class18",
			targetEntity = Class20.class
		)
		private Set<Class20>		class20;
		@OneToMany(
			mappedBy	 = "class18",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class18",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class18",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class18",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class18",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class18",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class18",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class18",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class18",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class18",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class18",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class18() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass19(Class19 class19) {
			this.class19.add(class19);
			class19.setClass18(this);
		}

		public void addClass20(Class20 class20) {
			this.class20.add(class20);
			class20.setClass18(this);
		}

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass18(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass18(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass18(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass18(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass18(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass18(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass18(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass18(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass18(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass18(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass18(this);
		}

		public void removeClass19(Class19 class19) {
			this.class19.remove(class19);
			class19.setClass18(null);
		}

		public void removeClass20(Class20 class20) {
			this.class20.remove(class20);
			class20.setClass18(null);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass18(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass18(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass18(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass18(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass18(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass18(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass18(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass18(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass18(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass18(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass18(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Class11 getClass11() {
			return this.class11;
		}

		public Class12 getClass12() {
			return this.class12;
		}

		public Class13 getClass13() {
			return this.class13;
		}

		public Class14 getClass14() {
			return this.class14;
		}

		public Class15 getClass15() {
			return this.class15;
		}

		public Class16 getClass16() {
			return this.class16;
		}

		public Class17 getClass17() {
			return this.class17;
		}

		public Set<Class19> getClass19() {
			return this.class19;
		}

		public Set<Class20> getClass20() {
			return this.class20;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass11(Class11 class11) {
			this.class11 = class11;
		}

		public void setClass12(Class12 class12) {
			this.class12 = class12;
		}

		public void setClass13(Class13 class13) {
			this.class13 = class13;
		}

		public void setClass14(Class14 class14) {
			this.class14 = class14;
		}

		public void setClass15(Class15 class15) {
			this.class15 = class15;
		}

		public void setClass16(Class16 class16) {
			this.class16 = class16;
		}

		public void setClass17(Class17 class17) {
			this.class17 = class17;
		}

		public void setClass19(Set<Class19> class19) {

			if (this.class19 != null) {

				for (Class19 lclass19 : this.class19) {
					lclass19.setClass18(null);
				}
			}

			this.class19 = class19;

			for (Class19 lclass19 : class19) {
				lclass19.setClass18(this);
			}
		}

		public void setClass20(Set<Class20> class20) {

			if (this.class20 != null) {

				for (Class20 lclass20 : this.class20) {
					lclass20.setClass18(null);
				}
			}

			this.class20 = class20;

			for (Class20 lclass20 : class20) {
				lclass20.setClass18(this);
			}
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass18(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass18(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass18(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass18(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass18(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass18(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass18(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass18(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass18(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass18(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass18(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass18(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass18(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass18(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass18(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass18(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass18(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass18(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass18(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass18(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass18(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass18(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class19")
	public static class Class19 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@JoinColumn(name = "class11")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class11			 class11;
		@JoinColumn(name = "class12")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class12			 class12;
		@JoinColumn(name = "class13")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class13			 class13;
		@JoinColumn(name = "class14")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class14			 class14;
		@JoinColumn(name = "class15")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class15			 class15;
		@JoinColumn(name = "class16")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class16			 class16;
		@JoinColumn(name = "class17")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class17			 class17;
		@JoinColumn(name = "class18")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class18			 class18;
		@OneToMany(
			mappedBy	 = "class19",
			targetEntity = Class20.class
		)
		private Set<Class20>		class20;
		@OneToMany(
			mappedBy	 = "class19",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class19",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class19",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class19",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class19",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class19",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class19",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class19",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class19",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class19",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class19",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class19() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass20(Class20 class20) {
			this.class20.add(class20);
			class20.setClass19(this);
		}

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass19(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass19(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass19(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass19(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass19(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass19(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass19(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass19(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass19(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass19(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass19(this);
		}

		public void removeClass20(Class20 class20) {
			this.class20.remove(class20);
			class20.setClass19(null);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass19(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass19(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass19(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass19(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass19(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass19(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass19(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass19(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass19(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass19(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass19(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Class11 getClass11() {
			return this.class11;
		}

		public Class12 getClass12() {
			return this.class12;
		}

		public Class13 getClass13() {
			return this.class13;
		}

		public Class14 getClass14() {
			return this.class14;
		}

		public Class15 getClass15() {
			return this.class15;
		}

		public Class16 getClass16() {
			return this.class16;
		}

		public Class17 getClass17() {
			return this.class17;
		}

		public Class18 getClass18() {
			return this.class18;
		}

		public Set<Class20> getClass20() {
			return this.class20;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass11(Class11 class11) {
			this.class11 = class11;
		}

		public void setClass12(Class12 class12) {
			this.class12 = class12;
		}

		public void setClass13(Class13 class13) {
			this.class13 = class13;
		}

		public void setClass14(Class14 class14) {
			this.class14 = class14;
		}

		public void setClass15(Class15 class15) {
			this.class15 = class15;
		}

		public void setClass16(Class16 class16) {
			this.class16 = class16;
		}

		public void setClass17(Class17 class17) {
			this.class17 = class17;
		}

		public void setClass18(Class18 class18) {
			this.class18 = class18;
		}

		public void setClass20(Set<Class20> class20) {

			if (this.class20 != null) {

				for (Class20 lclass20 : this.class20) {
					lclass20.setClass19(null);
				}
			}

			this.class20 = class20;

			for (Class20 lclass20 : class20) {
				lclass20.setClass19(this);
			}
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass19(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass19(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass19(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass19(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass19(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass19(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass19(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass19(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass19(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass19(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass19(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass19(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass19(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass19(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass19(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass19(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass19(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass19(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass19(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass19(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass19(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass19(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class20")
	public static class Class20 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@JoinColumn(name = "class11")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class11			 class11;
		@JoinColumn(name = "class12")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class12			 class12;
		@JoinColumn(name = "class13")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class13			 class13;
		@JoinColumn(name = "class14")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class14			 class14;
		@JoinColumn(name = "class15")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class15			 class15;
		@JoinColumn(name = "class16")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class16			 class16;
		@JoinColumn(name = "class17")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class17			 class17;
		@JoinColumn(name = "class18")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class18			 class18;
		@JoinColumn(name = "class19")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class19			 class19;
		@OneToMany(
			mappedBy	 = "class20",
			targetEntity = Class21.class
		)
		private Set<Class21>		class21;
		@OneToMany(
			mappedBy	 = "class20",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class20",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class20",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class20",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class20",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class20",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class20",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class20",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class20",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class20",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class20() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass21(Class21 class21) {
			this.class21.add(class21);
			class21.setClass20(this);
		}

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass20(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass20(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass20(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass20(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass20(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass20(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass20(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass20(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass20(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass20(this);
		}

		public void removeClass21(Class21 class21) {
			this.class21.remove(class21);
			class21.setClass20(null);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass20(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass20(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass20(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass20(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass20(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass20(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass20(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass20(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass20(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass20(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Class11 getClass11() {
			return this.class11;
		}

		public Class12 getClass12() {
			return this.class12;
		}

		public Class13 getClass13() {
			return this.class13;
		}

		public Class14 getClass14() {
			return this.class14;
		}

		public Class15 getClass15() {
			return this.class15;
		}

		public Class16 getClass16() {
			return this.class16;
		}

		public Class17 getClass17() {
			return this.class17;
		}

		public Class18 getClass18() {
			return this.class18;
		}

		public Class19 getClass19() {
			return this.class19;
		}

		public Set<Class21> getClass21() {
			return this.class21;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass11(Class11 class11) {
			this.class11 = class11;
		}

		public void setClass12(Class12 class12) {
			this.class12 = class12;
		}

		public void setClass13(Class13 class13) {
			this.class13 = class13;
		}

		public void setClass14(Class14 class14) {
			this.class14 = class14;
		}

		public void setClass15(Class15 class15) {
			this.class15 = class15;
		}

		public void setClass16(Class16 class16) {
			this.class16 = class16;
		}

		public void setClass17(Class17 class17) {
			this.class17 = class17;
		}

		public void setClass18(Class18 class18) {
			this.class18 = class18;
		}

		public void setClass19(Class19 class19) {
			this.class19 = class19;
		}

		public void setClass21(Set<Class21> class21) {

			if (this.class21 != null) {

				for (Class21 lclass21 : this.class21) {
					lclass21.setClass20(null);
				}
			}

			this.class21 = class21;

			for (Class21 lclass21 : class21) {
				lclass21.setClass20(this);
			}
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass20(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass20(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass20(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass20(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass20(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass20(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass20(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass20(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass20(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass20(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass20(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass20(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass20(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass20(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass20(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass20(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass20(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass20(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass20(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass20(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class21")
	public static class Class21 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@JoinColumn(name = "class11")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class11			 class11;
		@JoinColumn(name = "class12")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class12			 class12;
		@JoinColumn(name = "class13")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class13			 class13;
		@JoinColumn(name = "class14")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class14			 class14;
		@JoinColumn(name = "class15")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class15			 class15;
		@JoinColumn(name = "class16")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class16			 class16;
		@JoinColumn(name = "class17")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class17			 class17;
		@JoinColumn(name = "class18")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class18			 class18;
		@JoinColumn(name = "class19")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class19			 class19;
		@JoinColumn(name = "class20")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class20			 class20;
		@OneToMany(
			mappedBy	 = "class21",
			targetEntity = Class22.class
		)
		private Set<Class22>		class22;
		@OneToMany(
			mappedBy	 = "class21",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class21",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class21",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class21",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class21",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class21",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class21",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class21",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class21",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class21() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass22(Class22 class22) {
			this.class22.add(class22);
			class22.setClass21(this);
		}

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass21(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass21(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass21(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass21(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass21(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass21(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass21(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass21(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass21(this);
		}

		public void removeClass22(Class22 class22) {
			this.class22.remove(class22);
			class22.setClass21(null);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass21(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass21(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass21(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass21(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass21(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass21(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass21(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass21(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass21(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Class11 getClass11() {
			return this.class11;
		}

		public Class12 getClass12() {
			return this.class12;
		}

		public Class13 getClass13() {
			return this.class13;
		}

		public Class14 getClass14() {
			return this.class14;
		}

		public Class15 getClass15() {
			return this.class15;
		}

		public Class16 getClass16() {
			return this.class16;
		}

		public Class17 getClass17() {
			return this.class17;
		}

		public Class18 getClass18() {
			return this.class18;
		}

		public Class19 getClass19() {
			return this.class19;
		}

		public Class20 getClass20() {
			return this.class20;
		}

		public Set<Class22> getClass22() {
			return this.class22;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass11(Class11 class11) {
			this.class11 = class11;
		}

		public void setClass12(Class12 class12) {
			this.class12 = class12;
		}

		public void setClass13(Class13 class13) {
			this.class13 = class13;
		}

		public void setClass14(Class14 class14) {
			this.class14 = class14;
		}

		public void setClass15(Class15 class15) {
			this.class15 = class15;
		}

		public void setClass16(Class16 class16) {
			this.class16 = class16;
		}

		public void setClass17(Class17 class17) {
			this.class17 = class17;
		}

		public void setClass18(Class18 class18) {
			this.class18 = class18;
		}

		public void setClass19(Class19 class19) {
			this.class19 = class19;
		}

		public void setClass20(Class20 class20) {
			this.class20 = class20;
		}

		public void setClass22(Set<Class22> class22) {

			if (this.class22 != null) {

				for (Class22 lclass22 : this.class22) {
					lclass22.setClass21(null);
				}
			}

			this.class22 = class22;

			for (Class22 lclass22 : class22) {
				lclass22.setClass21(this);
			}
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass21(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass21(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass21(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass21(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass21(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass21(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass21(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass21(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass21(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass21(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass21(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass21(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass21(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass21(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass21(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass21(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass21(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass21(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class22")
	public static class Class22 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@JoinColumn(name = "class11")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class11			 class11;
		@JoinColumn(name = "class12")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class12			 class12;
		@JoinColumn(name = "class13")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class13			 class13;
		@JoinColumn(name = "class14")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class14			 class14;
		@JoinColumn(name = "class15")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class15			 class15;
		@JoinColumn(name = "class16")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class16			 class16;
		@JoinColumn(name = "class17")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class17			 class17;
		@JoinColumn(name = "class18")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class18			 class18;
		@JoinColumn(name = "class19")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class19			 class19;
		@JoinColumn(name = "class20")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class20			 class20;
		@JoinColumn(name = "class21")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class21			 class21;
		@OneToMany(
			mappedBy	 = "class22",
			targetEntity = Class23.class
		)
		private Set<Class23>		class23;
		@OneToMany(
			mappedBy	 = "class22",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class22",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class22",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class22",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class22",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class22",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class22",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class22",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class22() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass23(Class23 class23) {
			this.class23.add(class23);
			class23.setClass22(this);
		}

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass22(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass22(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass22(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass22(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass22(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass22(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass22(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass22(this);
		}

		public void removeClass23(Class23 class23) {
			this.class23.remove(class23);
			class23.setClass22(null);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass22(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass22(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass22(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass22(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass22(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass22(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass22(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass22(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Class11 getClass11() {
			return this.class11;
		}

		public Class12 getClass12() {
			return this.class12;
		}

		public Class13 getClass13() {
			return this.class13;
		}

		public Class14 getClass14() {
			return this.class14;
		}

		public Class15 getClass15() {
			return this.class15;
		}

		public Class16 getClass16() {
			return this.class16;
		}

		public Class17 getClass17() {
			return this.class17;
		}

		public Class18 getClass18() {
			return this.class18;
		}

		public Class19 getClass19() {
			return this.class19;
		}

		public Class20 getClass20() {
			return this.class20;
		}

		public Class21 getClass21() {
			return this.class21;
		}

		public Set<Class23> getClass23() {
			return this.class23;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass11(Class11 class11) {
			this.class11 = class11;
		}

		public void setClass12(Class12 class12) {
			this.class12 = class12;
		}

		public void setClass13(Class13 class13) {
			this.class13 = class13;
		}

		public void setClass14(Class14 class14) {
			this.class14 = class14;
		}

		public void setClass15(Class15 class15) {
			this.class15 = class15;
		}

		public void setClass16(Class16 class16) {
			this.class16 = class16;
		}

		public void setClass17(Class17 class17) {
			this.class17 = class17;
		}

		public void setClass18(Class18 class18) {
			this.class18 = class18;
		}

		public void setClass19(Class19 class19) {
			this.class19 = class19;
		}

		public void setClass20(Class20 class20) {
			this.class20 = class20;
		}

		public void setClass21(Class21 class21) {
			this.class21 = class21;
		}

		public void setClass23(Set<Class23> class23) {

			if (this.class23 != null) {

				for (Class23 lclass23 : this.class23) {
					lclass23.setClass22(null);
				}
			}

			this.class23 = class23;

			for (Class23 lclass23 : class23) {
				lclass23.setClass22(this);
			}
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass22(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass22(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass22(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass22(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass22(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass22(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass22(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass22(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass22(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass22(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass22(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass22(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass22(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass22(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass22(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass22(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class23")
	public static class Class23 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@JoinColumn(name = "class11")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class11			 class11;
		@JoinColumn(name = "class12")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class12			 class12;
		@JoinColumn(name = "class13")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class13			 class13;
		@JoinColumn(name = "class14")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class14			 class14;
		@JoinColumn(name = "class15")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class15			 class15;
		@JoinColumn(name = "class16")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class16			 class16;
		@JoinColumn(name = "class17")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class17			 class17;
		@JoinColumn(name = "class18")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class18			 class18;
		@JoinColumn(name = "class19")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class19			 class19;
		@JoinColumn(name = "class20")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class20			 class20;
		@JoinColumn(name = "class21")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class21			 class21;
		@JoinColumn(name = "class22")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class22			 class22;
		@OneToMany(
			mappedBy	 = "class23",
			targetEntity = Class24.class
		)
		private Set<Class24>		class24;
		@OneToMany(
			mappedBy	 = "class23",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class23",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class23",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class23",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class23",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class23",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class23",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class23() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass24(Class24 class24) {
			this.class24.add(class24);
			class24.setClass23(this);
		}

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass23(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass23(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass23(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass23(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass23(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass23(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass23(this);
		}

		public void removeClass24(Class24 class24) {
			this.class24.remove(class24);
			class24.setClass23(null);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass23(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass23(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass23(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass23(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass23(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass23(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass23(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Class11 getClass11() {
			return this.class11;
		}

		public Class12 getClass12() {
			return this.class12;
		}

		public Class13 getClass13() {
			return this.class13;
		}

		public Class14 getClass14() {
			return this.class14;
		}

		public Class15 getClass15() {
			return this.class15;
		}

		public Class16 getClass16() {
			return this.class16;
		}

		public Class17 getClass17() {
			return this.class17;
		}

		public Class18 getClass18() {
			return this.class18;
		}

		public Class19 getClass19() {
			return this.class19;
		}

		public Class20 getClass20() {
			return this.class20;
		}

		public Class21 getClass21() {
			return this.class21;
		}

		public Class22 getClass22() {
			return this.class22;
		}

		public Set<Class24> getClass24() {
			return this.class24;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass11(Class11 class11) {
			this.class11 = class11;
		}

		public void setClass12(Class12 class12) {
			this.class12 = class12;
		}

		public void setClass13(Class13 class13) {
			this.class13 = class13;
		}

		public void setClass14(Class14 class14) {
			this.class14 = class14;
		}

		public void setClass15(Class15 class15) {
			this.class15 = class15;
		}

		public void setClass16(Class16 class16) {
			this.class16 = class16;
		}

		public void setClass17(Class17 class17) {
			this.class17 = class17;
		}

		public void setClass18(Class18 class18) {
			this.class18 = class18;
		}

		public void setClass19(Class19 class19) {
			this.class19 = class19;
		}

		public void setClass20(Class20 class20) {
			this.class20 = class20;
		}

		public void setClass21(Class21 class21) {
			this.class21 = class21;
		}

		public void setClass22(Class22 class22) {
			this.class22 = class22;
		}

		public void setClass24(Set<Class24> class24) {

			if (this.class24 != null) {

				for (Class24 lclass24 : this.class24) {
					lclass24.setClass23(null);
				}
			}

			this.class24 = class24;

			for (Class24 lclass24 : class24) {
				lclass24.setClass23(this);
			}
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass23(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass23(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass23(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass23(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass23(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass23(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass23(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass23(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass23(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass23(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass23(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass23(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass23(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass23(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class24")
	public static class Class24 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@JoinColumn(name = "class11")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class11			 class11;
		@JoinColumn(name = "class12")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class12			 class12;
		@JoinColumn(name = "class13")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class13			 class13;
		@JoinColumn(name = "class14")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class14			 class14;
		@JoinColumn(name = "class15")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class15			 class15;
		@JoinColumn(name = "class16")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class16			 class16;
		@JoinColumn(name = "class17")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class17			 class17;
		@JoinColumn(name = "class18")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class18			 class18;
		@JoinColumn(name = "class19")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class19			 class19;
		@JoinColumn(name = "class20")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class20			 class20;
		@JoinColumn(name = "class21")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class21			 class21;
		@JoinColumn(name = "class22")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class22			 class22;
		@JoinColumn(name = "class23")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class23			 class23;
		@OneToMany(
			mappedBy	 = "class24",
			targetEntity = Class25.class
		)
		private Set<Class25>		class25;
		@OneToMany(
			mappedBy	 = "class24",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class24",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class24",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class24",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class24",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class24",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class24() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass25(Class25 class25) {
			this.class25.add(class25);
			class25.setClass24(this);
		}

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass24(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass24(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass24(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass24(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass24(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass24(this);
		}

		public void removeClass25(Class25 class25) {
			this.class25.remove(class25);
			class25.setClass24(null);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass24(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass24(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass24(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass24(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass24(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass24(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Class11 getClass11() {
			return this.class11;
		}

		public Class12 getClass12() {
			return this.class12;
		}

		public Class13 getClass13() {
			return this.class13;
		}

		public Class14 getClass14() {
			return this.class14;
		}

		public Class15 getClass15() {
			return this.class15;
		}

		public Class16 getClass16() {
			return this.class16;
		}

		public Class17 getClass17() {
			return this.class17;
		}

		public Class18 getClass18() {
			return this.class18;
		}

		public Class19 getClass19() {
			return this.class19;
		}

		public Class20 getClass20() {
			return this.class20;
		}

		public Class21 getClass21() {
			return this.class21;
		}

		public Class22 getClass22() {
			return this.class22;
		}

		public Class23 getClass23() {
			return this.class23;
		}

		public Set<Class25> getClass25() {
			return this.class25;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass11(Class11 class11) {
			this.class11 = class11;
		}

		public void setClass12(Class12 class12) {
			this.class12 = class12;
		}

		public void setClass13(Class13 class13) {
			this.class13 = class13;
		}

		public void setClass14(Class14 class14) {
			this.class14 = class14;
		}

		public void setClass15(Class15 class15) {
			this.class15 = class15;
		}

		public void setClass16(Class16 class16) {
			this.class16 = class16;
		}

		public void setClass17(Class17 class17) {
			this.class17 = class17;
		}

		public void setClass18(Class18 class18) {
			this.class18 = class18;
		}

		public void setClass19(Class19 class19) {
			this.class19 = class19;
		}

		public void setClass20(Class20 class20) {
			this.class20 = class20;
		}

		public void setClass21(Class21 class21) {
			this.class21 = class21;
		}

		public void setClass22(Class22 class22) {
			this.class22 = class22;
		}

		public void setClass23(Class23 class23) {
			this.class23 = class23;
		}

		public void setClass25(Set<Class25> class25) {

			if (this.class25 != null) {

				for (Class25 lclass25 : this.class25) {
					lclass25.setClass24(null);
				}
			}

			this.class25 = class25;

			for (Class25 lclass25 : class25) {
				lclass25.setClass24(this);
			}
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass24(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass24(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass24(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass24(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass24(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass24(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass24(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass24(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass24(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass24(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass24(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass24(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class25")
	public static class Class25 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@JoinColumn(name = "class11")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class11			 class11;
		@JoinColumn(name = "class12")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class12			 class12;
		@JoinColumn(name = "class13")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class13			 class13;
		@JoinColumn(name = "class14")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class14			 class14;
		@JoinColumn(name = "class15")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class15			 class15;
		@JoinColumn(name = "class16")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class16			 class16;
		@JoinColumn(name = "class17")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class17			 class17;
		@JoinColumn(name = "class18")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class18			 class18;
		@JoinColumn(name = "class19")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class19			 class19;
		@JoinColumn(name = "class20")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class20			 class20;
		@JoinColumn(name = "class21")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class21			 class21;
		@JoinColumn(name = "class22")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class22			 class22;
		@JoinColumn(name = "class23")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class23			 class23;
		@JoinColumn(name = "class24")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class24			 class24;
		@OneToMany(
			mappedBy	 = "class25",
			targetEntity = Class26.class
		)
		private Set<Class26>		class26;
		@OneToMany(
			mappedBy	 = "class25",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class25",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class25",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class25",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class25",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class25() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass26(Class26 class26) {
			this.class26.add(class26);
			class26.setClass25(this);
		}

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass25(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass25(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass25(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass25(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass25(this);
		}

		public void removeClass26(Class26 class26) {
			this.class26.remove(class26);
			class26.setClass25(null);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass25(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass25(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass25(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass25(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass25(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Class11 getClass11() {
			return this.class11;
		}

		public Class12 getClass12() {
			return this.class12;
		}

		public Class13 getClass13() {
			return this.class13;
		}

		public Class14 getClass14() {
			return this.class14;
		}

		public Class15 getClass15() {
			return this.class15;
		}

		public Class16 getClass16() {
			return this.class16;
		}

		public Class17 getClass17() {
			return this.class17;
		}

		public Class18 getClass18() {
			return this.class18;
		}

		public Class19 getClass19() {
			return this.class19;
		}

		public Class20 getClass20() {
			return this.class20;
		}

		public Class21 getClass21() {
			return this.class21;
		}

		public Class22 getClass22() {
			return this.class22;
		}

		public Class23 getClass23() {
			return this.class23;
		}

		public Class24 getClass24() {
			return this.class24;
		}

		public Set<Class26> getClass26() {
			return this.class26;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass11(Class11 class11) {
			this.class11 = class11;
		}

		public void setClass12(Class12 class12) {
			this.class12 = class12;
		}

		public void setClass13(Class13 class13) {
			this.class13 = class13;
		}

		public void setClass14(Class14 class14) {
			this.class14 = class14;
		}

		public void setClass15(Class15 class15) {
			this.class15 = class15;
		}

		public void setClass16(Class16 class16) {
			this.class16 = class16;
		}

		public void setClass17(Class17 class17) {
			this.class17 = class17;
		}

		public void setClass18(Class18 class18) {
			this.class18 = class18;
		}

		public void setClass19(Class19 class19) {
			this.class19 = class19;
		}

		public void setClass20(Class20 class20) {
			this.class20 = class20;
		}

		public void setClass21(Class21 class21) {
			this.class21 = class21;
		}

		public void setClass22(Class22 class22) {
			this.class22 = class22;
		}

		public void setClass23(Class23 class23) {
			this.class23 = class23;
		}

		public void setClass24(Class24 class24) {
			this.class24 = class24;
		}

		public void setClass26(Set<Class26> class26) {

			if (this.class26 != null) {

				for (Class26 lclass26 : this.class26) {
					lclass26.setClass25(null);
				}
			}

			this.class26 = class26;

			for (Class26 lclass26 : class26) {
				lclass26.setClass25(this);
			}
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass25(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass25(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass25(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass25(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass25(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass25(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass25(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass25(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass25(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass25(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class26")
	public static class Class26 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@JoinColumn(name = "class11")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class11			 class11;
		@JoinColumn(name = "class12")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class12			 class12;
		@JoinColumn(name = "class13")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class13			 class13;
		@JoinColumn(name = "class14")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class14			 class14;
		@JoinColumn(name = "class15")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class15			 class15;
		@JoinColumn(name = "class16")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class16			 class16;
		@JoinColumn(name = "class17")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class17			 class17;
		@JoinColumn(name = "class18")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class18			 class18;
		@JoinColumn(name = "class19")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class19			 class19;
		@JoinColumn(name = "class20")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class20			 class20;
		@JoinColumn(name = "class21")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class21			 class21;
		@JoinColumn(name = "class22")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class22			 class22;
		@JoinColumn(name = "class23")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class23			 class23;
		@JoinColumn(name = "class24")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class24			 class24;
		@JoinColumn(name = "class25")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class25			 class25;
		@OneToMany(
			mappedBy	 = "class26",
			targetEntity = Class27.class
		)
		private Set<Class27>		class27;
		@OneToMany(
			mappedBy	 = "class26",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class26",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class26",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class26",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class26() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass27(Class27 class27) {
			this.class27.add(class27);
			class27.setClass26(this);
		}

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass26(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass26(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass26(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass26(this);
		}

		public void removeClass27(Class27 class27) {
			this.class27.remove(class27);
			class27.setClass26(null);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass26(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass26(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass26(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass26(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Class11 getClass11() {
			return this.class11;
		}

		public Class12 getClass12() {
			return this.class12;
		}

		public Class13 getClass13() {
			return this.class13;
		}

		public Class14 getClass14() {
			return this.class14;
		}

		public Class15 getClass15() {
			return this.class15;
		}

		public Class16 getClass16() {
			return this.class16;
		}

		public Class17 getClass17() {
			return this.class17;
		}

		public Class18 getClass18() {
			return this.class18;
		}

		public Class19 getClass19() {
			return this.class19;
		}

		public Class20 getClass20() {
			return this.class20;
		}

		public Class21 getClass21() {
			return this.class21;
		}

		public Class22 getClass22() {
			return this.class22;
		}

		public Class23 getClass23() {
			return this.class23;
		}

		public Class24 getClass24() {
			return this.class24;
		}

		public Class25 getClass25() {
			return this.class25;
		}

		public Set<Class27> getClass27() {
			return this.class27;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass11(Class11 class11) {
			this.class11 = class11;
		}

		public void setClass12(Class12 class12) {
			this.class12 = class12;
		}

		public void setClass13(Class13 class13) {
			this.class13 = class13;
		}

		public void setClass14(Class14 class14) {
			this.class14 = class14;
		}

		public void setClass15(Class15 class15) {
			this.class15 = class15;
		}

		public void setClass16(Class16 class16) {
			this.class16 = class16;
		}

		public void setClass17(Class17 class17) {
			this.class17 = class17;
		}

		public void setClass18(Class18 class18) {
			this.class18 = class18;
		}

		public void setClass19(Class19 class19) {
			this.class19 = class19;
		}

		public void setClass20(Class20 class20) {
			this.class20 = class20;
		}

		public void setClass21(Class21 class21) {
			this.class21 = class21;
		}

		public void setClass22(Class22 class22) {
			this.class22 = class22;
		}

		public void setClass23(Class23 class23) {
			this.class23 = class23;
		}

		public void setClass24(Class24 class24) {
			this.class24 = class24;
		}

		public void setClass25(Class25 class25) {
			this.class25 = class25;
		}

		public void setClass27(Set<Class27> class27) {

			if (this.class27 != null) {

				for (Class27 lclass27 : this.class27) {
					lclass27.setClass26(null);
				}
			}

			this.class27 = class27;

			for (Class27 lclass27 : class27) {
				lclass27.setClass26(this);
			}
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass26(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass26(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass26(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass26(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass26(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass26(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass26(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass26(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class27")
	public static class Class27 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@JoinColumn(name = "class11")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class11			 class11;
		@JoinColumn(name = "class12")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class12			 class12;
		@JoinColumn(name = "class13")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class13			 class13;
		@JoinColumn(name = "class14")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class14			 class14;
		@JoinColumn(name = "class15")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class15			 class15;
		@JoinColumn(name = "class16")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class16			 class16;
		@JoinColumn(name = "class17")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class17			 class17;
		@JoinColumn(name = "class18")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class18			 class18;
		@JoinColumn(name = "class19")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class19			 class19;
		@JoinColumn(name = "class20")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class20			 class20;
		@JoinColumn(name = "class21")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class21			 class21;
		@JoinColumn(name = "class22")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class22			 class22;
		@JoinColumn(name = "class23")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class23			 class23;
		@JoinColumn(name = "class24")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class24			 class24;
		@JoinColumn(name = "class25")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class25			 class25;
		@JoinColumn(name = "class26")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class26			 class26;
		@OneToMany(
			mappedBy	 = "class27",
			targetEntity = Class28.class
		)
		private Set<Class28>		class28;
		@OneToMany(
			mappedBy	 = "class27",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class27",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class27",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class27() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass28(Class28 class28) {
			this.class28.add(class28);
			class28.setClass27(this);
		}

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass27(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass27(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass27(this);
		}

		public void removeClass28(Class28 class28) {
			this.class28.remove(class28);
			class28.setClass27(null);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass27(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass27(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass27(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Class11 getClass11() {
			return this.class11;
		}

		public Class12 getClass12() {
			return this.class12;
		}

		public Class13 getClass13() {
			return this.class13;
		}

		public Class14 getClass14() {
			return this.class14;
		}

		public Class15 getClass15() {
			return this.class15;
		}

		public Class16 getClass16() {
			return this.class16;
		}

		public Class17 getClass17() {
			return this.class17;
		}

		public Class18 getClass18() {
			return this.class18;
		}

		public Class19 getClass19() {
			return this.class19;
		}

		public Class20 getClass20() {
			return this.class20;
		}

		public Class21 getClass21() {
			return this.class21;
		}

		public Class22 getClass22() {
			return this.class22;
		}

		public Class23 getClass23() {
			return this.class23;
		}

		public Class24 getClass24() {
			return this.class24;
		}

		public Class25 getClass25() {
			return this.class25;
		}

		public Class26 getClass26() {
			return this.class26;
		}

		public Set<Class28> getClass28() {
			return this.class28;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass11(Class11 class11) {
			this.class11 = class11;
		}

		public void setClass12(Class12 class12) {
			this.class12 = class12;
		}

		public void setClass13(Class13 class13) {
			this.class13 = class13;
		}

		public void setClass14(Class14 class14) {
			this.class14 = class14;
		}

		public void setClass15(Class15 class15) {
			this.class15 = class15;
		}

		public void setClass16(Class16 class16) {
			this.class16 = class16;
		}

		public void setClass17(Class17 class17) {
			this.class17 = class17;
		}

		public void setClass18(Class18 class18) {
			this.class18 = class18;
		}

		public void setClass19(Class19 class19) {
			this.class19 = class19;
		}

		public void setClass20(Class20 class20) {
			this.class20 = class20;
		}

		public void setClass21(Class21 class21) {
			this.class21 = class21;
		}

		public void setClass22(Class22 class22) {
			this.class22 = class22;
		}

		public void setClass23(Class23 class23) {
			this.class23 = class23;
		}

		public void setClass24(Class24 class24) {
			this.class24 = class24;
		}

		public void setClass25(Class25 class25) {
			this.class25 = class25;
		}

		public void setClass26(Class26 class26) {
			this.class26 = class26;
		}

		public void setClass28(Set<Class28> class28) {

			if (this.class28 != null) {

				for (Class28 lclass28 : this.class28) {
					lclass28.setClass27(null);
				}
			}

			this.class28 = class28;

			for (Class28 lclass28 : class28) {
				lclass28.setClass27(this);
			}
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass27(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass27(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass27(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass27(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass27(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass27(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class28")
	public static class Class28 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@JoinColumn(name = "class11")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class11			 class11;
		@JoinColumn(name = "class12")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class12			 class12;
		@JoinColumn(name = "class13")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class13			 class13;
		@JoinColumn(name = "class14")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class14			 class14;
		@JoinColumn(name = "class15")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class15			 class15;
		@JoinColumn(name = "class16")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class16			 class16;
		@JoinColumn(name = "class17")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class17			 class17;
		@JoinColumn(name = "class18")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class18			 class18;
		@JoinColumn(name = "class19")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class19			 class19;
		@JoinColumn(name = "class20")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class20			 class20;
		@JoinColumn(name = "class21")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class21			 class21;
		@JoinColumn(name = "class22")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class22			 class22;
		@JoinColumn(name = "class23")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class23			 class23;
		@JoinColumn(name = "class24")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class24			 class24;
		@JoinColumn(name = "class25")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class25			 class25;
		@JoinColumn(name = "class26")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class26			 class26;
		@JoinColumn(name = "class27")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class27			 class27;
		@OneToMany(
			mappedBy	 = "class28",
			targetEntity = Class29.class
		)
		private Set<Class29>		class29;
		@OneToMany(
			mappedBy	 = "class28",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class28",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class28() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass29(Class29 class29) {
			this.class29.add(class29);
			class29.setClass28(this);
		}

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass28(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass28(this);
		}

		public void removeClass29(Class29 class29) {
			this.class29.remove(class29);
			class29.setClass28(null);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass28(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass28(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Class11 getClass11() {
			return this.class11;
		}

		public Class12 getClass12() {
			return this.class12;
		}

		public Class13 getClass13() {
			return this.class13;
		}

		public Class14 getClass14() {
			return this.class14;
		}

		public Class15 getClass15() {
			return this.class15;
		}

		public Class16 getClass16() {
			return this.class16;
		}

		public Class17 getClass17() {
			return this.class17;
		}

		public Class18 getClass18() {
			return this.class18;
		}

		public Class19 getClass19() {
			return this.class19;
		}

		public Class20 getClass20() {
			return this.class20;
		}

		public Class21 getClass21() {
			return this.class21;
		}

		public Class22 getClass22() {
			return this.class22;
		}

		public Class23 getClass23() {
			return this.class23;
		}

		public Class24 getClass24() {
			return this.class24;
		}

		public Class25 getClass25() {
			return this.class25;
		}

		public Class26 getClass26() {
			return this.class26;
		}

		public Class27 getClass27() {
			return this.class27;
		}

		public Set<Class29> getClass29() {
			return this.class29;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass11(Class11 class11) {
			this.class11 = class11;
		}

		public void setClass12(Class12 class12) {
			this.class12 = class12;
		}

		public void setClass13(Class13 class13) {
			this.class13 = class13;
		}

		public void setClass14(Class14 class14) {
			this.class14 = class14;
		}

		public void setClass15(Class15 class15) {
			this.class15 = class15;
		}

		public void setClass16(Class16 class16) {
			this.class16 = class16;
		}

		public void setClass17(Class17 class17) {
			this.class17 = class17;
		}

		public void setClass18(Class18 class18) {
			this.class18 = class18;
		}

		public void setClass19(Class19 class19) {
			this.class19 = class19;
		}

		public void setClass20(Class20 class20) {
			this.class20 = class20;
		}

		public void setClass21(Class21 class21) {
			this.class21 = class21;
		}

		public void setClass22(Class22 class22) {
			this.class22 = class22;
		}

		public void setClass23(Class23 class23) {
			this.class23 = class23;
		}

		public void setClass24(Class24 class24) {
			this.class24 = class24;
		}

		public void setClass25(Class25 class25) {
			this.class25 = class25;
		}

		public void setClass26(Class26 class26) {
			this.class26 = class26;
		}

		public void setClass27(Class27 class27) {
			this.class27 = class27;
		}

		public void setClass29(Set<Class29> class29) {

			if (this.class29 != null) {

				for (Class29 lclass29 : this.class29) {
					lclass29.setClass28(null);
				}
			}

			this.class29 = class29;

			for (Class29 lclass29 : class29) {
				lclass29.setClass28(this);
			}
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass28(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass28(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass28(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass28(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class29")
	public static class Class29 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@JoinColumn(name = "class11")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class11			 class11;
		@JoinColumn(name = "class12")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class12			 class12;
		@JoinColumn(name = "class13")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class13			 class13;
		@JoinColumn(name = "class14")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class14			 class14;
		@JoinColumn(name = "class15")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class15			 class15;
		@JoinColumn(name = "class16")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class16			 class16;
		@JoinColumn(name = "class17")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class17			 class17;
		@JoinColumn(name = "class18")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class18			 class18;
		@JoinColumn(name = "class19")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class19			 class19;
		@JoinColumn(name = "class20")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class20			 class20;
		@JoinColumn(name = "class21")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class21			 class21;
		@JoinColumn(name = "class22")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class22			 class22;
		@JoinColumn(name = "class23")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class23			 class23;
		@JoinColumn(name = "class24")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class24			 class24;
		@JoinColumn(name = "class25")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class25			 class25;
		@JoinColumn(name = "class26")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class26			 class26;
		@JoinColumn(name = "class27")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class27			 class27;
		@JoinColumn(name = "class28")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class28			 class28;
		@OneToMany(
			mappedBy	 = "class29",
			targetEntity = Class30.class
		)
		private Set<Class30>		class30;
		@OneToMany(
			mappedBy	 = "class29",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class29() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass30(Class30 class30) {
			this.class30.add(class30);
			class30.setClass29(this);
		}

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass29(this);
		}

		public void removeClass30(Class30 class30) {
			this.class30.remove(class30);
			class30.setClass29(null);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass29(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Class11 getClass11() {
			return this.class11;
		}

		public Class12 getClass12() {
			return this.class12;
		}

		public Class13 getClass13() {
			return this.class13;
		}

		public Class14 getClass14() {
			return this.class14;
		}

		public Class15 getClass15() {
			return this.class15;
		}

		public Class16 getClass16() {
			return this.class16;
		}

		public Class17 getClass17() {
			return this.class17;
		}

		public Class18 getClass18() {
			return this.class18;
		}

		public Class19 getClass19() {
			return this.class19;
		}

		public Class20 getClass20() {
			return this.class20;
		}

		public Class21 getClass21() {
			return this.class21;
		}

		public Class22 getClass22() {
			return this.class22;
		}

		public Class23 getClass23() {
			return this.class23;
		}

		public Class24 getClass24() {
			return this.class24;
		}

		public Class25 getClass25() {
			return this.class25;
		}

		public Class26 getClass26() {
			return this.class26;
		}

		public Class27 getClass27() {
			return this.class27;
		}

		public Class28 getClass28() {
			return this.class28;
		}

		public Set<Class30> getClass30() {
			return this.class30;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass11(Class11 class11) {
			this.class11 = class11;
		}

		public void setClass12(Class12 class12) {
			this.class12 = class12;
		}

		public void setClass13(Class13 class13) {
			this.class13 = class13;
		}

		public void setClass14(Class14 class14) {
			this.class14 = class14;
		}

		public void setClass15(Class15 class15) {
			this.class15 = class15;
		}

		public void setClass16(Class16 class16) {
			this.class16 = class16;
		}

		public void setClass17(Class17 class17) {
			this.class17 = class17;
		}

		public void setClass18(Class18 class18) {
			this.class18 = class18;
		}

		public void setClass19(Class19 class19) {
			this.class19 = class19;
		}

		public void setClass20(Class20 class20) {
			this.class20 = class20;
		}

		public void setClass21(Class21 class21) {
			this.class21 = class21;
		}

		public void setClass22(Class22 class22) {
			this.class22 = class22;
		}

		public void setClass23(Class23 class23) {
			this.class23 = class23;
		}

		public void setClass24(Class24 class24) {
			this.class24 = class24;
		}

		public void setClass25(Class25 class25) {
			this.class25 = class25;
		}

		public void setClass26(Class26 class26) {
			this.class26 = class26;
		}

		public void setClass27(Class27 class27) {
			this.class27 = class27;
		}

		public void setClass28(Class28 class28) {
			this.class28 = class28;
		}

		public void setClass30(Set<Class30> class30) {

			if (this.class30 != null) {

				for (Class30 lclass30 : this.class30) {
					lclass30.setClass29(null);
				}
			}

			this.class30 = class30;

			for (Class30 lclass30 : class30) {
				lclass30.setClass29(this);
			}
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass29(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass29(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class30")
	public static class Class30 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@JoinColumn(name = "class11")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class11			 class11;
		@JoinColumn(name = "class12")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class12			 class12;
		@JoinColumn(name = "class13")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class13			 class13;
		@JoinColumn(name = "class14")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class14			 class14;
		@JoinColumn(name = "class15")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class15			 class15;
		@JoinColumn(name = "class16")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class16			 class16;
		@JoinColumn(name = "class17")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class17			 class17;
		@JoinColumn(name = "class18")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class18			 class18;
		@JoinColumn(name = "class19")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class19			 class19;
		@JoinColumn(name = "class20")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class20			 class20;
		@JoinColumn(name = "class21")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class21			 class21;
		@JoinColumn(name = "class22")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class22			 class22;
		@JoinColumn(name = "class23")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class23			 class23;
		@JoinColumn(name = "class24")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class24			 class24;
		@JoinColumn(name = "class25")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class25			 class25;
		@JoinColumn(name = "class26")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class26			 class26;
		@JoinColumn(name = "class27")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class27			 class27;
		@JoinColumn(name = "class28")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class28			 class28;
		@JoinColumn(name = "class29")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class29			 class29;
		@OneToMany(
			mappedBy	 = "class30",
			targetEntity = Class31.class
		)
		private Set<Class31>		class31;

		//~ Constructors -------------------------------------------------------

		Class30() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public void addClass31(Class31 class31) {
			this.class31.add(class31);
			class31.setClass30(this);
		}

		public void removeClass31(Class31 class31) {
			this.class31.remove(class31);
			class31.setClass30(null);
		}

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Class11 getClass11() {
			return this.class11;
		}

		public Class12 getClass12() {
			return this.class12;
		}

		public Class13 getClass13() {
			return this.class13;
		}

		public Class14 getClass14() {
			return this.class14;
		}

		public Class15 getClass15() {
			return this.class15;
		}

		public Class16 getClass16() {
			return this.class16;
		}

		public Class17 getClass17() {
			return this.class17;
		}

		public Class18 getClass18() {
			return this.class18;
		}

		public Class19 getClass19() {
			return this.class19;
		}

		public Class20 getClass20() {
			return this.class20;
		}

		public Class21 getClass21() {
			return this.class21;
		}

		public Class22 getClass22() {
			return this.class22;
		}

		public Class23 getClass23() {
			return this.class23;
		}

		public Class24 getClass24() {
			return this.class24;
		}

		public Class25 getClass25() {
			return this.class25;
		}

		public Class26 getClass26() {
			return this.class26;
		}

		public Class27 getClass27() {
			return this.class27;
		}

		public Class28 getClass28() {
			return this.class28;
		}

		public Class29 getClass29() {
			return this.class29;
		}

		public Set<Class31> getClass31() {
			return this.class31;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass11(Class11 class11) {
			this.class11 = class11;
		}

		public void setClass12(Class12 class12) {
			this.class12 = class12;
		}

		public void setClass13(Class13 class13) {
			this.class13 = class13;
		}

		public void setClass14(Class14 class14) {
			this.class14 = class14;
		}

		public void setClass15(Class15 class15) {
			this.class15 = class15;
		}

		public void setClass16(Class16 class16) {
			this.class16 = class16;
		}

		public void setClass17(Class17 class17) {
			this.class17 = class17;
		}

		public void setClass18(Class18 class18) {
			this.class18 = class18;
		}

		public void setClass19(Class19 class19) {
			this.class19 = class19;
		}

		public void setClass20(Class20 class20) {
			this.class20 = class20;
		}

		public void setClass21(Class21 class21) {
			this.class21 = class21;
		}

		public void setClass22(Class22 class22) {
			this.class22 = class22;
		}

		public void setClass23(Class23 class23) {
			this.class23 = class23;
		}

		public void setClass24(Class24 class24) {
			this.class24 = class24;
		}

		public void setClass25(Class25 class25) {
			this.class25 = class25;
		}

		public void setClass26(Class26 class26) {
			this.class26 = class26;
		}

		public void setClass27(Class27 class27) {
			this.class27 = class27;
		}

		public void setClass28(Class28 class28) {
			this.class28 = class28;
		}

		public void setClass29(Class29 class29) {
			this.class29 = class29;
		}

		public void setClass31(Set<Class31> class31) {

			if (this.class31 != null) {

				for (Class31 lclass31 : this.class31) {
					lclass31.setClass30(null);
				}
			}

			this.class31 = class31;

			for (Class31 lclass31 : class31) {
				lclass31.setClass30(this);
			}
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	@Entity(name = "class31")
	public static class Class31 {

		//~ Instance fields ----------------------------------------------------

		@Id @NotNull private String id;
		@JoinColumn(name = "class00")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class00			 class00;
		@JoinColumn(name = "class01")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class01			 class01;
		@JoinColumn(name = "class02")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class02			 class02;
		@JoinColumn(name = "class03")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class03			 class03;
		@JoinColumn(name = "class04")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class04			 class04;
		@JoinColumn(name = "class05")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class05			 class05;
		@JoinColumn(name = "class06")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class06			 class06;
		@JoinColumn(name = "class07")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class07			 class07;
		@JoinColumn(name = "class08")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class08			 class08;
		@JoinColumn(name = "class09")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class09			 class09;
		@JoinColumn(name = "class10")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class10			 class10;
		@JoinColumn(name = "class11")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class11			 class11;
		@JoinColumn(name = "class12")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class12			 class12;
		@JoinColumn(name = "class13")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class13			 class13;
		@JoinColumn(name = "class14")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class14			 class14;
		@JoinColumn(name = "class15")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class15			 class15;
		@JoinColumn(name = "class16")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class16			 class16;
		@JoinColumn(name = "class17")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class17			 class17;
		@JoinColumn(name = "class18")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class18			 class18;
		@JoinColumn(name = "class19")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class19			 class19;
		@JoinColumn(name = "class20")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class20			 class20;
		@JoinColumn(name = "class21")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class21			 class21;
		@JoinColumn(name = "class22")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class22			 class22;
		@JoinColumn(name = "class23")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class23			 class23;
		@JoinColumn(name = "class24")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class24			 class24;
		@JoinColumn(name = "class25")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class25			 class25;
		@JoinColumn(name = "class26")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class26			 class26;
		@JoinColumn(name = "class27")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class27			 class27;
		@JoinColumn(name = "class28")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class28			 class28;
		@JoinColumn(name = "class29")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class29			 class29;
		@JoinColumn(name = "class30")
		@ManyToOne(fetch = FetchType.LAZY)
		private Class30			 class30;

		//~ Constructors -------------------------------------------------------

		Class31() {
			this.id = UUID.randomUUID()
						  .toString();
		}

		//~ Methods ------------------------------------------------------------

		public Class00 getClass00() {
			return this.class00;
		}

		public Class01 getClass01() {
			return this.class01;
		}

		public Class02 getClass02() {
			return this.class02;
		}

		public Class03 getClass03() {
			return this.class03;
		}

		public Class04 getClass04() {
			return this.class04;
		}

		public Class05 getClass05() {
			return this.class05;
		}

		public Class06 getClass06() {
			return this.class06;
		}

		public Class07 getClass07() {
			return this.class07;
		}

		public Class08 getClass08() {
			return this.class08;
		}

		public Class09 getClass09() {
			return this.class09;
		}

		public Class10 getClass10() {
			return this.class10;
		}

		public Class11 getClass11() {
			return this.class11;
		}

		public Class12 getClass12() {
			return this.class12;
		}

		public Class13 getClass13() {
			return this.class13;
		}

		public Class14 getClass14() {
			return this.class14;
		}

		public Class15 getClass15() {
			return this.class15;
		}

		public Class16 getClass16() {
			return this.class16;
		}

		public Class17 getClass17() {
			return this.class17;
		}

		public Class18 getClass18() {
			return this.class18;
		}

		public Class19 getClass19() {
			return this.class19;
		}

		public Class20 getClass20() {
			return this.class20;
		}

		public Class21 getClass21() {
			return this.class21;
		}

		public Class22 getClass22() {
			return this.class22;
		}

		public Class23 getClass23() {
			return this.class23;
		}

		public Class24 getClass24() {
			return this.class24;
		}

		public Class25 getClass25() {
			return this.class25;
		}

		public Class26 getClass26() {
			return this.class26;
		}

		public Class27 getClass27() {
			return this.class27;
		}

		public Class28 getClass28() {
			return this.class28;
		}

		public Class29 getClass29() {
			return this.class29;
		}

		public Class30 getClass30() {
			return this.class30;
		}

		public String getId() {
			return id;
		}

		public void setClass00(Class00 class00) {
			this.class00 = class00;
		}

		public void setClass01(Class01 class01) {
			this.class01 = class01;
		}

		public void setClass02(Class02 class02) {
			this.class02 = class02;
		}

		public void setClass03(Class03 class03) {
			this.class03 = class03;
		}

		public void setClass04(Class04 class04) {
			this.class04 = class04;
		}

		public void setClass05(Class05 class05) {
			this.class05 = class05;
		}

		public void setClass06(Class06 class06) {
			this.class06 = class06;
		}

		public void setClass07(Class07 class07) {
			this.class07 = class07;
		}

		public void setClass08(Class08 class08) {
			this.class08 = class08;
		}

		public void setClass09(Class09 class09) {
			this.class09 = class09;
		}

		public void setClass10(Class10 class10) {
			this.class10 = class10;
		}

		public void setClass11(Class11 class11) {
			this.class11 = class11;
		}

		public void setClass12(Class12 class12) {
			this.class12 = class12;
		}

		public void setClass13(Class13 class13) {
			this.class13 = class13;
		}

		public void setClass14(Class14 class14) {
			this.class14 = class14;
		}

		public void setClass15(Class15 class15) {
			this.class15 = class15;
		}

		public void setClass16(Class16 class16) {
			this.class16 = class16;
		}

		public void setClass17(Class17 class17) {
			this.class17 = class17;
		}

		public void setClass18(Class18 class18) {
			this.class18 = class18;
		}

		public void setClass19(Class19 class19) {
			this.class19 = class19;
		}

		public void setClass20(Class20 class20) {
			this.class20 = class20;
		}

		public void setClass21(Class21 class21) {
			this.class21 = class21;
		}

		public void setClass22(Class22 class22) {
			this.class22 = class22;
		}

		public void setClass23(Class23 class23) {
			this.class23 = class23;
		}

		public void setClass24(Class24 class24) {
			this.class24 = class24;
		}

		public void setClass25(Class25 class25) {
			this.class25 = class25;
		}

		public void setClass26(Class26 class26) {
			this.class26 = class26;
		}

		public void setClass27(Class27 class27) {
			this.class27 = class27;
		}

		public void setClass28(Class28 class28) {
			this.class28 = class28;
		}

		public void setClass29(Class29 class29) {
			this.class29 = class29;
		}

		public void setClass30(Class30 class30) {
			this.class30 = class30;
		}

		public void setId(String id) {
			this.id = id;
		}
	}
}
