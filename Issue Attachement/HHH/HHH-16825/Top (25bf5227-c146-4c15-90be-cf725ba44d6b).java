package org.hibernate.bugs.hash;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Top implements Serializable {
	private String id;
	private String name;
	private Set<Middle> middles;
	
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	
	public Set<Middle> getMiddles() { return middles; }
	public void setMiddles(Set<Middle> middles) { this.middles = middles; }
	public void addMiddle(Middle middle) {
		if (middles == null) middles = new HashSet<Middle>();
		middles.add(middle);
	}
}
