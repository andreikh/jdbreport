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
package jdbreport.design.model.xml;

import jdbreport.model.ReportBook;
import jdbreport.model.io.ResourceReader;
import jdbreport.model.io.xml.JReportHandler;

import jdbreport.util.xml.XMLParser;
import org.xml.sax.XMLReader;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 *
 */
public class JTemplateReportHandler extends JReportHandler {

	public JTemplateReportHandler(ReportBook reportBook, XMLReader reader, ResourceReader rr) {
		super(reportBook, reader, rr);
	}

	public XMLParser getDefaultHandler(JReportHandler handler) {
		return new TemplateBookParser(handler, getResourceReader());
	}
	
}
