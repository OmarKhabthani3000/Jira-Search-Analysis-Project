package com.xoricon.persistence.bo.multitenancy.test;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class SchemaBasedTenantResolver implements CurrentTenantIdentifierResolver  {

	public SchemaBasedTenantResolver() {
		System.out.println("SchemaBasedTenantResolver.<init>()");
	}
	
	@Override
	public String resolveCurrentTenantIdentifier() {
		System.out.println("SchemaBasedTenantResolver.resolveCurrentTenantIdentifier()");
		return "User1";
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return false;
	}

}
