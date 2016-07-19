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

package biz.deinum.validation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.ClassUtils;
import org.springframework.validation.Errors;

/**
 * 
 * @author mdeinum
 */
public abstract class AbstractClassMappingValidator extends AbstractValidator {
    private Map<Class<?>, String> mappings = new HashMap<Class<?>, String>();

    /**
     * Check all the mappings assigned to this validator. If one of the classes
     * is assignable to this object return true.
     * 
     * @return <code>true</code> when supported, <code>false</code> otherwise
     */
    @SuppressWarnings("rawtypes")
    public final boolean supports(final Class clazz) {
        final Iterator keyIter = this.mappings.keySet().iterator();
        boolean supports = false;
        while (keyIter.hasNext() && !supports) {
            final Class targetClazz = (Class) keyIter.next();
            supports = ClassUtils.isAssignable(targetClazz, clazz);
        }
        return supports;
    }

    /**
     * Gets the fieldname from the configured map. 
     * @param target
     * @return
     */
    protected final String getFieldName(final Object target) {
        String fieldName = null;
        for (final Entry<Class<?>, String> entry : this.mappings.entrySet()) {
            if (ClassUtils.isAssignable(entry.getKey(), target.getClass())) {
                fieldName = entry.getValue();
                break;
            }
        }

        if (fieldName == null) {
            throw new IllegalStateException("Cannot find fieldname for class " + target.getClass().getName()
                    + ". Class is not compatible with declared types. [" + this.mappings + "]");
        }
        return fieldName;

    }

    protected final Object getValue(final Object target, final Errors errors) {
        final String field = this.getFieldName(target);
        return errors.getFieldValue(field);
    }

    public final void setMappings(final Map<Class<?>, String> mappings) {
        this.mappings = mappings;
    }
}
