/*
 * Created on 20.01.2005
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2005-2009 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.model.io.xml;

import jdbreport.model.ReportBook;
import jdbreport.model.io.ResourceReader;
import jdbreport.model.io.xml.excel.ExcelXPParser;

import org.xml.sax.*;

import and.util.xml.XMLParser;

/**
 * @version 2.0 20.12.2009
 * @author Andrey Kholmanskih
 * 
 */
public class JReportHandler extends DefaultReaderHandler {

	private boolean isDbreport;
	private ResourceReader resourceReader;

	public JReportHandler(ReportBook reportBook, XMLReader reader, ResourceReader rr) {
		super(reportBook, reader);
		this.resourceReader = rr;
	}

	/**
	 * 
	 * @return reader to resources
	 * @since 2.0
	 */
	protected ResourceReader getResourceReader() {
		return resourceReader;
	}
	
	public XMLParser getDefaultHandler(JReportHandler handler) {
		return new ReportBookParser(handler, getResourceReader());
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (rootName == null) {
			rootName = qName;
			if (rootName.equals("jdbreport")) { //$NON-NLS-1$
				version = attributes.getValue("Version"); //$NON-NLS-1$
				getReportBook().setReportCaption(attributes.getValue("Name")); //$NON-NLS-1$
				isDbreport = true;
				return;
			} else if (rootName.equals("dbreport")) { //$NON-NLS-1$
				version = attributes.getValue("Version"); //$NON-NLS-1$
				if (version != null && version.length() > 0) {
					if (Double.parseDouble(version) >= 6) {
						if (attributes.getValue("Name") != null && //$NON-NLS-1$
								attributes.getValue("Name").length() > 0) //$NON-NLS-1$
							getReportBook().setReportCaption(
									attributes.getValue("Name")); //$NON-NLS-1$
						isDbreport = true;
						return;
					}
				}
				throw new SAXException(
						Messages.getString("JReportHandler.8") + rootName + " - " + version); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (rootName.equals("Workbook")) { //$NON-NLS-1$
				handler = new ExcelXPParser(this);
				return;
			} else if (rootName.equals(DataPacketParser.DATAPACKET)) {
				getReportBook().add();
				handler = new DataPacketParser(this);
				return;
			}
			throw new SAXException(
					Messages.getString("JReportHandler.unknow") + rootName); //$NON-NLS-1$
		}
		if (handler != null) {
			isParse = handler.startElement(qName, attributes);
			return;
		}
		if (isDbreport) {
			if (qName.equals("DocReport") || //$NON-NLS-1$
					qName.equals("DesignReport")) { //$NON-NLS-1$
				handler = getDefaultHandler(this);
				return;
			}
			if (qName.equals("fragment")) { //$NON-NLS-1$
				throw new SAXException(Messages
						.getString("JReportHandler.unknow") + rootName); //$NON-NLS-1$
			}
		}
		throw new SAXException(Messages.getString("JReportHandler.6")); //$NON-NLS-1$
	}

}
