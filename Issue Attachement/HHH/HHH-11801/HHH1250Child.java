package org.hibernate.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "hhh1250children")
public class HHH1250Child {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private Long id;

	@Version
	private Long version;


	@Column(nullable = false)
	private int key;
	
	@ManyToOne
	private HHH1250Parent parent;

	public HHH1250Parent getParent() {
		return parent;
	}

	public void setParent(HHH1250Parent parent) {
		this.parent = parent;
	}
	
}
