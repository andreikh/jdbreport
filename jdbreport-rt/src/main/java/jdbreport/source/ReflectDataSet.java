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
 * @version 3.1 15.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public abstract class ReflectDataSet extends AbstractDataSet {

	private Map<String, Object> columnMap;
	private Class<?> objectClass;
	protected Object current;

	public ReflectDataSet(String id) {
		super(id);
	}

	protected void reflect(Object o) {
		columnMap = new TreeMap<>();
		if (o == null) {
			return;
		}
		objectClass = o.getClass();
		Field[] f = objectClass.getFields();
		for (Field aF : f) {
			String name = aF.getName();
			if (!columnMap.containsKey(name)) {
				columnMap.put(name, aF);
			}
		}
		Method[] m = objectClass.getMethods();
		for (Method aM : m) {
			if (aM.getParameterTypes().length == 0
					&& aM.getReturnType() != Void.TYPE) {
				String name = aM.getName();
				if (name.startsWith("get") && name.length() > 3) {
					name = name.substring(3, 4).toLowerCase()
							+ name.substring(4);
					if (!columnMap.containsKey(name)) {
						columnMap.put(name, aM);
					}
				} else if (name.startsWith("is") && name.length() > 2
						&& aM.getReturnType() == Boolean.TYPE) {
					name = name.substring(2, 3).toLowerCase()
							+ name.substring(3);
					if (!columnMap.containsKey(name)) {
						columnMap.put(name, aM);
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
		return getValue(current, name);
	}

	public boolean hasNext() {
		return current != null;
	}

	@Override
	public Object getValue(Object current, String name) throws ReportException {
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


	@Override
	public boolean containsKey(String name) {
		return columnMap.containsKey(name);
	}


}
