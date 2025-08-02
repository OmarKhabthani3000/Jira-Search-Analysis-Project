/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.override;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.type.ManyToOneType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Steve Ebersole
 */
public class InheritedAttributeOverridingTest extends BaseUnitTestCase {
	private StandardServiceRegistry standardServiceRegistry;

	@Before
	public void buildServiceRegistry() {
		standardServiceRegistry = new StandardServiceRegistryBuilder().build();
	}

	@After
	public void releaseServiceRegistry() {
		if ( standardServiceRegistry != null ) {
			StandardServiceRegistryBuilder.destroy( standardServiceRegistry );
		}
	}

	@Test
	@TestForIssue( jiraKey = "HHH-9485" )
	public void testInheritedAttributeOverridingMappedsuperclass() {
		Metadata metadata = new MetadataSources( standardServiceRegistry )
				.addAnnotatedClass( A.class )
				.addAnnotatedClass( B.class )
				.buildMetadata();

		( (MetadataImplementor) metadata ).validate();
	}

	@Test
	@TestForIssue( jiraKey = "HHH-9485" )
	public void testInheritedAttributeOverridingEntity() {
		Metadata metadata = new MetadataSources( standardServiceRegistry )
				.addAnnotatedClass( C.class )
				.addAnnotatedClass( D.class )
				.buildMetadata();

		( (MetadataImplementor) metadata ).validate();
	}
	
	@Test
	public void testInheritedAttributeOverridingTargetEntity() {
		Metadata metadata = new MetadataSources( standardServiceRegistry )
				.addAnnotatedClass( C.class )
				.addAnnotatedClass( D.class )
				.addAnnotatedClass( F.class )
				.addAnnotatedClass( E.class )
				.buildMetadata();

		( (MetadataImplementor) metadata ).validate();
		PersistentClass target = metadata.getEntityBinding("org.hibernate.test.annotations.override.InheritedAttributeOverridingTest$F");
		SingleTableSubclass targetClass = (SingleTableSubclass)target;
		
		Property targetProp = targetClass.getProperty("target");
		ManyToOneType type = (ManyToOneType) targetProp.getType();
		String entityName = type.getAssociatedEntityName();
		Assert.assertEquals("org.hibernate.test.annotations.override.InheritedAttributeOverridingTest$D", entityName);
	}

	@MappedSuperclass
	public static class A {
		private Integer id;
		private String name;

		@Id
		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	@Entity( name = "B" )
	public static class B extends A {
		@Override
		public String getName() {
			return super.getName();
		}

		@Override
		public void setName(String name) {
			super.setName( name );
		}
	}


	@Entity( name = "C" )
	public static class C {
		private Integer id;
		private String name;

		@Id
		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	@Entity( name = "D" )
	public static class D extends C {
		@Override
		public String getName() {
			return super.getName();
		}

		@Override
		public void setName(String name) {
			super.setName( name );
		}
	}
	
	@Entity( name = "E" )
	public static class E {
		private Integer id;
		private C target;
		
		@Id
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		
		@ManyToOne(targetEntity=C.class)
		public C getTarget() {
			return target;
		}
		
		public void setTarget(C target) {
			this.target = target;
		}
	}
	
	@Entity( name = "F" )
	public static class F extends E {
		@ManyToOne(targetEntity=D.class)
		public C getTarget() {
			return super.getTarget();
		}
	}		
}
