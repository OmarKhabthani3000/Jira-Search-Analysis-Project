package org.hibernate.bugs.tuples;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "test_entity")
public class TestEntity {
	private UUID id;
	private String name;
	private String value;
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	
	@Column(name = "name")
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	@Column(name = "val")
	public String getValue() { return value; }
	public void setValue(String value) { this.value = value; }
}
