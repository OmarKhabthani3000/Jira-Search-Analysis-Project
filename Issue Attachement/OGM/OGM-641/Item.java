package com.abecorn.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Indexed
public class Item {
	
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Type(type="objectid")
	private String id;

	
	private String itemName;
	
	private String itemDescription;
	
	private Date dateManufactured;

	private Long bggId;
	
	private String bggType;
	
	@Transient
	private List<String> imageUrls;
	
	@Transient
	private String condition;
	
	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}

	public String getBggType() {
		return bggType;
	}

	public void setBggType(String bggType) {
		this.bggType = bggType;
	}

	public Long getBggId() {
		return bggId;
	}

	public void setBggId(Long bggId) {
		this.bggId = bggId;
	}
	@OneToMany(fetch=FetchType.EAGER)
	private List<FileReference> fileReferences;
	

	public List<FileReference> getFileReferences() {
		return fileReferences;
	}

	public void setFileReferences(List<FileReference> fileReferences) {
		this.fileReferences = fileReferences;
	}

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}


	public Date getDateManufactured() {
		return dateManufactured;
	}

	public void setDateManufactured(Date dateManufactured) {
		this.dateManufactured = dateManufactured;
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
