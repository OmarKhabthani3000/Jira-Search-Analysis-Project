package org.hibernate.jpamodelgen.test.enumconstant;

import jakarta.annotation.Generated;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.util.List;

@StaticMetamodel(CookBook.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class CookBook_ {

	public static final String QUERY_FIND_GOOD_BOOKS = "#findGoodBooks";
	public static final String ISBN = "isbn";
	public static final String TITLE = "title";
	public static final String BOOK_TYPE = "bookType";

	
	/**
	 * Execute named query {@value #QUERY_FIND_GOOD_BOOKS} defined by annotation of {@link CookBook}.
	 **/
	public static List<CookBook> findGoodBooks(@Nonnull EntityManager entityManager) {
		return entityManager.createNamedQuery(QUERY_FIND_GOOD_BOOKS)
				.getResultList();
	}
	
	/**
	 * @see org.hibernate.jpamodelgen.test.enumconstant.CookBook#isbn
	 **/
	public static volatile SingularAttribute<CookBook, String> isbn;
	
	/**
	 * @see org.hibernate.jpamodelgen.test.enumconstant.CookBook#title
	 **/
	public static volatile SingularAttribute<CookBook, String> title;
	
	/**
	 * @see org.hibernate.jpamodelgen.test.enumconstant.CookBook
	 **/
	public static volatile EntityType<CookBook> class_;
	
	/**
	 * @see org.hibernate.jpamodelgen.test.enumconstant.CookBook#bookType
	 **/
	public static volatile SingularAttribute<CookBook, BookType> bookType;

}

