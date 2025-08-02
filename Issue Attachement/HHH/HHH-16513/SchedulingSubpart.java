package org.hibernate.bugs;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "subpart")
public class SchedulingSubpart {
	private Long id;
	private SchedulingSubpart parentSubpart;
	
	@Id
	@Column(name = "id")
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	
	
	@ManyToOne(optional = true)
	@JoinColumn(name = "parent")
	public SchedulingSubpart getParentSubpart() { return parentSubpart; }
	public void setParentSubpart(SchedulingSubpart parentSubpart) { this.parentSubpart = parentSubpart; }
}
