package org.hibernate.ejb.test;

public class ItemNameDescription {
	private String name;
	private String descr;
	
	public ItemNameDescription(String name, String descr) {
		super();
		this.name = name;
		this.descr = descr;
	}

	public String getName() {
		return name;
	}

	public String getDescr() {
		return descr;
	}
	
}

