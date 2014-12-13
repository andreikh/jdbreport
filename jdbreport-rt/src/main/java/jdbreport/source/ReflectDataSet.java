/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2014 Andrey Kholmanskih
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
package jdbreport.source;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import java.lang.reflect.*;

import jdbreport.model.ReportException;

/**
 * @version 2.0 02.12.2011
 * @author Andrey Kholmanskih
 * 
 */
public abstract class ReflectDataSet extends AbstractDataSet {

	private Map<String, Object> columnMap;
	private Class<? extends Object> objectClass;
	protected Object current;

	public ReflectDataSet(String id) {
		super(id);
	}

	protected void reflect(Object o) {
		columnMap = new TreeMap<String, Object>();
		if (o == null) {
			return;
		}
		objectClass = o.getClass();
		Field[] f = objectClass.getFields();
		for (int i = 0; i < f.length; i++) {
			String name = f[i].getName();
			if (!columnMap.containsKey(name)) {
				columnMap.put(name, f[i]);
			}
		}
		Method[] m = objectClass.getMethods();
		for (int i = 0; i < m.length; i++) {
			if (m[i].getParameterTypes().length == 0) {
				String name = m[i].getName();
				if (name.startsWith("get") && name.length() > 3) {
					name = name.substring(3, 4).toLowerCase()
							+ name.substring(4);
					if (!columnMap.containsKey(name)) {
						columnMap.put(name, m[i]);
					}
				} else if (name.startsWith("is") && name.length() > 2) {
					name = name.substring(2, 3).toLowerCase()
							+ name.substring(3);
					if (!columnMap.containsKey(name)) {
						columnMap.put(name, m[i]);
					}
				}
			}
		}
	}

	protected Map<String, Object> getColumnMap() {
		return columnMap;
	}

	protected Class<? extends Object> getObjectClass() {
		return objectClass;
	}

	public Collection<String> getColumnNames() throws ReportException {
		return columnMap.keySet();
	}

	public Object getCurrentObject() {
		return current;
	}

	public Object getValue(String name) throws ReportException {
		try {
			Object o = getColumnMap().get(name);
			if (o == null)
				return null;
			if (o instanceof Method) {
				return ((Method) o).invoke(current, (Object[]) null);
			} else
				return ((Field) o).get(current);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error for value name=" + name, e);
		}
		return null;
	}

	public boolean hasNext() {
		return current != null;
	}

}
