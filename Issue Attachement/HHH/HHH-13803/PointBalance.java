package org.raju.yadav.entity;

import org.hibernate.Transaction;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import javax.persistence.*;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "pointbal")
public class PointBalance extends AbstractTrackableEntity<Long>  {

    private static final long serialVersionUID = 2495698617037810698L;

    @Audited
    @Column(name = "employeeid", nullable = false, precision = 12)
    private Long employeeId;

    @Column(name = "enddtm", nullable = false)
    @Convert(converter = LocalDateTimePersistenceConverter.class)
    private LocalDateTime endDateTime;


    @Audited
    @Column(name = "pointendbalamt", precision = 16, scale = 6)
    private BigDecimal pointEndBalanceAmount;

    @Column(name = "pointstartbalamt", precision = 16, scale = 6)
    private BigDecimal pointStartBalanceAmount;

    @Audited
    @Column(name = "startdtm")
    private LocalDateTime startDateTime;

    @Version
    @Column(name = "versioncnt", precision = 12)
    private long versioncnt;



    @ManyToOne (fetch=FetchType.LAZY)
    @JoinColumn(name = "transtatusid")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private TransactionStatus transactionStatus;

    @ManyToOne
    @JoinColumn(name = "pointtypeid", nullable = false)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private PointBalanceType pointBalanceType;

    @Override
    @Id
    @Column(name = "pointbalid")
    @Access(AccessType.PROPERTY)
    @GeneratedValue(generator = "pointBalanceSequence", strategy = GenerationType.SEQUENCE)
    public Long getId() {
        return id;
    }


    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }



    public BigDecimal getPointEndBalanceAmount() {
        return pointEndBalanceAmount;
    }

    public void setPointEndBalanceAmount(BigDecimal pointEndBalanceAmount) {
        this.pointEndBalanceAmount = pointEndBalanceAmount;
    }

    public BigDecimal getPointStartBalanceAmount() {
        return pointStartBalanceAmount;
    }

    public void setPointStartBalanceAmount(BigDecimal pointStartBalanceAmount) {
        this.pointStartBalanceAmount = pointStartBalanceAmount;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public long getVersioncnt() {
        return versioncnt;
    }

    public void setVersioncnt(long versioncnt) {
        this.versioncnt = versioncnt;
    }



    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public PointBalanceType getPointBalanceType() {
        return pointBalanceType;
    }

    public void setPointBalanceType(PointBalanceType pointBalanceType) {
        this.pointBalanceType = pointBalanceType;
    }


}
