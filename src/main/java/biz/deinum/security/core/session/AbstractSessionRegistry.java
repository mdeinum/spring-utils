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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.session.SessionRegistry;

/**
 * @author Marten Deinum
 */
public abstract class AbstractSessionRegistry implements SessionRegistry, ApplicationListener<SessionDestroyedEvent> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public final  void onApplicationEvent(SessionDestroyedEvent event) {
        String id = event.getId();
        logger.debug("SessionDestroyed [{}]", id);
        removeSessionInformation(id);
    }
}
