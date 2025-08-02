package entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
public class Child {
    @Id @GeneratedValue(generator="parentPKGenerator")
    @org.hibernate.annotations.GenericGenerator(
          name = "parentPKGenerator",
          strategy ="foreign",
          parameters = @org.hibernate.annotations.Parameter(name = "property", value = "parent")
    )
    private int id;

    @OneToOne(optional=false, fetch=FetchType.LAZY)
    @PrimaryKeyJoinColumn
    @org.hibernate.annotations.ForeignKey(name="fk_parent")
    private Parent parent;

    public Child() {}
    
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public Parent getParent() {
		return parent;
	}
	
	public void setParent(Parent parent) {
		this.parent = parent;
	}


}