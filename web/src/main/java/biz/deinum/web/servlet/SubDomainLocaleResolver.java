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

package biz.deinum.web.servlet;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.AbstractLocaleResolver;

/**
 * {@code LocaleResolver} for resolving the locale based on a subdomain. Assumes that
 * the first part of the servername is the locale to use i.e. {@code en.mysite.org} will
 * render the request resource in English.
 *
 * @author Marten Deinum
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
