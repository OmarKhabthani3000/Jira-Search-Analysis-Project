package com.mainsys.account.statement.model;

import java.util.Date;
import java.io.Serializable;

public class Statement implements Serializable {

     private long id;
     private Date lastProductionDate;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getLastProductionDate() {
        return this.lastProductionDate;
    }

    public void setLastProductionDate(Date lastPoductionDate) {
        this.lastProductionDate = lastPoductionDate;
    }

}