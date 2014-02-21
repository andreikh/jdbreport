/*
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
 * 
 */
package jdbreport.model;

import java.awt.Image;
import java.io.PrintWriter;

import jdbreport.model.io.SaveReportException;

import org.xml.sax.SAXException;

import and.util.xml.XMLParser;
import and.util.xml.XMLReaderHandler;

/**
 * @version 2.0 12.12.2009
 * @author Andrey Kholmanskih
 * 
 */
public abstract class AbstractValue<E> implements CellValue<E>, XMLParser {

	private static final long serialVersionUID = 1L;
	
	protected transient XMLReaderHandler handler;

	public XMLParser createParser(XMLReaderHandler handler) {
		this.handler = handler;
		return this;
	}

	protected XMLReaderHandler getHandler() {
		return handler;
	}

	public void characters(StringBuffer ch) throws SAXException {
	}

	public String toString() {
		return getValue() == null ? null : getValue().toString();
	}

	public boolean write(PrintWriter writer, ReportModel model, int row,
			int column, String format) throws SaveReportException {
		if (ReportBook.JRPT.equals(format)) {
			return write(writer, model, row, column);
		} 
		return false;
	}

	public Image getAsImage(ReportModel model, int row,
			int column) {
		return null;
	}
	

}
