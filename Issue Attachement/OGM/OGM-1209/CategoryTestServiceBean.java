package org.hibernate.ogm.test;


import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@Singleton
@Lock(LockType.WRITE)
public class CategoryServiceBean {

	@Inject private EntityManager entityManager;

	@Override
	public void createTestData() {
		Category category = generateCategory();
		entityManager.persist(category);
		for (Category subcategory : category.getSubcategories()) {
			entityManager.persist(subcategory);
		}
	}

	@Override
	public void removeTestData() {
		List<Category> categories = entityManager.createQuery("from Category c", Category.class).getResultList();
		if (categories == null || categories.isEmpty()) return;
		for (Category category : categories) {
			if (!category.getSubcategories().isEmpty()) continue;
			entityManager.remove(category);
		}
		for (Category category : categories) {
			if (category.getSubcategories().isEmpty()) continue;
			entityManager.remove(category);
		}
	}

	@Override
	public void reorderSubcategories(Category category) {
		Category persistedCategory = entityManager.find(Category.class, category.getId());
		List<Category> subcategories = new ArrayList<>(category.getSubcategories());
		persistedCategory.getSubcategories().forEach(s -> s.setSortOrder((byte) subcategories.indexOf(s)));
	}

	private Category generateCategory() {
		Category category = new Category("parent", "Parent Category");
		new Category("parent-sub0", "Subcategory 0", category, 0);
		new Category("parent-sub1", "Subcategory 1", category, 1);
		new Category("parent-sub2", "Subcategory 2", category, 2);
		new Category("parent-sub3", "Subcategory 3", category, 3);
		new Category("parent-sub4", "Subcategory 4", category, 4);
		return category;
	}
}
