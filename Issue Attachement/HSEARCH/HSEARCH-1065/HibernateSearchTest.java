package com.seanergie.persistence;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import junit.framework.Assert;

import org.apache.lucene.search.Query;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.junit.Test;

public class HibernateSearchTest {

	@Test
	@SuppressWarnings( "unchecked" )
	public void testFuzzySearchOnEnum() {
		TestEntity testEntity = new TestEntity();
		testEntity.setText( "Sample text" );
		testEntity.setTestEnum( TestEnum.TWO );

		SessionsManager.beginTransaction();
		SessionsManager.getSession().persist( testEntity );
		SessionsManager.commitAndClose();

		testEntity = SessionsManager.getSession().find( TestEntity.class, testEntity.getId() );
		Assert.assertNotNull( testEntity );

		FullTextEntityManager ftem = SessionsManager.getFullTextSession();

		Query query = ftem.getSearchFactory()
				.buildQueryBuilder()
				.forEntity( TestEntity.class )
				.get()
				.keyword()
				.fuzzy()
				.onField( "text" )
				.matching( "Sample" )
				.createQuery();

		List<TestEntity> ftResults = ftem.createFullTextQuery( query, TestEntity.class ).getResultList();

		Assert.assertEquals( 1, ftResults.size() );
		Assert.assertEquals( testEntity.getId(), ftResults.get( 0 ).getId() );

		query = ftem.getSearchFactory()
				.buildQueryBuilder()
				.forEntity( TestEntity.class )
				.get()
				.keyword()
				.fuzzy()
				.onField( "testEnum" )	// CRASH HERE
				.matching( "TWO" )
				.createQuery();
		ftResults = ftem.createFullTextQuery( query, TestEntity.class ).getResultList();

		Assert.assertEquals( 1, ftResults.size() );
		Assert.assertEquals( testEntity.getId(), ftResults.get( 0 ).getId() );

		query = ftem.getSearchFactory()
				.buildQueryBuilder()
				.forEntity( TestEntity.class )
				.get()
				.keyword()
				.fuzzy()
				.onField( "testEnum" )
				.matching( "ONE" )
				.createQuery();
		ftResults = ftem.createFullTextQuery( query, TestEntity.class ).getResultList();

		Assert.assertTrue( ftResults.isEmpty() );

		query = ftem.getSearchFactory()
				.buildQueryBuilder()
				.forEntity( TestEntity.class )
				.get()
				.keyword()
				.fuzzy()
				.onFields( "text", "testEnum" )
				.matching( "Sample" )
				.createQuery();
		ftResults = ftem.createFullTextQuery( query, TestEntity.class ).getResultList();

		Assert.assertEquals( 1, ftResults.size() );
		Assert.assertEquals( testEntity.getId(), ftResults.get( 0 ).getId() );
	}

	@Entity
	@Indexed
	public static class TestEntity {
		private Long id;
		private String text;
		private TestEnum testEnum;

		@Id
		@GeneratedValue
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}

		@Basic
		@Field( index = Index.YES, analyze = Analyze.YES )
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}

		@Basic
		@Field( index = Index.YES, analyze = Analyze.NO )
		public TestEnum getTestEnum() {
			return testEnum;
		}
		public void setTestEnum(TestEnum testEnum) {
			this.testEnum = testEnum;
		}
	}

	public static enum TestEnum {
		ONE, TWO, THREE;
	}
}