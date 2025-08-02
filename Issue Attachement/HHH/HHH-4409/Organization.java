package gov.osc.enrollment.backend.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

@Entity
@Table(name="ORGANIZATION")
@SequenceGenerator(name="OrgGen", sequenceName="SEQ_ORG", initialValue=1, allocationSize=1)
public class Organization extends EnvelopeInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="OrgGen")
    @Column(name="ORGANIZATION_ID") 
    private Long id;
    
    @Column(name="NAME")    
    @Index(name="ORG_NAME_INDEX", columnNames={"NAME"})    
    private String name;
    
    @Column(name="WEBSITE")
    private String website;
    
    @Column(name="ACTIVE_DATE")
    private Date activeDate;
    
    @Column(name="INACTIVE_DATE")
    private Date inactiveDate;
    
    @OneToOne(optional= true ,fetch=FetchType.EAGER)
    @JoinColumn(name="USERID",nullable=true)
    private User primaryAuthorizer;
    
    @OneToOne(optional=false,cascade=CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name="ADDRESS_ID")
    private Address address;
    
    @OneToOne(optional=false,cascade=CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="CONTACT_ID")
    private ContactInfo contactInfo;

	@OneToMany(targetEntity=gov.osc.enrollment.backend.domain.CrossReferenceCodes.class, cascade=CascadeType.ALL, fetch = FetchType.EAGER,
	           mappedBy="organization")
	//@org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN) 
	private Set<CrossReferenceCodes> crossReferenceCodes = new HashSet<CrossReferenceCodes>();
	
	@OneToMany(targetEntity=gov.osc.enrollment.backend.domain.OrganizationApplications.class, cascade=CascadeType.ALL, fetch = FetchType.EAGER,
	           mappedBy="id.organization")
	private Set<OrganizationApplications> organizationApps = new HashSet<OrganizationApplications>();
	
    @OneToOne(optional=false,cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="IRS_VALIDATION_ID",nullable=true)
    private IrsValidation irsValidation = new IrsValidation();     

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ORGANIZATION_TYPE_ID", nullable=false)
    private OrganizationTypes organizationType = new OrganizationTypes();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="STATUS_ID", nullable=false)
    private Status status = new Status();
   
    
    public Organization() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Date getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(Date activeDate) {
        this.activeDate = activeDate;
    }

    public Date getInactiveDate() {
        return inactiveDate;
    }

    public void setInactiveDate(Date inactiveDate) {
        this.inactiveDate = inactiveDate;
    }

    public User getPrimaryAuthorizer() {
        return primaryAuthorizer;
    }

    public void setPrimaryAuthorizer(User primaryAuthorizer) {
        this.primaryAuthorizer = primaryAuthorizer;
    }

   
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

	public Set<CrossReferenceCodes> getCrossReferenceCodes() {
		return crossReferenceCodes;
	}

	public void setCrossReferenceCodes(Set<CrossReferenceCodes> crossReferenceCodes) {
		this.crossReferenceCodes = crossReferenceCodes;
	}

	public IrsValidation getIrsValidation() {
		return irsValidation;
	}

	public void setIrsValidation(IrsValidation irsValidation) {
		this.irsValidation = irsValidation;
	}

	public OrganizationTypes getOrganizationType() {
		return organizationType;
	}

	public void setOrganizationType(OrganizationTypes organizationType) {
		this.organizationType = organizationType;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

    public Set<OrganizationApplications> getOrganizationApps() {
		return organizationApps;
	}

	public void setOrganizationApps(Set<OrganizationApplications> organizationApps) {
		this.organizationApps = organizationApps;
	}

}
