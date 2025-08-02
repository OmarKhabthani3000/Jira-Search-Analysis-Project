package br.com.gesplan.persistence.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class MyClassPhysicalNamingStrategy implements PhysicalNamingStrategy {
	
	public static final MyClassPhysicalNamingStrategy INSTANCE = new MyClassPhysicalNamingStrategy();
	
	private static List<String> ignoredWords = null;
	
	static {
		ignoredWords = new ArrayList<>();
		// Global custom attributes
		ignoredWords.add("alias");
		ignoredWords.add("status");
		ignoredWords.add("decimals");
		ignoredWords.add("concurrentdays");
		ignoredWords.add("basedays");
		// Financing custom attributes
		ignoredWords.add("workdays");
		// Report custom attributes
		ignoredWords.add("contentstatus");
		ignoredWords.add("civilstatus");
		ignoredWords.add("regstatus");
		// Integrator custom attributes
		ignoredWords.add("tablealias");
		ignoredWords.add("entrystatus");
	}
	
	@Override
	public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return name;
	}
	
	@Override
	public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return name;
	}
	
	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		final List<String> parts = splitAndReplace(name.getText());
		return toIdentifier(join(parts, false), jdbcEnvironment, true);
	}
	
	@Override
	public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		final LinkedList<String> parts = splitAndReplace(name.getText());
		if (!"seq".equalsIgnoreCase(parts.getLast())) {
			parts.add("seq");
		}
		return toIdentifier(join(parts), jdbcEnvironment, true);
	}
	
	@Override
	public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		final List<String> parts = splitAndReplace(name.getText());
		return toIdentifier(join(parts), jdbcEnvironment, true);
	}
	
	public static LinkedList<String> splitAndReplace(String name) {
		LinkedList<String> result = new LinkedList<>();
		for (String part : MyClassPhysicalNamingStrategy.splitByCharacterTypeCamelCase(name)) {
			if (part == null || part.trim().isEmpty()) {
				// skip null and space
				continue;
			}
			result.add(part.toLowerCase());
		}
		return result;
	}
	
	private static Collection<String> splitByCharacterTypeCamelCase(String name) {
		if (name.toUpperCase().endsWith("_KEY")) {
			name = name.substring(0, name.toUpperCase().lastIndexOf("_KEY"));
		}
		Collection<String> s = new LinkedList<>();
		if (name.equals(name.toUpperCase())) {
			s.add(name);
			return s;
		}
		char a;
		int lastIndex = 0;
		LinkedList<String> list = (LinkedList<String>) s;
		String item;
		for (int x = 0; x < name.length(); x++) {
			a = name.charAt(x);
			if (a == '_') {
				s.add(name.substring(lastIndex, x));
				lastIndex = x + 1;
			} else if (Character.isUpperCase(a)) {
				// Se for uma sequencia maiuscula, trata como uma coisa so
				if (!Character.isUpperCase(name.charAt(x - 1))) {
					item = name.substring(lastIndex, x);
					s.add(item);
					lastIndex = x;
					
					// Caso o ultimo caracter for maiusculo junta ele na String para não ficar sozinho
					if (x == name.length() - 1) {
						item = list.removeLast();
						item += a;
						list.add(item);
						lastIndex = x + 1;
					}
				}
			}
		}
		s.add(name.substring(lastIndex));
		return s;
	}
	
	public static String join(List<String> parts) {
		return MyClassPhysicalNamingStrategy.join(parts, true);
	}
	
	public static String join(List<String> parts, boolean cutPlural) {
		boolean firstPass = true;
		String separator = "";
		StringBuilder joined = new StringBuilder();
		String part;
		for (int x = 0; x < parts.size(); x++) {
			part = parts.get(x);
			if (cutPlural && x == parts.size() - 2 && parts.get(x + 1).toLowerCase().equals("id")) {
				joined.append(separator).append(validateName(part));
			} else {
				joined.append(separator).append(part);
			}
			if (firstPass) {
				firstPass = false;
				separator = "_";
			}
		}
		
		return joined.toString();
	}
	
	// FIXME: VALIDAR PLURAL apenas nas FK
	private static String validateName(String name) {
		String suffix = null;
		
		if (name == null) {
			return name;
		}
		
		if (name.toLowerCase().startsWith("id_")) {
			name = name.substring(3);
		}
		
		if (name.indexOf(".") > 0) {
			suffix = name.substring(name.indexOf(".") + 1);
			name = name.substring(0, name.indexOf("."));
		}
		
		if (name.toLowerCase().endsWith("_id")) {
			suffix = "_id" + ((suffix != null) ? "." + suffix : "");
			name = name.replaceAll("_id$", "");
			name = name.replaceAll("_ID$", "");
		}
		
		if (name.toLowerCase().endsWith("s") && !name.toLowerCase().endsWith("ss") && (ignoredWords == null || !ignoredWords.contains(name.toLowerCase()))) {
			if (name.toLowerCase().endsWith("ies")) {
				name = name.replaceAll("ies$", "y");
				name = name.replaceAll("IES$", "Y");
			} else {
				name = name.replaceAll("s$", "");
				name = name.replaceAll("S$", "");
			}
		}
		
		return name + ((suffix != null) ? suffix : "");
	}
	
	private Identifier toIdentifier(String stringForm, JdbcEnvironment jdbcEnvironment, boolean quoted) {
		return jdbcEnvironment.getIdentifierHelper().toIdentifier(stringForm.toUpperCase(), quoted);
	}
}
