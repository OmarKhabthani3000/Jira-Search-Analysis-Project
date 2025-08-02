package org.hibernate.bytecode.internal.bytebuddy;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TestSubclass")
public class TestSubclass extends TestSuperclass {
	@Basic
	private String declaredProperty;
	
	public TestSubclass() {
		
	}
	
	public TestSubclass(String id, String inheritedProperty, String declaredProperty) {
		super( id, inheritedProperty );
		this.declaredProperty = declaredProperty;
	}

	public String getDeclaredProperty() {
		return declaredProperty;
	}

	public void setDeclaredProperty(String declaredProperty) {
		this.declaredProperty = declaredProperty;
	}
}