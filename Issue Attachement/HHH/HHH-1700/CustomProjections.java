package com.renxo.cms.dao.hibernate;

import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.type.Type;

public class CustomProjections {

	public static Projection groupByHaving(String column, Type type,
			String having) {

		String[] columns = new String[1];
		Type[] types = new Type[1];

		columns[0] = column;
		types[0] = type;

		return groupByHaving(columns, null, types, having);
	}

	public static Projection groupByHaving(String column, String alias,
			Type type, String having) {

		String[] columns = new String[1];
		String[] aliases = new String[1];
		Type[] types = new Type[1];

		columns[0] = column;
		aliases[0] = alias;
		types[0] = type;

		return groupByHaving(columns, aliases, types, having);
	}

	public static Projection groupByHaving(String[] columns, Type[] types,
			String having) {

		return groupByHaving(columns, null, types, having);
	}

	public static Projection groupByHaving(String[] columns, String[] aliases,
			Type[] types, String having) {

		if (aliases != null && columns.length != aliases.length) {
			return null;
		}
		if (columns.length != types.length) {
			return null;
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < columns.length; i++) {
			sb.append(columns[i]);
			if (aliases != null) {
				sb.append(" as " + aliases[i]);
			}
			if (i < columns.length - 1) {
				sb.append(",");
			}
		}

		return Projections.sqlGroupProjection(sb.toString(), sb.toString()
				+ " having " + having, aliases, types);
	}
}
