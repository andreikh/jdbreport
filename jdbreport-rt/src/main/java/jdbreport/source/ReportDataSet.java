/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2010 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.source;

import java.util.Collection;

import jdbreport.model.ReportException;

import and.dbcomp.DataSetParams;

/**
 * @version 1.4 15.03.2010
 * @author Andrey Kholmanskih
 * 
 */
public interface ReportDataSet extends Cloneable {

	/**
	 * 
	 * @return DataSet id
	 */
	String getId();

	/**
	 * Moves the cursor on the next record (object) in the DataSet
	 * 
	 * @return true if successful
	 * @throws ReportException
	 */
	boolean next() throws ReportException;

	/**
	 * Returns value from the current record (object) by the name
	 * 
	 * @param name -
	 *            the column's name or the property's name
	 * @return value by the name
	 * @throws ReportException
	 */
	Object getValue(String name) throws ReportException;

	/**
	 * Returns names' collection of columns or properties
	 * 
	 * @return names of columns or properties
	 * @throws ReportException
	 */
	Collection<String> getColumnNames() throws ReportException;

	/**
	 * Returns a current object, can be null
	 * 
	 * @return - current object
	 */
	Object getCurrentObject();

	/**
	 * Returns parameters
	 * 
	 * @return parameters
	 */
	DataSetParams getParams() throws ReportException;

	/**
	 * Returns id of the leading DataSet
	 * 
	 * @return masterId property
	 */
	String getMasterId();

	/**
	 * Reopens the DataSet when the MasterDataSet changes parameters
	 * @return true if not eof
	 * @throws ReportException
	 */
	boolean reopen() throws ReportException;

	Object clone();

    boolean hasNext(); 
}
