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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import biz.deinum.multitenant.aop.target.registry.TargetRegistry;

@RunWith(MockitoJUnitRunner.class)
public class SimpleCachingTargetRegistryTest {

    @Mock
    private TargetRegistry target;
    private SimpleCachingTargetRegistry cachingRegistry;

    @Before
    public void setup() {
        this.cachingRegistry = new SimpleCachingTargetRegistry(this.target);
    }

    @Test
    public void singleCall() {
        final DummyTarget dummyTarget = new DummyTarget();
        when(this.target.getTarget("test")).thenReturn(dummyTarget);
        final Object result = this.cachingRegistry.getTarget("test");
        assertEquals(dummyTarget, result);
        verify(this.target, times(1)).getTarget("test");
    }

    @Test
    public void repeatedCall() {
        final DummyTarget dummyTarget = new DummyTarget();
        when(this.target.getTarget("test")).thenReturn(dummyTarget);
        final Object result = this.cachingRegistry.getTarget("test");
        assertEquals(dummyTarget, result);
        final Object result2 = this.cachingRegistry.getTarget("test");
        assertEquals(dummyTarget, result2);
        assertEquals(result, result2);
    }

}
