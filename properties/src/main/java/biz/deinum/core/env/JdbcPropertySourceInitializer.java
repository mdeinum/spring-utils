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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

/**
 * {@code ApplicationContextInitializer} which (optionally) adds a {@code PropertiesPropertySource} containing properties
 * from a database table. The used {@code DataSource} can be either registered in JNDI or configured locally.
 *
 * <pre>
 *     config.jdbc.jndi-name : JNDI used to lookup the datasource
 *     config.jdbc.url : The JDBC Url to use for a local datasource
 *     config.jdbc.username : The username to use for a local datasource
 *     config.jdbc.password : The password to user for a local datasource
 *     config.jdbc.query : The query to execute
 * </pre>
 *
 * @author Marten Deinum
 */
public class JdbcPropertySourceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final Logger logger = LoggerFactory.getLogger(JdbcPropertySourceInitializer.class);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment env = applicationContext.getEnvironment();
        DataSource dataSource = getDataSource(env);
        if (dataSource != null) {
            logger.info("Initializing JDBC properties.");
            Properties jdbcProperties = new JdbcPropertiesLoader(env, dataSource).load();
            env.getPropertySources().addLast(new PropertiesPropertySource("jdbc-properties", jdbcProperties));
        } else {
            logger.info("Skipping initializing JDBC properties, no 'config.jdbc.*` properties detected.");
        }
    }

    protected DataSource getDataSource(Environment env) {
        if (env.containsProperty("config.jdbc.jndi-name")) {
            return new JndiDataSourceLookup().getDataSource(env.getProperty("config.jdbc.jndi-name"));
        } else if (env.containsProperty("config.jdbc.url")){
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setUrl(env.getProperty("config.jdbc.url"));
            dataSource.setUsername(env.getProperty("config.jdbc.username"));
            dataSource.setPassword(env.getProperty("config.jdbc.password"));
            return dataSource;
        }
        return null;
    }

    private static class JdbcPropertiesLoader {

        private static final String DEFAULT_QUERY = "select NAME, VALUE from CONFIGURATION";

        private final String query;
        private final JdbcTemplate jdbc;

        private JdbcPropertiesLoader(Environment env, DataSource dataSource) {
            this.query = env.resolveRequiredPlaceholders(env.getProperty("config.jdbc.query", DEFAULT_QUERY));
            this.jdbc= new JdbcTemplate(dataSource);
        }

        public Properties load() {
            final Properties props = new Properties();
            jdbc.query(query, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    String value = rs.getString(2);
                    props.setProperty(rs.getString(1), value != null ? value : "");
                }
            });
            return props;
        }

    }


}
