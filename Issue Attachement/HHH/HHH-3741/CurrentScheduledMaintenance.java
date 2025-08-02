/*
 * Created on 10 Sep 2007
 *
 * Copyright (c) 2007 Valueworks Ltd
 */
package uk.co.valueworks.model;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

/**
 * Extends CurrentMaintenance to include the properties specific to only
 * Scheduled maintenance
 */
@NamedQueries ({
    @NamedQuery(
        name = "findCurrentScheduledMaintenanceByProperty",
        query = "select cm from CurrentScheduledMaintenance cm " 
                + "where cm.property.propertyId = :property"
        ),
    @NamedQuery(
        name = "findCurrentScheduledMaintenanceByPropertyandStatus",
        query = "select cm from CurrentScheduledMaintenance cm " 
               + "where cm.property.propertyId = :property "
               + "and cm.maintenanceStatus.code = :code "
        ),    
        @NamedQuery(
            name = "findCurrentScheduledMaintenanceByWorkstreamMaintenance",
            query = "select cm from CurrentScheduledMaintenance cm " 
                   + "where cm.workstreamMaintenance.workstreamMaintenanceId" 
                   + " = :workstreamMaintenanceId "
            )  
})
@Entity
@DiscriminatorValue("S")
@Audited
@AuditTable (value = "CurrentMaintenance_AUD")
public class CurrentScheduledMaintenance extends BaseCurrentMaintenance {

    /**
     * SUID
     */
    private static final long serialVersionUID = -8633567472170744933L;

    private Date complianceDeadline;

    /**
     * Returns the property's compliance deadline as it was when the property
     * was put into the cycle.
     * @return the complianceDeadline
     */
    public Date getComplianceDeadline() {
        return complianceDeadline;
    }

    /**
     * Sets the property complianceDeadline.
     * @param complianceDeadline the complianceDeadline to set
     */
    public void setComplianceDeadline(Date complianceDeadline) {
        this.complianceDeadline = complianceDeadline;
    }
    
//    private Period period;
//    
//    /**
//     * Returns the property period.
//     * @return the period
//     */
//    @OneToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "periodId")
//    @ForeignKey(name = "currentscheduled_period_fk")
//    public Period getPeriod() {
//        return period;
//    }
//    
//    /**
//     * Sets the property period.
//     * @param period the period to set
//     */
//    public void setPeriod(Period period) {
//        this.period = period;
//    }
//

    
}
