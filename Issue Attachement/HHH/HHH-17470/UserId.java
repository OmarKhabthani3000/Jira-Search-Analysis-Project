package com.test.hibernate.model;

import java.io.Serializable;

import jakarta.persistence.Basic;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.FetchType;

@Embeddable
public class UserId implements Serializable {
	
	@Embedded
	private CarId car;
	@jakarta.persistence.Column(name = "identifier_", unique = false, nullable = false, insertable = true, updatable = false,
			length = 255)
	@Basic(fetch = FetchType.EAGER, optional = false)
	private String identifier = null;

}
