package biz.deinum.multitenant.aop.target.registry.impl;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

public class JndiLookupTargetRegistryTests {

    private static final String CONTEXT = "test-context";

    private SimpleNamingContextBuilder builder;

    private JndiLookupTargetRegistry targetRegistry = new JndiLookupTargetRegistry();

    @Before
    public void setup() throws Exception {

        targetRegistry = new JndiLookupTargetRegistry();

        builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        builder.bind(CONTEXT, new Date());
    }

    @After
    public void clean() throws Exception {
        builder.clear();
    }

    @Test
    public void notFound() {
        assertNull(targetRegistry.getTarget("not-existing"));
    }

    @Test
    public void found() {
        Object target = targetRegistry.getTarget(CONTEXT);
        assertTrue(target instanceof Date);
    }

    @Test
    public void prefixing() {
        String prefix = "prefix-";
        String suffix = "-suffix";
        targetRegistry.setPrefix(prefix);
        targetRegistry.setSuffix(suffix);
        String name = prefix + CONTEXT + suffix;

        assertEquals(name, targetRegistry.getJndiName(CONTEXT));
    }

}
