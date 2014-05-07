/*
 * Created 11.01.2011
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
