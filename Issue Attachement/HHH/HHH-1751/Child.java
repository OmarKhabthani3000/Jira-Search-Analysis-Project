//$Id: Group.java 6058 2005-03-11 17:05:19Z oneovthafew $
package org.hibernate.test.hqlfetchscroll;

public class Child {
	
	private String name;
	
	Child() {}
	
	public Child(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	private void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
}
