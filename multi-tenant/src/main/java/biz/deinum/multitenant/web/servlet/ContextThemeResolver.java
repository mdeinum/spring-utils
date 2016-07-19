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

package biz.deinum.multitenant.web.servlet;

import biz.deinum.multitenant.core.ContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.theme.AbstractThemeResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@code ThemeResolver} implementation which returns the current Context as
 * the name of the theme to use.
 *
 * Optionally a {@code prefix} and {@code suffix} can be applied.
 *
 * @author Marten Deinum
 * @since 1.3
 */
public class ContextThemeResolver extends AbstractThemeResolver {

    private String prefix = "";

    private String suffix = "";


    @Override
    public String resolveThemeName(HttpServletRequest request) {
        String theme = ContextHolder.getContext();
        if (StringUtils.hasText(theme)) {
            return this.prefix + theme + this.suffix;
        }
        return getDefaultThemeName();
    }

    @Override
    public void setThemeName(HttpServletRequest request, HttpServletResponse response, String themeName) {
        throw new UnsupportedOperationException("Cannot change theme - use a different theme resolution strategy");
    }

    public void setPrefix(String prefix) {
        this.prefix = (prefix != null ? prefix : "");
    }

    public void setSuffix(String suffix) {
        this.suffix = (suffix != null ? suffix : "");
    }
}
