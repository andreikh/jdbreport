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
