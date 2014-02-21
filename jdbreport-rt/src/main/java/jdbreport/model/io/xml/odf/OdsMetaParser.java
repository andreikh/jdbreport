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
			return;
		}
	}

}
