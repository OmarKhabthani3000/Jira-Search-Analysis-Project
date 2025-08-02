package com.abecorn.mongo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

import org.apache.lucene.search.Query;
import org.bson.types.ObjectId;
import org.hibernate.Hibernate;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.DatabaseRetrievalMethod;
import org.hibernate.search.query.ObjectLookupMethod;
import org.hibernate.search.query.dsl.QueryBuilder;

import com.abecorn.model.FileReference;
import com.abecorn.model.Item;
import com.abecorn.model.TagReference;
import com.abecorn.model.wrappers.ItemSearchWrapper;


@ApplicationScoped
public class ItemRepository {
	
	@Inject
	private EntityManager em;
	
	
	
	public List<Item> fullTextSearch(String q, int start, int limit)
	{
		
		//Transform the Lucene Query in a JPA Query:
		FullTextQuery ftQuery = createFullTextQuery(q);
		
		//This is a requirement when using Hibernate OGM instead of ORM:
		ftQuery.initializeObjectsWith(ObjectLookupMethod.SKIP, DatabaseRetrievalMethod.FIND_BY_ID).setFirstResult(start).setMaxResults(limit);
		
		
		List<Item> resultList = ftQuery.getResultList();
		return resultList;
	}
	
	public Integer countFullTextSearch(String q)
	{
		FullTextQuery ftQuery = createFullTextQuery(q);
		return ftQuery.getResultSize();
	}
	
	private FullTextQuery createFullTextQuery(String q)
	{
		FullTextEntityManager ftem = Search.getFullTextEntityManager(em);
		
		QueryBuilder qBuilder = ftem.getSearchFactory().buildQueryBuilder().forEntity(Item.class).get();
		
		//Create a Lucene Query
		Query lq = qBuilder.keyword().fuzzy().withThreshold(.1f).withPrefixLength(1).onFields("itemName","itemDescription").matching(q).createQuery();
		
		//Transform the Lucene Query in a JPA Query:
		FullTextQuery ftQuery = ftem.createFullTextQuery(lq, Item.class);
		return ftQuery;
	}
	
	public ItemSearchWrapper searchItems(String q, int start, int limit)
	{
		ItemSearchWrapper itemSearchWrapper = new ItemSearchWrapper();
		List<Item> items = fullTextSearch(q,start,limit);
		itemSearchWrapper.setItems(items);
		itemSearchWrapper.setTotalItems(countFullTextSearch(q));
		return itemSearchWrapper;
		
	}
	
}