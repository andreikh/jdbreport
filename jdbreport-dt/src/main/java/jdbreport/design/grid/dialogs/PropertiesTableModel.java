/*
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
package jdbreport.design.grid.dialogs;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

/**
 * TableModel for properties
 * 
 * @author Andrey Kholmanskih
 * @version 3.0 12.12.2014
 */
public class PropertiesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private ArrayList<Property> values;
	private boolean editable;

	public PropertiesTableModel(Hashtable<Object, Object> properties) {
		this(properties, true);
	}
	
	public PropertiesTableModel(Hashtable<Object, Object> properties, boolean editable) {
		values = new ArrayList<Property>();
		this.editable = editable;
		setProperties(properties);
	}

	public void setProperties(Hashtable<Object, Object> properties) {
		values.clear();
		if (properties == null)
			return;
		for (Object key : properties.keySet()) {
			Object value  =  properties.get(key);
			values.add(new Property(key.toString(), value == null ? null : value.toString()));
		}
	}

	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		return values.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Property prop =  values.get(rowIndex);
		return columnIndex == 0 ? prop.name : prop.value;
	}

	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}
	
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Property prop = values.get(rowIndex);
		if (columnIndex == 0) {
			if (aValue == null) 
				prop.name = "";  //$NON-NLS-1$
			else
				prop.name = aValue.toString();
		} else {
			if (aValue == null) 
				prop.value = "";  //$NON-NLS-1$
			else
				prop.value = aValue.toString();
		}
	}

	public String getColumnName(int columnIndex) {
		return columnIndex == 0 ? jdbreport.design.grid.Messages.getString("PropertiesTableModel.2") : jdbreport.design.grid.Messages.getString("PropertiesTableModel.3"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return editable;
	}
	
	public void addRow() {
		values.add(new Property("", "")); //$NON-NLS-1$ //$NON-NLS-2$
		fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
	}

	public void delRow(int rowIndex, int rowEndIndex) {
		int index = rowEndIndex;
		while (index >= rowIndex) {
			values.remove(index);
			index--;
		}
		fireTableRowsDeleted(rowIndex, rowEndIndex);
	}

	public Hashtable<Object, Object> getProperties() {
		Hashtable<Object, Object> properties = new Hashtable<Object, Object>();
		return getProperties(properties);
	}
	
	public Hashtable<Object, Object> getProperties(Hashtable<Object, Object> properties) {
		properties.clear();
		for (int i = 0; i < values.size(); i++) {
			Property prop = values.get(i);
			if (prop.name != null && !"".equals(prop.name)
					&& !properties.containsKey(prop.name)
					&& prop.value != null
					&& !"".equals(prop.value)) {
				if (properties instanceof Properties) 
					((Properties)properties).setProperty(prop.name, prop.value);
				else	
					properties.put(prop.name, prop.value);
			}
		}
		return properties;
	}
	
	private static class Property {
		String name;
		String value;

		public Property(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}

	
}
