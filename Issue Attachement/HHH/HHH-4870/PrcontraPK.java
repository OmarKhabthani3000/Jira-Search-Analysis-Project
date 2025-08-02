/*
 * PrcontraPK.java
 * 
 * Created on 27-sep-2007, 16:38:12
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kyrian.entity.muvale;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author marcial
 */
@Embeddable
public class PrcontraPK implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7441961985141369232L;
	@Column(name = "NUMERO", nullable = false)
    private BigInteger numero;
    @Column(name = "RENOV", nullable = false)
    private BigInteger renov;
    @Column(name = "TIPO", nullable = false)
    private BigInteger tipo;

    public PrcontraPK() {
    }

    public PrcontraPK(BigInteger numero, BigInteger renov, BigInteger tipo) {
        this.numero = numero;
        this.renov = renov;
        this.tipo = tipo;
    }

    public BigInteger getNumero() {
        return numero;
    }

    public void setNumero(BigInteger numero) {
        this.numero = numero;
    }

    public BigInteger getRenov() {
        return renov;
    }

    public void setRenov(BigInteger renov) {
        this.renov = renov;
    }

    public BigInteger getTipo() {
        return tipo;
    }

    public void setTipo(BigInteger tipo) {
        this.tipo = tipo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (numero != null ? numero.hashCode() : 0);
        hash += (renov != null ? renov.hashCode() : 0);
        hash += (tipo != null ? tipo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
if (!(object instanceof PrcontraPK)) {
            return false;
        }
        PrcontraPK other = (PrcontraPK) object;
        if (this.numero != other.numero && (this.numero == null || !this.numero.equals(other.numero))) {
            return false;
        }
        if (this.renov != other.renov && (this.renov == null || !this.renov.equals(other.renov))) {
            return false;
        }
        if (this.tipo != other.tipo && (this.tipo == null || !this.tipo.equals(other.tipo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.kyrian.entity.muvale.PrcontraPK[numero=" + numero + ", renov=" + renov + ", tipo=" + tipo + "]";
    }

}
