/*
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2014 Andrey Kholmanskih
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
package jdbreport.model.io.xml.odf;

import java.util.HashMap;
import java.util.Map;

import jdbreport.model.ReportBook;
import jdbreport.model.io.xml.DefaultReaderHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @version 3.0 13.12.2014
 * @author Andrey Kholmanskih
 * 
 */
class OdsContentHandler extends DefaultReaderHandler {

	private Map<Object, ColumnStyle> columnStyles;
	private Map<Object, RowStyle> rowStyles;
	private Map<Object, PageStyle> pageStyles;
	private OdsParser parser;
	private String basePath;
	private Map<Object, MasterPageStyle> masterPageStyles;
	private Map<Object, TableStyle> tableStyles;

	public OdsContentHandler(ReportBook reportBook, String basePath) {
		super(reportBook, null);
		this.basePath = basePath;
	}

	public OdsContentHandler(ReportBook reportBook, String basePath,
			XMLReader reader) {
		super(reportBook, reader);
		this.basePath = basePath;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (rootName == null) {
			rootName = qName;
			if (rootName.equals("office:document")
					|| rootName.equals("office:document-content")
					|| rootName.equals("office:document-styles")
					|| rootName.equals("office:document-meta")) {
				version = attributes.getValue("office:version");
				handler = getOdsParser();
				return;
			}
			throw new SAXException(
					Messages.getString("OdsContentHandler.5") + rootName);
		}
		if (handler != null) {
			isParse = handler.startElement(qName, attributes);
			return;
		}
		throw new SAXException(Messages.getString("OdsContentHandler.6"));
	}

	private OdsParser getOdsParser() {
		if (parser == null)
			parser = new OdsParser(this);
		return parser;
	}

	public Map<Object, ColumnStyle> getColumnStyles() {
		if (columnStyles == null) {
			columnStyles = new HashMap<>();
		}
		return columnStyles;
	}

	public Map<Object, RowStyle> getRowStyles() {
		if (rowStyles == null) {
			rowStyles = new HashMap<>();
		}
		return rowStyles;
	}

	public Map<Object, PageStyle> getPageStyles() {
		if (pageStyles == null) {
			pageStyles = new HashMap<>();
		}
		return pageStyles;
	}

	public Map<Object, MasterPageStyle> getMasterPageStyles() {
		if (masterPageStyles == null) {
			masterPageStyles = new HashMap<>();
		}
		return masterPageStyles;
	}

	public Map<Object, TableStyle> getTableStyles() {
		if (tableStyles == null) {
			tableStyles = new HashMap<>();
		}
		return tableStyles;
	}

	public String getBasePath() {
		return basePath;
	}
}
