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
