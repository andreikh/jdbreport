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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;

/**
 * @author Andrey Kholmanskih
 *
 * @version 1.0 11.01.2011
 */
public class VarResolver extends ELResolver{

	private final Map<Object, Object> map = Collections.synchronizedMap(new HashMap<>());

	public VarResolver() {
		
	}
	
	@Override
	public Class<?> getCommonPropertyType(ELContext context, Object base) {
		return isResolvable(context) ? Object.class : null;
	}

	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context,
			Object base) {
		return null;
	}

	@Override
	public Class<?> getType(ELContext context, Object base, Object property) {
		return resolve(context, base, property) ? Object.class : null;
	}

	@Override
	public Object getValue(ELContext context, Object base, Object property) {
		if (resolve(context, base, property)) {
			if (!map.containsKey(property)) {
				throw new PropertyNotFoundException("Cannot find property " + property);
			}
			return getProperty(property);
		}
		return null;
	}

	@Override
	public boolean isReadOnly(ELContext context, Object base, Object property) {
		return false;
	}

	@Override
	public void setValue(ELContext context, Object base, Object property,
			Object value) {
		if (resolve(context, base, property)) {
			map.put(property, value);
		}
	}

	public Object getProperty(Object property) {
		return map.get(property);
	}
	
	private boolean isResolvable(Object base) {
		return base == null;
	}
	
	private boolean resolve(ELContext context, Object base, Object property) {
		context.setPropertyResolved(isResolvable(base));
		return context.isPropertyResolved();
	}

}
