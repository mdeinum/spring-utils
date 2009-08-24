package biz.deinum.springframework.aop.target.registry.impl;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import biz.deinum.springframework.aop.target.registry.TargetRegistry;


public class SimpleCachingTargetRegistryTest {

	private TargetRegistry target;
	private SimpleCachingTargetRegistry cachingRegistry;
	
	@Before
	public void setup() {
		target = EasyMock.createMock(TargetRegistry.class);
		cachingRegistry = new SimpleCachingTargetRegistry(target);
	}
	
	@After
	public void after() {
		EasyMock.verify(target);
	}
	
	@Test
	public void singleCall() {
		DummyTarget dummyTarget = new DummyTarget();
		expect(target.getTarget("test")).andReturn(dummyTarget);
		expectLastCall();
		replay(target);
		Object result = cachingRegistry.getTarget("test");
		assertEquals(dummyTarget, result);
	}

	@Test
	public void repeatedCall() {
		DummyTarget dummyTarget = new DummyTarget();
		expect(target.getTarget("test")).andReturn(dummyTarget);
		expectLastCall();
		replay(target);
		Object result = cachingRegistry.getTarget("test");
		assertEquals(dummyTarget, result);
		Object result2 = cachingRegistry.getTarget("test");
		assertEquals(dummyTarget, result2);
		assertEquals(result, result2);
	}

	
}
