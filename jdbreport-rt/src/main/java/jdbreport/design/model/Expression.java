/*
 * Expression.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2008-2014 Andrey Kholmanskih
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

import jdbreport.model.ReportException;

/**
 * Interface is used to calculate expressions in the CellObject.<br>
 * CellObject can contain several expressions, for instance:
 * <i><b>Customer: ${customer.name}</b></i> contains two expressions:
 * <i><b>Customer: </b><i> and  <i><b>${customer.name}</b></i>.
 *
 * @version 2.0 10.01.2011
 * @author Andrey Kholmanskih
 *
 */
public interface Expression {

	/**
	 * Text of the expression
	 * @return text of the expression
	 */
	public String getText();
	
	/**
	 * The name of basic object
	 * @return the name of object
	 */
	public String getBaseName();
	
	/**
	 * The name of property
	 * @return the name of property
	 * @since 2.0
	 */
	public String getProperty();
	
	/**
	 * The meaning of the expression
	 * @return the meaning of the expression
	 * @throws ReportException
	 */
	public Object getValue() throws ReportException;
	
	/**
	 * The type of the expression
	 * Can accept the meanings: CellObject.TYPE_NONE, 
	 * CellObject.TYPE_VAR, CellObject.TYPE_FIELD
	 * @return the type of the expression
	 */
	public int getType();
	
	/**
	 * Returns a formatted string
	 * @return formatted string
	 * @throws ReportException 
	 * @since 1.4
	 */
	public String getFormatValue() throws ReportException;
}
