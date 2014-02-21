/*
 * ExcelWriter.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2010 Andrey Kholmanskih. All rights reserved.
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
import jdbreport.model.JReportModel;
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
