package com.example;

import java.io.File;
import java.util.Properties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.SQLServer2012Dialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaExport.Type;
import org.hibernate.tool.hbm2ddl.Target;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestNationalized {
	private static final Properties props = new Properties();

	@BeforeClass
	public static void init() {
		props.put(AvailableSettings.DIALECT, SQLServer2012Dialect.class.getName());
		props.put(AvailableSettings.USE_NATIONALIZED_CHARACTER_DATA, "true");
	}

	@Entity
	@Table(name = "user")
	class User {
		private Long id;
		private String name;

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		@Column(name = "name", length = 255)
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	@Test
	@TestForIssue(jiraKey = "HHH-10528")
	public void testDDL() {
		org.hibernate.boot.registry.StandardServiceRegistryBuilder registryBuilder = new org.hibernate.boot.registry.StandardServiceRegistryBuilder();
		registryBuilder.applySettings(props);
		org.hibernate.boot.MetadataSources metadataSources = new org.hibernate.boot.MetadataSources(
				registryBuilder.build());

		metadataSources.addAnnotatedClass(User.class);
		Metadata metadata = metadataSources.buildMetadata();
		String fileName = "script.sql";
		File f = new File("sql", fileName);
		f.getParentFile().mkdirs();
		SchemaExport se = new SchemaExport((MetadataImplementor) metadata);
		se.setFormat(true);
		se.setOutputFile(f.getAbsolutePath());
		se.execute(Target.SCRIPT, Type.CREATE);
	}
}
