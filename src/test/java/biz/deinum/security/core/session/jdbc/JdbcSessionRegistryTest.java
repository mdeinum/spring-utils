/*
 *  Copyright 2007-2016 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package biz.deinum.security.core.session.jdbc;

import java.sql.Connection;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.jdbc.support.JdbcUtils;

import biz.deinum.security.core.session.AbstractSessionRegistry;
import biz.deinum.security.core.session.BaseSessionRegistryTest;

/**
 * @author marten
 */
public class JdbcSessionRegistryTest extends BaseSessionRegistryTest {

    private static String JDBC_URL = "jdbc:hsqldb:mem:session_registry";

    private static DriverManagerDataSource dataSource;

    @BeforeClass
    public static void createDatabase() throws Exception {
        dataSource = new DriverManagerDataSource();
        dataSource.setUrl(JDBC_URL);
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        destroyDatabase();


    }

    @AfterClass
    public static void destroyDatabase() throws Exception {
        if (dataSource != null) {
            Connection conn = dataSource.getConnection();
            conn.createStatement().execute("SHUTDOWN");
            JdbcUtils.closeConnection(conn);
        }
    }

    @Before
    public void initDatabase() throws Exception {
        Connection conn = dataSource.getConnection();
        ScriptUtils.executeSqlScript(conn, new ClassPathResource("sql/session_registry_schema.sql"));
        JdbcUtils.closeConnection(conn);

    }

    @Override
    protected AbstractSessionRegistry createSessionRegistry() throws Exception {
        return new JdbcSessionRegistry(dataSource);
    }
}