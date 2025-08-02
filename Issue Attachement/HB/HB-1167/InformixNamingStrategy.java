package net.sf.hibernate.cfg;

import net.sf.hibernate.util.StringHelper;

/**
 * This is a slight adjustment to the Hibernate built-in
 * ImprovedNamingStrategy to fix some problems with the
 * Hibernate unit tests running on Informix.
 *
 * The main
 * problem is just that the unit tests are hardcoded all over
 * the place to use specific table and column names, so it is
 * hard to switch NamingStrategy.  However, we have to switch
 * NamingStrategy, since the test mappings also use a lot of
 * names that are not quoted but contain mixed case, and Informix
 * converts all these to lower case which of course makes the
 * tests fail.  So the solution is to use this NamingStrategy.
 * It extends the ImprovedNamingStrategy, which helpfully
 * uses embedded underscores instead of mixed case names (thus
 * solving a lot of Informix issues).  The change here is just
 * to use unqualified property names for column names, to match
 * what the unit tests are expecting.
 *
 * @author Marko Balabanovic, Last Minute Network Ltd (lastminute.com)
 */
public class InformixNamingStrategy extends ImprovedNamingStrategy {

	/**
	 * The singleton instance
	 */
	public static final NamingStrategy INSTANCE = new InformixNamingStrategy();


	/**
	 * Return the unqualified property name, mixed
	 * case converted to underscores
	 */
	public String propertyToColumnName(String propertyName) {
        return addUnderscores(StringHelper.unqualify(propertyName));
	}


    /**
     * This is just a copy of the method in ImprovedNamingStrategy
     * (which is private, should be protected)
     *
     */
	private String addUnderscores(String name) {
		StringBuffer buf = new StringBuffer( name.replace('.', '_') );
		for (int i=1; i<buf.length()-1; i++) {
			if (
				'_'!=buf.charAt(i-1) &&
				Character.isUpperCase( buf.charAt(i) ) &&
				!Character.isUpperCase( buf.charAt(i+1) )
			) {
				buf.insert(i++, '_');
			}
		}
		return buf.toString().toLowerCase();
	}

}


