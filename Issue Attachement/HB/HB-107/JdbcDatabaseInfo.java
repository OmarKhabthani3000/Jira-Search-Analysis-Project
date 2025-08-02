//$Id: JdbcDatabaseInfo.java,v 1.5 2003/03/21 12:03:43 oneovthafew Exp $
package net.sf.hibernate.tool.hbm2ddl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.dialect.Dialect;

public class JdbcDatabaseInfo
{
	private final Map _tables = new HashMap();
	private final Set _sequences = new HashSet();

	private DatabaseMetaData meta = null;
	
	public JdbcDatabaseInfo(Connection connection, Dialect dialect) throws SQLException
	{
		meta = connection.getMetaData();
		
		/*
		initTables(meta);
		initColumns(meta);
		initForeignKeys(meta);
		initIndexes(meta);
		*/
		initSequences(connection, dialect);
	}
	
	public JdbcTableInfo getTableInfo(String name) throws HibernateException
	{
		JdbcTableInfo tableInfo = null;
		
		if (name != null)
		{
			tableInfo = (JdbcTableInfo)_tables.get(name.toUpperCase());
			
			if (tableInfo == null)
			{
				String[] types = {"TABLE"};
				ResultSet rs = null;
		
				try
				{
					try 
					{
						rs = meta.getTables(null, "%", name.toUpperCase(), types);
				
						while (rs.next())
						{
							if (name.equalsIgnoreCase(rs.getString("TABLE_NAME")))
							{
								tableInfo = new JdbcTableInfo(rs);
								
								initColumns(tableInfo);
								tableInfo.initForeignKeys(meta);
								tableInfo.initIndexes(meta);
								
								_tables.put(name.toUpperCase(), tableInfo);
								break;
							}
						}
					}
					finally
					{
							if (rs != null) rs.close();
					}
				}
				catch(SQLException e)
				{
					throw new HibernateException(e);
				}
			}
		}
		
		return tableInfo;
	}
	
	private void initColumns(JdbcTableInfo tableInfo) throws SQLException
	{
		ResultSet rs = null;
		
		try 
		{
			rs = meta.getColumns(null, "%", tableInfo.getName(), "%");
			
			while (rs.next()) 
			{
				tableInfo.addColumn(rs);
			}
		} 
		finally 
		{
			if (rs != null) 
				rs.close();
		}
	}
	
	/*
	public JdbcTableInfo getTableInfo(String name)
	{
		return name != null ? (JdbcTableInfo)_tables.get(name.toUpperCase()) : null;
	}
	
	private void initTables(DatabaseMetaData meta) throws SQLException
	{
		ResultSet rs = null;
		
		try {
			String[] types = {"TABLE"};
			
			rs = meta.getTables(null, "%", "%", types);
			
			while (rs.next()) addTable(rs);
		} finally {
			if (rs != null) rs.close();
		}
	}
	
	private void addTable(ResultSet rs) throws SQLException
	{
		String name = rs.getString("TABLE_NAME");
		
		if (name == null) return;
		
		if (getTableInfo(name) == null) {
			JdbcTableInfo info = new JdbcTableInfo(rs);
			
			_tables.put(info.getName().toUpperCase(), info);
		}
	}
	
	private void initColumns(DatabaseMetaData meta) throws SQLException
	{
		ResultSet rs = null;
		
		try {
			rs = meta.getColumns(null, "%", "%", "%");
			
			while (rs.next()) {
				JdbcTableInfo info = getTableInfo(rs.getString("TABLE_NAME"));
				
				if (info == null) continue;
				
				info.addColumn(rs);
			}
		} finally {
			if (rs != null) rs.close();
		}
	}
	
	private void initForeignKeys(DatabaseMetaData meta) throws SQLException
	{
		Iterator iterator = _tables.values().iterator();
		
		while (iterator.hasNext()) {
			JdbcTableInfo info = (JdbcTableInfo)iterator.next();
			
			info.initForeignKeys(meta);
		}
	}
	
	private void initIndexes(DatabaseMetaData meta) throws SQLException
	{
		Iterator iterator = _tables.values().iterator();
		
		while (iterator.hasNext()) {
			JdbcTableInfo info = (JdbcTableInfo)iterator.next();
			
			info.initIndexes(meta);
		}
	}
	*/
	
	private void initSequences(Connection connection, Dialect dialect) throws SQLException
	{
		String sql = dialect.getQuerySequencesString();
		
		if (sql == null) return;
		
		Statement statement = null;
		ResultSet rs = null;
		
		try {
			statement = connection.createStatement();
			rs = statement.executeQuery(sql);
			
			while (rs.next()) _sequences.add(rs.getString(1).toUpperCase());
		} finally {
			if (rs != null) rs.close();
			if (statement != null) statement.close();
		}
	}
	
	public boolean isSequence(Object key)
	{
		return key instanceof String && _sequences.contains(((String)key).toUpperCase());
	}

	public boolean isTable(Object key) throws HibernateException
	{
		//return key instanceof String && _tables.containsKey(((String)key).toUpperCase());
		return key instanceof String && (getTableInfo((String)key) != null);
	}
	
}





