package org.hibernate.bugs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
