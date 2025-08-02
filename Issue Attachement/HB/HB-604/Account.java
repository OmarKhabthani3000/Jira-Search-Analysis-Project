package test;

/**
 * <p>
 * Denali - A CarShare Reservation System developed by EngineGreen
 * Copyright 2003, EngineGreen.  All Rights Reserved.
 * </p>
 *
 * @version $Id: Account.java,v 1.1 2004/01/05 01:30:58 matt Exp $
 * @author Matt Ho <a href="mailto:matt@enginegreen.com">&lt;matt@enginegreen.com&gt;</a>
 */
public class Account {
    private Long accountId;
    private String name;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
