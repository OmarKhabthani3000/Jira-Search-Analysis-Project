/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.boot.binding;

import org.hibernate.annotations.NaturalId;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author Steve Ebersole
 */
public class Order {
	private Integer id;
	private String referenceCode;
	private byte[] rv;


	public Integer getId() {
		return this.id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getReferenceCode() {
		return this.referenceCode;
	}


	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}


	public byte[] getRv() {
		return this.rv;
	}


	public void setRv(byte[] rv) {
		this.rv = rv;
	}
}
