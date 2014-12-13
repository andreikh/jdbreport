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
package jdbreport.model.io.xml.excel;

import jdbreport.model.io.xml.DefaultReaderHandler;
import jdbreport.model.io.xml.DefaultReportParser;

import org.xml.sax.Attributes;

/**
 * @version 1.1 03/09/08
 * 
 * @author Andrey Kholmanskih
 * 
 */
public class ExcelXPParser extends DefaultReportParser {

	public ExcelXPParser() {
		super(null);
	}

	public ExcelXPParser(DefaultReaderHandler reportHandler) {
		super(reportHandler);
	}

	public boolean startElement(String name, Attributes attributes) {
		if (name.equals("Styles")) {
			getHandler().pushHandler(
					new ExcelStyleParser(getDefaultReportHandler()));
			return true;
		}
		if (name.equals("Worksheet")) {
			setCurrentModel(getReportBook().add());
			getReportModel().setReportTitle(attributes.getValue("ss:Name"));
			getHandler().pushHandler(
					new ExcelSheetParser(getDefaultReportHandler()));
			return true;
		}
		return false;
	}

	public void endElement(String name, StringBuffer value) {
		if (name.equals("Worksheet")) {
			if (getReportModel().getRowCount() == 0) {
				getReportBook().remove(getCurrentModel());
			}
		}
	}

}
