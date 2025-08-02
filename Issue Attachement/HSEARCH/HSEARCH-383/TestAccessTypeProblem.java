import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import junit.framework.TestCase;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

/**
 * This test case demonstrates a subtle bug in the Hibernate Search project.
 *
 * This occurs when indexing an Entity annotated as @IndexedEmbedded if the
 * object that it is @ContainedIn is a proxy object that has not yet been loaded
 * and if the @AccessType of the @Id of the proxy object has been overriden from
 * field to property.
 *
 * This is because Hibernate Search does not respect the @AccessType annotation,
 * and so attempts to read the id of the parent object directly from the member
 * variable, which is not initialised in the proxy and so returns 0 in the case
 * demonstrated.
 *
 * The problem is in:
 *   org.hibernate.search.engine.DocumentBuilderIndexedEntity.checkDocumentId().
 *
 * This results in a record in the Lucene index that has no reference to the
 * containing instance. So, while the number of results is returned correctly,
 * any attempt to actually retrieve the results and convert them into Hibernate
 * objects fails.
 *
 * @author Steven Knock
 * @since 2009/6/23
 */
public class TestAccessTypeProblem extends TestCase
{
	private SessionFactory sessionFactory;

	private Session session;

	private FullTextSession textSession;

	public void testAccessTypeWithProxy()
	{
		Transaction tx = session.beginTransaction();

		// 1. Create Parent record
		Parent p = createParent();

		// 2. Remove Parent from session
		session.evict( p );

		// 3. Get a proxy to parent - its Id member variable will be 0 after loading
		p = loadParent( p.getId() );

		// 4. Create Child record using proxy parent
		//    At this point, the search code writes to the lucene index, but uses
		//    the 0 value of the Parent.id because it is reading directly from the
		//    member variable and not using the getId() method, as directed by the
		//    @AccessType( "property" ) annotation.
		createChild( p );

		tx.commit();

		// 5. Perform a text-query to determine whether child was correctly indexed
		FullTextQuery query = prepareTextQuery();

		// This will return 1, because the child record is indexed
		assertEquals( 1, query.getResultSize() );

		// However, this will return 0 because the parent id is 0 in the index, and so can't be retrieved
		// This line currently fails and reveals the bug
		assertEquals( 1, query.list().size() );
	}

	public void testAccessTypeWithoutProxy()
	{
		// This is the same test case, but this time without using a proxy parent
		// Naturally, it works as expected, because the parent.id member variable
		// contains the parent's id, and so the correct value is written to the index.

		Transaction tx = session.beginTransaction();
		Parent p = createParent();
		createChild( p );
		tx.commit();

		FullTextQuery query = prepareTextQuery();
		assertEquals( 1, query.getResultSize() );
		assertEquals( 1, query.list().size() );
	}

	// Helper routines to avoid duplication

	private Parent createParent()
	{
		Parent p = new Parent();
		p.setName( "Parent" );
		session.save( p );
		session.flush();
		return p;
	}

	private Parent loadParent( long id )
	{
		return ( Parent ) session.load( Parent.class, Long.valueOf( id ) );
	}

	private Child createChild( Parent p )
	{
		Child c = new Child();
		c.setName( "Child" );
		c.setParent( p );
		p.getChildren().add( c );
		session.saveOrUpdate( p );
		session.flush();
		return c;
	}

	private FullTextQuery prepareTextQuery()
	{
		Query query = new TermQuery( new Term( "children.name", "child" ) );
		return textSession.createFullTextQuery( query );
	}

	protected void setUp()
	{
		AnnotationConfiguration cfg = new AnnotationConfiguration();
		cfg.setProperty( "hibernate.connection.driver_class", "org.hsqldb.jdbcDriver" );
		cfg.setProperty( "hibernate.connection.url", "jdbc:hsqldb:mem:testdb" );
		cfg.setProperty( "hibernate.dialect", "org.hibernate.dialect.HSQLDialect" );
		cfg.setProperty( "hibernate.hbm2ddl.auto", "create" );
		cfg.addAnnotatedClass( Parent.class );
		cfg.addAnnotatedClass( Child.class );

		sessionFactory = cfg.buildSessionFactory();
		session = sessionFactory.openSession();
		textSession = Search.getFullTextSession( session );
		textSession.purgeAll( Parent.class );
	}

	protected void tearDown()
	{
		if ( textSession != null && textSession.isOpen() )
		{
			textSession.close();
		}
		if ( session != null && session.isOpen() )
		{
			session.close();
		}
		if ( sessionFactory != null && !sessionFactory.isClosed() )
		{
			sessionFactory.close();
		}
	}
}

@Entity
@Indexed
class Parent
{
	@Id
	@GeneratedValue
	@AccessType( "property" )
	private long id;

	@Field
	private String name;

	@OneToMany( mappedBy = "parent" )
	@Cascade( { CascadeType.SAVE_UPDATE, CascadeType.DELETE_ORPHAN } )
	@IndexedEmbedded
	private Set< Child > children = new HashSet< Child >();

	public long getId()
	{
		return id;
	}

	void setId( long id )
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public Set< Child > getChildren()
	{
		return children;
	}
}

@Entity
class Child
{
	@Id
	@GeneratedValue
	@AccessType( "property" )
	private long id;

	@Field
	private String name;

	@ManyToOne( fetch = FetchType.LAZY )
	@ContainedIn
	private Parent parent;

	public long getId()
	{
		return id;
	}

	void setId( long id )
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public Parent getParent()
	{
		return parent;
	}

	public void setParent( Parent parent )
	{
		this.parent = parent;
	}
}