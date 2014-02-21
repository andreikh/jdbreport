/*
 * JDBReport Generator
 * 
 * Copyright (C) 2005-2008 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.model.io.xml;

import jdbreport.model.ReportBook;
import jdbreport.model.ReportModel;

import org.xml.sax.XMLReader;

import and.util.xml.XMLReaderHandler;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class DefaultReaderHandler extends XMLReaderHandler {

	private int currentModel;
	private ReportBook reportBook;

	public DefaultReaderHandler(ReportBook reportBook, XMLReader reader) {
		super(reader);
		this.reportBook = reportBook;
		currentModel = 0;
	}

	public ReportModel getReportModel() {
		return reportBook.getReportModel(getCurrentModel());
	}

	public ReportBook getReportBook() {
		return reportBook;
	}

	public void setCurrentModel(int i) {
		if (this.currentModel != i) {
			this.currentModel = i;
		}
	}

	public int getCurrentModel() {
		return currentModel;
	}

}
