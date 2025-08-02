package test;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class A {

	private long id;
	private B b;
	private C c;
	
	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Embedded
	public B getB() {
		return b;
	}

	public void setB(B b) {
		this.b = b;
	}

	@Embedded
	public C getC() {
		return c;
	}

	public void setC(C c) {
		this.c = c;
	}
}
