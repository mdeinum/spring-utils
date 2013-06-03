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
