package biz.deinum.multitenant.aop.target.registry.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanFactoryTargetRegistryTest {

    private BeanFactoryTargetRegistry<?> registry = new BeanFactoryTargetRegistry();
    private BeanFactory bf;

    @Before
    public void setup() {
        if (bf == null) {
            bf = new ClassPathXmlApplicationContext(
                    "classpath:/biz/deinum/multitenant/aop/target/registry/beanfactorytargetregistry-context.xml");
            registry.setBeanFactory(bf);
        }
    }

    @Test
    public void nothingFound() {
        assertNull(registry.getTarget("bean3"));
    }

    @Test
    public void beanFound() {
        Object target = registry.getTarget("bean1");
        Object bean = bf.getBean("bean1");
        assertNotNull(target);
        assertEquals(bean, target);
    }

}
