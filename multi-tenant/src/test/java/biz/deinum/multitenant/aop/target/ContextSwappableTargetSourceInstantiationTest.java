package biz.deinum.multitenant.aop.target;

import biz.deinum.multitenant.aop.target.registry.impl.BeanFactoryTargetRegistry;
import biz.deinum.multitenant.aop.target.registry.impl.MapTargetRegistry;
import org.junit.Test;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ContextSwappableTargetSourceInstantiationTest {

    @Test(expected = IllegalArgumentException.class)
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

    @Test(expected = IllegalArgumentException.class)
    public void targetClassAndRegistryAndNullDefaultTargetSet() throws Exception {
        ContextSwappableTargetSource target = new ContextSwappableTargetSource(javax.sql.DataSource.class);
        target.setTargetRegistry(new MapTargetRegistry<DataSource>());
        target.setAlwaysReturnTarget(true);
        target.afterPropertiesSet();
    }

}
