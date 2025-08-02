package org.hibernate.id;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

import junit.framework.Assert;
import org.jboss.logging.Logger;
import org.junit.Test;

import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;

/**
 * @author Strong Liu <stliu@hibernate.org>
 */
public class EmbeddedLoadTest extends BaseCoreFunctionalTestCase {
	private static Logger logger = Logger.getLogger( EmbeddedLoadTest.class );

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class[] { Item.class, Named.class };
	}


	@Test
	public void testLoadedEmbeddedIsNull() {
		logger.info( "persist data, only Item without the embedded Named" );
		Session session = openSession();
		session.beginTransaction();
		session.persist( new Item( 1l ) );
		session.getTransaction().commit();
		session.clear();
		session.close();

		logger.info(
				"now try to LOAD the Item and see if the Named returned, since DB doesn't have this Named, so, " +
						"it is expected the getNamed() returns an Non-Null instance ( due to it is initialized in the Item, " +
						"and all attributes in Named instance are null"
		);
		session = openSession();
		session.beginTransaction();

		Item item = (Item) session.load( Item.class, 1l );

		Assert.assertNotNull( item );
		Assert.assertNotNull( item.getNamed() );
		Assert.assertNull( item.getNamed().getFirstName() );
		Assert.assertNull( item.getNamed().getSecondName() );
		session.getTransaction().commit();
		session.close();

		logger.info( "try GET" );

		session = openSession();
		session.beginTransaction();

		item = (Item) session.get( Item.class, 1l );

		Assert.assertNotNull( item );
		Assert.assertNotNull( item.getNamed() );
		Assert.assertNull( item.getNamed().getFirstName() );
		Assert.assertNull( item.getNamed().getSecondName() );
		session.getTransaction().commit();
		session.close();

		logger.info( "update Item w/ Named instance" );
		session = openSession();
		session.beginTransaction();
		item = (Item) session.get( Item.class, 1l );
		item.setNamed( new Named( "Strong", "Liu" ) );

		Assert.assertNotNull( item );
		Assert.assertNotNull( item.getNamed() );

		session.getTransaction().commit();
		session.close();


		logger.info(
				"now try to LOAD the Item and see if the Named returned, since DB doesn't have this Named, so, " +
						"it is expected the getNamed() returns an Non-Null instance ( due to it is initialized in the Item, " +
						"and all attributes in Named instance are null"
		);
		session = openSession();
		session.beginTransaction();

		item = (Item) session.load( Item.class, 1l );

		Assert.assertNotNull( item );
		Assert.assertNotNull( item.getNamed() );
		Assert.assertEquals( "Strong", item.getNamed().getFirstName() );
		Assert.assertEquals( "Liu", item.getNamed().getSecondName() );
		session.getTransaction().commit();
		session.close();

		logger.info( "try GET" );

		session = openSession();
		session.beginTransaction();

		item = (Item) session.get( Item.class, 1l );

		Assert.assertNotNull( item );
		Assert.assertNotNull( item.getNamed() );
		Assert.assertEquals( "Strong", item.getNamed().getFirstName() );
		Assert.assertEquals( "Liu", item.getNamed().getSecondName() );
		session.getTransaction().commit();
		session.close();


	}

	@Entity
	public static class Item {

		private long id;

		private Named named = new Named();

		public Item(final long id) {
			this.id = id;
		}

		public Item() {
		}

		@Id
		public long getId() {
			return id;
		}

		public void setId(final long id) {
			this.id = id;
		}

		@Embedded
		public Named getNamed() {
			return named;
		}

		public void setNamed(final Named named) {
			if ( named != null ) {
				this.named = named;
			}
		}
	}

	@Embeddable
	public static class Named implements Serializable {
		private String firstName;
		private String secondName;

		public Named(final String firstName, final String secondName) {
			this.firstName = firstName;
			this.secondName = secondName;
		}

		public Named() {
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(final String firstName) {
			this.firstName = firstName;
		}

		public String getSecondName() {
			return secondName;
		}

		public void setSecondName(final String secondName) {
			this.secondName = secondName;
		}

		@Override
		public boolean equals(final Object o) {
			if ( this == o ) {
				return true;
			}
			if ( !( o instanceof Named ) ) {
				return false;
			}

			final Named named = (Named) o;

			if ( firstName != null ? !firstName.equals( named.firstName ) : named.firstName != null ) {
				return false;
			}
			if ( secondName != null ? !secondName.equals( named.secondName ) : named.secondName != null ) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			int result = firstName != null ? firstName.hashCode() : 0;
			result = 31 * result + ( secondName != null ? secondName.hashCode() : 0 );
			return result;
		}
	}


}
