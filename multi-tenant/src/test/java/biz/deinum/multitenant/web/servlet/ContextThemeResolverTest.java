package biz.deinum.multitenant.web.servlet;

import biz.deinum.multitenant.core.ContextHolder;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * @author marten
 */
public class ContextThemeResolverTest {

    private ContextThemeResolver resolver;

    @Before
    public void setup() {
        ContextHolder.clear();
        resolver = new ContextThemeResolver();
    }

    @Test
    public void whenNoContextSetReturnTheDefaultTheme() {
        String theme = resolver.resolveThemeName(null);
        assertThat(theme, is("theme"));
    }

    @Test
    public void whenContextSetReturnThatContextAsTheme() {
        ContextHolder.setContext("test-theme");

        String theme = resolver.resolveThemeName(null);
        assertThat(theme, is("test-theme"));
    }

    @Test
    public void whenContextSetReturnThatContextAsThemeWithPrefixAndSUffixApplied() {
        ContextHolder.setContext("test-theme");
        resolver.setSuffix("-after");
        resolver.setPrefix("before-");
        String theme = resolver.resolveThemeName(null);
        assertThat(theme, is("before-test-theme-after"));
    }


}