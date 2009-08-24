package biz.deinum.springframework.aop.target;

import org.junit.Test;

import biz.deinum.springframework.aop.target.ContextSwappableTargetSource;
import biz.deinum.springframework.aop.target.registry.impl.MapTargetRegistry;


public class ContextSwappableTargetSourceInstantiationTest {

	
	@Test(expected=IllegalArgumentException.class)
	public void noSettingsTest() throws Exception {
		ContextSwappableTargetSource target = new ContextSwappableTargetSource(null);
		target.afterPropertiesSet();
	}

	@Test(expected=IllegalArgumentException.class)
	public void onlyTargetClassSet() throws Exception {
		ContextSwappableTargetSource target = new ContextSwappableTargetSource(javax.sql.DataSource.class);
		target.afterPropertiesSet();
	}

	@Test(expected=IllegalArgumentException.class)
	public void targetClassAndRegistryAndNullDefaultTargetSet() throws Exception {
		ContextSwappableTargetSource target = new ContextSwappableTargetSource(javax.sql.DataSource.class);
		target.setTargetRegistry(new MapTargetRegistry());
		target.setAlwaysReturnTarget(true);
		target.afterPropertiesSet();
	}

}
