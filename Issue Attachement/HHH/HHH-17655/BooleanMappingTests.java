/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.orm.test.mapping.type.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Types;
import java.util.List;

import org.hibernate.annotations.ConverterRegistration;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.mapping.internal.BasicAttributeMapping;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.util.ServiceRegistryUtil;
import org.hibernate.tool.schema.internal.SchemaCreatorImpl;
import org.hibernate.type.NumericBooleanConverter;
import org.junit.jupiter.api.Test;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

public class BooleanMappingTests {

	@Test
	public void basicAssertions() {
		StandardServiceRegistry ssr = ServiceRegistryUtil.serviceRegistryBuilder()
				.applySetting( AvailableSettings.PREFERRED_BOOLEAN_JDBC_TYPE, "SMALLINT" )
				.build();
		
		try {
			MetadataImplementor metadata = (MetadataImplementor) new MetadataSources( ssr )
					.addAnnotatedClass( BooleanMappingTestEntity.class )
					.buildMetadata();
			metadata.validate();

			
			SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor) metadata.getSessionFactoryBuilder().build();
			final EntityPersister entityDescriptor = sessionFactory.getMappingMetamodel().getEntityDescriptor(BooleanMappingTestEntity.class );
			final BasicAttributeMapping testAttribute = (BasicAttributeMapping) entityDescriptor.findAttributeMapping( "test" );
			assertThat(testAttribute.getJdbcMapping().getJdbcType().getJdbcTypeCode() ).isEqualTo( Types.SMALLINT );
			assertThat(testAttribute.getJdbcMapping().getJavaTypeDescriptor().getJavaTypeClass() ).isEqualTo( Boolean.class );
			 

			
			boolean foundCorectCreate = true;

			List<String> commands = new SchemaCreatorImpl( ssr ).generateCreationCommands( metadata, false );
			for ( String command : commands ) {
				if ( !command.contains("smallint") ) {
					foundCorectCreate = false;
				}
				System.out.println(command);
			}
			assertTrue( foundCorectCreate, "Expected create table command for BooleanMappingTestEntity not found");
		}
		finally {
			StandardServiceRegistryBuilder.destroy( ssr );
		}
	}
	
	

	@ConverterRegistration(converter = NumericBooleanConverter.class)
	@Entity( name = "BooleanMappingTestEntity" )
	@Table( name = "boolean_map_test_entity" )
	public static class BooleanMappingTestEntity {
		@Id
		private Integer id;
		
		@Column
		@NotNull
		private Boolean test;

		public BooleanMappingTestEntity() {
		}

		public BooleanMappingTestEntity(Integer id, Boolean test) {
			this.id = id;
			this.test = test;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Boolean getTest() {
			return test;
		}

		public void setTest(Boolean test) {
			this.test = test;
		}
	}
}
