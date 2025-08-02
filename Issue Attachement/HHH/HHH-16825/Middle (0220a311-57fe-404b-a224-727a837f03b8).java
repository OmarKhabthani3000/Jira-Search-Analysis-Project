package org.hibernate.bugs.hash;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Middle implements Serializable {
	private String id;
	private Top top;
	private Set<Bottom> bottoms;
	
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }

	public Top getTop() { return top; }
	public void setTop(Top student) { this.top = student; }
	
	public Set<Bottom> getBottoms() { return bottoms; }
	public void setBottoms(Set<Bottom> bottoms) { this.bottoms = bottoms; }
	public void addBottom(Bottom bottom) {
		if (bottoms == null) bottoms = new HashSet<Bottom>();
		bottoms.add(bottom);
	}
}
