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
