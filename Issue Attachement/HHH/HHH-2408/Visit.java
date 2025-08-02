package test;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Visit extends PersistentEntity {
	@ManyToOne
	@JoinColumn (name = "BRANCH_ID", nullable = false, updatable = false)
	private Branch branch;

	public Branch getBranch() {
		return branch;
	}
	public void setBranch(Branch branch) {
		this.branch = branch;
	}
}
