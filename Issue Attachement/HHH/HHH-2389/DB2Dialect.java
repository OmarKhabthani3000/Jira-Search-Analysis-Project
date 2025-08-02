/**
 * @author Nicolas Billard
 *
 *Overrides DB2 dialect to limit number of results.
 */
public class DB2Dialect extends org.hibernate.dialect.DB2Dialect {

	public boolean supportsLimitOffset() {
		return false;
	}

	public boolean supportsVariableLimit() {
		return false;
	}
	
	public String getLimitString(String query, int offset, int limit) {
		
		if (offset > 0) {
			return
				"select * from (select rownumber() over () as rownumber, t.* from (" +
				query +
				" fetch first " + limit + " row only " +
				") as t)as t where rownumber > " + offset;
		}
		
		//
		return query + " fetch first " + limit + " row only ";

	}

	public static void main(String[] args) {
		System.out.println( new DB2Dialect().getLimitString("select * from essai e", -1, 30) );
		System.out.println( new DB2Dialect().getLimitString("select * from essai e", 20, 30) );
	}

}
