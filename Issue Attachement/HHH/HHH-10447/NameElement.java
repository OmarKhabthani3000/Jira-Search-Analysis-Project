/* Copyright (c) 1994 - 2016 by OneVision Software AG, Regensburg, Germany
 * All rights reserved, strictly confidential
 */
package test;

public class NameElement {

	private String name="";

	public NameElement() {
	}

	public NameElement(String name) {
		setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
