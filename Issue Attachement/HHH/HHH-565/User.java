 /*
  * User.java
  *
  * Created on May 3, 2005, 9:42 AM
  */

package edu.ucsd.netDB;

import java.util.*;


/**
 * @author Kevin
 */
public class User {
    
    private Integer id;
    private String userid;
    private String mailname = " ";
    private char status = 'I';
    private Integer datecreated = 0;
    private Integer updatetime = 0;
    private Integer expiration = 0;
    private String flags = " ";
    private String permission = " ";
    private String affiliation = " ";
    private String dialreg = " ";
    private String studentid = " ";
    private String staffid = " ";
    private String otherid = " ";
    private String sponsor = " ";
    
    private Set mail = new HashSet();
    //private Set mail = new org.hibernate.collection.PersistentSet();
    
    public User() {
        
    }
    
    
    //    public String toString() {
    //        return id + ":" + mailname;
    //
    //    }
    
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getUserid() {
        return userid;
    }
    
    public void setUserid(String userid) {
        this.userid = userid;
    }
    
    public String getMailname() {
        return mailname;
    }
    
    public void setMailname(String mailname) {
        this.mailname = mailname;
    }
    
    public char getStatus() {
        return status;
    }
    
    public void setStatus(char status) {
        this.status = status;
    }
    
    public Integer getDatecreated() {
        return datecreated;
    }
    
    public void setDatecreated(Integer datecreated) {
        this.datecreated = datecreated;
    }
    
    public Integer getExpiration() {
        return expiration;
    }
    
    public void setExpiration(Integer expiration) {
        this.expiration = expiration;
    }
    
    public String getFlags() {
        return flags;
    }
    
    public void setFlags(String flags) {
        this.flags = flags;
    }
    
    public String getPermission() {
        return permission;
    }
    
    public void setPermission(String permission) {
        this.permission = permission;
    }
    
    public String getAffiliation() {
        return affiliation;
    }
    
    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }
    
    public String getDialreg() {
        return dialreg;
    }
    
    public void setDialreg(String dialreg) {
        this.dialreg = dialreg;
    }
    
    public String getStudentid() {
        return studentid;
    }
    
    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }
    
    public String getStaffid() {
        return staffid;
    }
    
    public void setStaffid(String staffid) {
        this.staffid = staffid;
    }
    
    public String getOtherid() {
        return otherid;
    }
    
    public void setOtherid(String otherid) {
        this.otherid = otherid;
    }
    
    public String getSponsor() {
        //System.out.println("getsponsor: " + sponsor);
        return sponsor;
    }
    
    public void setSponsor(String sponsor) throws Exception {
        
        this.sponsor = sponsor;
    }
    
    public Integer getUpdatetime() {
        return updatetime;
    }
    
    public void setUpdatetime(Integer updatetime) {
        this.updatetime = updatetime;
    }
    
    public Set getMail() {
        return mail;
    }
    
    public void setMail(Set mail) {
        this.mail = mail;
    }
    
}
