/*
 * Copyright 2001-2005 Fizteh-Center Lab., MIPT, Russia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created on 15.06.2005
 */
package ru.arptek.arpsite.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.XADataSource;

import org.enhydra.jdbc.pool.StandardXAPoolDataSource;
import org.enhydra.jdbc.standard.StandardXADataSource;
import org.hibernate.HibernateException;
import org.hibernate.connection.ConnectionProvider;

public class XAPoolConnectionFactory implements ConnectionProvider {

    public final static String DRIVER = "hibernate.arpsite.driver";

    public final static String MAX_CONNECTIONS = "hibernate.arpsite.max_connections";

    public final static String URL = "hibernate.arpsite.url";

    private StandardXAPoolDataSource dataSource;

    private int maxConnections = 10;

    public void close() throws HibernateException {
        dataSource.shutdown(false);
    }

    public void closeConnection(Connection conn) throws SQLException {
        conn.close();
    }

    public void configure(Properties properties) throws HibernateException {
        final String driver = properties.getProperty(DRIVER);
        final String url = properties.getProperty(URL);
        try {
            this.maxConnections = Integer.parseInt(properties
                    .getProperty(MAX_CONNECTIONS));
        } catch (NumberFormatException exc) {
            maxConnections = 10;
        }

        // XADataSource xaDataSource;
        // try {
        // xaDataSource = (XADataSource) Class.forName(
        // "org.apache.derby.jdbc.EmbeddedXADataSource").newInstance();
        // } catch (Exception exc) {
        // throw new HibernateException(exc);
        // }

        XADataSource xaDataSource = new StandardXADataSource();
        ((StandardXADataSource) xaDataSource).setDeadLockMaxWait(10000);
        ((StandardXADataSource) xaDataSource).setMaxCon(maxConnections);
        ((StandardXADataSource) xaDataSource).setDriverName(driver);
        ((StandardXADataSource) xaDataSource).setUrl(url);
        ((StandardXADataSource) xaDataSource)
                .setTransactionManager(JOTMTransactionFactory
                        .getTransactionManager());

        dataSource = new StandardXAPoolDataSource();
        try {
            dataSource.setMaxSize(maxConnections);
            dataSource.setDataSource(xaDataSource);
            dataSource.setTransactionManager(JOTMTransactionFactory
                    .getTransactionManager());
        } catch (Exception exc) {
            throw new RuntimeException("Could not initialize XADataSource", exc);
        }

    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public boolean supportsAggressiveRelease() {
        return true;
    }

}
