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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Listener which shows what is happening to the {@code ServletContext}. Useful for insight in what is
 * being put on the {@code ServletContext}.
 *
 * @author Marten Deinum
 *
 * @see ServletContextListener
 * @see ServletContextAttributeListener
 */
public class ServletContextDebugListener implements ServletContextListener, ServletContextAttributeListener {

    private final Logger logger = LoggerFactory.getLogger(ServletContextDebugListener.class);

    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
        logger.debug("Attribute {} : {} added to servletcontext [{}].", event.getName(), event.getValue(), event.getServletContext().getServletContextName());
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) {
        logger.debug("Attribute {} : {} removed from servletcontext [{}].", event.getName(), event.getValue(), event.getServletContext().getServletContextName());
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) {
        logger.debug("Attribute {} : {} replaced in servletcontext [{}].", event.getName(), event.getValue(), event.getServletContext().getServletContextName());
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.debug("ServletContext [{}] initialized.", sce.getServletContext().getServletContextName());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.debug("ServletContext [{}] destroyed.", sce.getServletContext().getServletContextName());
    }
}
