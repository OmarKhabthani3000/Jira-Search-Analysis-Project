package sk.c.domain;

import java.awt.List;
import java.util.ArrayList;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;

@Entity
public class A {
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	Long id;
	

	@NotNull
	String name;
	
    @OneToMany( cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
	@OrderBy("name")
	java.util.List<B> bs = new ArrayList<B>();
	

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public java.util.List<B> getBs() {
		return bs;
	}

//	public void setBs(java.util.List<B> bs) {
//		this.bs = bs;
//	}

	@Override
	public String toString() {
		return "A [id=" + id + ", name=" + name + ", bs=" + bs + "]";
	}
}
