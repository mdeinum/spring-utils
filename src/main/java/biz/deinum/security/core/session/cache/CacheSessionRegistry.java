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

package biz.deinum.security.core.session.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.util.Assert;

import biz.deinum.security.core.session.AbstractSessionRegistry;

/**
 * @author Marten Deinum
 */
public class CacheSessionRegistry extends AbstractSessionRegistry implements InitializingBean {

    private static final String DEFAULT_SESSIONIDS_CACHE_NAME = "sessionIds";
    private static final String DEFAULT_PRINCIPAL_CACHE_NAME = "principals";

    private final CacheManager cacheManager;
    private Cache principals;
    private Cache sessionIds;

    private String sessionIdCacheName = DEFAULT_SESSIONIDS_CACHE_NAME;
    private String principalsCacheName = DEFAULT_PRINCIPAL_CACHE_NAME;

    public CacheSessionRegistry(CacheManager cacheManager) {
        super();
        this.cacheManager=cacheManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.debug("Looking up caches '{}','{}'.", sessionIdCacheName, principalsCacheName);
        this.sessionIds=cacheManager.getCache(sessionIdCacheName);
        this.principals=cacheManager.getCache(principalsCacheName);
    }


    @Override
    public List<Object> getAllPrincipals() {
        throw new UnsupportedOperationException("getAllPrincipals not supported.");
    }

    @Override
    public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {


        if (principals.get(principal) == null) {
            return Collections.emptyList();
        }
        final Set<String> sessionsUsedByPrincipal = (Set<String>) principals.get(principal).get();

        List<SessionInformation> list = new ArrayList<SessionInformation>(sessionsUsedByPrincipal.size());

        for (String sessionId : sessionsUsedByPrincipal) {
            SessionInformation sessionInformation = getSessionInformation(sessionId);

            if (sessionInformation == null) {
                continue;
            }

            if (includeExpiredSessions || !sessionInformation.isExpired()) {
                list.add(sessionInformation);
            }
        }

        return list;
    }

    public SessionInformation getSessionInformation(String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");
        Cache.ValueWrapper wrapper = sessionIds.get(sessionId);
        if (wrapper != null) {
            return (SessionInformation) wrapper.get();
        }
        return null;
    }
    @Override
    public void refreshLastRequest(String sessionId) {
        logger.info("refreshLastRequest");
        SessionInformation session = getSessionInformation(sessionId);
        if (session != null) {
            session.refreshLastRequest();
            sessionIds.put(sessionId, session);
        }
    }

    @Override
    public void registerNewSession(String sessionId, Object principal) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");
        Assert.notNull(principal, "Principal required as per interface contract");

        if (logger.isDebugEnabled()) {
            logger.debug("Registering session {}, for principal {}" + sessionId, principal);
        }

        if (getSessionInformation(sessionId) != null) {
            removeSessionInformation(sessionId);
        }

        sessionIds.put(sessionId, new SessionInformation(principal, sessionId, new Date()));


        Set<String> sessionsUsedByPrincipal = new CopyOnWriteArraySet<String>();
        if (principals.get(principal) == null) {
            principals.put(principal, sessionsUsedByPrincipal);
        } else {
            sessionsUsedByPrincipal = (Set<String>) principals.get(principal).get();
        }


        sessionsUsedByPrincipal.add(sessionId);

        if (logger.isTraceEnabled()) {
            logger.trace("Sessions used by '{}' : {}" + principal, sessionsUsedByPrincipal);
        }
    }
    @Override
    public void removeSessionInformation(String sessionId) {
        Assert.hasText(sessionId, "SessionId required as per interface contract");

        SessionInformation info = getSessionInformation(sessionId);

        if (info == null) {
            return;
        }

        logger.debug("Removing session '{}' from set of registered sessions.", sessionId );

        sessionIds.evict(sessionId);

        Set<String> sessionsUsedByPrincipal = null;
        if (principals.get(info.getPrincipal()) != null) {
            sessionsUsedByPrincipal = (Set<String>) principals.get(info.getPrincipal()).get();
        }

        if (sessionsUsedByPrincipal == null) {
            return;
        }

        logger.debug("Removing session '{}' from principal's set of registered sessions.", sessionId);

        sessionsUsedByPrincipal.remove(sessionId);

        if (sessionsUsedByPrincipal.isEmpty()) {
            // No need to keep object in principals Map anymore
            logger.debug("Removing principal '{}' from registry.", info.getPrincipal() );
            principals.evict(info.getPrincipal());
        }

        logger.trace("Sessions used by '{}' : ",info.getPrincipal() , sessionsUsedByPrincipal);
    }

    public void setSessionIdCacheName(String sessionIdCacheName) {
        this.sessionIdCacheName = sessionIdCacheName;
    }

    public void setPrincipalsCacheName(String principalsCacheName) {
        this.principalsCacheName = principalsCacheName;
    }
}
