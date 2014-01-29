package biz.deinum.web.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.*;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Session listener to debug session creation/passivation/assignment.
 *
 * @author Marten Deinum
 *
 * @see HttpSessionListener
 * @see HttpSessionActivationListener
 * @see HttpSessionAttributeListener
 */
public class HttpSessionDebugListener implements HttpSessionListener, HttpSessionActivationListener, HttpSessionAttributeListener {

    private final Logger logger = LoggerFactory.getLogger(HttpSessionDebugListener.class);

    @Override
    public void sessionWillPassivate(HttpSessionEvent se) {
        logger.debug("Session [{}] will be passivated.", se.getSession().getId());
    }

    @Override
    public void sessionDidActivate(HttpSessionEvent se) {
        logger.debug("Session [{}] was activated.", se.getSession().getId());
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        logger.debug("Attribute {} : {} added to session [{}].", event.getName(), event.getValue(), event.getSession().getId());
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        logger.debug("Attribute {} : {} removed from session [{}].", event.getName(), event.getValue(), event.getSession().getId());
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        logger.debug("Attribute {} : {} replaced in session [{}].", event.getName(), event.getValue(), event.getSession().getId());
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        logger.debug("Session [{}] created.", se.getSession().getId());
    }

    /**
     * Logs if a session timed out or not.
     *
     * Timeout detection is a best effort and isn't 100% reliable!
     *
     * @param se
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        long current = System.currentTimeMillis();
        long lastAccessed = session.getLastAccessedTime();
        long interval = SECONDS.toMillis(session.getMaxInactiveInterval());
        boolean timedOut = (current - lastAccessed) > interval;

        logger.debug("Session [{}] destroyed [timeout: {}].", se.getSession().getId(), timedOut);
    }
}
