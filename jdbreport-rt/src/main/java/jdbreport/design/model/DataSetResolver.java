/*
 * Created 08.01.2011
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

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;

import jdbreport.model.ReportException;
import jdbreport.source.ReportDataSet;

/**
 * @author Andrey Kholmanskih
 *
 * @version 1.0 08.01.2011
 */
public class DataSetResolver extends ELResolver {


	/* (non-Javadoc)
	 * @see javax.el.ELResolver#getCommonPropertyType(javax.el.ELContext, java.lang.Object)
	 */
	@Override
	public Class<?> getCommonPropertyType(ELContext context, Object base) {
		return isResolvable(base) ? Object.class : null;
	}

	/* (non-Javadoc)
	 * @see javax.el.ELResolver#getFeatureDescriptors(javax.el.ELContext, java.lang.Object)
	 */
	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context,
			Object base) {
		if (isResolvable(base)) {
			final Iterator<String> keys;
			try {
				keys = ((ReportDataSet) base).getColumnNames().iterator();
			} catch (ReportException e) {
				throw new IllegalArgumentException();
			}
			return new Iterator<FeatureDescriptor>() {
				public boolean hasNext() {
					return keys.hasNext();
				}
				public FeatureDescriptor next() {
					FeatureDescriptor feature = new FeatureDescriptor();
					feature.setDisplayName(keys.next());
					feature.setName(feature.getDisplayName());
					feature.setShortDescription("");
					feature.setExpert(true);
					feature.setHidden(false);
					feature.setPreferred(true);
					feature.setValue(TYPE, Object.class);
					feature.setValue(RESOLVABLE_AT_DESIGN_TIME, true);
					return feature;
				}
				public void remove() {
					throw new UnsupportedOperationException("Cannot remove");
					
				}
			};
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.el.ELResolver#getType(javax.el.ELContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public Class<?> getType(ELContext context, Object base, Object property) {
		if (context == null) {
			throw new NullPointerException("context is null");
		}
		if (isResolvable(base)) {
			context.setPropertyResolved(true);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.el.ELResolver#getValue(javax.el.ELContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object getValue(ELContext context, Object base, Object property) {
		if (context == null) {
			throw new NullPointerException("context is null");
		}
		Object result = null;
		if (isResolvable(base)) {
			if (property != null) {
				try {
					result = ((ReportDataSet) base).getValue(property.toString());
				} catch (ReportException e) {
					result = "";
				}
			}
			context.setPropertyResolved(true);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see javax.el.ELResolver#isReadOnly(javax.el.ELContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isReadOnly(ELContext context, Object base, Object property) {
		if (context == null) {
			throw new NullPointerException("context is null");
		}
		if (isResolvable(base)) {
			context.setPropertyResolved(true);
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.el.ELResolver#setValue(javax.el.ELContext, java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setValue(ELContext context, Object base, Object property,
			Object value) {
		if (context == null) {
			throw new NullPointerException("context is null");
		}
		if (isResolvable(base)) {
			throw new PropertyNotWritableException("resolver is read-only");
		}
	}
	
	private boolean isResolvable(Object base) {
		return base instanceof ReportDataSet;
	}

}
