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
