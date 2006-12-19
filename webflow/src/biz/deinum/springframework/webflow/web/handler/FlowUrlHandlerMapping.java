package biz.deinum.springframework.webflow.web.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.util.Assert;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.webflow.executor.mvc.FlowController;


public class FlowUrlHandlerMapping extends AbstractUrlHandlerMapping {
	private Logger logger = LoggerFactory.getLogger(FlowUrlHandlerMapping.class);
	private final Map urlMap = new HashMap();
	private FlowController controller;
	
	public void setMappings(final Map mappings) {
		this.urlMap.putAll(mappings);
	}
	
	public void setFlowController(final FlowController flowController) {
		this.controller = flowController;
	}
	
	protected void initApplicationContext() throws BeansException {
		Assert.notNull(controller, "FlowController must be set.");
		super.initApplicationContext();
		registerHandlers(this.urlMap);
	}

	protected Object lookupHandler(String urlPath, HttpServletRequest request) {
		Object flow = super.lookupHandler(urlPath, request);
		Object handler = null;
		if (flow != null) {
			request.setAttribute(controller.getArgumentHandler().getFlowIdArgumentName(), flow);
			handler=controller;
		}
		return handler;
	}
	
	protected void registerHandlers(Map urlMap) throws BeansException {
		if (urlMap.isEmpty()) {
			logger.warn("Neither 'urlMap' nor 'mappings' set on SimpleUrlHandlerMapping");
		}
		else {
			Iterator it = urlMap.keySet().iterator();
			while (it.hasNext()) {
				String url = (String) it.next();
				Object handler = urlMap.get(url);
				// Prepend with slash if not already present.
				if (!url.startsWith("/")) {
					url = "/" + url;
				}
				registerHandler(url, handler);
			}
		}
	}

}
