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

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.util.Assert;

import biz.deinum.security.core.session.AbstractSessionRegistry;

/**
 * @author Marten Deinum
 */
public class JdbcSessionRegistry extends AbstractSessionRegistry {

    private static final String SELECT_ALL_PRINCIPALS_QUERY =   "SELECT DISTINCT principal FROM SESSION_REGISTRY";
    private static final String SELECT_ALL_ACTIVE_SESSIONS =    "SELECT * FROM SESSION_REGISTRY where principal=? and expired=false";
    private static final String SELECT_ALL_SESSIONS =           "SELECT * FROM SESSION_REGISTRY where principal=?";
    private static final String SELECT_SESSION_INFORMATION =    "SELECT * FROM SESSION_REGISTRY WHERE sessionid=?";
    private static final String INSERT_SESSION_INFORMATION =    "INSERT INTO SESSION_REGISTRY(sessionid, principal, expired, lastrequest) VALUES (?,?,?,?)";
    private static final String UPDATE_SESSION_INFORMATION =    "UPDATE SESSION_REGISTRY SET expired=?, lastrequest=? WHERE sessionid=?";
    private static final String DELETE_SESSION_INFORMATION =    "DELETE FROM SESSION_REGISTRY WHERE sessionid=?";

    private final JdbcTemplate jdbcTemplate;
    
    public JdbcSessionRegistry(DataSource dataSource) {
        this(new JdbcTemplate(dataSource));
    }

    public JdbcSessionRegistry(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate=jdbcTemplate;
    }


    @Override
    public List<Object> getAllPrincipals() {
        List<Object> result = new ArrayList<>();
        result.addAll(this.jdbcTemplate.queryForList(SELECT_ALL_PRINCIPALS_QUERY, String.class));
        return result;
    }

    @Override
    public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
        if (includeExpiredSessions) {
            return this.jdbcTemplate.query(SELECT_ALL_SESSIONS, new SessionInformationRowMapper(), principal);
        } else {
            return this.jdbcTemplate.query(SELECT_ALL_ACTIVE_SESSIONS, new SessionInformationRowMapper(), principal);
        }
    }

    @Override
    public SessionInformation getSessionInformation(String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");
        List<SessionInformation> sessionInformation = this.jdbcTemplate.query(SELECT_SESSION_INFORMATION, new SessionInformationRowMapper(), sessionId);
        if (sessionInformation.size() == 1) {
            return sessionInformation.get(0);
        }
        return null;
    }

    @Override
    public void refreshLastRequest(String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");
        SessionInformation sessionInformation = getSessionInformation(sessionId);
        if (sessionInformation != null) {
            sessionInformation.refreshLastRequest();
            update(sessionInformation);
        }
    }

    @Override
    public void registerNewSession(String sessionId, Object principal) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");
        Assert.notNull(principal, "Principal required as per interface contract");

        logger.debug("Registering session {}, for principal {}", sessionId, principal);

        SessionInformation sessionInformation = new JdbcSessionInformation(principal, sessionId, new java.util.Date(), this);
        save(sessionInformation);
    }

    @Override
    public void removeSessionInformation(String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");

        this.jdbcTemplate.update(DELETE_SESSION_INFORMATION, sessionId);
    }

    void save(final SessionInformation sessionInformation) {
        this.jdbcTemplate.update(INSERT_SESSION_INFORMATION, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, sessionInformation.getSessionId());
                ps.setString(2, sessionInformation.getPrincipal().toString());
                ps.setBoolean(3, sessionInformation.isExpired());
                ps.setTimestamp(4, new Timestamp(sessionInformation.getLastRequest().getTime()));
            }
        });
    }

    void update(SessionInformation sessionInformation) {
        if (sessionInformation != null) {
            this.jdbcTemplate.update(UPDATE_SESSION_INFORMATION, sessionInformation.isExpired(), sessionInformation.getLastRequest(), sessionInformation.getSessionId());
        }
    }


    private final class SessionInformationRowMapper implements RowMapper<SessionInformation> {
        @Override
        public SessionInformation mapRow(ResultSet resultSet, int i) throws SQLException {
            String sessionId = resultSet.getString("sessionid");
            String principal = resultSet.getString("principal");
            boolean expired = resultSet.getBoolean("expired");
            Date lastRequest = new Date(resultSet.getTimestamp("lastrequest").getTime());
            JdbcSessionInformation si = new JdbcSessionInformation(principal, sessionId, lastRequest);
            if (expired) {
                si.expireNow();
            }
            si.setRegistry(JdbcSessionRegistry.this);
            return si;
        }
    }

    public static class JdbcSessionInformation extends SessionInformation {

        private transient JdbcSessionRegistry registry;

        public JdbcSessionInformation(Object principal, String sessionId, java.util.Date lastRequest) {
            this(principal, sessionId, lastRequest, null);
        }
        public JdbcSessionInformation(Object principal, String sessionId, java.util.Date lastRequest, JdbcSessionRegistry registry) {
            super(principal, sessionId, lastRequest);
            this.registry=registry;
        }

        void setRegistry(JdbcSessionRegistry registry) {
            this.registry = registry;
        }

        @Override
        public void expireNow() {
            super.expireNow();
            if (registry != null) {
                registry.update(this);
            }
        }
    }

}
