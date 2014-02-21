/*
 * Created 08.01.2011
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
	
	private final boolean isResolvable(Object base) {
		return base instanceof ReportDataSet;
	}

}
