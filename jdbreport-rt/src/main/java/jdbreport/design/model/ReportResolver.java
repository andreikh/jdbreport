/*
 * Created 11.01.2011
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

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;

/**
 * @author Andrey Kholmanskih
 *
 * @version 1.0 11.01.2011
 */
public class ReportResolver  extends ELResolver {

	private final CompositeELResolver delegate;

		public ReportResolver() {
			delegate = new CompositeELResolver();
			delegate.add(new VarResolver());
			delegate.add(new DataSetResolver());
			delegate.add(new ArrayELResolver(false));
			delegate.add(new ListELResolver(false));
			delegate.add(new MapELResolver(false));
			delegate.add(new ResourceBundleELResolver());
			delegate.add(new BeanELResolver(false));
		}

	
		@Override
		public Class<?> getCommonPropertyType(ELContext context, Object base) {
			return delegate.getCommonPropertyType(context, base);
		}

		@Override
		public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
			return delegate.getFeatureDescriptors(context, base);
		}

		@Override
		public Class<?> getType(ELContext context, Object base, Object property) {
			return delegate.getType(context, base, property);
		}

		@Override
		public Object getValue(ELContext context, Object base, Object property) {
			return delegate.getValue(context, base, property);
		}

		@Override
		public boolean isReadOnly(ELContext context, Object base, Object property) {
			return delegate.isReadOnly(context, base, property);
		}

		@Override
		public void setValue(ELContext context, Object base, Object property, Object value) {
			delegate.setValue(context, base, property, value);
		}

		@Override
		public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
			return delegate.invoke(context, base, method, paramTypes, params);
		}
	
}
