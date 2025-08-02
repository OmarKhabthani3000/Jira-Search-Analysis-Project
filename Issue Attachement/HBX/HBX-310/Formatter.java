//$Id$
package org.hibernate.pretty;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;

import org.hibernate.util.StringHelper;

public class Formatter {
	
	private static final Set BEGIN_CLAUSES = new HashSet();
	private static final Set END_CLAUSES = new HashSet();
	private static final Set LOGICAL = new HashSet();
	static {
		
		BEGIN_CLAUSES.add("left");
		BEGIN_CLAUSES.add("right");
		BEGIN_CLAUSES.add("inner");
		BEGIN_CLAUSES.add("outer");
		BEGIN_CLAUSES.add("group");
		BEGIN_CLAUSES.add("order");

		END_CLAUSES.add("where");
		END_CLAUSES.add("having");
		END_CLAUSES.add("join");
		END_CLAUSES.add("from");
		END_CLAUSES.add("by");
		END_CLAUSES.add("join");
		
		LOGICAL.add("and");
		LOGICAL.add("or");
		LOGICAL.add("when");
		LOGICAL.add("else");
		LOGICAL.add("end");

	}
	
	String indentString = "    ";

	boolean beginLine = true;
	boolean afterBeginBeforeEnd = false;
	boolean afterByOrFromOrSelect = false;
	boolean afterOn = false;
	int inFunction = 0;
	int parensSinceSelect = 0;
	private LinkedList parenCounts = new LinkedList();

	int indent = 0;

	StringBuffer result = new StringBuffer();
	StringTokenizer tokens;
	String lastToken;
	String token;
	String lcToken;
	
	public Formatter(String sql) {
		tokens = new StringTokenizer(
				sql, 
				"()+*/-=<>'`\"[]," + StringHelper.WHITESPACE, 
				true
			);
	}

	public  String format() {
		
		while ( tokens.hasMoreTokens() ) {
			token = tokens.nextToken();
			lcToken = token.toLowerCase();
						
			if ( afterByOrFromOrSelect && ",".equals(token) ) {
				commaInCommaList();
			}
			else if ( afterOn && ",".equals(token) ) {
				commaAfterOn();
			}
			
			else if ( "(".equals(token) ) {
				openParen();
			}
			else if ( ")".equals(token) ) {
				closeParen();
			}

			else if ( BEGIN_CLAUSES.contains(lcToken) ) {
				beginNewClause();
			}

			else if ( END_CLAUSES.contains(lcToken) ) {
				endNewClause(); 
			}
			
			else if ( "select".equals(lcToken) ) {
				select();
			}
			
			else if ( "on".equals(lcToken) ) {
				on();
			}
			
			else if ( LOGICAL.contains(lcToken) ) {
				logical();
			}
			
			else if ( isWhitespace(token) ) {
				white();
			}
			
			else {
				misc();
			}
			
			if ( !isWhitespace( token ) ) lastToken = lcToken;
			
		}
		return result.toString();
	}

	private void commaAfterOn() {
		out();
		indent--;
		newline();
		afterOn = false;
		afterByOrFromOrSelect = true;
	}

	private void commaInCommaList() {
		out();
		newline();
	}

	private void logical() {
		if ( "end".equals(lcToken) ) indent--;
		newline();
		out();
		beginLine = false;
	}

	private void on() {
		indent++;
		afterOn = true;
		newline();
		out();
		beginLine = false;
	}

	private void misc() {
		out();
		beginLine = false;
		if ( "case".equals(lcToken) ) {
			indent++;
		}
	}

	private void white() {
		if ( !beginLine ) {
			result.append(" ");
		}
	}

	private void select() {
		out();
		indent++;
		newline();
		afterBeginBeforeEnd = false; 
		parenCounts.addLast( new Integer(parensSinceSelect) );
		parensSinceSelect = 0;
		afterByOrFromOrSelect = true;
	}

	private void out() {
		result.append(token);
	}

	private void endNewClause() {
		if (!afterBeginBeforeEnd) {
			indent--;
			if (afterOn) {
				indent--;
				afterOn=false;
			}
			newline();
		}
		out();
		indent++;
		newline();
		afterBeginBeforeEnd = false;
		afterByOrFromOrSelect = "by".equals(lcToken) 
				|| "from".equals(lcToken);
	}

	private void beginNewClause() {
		if (!afterBeginBeforeEnd) {
			if (afterOn) {
				indent--;
				afterOn=false;
			}
			indent--;
			newline();
		}
		out();
		beginLine = false;
		afterBeginBeforeEnd = true;
	}

	private void closeParen() {
		parensSinceSelect--;
		if (parensSinceSelect<0) {
			indent--;
			parensSinceSelect = ( (Integer) parenCounts.removeLast() ).intValue();
		}
		if ( inFunction>0 ) {
			inFunction--;
			out();
		}
		else {
			if (!afterByOrFromOrSelect) {
				indent--;
				newline();
			}
			out();
		}
		beginLine = false;
	}

	private void openParen() {
		if ( isFunctionName( lastToken ) || inFunction>0 ) {
			inFunction++;
		}
		beginLine = false;
		if ( inFunction>0 ) {
			out();
		}
		else {
			out();
			if (!afterByOrFromOrSelect) {
				indent++;
				newline();
				beginLine = true;
			}
		}
		parensSinceSelect++;
	}

	private static boolean isFunctionName(String lastToken) {
		return Character.isJavaIdentifierStart( lastToken.charAt(0) ) && 
				!LOGICAL.contains(lastToken) && 
				!END_CLAUSES.contains(lastToken);
	}

	private static boolean isWhitespace(String token) {
		return StringHelper.WHITESPACE.indexOf(token)>=0;
	}
	
	private void newline() {
		result.append("\n");
		for ( int i=0; i<indent; i++ ) {
			result.append(indentString);
		}
		beginLine = true;
	}

	public static void main(String[] args) {
		System.out.println( 
				new Formatter("select p.name, a.zipCode, count(*) from Person p left outer join Employee e on e.id = p.id and p.type = 'E' and (e.effective>? or e.effective<?) join Address a on a.pid = p.id where upper(p.name) like 'G%' and p.age > 100 and (p.sex = 'M' or p.sex = 'F') and coalesce( trim(a.street), a.city, (a.zip) ) is not null order by p.name asc, a.zipCode asc").format()
			);
		System.out.println();
		System.out.println();
		System.out.println( 
			new Formatter("select ( (m.age - p.age) * 12 ), trim(upper(p.name)) from Person p, Person m where p.mother = m.id and ( p.age = (select max(p0.age) from Person p0 where (p0.mother=m.id)) and p.name like ? )").format()
		);
		System.out.println();
		System.out.println();
		System.out.println( 
			new Formatter("select * from Address a join Person p on a.pid = p.id, Person m join Address b on b.pid = m.id where p.mother = m.id and p.name like ?").format()
		);
		System.out.println();
		System.out.println();
		System.out.println( 
			new Formatter("select case when p.age > 50 then 'old' when p.age > 18 then 'adult' else 'child' end from Person p where ( case when p.age > 50 then 'old' when p.age > 18 then 'adult' else 'child' end ) like ?").format()
		);
	}

}
