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
