package hhh8689;

import java.io.Serializable;

public class EntityClass1 implements Serializable {

	private static final long serialVersionUID = 1L;

	/** identifier field */
	private Long key;

	/** persistent field */
	private String field1;

	/** persistent field */
	private String field2;
	
	private Long hibernateVersion;

	/** default constructor */
	public EntityClass1() {
	}

	public Long getKey() {
		return this.key;
	}

	public void setKey(Long key) {
		this.key = key;
	}
	
	public Long getHibernateVersion() {
		return hibernateVersion;
	}

	public void setHibernateVersion(Long newHibernateVersion) {
		this.hibernateVersion = newHibernateVersion;
	}	

	public String getField2() {
		return this.field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	public String getField1() {
		return this.field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

}