/*
 * Created on 28.01.2005
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2005-2008 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.model.io.xml;

import jdbreport.model.ReportBook;
import jdbreport.model.ReportModel;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import and.util.xml.AbstractXMLParser;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class DefaultReportParser extends AbstractXMLParser {

	public static final String CELL = "cell";
	public static final String STYLES = "Styles";
	public static final String STYLE = "Style";
	public static final String SHEET = "Sheet";
	public static final String ROW = "row";

	/**
	 * 
	 */
	public DefaultReportParser(DefaultReaderHandler reportHandler) {
		super(reportHandler);
	}

	protected DefaultReaderHandler getDefaultReportHandler() {
		return (DefaultReaderHandler) getHandler();
	}

	public boolean startElement(String name, Attributes attributes)
			throws SAXException {
		return false;
	}

	public void endElement(String name, StringBuffer value) throws SAXException {

	}

	public ReportModel getReportModel() {
		return ((DefaultReaderHandler) getHandler()).getReportModel();
	}

	public ReportBook getReportBook() {
		return ((DefaultReaderHandler) getHandler()).getReportBook();
	}

	public void setCurrentModel(int current) {
		((DefaultReaderHandler) getHandler()).setCurrentModel(current);
	}

	public int getCurrentModel() {
		return ((DefaultReaderHandler) getHandler()).getCurrentModel();
	}
}
