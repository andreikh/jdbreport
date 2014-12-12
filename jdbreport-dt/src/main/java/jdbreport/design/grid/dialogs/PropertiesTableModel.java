/*
 * Copyright (C) 2006 Andrey Kholmanskih. All rights reserved.
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
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
 * 
 */
package jdbreport.design.grid.dialogs;

import jdbreport.design.grid.*;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

/**
 * TableModel for properties
 * 
 * @author Andrey Kholmanskih
 * @version 1.0, 12/27/06
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
			if (prop.name != null && !"".equals(prop.name) //$NON-NLS-1$
					&& !properties.containsKey(prop.name)
					&& prop.value != null
					&& !"".equals(prop.value)) { //$NON-NLS-1$
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
