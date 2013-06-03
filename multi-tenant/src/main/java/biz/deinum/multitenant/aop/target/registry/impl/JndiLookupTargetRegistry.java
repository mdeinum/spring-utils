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
package biz.deinum.multitenant.aop.target.registry.impl;

import biz.deinum.multitenant.aop.target.registry.TargetRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jndi.JndiLocatorSupport;
import org.springframework.jndi.JndiObjectLocator;
import org.springframework.jndi.JndiTemplate;

import javax.naming.NamingException;

/**
 * {@link TargetRegistry} implementation which delegates the lookup to a
 * {@link JndiTemplate}. 
 * 
 * Can optionally include a <code>prefix</code> and <code>suffix</code> to be 
 * added to make up the actual JNDI name.
 * 
 * @author Marten Deinum
 * @since 1.1
 * 
 * @see JndiObjectLocator
 */
public class JndiLookupTargetRegistry<T> extends JndiLocatorSupport implements TargetRegistry<T> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String prefix = "";

    private String suffix = "";

    /**
     * {@inheritDoc}
     * 
     * @see #lookup(String)
     */
    @SuppressWarnings("unchecked")
    public T getTarget(final String context) {
        T target = null;
        try {
            final String jndiName = this.getJndiName(context);
            target = (T) this.lookup(jndiName);
        } catch (final NamingException e) {
            //Log exception but don't rethrow, that would break the TargetRegistry contract.
            this.logger.error("Error looking up target for context '{}'", context, e);
        }
        return target;
    }

    /**
     * Set the prefix that gets prepended to the context name when building the
     * jndiname.
     */
    public void setPrefix(final String prefix) {
        this.prefix = (prefix != null ? prefix : "");
    }

    /**
     * Set the suffix that gets appended to the context name when building the
     * jndiname.
     */
    public void setSuffix(final String suffix) {
        this.suffix = (suffix != null ? suffix : "");
    }

    protected String getJndiName(final String context) {
        return this.prefix + context + this.suffix;
    }

}
