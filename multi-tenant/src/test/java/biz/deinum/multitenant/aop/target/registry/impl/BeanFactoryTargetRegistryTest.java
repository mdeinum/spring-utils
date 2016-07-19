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
