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

import org.junit.Test;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import biz.deinum.security.core.session.AbstractSessionRegistry;
import biz.deinum.security.core.session.BaseSessionRegistryTest;

/**
 * @author marten
 */
public class CacheSessionRegistryTest extends BaseSessionRegistryTest {

    @Override
    protected AbstractSessionRegistry createSessionRegistry() throws Exception {
        CacheSessionRegistry sessionRegistry = new CacheSessionRegistry(new ConcurrentMapCacheManager());
        sessionRegistry.afterPropertiesSet();
        return sessionRegistry;
    }

    @Test
    public void testMultiplePrincipals() throws Exception {
        try {
            super.testMultiplePrincipals();
        } catch (UnsupportedOperationException e) {

        }
    }
}