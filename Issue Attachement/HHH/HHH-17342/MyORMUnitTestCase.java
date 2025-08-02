/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.bugs;

import jakarta.persistence.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;


public class MyORMUnitTestCase extends BaseCoreFunctionalTestCase {
	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[] { Person.class, Doctor.class, Engineer.class };
	}

	@Override
	protected String[] getMappings() {
		return new String[] {};
	}

	@Override
	protected String getBaseForMappings() {
		return "org/hibernate/test/";
	}

	@Override
	protected void configure(Configuration configuration) {
		super.configure( configuration );

		configuration.setProperty( AvailableSettings.SHOW_SQL, Boolean.TRUE.toString() );
		configuration.setProperty( AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString() );
	}

	@Test
	public void hhh123Test() throws Exception {
		addData();
		processingDataFixed();
//		processingDataFailsBecauseOfProxy();
	}

	private void addData() {
		Session s = openSession();
		Transaction tx = s.beginTransaction();

		Person a = new Doctor("A");
		Person b = new Engineer("B");
		Person c = new Engineer("C");
		Person d = new Doctor("D");

		// Because person "a" contains a reference to "d" AND because "a" appears before "d" in the resultList
		// while "a" also contains a reference to "d", "d" is loaded as a proxy, while "a", "b" and "d" are non-proxies.
		a.setPartner(d);

		s.persist(a);
		s.persist(b);
		s.persist(c);
		s.persist(d);

		tx.commit();
		s.close();
	}

	private void processingDataFailsBecauseOfProxy() {
		Session s = openSession();
		Transaction tx = s.beginTransaction();

		// Note that this is just a simple fetch all query, in which some 'p' are proxies while others are not.
		s.createQuery("select p from Person p", Person.class).list().forEach(person -> {
			if (person instanceof Doctor) {
				System.out.println("Hi doc: " + person);
			} else if (person instanceof Engineer) {
				System.out.println("Hi engi: " + person);
			} else {
				System.out.println("ERROR: Unknown person type!");
				assertFalse(person instanceof HibernateProxy, person + " is a HibernateProxy!");
			}
		});

		tx.commit();
		s.close();
	}
	private void processingDataFixed() {
		Session s = openSession();
		Transaction tx = s.beginTransaction();


		s.createQuery("select p from Person p", Person.class).list().forEach(person -> {
			// The solution is to manually unproxy person "d"
			// even though it doesn't cause additional SQL queries.
			// So IMO this should be done by default, so that unnecessary proxy creation is avoided.
			Person unproxiedPerson = person instanceof HibernateProxy
					? (Person) ((HibernateProxy) person).getHibernateLazyInitializer().getImplementation()
					: person;

			if (unproxiedPerson instanceof Doctor) {
				System.out.println("Hi doc: " + person);
			} else if (unproxiedPerson instanceof Engineer) {
				System.out.println("Hi engi: " + person);
			} else {
				System.out.println("ERROR: Unknown person type!");
				assertFalse(person instanceof HibernateProxy, person + " is a HibernateProxy!");
			}
		});

		tx.commit();
		s.close();
	}

	public Person getUnproxiedPerson(Person person) {
		return person instanceof HibernateProxy
			? (Person) ((HibernateProxy) person).getHibernateLazyInitializer().getImplementation()
			: person;
	}
}

@Entity
abstract class Person {
	@Id @GeneratedValue
	private Long id;
	private String name;
	@ManyToOne(fetch = FetchType.LAZY)
	private Person partner;

	public Person() {}
	public Person(String name) { this.name = name; }

	public String getName() { return name; }
	public void setPartner(Person partner) { this.partner = partner; }

	@Override
	public String toString() {
		return "Person{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}

@Entity
class Doctor extends Person {
	public Doctor() {}
	public Doctor(String name) { super(name); }

	@Override
	public String toString() {
		return "Doctor{} " + super.toString();
	}
}

@Entity
class Engineer extends Person {
	public Engineer() {}
	public Engineer(String name) { super(name); }

	@Override
	public String toString() {
		return "Engineer{} " + super.toString();
	}
}