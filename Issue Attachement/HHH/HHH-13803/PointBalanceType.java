package org.raju.yadav.entity;

import javax.persistence.*;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@Table(name = "pointtype")
public class PointBalanceType extends AbstractReferenceDataEntity<Long> {

    private static final long serialVersionUID = -3732362787488510501L;

    @Column(nullable = false, precision = 1)
    private BigDecimal decimalPlaceNum;

    @Column(name = "ispointexpiredsw", nullable = false)
    private boolean isPointExpiredSw;

    @Column(name = "istimeexpiredsw", nullable = false)
    private boolean isTimeExpiredSw;

    @Column(name = "maxbalanceamt", nullable = false, precision = 16, scale = 6)
    private BigDecimal maxBalanceAmount;

    @Column(name = "minbalanceamt", nullable = false, precision = 16, scale = 6)
    private BigDecimal minBalanceAmount;

    @Column(name = "startbalanceamt", nullable = false, precision = 3)
    private BigDecimal startBalanceAmount;

    @Column(name = "ordernum", precision = 12)
    private long orderNumber;


    @Version
    @Column(name = "versioncnt", precision = 12)
    private long versioncnt;


    @Override
    @Id
    @Column(name = "pointtypeid")
    @Access(AccessType.PROPERTY)
    public Long getId() {
        return id;
    }

    public BigDecimal getDecimalPlaceNum() {
        return decimalPlaceNum;
    }

    public void setDecimalPlaceNum(BigDecimal decimalPlaceNum) {
        this.decimalPlaceNum = decimalPlaceNum;
    }

    public boolean isPointExpiredSw() {
        return isPointExpiredSw;
    }

    public void setPointExpiredSw(boolean isPointExpiredSw) {
        this.isPointExpiredSw = isPointExpiredSw;
    }

    public boolean isTimeExpiredSw() {
        return isTimeExpiredSw;
    }

    public void setTimeExpiredSw(boolean isTimeExpiredSw) {
        this.isTimeExpiredSw = isTimeExpiredSw;
    }

    public BigDecimal getMaxBalanceAmount() {
        return maxBalanceAmount;
    }

    public void setMaxBalanceAmount(BigDecimal maxBalanceAmount) {
        this.maxBalanceAmount = maxBalanceAmount;
    }

    public BigDecimal getMinBalanceAmount() {
        return minBalanceAmount;
    }

    public void setMinBalanceAmount(BigDecimal minBalanceAmount) {
        this.minBalanceAmount = minBalanceAmount;
    }

    public long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(long orderNumber) {
        this.orderNumber = orderNumber;
    }



    public long getVersioncnt() {
        return versioncnt;
    }

    public void setVersioncnt(long versioncnt) {
        this.versioncnt = versioncnt;
    }

    public BigDecimal getStartBalanceAmount() {
        return startBalanceAmount;
    }

    public void setStartBalanceAmount(BigDecimal startBalanceAmount) {
        this.startBalanceAmount = startBalanceAmount;
    }
}
