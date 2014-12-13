/*
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
import jdbreport.model.ReportModel;

import org.xml.sax.XMLReader;

import jdbreport.util.xml.XMLReaderHandler;

/**
 * @version 3.0 12.12.2014
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
