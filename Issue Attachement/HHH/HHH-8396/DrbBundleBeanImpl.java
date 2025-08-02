package com.synchronoss.readytogo.dao;

import java.io.Serializable;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import com.synchronoss.readytogo.dbresourcebundle.DrbBundleBean;

@Entity(name="DrbBundleBean")
@Table( name="SNCR_DRB_BUNDLE")
@IdClass(DrbBundleBeanImpl.PK.class)
public class DrbBundleBeanImpl implements DrbBundleBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
    private String bundle;
    @Id
    private String locale;
    @Id
    @Column(name="KEY_NAME")
    private String key;

    private String value;

	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * The primary key for the bundle bean
	 *
	 */
	public static class PK implements Serializable {
		private static final long serialVersionUID = 2697712362067232634L;
		private String bundle;
	    private String locale;
	    private String key;

	    public PK(){
	    }
	    
	    public PK(DrbBundleBean bean){
	        this.bundle = bean.getBundle();
	        this.locale = bean.getLocale();
	        this.key = bean.getKey();
	    }
	    
	    public String getBundle() {
	        return bundle;
	    }

	    public void setBundle(String bundle) {
	        this.bundle = bundle;
	    }

	    public String getLocale() {
	        return locale;
	    }

	    public void setLocale(String locale) {
	        this.locale = locale;
	    }

	    public String getKey() {
	        return key;
	    }

	    public void setKey(String key) {
	        this.key = key;
	    }
	    
	    public boolean equals(Object o)
	    {
	    	if (o instanceof PK)
	    	{
	    		PK pk2 = (PK)o;
	    		return new EqualsBuilder().append( getBundle(), pk2.getBundle() )
	    				.append( getLocale(), pk2.getLocale() )
	    				.append( getKey(), pk2.getKey() ).isEquals();	
	    	}
	    	return false;
	    }
	    
	    public int hashCode()
	    {
	    	return new HashCodeBuilder().append( getBundle() ).append( getLocale() ).append( getKey() ).toHashCode();
	    }
	    
	}
	
}
