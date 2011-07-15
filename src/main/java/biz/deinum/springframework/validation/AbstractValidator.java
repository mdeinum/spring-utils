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
package biz.deinum.springframework.validation;

import org.springframework.validation.Validator;

public abstract class AbstractValidator implements Validator {
	/** Prefix Strings which are added to the field names */
	protected static final String REQUIRED = "required";
	protected static final String INVALID = "invalid";

	protected boolean allowEmpty = false;

	public final void setAllowEmpty(final boolean allowEmpty) {
		this.allowEmpty = allowEmpty;
	}
}
