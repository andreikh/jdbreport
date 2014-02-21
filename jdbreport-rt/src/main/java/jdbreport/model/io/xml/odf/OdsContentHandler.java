/*
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2008 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.model.io.xml.odf;

import java.util.HashMap;
import java.util.Map;

import jdbreport.model.ReportBook;
import jdbreport.model.io.xml.DefaultReaderHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @version 1.1 03/09/08
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
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (rootName == null) {
			rootName = qName;
			if (rootName.equals("office:document") //$NON-NLS-1$
					|| rootName.equals("office:document-content") //$NON-NLS-1$
					|| rootName.equals("office:document-styles") //$NON-NLS-1$
					|| rootName.equals("office:document-meta")) { //$NON-NLS-1$
				version = attributes.getValue("office:version"); //$NON-NLS-1$
				handler = getOdsParser();
				return;
			}
			throw new SAXException(
					Messages.getString("OdsContentHandler.5") + rootName); //$NON-NLS-1$
		}
		if (handler != null) {
			isParse = handler.startElement(qName, attributes);
			return;
		}
		throw new SAXException(Messages.getString("OdsContentHandler.6")); //$NON-NLS-1$
	}

	/**
	 * @return
	 */
	private OdsParser getOdsParser() {
		if (parser == null)
			parser = new OdsParser(this);
		return parser;
	}

	public Map<Object, ColumnStyle> getColumnStyles() {
		if (columnStyles == null) {
			columnStyles = new HashMap<Object, ColumnStyle>();
		}
		return columnStyles;
	}

	public Map<Object, RowStyle> getRowStyles() {
		if (rowStyles == null) {
			rowStyles = new HashMap<Object, RowStyle>();
		}
		return rowStyles;
	}

	public Map<Object, PageStyle> getPageStyles() {
		if (pageStyles == null) {
			pageStyles = new HashMap<Object, PageStyle>();
		}
		return pageStyles;
	}

	public Map<Object, MasterPageStyle> getMasterPageStyles() {
		if (masterPageStyles == null) {
			masterPageStyles = new HashMap<Object, MasterPageStyle>();
		}
		return masterPageStyles;
	}

	public Map<Object, TableStyle> getTableStyles() {
		if (tableStyles == null) {
			tableStyles = new HashMap<Object, TableStyle>();
		}
		return tableStyles;
	}

	public String getBasePath() {
		return basePath;
	}
}
