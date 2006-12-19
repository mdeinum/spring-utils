package biz.deinum.springframework.orm.hibernate3;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.orm.toplink.SessionFactory;

import biz.deinum.springframework.core.ContextHolder;

/**
 * Create multiple SessionFactories and return the correct one for the current
 * company.
 * 
 * @author M. Deinum
 * @see TargetSource
 */

public class MultiSessionFactoryTargetSource implements TargetSource {

        private Logger logger = LoggerFactory.getLogger(MultiSessionFactoryTargetSource.class); 

        private String defaultSessionFactory = "ctb";

        private final Map sessionFactories = Collections.synchronizedMap(new HashMap());

        /**
         * Sets the map of SessionFactory objects. Clear the map first and then
         * putAll the sessionFactories in the map. We do this because we want to
         * keep the synchronized map.
         * 
         * @param sessionFactoryMap
         *            the SessionFactory map.
         */
        public void setSessionFactories(final Map sessionFactories) {
            this.sessionFactories.clear();
            this.sessionFactories.putAll(sessionFactories);
        }

        /**
         * Locate and return the sessionfactory for the current context.
         * 
         * First we lookup the context name from the
         * <code>ContextPathHolder</code> this context name is used to lookup
         * the desired sessionfactory. When none is found we return the default
         * sessionfactory, which should always be configured!
         * 
         * @see ContextPathHolder
         */
        public Object getTarget() throws Exception {
            // Determine the current context name from theclass that holds the
            // context name for the current thread.
            String contextName = ContextHolder.getContext();
            if (logger.isDebugEnabled()) {
                logger.debug("Current context: '" + contextName + "'");
            }
            
            Object sessionObject = sessionFactories.get(contextName);
            if (sessionObject == null) {
                sessionObject = sessionFactories.get(defaultSessionFactory);
                logger.debug("Returning default session factory.");
            }
            return sessionObject;
        }

        public Class getTargetClass() {
            return SessionFactory.class;
        }
        
        public boolean isStatic() {
            return false;
        }

        public void releaseTarget(Object arg0) throws Exception {
            // no-op
        }
        
        public void setDefault(final String defaultSessionFactory) {
            this.defaultSessionFactory=defaultSessionFactory;
        } 
}