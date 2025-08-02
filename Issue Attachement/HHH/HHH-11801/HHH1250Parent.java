package org.hibernate.test;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "hhh1250parents")
public class HHH1250Parent {
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private Long id;

	@Version
	private Long version;

	
	@OneToMany(mappedBy="parent")
	@MapKey(name="key")
	private Map<Integer, HHH1250Child> children = new HashMap<>();
	
	
	public Map<Integer, HHH1250Child> getChildren() {
		return children;
	}
	
}
