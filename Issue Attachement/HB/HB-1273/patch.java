Index: BatcherImpl.java 
=================================================================== 
RCS file: /cvsroot/hibernate/Hibernate2/src/net/sf/hibernate/impl/BatcherImpl.java,v 
retrieving revision 1.16 
diff -u -r1.16 BatcherImpl.java 
--- BatcherImpl.java 7 Aug 2004 14:05:39 -0000 1.16 
+++ BatcherImpl.java 19 Oct 2004 22:59:44 -0000 
@@ -7,6 +7,8 @@ 
import java.sql.SQLException; 
import java.util.HashSet; 
import java.util.Iterator; 
+import java.util.regex.Matcher; 
+import java.util.regex.Pattern; 

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory; 
@@ -224,9 +226,25 @@ 

private void log(String sql) { 
sqlLog.debug(sql); 
- if ( factory.isShowSqlEnabled() ) System.out.println("Hibernate: " + sql); 
+ if ( factory.isShowSqlEnabled() ) System.out.println("Hibernate: " + format(sql)); 
} 
- 
+ 
+ private String format(String sql) { 
+ String out = sql; 
+ Pattern p = Pattern.compile(" (left|right|)\\s*(inner|outer|)\\s*join "); 
+ Matcher matcher = p.matcher(out); 
+ if (matcher.find()) { 
+ out = matcher.replaceAll("\n" + matcher.group()); 
+ } 
+ p = Pattern.compile(" from "); 
+ matcher = p.matcher(out); 
+ out = matcher.replaceAll("\n from "); 
+ p = Pattern.compile(" where "); 
+ matcher = p.matcher(out); 
+ out = matcher.replaceAll("\n where "); 
+ return out; 
+ } 
+ 
private PreparedStatement getPreparedStatement(final Connection conn, final String sql, boolean scrollable) 
throws SQLException { 
return getPreparedStatement(conn, sql, scrollable, false);