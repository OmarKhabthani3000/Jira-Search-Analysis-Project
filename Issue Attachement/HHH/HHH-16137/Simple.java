package org.ligoj.app.plugin.prov.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.lang.Nullable;

@Entity
@Table(name = "SIMPLE")
public class Simple {

	@Id
	@GeneratedValue
	private @Nullable Integer id;

	@Nullable
	public Integer getId() {
		return id;
	}

	public void setId(@Nullable Integer id) {
		this.id = id;
	}

	private Double maxRam;
	public Double getMaxRam() { return maxRam;}
	public void setMaxRam(Double maxRam) { this.maxRam = maxRam;}

}
