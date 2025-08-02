package org.hibernate.test.proxyevict;

import java.util.HashSet;
import java.util.Set;

public class Person {
	
	public Person() {
	}

	public Person(String name) {
		this.name = name;
		this.children = new HashSet();
	}

	private Long id;
	
	private Set children;
	
	private Person parent;
	
	private Person friend;

	private String name;

	public void addChild(Person person) {
		children.add(person);
	}

	public void setFriend(Person person) {
		friend = person;
	}

	public Set getChildren() {
		return children;
	}

	public Person getFriend() {
		return friend;
	}

	public String getName() {
		return name;
	}

}
