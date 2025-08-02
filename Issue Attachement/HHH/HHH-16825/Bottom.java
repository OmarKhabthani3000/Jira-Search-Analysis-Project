package org.hibernate.bugs.hash;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bottom")
public class Bottom {
	private Middle middle;
	private Integer type;
	private String note;
	
	@Id
	@ManyToOne(optional = false)
	@JoinColumn(name = "middle_id", nullable = false)
	public Middle getMiddle() { return middle; }
	public void setMiddle(Middle middle) { this.middle = middle; }

	@Id
	@Column(name = "type")
	public Integer getType() { return type; }
	public void setType(Integer type) { this.type = type; }
	
	@Column(name = "note", nullable = true, length = 2048)
	public String getNote() { return note; }
	public void setNote(String note) { this.note = note; }
}
