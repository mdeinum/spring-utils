package biz.deinum.springframework.aop.target;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.annotation.ExpectedException;

import biz.deinum.springframework.aop.target.ContextSwappableTargetSource;
import biz.deinum.springframework.aop.target.registry.impl.BeanFactoryTargetRegistry;
import biz.deinum.springframework.aop.target.registry.impl.MapTargetRegistry;


public class ContextSwappableTargetSourceInstantiationTest {

	
	@Test(expected=IllegalArgumentException.class)
	public void nullTargetClass() throws Exception {
		ContextSwappableTargetSource target = new ContextSwappableTargetSource(null);
		target.afterPropertiesSet();
	}

	@Test
	public void checkInternalDefaults() throws Exception {
		ContextSwappableTargetSource target = new ContextSwappableTargetSource(javax.sql.DataSource.class);
		target.setApplicationContext(new GenericApplicationContext());
		target.afterPropertiesSet();
		
		assertEquals(1, target.getTargetRegistries().size());
		assertTrue(target.getTargetRegistries().get(0) instanceof BeanFactoryTargetRegistry);		
	}

	@Test
	public void checkConfigurationInApplicationContext() throws Exception {
		ContextSwappableTargetSource target = new ContextSwappableTargetSource(javax.sql.DataSource.class);
		GenericApplicationContext context = new GenericApplicationContext();
		context.registerBeanDefinition("registry1", new RootBeanDefinition(BeanFactoryTargetRegistry.class));
		context.registerBeanDefinition("registry2", new RootBeanDefinition(MapTargetRegistry.class));
		context.refresh();
		target.setApplicationContext(context);
		target.afterPropertiesSet();
		
		assertEquals(2, target.getTargetRegistries().size());
	}

	
	@Test(expected=IllegalArgumentException.class)
	public void targetClassAndRegistryAndNullDefaultTargetSet() throws Exception {
		ContextSwappableTargetSource target = new ContextSwappableTargetSource(javax.sql.DataSource.class);
		target.setTargetRegistry(new MapTargetRegistry());
		target.setAlwaysReturnTarget(true);
		target.afterPropertiesSet();
	}

}
