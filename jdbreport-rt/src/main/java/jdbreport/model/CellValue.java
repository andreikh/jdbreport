/*
 * CellValue.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2009 Andrey Kholmanskih. All rights reserved.
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

package jdbreport.model;

import java.awt.Image;
import java.io.PrintWriter;
import java.io.Serializable;

import jdbreport.model.io.ResourceReader;
import jdbreport.model.io.ResourceWriter;
import jdbreport.model.io.SaveReportException;

import jdbreport.util.xml.XMLParser;
import jdbreport.util.xml.XMLReaderHandler;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public interface CellValue<E> extends Serializable {

	/**
	 * Reads from XML
	 * 
	 * @return XMLParser
	 */
	XMLParser createParser(XMLReaderHandler handler);

	/**
	 * Reads from XML
	 * 
	 * @return XMLParser
	 * @since 2.0
	 */
	XMLParser createParser(XMLReaderHandler handler, ResourceReader resourceReader);
	
	/**
	 * Writes to XML
	 * 
	 * @param writer
	 * @param model
	 * @param row
	 * @param column
	 * @return true if supported
	 * @throws SaveReportException 
	 */
	boolean write(PrintWriter writer, ReportModel model, int row, int column) throws SaveReportException;

	/**
	 * Writes to writer
	 * @param writer
	 * @param model
	 * @param row
	 * @param column
	 * @param format
	 * @return true if supported
	 */
	boolean write(PrintWriter writer, ReportModel model, int row, int column,
			String format) throws SaveReportException;

	/**
	 *  Writes to writer
	 * @param writer
	 * @param model
	 * @param row
	 * @param column
	 * @param resourceWriter
	 * @param format
	 * @return true if supported
	 * @throws SaveReportException 
	 * @since 2.0
	 */
	boolean write(PrintWriter writer, ReportModel model, int row, int column, ResourceWriter resourceWriter, 
			String format) throws SaveReportException;
	
	public E getValue();

	public void setValue(E e);

	/**
	 * 
	 * @return as image
	 * @since 2.0
	 */
	public Image getAsImage(ReportModel model, int row,
			int column);
}
