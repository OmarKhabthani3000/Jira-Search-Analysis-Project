package org.hibernate.ogm.test;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SortNatural;
import org.hibernate.search.annotations.Indexed;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Indexed
public class Category implements Serializable, Comparable<Category> {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private String name;
	private byte sortOrder;

	@ManyToOne
	private Category parentCategory;

	@OneToMany(mappedBy = "parentCategory", fetch = FetchType.EAGER)
	@SortNatural
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private SortedSet<Category> subcategories;

	public Category(String id, String name) {
		this.id = id;
		setName(name);
	}

	public Category(String id, String name, Category parentCategory, int sortOrder) {
		this.id = id;
		setName(name);
		setSortOrder((byte) sortOrder);
		setParentCategory(parentCategory);
	}

	protected Category() {
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int compareTo(Category category) {
		return Byte.compare(sortOrder, category.getSortOrder());
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(byte sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Category getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(Category parentCategory) {
		if (this.parentCategory != null) {
			this.parentCategory.subcategories.remove(this);
		}
		this.parentCategory = parentCategory;
		if (parentCategory != null) {
			parentCategory.getSubcategories().add(this);
		}
	}

	public SortedSet<Category> getSubcategories() {
		if (subcategories == null) subcategories = new TreeSet<>();
		return subcategories;
	}

	public void setSubcategories(SortedSet<Category> subcategories) {
		this.subcategories = subcategories;
	}

}
