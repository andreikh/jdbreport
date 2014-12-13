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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jdbreport.model.ReportBook;
import jdbreport.model.ReportModel;
import jdbreport.model.io.ReportWriter;
import jdbreport.model.io.SaveReportException;

/**
 * @version 2.0 20.10.2011
 * @author Andrey Kholmanskih
 * 
 */
class OdfMetaWriter extends OdfBaseWriter implements ReportWriter {

	public OdfMetaWriter() {
	}

	public void save(Writer writer, ReportBook reportBook)
			throws SaveReportException {
		PrintWriter fw;
		if (writer instanceof PrintWriter)
			fw = (PrintWriter) writer;
		else
			fw = new PrintWriter(writer);
		fw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		fw.print("<office:document-meta ");
		fw
				.print("xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\" ");
		fw.print("xmlns:xlink=\"http://www.w3.org/1999/xlink\" ");
		fw.print("xmlns:dc=\"http://purl.org/dc/elements/1.1/\" ");
		fw
				.print("xmlns:meta=\"urn:oasis:names:tc:opendocument:xmlns:meta:1.0\" ");
		fw.print("xmlns:ooo=\"http://openoffice.org/2004/office\" ");
		fw.println("office:version=\"1.2\">");
		fw.print("<office:meta>");
		fw.print("<meta:generator>");
		fw.print("JDBReport 2.2");
		fw.println("</meta:generator>");
		fw.print("<meta:initial-creator>");
		if (reportBook.getCreator() == null) {
			reportBook.setCreator(System.getProperty( "USER" ));
		}
		fw.print(reportBook.getCreator());
		fw.println("</meta:initial-creator>");
		fw.print("<meta:creation-date>");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		if (reportBook.getCreationDate() == null) {
			reportBook.setCreationDate(new Date());
		}
		String date = format.format(reportBook.getCreationDate());
		fw.print(date);
		fw.println("</meta:creation-date>");

		fw.print("<dc:creator>");
		fw.print(System.getProperty( "USER" ));
		fw.println("</dc:creator>");
		fw.print("<dc:date>");
		fw.print(format.format(new Date()));
		fw.println("</dc:date>");
		fw.print("<dc:language>");
		Locale locale = Locale.getDefault();
		fw.print(locale.getLanguage() + "-" + locale.getCountry());
		fw.println("</dc:language>");

		fw.print("<meta:document-statistic meta:table-count=\"");
		fw.print(reportBook.size());
		fw.print("\" meta:cell-count=\"");
		int cellCount = 0;
		for (ReportModel model : reportBook) {
			cellCount += model.getColumnCount() * model.getRowCount();
		}
		fw.print(cellCount);
		fw.print("\"/>");

		fw.println("</office:meta>");
		fw.println("</office:document-meta>");
	}

}
