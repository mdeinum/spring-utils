package biz.deinum.springframework.webflow.web.tag;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.executor.mvc.FlowController;
import org.springframework.webflow.executor.support.FlowExecutorArgumentHandler;

/**
 * Base class for all WebFlowTags.
 * 
 * @author M. Deinum (487)
 * 
 */
public abstract class AbstractWebFlowTag extends AbstractHtmlElementTag {
	private Logger logger = LoggerFactory.getLogger(AbstractWebFlowTag.class);

	private static FlowExecutorArgumentHandler argumentHandler;

	private ExternalContext context;

	public AbstractWebFlowTag() {
		super();
	}

	protected void initTag() throws Exception {
	}

	/**
	 * Do some initial setup, the first the the argumentHandler is retrieved
	 * from the FlowController. To do custom initializing override the
	 * {@link #initTag()} method.
	 */
	protected final void doSetupTag() throws Exception {
		if (argumentHandler == null) {
			logger.debug("Initializing 'argumentHandler'.");
			ApplicationContext ctx = RequestContextUtils
					.getWebApplicationContext(pageContext.getRequest());
			Map controllers = ctx.getBeansOfType(FlowController.class, false,
					false);
			if (controllers.isEmpty()) {
				throw new IllegalStateException(
						"Cannot retrieve ArgumentHandler no FlowController defined in scope.");
			}
			Object key = controllers.keySet().iterator().next();
			FlowController controller = (FlowController) controllers.get(key);
			argumentHandler = controller.getArgumentHandler();
			Assert.notNull(argumentHandler,
					"ArgumentHandler not set, error in configuration!");
			logger.debug("argumentHandler is set.");
		}
		initTag();
	}

	protected final FlowExecutorArgumentHandler getArgumentHandler() {
		return argumentHandler;
	}

	protected final String getFlowIdArgumentName() {
		return argumentHandler.getFlowIdArgumentName();
	}

	protected final String getFlowExecutionKeyAttributeName() {
		return argumentHandler.getFlowExecutionKeyAttributeName();
	}

	protected final String getFlowExecutionKeyArgumentName() {
		return argumentHandler.getFlowExecutionKeyArgumentName();
	}

	protected final String getEventIdArgumentName() {
		return argumentHandler.getEventIdArgumentName();
	}

	protected final String getFlowExecutionKey() {
		return argumentHandler.extractFlowExecutionKey(getContext());
	}

	/**
	 * Retrieves a ExternalContext based on the current pageContext. If not yet
	 * initialized it first initializes the <code>context</code> parameter.
	 * @return
	 */
	protected final ExternalContext getContext() {
		if (context == null) {
			logger.debug("Initializing ExternalContext");
			context = new ServletExternalContext(pageContext
					.getServletContext(), (HttpServletRequest) pageContext
					.getRequest(), (HttpServletResponse) pageContext
					.getResponse());
		}
		return context;
	}
}
