package biz.deinum.beans.factory.annotation;

import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.*;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


/**
 * Created by in329dei on 29-1-14.
 */
public class FilterInitDestroyBeanFactoryPostProcessorTest {


    @Test
    public void standaloneBeanPostProcessorTest() {
        MockServletContext servletContext = new MockServletContext();
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("foo", "bar");

        final DummyTestFilter bean = new DummyTestFilter();
        final String beanName = "testFilter";

        assertFalse(bean.isInitCalled());
        assertFalse(bean.isDestroyCalled());

        FilterInitDestroyBeanFactoryPostProcessor processor = new FilterInitDestroyBeanFactoryPostProcessor();
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
        environment.setProperty("foo", "bar");

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
        assertEquals("bar", filter2.getValue());
        assertEquals("filter2", filter2.getName());

        context.destroy();
        assertTrue(filter1.isDestroyCalled());
        assertTrue(filter2.isDestroyCalled());


    }

    @Configuration
    public static class TestConfig {

        @Bean
        public static FilterInitDestroyBeanFactoryPostProcessor filterInitDestroyBeanFactoryPostProcessor() {
            return new FilterInitDestroyBeanFactoryPostProcessor();
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
