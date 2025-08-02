package org.hibernate.test.annotations.cache;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(region = "item", usage = CacheConcurrencyStrategy.READ_WRITE)
public class Item {
	@Id
	@GeneratedValue
	private Long id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public Long getVersion() {
		return version;
	}

	private String name;
	@Version
	private Long version;
}
