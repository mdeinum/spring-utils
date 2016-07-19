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

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

/**
 * @author Marten Deinum
 */
public class JdbcPropertySourceInitializerTest {

    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();

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

        System.setProperty("config.jdbc.url", "jdbc:h2:mem:test_config_local;INIT=runscript from 'classpath:/init.sql'");
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

        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl("jdbc:h2:mem:test_config_jndi;INIT=runscript from 'classpath:/init.sql'");
        ds.setUsername("sa");
        ds.setPassword("");

        SimpleNamingContextBuilder contextBuilder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        contextBuilder.bind("jdbc/ds", ds);

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

        System.setProperty("config.jdbc.url", "jdbc:h2:mem:test_config_local;INIT=runscript from 'classpath:/init.sql'");
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