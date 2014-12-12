/*
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2008 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.grid.undo;

import jdbreport.model.ReportModel;
import jdbreport.model.io.xml.DefaultReaderHandler;

import jdbreport.util.xml.XMLParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class GridHandler extends DefaultReaderHandler {

	private boolean isDbreport;
	private ReportModel model;

	public GridHandler(ReportModel model, XMLReader reader) {
		super(null, reader);
		this.model = model;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (rootName == null) {
			rootName = qName;
			if (rootName.equals("jdbreport")) {
				isDbreport = true;
				return;
			}
			throw new SAXException(
					Messages.getString("GridHandler.1") + rootName);
		}
		if (handler != null) {
			isParse = handler.startElement(qName, attributes);
			return;
		}
		if (isDbreport) {
			if (qName.equals("reportgrid")) {
				handler = getDefaultHandler();
				return;
			}
		}
		throw new SAXException(Messages.getString("GridHandler.3"));
	}

	protected XMLParser getDefaultHandler() {
		return new GridParser(this);
	}

	public ReportModel getReportModel() {
		return model;
	}

}
