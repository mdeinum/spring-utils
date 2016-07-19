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

import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.filter.GenericFilterBean;

/**
 * {@code BeanPostProcessor} implementation for {@code javax.servlet.Filter} instances in the context. Will call the {@code Filter#init}
 * and {@code Filter#destroy} methods on the filters defined in the application context..
 *
 * When calling the init method calls to {@code FilterConfig#getInitParameter} will be delegated to the {@code Environment} this
 * allows for filters not having properties exposed as setter to be configured in a Spring application context.
 *
 * @author Marten Deinum
 *
 */
public class FilterInitDestroyBeanPostProcessor implements EnvironmentAware, ServletContextAware, DestructionAwareBeanPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(FilterInitDestroyBeanPostProcessor.class);

    private Environment environment;
    private ServletContext servletContext;

    /** Use <code>beanName</code> as prefix for the property to lookup */
    private boolean useBeanNameAsPrefix = true;

    /** Filter class this BeanPostProcessor applies to */
    private Class[] mappedFilterClasses;


    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        if (shouldApplyTo(bean)) {
            logger.debug("Invoking destroy method on bean '" + beanName + "'");
            try {
                ((Filter) bean).destroy();
            } catch (Throwable th) {
                logger.error("Couldn't invoke destroy method on bean with name '" + beanName + "'", th);
            }
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (shouldApplyTo(bean)) {
            try {
                logger.debug("Invoking init method on bean '" + beanName + "'");
                ((Filter) bean).init(new EnvironmentAwareFilterConfig(beanName));
            } catch (ServletException e) {
                throw new BeanCreationException(beanName, "Invocation of init method failed", e);
            }
        }
        return bean;
    }

    /**
     * Check whether this post processor should be applied to the given bean. By default applies to all {@code javax.servlet.Filter}
     * beans which aren't a subclass of {@code GenericFilterBean}. The latter has its own initializing mechanism.
     *
     * Additionally checks if the filter matches one of the given <code>mappedFilterClasses</code> if specified.
     *
     * @param bean the bean to match.
     * @return
     */
    protected boolean shouldApplyTo(Object bean) {
        if (bean instanceof Filter && !(bean instanceof GenericFilterBean)) {
            if (mappedFilterClasses != null) {
                for (Class handlerClass : this.mappedFilterClasses) {
                    if (handlerClass.isInstance(bean)) {
                        return true;
                    }
                }
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment=environment;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext=servletContext;
    }

    /**
     * Should we use the <code>beanName</code> as the prefix for the property to lookup. Default is <code>true</code>.
     *
     * @param useBeanNameAsPrefix
     */
    public void setUseBeanNameAsPrefix(boolean useBeanNameAsPrefix) {
        this.useBeanNameAsPrefix = useBeanNameAsPrefix;
    }

    /**
     * Specify the set of classes that this post processor should apply to.
     */
    public void setMappedFilterClasses(Class[] mappedFilterClasses) {
        this.mappedFilterClasses = mappedFilterClasses;
    }

    /**
     * {@code FilterConfig} which delegates calls to the {@code #getInitParameter} to the {@coe Environment}.
     *
     * <code>getFilterName</code> will return the <code>beanName</code>
     * <code>getInitParameter</code> delegates to the {@code Environment}
     * <code>getServletContext</code> returns the current <code>ServletContext</code>
     * <code>getInitParameterNames</code> always returns <code>null</code>
     */
    private class EnvironmentAwareFilterConfig implements FilterConfig {

        private final String beanName;

        private EnvironmentAwareFilterConfig(String beanName) {
            this.beanName = beanName;
        }

        /**
         * Returns the name of the bean, which with a {@code DelegatingFilterProxy} is often the same as the actual filter-name.
         */
        @Override
        public String getFilterName() {
            return this.beanName;
        }

        @Override
        public ServletContext getServletContext() {
            return servletContext;
        }

        /**
         * Return the given property from the <code>Environment</code>.
         */
        @Override
        public String getInitParameter(String name) {
            if (useBeanNameAsPrefix) {
                return environment.getProperty(beanName+"."+name);
            } else {
                return environment.getProperty(name);
            }
        }

        /**
         * Always return <code>null</code>.
         *
         * @return
         */
        @Override
        public Enumeration<String> getInitParameterNames() {
            return null;
        }
    }
}
