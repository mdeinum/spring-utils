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

package biz.deinum.web.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.*;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Session listener to debug session creation/passivation/assignment.
 *
 * @author Marten Deinum
 *
 * @see HttpSessionListener
 * @see HttpSessionActivationListener
 * @see HttpSessionAttributeListener
 */
public class HttpSessionDebugListener implements HttpSessionListener, HttpSessionActivationListener, HttpSessionAttributeListener {

    private final Logger logger = LoggerFactory.getLogger(HttpSessionDebugListener.class);

    @Override
    public void sessionWillPassivate(HttpSessionEvent se) {
        logger.debug("Session [{}] will be passivated.", se.getSession().getId());
    }

    @Override
    public void sessionDidActivate(HttpSessionEvent se) {
        logger.debug("Session [{}] was activated.", se.getSession().getId());
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        logger.debug("Attribute {} : {} added to session [{}].", event.getName(), event.getValue(), event.getSession().getId());
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        logger.debug("Attribute {} : {} removed from session [{}].", event.getName(), event.getValue(), event.getSession().getId());
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        logger.debug("Attribute {} : {} replaced in session [{}].", event.getName(), event.getValue(), event.getSession().getId());
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        logger.debug("Session [{}] created.", se.getSession().getId());
    }

    /**
     * Logs if a session timed out or not.
     *
     * Timeout detection is a best effort and isn't 100% reliable!
     *
     * @param se
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        long current = System.currentTimeMillis();
        long lastAccessed = session.getLastAccessedTime();
        long interval = SECONDS.toMillis(session.getMaxInactiveInterval());
        boolean timedOut = (current - lastAccessed) > interval;

        logger.debug("Session [{}] destroyed [timeout: {}].", se.getSession().getId(), timedOut);
    }
}
