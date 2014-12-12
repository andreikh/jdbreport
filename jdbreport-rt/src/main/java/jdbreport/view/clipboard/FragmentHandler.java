/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2008 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.view.clipboard;

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
public class FragmentHandler extends DefaultReaderHandler {

	public static final String FRAGMENT = "fragment";
	public static final String ROOT_TAG = "jdbreport";
	private boolean isDbreport;
	private ReportModel model;
	protected int startRow;
	protected int startCol;

	public FragmentHandler(ReportModel model, XMLReader reader, int startRow,
			int startCol) {
		super(null, reader);
		this.model = model;
		this.startRow = startRow;
		this.startCol = startCol;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (rootName == null) {
			rootName = qName;
			if (rootName.equals(FragmentHandler.ROOT_TAG)) { //$NON-NLS-1$
				version = attributes.getValue("Version"); //$NON-NLS-1$
				if (version != null && version.length() > 0) {
					if (Double.parseDouble(version) >= 6) {
						isDbreport = true;
						return;
					}
				}
				throw new SAXException(
						Messages.getString("FragmentHandler.2") + rootName + " - " + version); //$NON-NLS-1$ //$NON-NLS-2$
			}
			throw new SAXException(
					Messages.getString("FragmentHandler.4") + rootName); //$NON-NLS-1$
		}
		if (handler != null) {
			isParse = handler.startElement(qName, attributes);
			return;
		}
		if (isDbreport) {
			if (qName.equals(FragmentHandler.FRAGMENT)) { //$NON-NLS-1$
				handler = getClipboardHandler();
				return;
			}
		}
		throw new SAXException(Messages.getString("FragmentHandler.6")); //$NON-NLS-1$
	}

	protected XMLParser getClipboardHandler() {
		return new ClipboardParser(this, startRow, startCol);
	}

	public ReportModel getReportModel() {
		return model;
	}

}
