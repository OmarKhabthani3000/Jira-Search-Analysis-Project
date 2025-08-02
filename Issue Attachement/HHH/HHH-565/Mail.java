/*
 * Alias.java
 *
 * Created on May 3, 2005, 1:01 PM
 */

package edu.ucsd.netDB;

/**
 *
 * @author Kevin
 */
public class Mail {
    
    private Integer id;
    private String userid;
    private String mailbox;
    private String alias;
    private String pager;
    private String url;
    private String apop;
    private String cram;
    private Integer expiration = 0;
    private User user;
    
//    private User user;

    public Mail() {
    }

//    public String toString() {
//        return getAlias() + ":" + getMailbox();
//    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getMailbox() {
        return mailbox;
    }

    public void setMailbox(String mailbox) {
        this.mailbox = mailbox;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPager() {
        return pager;
    }

    public void setPager(String pager) {
        this.pager = pager;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApop() {
        return apop;
    }

    public void setApop(String apop) {
        this.apop = apop;
    }

    public String getCram() {
        return cram;
    }

    public void setCram(String cram) {
        this.cram = cram;
    }

    public Integer getExpiration() {
        return expiration;
    }

    public void setExpiration(Integer expiration) {
        this.expiration = expiration;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
    

    
}
