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

package biz.deinum.core.env;

import java.sql.Connection;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

/**
 * @author Marten Deinum
 */
public class JdbcPropertySourceInitializerTest {

    private static final Logger logger = LoggerFactory.getLogger(JdbcPropertySourceInitializerTest.class);

    private static String JDBC_URL = "jdbc:hsqldb:mem:config_db";

    private static DriverManagerDataSource dataSource;

    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @BeforeClass
    public static void createDatabase() throws Exception {
        dataSource = new DriverManagerDataSource();
        dataSource.setUrl(JDBC_URL);
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        destroyDatabase();

        Connection conn = dataSource.getConnection();
        ScriptUtils.executeSqlScript(conn, new ClassPathResource("init.sql"));
        JdbcUtils.closeConnection(conn);

    }

    @AfterClass
    public static void destroyDatabase() throws Exception {
        if (dataSource != null) {
            Connection conn = dataSource.getConnection();
            conn.createStatement().execute("SHUTDOWN");
            JdbcUtils.closeConnection(conn);
        }
    }

    @After
    public void cleanup() {
        System.clearProperty("config.jdbc.url");
        System.clearProperty("config.jdbc.username");
        System.clearProperty("config.jdbc.password");
        System.clearProperty("config.jdbc.query");
        System.clearProperty("config.jdbc.jndi-name");
    }

    @Test
    public void shouldNotRegisterJdbcPropertiesWhenPropertiesAreNotSet() throws Exception {
        GenericApplicationContext context = new GenericApplicationContext();

        JdbcPropertySourceInitializer initializer = new JdbcPropertySourceInitializer();
        initializer.initialize(context);

        PropertySource jdbcProperties = context.getEnvironment().getPropertySources().get("jdbc-properties");
        softly.assertThat(jdbcProperties).isNull();
    }

    @Test
    public void shouldUseLocalDatabaseForLookup() {

        System.setProperty("config.jdbc.url", JDBC_URL);
        System.setProperty("config.jdbc.username", "sa");
        System.setProperty("config.jdbc.password", "");

        GenericApplicationContext context = new GenericApplicationContext();

        JdbcPropertySourceInitializer initializer = new JdbcPropertySourceInitializer();
        initializer.initialize(context);

        PropertySource jdbcProperties = context.getEnvironment().getPropertySources().get("jdbc-properties");
        softly.assertThat(jdbcProperties).isNotNull();
        softly.assertThat(jdbcProperties.getProperty("FOO")).isEqualTo("BAR");
        softly.assertThat(jdbcProperties.getProperty("TEST")).isEqualTo("TEST");

    }

    @Test
    public void shouldUseJNDIDatabaseForLookup() throws Exception {
        SimpleNamingContextBuilder contextBuilder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        contextBuilder.bind("jdbc/ds", dataSource);

        System.setProperty("config.jdbc.jndi-name", "jdbc/ds");

        GenericApplicationContext context = new GenericApplicationContext();

        JdbcPropertySourceInitializer initializer = new JdbcPropertySourceInitializer();
        initializer.initialize(context);

        PropertySource jdbcProperties = context.getEnvironment().getPropertySources().get("jdbc-properties");
        softly.assertThat(jdbcProperties).isNotNull();
        softly.assertThat(jdbcProperties.getProperty("FOO")).isEqualTo("BAR");
        softly.assertThat(jdbcProperties.getProperty("TEST")).isEqualTo("TEST");

    }

    @Test
    public void shouldReadFromCustomTableWhenUsingModifiedQuery() {

        System.setProperty("config.jdbc.url", JDBC_URL);
        System.setProperty("config.jdbc.username", "sa");
        System.setProperty("config.jdbc.password", "");
        System.setProperty("config.jdbc.query", "select k,v from custom_config");

        GenericApplicationContext context = new GenericApplicationContext();

        JdbcPropertySourceInitializer initializer = new JdbcPropertySourceInitializer();
        initializer.initialize(context);

        PropertySource jdbcProperties = context.getEnvironment().getPropertySources().get("jdbc-properties");
        softly.assertThat(jdbcProperties).isNotNull();
        softly.assertThat(jdbcProperties.getProperty("BAR")).isEqualTo("FOO");
        softly.assertThat(jdbcProperties.getProperty("test")).isEqualTo("testing123");

    }


}