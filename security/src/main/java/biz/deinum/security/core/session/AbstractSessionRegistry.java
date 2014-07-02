package biz.deinum.security.core.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.session.SessionRegistry;

/**
 * Created with IntelliJ IDEA.
 * User: in329dei
 * Date: 11-12-13
 * Time: 14:56
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSessionRegistry implements SessionRegistry, ApplicationListener<SessionDestroyedEvent> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public final  void onApplicationEvent(SessionDestroyedEvent event) {
        String id = event.getId();
        logger.debug("SessionDestroyed [{}]", id);
        removeSessionInformation(id);
    }
}
