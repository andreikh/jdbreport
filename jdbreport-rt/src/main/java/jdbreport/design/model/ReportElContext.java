/*
 * Created 10.01.2011
 *
 * Copyright (C) 2011 Andrey Kholmanskih. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, write to the 
 *
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
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