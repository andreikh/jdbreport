/**
 * OdsSettingsParser.java 04.11.2006
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2009 Andrey Kholmanskih. All rights reserved.
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

import jdbreport.model.io.xml.DefaultReaderHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @version 1.2 02/22/09
 * @author Andrey Kholmanskih
 */
class OdsSettingsParser extends OdsReportParser {

	private boolean inPrintSettings;

	private boolean inPrinterName;

	private boolean inPrinterSetup;

	public OdsSettingsParser(DefaultReaderHandler reportHandler) {
		super(reportHandler);
	}

	public boolean startElement(String name, Attributes attributes) {
		if (inPrintSettings) {
			if (name.equals("config:config-item")) {
				if (attributes.getValue("config:name").equals("PrinterName")) {
					inPrinterName = true;
					return true;
				}
				if (attributes.getValue("config:name").equals("PrinterSetup")) {
					inPrinterSetup = true;
					return true;
				}
			}
		}
		if (name.equals("config:config-item-set")
				&& attributes.getValue("config:name").equals(
						"ooo:configuration-settings")) {
			inPrintSettings = true;
			return true;
		}
		return false;
	}

	public void endElement(String name, StringBuffer value) throws SAXException {
		if (inPrintSettings) {
			if (name.equals("config:config-item")) {
				if (inPrinterName) {
					inPrinterName = false;
					return;
				}
				if (inPrinterSetup) {
					inPrinterSetup = false;
					return;
				}
			}
			if (name.equals("config:config-item-set")) {
				inPrintSettings = false;
				return;
			}
		}
		if (name.equals("office:settings")) {
			getHandler().popHandler(name);
			return;
		}
	}

}
