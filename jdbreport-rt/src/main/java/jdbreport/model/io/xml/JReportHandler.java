/*
 * Created on 20.01.2005
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
import jdbreport.model.io.ResourceReader;
import jdbreport.model.io.xml.excel.ExcelXPParser;

import org.xml.sax.*;

import jdbreport.util.xml.XMLParser;

/**
 * @version 3.0 12.12.2014
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
