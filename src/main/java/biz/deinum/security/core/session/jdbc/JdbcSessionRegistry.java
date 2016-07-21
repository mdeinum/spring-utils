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
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;

/**
 * Created with IntelliJ IDEA.
 * User: in329dei
 * Date: 11-12-13
 * Time: 8:36
 * To change this template use File | Settings | File Templates.
 */
public class JdbcSessionRegistry extends JdbcDaoSupport implements SessionRegistry {

    private static final String SELECT_ALL_PRINCIPALS_QUERY =   "SELECT DISTINCT principal FROM SESSION_REGISTRY";
    private static final String SELECT_ALL_ACTIVE_SESSIONS =    "SELECT * FROM SESSION_REGISTRY where principal=? and expired=false";
    private static final String SELECT_ALL_SESSIONS =           "SELECT * FROM SESSION_REGISTRY where principal=?";
    private static final String SELECT_SESSION_INFORMATION =    "SELECT * FROM SESSION_REGISTRY WHERE sessionid=?";
    private static final String INSERT_SESSION_INFORMATION =    "INSERT INTO SESSION_REGISTRY(sessionid, principal, expired, lastrequest) VALUES (?,?,?,?)";
    private static final String UPDATE_SESSION_INFORMATION =    "UPDATE SESSION_REGISTRY SET expired=?, lastrequest=? WHERE sessionid=?";
    private static final String DELETE_SESSION_INFORMATION =    "DELETE FROM SESSION_REGISTRY WHERE sessionid=?";

    @Override
    public List<Object> getAllPrincipals() {
//        return (List<Object>) getJdbcTemplate().queryForList(SELECT_ALL_PRINCIPALS_QUERY, String.class);
        return null;
    }

    @Override
    public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
        if (includeExpiredSessions) {
            return getJdbcTemplate().query(SELECT_ALL_SESSIONS, new SessionInformationRowMapper(), principal);
        } else {
            return getJdbcTemplate().query(SELECT_ALL_ACTIVE_SESSIONS, new SessionInformationRowMapper(), principal);
        }
    }

    @Override
    public SessionInformation getSessionInformation(String sessionId) {
        return getJdbcTemplate().queryForObject(SELECT_SESSION_INFORMATION, new SessionInformationRowMapper(), sessionId);
    }

    @Override
    public void refreshLastRequest(String sessionId) {
        SessionInformation sessionInformation = getSessionInformation(sessionId);
        if (sessionInformation != null) {
            sessionInformation.refreshLastRequest();
            getJdbcTemplate().update(UPDATE_SESSION_INFORMATION, sessionInformation.isExpired(), sessionInformation.getLastRequest(), sessionId);
        }
    }

    public void save(final SessionInformation sessionInformation) {
        getJdbcTemplate().update(INSERT_SESSION_INFORMATION, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, sessionInformation.getSessionId());
                ps.setString(2, sessionInformation.getPrincipal().toString());
                ps.setBoolean(3, sessionInformation.isExpired());
                ps.setTimestamp(4, new Timestamp(sessionInformation.getLastRequest().getTime()));
            }
        });
    }

    @Override
    public void registerNewSession(String sessionId, Object principal) {
        SessionInformation sessionInformation = new SessionInformation(principal, sessionId, new java.util.Date());
        save(sessionInformation);
    }

    @Override
    public void removeSessionInformation(String sessionId) {
        getJdbcTemplate().update(DELETE_SESSION_INFORMATION, sessionId);
    }

    private final class SessionInformationRowMapper implements RowMapper<SessionInformation> {
        @Override
        public SessionInformation mapRow(ResultSet resultSet, int i) throws SQLException {
            String sessionId = resultSet.getString("sessionid");
            String principal = resultSet.getString("principal");
            boolean expired = resultSet.getBoolean("expired");
            Date lastRequest = new Date(resultSet.getTimestamp("lastrequest").getTime());
            SessionInformation si = new SessionInformation(principal, sessionId, lastRequest);
            if (expired) {
                si.expireNow();
            }

            return si;
        }
    }

}
