/*
 * Created on 28.01.2005
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2005-2014 Andrey Kholmanskih
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
package jdbreport.model.io.xml;

import jdbreport.model.ReportBook;
import jdbreport.model.ReportModel;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import jdbreport.util.xml.AbstractXMLParser;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class DefaultReportParser extends AbstractXMLParser {

	public static final String CELL = "cell";
	public static final String STYLES = "Styles";
	public static final String STYLE = "Style";
	public static final String SHEET = "Sheet";
	public static final String ROW = "row";

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
