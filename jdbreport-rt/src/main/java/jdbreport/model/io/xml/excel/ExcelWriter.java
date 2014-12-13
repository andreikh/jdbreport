/*
 * ExcelWriter.java
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

package jdbreport.model.io.xml.excel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import jdbreport.model.CellStyle;
import jdbreport.view.model.JReportModel;
import jdbreport.model.ReportBook;
import jdbreport.model.io.ReportWriter;
import jdbreport.model.io.SaveReportException;

/**
 * @version 2.0 30.03.2012
 * @author Andrey Kholmanskih
 * 
 */
public class ExcelWriter implements ReportWriter {

	public void save(OutputStream out, ReportBook reportBook)
			throws SaveReportException {
		PrintWriter fw = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(out, java.nio.charset.Charset
						.forName("UTF-8"))));
		try {
			save(fw, reportBook);
		} finally {
			fw.close();
		}
	}

	public void save(Writer writer, ReportBook reportBook)
			throws SaveReportException {
		PrintWriter fw;
		if (writer instanceof PrintWriter)
			fw = (PrintWriter) writer;
		else
			fw = new PrintWriter(writer);
		fw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		fw.println("<?mso-application progid=\"Excel.Sheet\"?>");
		fw
				.println("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"");
		fw.println("xmlns:o=\"urn:schemas-microsoft-com:office:office\"");
		fw.println("xmlns:x=\"urn:schemas-microsoft-com:office:excel\"");
		fw.println("xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"");
		fw.println("xmlns:html=\"http://www.w3.org/TR/REC-html40\">");
		fw
				.println("<DocumentProperties xmlns=\"urn:schemas-microsoft-com:office:office\">");
		fw.println("<Author></Author>");
		fw.println("<LastAuthor></LastAuthor>");
		fw.println("<Created></Created>");
		fw.println("<LastSaved></LastSaved>");
		fw.println("<Company></Company>");
		fw.println("<Version>10.2625</Version>");
		fw.println("</DocumentProperties>");
		fw
				.println("<OfficeDocumentSettings xmlns=\"urn:schemas-microsoft-com:office:office\">");
		fw.println("<DownloadComponents/>");
		fw.println("<LocationOfComponents HRef=\"/\"/>");
		fw.println("</OfficeDocumentSettings>");
		fw
				.println("<ExcelWorkbook xmlns=\"urn:schemas-microsoft-com:office:excel\">");
		fw.println("<ProtectStructure>False</ProtectStructure>");
		fw.println("<ProtectWindows>False</ProtectWindows>");
		fw.println("</ExcelWorkbook>");
		fw.println("<Styles>");
		ExcelStyleParser.saveStyle(fw, CellStyle.getDefaultStyle());
		for (CellStyle style : reportBook.getStyleList().values()) {
			ExcelStyleParser.saveStyle(fw, style);
		}
		fw.println("</Styles>");

		ExcelSheetParser sheetSaver = new ExcelSheetParser();

		Set<String> titles = new HashSet<String>();
		for (int i = 0; i < reportBook.size(); i++) {
			JReportModel model = reportBook.getReportModel(i);
			String reportTitle = model.getReportTitle();
			if (reportTitle.length() > 26)
				reportTitle = reportTitle.substring(0, 26);
			String title = reportTitle;
			int n = 1;
			while (titles.contains(reportTitle.toUpperCase())) {
				reportTitle = title + "(" + n++ + ")";
			}
			titles.add(reportTitle.toUpperCase());
			sheetSaver.saveSheet(fw, model, reportTitle);
		}

		fw.println("</Workbook>");
	}

	public void save(File file, ReportBook reportBook)
			throws SaveReportException {
		try {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			try {
				save(out, reportBook);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			throw new SaveReportException(e);
		}
	}

	public String write(String fileName, Object resource) throws SaveReportException {
		throw new SaveReportException("The method is not supported"); 
	}

}
