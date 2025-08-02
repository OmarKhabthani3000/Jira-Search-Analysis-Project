package org.hibernate.bugs.duplicates;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "course")
public class Course {
	private UUID id;
	private String name;
	private Set<Enrollment> enrollments;
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	
	@Column(name = "name")
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "course", cascade = { CascadeType.ALL }, orphanRemoval = true)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	public Set<Enrollment> getEnrollments() { return enrollments; }
	public void setEnrollments(Set<Enrollment> enrollments) { this.enrollments = enrollments; }
	public void addEnrollment(Enrollment enrollment) {
		if (enrollments == null) enrollments = new HashSet<Enrollment>();
		enrollments.add(enrollment);
	}
}
