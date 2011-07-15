/*
 * Copyright 2007-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

	private final Map<String, String> paramMap = new LinkedHashMap<String, String>();

	public URLBuilder(final String baseURL) {
		super();
		this.parseUrl(baseURL);

	}

	private void parseUrl(final String url) {
		int num = url.indexOf("?");
		String params = "";

		if (num > 0) {
			this.baseURL = url.substring(0, num);
			params = url.substring(++num);
		} else {
			this.baseURL = url;
		}

		if (StringUtils.hasLength(params)) {
			final StringTokenizer tokenizer = new StringTokenizer(params, "&");
			while (tokenizer.hasMoreTokens()) {
				final String parameter = tokenizer.nextToken();
				int index = parameter.indexOf("=");
				final String key = parameter.substring(0, index);
				final String value = parameter.substring(++index);
				this.paramMap.put(key, value);
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
	public URLBuilder addParam(final String key, final String value) {
		this.paramMap.put(key, value);
		return this;
	}

	public Map<String, String> getParams() {
		return Collections.unmodifiableMap(this.paramMap);
	}

	public String getBaseURL() {
		return this.baseURL;
	}

	public String getURL() {
		final StringBuilder builder = new StringBuilder(this.baseURL);
		char c = '?';
		for (final Entry<String, String> entry : this.paramMap.entrySet()) {
			builder.append(c).append(entry.getKey()).append('=').append(entry.getValue());
			c = '&';
		}
		return builder.toString();
	}
}
