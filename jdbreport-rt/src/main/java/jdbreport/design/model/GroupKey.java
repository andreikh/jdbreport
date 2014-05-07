/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2009 Andrey Kholmanskih. All rights reserved.
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

/**
 * Object for working with the group's keys
 * 
 * @version 1.3 03.08.2009
 * @author Andrey Kholmanskih
 *
 */
public class GroupKey implements Cloneable {

	private String name;
	private String datasetID;
	private Object value;
	
	public GroupKey() {
		super();
	}

	/**
	 * 
	 * @param name key name
	 */
	public GroupKey(String name) {
		super();
		this.name = name;
	}

	public GroupKey(String name, String datasetID) {
		this(name);
		setDatasetID(datasetID);
	}
	
	/**
	 * Returns the type of the key.
	 * It can accept the meanings TYPE_VAR or TYPE_FIELD
	 * 
	 * @return the type.
	 */
	public int getType() {
		return (datasetID == null) ? CellObject.TYPE_VAR : CellObject.TYPE_FIELD;
	}


	/**
	 * Returns the name of the key.
	 * 
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * Sets name of the key 
	 * 	  
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Sets alias DataSet for the key 
	 * @param datasetID The datasetID to set.
	 */
	public void setDatasetID(String datasetID) {
		if (datasetID != null && datasetID.trim().length() == 0)
			this.datasetID = null;
		else
			this.datasetID = datasetID;
	}

	/**
	 * Returns the alias DataSet for the key.
	 * 
	 * @return Returns the datasetID.
	 */
	public String getDatasetID() {
		return datasetID;
	}

	/**
	 * Returns value of the key.
	 * @return value of the key
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Sets value of the key.
	 * @param value key value
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	
	public Object clone() {
			GroupKey key = new GroupKey(); 
			key.name = name;
			key.datasetID = datasetID;
			return key;
	}
}
