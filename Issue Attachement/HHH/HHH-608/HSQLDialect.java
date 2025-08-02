diff -Naur old/HSQLDialect.java new/HSQLDialect.java
--- old/HSQLDialect.java	2005-08-03 18:04:59.000000000 -0400
+++ new/HSQLDialect.java	2005-08-10 21:12:56.000000000 -0400
@@ -202,8 +202,21 @@
 		return true;
 	}
 
+    /**
+     * This will not work at all for HSQLDB v. 1.7.1 and earlier, because
+     * there was no system_sequences table.
+     */
 	public String getQuerySequencesString() {
-		return "select sequence_name from system_sequences";
+        try {
+            // Does present HSQLDB Database class support schemas?
+            Class.forName("org.hsqldb.Database", false,
+                    HSQLDialect.class.getClassLoader()).
+                    getDeclaredField("schemaManager");
+            return "select sequence_name from "
+                + "information_schema.system_sequences";
+        } catch (Throwable t) {
+            return "select sequence_name from system_sequences";
+        }
 	}
 
 	/**
