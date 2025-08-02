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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "middle")
public class Middle {
	private UUID id;
	private Top top;
	private Set<Bottom> bottoms;
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }

	@ManyToOne(optional = false)
	@JoinColumn(name = "top_id", nullable = false)
	public Top getTop() { return top; }
	public void setTop(Top student) { this.top = student; }
	
	@OneToMany(mappedBy = "middle", cascade = {CascadeType.ALL})
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	public Set<Bottom> getBottoms() { return bottoms; }
	public void setBottoms(Set<Bottom> bottoms) { this.bottoms = bottoms; }
	public void addBottom(Bottom bottom) {
		if (bottoms == null) bottoms = new HashSet<Bottom>();
		bottoms.add(bottom);
	}
}
