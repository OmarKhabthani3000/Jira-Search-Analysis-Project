1.  Test class created an placed in project/entitymanager test source code

2.  project/parent/pom.xml extended with the following profile for test database connection

<!-- The SQLServer2008 (MS JDBC) test envionment -->
<profile>
    <id>mssql2008-noel</id>
    <dependencies>
	<dependency>
	    <groupId>com.microsoft.sqlserver</groupId>
	    <artifactId>msjdbc</artifactId>
	    <version>3.0.1301.101</version>
	    <scope>system</scope>
	    <systemPath>C:/development/libraries/sqljdbc_3.0/enu/sqljdbc4.jar</systemPath>
	</dependency>
    </dependencies>
    <properties>
	<db.dialect>org.hibernate.dialect.SQLServer2005Dialect</db.dialect>
	<jdbc.driver>com.microsoft.sqlserver.jdbc.SQLServerDriver</jdbc.driver>
	<jdbc.url>jdbc:sqlserver://localhost:1434;databaseName=avinodeintegrationdb;SelectMethod=cursor</jdbc.url>
	<jdbc.user>sa</jdbc.user>
	<jdbc.pass>PASSWORD</jdbc.pass>
	<jdbc.isolation>2</jdbc.isolation> <!-- read committed -->
    </properties>
</profile>   

3.  In project/entitymanager, execute the following from the command line.

mvn test -Dtest=org.hibernate.ejb.test.query.MSSQLPagingTest -P mssql2008-noel