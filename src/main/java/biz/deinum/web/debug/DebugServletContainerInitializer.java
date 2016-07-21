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

package biz.deinum.web.debug;

import org.springframework.web.util.ServletContextPropertyUtils;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

/**
 * {@code ServletContainerInitializer} which automatically registers the  {@code HttpSessionDebugListener}
 * and {@code ServletContextDebugListener} if the <code>web.debug.enabled</code> property resolves to <code>true</code>.
 * <p/>
 * The property can be set by
 * 1. ServletContect attribute (context-param)
 * 2. System property (-D to JVM)
 * 3. Environment variable
 * <p/>
 * This is also the order of consulting properties so context overrides system which overrides the environment.
 *
 * @author Marten Deinum
 */
public class DebugServletContainerInitializer implements ServletContainerInitializer {

    private static final String ATTRIBUTE_NAME = "web.debug.enabled";

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        if (isDebugEnabled(ctx)) {
            ctx.log("Debug enabled, registering HttpSessionDebugListener and ServletContextDebugListener.");
            ctx.addListener(HttpSessionDebugListener.class);
            ctx.addListener(ServletContextDebugListener.class);
        } else {
            ctx.log("Debug disabled, skipping registration of HttpSessionDebugListener and ServletContextDebugListener.");
        }
    }

    private boolean isDebugEnabled(ServletContext ctx) {
        String placeholder = "${" + ATTRIBUTE_NAME + ":false}";
        String value = ServletContextPropertyUtils.resolvePlaceholders(placeholder, ctx);
        return Boolean.valueOf(value);
    }

}
