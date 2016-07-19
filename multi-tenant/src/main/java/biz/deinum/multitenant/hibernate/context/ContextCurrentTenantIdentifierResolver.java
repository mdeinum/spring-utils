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

package biz.deinum.multitenant.hibernate.context;

import biz.deinum.multitenant.core.ContextHolder;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

/**
 * {@code CurrentTenantIdentifierResolver} which simply delegates to the {@code ContextHolder} to
 * determine the current tenant identifier.
 *
 * @author Marten Deinum
 *
 * @since 1.3.0
 */
public class ContextCurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    private boolean validateExistingCurrentSessions = true;

    @Override
    public String resolveCurrentTenantIdentifier() {
        return ContextHolder.getContext();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return validateExistingCurrentSessions;
    }

    public void setValidateExistingCurrentSessions(boolean validateExistingCurrentSessions) {
        this.validateExistingCurrentSessions = validateExistingCurrentSessions;
    }
}
