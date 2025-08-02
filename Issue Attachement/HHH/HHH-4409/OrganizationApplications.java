package gov.osc.enrollment.backend.domain;


import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="ORGANIZATION_APPLICATIONS")
	   
@SequenceGenerator(name="OrgAppsGen", sequenceName="SEQ_ORG_APPS", initialValue=1, allocationSize=1)

@NamedQueries
(
	{
		@NamedQuery(name="OrganizationApplications.findAppByOrganizationId",query="SELECT orgApps FROM OrganizationApplications orgApps WHERE orgApps.id.organization.id = :ORGANIZATION_ID")
		
	}
)

public class OrganizationApplications extends EnvelopeInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private OrganizationApplicationsPK id;

	public OrganizationApplicationsPK getId() {
		return id;
	}

	public void setId(OrganizationApplicationsPK id) {
		this.id = id;
	}

	
	

	


}
