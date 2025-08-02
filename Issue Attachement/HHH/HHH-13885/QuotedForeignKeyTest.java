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

import java.io.StringWriter;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaExport.Action;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToWriter;
import org.hibernate.tool.schema.spi.ScriptTargetOutput;
import org.hibernate.tool.schema.spi.TargetDescriptor;
import org.junit.Assert;
import org.junit.Test;

public class QuotedForeignKeyTest {
	@Test
	public void test() {
		doTest(SimpleEntity.class, EntityWithLinks.class);
	}
	
	private void doTest(Class<?>...classes) {
		StandardServiceRegistryBuilder srb = new StandardServiceRegistryBuilder()
			.applySetting( "hibernate.globally_quoted_identifiers", "true" )
			.applySetting( "hibernate.hbm2ddl.auto", "update" )
			.applySetting( "hibernate.dialect", H2Dialect.class.getName() );
		
		MetadataSources metadataSources = new MetadataSources(srb.build());
		
		for (Class<?> entityClass : classes) {
			metadataSources.addAnnotatedClass(entityClass);
		}

		Metadata metadata = metadataSources.buildMetadata();
		
		StringWriter writer = new StringWriter();
		
		ScriptTargetOutputToWriter output = new ScriptTargetOutputToWriter(writer);
		
		SchemaExport exporter = new SchemaExport();
		
		exporter.doExecution(
			Action.CREATE,
			false,
			metadata,
			( (MetadataImplementor) metadata ).getMetadataBuildingOptions().getServiceRegistry(),
			new TargetDescriptor() {
				@Override
				public EnumSet<TargetType> getTargetTypes() {
					return EnumSet.of(TargetType.SCRIPT);
				}
				
				@Override
				public ScriptTargetOutput getScriptTargetOutput() {
					return output;
				}
			}
		);
		
		String ddl = writer.toString();
		
		String rawPattern = "alter table \"join_table\" add constraint \"(fk_source|FK73rt4ujemgkh5lya42539u383)\" foreign key ";
		
		Pattern pattern = Pattern.compile(rawPattern);
		
		Matcher matcher = pattern.matcher(ddl);
		
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		
		Assert.assertEquals(2, count);
	}

	
	@Entity
	public static class EntityWithLinks {
		@Id
		private Long id;
		
		@ManyToMany
		@JoinTable(
			name = "join_table",
			joinColumns = @JoinColumn(name = "source_id"),
			foreignKey = @ForeignKey(name = "fk_source"),
			inverseJoinColumns = @JoinColumn(name = "link_id")
			// -- inverse foreign key name is generated
		)
		private List<SimpleEntity> links;
	}
	
	
	
	@Entity
	public static class SimpleEntity {
		@Id
		private Long id;
	}
}
