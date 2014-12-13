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
