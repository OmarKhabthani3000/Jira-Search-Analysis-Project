package gov.osc.enrollment.backend.domain;


import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class OrganizationApplicationsPK implements Serializable {

	private static final Long serialVersionUID = 1L;
	
	public OrganizationApplicationsPK() {
		super();
	}
	
	
	@ManyToOne
	@JoinColumn(name="APPLICATION_ID",nullable=false)
	private Applications application;

	@ManyToOne(targetEntity=gov.osc.enrollment.backend.domain.Organization.class, cascade=CascadeType.ALL)
	@JoinColumn(name="ORGANIZATION_ID", referencedColumnName="ORGANIZATION_ID", nullable=false, insertable=true, updatable=true)
	private Organization organization;


	public Applications getApplication() {
		return application;
	}

	public void setApplication(Applications application) {
		this.application = application;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if ( ! (o instanceof OrganizationApplicationsPK)) {
			return false;
		}
		OrganizationApplicationsPK other = (OrganizationApplicationsPK) o;
		return (this.getOrganization().getId() == other.getOrganization().getId())
			&& (this.getApplication().getId() == other.getApplication().getId());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.getOrganization().getId() ^ (this.getOrganization().getId() >>> 32)));
		hash = hash * prime + ((int) (this.getApplication().getId() ^ (this.getApplication().getId() >>> 32)));
		return hash;
	}

}
