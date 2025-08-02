package com.assaf.core.generation;

import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;

import java.util.List;

import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;

public class AssafReverseEngineeringStrategy extends
		DelegatingReverseEngineeringStrategy {

	public AssafReverseEngineeringStrategy(ReverseEngineeringStrategy delegate) {
		super(delegate);
	}

	@Override
	public String foreignKeyToEntityName(String keyname,
			TableIdentifier fromTable, List fromColumnNames,
			TableIdentifier referencedTable, List referencedColumnNames,
			boolean uniqueReference) {
		System.out.println("fromTable: " + fromTable + " referencedTable " + referencedTable);
		return super.foreignKeyToEntityName(keyname, fromTable,
				fromColumnNames, referencedTable, referencedColumnNames,
				uniqueReference);
	}
}