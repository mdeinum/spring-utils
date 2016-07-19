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

package biz.deinum.multitenant.aop.target;

import biz.deinum.multitenant.aop.target.registry.TargetRegistry;
import biz.deinum.multitenant.core.ContextHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContextSwappableTargetSourceTest {

    private static final String CONTEXT = "test-context";
    private ContextSwappableTargetSource ts;
    @Mock
    private TargetRegistry registry;

    @Before
    public void before() {
        this.ts = new ContextSwappableTargetSource(DummyTestInterface.class);
        this.ts.setTargetRegistry(this.registry);
        ContextHolder.setContext(CONTEXT);
    }

    @After
    public void after() {
        ContextHolder.setContext(null);
    }

    @Test(expected = TargetLookupFailureException.class)
    public void nullResult() throws Exception {
        when(this.registry.getTarget(CONTEXT)).thenReturn(null);
        this.ts.getTarget();
        verify(this.registry, times(1)).getTarget((CONTEXT));
    }

    @Test(expected = TargetLookupFailureException.class)
    public void objectOfWrongType() throws Exception {
        when(this.registry.getTarget(CONTEXT)).thenReturn(new ArrayList());
        this.ts.getTarget();
    }

    @Test
    public void nullObjectWithDefaultTarget() throws Exception {
        final DummyTestInterface defaultTarget = new DummyTestInterface() {
        };
        this.ts.setAlwaysReturnTarget(true);
        this.ts.setDefaultTarget(defaultTarget);
        when(this.registry.getTarget(CONTEXT)).thenReturn(null);
        final Object target = this.ts.getTarget();
        assertEquals(defaultTarget, target);
    }

}
