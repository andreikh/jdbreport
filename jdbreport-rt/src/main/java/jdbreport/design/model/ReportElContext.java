/*
 * Created 10.01.2011
 *
 * Copyright (C) 2011-2014 Andrey Kholmanskih
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
*/
package jdbreport.design.model;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.ValueExpression;
import javax.el.VariableMapper;


/**
 * 
 * @author Andrey Kholmanskih
 *
 * @version 1.0 04.03.2011
 */
public class ReportElContext extends ELContext {
	
	private Functions functions;
	private Variables variables;
	private ELResolver resolver;

	public ReportElContext() {
		super();
	}

	static class Functions extends FunctionMapper {
		Map<String, Method> map = new HashMap<String, Method>();

		@Override
		public Method resolveFunction(String prefix, String localName) {
			return map.get(prefix + ":" + localName);
		}

		public void setFunction(String prefix, String localName, Method method) {
			map.put(prefix + ":" + localName, method);
		}
	}

	static class Variables extends VariableMapper {
		Map<String, ValueExpression> map = new HashMap<String, ValueExpression>();

		@Override
		public ValueExpression resolveVariable(String variable) {
			return map.get(variable);
		}

		@Override
		public ValueExpression setVariable(String variable, ValueExpression expression) {
			return map.put(variable, expression);
		}
	}


	/**
	 * Define a function.
	 */
	public void setFunction(String prefix, String localName, Method method) {
		if (functions == null) {
			functions = new Functions();
		}
		functions.setFunction(prefix, localName, method);
	}

	public ValueExpression setVariable(String name, ValueExpression expression) {
		if (variables == null) {
			variables = new Variables();
		}
		return variables.setVariable(name, expression);
	}

	/**
	 * Get our function mapper.
	 */
	@Override
	public FunctionMapper getFunctionMapper() {
		if (functions == null) {
			functions = new Functions();
		}
		return functions;
	}

	@Override
	public VariableMapper getVariableMapper() {
		if (variables == null) {
			variables = new Variables();
		}
		return variables;
	}

	@Override
	public ELResolver getELResolver() {
		if (resolver == null) {
			resolver = new ReportResolver();
		}
		return resolver;
	}

	/**
	 * Set our resolver.
	 * 
	 * @param resolver
	 */
	public void setELResolver(ELResolver resolver) {
		this.resolver = resolver;
	}

}