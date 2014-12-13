/*
 * PageNumber.java
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

import jdbreport.model.io.ResourceReader;
import jdbreport.model.io.ResourceWriter;

import jdbreport.util.xml.XMLCoder;
import jdbreport.util.xml.XMLParser;
import jdbreport.util.xml.XMLReaderHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * The object that returns a number of the current page
 * 
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class PageNumber implements CellValue<Integer>, XMLParser {

	private static final long serialVersionUID = 1L;

	private int number = 0;

	protected transient XMLReaderHandler handler;

	private String format;

	public PageNumber() {

	}

	public PageNumber(String format) {
		this.format = format;
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
		return number;
	}

	public void setValue(Integer e) {
		this.number = e == null ? 0 : e;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public boolean write(PrintWriter writer, ReportModel model, int row, int column) {
		Integer value = model.getRowModel().getPageNumber(row, column);
		if (value == null) {
			return true;
		}
		writer.print("<value class=\"");
		writer.print(getClass().getName());
		writer.println("\" >");
		if (format != null) {
			writer.println("<format>" + XMLCoder.replaceSpecChar(format)
					+ "</format>");
		}
		writer.print(value);
		writer.println("</value>");
		return true;
	}

	public boolean write(PrintWriter writer, ReportModel model, int row,
			int column, String format) {
		if (ReportBook.JRPT.equals(format)) {
			return write(writer, model, row, column);
		} else {
	//		if (number > 0) {
				if (this.format != null) {
					writer.print(String.format(this.format, model.getRowModel()
							.getPageNumber(row, column)));
				} else {
					Integer value = model.getRowModel().getPageNumber(row,
							column);
					writer
							.print(value != null ? value : 0);
				}
		//	}
		}
		return true;
	}

	public String toString() {
		if (format != null && number > 0) {
			return String.format(format, number);
		}
		return number > 0 ? ("" + number) : "";
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
		} else if (name.equals("format")) {
			format = value.toString();
		}
	}

	public boolean startElement(String name, Attributes attributes)
			throws SAXException {
		return name.equals("format");
	}

	public Image getAsImage(ReportModel model, int row,
			int column) {
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
