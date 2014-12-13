/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2014 Andrey Kholmanskih
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
