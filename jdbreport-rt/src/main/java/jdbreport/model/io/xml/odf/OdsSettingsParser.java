/**
 * OdsSettingsParser.java 04.11.2006
 *
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
		}
	}

}
