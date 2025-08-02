package entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Parent {

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
      
    @OneToOne(mappedBy = "parent", fetch=FetchType.LAZY, cascade=CascadeType.ALL, optional=true)
    private Child child;

    public Parent() {}

	public Child getChild() {
		return child;
	}

	public void setChild(Child child) {
		this.child = child;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
      
}