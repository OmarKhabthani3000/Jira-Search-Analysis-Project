package net.sf.hibernate.cfg;

import com.crossdb.sql.*;
import net.sf.hibernate.impl.SessionFactoryImpl;
import net.sf.hibernate.mapping.PersistentClass;
import net.sf.hibernate.mapping.Property;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.*;

/**
 *
 * @author Travis Reeder - travis@spaceprogram.com
 * Date: Mar 26, 2003
 * Time: 12:40:13 AM
 * @version 0.1
 */
public class DbBuilder {

    private static Log logger = LogFactory.getLog(Configuration.class);

    private static Hashtable crossdbMapping = null;
    private static int DEFAULT_VARCHAR_SIZE = 250;

    static {
        crossdbMapping = new Hashtable();

        // supported by crossdb
        crossdbMapping.put("hsqldb", "com.spaceprogram.sql.hsqldb.HsqldbFactory");
        crossdbMapping.put("mssqlserver", "com.thinkvirtual.sql.sqlserver.SQLServerFactory");
        crossdbMapping.put("mysql", "com.spaceprogram.sql.mysql.MySQLFactory");
        crossdbMapping.put("oracle", "com.thinkvirtual.sql.oracle.OracleSQLFactory");
        crossdbMapping.put("sybase", "com.thinkvirtual.sql.sybase.SybaseFactory");

    }

    /**
     *
     */
    public static String getCrossdbDriverFromPlatform(String platform) {
        // going to use the url property because doesn't seem to be a property stating which db is being used.
        String driverCls = null;
        String lcPlatform = platform.toLowerCase();
        // go through list of drivers and see if platform contains the name
        Enumeration enum = crossdbMapping.keys();
        while (enum.hasMoreElements()) {
            String s = (String) enum.nextElement();
            if (lcPlatform.indexOf(s) != -1) {
                // found
                driverCls = (String) crossdbMapping.get(s);
            }
        }

        //driverCls = (String) platform2driver.get(lcPlatform);
        if (driverCls == null) {
            logger.error("Platform '" + platform + "' not supported by crossdb. Autobuild cancelled.");

        }
        return driverCls;
    }

    public static void autoBuild(Configuration cfg, SessionFactoryImpl sessImpl) {
        logger.info("Attempting db autobuild.");
        Iterator iter;

        // get crossdb driver for chosen platform

        String crossdbDriver =
                getCrossdbDriverFromPlatform(cfg.getProperty(Environment.URL));
        
        if (crossdbDriver == null) {
            logger.error("Could not build db, crossdbDriver is null");
            // cancel this
            return;
        }
        Connection connection = null;
        try {
            connection = sessImpl.openConnection();

            Class factory_class = Class.forName(crossdbDriver);
            // put the implementation string into forName()
            SQLFactory factory = (SQLFactory) (factory_class.newInstance());

            List tableNames = new ArrayList();
            DatabaseMetaData dmd = connection.getMetaData();

            String types[] = {"TABLE"};
            ResultSet rs = dmd.getTables(null, null, null, types);
            //System.out.println("TABLES IN DB:");

            while (rs.next()) {
                String tname = rs.getString("TABLE_NAME");
                //  System.out.println(tname);
                tableNames.add(tname);
            }
            rs.close();

            Statement st = connection.createStatement();

            iter = cfg.getClassMappings();
            while (iter.hasNext()) {
                PersistentClass model = (PersistentClass) iter.next();
                //Class persisterClass = model.getPersister();

                // check if table exists in db
                String tname = model.getTable().getName();
                //System.out.println(tname);
                if (exists(tableNames, tname)) {
                    // then check cols, see if they exist
                    List colsInDb = new ArrayList();
                    SelectQuery sq = factory.getSelectQuery();
                    sq.addTable(tname);

                    ResultSet rs2 = sq.execute(st); //q.executeQuery("SELECT * FROM " + tname);
                    ResultSetMetaData rsmd = rs2.getMetaData();
                    int numberOfColumns = rsmd.getColumnCount();

                    for (int i = 1; i <= numberOfColumns; i++) {
                        colsInDb.add(rsmd.getColumnName(i));

                    }
                    rs2.close();

                    // compare columns in db to fields in model
                    Iterator iter2 = model.getTable().getColumnIterator();
                    while (iter2.hasNext()) {
                        net.sf.hibernate.mapping.Column column = (net.sf.hibernate.mapping.Column) iter2.next();

                        String colname = column.getName();
                        if (!exists(colsInDb, colname)) {
                            // alter table to add column
                            logger.info(
                                    "Altering table [" + tname + "] - adding column [" + colname + "]");
                            AlterTableQuery altq = factory.getAlterTableQuery();
                            altq.setTable(tname);

                            // todo: is this right to get the java.sql.Types type??
                            // todo: should we check for auto incs on altering table or assume it's already there?
                            Column col = new Column(colname, column.getType().sqlTypes(cfg)[column.getTypeIndex()]);
                            fixColumn(col, column);
                            altq.addColumn(col);
                            altq.execute(st);
                        }
                    }
                }
                else {
                    // table does not exist, so make it
                    logger.info("Creating table [" + tname + "] using " + factory.getClass().getName());

                    CreateTableQuery ctq = factory.getCreateTableQuery();
                    ctq.setName(tname);
                    net.sf.hibernate.mapping.Table hibTable = model.getTable();
                    // we'll get the name of the id column before hand
                    // todo: must be a better (more efficient) way to do this

                    Property idProp = model.getIdentifierProperty();
                    String idColumnName = null;

                    if (idProp != null) {
                        Iterator idIter = idProp.getColumnIterator();
                        while (idIter.hasNext()) {
                            net.sf.hibernate.mapping.Column column = (net.sf.hibernate.mapping.Column) idIter.next();
                            idColumnName = column.getName();
                        }
                    }

                    Iterator iter2 = hibTable.getColumnIterator();
                    while (iter2.hasNext()) {
                        net.sf.hibernate.mapping.Column column = (net.sf.hibernate.mapping.Column) iter2.next();
                        String colname = column.getName();
                        Column col = new Column(colname, column.getType().sqlTypes(cfg)[column.getTypeIndex()]);
                        //System.out.println("coltype: " + column.getType().sqlTypes(cfg)[column.getTypeIndex()] + " - " + column.getType().getName());
                        // check if auto inc

                        if (colname.equalsIgnoreCase(idColumnName)) {

                            col.setAutoIncrement(true);
                            col.setNullable(0);
                        }

                        fixColumn(col, column);


                        ctq.addColumn(col);
                    }
                     logger.debug(ctq.toString());
                    ctq.execute(st);
                    //System.out.println(ctq);
                    tableNames.add(tname);
                }
            }


        }
        catch (Exception e) {
            logger.error("", e);
        }
        finally {
            try {
                if (connection != null) connection.close();
            }
            catch (SQLException ignore) {
            }
        }
    }

    private static boolean exists(List tableNames, String tname) {
        for (int i = 0; i < tableNames.size(); i++) {
            String s = (String) tableNames.get(i);
            if (s.equalsIgnoreCase(tname)) {
                //System.out.println("table exists: " + tname + " - " + s);
                return true;
            }

        }
        return false;
    }

    private static void fixColumn(Column col, net.sf.hibernate.mapping.Column colHib) {
        if (col.getType() == java.sql.Types.VARCHAR) {
            if (colHib.getLength() > 0) {
                col.setSize(colHib.getLength());
            }
            else {
                // default to 250
                col.setSize(DEFAULT_VARCHAR_SIZE);
            }
        }
    }


}
