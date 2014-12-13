/*
 * CellValue.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2014 Andrey Kholmanskih
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
	 * @param writer PrintWriter
	 * @param model ReportModel
	 * @param row row number
	 * @param column column number
	 * @return true if supported
	 * @throws SaveReportException 
	 */
	boolean write(PrintWriter writer, ReportModel model, int row, int column) throws SaveReportException;

	/**
	 * Writes to writer
	 * @param writer PrintWriter
	 * @param model ReportModel
	 * @param row row number
	 * @param column column number
	 * @param format format string
	 * @return true if supported
	 */
	boolean write(PrintWriter writer, ReportModel model, int row, int column,
			String format) throws SaveReportException;

	/**
	 *  Writes to writer
	 * @param writer PrintWriter
	 * @param model ReportModel
	 * @param row row number
	 * @param column column number
	 * @param resourceWriter ResourceWriter
	 * @param format format string
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
