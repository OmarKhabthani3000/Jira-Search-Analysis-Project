package com.mycompany.tools.domain;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class JpaAttributeConverterExample {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String summary;
	private String description;
	@Convert(converter = BooleanTFConverter.class)
	private Boolean isActive;

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean param) {
		this.isActive = param;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Todo [summary=" + summary + ", description=" + description
				+ "isActive= " + isActive + "]";
	}

}