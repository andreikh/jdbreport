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
 * @version 3.1 14.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public interface CellObject extends Cell {

	/**
	 * No total function
	 */
	int AF_NONE = 0;

	/**
	 * Total function Sum
	 */
	int AF_SUM = 1;

	/**
	 * Total function Max
	 */
	int AF_MAX = 2;

	/**
	 * Total function Min
	 */
	int AF_MIN = 3;

	/**
	 * Total function Avg
	 */
	int AF_AVG = 4;

	/**
	 * Text representation total functions
	 */
	String[] AGR_FUNC_NAME = {
			null,
			Messages.getString("CellObject.sum"), Messages.getString("CellObject.max"),
			Messages.getString("CellObject.min"), Messages.getString("CellObject.avg") };
	/**
	 * @return Not repeated value for column
	 */
	boolean isNotRepeat();

	/**
	 * 
	 * @param notRepeat not repeated value for column
	 */
	void setNotRepeat(boolean notRepeat);

	Object clone();

	/**
	 * Creates the Cell object and copies its properties there
	 * 
	 * @return the Cell object
	 */
	Cell createCellItem();

	/**
	 * Name of the CellFunction object
	 * @return name of the CellFunction object
	 */
	String getFunctionName();

	/**
	 * Sets the name of the CellFunction object
	 * @param functionName name of the CellFunction object
	 */
	void setFunctionName(String functionName);

	/**
	 * @return the total function: Sum, Max, Min or Avg
	 */
	int getTotalFunction();

	/**
	 * 
	 * @param func 
	 *           the total function: Sum, Max, Min or Avg
	 */
	void setTotalFunction(int func);

	/**
	 * Remembers the current meaning of the generated cell
	 * 	   
	 * @param oldValue the current meaning
	 */
	void setOldValue(Object oldValue);
	
	/**
	 * Compares the meaning of the new generated cell with saved meaning
	 * 
	 * @param value the new meaning
	 * @return true if equal
	 */
	boolean isOldEquals(Object value);


	/**
	 * A list of expressions contained in cells
	 * 
	 * @return a list of expressions contained in cells
	 */
	Expression[] getExpressions();
	
	/**
	 * 
	 * @param expr a list of expressions
	 */
	void setExpressions(Expression[] expr);

	/**
	 * A list of DataSet aliases contained in the cell
	 * @return the aliases of DataSet
	 */
	String[] getDataSetIds();
	
	/**
	 * A list of field items contained in the cell for DataSet named dsId
	 * @param dsId the dataset's alias
	 * @return a list of field items
	 */
	String[] getFieldNames(String dsId);
	
	/**
	 * 
	 * @return true if the cell is set to change
	 * @since 2.0
	 */
	boolean isReplacement();

	/**
	 * Sets the sign of the replacement value of the cell
	 * @param b sign of the replacement
	 * @since 2.0
	 */
	void setReplacement(boolean b);
}
