package org.hibernate.bugs.hash;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "top")
public class Top {
	private UUID id;
	private String name;
	private Set<Middle> middles;
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	
	@Column(name = "name")
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	
	@OneToMany(mappedBy = "top", cascade = { CascadeType.ALL })
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	public Set<Middle> getMiddles() { return middles; }
	public void setMiddles(Set<Middle> middles) { this.middles = middles; }
	public void addMiddle(Middle middle) {
		if (middles == null) middles = new HashSet<Middle>();
		middles.add(middle);
	}
}
