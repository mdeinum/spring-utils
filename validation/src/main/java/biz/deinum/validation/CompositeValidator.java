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

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This validator will delegate each of it's child validators.
 *
 * @author Marten Deinum (thanks to Colin Yates)
 *
 */
public final class CompositeValidator implements Validator {
    private final List<Validator> validators = new LinkedList<Validator>();

    public CompositeValidator(final Validator[] validators) {
        this(Arrays.asList(validators));
    }

    public CompositeValidator(final List<Validator> validators) {
        super();
        this.validators.addAll(validators);
    }

    /**
     * Will return true if this class is in the specified map.
     */
    @SuppressWarnings("rawtypes")
    public boolean supports(final Class clazz) {
        for (final Validator v : this.validators) {
            if (v.supports(clazz)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validate the specified object using the validator registered for the object's class.
     */
    public void validate(final Object target, final Errors errors) {
        for (final Validator v : this.validators) {
            if (v.supports(target.getClass())) {
                v.validate(target, errors);
            }
        }
    }
}
