package test;

import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.CascadeType;
import javax.ejb.Entity;
import javax.ejb.GeneratorType;
import javax.ejb.Id;
import javax.ejb.JoinColumn;
import javax.ejb.OneToMany;

@Entity
public class One {
	private int id;
	private Collection<Many> many = new ArrayList<Many>();
	
	@Id(generate=javax.ejb.GeneratorType.IDENTITY)
	public int getId() {
		return id;
	}

	@OneToMany(cascade=CascadeType.CREATE)
	@JoinColumn(name="oneId")
	public Collection<Many> getMany() {
		return many;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setMany(Collection<Many> many) {
		this.many = many;
	}
}
