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

package biz.deinum.multitenant.aop.target.registry.impl;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

public class JndiLookupTargetRegistryTests {

    private static final String CONTEXT = "test-context";

    private SimpleNamingContextBuilder builder;

    private JndiLookupTargetRegistry targetRegistry = new JndiLookupTargetRegistry();

    @Before
    public void setup() throws Exception {

        targetRegistry = new JndiLookupTargetRegistry();

        builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        builder.bind(CONTEXT, new Date());
    }

    @After
    public void clean() throws Exception {
        builder.clear();
    }

    @Test
    public void notFound() {
        assertNull(targetRegistry.getTarget("not-existing"));
    }

    @Test
    public void found() {
        Object target = targetRegistry.getTarget(CONTEXT);
        assertTrue(target instanceof Date);
    }

    @Test
    public void prefixing() {
        String prefix = "prefix-";
        String suffix = "-suffix";
        targetRegistry.setPrefix(prefix);
        targetRegistry.setSuffix(suffix);
        String name = prefix + CONTEXT + suffix;

        assertEquals(name, targetRegistry.getJndiName(CONTEXT));
    }

}
