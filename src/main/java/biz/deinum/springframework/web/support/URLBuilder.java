package biz.deinum.springframework.web.support;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.springframework.util.StringUtils;

/**
 * Helper class to parse and create URL's with parameters.
 * 
 * @author Marten Deinum
 * @since 1.0.0
 *
 */
public final class URLBuilder {

	private String baseURL;

	private Map<String, String> paramMap = new LinkedHashMap<String, String>();

	public URLBuilder(String baseURL) {
		super();
		parseUrl(baseURL);

	}

	private void parseUrl(String url) {
		int num = url.indexOf("?");
		String params = "";

		if (num > 0) {
			baseURL = url.substring(0, num);
			params = url.substring(++num);
		} else {
			baseURL = url;
		}

		if (StringUtils.hasLength(params)) {
			final StringTokenizer tokenizer = new StringTokenizer(params, "&");
			while (tokenizer.hasMoreTokens()) {
				String parameter = tokenizer.nextToken();
				int index = parameter.indexOf("=");
				String key = parameter.substring(0, index);
				String value = parameter.substring(++index);
				paramMap.put(key, value);
			}
		}
	}

	/**
	 * Add a parameter to the url. Can be used to build urls.
	 * 
	 * @param key the name of the parameter
	 * @param value the value
	 * @return return this 
	 */
	public URLBuilder addParam(String key, String value) {
		paramMap.put(key, value);
		return this;
	}

	public Map<String, String> getParams() {
		return Collections.unmodifiableMap(paramMap);
	}

	public String getBaseURL() {
		return baseURL;
	}

	public String getURL() {
		final StringBuilder builder = new StringBuilder(baseURL);
		char c = '?';
		for (Entry<String, String> entry : paramMap.entrySet()) {
			builder.append(c).append(entry.getKey()).append('=').append(entry.getValue());
			c = '&';
		}
		return builder.toString();
	}
}
