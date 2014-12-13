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
package jdbreport.model.io.xml.odf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdbreport.model.io.xml.DefaultReaderHandler;
import jdbreport.model.io.xml.DefaultReportParser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
class OdsMetaParser extends DefaultReportParser {

	private boolean inCreator;
	private boolean inCreationDate;

	public OdsMetaParser(DefaultReaderHandler reportHandler) {
		super(reportHandler);
	}

	public boolean startElement(String name, Attributes attributes) {
		if (name.equals("meta:initial-creator")) {
			inCreator = true;
			return true;
		}
		if (name.equals("meta:creation-date")) {
			inCreationDate = true;
			return true;
		}
		return false;
	}

	public void endElement(String name, StringBuffer value) throws SAXException {
		if (inCreator && name.equals("meta:initial-creator")) {
			getReportBook().setCreator(value.toString());
			inCreator = false;
			return;
		}
		if (inCreationDate && name.equals("meta:creation-date")) {
			try {
				Date date = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
						.parse(value.toString().trim());
				getReportBook().setCreationDate(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			inCreationDate = false;
			return;
		}
		if (name.equals("office:meta")) {
			getHandler().popHandler(name);
		}
	}

}
