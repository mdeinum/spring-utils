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
