package com.electrainfo.tradesafe.datamodel.entity ;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.NotNull;

import com.electrainfo.tradesafe.datamodel.util.MemorySortModel;


@Entity
@Table( name = "nonbusinessdate"
	, uniqueConstraints = @UniqueConstraint( columnNames = "nbdate" )
)

public class NonBusinessDate
implements java.io.Serializable
{

	private Date nbdate ;
	private Date nbdate2 ;
	
	public static final Comparator COMPARATOR = new Comparator<NonBusinessDate>()
	{
		public int compare( NonBusinessDate one, NonBusinessDate other)
		{
			return MemorySortModel.compareInclNull( one.getNbdate(), other.getNbdate() ) ;
		}

	} ;

	public NonBusinessDate()
	{
	}

	public NonBusinessDate( Date nbdate )
	{
		this.nbdate = nbdate ;
	}

	@Id
	@Temporal(TemporalType.DATE)
	@Column( name = "nbdate", nullable = false )
	@NotNull
	public Date getNbdate()
	{
		return this.nbdate ;
	}

	public void setNbdate( Date nbdate )
	{
		this.nbdate = nbdate ;
	}

	@Temporal(TemporalType.DATE)
	@Column( name = "nbdate2", nullable = false )
	@NotNull
	public Date getNbdate2()
	{
		return this.nbdate2 ;
	}

	public void setNbdate2( Date nbdate2 )
	{
		this.nbdate2 = nbdate2 ;
	}


}
