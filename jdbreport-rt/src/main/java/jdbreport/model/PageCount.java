/*
 * PageCount.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2013-2014 Andrey Kholmanskih
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

import jdbreport.model.io.ResourceReader;
import jdbreport.model.io.ResourceWriter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import jdbreport.util.xml.XMLParser;
import jdbreport.util.xml.XMLReaderHandler;

/**
 * The object that returns page numbers
 * 
 * @version 3.0 12.12.2014
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
		return name.equals("format");
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
