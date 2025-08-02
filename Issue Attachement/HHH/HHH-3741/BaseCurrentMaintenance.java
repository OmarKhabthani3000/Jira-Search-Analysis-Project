/*
 * Created on 29 Aug 2007
 *
 * Copyright (c) 2007 Valueworks Ltd
 */
package uk.co.valueworks.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

/**
 * Model object for properties currently undergoing maintenance, scheduled 
 * or responsive
 */
@NamedQueries ({
    @NamedQuery(
        name = "findCurrentMaintenanceByStatusCode",
        query = "select cm from BaseCurrentMaintenance cm " 
        		+ "where cm.maintenanceStatus.code = :code "
        		+ "and cm.property.clientAddress.addressId = :client "
        		+ "and cm.workstream.workstreamId = :workstreamId"
        ),
    @NamedQuery(
        name = "findCurrentMaintenanceByProperty",
        query = "select cm from BaseCurrentMaintenance cm " 
                + "where cm.property.propertyId = :property"
        ),
    @NamedQuery(
        name = "findCurrentMaintenanceByVisitDateRange",
        query = "select cm "
            +   "from BaseCurrentMaintenance cm "
            +   "where visitDate between :fromDate and :toDate"
        ),
    @NamedQuery(
        name = "findCurrentMaintenanceByStatusCodeBetweenDates",
        query = "select cm from BaseCurrentMaintenance cm " 
        		+ "where cm.maintenanceStatus.code = :code "
        		+ "and cm.property.clientAddress.addressId = :client "
      		+ "and cm.statusDeadlineDate between :fromDate and :toDate"
        ),
    @NamedQuery(
        name = "findCurrentMaintenanceByLastChangedDate",
        query = "select cm from BaseCurrentMaintenance cm " 
              + "where cm.lastChanged between :fromDate and :toDate"
      ),
    @NamedQuery(
                name = "findCurrentMaintenanceByIdandWorkstream",
                query = "select cm from BaseCurrentMaintenance cm " 
                        + "where cm.id = :Id " 
                        + "and cm.workstream.workstreamId = :workstreamId"
                )
})
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name = "maintenanceType",
    discriminatorType = DiscriminatorType.STRING,
    length = 1    
)
@DiscriminatorValue("U")
@Table(name = "CurrentMaintenance")
@Audited
@AuditTable (value = "CurrentMaintenance_AUD")
public abstract class BaseCurrentMaintenance 
    extends VWBaseObject 
    implements IMaintenance {

    /**
     * SUID 
     */
    private static final long serialVersionUID = -7977019783032981796L;

    private Long id;
    
    private Property property;
    
    // The resource performing the maintenance
    private Address appointmentResource;
    
    private MaintenanceStatus maintenanceStatus;
    
    private Date statusDeadlineDate;
    
    private Date appointmentDate;
    
    private Workstream workstream;

    private AppointmentSlot appointmentSlot;
        
    private String title;
    private String forename;
    private String surname;
    private String telephone;
    private String notes;

    private Date visitDate;
    private Address visitResource;

	private WorkstreamMaintenance workstreamMaintenance;
    
    
	private Period period;
    
    /**
     * Returns the property period.
     * @return the period
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "periodId")
    @ForeignKey(name = "currentmaintenance_period_fk")
    public Period getPeriod() {
        return period;
    }
    
    /**
     * Sets the property period.
     * @param period the period to set
     */
    public void setPeriod(Period period) {
        this.period = period;
    }
	
    /**
     * {@inheritDoc}
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the property workstream.
     * @return the workstream
     */
    @OneToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "WORKSTREAM_ID")
    @ForeignKey(name = "currentmaintenance_workstream_fk")
    public Workstream getWorkstream() {
        return workstream;
    }

    /**
     * Sets the property workstream.
     * @param workstream the workstream to set
     */
    public void setWorkstream(Workstream workstream) {
        this.workstream = workstream;
    }

    /**
     * {@inheritDoc}
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "propertyId")
    @ForeignKey(name = "currentmaintenance_property_fk")
    public Property getProperty() {
        return property;
    }

    /**
     * {@inheritDoc}
     */
    public void setProperty(Property property) {
        this.property = property;
    }

    /**
     * {@inheritDoc}
     */
    @ManyToOne(fetch = FetchType.EAGER)	
    @JoinColumn(name = "maintenanceStatusId")
    @AuditJoinTable(name="MaintenanceStatus_AUD", 
		inverseJoinColumns = @JoinColumn(name="maintenanceStatusId"))
    public MaintenanceStatus getMaintenanceStatus() {
        return maintenanceStatus;
    }

    /**
     * {@inheritDoc}
     */
    public void setMaintenanceStatus(MaintenanceStatus maintenanceStatus) {
        this.maintenanceStatus = maintenanceStatus;
    }

    
    
    /**
     * {@inheritDoc}
     */
    @Temporal (TemporalType.DATE)
    public Date getStatusDeadlineDate() {
        return statusDeadlineDate;
    }

    /**
     * {@inheritDoc}
     */
    public void setStatusDeadlineDate(Date statusDeadlineDate) {
        this.statusDeadlineDate = statusDeadlineDate;
    }

    /**
     * {@inheritDoc}
     */
    public Date getAppointmentDate() {
        return appointmentDate;
    }

    /**
     * {@inheritDoc}
     */
    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    /**
     * {@inheritDoc}
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "appointmentSlotId")
    @ForeignKey(name = "currentmaintenance_appointment_fk")
    public AppointmentSlot getAppointmentSlot() {
        return appointmentSlot;
    }

    /**
     * {@inheritDoc}
     */
    public void setAppointmentSlot(AppointmentSlot appointmentSlot) {
        this.appointmentSlot = appointmentSlot;
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return title;
    }

    /**
     * {@inheritDoc}
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * {@inheritDoc}
     */
    public String getForename() {
        return forename;
    }

    /**
     * {@inheritDoc}
     */
    public void setForename(String forename) {
        this.forename = forename;
    }

    /**
     * {@inheritDoc}
     */
    public String getSurname() {
        return surname;
    }

    /**
     * {@inheritDoc}
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * {@inheritDoc}
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * {@inheritDoc}
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * {@inheritDoc}
     */
    @Column(length = 1024)
    public String getNotes() {
        return notes;
    }

    /**
     * {@inheritDoc}
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }


    /**
     * Returns the property resourceAddress.
     * @return the resourceAddress
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "appointmentResourceAddressId")
    @ForeignKey(name = "currentmaintenance_appointmentresource_fk")
    public Address getAppointmentResource() {
        return appointmentResource;
    }

    /**
     * Sets the property resourceAddress.
     * @param appointmentResource the appointmentResource to set
     */
    public void setAppointmentResource(Address appointmentResource) {
        this.appointmentResource = appointmentResource;
    }

    /**
     * Returns the property visitDate.
     * @return the visitDate
     */
    @Temporal (value = TemporalType.TIMESTAMP)
    public Date getVisitDate() {
        return visitDate;
    }

    /**
     * Sets the property visitDate.
     * @param visitDate the visitDate to set
     */
    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    /**
     * Returns the property visitResource.
     * @return the visitResource
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "visitResourceAddressId")
    @ForeignKey(name = "currentmaintenance_visitresource_fk")
    public Address getVisitResource() {
        return visitResource;
    }

    /**
     * Sets the property visitResource.
     * @param visitResource the visitResource to set
     */
    public void setVisitResource(Address visitResource) {
        this.visitResource = visitResource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        if (null != appointmentDate) {
            result = prime * result + appointmentDate.hashCode();
        }
        if (null != appointmentSlot) {
            result = prime * result + appointmentSlot.hashCode();
        }
        if (null != maintenanceStatus) {
            result = prime * result + maintenanceStatus.hashCode();
        }
        if (null != property) {
            result = prime * result + property.hashCode();
        }
        if (null != statusDeadlineDate) {
            result = prime * result + statusDeadlineDate.hashCode();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BaseCurrentMaintenance other = (BaseCurrentMaintenance) obj;
        if (appointmentDate == null) {
            if (other.appointmentDate != null) {
                return false;
            }
        } else if (!appointmentDate.equals(other.appointmentDate)) {
            return false;
        }
        if (appointmentSlot == null) {
            if (other.appointmentSlot != null) {
                return false;
            }
        } else if (!appointmentSlot.equals(other.appointmentSlot)) {
            return false;
        }
        if (maintenanceStatus == null) {
            if (other.maintenanceStatus != null) {
                return false;
            }
        } else if (!maintenanceStatus.equals(other.maintenanceStatus)) {
            return false;
        }
        if (property == null) {
            if (other.property != null) {
                return false;
            }
        } else if (!property.equals(other.property)) {
            return false;
        }
        if (statusDeadlineDate == null) {
            if (other.statusDeadlineDate != null) {
                return false;
            }
        } else if (!statusDeadlineDate.equals(other.statusDeadlineDate)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
            ToStringStyle.MULTI_LINE_STYLE).toString();
    }

	/**
	 * Returns the property workstreamMaintenance.
	 * @return the workstreamMaintenance
	 */
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "workstreamMaintenanceId")
	@ForeignKey(name = "currentmaintenance_workstreammaintenance_fk")
	public WorkstreamMaintenance getWorkstreamMaintenance() {
	    return workstreamMaintenance;
	}

	/**
	 * Sets the property workstreamMaintenance.
	 * @param workstreamMaintenance the workstreamMaintenance to set
	 */
	public void setWorkstreamMaintenance(
	    WorkstreamMaintenance workstreamMaintenance) {
	    
	    this.workstreamMaintenance = workstreamMaintenance;
	}

    
}
