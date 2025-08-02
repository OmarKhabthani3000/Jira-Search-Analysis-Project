package org.hibernate.bytecode.internal.bytebuddy;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TestSuperclass")
public class TestSuperclass {
	@Id
	private String id;
	
	@Basic
	private String inheritedProperty;
	
	public TestSuperclass() {
		
	}
	
	public TestSuperclass(String id, String inheritedProperty) {
		this.id = id;
		this.inheritedProperty = inheritedProperty;
	}
	
	public String getId() {
		return id;
	}

	public String getInheritedProperty() {
		return inheritedProperty;
	}
	
	public void setInheritedProperty(String inheritedProperty) {
		this.inheritedProperty = inheritedProperty;
	}
}