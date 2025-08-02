//$Id: ProjectionQueryTest.java 11687 2007-06-13 19:52:17Z epbernard $
package org.hibernate.search.test.query;

import java.util.List;

import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchException;
import org.hibernate.search.test.SearchTestCase;

/**
 * @author Emmanuel Bernard
 */
public class ProjectionQueryTest extends SearchTestCase {

	public void testProjection() throws Exception {
		FullTextSession s = Search.createFullTextSession( openSession() );
		Transaction tx = s.beginTransaction();
		Book book = new Book( 1, "La chute de la petite reine a travers les yeux de Festina", "La chute de la petite reine a travers les yeux de Festina, blahblah" );
		s.save( book );
		Author emmanuel = new Author();
		emmanuel.setName( "Emmanuel" );
		s.save( emmanuel );
		book.setMainAuthor(emmanuel);
		tx.commit();
		s.clear();
		tx = s.beginTransaction();
		QueryParser parser = new QueryParser( "title", new StopAnalyzer() );

		Query query = parser.parse( "summary:Festina" );
		org.hibernate.search.FullTextQuery hibQuery = s.createFullTextQuery( query, Book.class );
		hibQuery.setIndexProjection( "id", "summary", "mainAuthor.name");
		
		List result = hibQuery.list();
		assertNotNull( result );
		assertEquals( "Query with no explicit criteria", 1, result.size() );
		Object[] projection = (Object[]) result.get( 0 );
		assertEquals( "id", 1, projection[0] );
		assertEquals( "summary", "La chute de la petite reine a travers les yeux de Festina", projection[1] );
		assertEquals( "mainAuthor.name (embedded objects)", "Emmanuel", projection[2] );

		hibQuery = s.createFullTextQuery( query, Book.class );
		hibQuery.setIndexProjection( "id", "body", "mainAuthor.name");

		try {
			result = hibQuery.list();
			fail("Projecting an unstored field should raise an exception");
		}
		catch (SearchException e) {
			//success
		}


		hibQuery = s.createFullTextQuery( query, Book.class );
		hibQuery.setIndexProjection();
		result = hibQuery.list();
		assertNotNull( result );
		assertEquals( 1, result.size() );
		assertTrue( "Should not trigger projection", result.get(0) instanceof Book);

		hibQuery = s.createFullTextQuery( query, Book.class );
		hibQuery.setIndexProjection(null);
		result = hibQuery.list();
		assertNotNull( result );
		assertEquals( 1, result.size() );
		assertTrue( "Should not trigger projection", result.get(0) instanceof Book);

		//cleanup
		for (Object element : s.createQuery( "from " + Book.class.getName() ).list()) s.delete( element );
		for (Object element : s.createQuery( "from " + Author.class.getName() ).list()) s.delete( element );
		tx.commit();
		s.close();
	}
    
    public void testProjectionNullFields(){
        FullTextSession s = Search.createFullTextSession( openSession() );
        Transaction tx = s.beginTransaction();
        Book book = new Book( new Integer(1), "La chute de la petite reine a travers les yeux de Festina", "La chute de la petite reine a travers les yeux de Festina, blahblah" );
        s.save( book );
        Author emmanuel = new Author();
        //Just leave the authors name null here!
        s.save( emmanuel );
        book.setMainAuthor(emmanuel);
        tx.commit();
        s.clear();
        tx = s.beginTransaction();
        QueryParser parser = new QueryParser( "title", new StopAnalyzer() );
        Query query = null;
        try{
            query = parser.parse( "summary:Festina" );
        }catch(ParseException e){
            e.printStackTrace();
        }
        org.hibernate.search.FullTextQuery hibQuery = s.createFullTextQuery( query, Book.class );
        hibQuery.setIndexProjection( "id", "summary", "mainAuthor.name");
        
        List result=null;
        try{
            result = hibQuery.list();
        }catch(NullPointerException e){
            fail("No NPE should be thrown when projecting fields with null values.");
        }
            
        assertNotNull( result );
        assertEquals( "Query with no explicit criteria", 1, result.size() );
        Object[] projection = (Object[]) result.get( 0 );
        assertEquals( "id", new Integer(1), projection[0] );
        assertEquals( "summary", "La chute de la petite reine a travers les yeux de Festina", projection[1] );
        assertEquals( "mainAuthor.name (embedded objects)", null, projection[2] );

//      cleanup
        for (Object element : s.createQuery( "from " + Book.class.getName() ).list()) s.delete( element );
        for (Object element : s.createQuery( "from " + Author.class.getName() ).list()) s.delete( element );
        tx.commit();
        s.close();
    }


	protected Class[] getMappings() {
		return new Class[] {
				Book.class,
				Author.class
		};
	}
}
