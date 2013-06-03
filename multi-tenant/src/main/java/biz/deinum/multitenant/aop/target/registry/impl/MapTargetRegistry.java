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

import biz.deinum.multitenant.aop.target.registry.AbstractTargetRegistry;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * TargetRegistry which retrieves the correct target from a Map.
 * 
 * @author Marten Deinum
 * @version 1.0
 * 
 */
public class MapTargetRegistry<T> extends AbstractTargetRegistry<T> {

    private final Map<String, T> targets = Collections.synchronizedMap(new WeakHashMap<String, T>());

    public void setTargets(final Map<String, T> targets) {
        this.targets.clear();
        this.targets.putAll(targets);
    }

    /**
     * Retrieves the target from the configured Map. Using the context as a key.
     * 
     * @return the target or <code>null</code>
     */
    @Override
    protected T getTargetInternal(final String context) {
        return this.targets.get(context);
    }

}
