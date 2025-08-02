package com.test.hibernate.model;

import java.io.Serializable;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
@Audited
@Entity
public class Bike implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String name;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumns({
			@JoinColumn(name = "user_identifier_", referencedColumnName = "identifier_", unique = false, nullable = true),
			@JoinColumn(name = "user_car_identifier_", referencedColumnName = "car_identifier_", unique = false,
					nullable = true) })
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)

	private User user;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumns({
			@JoinColumn(name = "car_identifier_", referencedColumnName = "identifier_", unique = false, nullable = true) })
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)

	private Car car;

}
