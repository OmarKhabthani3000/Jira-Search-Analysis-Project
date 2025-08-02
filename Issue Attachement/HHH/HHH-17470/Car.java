package com.test.hibernate.model;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

@Entity
public class Car implements Serializable {
	
	@EmbeddedId
	private CarId codeObject = new CarId();
	
	@OneToMany(targetEntity = User.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY,
			mappedBy = "car", orphanRemoval = true)

	private Set<User> user = new java.util.HashSet<>();


}
