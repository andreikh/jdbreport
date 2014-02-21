/*
 * Expression.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2008-2011 Andrey Kholmanskih. All rights reserved.
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
 */

package jdbreport.design.model;

import jdbreport.model.ReportException;

/**
 * Interface is used to calculate expressions in the CellObject.<br>
 * CellObject can contain several expressions, for instance:
 * <i><b>Customer: ${customer.name}</b></i> contains two expressions:
 * <i><b>Customer: </b><i> and  <i><b>${customer.name}</b></i>.
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
