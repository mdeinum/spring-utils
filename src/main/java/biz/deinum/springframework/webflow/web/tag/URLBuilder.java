package biz.deinum.springframework.webflow.web.tag;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.springframework.util.StringUtils;

public final class URLBuilder {

	private String baseURL;

	private Map paramMap = new HashMap();

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

	public URLBuilder addParam(String key, String value) {
		paramMap.put(key, value);
		return this;
	}

	public Map getParams() {
		return Collections.unmodifiableMap(paramMap);
	}

	public String getBaseURL() {
		return baseURL;
	}

	public String getURL() {
		String url = baseURL;
		boolean first = true;

		Iterator entrySetIter = paramMap.entrySet().iterator();

		while (entrySetIter.hasNext()) {
			Entry entry = (Entry) entrySetIter.next();
			if (first) {
				first = false;
				url += "?" + entry.getKey() + "=" + entry.getValue();
			} else {
				url += "&" + entry.getKey() + "=" + entry.getValue();
			}
			
		}
		return url;
	}
}
