package test;

import java.io.Serializable;

public class Test implements Serializable {

	public static final byte TEST_VALUE = 42;

	private Integer id;

	private Byte byteValue;

	public Test() {
		super();
	}

	public Test(Byte byteValue) {
		super();
		this.byteValue = byteValue;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Byte getByteValue() {
		return byteValue;
	}

	public void setByteValue(Byte byteValue) {
		this.byteValue = byteValue;
	}

}
