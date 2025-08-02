package sk.c.domain;

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
public class B {
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	Long id;
	
	@NotNull
	String name;	
	
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
	@OrderBy("name")
	java.util.List<C> cs = new ArrayList<C>();

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
	

	public java.util.List<C> getCs() {
		return cs;
	}

	@Override
	public String toString() {
		return "B [id=" + id + ", name=" + name + ", cs="+cs+"]";
	}

}
