/*
 * Hibernate OGM, Domain model persistence for NoSQL datastores
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.ogm.datastore.mongodb.test.gridfs;

import static org.fest.assertions.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.ogm.datastore.mongodb.impl.MongoDBDatastoreProvider;
import org.hibernate.ogm.datastore.mongodb.type.GridFS;
import org.hibernate.ogm.datastore.spi.DatastoreProvider;
import org.hibernate.ogm.hibernatecore.impl.OgmSessionFactoryImpl;
import org.hibernate.ogm.utils.TestForIssue;
import org.hibernate.ogm.utils.jpa.OgmJpaTestCase;
import org.junit.After;
import org.junit.Test;

import com.mongodb.client.MongoDatabase;

/**
 * @author Davide D'Alto
 * @author Sergey Chernolyas &amp;sergey_chernolyas@gmail.com&amp;
 * @see <a href="http://mongodb.github.io/mongo-java-driver/3.4/driver/tutorials/gridfs/">GridFSBucket</a>
 */
@TestForIssue(jiraKey = "OGM-786")
public class GridFSTest2 extends OgmJpaTestCase {

	private static final String DEFAULT_BUCKET_NAME = Photo.class.getSimpleName() + "_bucket";

	/*
	 * WARNING: GridFS makes sense for big files (over 16 MB). In case of error
	 * the test might try to print the result and it will take some time.
	 *
	 * I will keep this to a high value to make sure that there are no problems but,
	 * for development, it makes sense to use something smaller.
	 */
	private static final int CONTENT_SIZE = 30_000_000; // ~30 MB

	private static final String STRING_CONTENT_1 = createString( 'a', CONTENT_SIZE );
	private static final String STRING_CONTENT_2 = createString( 'x', CONTENT_SIZE );

	private static final byte[] BYTE_ARRAY_CONTENT_1 = STRING_CONTENT_1.getBytes();
	private static final byte[] BYTE_ARRAY_CONTENT_2 = STRING_CONTENT_2.getBytes();

	private static String createString(char c, int size) {
		char[] chars = new char[size];
		Arrays.fill( chars, c );
		return new String( chars );
	}

	@After
	public void deleteAll() throws Exception {
		removeEntities();
	}

	@Test
	public void testSettingDifferentBuckets() throws Exception {
			GridListEventBody body = new GridListEventBody();
			body.setId( 1L );
			body.setName( "body" );
			body.setGridlist( new GridFS( BYTE_ARRAY_CONTENT_2 ) );

			GridListEventBody body2 = new GridListEventBody();
			body2.setId( 2L );
			body2.setName( "body2" );
			body2.setGridlist( new GridFS( BYTE_ARRAY_CONTENT_2 ) );

			GridListEvent event = new GridListEvent();
			event.setId( 1L );
			event.setName( "event" );
			event.setOgmEventBody( body );

			GridListEvent event2 = new GridListEvent();
			event2.setId( 2L );
			event2.setName( "event" );
			event2.setOgmEventBody( body2 );

		inTransaction( em -> {
			em.persist( body );
			em.persist( body2 );
			em.persist( event );
			em.persist( event2 );
		} );

		inTransaction( em -> {
			List<?> paramList = Arrays.asList( event.getId(), event2.getId() );
			List<GridListEvent> result = em.createQuery("SELECT e FROM GridListEvent e WHERE e.id IN :ids")
				.setParameter( "ids", paramList )
				.getResultList();
			System.out.println( result );
		});
	}

	private void assertThatGridFSAreEqual(GridFS actual, byte[] expected) {
		byte[] bytes = convertToBytes( actual.getInputStream() );
		assertThat( bytes ).isEqualTo( expected );
	}

	private static byte[] convertToBytes(InputStream is) {
		byte[] bytes = new byte[CONTENT_SIZE];
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try ( DataInputStream data = new DataInputStream( is ) ) {
			int nRead = 0;
			while ( ( nRead =  is.read( bytes, 0, CONTENT_SIZE ) ) != -1 ) {
				os.write( bytes, 0, nRead );
			}
			os.flush();
			return os.toByteArray();
		}
		catch (IOException e) {
			throw new RuntimeException( e );
		}
	}

	private MongoDatabase getCurrentDB(EntityManager em) {
		Session session = em.unwrap( Session.class );
		OgmSessionFactoryImpl sessionFactory = (OgmSessionFactoryImpl) session.getSessionFactory();
		MongoDBDatastoreProvider mongoDBDatastoreProvider = (MongoDBDatastoreProvider) sessionFactory.getServiceRegistry()
				.getService( DatastoreProvider.class );
		return mongoDBDatastoreProvider.getDatabase();
	}

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class[]{ GridListEvent.class, GridListEventBody.class };
	}

}
