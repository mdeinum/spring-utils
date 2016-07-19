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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * This class takes two parameters a {@code supportedClass} and a {@code field}. The {@code supportedClass}
 * is the class supported by this validator and the {@code field} is the field to validate.
 * 
 * @author Marten Deinum
 * @version 1.1
 *
 */
public abstract class AbstractSimpleClassMappingValidator extends AbstractValidator implements InitializingBean {
    private Class<?> supportedClass;
    protected String field;

    @SuppressWarnings("rawtypes")
    public final boolean supports(final Class clazz) {
        return this.supportedClass.isAssignableFrom(clazz);
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.supportedClass, "'SupportedClass' must be set!");
        Assert.notNull(this.field, "'Field' must be set!");
    }

    protected Object getValue(final Errors errors) {
        return errors.getFieldValue(this.field);
    }

    public final void setSupportedClass(final Class<?> supportedClass) {
        this.logger.debug("supportedClass={}", supportedClass.getName());
        this.supportedClass = supportedClass;
    }

    public final void setField(final String field) {
        this.logger.debug("field={}", field);
        this.field = field;
    }
}
