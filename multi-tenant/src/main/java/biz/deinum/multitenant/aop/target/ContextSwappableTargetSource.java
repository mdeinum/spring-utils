/*
 * Copyright 2007-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package biz.deinum.multitenant.aop.target;

import biz.deinum.multitenant.aop.target.registry.TargetRegistry;
import biz.deinum.multitenant.aop.target.registry.impl.BeanFactoryTargetRegistry;
import biz.deinum.multitenant.core.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.OrderComparator;
import org.springframework.util.Assert;

import java.util.*;

/**
 * TargetSource which returns the correct target based on the current context set in the {@link biz.deinum.multitenant.core.ContextHolder}.
 * If no context is found a {@link TargetLookupFailureException} is thrown or the <code>defaultTarget</code> is returned
 * , depending on the setting of the alwaysReturnTarget property (default is false);
 * 
 * By default this class delegates the detection of target to a {@link BeanFactoryTargetRegistry}. 
 * Multiple target registries can be registered on this bean, either manually or by just declaring
 * them in the ApplicationContext. A best effort is made to lookup the TargetRegistries from the
 * ApplicatonContext.
 *  
 * 
 * @author M. Deinum
 * @version 1.2
 * @see ContextHolder
 * @see TargetSource
 * @see TargetRegistry
 */
public class ContextSwappableTargetSource implements TargetSource, InitializingBean, ApplicationContextAware {
    private final Logger logger = LoggerFactory.getLogger(ContextSwappableTargetSource.class);

    /** The TargetRegistries used to lookup the desired target */
    private final List<TargetRegistry<?>> registries = new LinkedList<TargetRegistry<?>>();

    /** The type of class this TargetSource supports */
    private final Class<?> targetClass;

    /** Should we always return a value, if <code>true</code>, defaultTarget must also be set */
    private boolean alwaysReturnTarget = false;

    /** The defaultObject to return if <code>alwaysReturnTarget</code> is true */
    private Object defaultTarget;

    /** The ApplicationContext we are defined in */
    private ApplicationContext context;

    /**
     * Constructor for the {@link ContextSwappableTargetSource} class. It takes a 
     * Class as a parameter.
     * 
     * @param targetClass The Class which this TargetSource represents.
     */
    public ContextSwappableTargetSource(final Class<?> targetClass) {
        super();
        this.targetClass = targetClass;
    }

    /**
     * Locate and return the target for the current context.
     * 
     * First we lookup the context name from the {@link ContextHolder}
     * this context name is used to lookup the desired target. When no
     * target is found a {@link TargetLookupFailureException} is thrown.
     * 
     * If the targetClass is of a invalid type we throw a {@link TargetLookupFailureException}
     * 
     * @see ContextHolder
     * @throws TargetLookupFailureException
     */

    public Object getTarget() throws Exception {
        final String contextName = ContextHolder.getContext();
        this.logger.debug("Current context: '{}'", contextName);

        final Object target = this.getTarget(contextName);

        if (target == null) {
            this.logger.error("Cannot locate a target of type '{}' for context '{}'", this.targetClass.getName(),
                    contextName);
            throw new TargetLookupFailureException("Cannot locate a target for context '" + contextName + "'");
        }

        if (!this.targetClass.isAssignableFrom(target.getClass())) {
            throw new TargetLookupFailureException("The target for '" + contextName + "' is not of the required type."
                    + "Expected '" + this.targetClass.getName() + "' and got '" + target.getClass().getName() + "'");
        }

        return target;

    }

    public final Class<?> getTargetClass() {
        return this.targetClass;
    }

    public final boolean isStatic() {
        return false;
    }

    /**
     * Gets the targetobject from the configured {@link TargetRegistry}. If no
     * target is found and <code>alwaysReturnTarget</code> is set to <code>
     * true</code>, the <code>defaultTarget</code> is used as the targetObject,
     * else <code>null</code> is being returned. 
     * 
     * @param context
     * @return targetObject or <code>null</code>
     * @throws TargetLookupFailureException if nothing can be returned
     * 
     * @see TargetRegistry#getTarget(String)
     */
    private Object getTarget(final String context) {
        Object target = this.resolveTarget(context);
        if (target == null && this.alwaysReturnTarget) {
            this.logger.debug("Return default target for context '{}'", context);
            target = this.defaultTarget;
        }
        return target;
    }

    protected Object resolveTarget(final String context) {
        Object target = null;
        for (final TargetRegistry<?> registry : this.registries) {
            this.logger.debug("Using '{}' to lookup '{}'.", registry, context);
            target = registry.getTarget(context);
            if (target != null) {
                break;
            }
        }
        return target;
    }

    public void releaseTarget(final Object target) throws Exception {
    }

    public final void afterPropertiesSet() throws Exception {
        Assert.notNull(this.targetClass, "TargetClass property must be set!");

        this.initTargetRegistries();

        if (this.alwaysReturnTarget) {
            Assert.notNull(this.defaultTarget,
                    "The defaultTarget property is null, while alwaysReturnTarget is set to true. "
                            + "When alwaysReturnTarget is set to true a defaultTarget must be set!");
        }
    }

    @SuppressWarnings("unchecked")
    private void initTargetRegistries() {
        if (this.registries.isEmpty()) {
            final Map<String, ? extends TargetRegistry> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
                    this.context, TargetRegistry.class, true, false);
            if (!matchingBeans.isEmpty()) {
                this.registries.addAll((Collection<? extends TargetRegistry<?>>) matchingBeans.values());
                Collections.sort(this.registries, new OrderComparator());
            } else {
                @SuppressWarnings("rawtypes")
                final BeanFactoryTargetRegistry<?> registry = new BeanFactoryTargetRegistry();
                registry.setBeanFactory(this.context);
                this.registries.add(registry);
            }
        }
    }

    public final void setAlwaysReturnTarget(final boolean alwaysReturnTarget) {
        this.alwaysReturnTarget = alwaysReturnTarget;
    }

    public final void setDefaultTarget(final Object defaultTarget) {
        this.defaultTarget = defaultTarget;
    }

    public final void setTargetRegistry(final TargetRegistry<?> registry) {
        this.registries.clear();
        this.registries.add(registry);
    }

    public final void setTargetRegistries(final List<TargetRegistry<?>> registries) {
        this.registries.addAll(registries);
    }

    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    List<TargetRegistry<?>> getTargetRegistries() {
        return this.registries;
    }

}
