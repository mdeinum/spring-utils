package biz.deinum.springframework.aop.target;
import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import biz.deinum.springframework.aop.target.registry.TargetPostProcessor;
import biz.deinum.springframework.aop.target.registry.TargetRegistry;
import biz.deinum.springframework.core.ContextHolder;


public class ContextSwappableTargetSourceTest {

	private static final String CONTEXT = "test-context";
	
	private ContextSwappableTargetSource ts;
	private TargetRegistry registry;
	private TargetPostProcessor targetPostProcessor;
	
	
	@Before
	public void before() {
		registry = EasyMock.createMock(TargetRegistry.class);
		targetPostProcessor = EasyMock.createMock(TargetPostProcessor.class);
		ts = new ContextSwappableTargetSource(DummyTestInterface.class);
		ts.setTargetRegistry(registry);
		ContextHolder.setContext(CONTEXT);
	}
	
	@After
	public void after() {
		EasyMock.reset(registry, targetPostProcessor);
		ContextHolder.setContext(null);
	}
	
	@Test(expected=TargetLookupFailureException.class)
	public void nullResult() throws Exception {
		expect(registry.getTarget(CONTEXT)).andStubReturn(null);
		replay(registry);
		ts.getTarget();
	}

	
	@Test(expected=TargetLookupFailureException.class)
	public void objectOfWrongType() throws Exception {
		expect(registry.getTarget(CONTEXT)).andStubReturn(new ArrayList());
		replay(registry);
		ts.getTarget();		
	}
	
	@Test
	public void nullObjectWithDefaultTarget() throws Exception {
		DummyTestInterface defaultTarget = new DummyTestInterface() {};
		ts.setAlwaysReturnTarget(true);
		ts.setDefaultTarget(defaultTarget);
		expect(registry.getTarget(CONTEXT)).andStubReturn(null);
		replay(registry, targetPostProcessor);
		Object target = ts.getTarget();
		assertEquals(defaultTarget, target);		
		verify(registry, targetPostProcessor);
	}
	
	@Test
	public void callingPostProcessor() throws Exception {
		DummyTestInterface target = new DummyTestInterface() {};
		ts.setTargetPostProcessor(targetPostProcessor);
		
		expect(registry.getTarget(CONTEXT)).andStubReturn(target);
		expect(targetPostProcessor.supports(isA(Class.class))).andStubReturn(Boolean.TRUE);
		targetPostProcessor.postProcess(target);
		replay(registry, targetPostProcessor);
		assertEquals(target, ts.getTarget());
		verify(registry, targetPostProcessor);
	}
	
	
}
