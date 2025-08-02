package org.hibernate.orm.test.query.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.query.spi.NativeQueryImplementor;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.junit.Before;
import org.junit.Test;


@DomainModel(
		annotatedClasses = {
				JoinNativeQueryTest.Shelf.class,
				JoinNativeQueryTest.Book.class,
		}
)
@SessionFactory
public class JoinNativeQueryTest extends BaseNonConfigCoreFunctionalTestCase {
	private static final String SHELF_ID = "shelf1";
	private static final String FILE_ID = "file1";

	@Before
	public void setUp() {
		inTransaction(
				session -> {
					Shelf shelf = new Shelf();
					shelf.setShelfid( SHELF_ID );
					shelf.setArea( "nonfiction" );
					shelf.setPosition( 5 );
					shelf.setShelfNumber( 3 );
					shelf.setBooks( new HashSet<>() );
					session.persist( shelf );
					
					Book book = new Book( FILE_ID );
					book.setTitle( "Birdwatchers Guide to Dodos" );
					book.setShelf( shelf );
					session.persist( book );
				}
		);
	}

	@Test
	public void testNativeQueryWithPlaceholders() {
		inTransaction(
				session -> {
					
					NativeQueryImplementor<Book> query = session
							.createNativeQuery(
									"select {book.*} from BOOK_T book, SHELF_BOOK book_1_ where book.fileid = book_1_.fileid" );
					query.addEntity( "book", Book.class );
					List<Book> results = query.list();

					assertEquals( 1, results.size() );
					Book retrievedBook = results.get( 0 );
					
					assertEquals( FILE_ID, retrievedBook.getFileId() );
					assertEquals( "Birdwatchers Guide to Dodos", retrievedBook.getTitle() );
					assertEquals( "nonfiction", retrievedBook.getShelf().getArea() );
					assertEquals( 3, retrievedBook.getShelf().getShelfNumber() );
					assertEquals( SHELF_ID, retrievedBook.getShelf().getShelfid() );
					assertEquals( 5, retrievedBook.getShelf().getPosition() );
				}
		);
	}

	@Entity(name = "Shelf")
	@Table(name = "SHELF")
	public static class Shelf {
		@Id
		@Column(name = "shelfid")
		private String shelfid;
		
		@Column(name = "area")
		private String area;

		@Column(name = "shelfNumber")
		private Integer shelfNumber;

		@Column(name = "position")
		private Integer position;
		
		@OneToMany
		@JoinTable(name = "SHELF_BOOK", joinColumns = @JoinColumn(name = "shelfid"), inverseJoinColumns = @JoinColumn(name = "fileid"))
		private Set<Book> books;

		public Shelf() {
		}

		public String getShelfid() {
			return shelfid;
		}

		public void setShelfid(String shelfid) {
			this.shelfid = shelfid;
		}

		public String getArea() {
			return area;
		}

		public void setArea(String area) {
			this.area = area;
		}

		public Integer getShelfNumber() {
			return shelfNumber;
		}

		public void setShelfNumber(Integer shelfNumber) {
			this.shelfNumber = shelfNumber;
		}

		public Integer getPosition() {
			return position;
		}

		public void setPosition(Integer position) {
			this.position = position;
		}

		public Set<Book> getBooks() {
			return books;
		}

		public void setBooks(Set<Book> books) {
			this.books = books;
		}
	};
	
	@Entity(name = "Book")
	@Table(name = "BOOK_T")
	public static class Book {
		@Id
		@Column(name = "fileid")
		private String fileid;

		@Column(name = "title")
		private String title;
		
		@ManyToOne(optional = false, fetch = FetchType.EAGER)
		@JoinTable(name = "SHELF_BOOK")
		@JoinColumn(name = "shelfid")
		private Shelf shelf;

		public Book() {
		}

		public Book(final String fileid) {
			this.fileid = fileid;
		}

		public String getFileId() {
			return fileid;
		}

		public void setFileId(final String fileid) {
			this.fileid = fileid;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(final String title) {
			this.title = title;
		}

		public Shelf getShelf() {
			return shelf;
		}

		public void setShelf(Shelf shelf) {
			this.shelf = shelf;
		}
	}

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] { Shelf.class, Book.class };
	}
}
