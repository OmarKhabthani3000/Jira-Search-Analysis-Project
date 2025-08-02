package org.hibernate.envers.test.integration.reventity.trackmodifiedentities;

import org.hibernate.cfg.Environment;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.entities.StrTestEntity;
import org.hibernate.mapping.Table;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
@RequiresDialect({H2Dialect.class})
@TestForIssue(jiraKey = "HHH-7441")
public class RevChangesSchemaTest extends BaseEnversJPAFunctionalTestCase {
    private static final String SCHEMA_NAME = "ENVERS_AUDIT";
    
    @Override
	public void addConfigOptions(Map configuration) {
		super.addConfigOptions( configuration );
        // Creates new schema after establishing connection
        configuration.putAll(Environment.getProperties());
        configuration.put(Environment.URL, configuration.get(Environment.URL) + ";INIT=CREATE SCHEMA IF NOT EXISTS " + SCHEMA_NAME);
        configuration.put("org.hibernate.envers.default_schema", SCHEMA_NAME);
        configuration.put("org.hibernate.envers.track_entities_changed_in_revision", "true");
	}

    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class[]{ StrTestEntity.class };
    }

    @Test
    public void testRevChangesSchemaName() {
        Iterator<Table> tableIterator = getCfg().getTableMappings();
        while (tableIterator.hasNext()) {
            Table table = tableIterator.next();
            if ("REVCHANGES".equals(table.getName())) {
                Assert.assertEquals(SCHEMA_NAME, table.getSchema());
                return;
            }
        }
        Assert.fail();
    }
}
