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

import java.io.PrintWriter;
import java.io.Writer;

import jdbreport.model.ReportBook;
import jdbreport.model.io.ReportWriter;
import jdbreport.model.io.SaveReportException;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
class OdfSettingsWriter extends OdfBaseWriter implements ReportWriter {

	public void save(Writer writer, ReportBook reportBook)
			throws SaveReportException {
		PrintWriter fw;
		if (writer instanceof PrintWriter)
			fw = (PrintWriter) writer;
		else
			fw = new PrintWriter(writer);
		fw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		fw.print("<office:document-settings ");
		fw
				.print("xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\" ");
		fw.print("xmlns:xlink=\"http://www.w3.org/1999/xlink\" ");
		fw
				.print("xmlns:config=\"urn:oasis:names:tc:opendocument:xmlns:config:1.0\" ");
		fw.print("xmlns:ooo=\"http://openoffice.org/2004/office\" ");
		fw.println("office:version=\"1.2\">");
		fw.println("<office:settings>");

		fw.println("</office:settings>");
		fw.println("</office:document-settings>");
	}

}
