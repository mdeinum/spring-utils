package biz.deinum.logging.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

/**
 * Created by in329dei on 13-2-14.
 */
public class MdcInsertingListener implements ServletRequestListener, ServletContextListener {

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        // Add Server
        // Add Port
        // Add Application Code
        // Add Thread-Id
        // Add Audit ID + Audit Type

    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {

    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().getServerInfo()
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
