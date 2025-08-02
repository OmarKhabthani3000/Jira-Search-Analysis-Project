package test;

import javax.ejb.Entity;
import javax.ejb.GeneratorType;
import javax.ejb.Id;
import javax.ejb.JoinColumn;
import javax.ejb.ManyToOne;

@Entity
public class Many {
	private int id;
	private One one;
	
	@Id(generate=javax.ejb.GeneratorType.IDENTITY)
	public int getId() {
		return id;
	}

	@ManyToOne
	@JoinColumn(name="oneId")
	public One getOne() {
		return one;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setOne(One one) {
		this.one = one;
	}
}
