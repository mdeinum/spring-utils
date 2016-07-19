package biz.deinum.web.servlet;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.AbstractLocaleResolver;

/**
 * @author marten
 */
public class SubDomainLocaleResolver extends AbstractLocaleResolver {


    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String domain = request.getServerName();
        String language = domain.substring(0, domain.indexOf('.'));
        Locale  locale = StringUtils.parseLocaleString(language);
        if (locale == null) {
            locale = determineDefaultLocale(request);
        }
        return locale != null ? locale : determineDefaultLocale(request);
    }

    /**
     * Determine the default locale for the given request,
     * Called if no locale cookie has been found.
     * <p>The default implementation returns the specified default locale,
     * if any, else falls back to the request's accept-header locale.
     * @param request the request to resolve the locale for
     * @return the default locale (never {@code null})
     * @see #setDefaultLocale
     * @see javax.servlet.http.HttpServletRequest#getLocale()
     */
    protected Locale determineDefaultLocale(HttpServletRequest request) {
        Locale defaultLocale = getDefaultLocale();
        if (defaultLocale == null) {
            defaultLocale = request.getLocale();
        }
        return defaultLocale;
    }


    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        throw new UnsupportedOperationException("Cannot change sub-domain locale - use a different locale resolution strategy");

    }
}
