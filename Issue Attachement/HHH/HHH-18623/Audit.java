package org.hibernate.test.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name="audit")
public class Audit implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "audit_sequence", sequenceName = "audit_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "audit_sequence", strategy=GenerationType.SEQUENCE)
	private Long id;
	
	@Deprecated
	private String username;

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	@Deprecated
	public String getUsername() {
		return username;
	}

	@Deprecated
	public void setUsername(String username) {
		this.username = username;
	}
}
