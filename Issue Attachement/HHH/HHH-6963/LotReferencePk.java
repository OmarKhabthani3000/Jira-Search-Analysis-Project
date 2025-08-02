package com.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class LotReferencePk implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	@ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "idLot_id")
	private Lot			idLot;

	@ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "idReferencia_id")
	private Reference		idReference;

	public Reference getIdReferencia()
	{
		return idReference;
	}

	public void setIdReferencia(Reference idReference)
	{
		this.idReference = idReference;
	}

	public Lot getIdLot()
	{
		return idLot;
	}

	public void setIdLot(Lot idLot)
	{
		this.idLot = idLot;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (int) (prime * result + ((idLot == null) ? 0 : idLot.getCode().hashCode()));
		result = (int) (prime * result + ((idReference == null) ? 0 : idReference.getCode().hashCode()));
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		LotReferencePk other = (LotReferencePk) obj;
		if (idLot == null)
		{
			if (other.idLot != null)
			{
				return false;
			}
		}
		else if (!idLot.equals(other.idLot))
		{
			return false;
		}
		if (idReference == null)
		{
			if (other.idReference != null)
			{
				return false;
			}
		}
		else if (!idReference.equals(other.idReference))
		{
			return false;
		}
		return true;
	}
}
