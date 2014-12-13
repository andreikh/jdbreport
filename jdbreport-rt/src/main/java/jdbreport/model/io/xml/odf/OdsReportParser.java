/*
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2014 Andrey Kholmanskih
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

import java.util.Map;

import jdbreport.model.io.xml.DefaultReaderHandler;
import jdbreport.model.io.xml.DefaultReportParser;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
abstract class OdsReportParser extends DefaultReportParser {

	public OdsReportParser(DefaultReaderHandler reportHandler) {
		super(reportHandler);
	}

	public OdsContentHandler getOdsContentHandler() {
		return (OdsContentHandler) getHandler();
	}

	public Map<Object, ColumnStyle> getColumnStyles() {
		return getOdsContentHandler().getColumnStyles();
	}

	public Map<Object, RowStyle> getRowStyles() {
		return getOdsContentHandler().getRowStyles();
	}

	public Map<Object, PageStyle> getPageStyles() {
		return getOdsContentHandler().getPageStyles();
	}

	public Map<Object, MasterPageStyle> getMasterPageStyles() {
		return getOdsContentHandler().getMasterPageStyles();
	}

	public Map<Object, TableStyle> getTableStyles() {
		return getOdsContentHandler().getTableStyles();
	}

	public String getBasePath() {
		return getOdsContentHandler().getBasePath();
	}
}
