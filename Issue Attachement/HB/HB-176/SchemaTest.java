//$Id:$
package com;

import junit.framework.TestCase;
import net.sf.hibernate.mapping.Table;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.dialect.MySQLDialect;
import net.sf.hibernate.dialect.GenericDialect;

/**
 * Test for Dialect.getSeperator patch.
 * @author Chris Hane
 * Date: Aug 29, 2003
 */
public class SchemaTest extends TestCase  {

   public void testSchemaSeparator() throws Exception {
      Dialect mySQLDialect = new MySQLDialect();
      Dialect genericDialect = new GenericDialect();

      Table table = new Table();
      table.setName("theTableName");

      assertEquals("theTableName", table.getQualifiedName(genericDialect));
      assertEquals("theTableName", table.getQualifiedName(mySQLDialect));
      assertEquals("defSchema.theTableName", table.getQualifiedName(genericDialect, "defSchema"));
      assertEquals("defSchema_theTableName", table.getQualifiedName(mySQLDialect, "defSchema"));

      table.setSchema("schemaName");

      assertEquals("schemaName.theTableName", table.getQualifiedName(genericDialect));
      assertEquals("schemaName_theTableName", table.getQualifiedName(mySQLDialect));
      assertEquals("schemaName.theTableName", table.getQualifiedName(genericDialect, "defSchema"));
      assertEquals("schemaName_theTableName", table.getQualifiedName(mySQLDialect, "defSchema"));
   }

}
