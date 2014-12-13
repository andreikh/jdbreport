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

import jdbreport.model.Cell;

/**
 * 
 * Interface for cell in TemplateReport
 * 
 * @version 2.0 30.01.2012
 * @author Andrey Kholmanskih
 * 
 */
public interface CellObject extends Cell {

	public final static int TYPE_NONE = 0;

	public final static int TYPE_VAR = 5;

	public final static int TYPE_FIELD = 6;

	/**
	 * No total function
	 */
	public final static int AF_NONE = 0;

	/**
	 * Total function Sum
	 */
	public final static int AF_SUM = 1;

	/**
	 * Total function Max
	 */
	public final static int AF_MAX = 2;

	/**
	 * Total function Min
	 */
	public final static int AF_MIN = 3;

	/**
	 * Total function Avg
	 */
	public final static int AF_AVG = 4;

	/**
	 * Text representation total functions
	 */
	public final static String[] AGR_FUNC_NAME = {
			null,
			Messages.getString("CellObject.0"), Messages.getString("CellObject.1"), Messages.getString("CellObject.2"), Messages.getString("CellObject.3") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$


	/**
	 * @return the field's name
	 */
	public String getFieldName();

	/**
	 * 
	 * @param name the field's name
	 */
	public void setFieldName(String name);

	/**
	 * @return the DataSet's alias
	 */
	public String getDataSetId();

	/**
	 * 
	 * @param dataSetId the DataSet's alias
	 */
	public void setDataSetId(String dataSetId);

	/**
	 * @return Data type (TYPE_VAR, TYPE_FIELD or TYPE_NONE)
	 */
	public int getType();

	/**
	 * @param type Data type (TYPE_VAR, TYPE_FIELD or TYPE_NONE)
	 */
	public void setType(int type);

	/**
	 * @return Not repeated value for column
	 */
	public boolean isNotRepeate();

	/**
	 * 
	 * @param notRepeate not repeated value for column
	 */
	public void setNotRepeate(boolean notRepeate);

	public Object clone();

	/**
	 * Creates the Cell object and copies its properties there
	 * 
	 * @return the Cell object
	 */
	public Cell createCellItem();

	/**
	 * Name of the CellFunction object
	 * @return name of the CellFunction object
	 */
	public String getFunctionName();

	/**
	 * Sets the name of the CellFunction object
	 * @param functionName name of the CellFunction object
	 */
	public void setFunctionName(String functionName);

	/**
	 * @return the total function: Sum, Max, Min or Avg
	 */
	public int getTotalFunction();

	/**
	 * 
	 * @param func 
	 *           the total function: Sum, Max, Min or Avg
	 */
	public void setTotalFunction(int func);

	/**
	 * Remembers the current meaning of the generated cell
	 * 	   
	 * @param oldValue the current meaning
	 */
	public void setOldValue(Object oldValue);
	
	/**
	 * Compares the meaning of the new generated cell with saved meaning
	 * 
	 * @param value the new meaning
	 * @return true if equal
	 */
	public boolean isOldEquals(Object value);


	/**
	 * A list of expressions contained in cells
	 * 
	 * @return a list of expressions contained in cells
	 */
	public Expression[] getExpressions();
	
	/**
	 * 
	 * @param expr a list of expressions
	 */
	public void setExpressions(Expression[] expr);

	/**
	 * A list of DataSet aliases contained in the cell
	 * @return the aliases of DataSet
	 */
	public String[] getDataSetIds();
	
	/**
	 * A list of field items contained in the cell for DataSet named dsId
	 * @param dsId the dataset's alias
	 * @return a list of field items
	 */
	public String[] getFieldNames(String dsId);
	
	/**
	 * 
	 * @return true if the cell is set to change
	 * @since 2.0
	 */
	public boolean isReplacement();

	/**
	 * Sets the sign of the replacement value of the cell
	 * @param b sign of the replacement
	 * @since 2.0
	 */
	public void setReplacement(boolean b);
}
