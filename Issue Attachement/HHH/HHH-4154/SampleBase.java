package org.frecklepuppy.bb.model;

import static javax.persistence.GenerationType.AUTO;
import static javax.persistence.InheritanceType.JOINED;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Temporal;

@Entity
@Inheritance(strategy=JOINED)
public class SampleBase
{
	private Long id;
	private Date timestamp = new Date();
	
	
	@Id
	@GeneratedValue(strategy=AUTO)
	public Long getId()
    {
    	return id;
    }
	
	
	public void setId(Long id)
    {
    	this.id = id;
    }
	
	
	@Temporal(TIMESTAMP)
	@Basic(optional=false)
	public Date getTimestamp()
    {
    	return timestamp;
    }
	
	
	public void setTimestamp(Date timestamp)
    {
    	this.timestamp = timestamp;
    }
	


}
