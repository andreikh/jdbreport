/*
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

import jdbreport.model.io.SaveReportException;

import jdbreport.util.xml.XMLParser;
import jdbreport.util.xml.XMLReaderHandler;
import org.xml.sax.SAXException;

/**
 * @version 3.0 12.12.2014
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
