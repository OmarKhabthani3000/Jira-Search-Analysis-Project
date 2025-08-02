/*
 * Copyright (c) 2019 Aegaeon IT S.A.. All Rights Reserved
 * This program is a trade secret of Aegaeon IT S.A., and it is not to be
 * reproduced, published, disclosed to others, copied, adapted, distributed
 * or displayed without the prior authorization of Aegaeon IT S.A..
 * Licensee agrees to attach or embed this notice on all copies of the
 * program, including partial copies or modified versions thereof, and
 * is licensed subject to restrictions on use and distribution.
 */
package com.aegaeon.dbmodel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.AssociationOverride;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Table.ForeignKeyKey;
import org.junit.Assert;
import org.junit.Test;

public class AssociationOverrideForeignKeyTest {
	@Test
	public void no_override() {
		doTest(SimpleEntity.class, NoOverrideEntity.class);
	}
	
	@Test
	public void override() {
		doTest(SimpleEntity.class, OverrideEntity.class);
	}
	
	private void doTest(Class<?>...classes) {
		StandardServiceRegistryBuilder srb = new StandardServiceRegistryBuilder()
			.applySetting( "hibernate.show_sql", "true" )
			.applySetting( "hibernate.format_sql", "true" )
			.applySetting( "hibernate.hbm2ddl.auto", "update" )
			.applySetting( "hibernate.dialect", H2Dialect.class.getName() );
		
		MetadataSources metadataSources = new MetadataSources(srb.build());
		
		for (Class<?> entityClass : classes) {
			metadataSources.addAnnotatedClass(entityClass);
		}

		Metadata metadata = metadataSources.buildMetadata();

		Set<String> foreignKeyNames = new HashSet<String>(2);
		
		for (Collection col : metadata.getCollectionBindings()) {
			Map<ForeignKeyKey, org.hibernate.mapping.ForeignKey> foreignKeys = col.getCollectionTable().getForeignKeys();
			
			for (org.hibernate.mapping.ForeignKey fk : foreignKeys.values()) {
				foreignKeyNames.add(fk.getName());
			}
		}
		
		Assert.assertEquals(2, foreignKeyNames.size());
		Assert.assertTrue(foreignKeyNames.contains("fk_source"));
		Assert.assertTrue(foreignKeyNames.contains("fk_link"));
		
	}

	@MappedSuperclass
	public static abstract class NoOverrideBaseClass {
		@Id
		private Long id;
		
		@ManyToMany
		@JoinTable(
			name = "join_table",
			joinColumns = @JoinColumn(name = "source_id"),
			foreignKey = @ForeignKey(name = "fk_source"),
			inverseJoinColumns = @JoinColumn(name = "link_id"),
			inverseForeignKey = @ForeignKey(name = "fk_link")
		)
		public List<SimpleEntity> links;
	}
	
	@Entity
	public static class NoOverrideEntity extends NoOverrideBaseClass {
		
	}
	
	@MappedSuperclass
	public static abstract class OverrideBaseClass {
		@Id
		private Long id;
		
		@ManyToMany
		public List<SimpleEntity> links;
	}
	
	@Entity
	@AssociationOverride(
		name = "links",
		joinTable = @JoinTable(
			name = "join_table",
			joinColumns = @JoinColumn(name = "source_id"),
			foreignKey = @ForeignKey(name = "fk_source"),
			inverseJoinColumns = @JoinColumn(name = "link_id"),
			inverseForeignKey = @ForeignKey(name = "fk_link")
		)
	)
	public static class OverrideEntity extends OverrideBaseClass {
		
	}
	
	
	@Entity
	public static class SimpleEntity {
		@Id
		private Long id;
	}
}
