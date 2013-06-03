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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link TargetRegistry} implementation that does some simple caching.
 * It first checks the internal cache it it hasn't found a target it checks
 * the delegate TargetRegistry for a target, if found it is registered in the
 * internal cache for future use.
 * 
 * @author Marten Deinum
 * @since 1.2.0
 *
 */
public class SimpleCachingTargetRegistry<T> implements TargetRegistry<T> {

    private final Map<String, T> cache = new ConcurrentHashMap<String, T>();
    private final TargetRegistry<T> delegate;

    public SimpleCachingTargetRegistry(final TargetRegistry<T> delegate) {
        super();
        this.delegate = delegate;
    }

    public T getTarget(final String context) {
        T target = this.cache.get(context);
        if (target == null) {
            target = this.delegate.getTarget(context);
            if (target != null) {
                this.cache.put(context, target);
            }
        }
        return target;
    }
}
