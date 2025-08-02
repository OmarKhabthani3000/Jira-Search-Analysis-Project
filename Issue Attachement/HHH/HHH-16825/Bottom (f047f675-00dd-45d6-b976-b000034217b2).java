package org.hibernate.bugs.hash;

import java.io.Serializable;

public class Bottom implements Serializable {
	private Middle middle;
	private Integer type;
	private String note;
	
	public Middle getMiddle() { return middle; }
	public void setMiddle(Middle middle) { this.middle = middle; }

	public Integer getType() { return type; }
	public void setType(Integer type) { this.type = type; }
	
	public String getNote() { return note; }
	public void setNote(String note) { this.note = note; }
}
