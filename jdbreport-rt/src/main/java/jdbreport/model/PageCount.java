/*
 * PageCount.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2013 Andrey Kholmanskih. All rights reserved.
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

import jdbreport.model.io.ResourceReader;
import jdbreport.model.io.ResourceWriter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import and.util.xml.XMLParser;
import and.util.xml.XMLReaderHandler;

/**
 * The object that returns page numbers
 * 
 * @version 2.0 11.08.2013
 * @author Andrey Kholmanskih
 * 
 */
public class PageCount implements CellValue<Integer>, XMLParser {

	private static final long serialVersionUID = 1L;

	private int count = 0;

	protected transient XMLReaderHandler handler;

	public PageCount() {

	}

	public XMLParser createParser(XMLReaderHandler handler) {
		this.handler = handler;
		return this;
	}

	protected XMLReaderHandler getHandler() {
		return handler;
	}

	public void characters(StringBuffer ch) throws SAXException {

	}

	public Integer getValue() {
		return count;
	}

	public void setValue(Integer e) {
		this.count = e == null ? 0 : e;
	}

	public boolean write(PrintWriter writer, ReportModel model, int row,
			int column) {
		writer.print("<value class=\"");
		writer.print(getClass().getName());
		writer.println("\" >");
		writer.print(model.getRowModel().getPageCount());
		writer.println("</value>");
		return true;
	}

	public boolean write(PrintWriter writer, ReportModel model, int row,
			int column, String format) {
		if (ReportBook.JRPT.equals(format)) {
			return write(writer, model, row, column);
		} else {
			writer.print(model.getRowModel().getPageCount());
		}
		return true;
	}

	public String toString() {
		return count > 0 ? String.valueOf(count) : "";
	}

	public void endElement(String name, StringBuffer value) throws SAXException {
		if (name.equals("value")) {
			if (value.length() > 0) {
				try {
					setValue(new Integer(value.toString().trim()));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			getHandler().popHandler(name);
			handler = null;
		}
	}

	public boolean startElement(String name, Attributes attributes)
			throws SAXException {
		if (name.equals("format")) {
			return true;
		}
		return false;
	}

	public Image getAsImage(ReportModel model, int row, int column) {
		return null;
	}

	public boolean write(PrintWriter writer, ReportModel model, int row,
			int column, ResourceWriter resourceWriter, String format) {
		return write(writer, model, row, column, format);
	}

	public XMLParser createParser(XMLReaderHandler handler,
			ResourceReader resourceReader) {
		return createParser(handler);
	}

}
