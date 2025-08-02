package com.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "m_lot_reference")
public class LotReference implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	@EmbeddedId
	private LotReferencePk	pk			= new LotReferencePk();

	@Column(name = "amount")
	private Integer			amount;

	public LotReference()
	{
		this.setIdLot(new Lot());
		this.setIdReferencia(new Reference());
	}

	public LotReference(Integer amount, Reference idReference, Lot idLot)
	{
		this.amount = amount;
		this.setIdReferencia(idReference);
		this.setIdLot(idLot);
	}

	public LotReference(Long lot, Long referencia)
	{
		this.pk.setIdLot(new Lot(lot));
		this.pk.setIdReferencia(new Reference(referencia));
	}

	public Reference getIdReferencia()
	{
		return pk.getIdReferencia();
	}

	public void setIdReferencia(Reference idReference)
	{
		this.pk.setIdReferencia(idReference);
	}

	public Lot getIdLot()
	{
		return pk.getIdLot();
	}

	public void setIdLot(Lot idLot)
	{
		this.pk.setIdLot(idLot);
	}

	public Integer getAmount()
	{
		return amount;
	}

	public void setAmount(Integer amount)
	{
		this.amount = amount;
	}


	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + "@[ hash: " + hashCode() + ", " + this.getIdReferencia() + ", " + this.getIdLot() + "]";
	}

	@Override
	public int hashCode()
	{
		return pk.hashCode();
	}

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
		LotReference other = (LotReference) obj;
		if (other.hashCode() != this.hashCode())
		{
			return false;
		}
		return true;
	}

}
