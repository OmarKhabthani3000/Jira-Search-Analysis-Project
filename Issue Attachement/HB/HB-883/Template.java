//$Id: Template.java,v 1.3.2.3 2003/11/29 06:52:55 oneovthafew Exp $
package net.sf.hibernate.sql;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.dialect.SQLFunction;
import net.sf.hibernate.util.StringHelper;

/**
 * Parses SQL fragments specified in mapping documents
 * 
 * @author Gavin King
 */
public final class Template {

	private static final java.util.Set KEYWORDS = new HashSet();
	static {
		KEYWORDS.add("and");
		KEYWORDS.add("or");
		KEYWORDS.add("not");
		KEYWORDS.add("like");
		KEYWORDS.add("is");
		KEYWORDS.add("in");
		KEYWORDS.add("between");
		KEYWORDS.add("null");
		KEYWORDS.add("select");
		KEYWORDS.add("from");
		KEYWORDS.add("join");
		KEYWORDS.add("inner");
		KEYWORDS.add("outer");
		KEYWORDS.add("on");
		KEYWORDS.add("where");
		KEYWORDS.add("having");
		KEYWORDS.add("group");
		KEYWORDS.add("order");
		KEYWORDS.add("by");
		KEYWORDS.add("case");
		KEYWORDS.add("when");
		KEYWORDS.add("else");
		KEYWORDS.add("end");		
	}
	
	public static final String TEMPLATE = "$PlaceHolder$";
	
	private Template() {}

	public static String renderWhereStringTemplate(String sqlWhereString, Dialect dialect) {
		// takes the where condition provided in the mapping
		// attribute and interpolates the alias
		//TODO: make this a bit nicer
		StringTokenizer tokens = new StringTokenizer(sqlWhereString, " =><!+-*/()',", true);
		StringBuffer result = new StringBuffer();
		boolean quoted = false;
		boolean afterFrom = false;
		boolean afterFromTable = false;
		boolean hasMore = tokens.hasMoreTokens();
		String nextToken = hasMore ? tokens.nextToken() : null;
		
		
		Map functions = dialect.getFunctions();
		Iterator it = dialect.getFunctions().keySet().iterator();
		String functionName;
		SQLFunction function;
		
		while (it.hasNext()) 
		{
			functionName = (String)it.next();
			function = (SQLFunction) functions.get(functionName);
			if (!function.hasParenthesesIfNoArguments()) KEYWORDS.add(functionName);
		}

		while (hasMore) {
			String token = nextToken;
			hasMore = tokens.hasMoreTokens();
			nextToken = hasMore ? tokens.nextToken() : null;
			if ( "'".equals(token) ) quoted = !quoted;
			if ( quoted || Character.isWhitespace( token.charAt(0) ) ) {
				result.append(token);
			}
			else {
				
				boolean isIdentifier = token.charAt(0)=='`' || ( //allow any identifier quoted with backtick
					Character.isLetter( token.charAt(0) ) && //only recognizes identifiers beginning with a letter 
					!KEYWORDS.contains( token.toLowerCase() ) &&
					token.indexOf('.') < 0
				);
								
				if (afterFrom) {
					result.append(token);
					afterFrom = false;
					afterFromTable = true;
				}
				else if (afterFromTable) {
					afterFromTable = false;
					result.append(token);
				}
				else if ( 
					isIdentifier && 
					( nextToken==null || !nextToken.equals("(") ) // not a function call 
				) {
					result.append(TEMPLATE)
						.append(StringHelper.DOT)
						.append( quote(token, dialect) );
				}
				else {
					if ( "from".equals( token.toLowerCase() ) ) afterFrom = true;
					result.append(token);
				}

			}
		}
		System.out.println(result.toString());
		return result.toString();
	}

	public static String renderOrderByStringTemplate(String sqlOrderByString, Dialect dialect) {
		// takes order by clause provided in the mapping
		// attribute and interpolates the alias
		//TODO: make this a bit nicer
		StringTokenizer tokens = new StringTokenizer(sqlOrderByString, ",");
		StringBuffer result = new StringBuffer();
		while ( tokens.hasMoreTokens() ) {
			String column = quote( tokens.nextToken().trim(), dialect );
			result.append(TEMPLATE)
				.append(StringHelper.DOT)
				.append(column);
			if ( tokens.hasMoreTokens() ) result.append(StringHelper.COMMA_SPACE);
		}
		return result.toString();
	}

	private static String quote(String column, Dialect dialect) {
		if ( column.charAt(0)=='`' ) {
			return dialect.openQuote() + column.substring(1, column.length()-1 ) + dialect.closeQuote();
		}
		else {
			return column;
		}
	}

}
