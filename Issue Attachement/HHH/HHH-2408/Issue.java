package test;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQuery (name = "Issue.refreshDueDate", query = "UPDATE Issue i SET i.dueDate = i.dueDate + 10 WHERE i.visit.branch.id = :branchId")
public class Issue extends PersistentEntity {
	@Temporal (TemporalType.DATE)
	@Column (name = "DUE_DATE")
	private Date dueDate;
	@ManyToOne
	@JoinColumn (name = "VISIT_ID", nullable = false, updatable = false)
	private Visit visit;

	protected void setVisit(Visit visit) {
		this.visit = visit;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public Visit getVisit() {
		return visit;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
}
