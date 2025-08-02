package test;

import java.util.List;

import javax.persistence.Embeddable;

@Embeddable
public class B {

	private List<byte[]> b;

	public List<byte[]> getB() {
		return b;
	}

	public void setB(List<byte[]> b) {
		this.b = b;
	}
}
