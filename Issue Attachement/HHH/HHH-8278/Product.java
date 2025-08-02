package com.tecnotree.upc.hibernate.entity.standard;

import java.math.BigDecimal;


import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

/**
 * Product entity. @author MyEclipse Persistence Tools
 */

@XmlRootElement(name="product")
@Entity
@Table(name = "PRODUCT")
@SelectBeforeUpdate
@DynamicUpdate
public class Product implements java.io.Serializable {

	// Fields
	private static final long serialVersionUID = 1L;
	private BigDecimal id;
	private String name;
	private Set<ProductTResource> resource=new HashSet<ProductTResource>(0);



	// Constructors

	/** default constructor */
	public Product() {
	}
	/** full constructor */
public Product(String name, Set<ProductTResource> resource) {
		super();
		this.name = name;
		this.resource = resource;
	}

	// Property accessors
	@GenericGenerator(name = "generator", strategy = "increment")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "ID", unique = true, nullable = false, precision = 22, scale = 0)
	public BigDecimal getId() {
		return this.id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	@Column(name = "NAME", length = 50)
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	
	@XmlElementWrapper(name="associatedProductResources")
	@XmlElement(name="associatedProductResource")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "id.product",cascade=CascadeType.ALL)
	public Set<ProductTResource> getResource() {
		return resource;
	}

	public void setResource(Set<ProductTResource> resource) {
		Set<ProductTResource> resource1=getResource();
		System.out.println(resource1.toString());
		resource1=resource;
		this.resource = resource;
		setProductRefrenceForResource();
	}
	public void setProductRefrenceForResource(){
		if(resource!=null){
			Iterator iter = resource.iterator();
			while(iter.hasNext())
			{
				ProductTResource productTResource = (ProductTResource)iter.next();
				productTResource.setProduct(this);
			}
			}
	}
}