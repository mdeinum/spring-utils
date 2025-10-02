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

import javax.naming.InitialContext;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.jdbc.support.JdbcUtils;

/**
 * @author Marten Deinum
 */
@ExtendWith(SoftAssertionsExtension.class)
public class JdbcPropertySourceInitializerTest {

  private static final String JDBC_URL = "jdbc:hsqldb:mem:config_db";

  private static DriverManagerDataSource dataSource;

  @InjectSoftAssertions
  public SoftAssertions softly;

  @BeforeAll
  public static void createDatabase() throws Exception {
    dataSource = new DriverManagerDataSource();
    dataSource.setUrl(JDBC_URL);
    dataSource.setUsername("sa");
    dataSource.setPassword("");

    destroyDatabase();

    var conn = dataSource.getConnection();
    ScriptUtils.executeSqlScript(conn, new ClassPathResource("init.sql"));
    JdbcUtils.closeConnection(conn);

  }

  @AfterAll
  public static void destroyDatabase() throws Exception {
    if (dataSource != null) {
      var conn = dataSource.getConnection();
      conn.createStatement().execute("SHUTDOWN");
      JdbcUtils.closeConnection(conn);
    }
  }

  @AfterEach
  public void cleanup() {
    System.clearProperty("config.jdbc.url");
    System.clearProperty("config.jdbc.username");
    System.clearProperty("config.jdbc.password");
    System.clearProperty("config.jdbc.query");
    System.clearProperty("config.jdbc.jndi-name");
  }

  @Test
  public void shouldNotRegisterJdbcPropertiesWhenPropertiesAreNotSet() throws Exception {
    var context = new GenericApplicationContext();

    var initializer = new JdbcPropertySourceInitializer();
    initializer.initialize(context);

    var jdbcProperties = context.getEnvironment().getPropertySources().get("jdbc-properties");
    softly.assertThat(jdbcProperties).isNull();
  }

  @Test
  public void shouldUseLocalDatabaseForLookup() {

    System.setProperty("config.jdbc.url", JDBC_URL);
    System.setProperty("config.jdbc.username", "sa");
    System.setProperty("config.jdbc.password", "");

    var context = new GenericApplicationContext();

    var initializer = new JdbcPropertySourceInitializer();
    initializer.initialize(context);

    var jdbcProperties = context.getEnvironment().getPropertySources().get("jdbc-properties");
    softly.assertThat(jdbcProperties).isNotNull();
    softly.assertThat(jdbcProperties.getProperty("FOO")).isEqualTo("BAR");
    softly.assertThat(jdbcProperties.getProperty("TEST")).isEqualTo("TEST");

  }

  @Test
  @Disabled("Currently not working with Simple-JNDI")
  public void shouldUseJNDIDatabaseForLookup() throws Exception {
    new InitialContext().createSubcontext("jdbc").bind("ds", dataSource);
    System.out.println(new InitialContext());
    System.setProperty("config.jdbc.jndi-name", "jdbc/ds");

    var context = new GenericApplicationContext();

    var initializer = new JdbcPropertySourceInitializer();
    initializer.initialize(context);

    var jdbcProperties = context.getEnvironment().getPropertySources().get("jdbc-properties");
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