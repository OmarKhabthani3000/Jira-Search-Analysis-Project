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
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.DatabaseRetrievalMethod;
import org.hibernate.search.query.ObjectLookupMethod;
import org.hibernate.search.query.dsl.QueryBuilder;

import com.abecorn.model.FileReference;
import com.abecorn.model.Item;
import com.abecorn.model.TagReference;


@ApplicationScoped
public class ItemRepository {
	
	@Inject
	private EntityManager em;
	
	
	public Item[][] getAllItems()
	{
		List<Item[]> allItems = new ArrayList<Item[]>();
		String nativeQuery = "{}";
		
		javax.persistence.Query query = em.createNativeQuery(nativeQuery, Item.class);
		List<Item> items = query.getResultList();
		if(items != null && items.size() > 0)
		{
			int count = 0;
			int i = 0;
			Iterator<Item> itr = items.iterator();
			Item[] list = new Item[4];
			while(itr.hasNext())
			{
				
				Item item = itr.next();
				if(count % 4 == 0)
				{
					allItems.add(list);
					list = new Item[4];
					i = 0;
				}
				list[i] = item;
				count++;
				i++;
			}
			if(i > 0)
			{
				allItems.add(list);
			}
		}
		Item[][] values = (Item[][])allItems.toArray(new Item[0][0]);
		
		return values;
	}
	
	public Item getItem(String id)
	{
		String nativeQuery = "{'_id':{'$oid' : '" + id + "'}}";
		//Query query = em.createQuery("from FileReference f where f.itemId = :itemId").setParameter("itemId", new ObjectId(id));
		javax.persistence.Query query = em.createNativeQuery(nativeQuery, Item.class);
		return (Item)query.getSingleResult();
		
	}
	
	public Item getItemByBggId(Long id, String bggType)
	{
		
		Item item = null;
		//String nativeQuery = "{'bggId':" + id + ",'bggType':'" + bggType + "'}";
		
		javax.persistence.Query q = em.createQuery("SELECT item FROM Item item WHERE item.bggId = " + id + " and item.bggType = '"+bggType+"'");
		
		//javax.persistence.Query query = em.createNativeQuery(nativeQuery, Item.class);
		try
		{
			item =  (Item)q.getSingleResult();
		}
		catch(NoResultException e)
		{
			//no result
		}
		
		return item;
	}
	
	
	public List<Item> fullTextSearch(String q, int start, int limit)
	{
		FullTextEntityManager ftem = Search.getFullTextEntityManager(em);
		
		QueryBuilder qBuilder = ftem.getSearchFactory().buildQueryBuilder().forEntity(Item.class).get();
		
		//Create a Lucene Query
		Query lq = qBuilder.keyword().fuzzy().withThreshold(.1f).withPrefixLength(1).onFields("itemName","itemDescription").matching(q).createQuery();
		
		//Transform the Lucene Query in a JPA Query:
		FullTextQuery ftQuery = ftem.createFullTextQuery(lq, Item.class);
		
		//This is a requirement when using Hibernate OGM instead of ORM:
		ftQuery.initializeObjectsWith(ObjectLookupMethod.SKIP, DatabaseRetrievalMethod.FIND_BY_ID).setFirstResult(start).setMaxResults(limit);
		
		
		
		List<Item> resultList = ftQuery.getResultList();
		return resultList;
	}
	
}
