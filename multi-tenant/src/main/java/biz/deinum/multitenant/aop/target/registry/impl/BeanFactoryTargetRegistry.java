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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.Assert;

import biz.deinum.multitenant.aop.target.registry.AbstractTargetRegistry;

/**
 * {@code TargetRegistry} which retrieves a bean from the {@link BeanFactory}.
 *
 * @author Marten Deinum
 * @version 1.0
 */
public class BeanFactoryTargetRegistry<T> extends AbstractTargetRegistry<T> implements BeanFactoryAware {

    private final Logger logger = LoggerFactory.getLogger(BeanFactoryTargetRegistry.class);

    private BeanFactory beanFactory;
    private String prefix = "";
    private String suffix = "";

    public void setPrefix(final String prefix) {
        this.prefix = (prefix != null ? prefix : "");
    }

    public void setSuffix(final String suffix) {
        this.suffix = (suffix != null ? suffix :"");
    }

    private String getTargetName(final String context) {
        final String beanName = this.prefix + context + this.suffix;
        this.logger.debug("TargetName: {}", beanName);
        return beanName;
    }

    /**
     * Gets the target from the {@code BeanFactory}. The name of the bean is being
     * constructed with the configured {@code prefix} and {@code suffix}.
     * 
     * @return the found object or {@code null}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected T getTargetInternal(final String context) {
        final String beanName = this.getTargetName(context);
        T target = null;
        try {
            this.logger.debug("Retrieving bean '{}' from BeanFactory.", beanName);
            target = (T) this.beanFactory.getBean(beanName);
        } catch (final BeansException be) {
            this.logger.warn("Could not retrieve bean '{}'", context, be);
        }
        return target;
    }

    /**
     * {@inheritDoc}
     */
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

}
