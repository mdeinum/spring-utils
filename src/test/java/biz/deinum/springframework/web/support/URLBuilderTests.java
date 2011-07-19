package biz.deinum.springframework.web.support;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for {@link URLBuilder}
 * 
 * @author Marten Deinum
 * 
 */
public class URLBuilderTests {

    private static final String BASE_URL = "http://www.here.com";

    @Test
    public void noParameters() {
        String url = "http://www.here.com";
        URLBuilder builder = new URLBuilder(BASE_URL);
        assertEquals(BASE_URL, builder.getURL());
        assertTrue(builder.getParams().isEmpty());
    }

    @Test
    public void noParameters2() {
        String url = "http://www.here.com?";
        URLBuilder builder = new URLBuilder(url);
        assertEquals(BASE_URL, builder.getBaseURL());
        assertEquals(BASE_URL, builder.getURL());
        assertTrue(builder.getParams().isEmpty());
    }

    @Test
    public void singleParameter() {
        String url = "http://www.here.com?param1=value1";
        URLBuilder builder = new URLBuilder(url);
        assertEquals(BASE_URL, builder.getBaseURL());
        assertEquals(url, builder.getURL());
        assertEquals(1, builder.getParams().size());
    }

    @Test
    public void twoParameter() {
        String url = "http://www.here.com?param1=value1&param2=value2";
        URLBuilder builder = new URLBuilder(url);
        assertEquals(BASE_URL, builder.getBaseURL());
        assertEquals(url, builder.getURL());
        assertEquals(2, builder.getParams().size());
    }

    @Test
    public void addSingleParameter() {
        String url = "http://www.here.com?param1=value1";
        URLBuilder builder = new URLBuilder(BASE_URL);
        assertEquals(BASE_URL, builder.getBaseURL());
        assertEquals(0, builder.getParams().size());
        builder.addParam("param1", "value1");
        assertEquals(BASE_URL, builder.getBaseURL());
        assertEquals(url, builder.getURL());
        assertEquals(1, builder.getParams().size());
    }

    @Test
    public void addTwoParameters() {
        String url = "http://www.here.com?param1=value1&param2=value2";
        URLBuilder builder = new URLBuilder(BASE_URL);
        assertEquals(0, builder.getParams().size());
        builder.addParam("param1", "value1");
        builder.addParam("param2", "value2");
        assertEquals(BASE_URL, builder.getBaseURL());
        assertEquals(url, builder.getURL());
        assertEquals(2, builder.getParams().size());
    }

}
