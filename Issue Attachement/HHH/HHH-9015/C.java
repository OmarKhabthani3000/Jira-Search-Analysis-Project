package test;

import javax.persistence.Embeddable;

@Embeddable
public class C {

	private int c;

	public int getC() {
		return c;
	}

	public void setC(int c) {
		this.c = c;
	}
}
