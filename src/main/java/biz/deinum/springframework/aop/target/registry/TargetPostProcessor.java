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
package biz.deinum.springframework.aop.target.registry;

/**
 * Implementors of this interface have the opportunity to modify some final
 * settings on the target.
 *  
 * @author Marten Deinum
 *
 * @see biz.deinum.springframework.aop.target.ContextSwappableTargetSource
 *
 */
public interface TargetPostProcessor {

	/**
	 * true if this <em>TargetPostProcessor</em> supports this type of target.
	 * 
	 * @param class1
	 * @return
	 */
	boolean supports(Class class1);

	/**
	 * Do some post processing on the given <code>target</code>.
	 * @param target
	 */
	void postProcess(Object target);

}
