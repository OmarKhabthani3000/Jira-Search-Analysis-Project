package com.catlin.ac.p2p.util;

import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;

public class ExampleStrategy extends DelegatingReverseEngineeringStrategy {

    public ExampleStrategy(ReverseEngineeringStrategy delegate) {
        super(delegate);
    }

    public String columnToPropertyName(TableIdentifier table, String column) {
        if (column.endsWith("PK")) {
            return "id";
        } else {
            return super.columnToPropertyName(table, column);
        }
    }
}