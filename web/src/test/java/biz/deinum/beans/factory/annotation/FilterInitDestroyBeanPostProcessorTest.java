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

package biz.deinum.beans.factory.annotation;

import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;


/**
 * Created by in329dei on 29-1-14.
 */
public class FilterInitDestroyBeanPostProcessorTest {


    @Test
    public void shouldApplyTo() {
        FilterInitDestroyBeanPostProcessor processor = new FilterInitDestroyBeanPostProcessor();
        assertFalse("Date is not a Filter, should not apply.", processor.shouldApplyTo(new Date()));
        assertFalse("Filter is a GenericFilterBean, should not apply", processor.shouldApplyTo(new GenericFilterBean() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            }
        }));
        assertTrue("TestFilter is a normal filter, should apply.", processor.shouldApplyTo(new DummyTestFilter()));
        processor.setMappedFilterClasses(new Class[] {CharacterEncodingFilter.class});

        assertFalse("TestFilter isn't a mappedFilterClass, should not apply.", processor.shouldApplyTo(new DummyTestFilter()));
    }

    @Test
    public void standaloneBeanPostProcessorTest() {
        final DummyTestFilter bean = new DummyTestFilter();
        final String beanName = "testFilter";

        MockServletContext servletContext = new MockServletContext();
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty(beanName+".foo", "bar");


        assertFalse(bean.isInitCalled());
        assertFalse(bean.isDestroyCalled());

        FilterInitDestroyBeanPostProcessor processor = new FilterInitDestroyBeanPostProcessor();
        processor.setServletContext(servletContext);
        processor.setEnvironment(environment);
        processor.postProcessBeforeInitialization(bean, beanName);

        assertTrue(bean.isInitCalled());
        assertFalse(bean.isDestroyCalled());
        assertEquals("bar", bean.getValue());
        assertEquals(beanName, bean.getName());

        processor.postProcessBeforeDestruction(bean, beanName);

        assertTrue(bean.isInitCalled());
        assertTrue(bean.isDestroyCalled());
        assertEquals("bar", bean.getValue());
        assertEquals(beanName, bean.getName());

    }

    @Test
    public void integrationBeanPostProcessorTest() {
        MockServletContext servletContext = new MockServletContext();
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("filter1.foo", "bar");
        environment.setProperty("filter2.foo", "baz");

        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(servletContext);
        context.setEnvironment(environment);
        context.register(TestConfig.class);
        context.refresh();

        Map<String, DummyTestFilter> filters = context.getBeansOfType(DummyTestFilter.class);
        assertEquals(2, filters.size());

        DummyTestFilter filter1 = filters.get("filter1");
        assertTrue(filter1.isInitCalled());
        assertFalse(filter1.isDestroyCalled());
        assertEquals("bar", filter1.getValue());
        assertEquals("filter1", filter1.getName());

        DummyTestFilter filter2 = filters.get("filter2");
        assertTrue(filter2.isInitCalled());
        assertFalse(filter2.isDestroyCalled());
        assertEquals("baz", filter2.getValue());
        assertEquals("filter2", filter2.getName());

        context.destroy();
        assertTrue(filter1.isDestroyCalled());
        assertTrue(filter2.isDestroyCalled());


    }

    @Configuration
    public static class TestConfig {

        @Bean
        public static FilterInitDestroyBeanPostProcessor filterInitDestroyBeanFactoryPostProcessor() {
            return new FilterInitDestroyBeanPostProcessor();
        }

        @Bean
        public Filter filter1() {
            return new DummyTestFilter();
        }

        @Bean
        public Filter filter2() {
            return new DummyTestFilter();
        }

    }

    private static class DummyTestFilter implements Filter {

        private String value;
        private String name;

        private boolean initCalled = false;
        private boolean destroyCalled = false;

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            name = filterConfig.getFilterName();
            value = filterConfig.getInitParameter("foo");
            initCalled=true;
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        }

        @Override
        public void destroy() {
            destroyCalled=true;
        }

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        public boolean isInitCalled() {
            return initCalled;
        }

        public boolean isDestroyCalled() {
            return destroyCalled;
        }
    }

}
