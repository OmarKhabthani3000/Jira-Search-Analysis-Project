package com.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "m_lot")
public class Lot implements Serializable
{
	private static final long		serialVersionUID	= 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", length = 20, precision = 0)
	private Long				id;

	@Column(name = "code", unique = true)
	private String				code;

	@Column(name = "dataRecord")
	private Date				dataRecord;

	@OneToMany(fetch = FetchType.LAZY, targetEntity = LotReference.class, mappedBy = "pk.idLote", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE })
	private Set<LotReference>	lotReference	= new HashSet<LotReference>();

	@Column(name = "finalized", nullable = false)
	private Boolean				finalized;

	public Lot()
	{
	}

	public Lot(Long id)
	{
		this.id=id;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public Date getDataRecord()
	{
		return dataRecord;
	}

	public void setDataRecord(Date dataRecord)
	{
		this.dataRecord = dataRecord;
	}

	public Set<LotReference> getLotReference()
	{
		return lotReference;
	}

	public void setLotReference(Set<LotReference> lotReference)
	{
		this.lotReference = lotReference;
	}

	public Boolean getFinalized()
	{
		return finalized;
	}

	public void setFinalized(Boolean finalized)
	{
		this.finalized = finalized;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		Lot other = (Lot) obj;
		if (code == null)
		{
			if (other.code != null) return false;
		}
		else if (!code.equals(other.code)) return false;
		return true;
	}

	public int compareTo(Lot bean)
	{
		Lot beanAll = (Lot) bean;
		if (this.hashCode() < beanAll.hashCode())
		{
			return -1;
		}
		if (this.hashCode() > beanAll.hashCode())
		{
			return 1;
		}
		return 0;
	}
}
