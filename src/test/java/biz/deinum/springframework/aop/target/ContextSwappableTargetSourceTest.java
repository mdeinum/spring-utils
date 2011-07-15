package biz.deinum.springframework.aop.target;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import biz.deinum.springframework.aop.target.registry.TargetPostProcessor;
import biz.deinum.springframework.aop.target.registry.TargetRegistry;
import biz.deinum.springframework.core.ContextHolder;

@RunWith(MockitoJUnitRunner.class)
public class ContextSwappableTargetSourceTest {

	private static final String CONTEXT = "test-context";
	private ContextSwappableTargetSource ts;
	@Mock
	private TargetRegistry registry;
	@Mock
	private TargetPostProcessor targetPostProcessor;

	@Before
	public void before() {
		this.ts = new ContextSwappableTargetSource(DummyTestInterface.class);
		this.ts.setTargetRegistry(this.registry);
		this.ts.setTargetPostProcessor(this.targetPostProcessor);
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

	@Test
	public void callingPostProcessor() throws Exception {
		final DummyTestInterface target = new DummyTestInterface() {
		};
		this.ts.setTargetPostProcessor(this.targetPostProcessor);

		when(this.registry.getTarget(CONTEXT)).thenReturn(target);
		when(this.targetPostProcessor.supports(isA(Class.class))).thenReturn(Boolean.TRUE);
		this.targetPostProcessor.postProcess(target);
		assertEquals(target, this.ts.getTarget());

		verify(this.registry, times(1)).getTarget(CONTEXT);
		verify(this.targetPostProcessor, times(1)).supports(isA(Class.class));
	}

}
