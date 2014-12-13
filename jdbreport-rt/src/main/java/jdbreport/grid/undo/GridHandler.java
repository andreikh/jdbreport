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
