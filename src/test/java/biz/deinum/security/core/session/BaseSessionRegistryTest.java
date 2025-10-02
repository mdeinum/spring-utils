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

package biz.deinum.security.core.session;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.session.SessionInformation;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author marten
 */
public abstract class BaseSessionRegistryTest {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private AbstractSessionRegistry sessionRegistry;

    // ~ Methods
    // ========================================================================================================

    @BeforeEach
    public void setUp() throws Exception {
        logger.debug("Creating SessionRegistry for Testing.");
        sessionRegistry = createSessionRegistry();
    }

    protected abstract AbstractSessionRegistry createSessionRegistry() throws Exception;

    @Test
    public void sessionDestroyedEventRemovesSessionFromRegistry() {
        Object principal = "Some principal object";
        final String sessionId = "zzzz";

        // Register new Session
        sessionRegistry.registerNewSession(sessionId, principal);

        // De-register session via an ApplicationEvent
        sessionRegistry.onApplicationEvent(new SessionDestroyedEvent("") {
            @Override
            public String getId() {
                return sessionId;
            }

            @Override
            public List<SecurityContext> getSecurityContexts() {
                return null;
            }
        });

        // Check attempts to retrieve cleared session return null
        assertThat(sessionRegistry.getSessionInformation(sessionId)).isNull();
    }

    @Test
    public void testMultiplePrincipals() throws Exception {
        Object principal1 = "principal_1";
        Object principal2 = "principal_2";
        String sessionId1 = "1234567890";
        String sessionId2 = "9876543210";
        String sessionId3 = "5432109876";

        sessionRegistry.registerNewSession(sessionId1, principal1);
        sessionRegistry.registerNewSession(sessionId2, principal1);
        sessionRegistry.registerNewSession(sessionId3, principal2);

        assertThat(sessionRegistry.getAllPrincipals()).hasSize(2);
        assertThat(sessionRegistry.getAllPrincipals().contains(principal1)).isTrue();
        assertThat(sessionRegistry.getAllPrincipals().contains(principal2)).isTrue();
    }

    @Test
    public void testSessionInformationLifecycle() throws Exception {
        Object principal = "Some principal object";
        String sessionId = "1234567890";
        // Register new Session
        sessionRegistry.registerNewSession(sessionId, principal);

        // Retrieve existing session by session ID
        Date currentDateTime = sessionRegistry.getSessionInformation(sessionId)
                .getLastRequest();
        assertThat(sessionRegistry.getSessionInformation(sessionId).getPrincipal()).isEqualTo(principal);
        assertThat(sessionRegistry.getSessionInformation(sessionId).getSessionId()).isEqualTo(sessionId);
        assertThat(sessionRegistry.getSessionInformation(sessionId).getLastRequest()).isNotNull();

        // Retrieve existing session by principal
        assertThat(sessionRegistry.getAllSessions(principal, false)).hasSize(1);

        // Sleep to ensure SessionRegistryImpl will update time
        Thread.sleep(1000);

        // Update request date/time
        sessionRegistry.refreshLastRequest(sessionId);

        Date retrieved = sessionRegistry.getSessionInformation(sessionId)
                .getLastRequest();
        assertThat(retrieved.after(currentDateTime)).isTrue();

        // Check it retrieves correctly when looked up via principal
        assertThat(sessionRegistry.getAllSessions(principal, false).get(0).getLastRequest()).isCloseTo(retrieved, 2000L);

        // Clear session information
        sessionRegistry.removeSessionInformation(sessionId);

        // Check attempts to retrieve cleared session return null
        assertThat(sessionRegistry.getSessionInformation(sessionId)).isNull();
        assertThat(sessionRegistry.getAllSessions(principal, false)).isEmpty();
    }

    @Test
    public void testTwoSessionsOnePrincipalExpiring() throws Exception {
        Object principal = "Some principal object";
        String sessionId1 = "1234567890";
        String sessionId2 = "9876543210";

        sessionRegistry.registerNewSession(sessionId1, principal);
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal,
                false);
        assertThat(sessions).hasSize(1);
        assertThat(contains(sessionId1, principal)).isTrue();

        sessionRegistry.registerNewSession(sessionId2, principal);
        sessions = sessionRegistry.getAllSessions(principal, false);
        assertThat(sessions).hasSize(2);
        assertThat(contains(sessionId2, principal)).isTrue();

        // Expire one session
        SessionInformation session = sessionRegistry.getSessionInformation(sessionId2);
        session.expireNow();

        // Check retrieval still correct
        assertThat(sessionRegistry.getSessionInformation(sessionId2).isExpired()).isTrue();
        assertThat(sessionRegistry.getSessionInformation(sessionId1).isExpired()).isFalse();
    }

    @Test
    public void testTwoSessionsOnePrincipalHandling() throws Exception {
        Object principal = "Some principal object";
        String sessionId1 = "1234567890";
        String sessionId2 = "9876543210";

        sessionRegistry.registerNewSession(sessionId1, principal);
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal,
                false);
        assertThat(sessions).hasSize(1);
        assertThat(contains(sessionId1, principal)).isTrue();

        sessionRegistry.registerNewSession(sessionId2, principal);
        sessions = sessionRegistry.getAllSessions(principal, false);
        assertThat(sessions).hasSize(2);
        assertThat(contains(sessionId2, principal)).isTrue();

        sessionRegistry.removeSessionInformation(sessionId1);
        sessions = sessionRegistry.getAllSessions(principal, false);
        assertThat(sessions).hasSize(1);
        assertThat(contains(sessionId2, principal)).isTrue();

        sessionRegistry.removeSessionInformation(sessionId2);
        assertThat(sessionRegistry.getSessionInformation(sessionId2)).isNull();
        assertThat(sessionRegistry.getAllSessions(principal, false)).isEmpty();
    }

    private boolean contains(String sessionId, Object principal) {
        List<SessionInformation> info = sessionRegistry.getAllSessions(principal, false);

        for (int i = 0; i < info.size(); i++) {
            if (sessionId.equals(info.get(i).getSessionId())) {
                return true;
            }
        }

        return false;
    }
}
