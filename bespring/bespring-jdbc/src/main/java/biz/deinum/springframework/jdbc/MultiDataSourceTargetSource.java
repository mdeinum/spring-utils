package biz.deinum.springframework.jdbc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.TargetSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;

import biz.deinum.springframework.core.ContextHolder;

/**
 * TargetSource which returns the correct datasource based on the current context set in the {@link biz.deinum.springframework.core.ContextHolder}. 
 * If no context is found an error is thrown or the <code>defaultDataSource</code> is returned
 * , depending on the setting of the alwaysReturnDataSource property (default is false);
 * 
 * @author M. Deinum
 * @see ContextHolder
 * @see TargetSource
 */
public class MultiDataSourceTargetSource implements TargetSource, InitializingBean {

    private static final Log logger = LogFactory.getLog(MultiDataSourceTargetSource.class); 
    private DataSource defaultDataSource;
    private final Map datasources = Collections.synchronizedMap(new HashMap());
    private boolean alwaysReturnDataSource = false;

    /**
     * Sets the map of SessionFactory objects. Clear the map first and then
     * putAll the sessionFactories in the map. We do this because we want to
     * keep the synchronized map.
     * 
     * @param sessionFactoryMap
     *            the SessionFactory map.
     */
    public void setDatasources(final Map datasources) {
        this.datasources.clear();
        this.datasources.putAll(datasources);
    }

    /**
     * Locate and return the sessionfactory for the current context.
     * 
     * First we lookup the context name from the
     * <code>ContextHolder</code> this context name is used to lookup
     * the desired sessionfactory. When none is found we return the default
     * sessionfactory, which should always be configured!
     * 
     * @see ContextHolder
     */
    public Object getTarget() throws Exception {
        // Determine the current context name from theclass that holds the
        // context name for the current thread.
        String contextName = ContextHolder.getContext();
        if (logger.isTraceEnabled()) {
        	logger.trace("Current context: '" + contextName + "'");
        }
        
        Object datasource = datasources.get(contextName);
        if (datasource == null && alwaysReturnDataSource) {
        	if (logger.isTraceEnabled()) {
        		logger.trace("Return default datasource for context '"+contextName+"'");
        	}
    		datasource = defaultDataSource;
        } else {
        	throw new DataSourceLookupFailureException("Cannot locate a datasource for context '"+contextName+"'");
        }
        return datasource;
    }

    /**
     * {@inheritDoc}
     */
    public Class getTargetClass() {
        return DataSource.class;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isStatic() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void releaseTarget(Object arg0) throws Exception {
        // no-op
    }
    
    /**
     * Sets the defaultDataSource to use. 
     * setting a new value.
     * 
     * @param defaultDataSource
     */
    public void setDefaultDataSource(final DataSource defaultDataSource) {
        this.defaultDataSource=defaultDataSource;
    }
    
    /**
     * Sets the <code>alwaysReturnDataSource</code> property. Default is <b>false</b>.
     * 
     * @param alwaysReturnDataSource
     */
    public void setAlwaysReturnDataSource(final boolean alwaysReturnDataSource) {
    	this.alwaysReturnDataSource=alwaysReturnDataSource;
    }

	public void afterPropertiesSet() throws Exception {
		if (alwaysReturnDataSource && defaultDataSource == null) {
			throw new IllegalStateException("The defaultDataSource is null, while alwaysReturnDataSource is set to true. " +
					"When alwaysReturnDataSource is set to true a defaultDataSource must be set!");
		}
	}

}
