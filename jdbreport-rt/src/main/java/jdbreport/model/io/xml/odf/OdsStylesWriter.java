/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2009 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.model.io.xml.odf;

import java.io.PrintWriter;
import java.io.Writer;

import and.util.Utilities;

import jdbreport.model.CellStyle;
import jdbreport.model.ReportBook;
import jdbreport.model.Units;
import jdbreport.model.io.SaveReportException;
import jdbreport.model.print.ReportPage;

/**
 * @version 2.0 19.12.2009
 * @author Andrey Kholmanskih
 * 
 */
class OdsStylesWriter extends OdfBaseWriter {

	public void save(Writer writer, ReportBook reportBook)
			throws SaveReportException {
		PrintWriter fw;
		if (writer instanceof PrintWriter)
			fw = (PrintWriter) writer;
		else
			fw = new PrintWriter(writer);

		fw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		fw.print("<office:document-styles ");
		fw
				.print("xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\" ");
		fw
				.print("xmlns:style=\"urn:oasis:names:tc:opendocument:xmlns:style:1.0\" ");
		fw
				.print("xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" ");
		fw
				.print("xmlns:table=\"urn:oasis:names:tc:opendocument:xmlns:table:1.0\" ");
		fw
				.print("xmlns:draw=\"urn:oasis:names:tc:opendocument:xmlns:drawing:1.0\" ");
		fw
				.print("xmlns:fo=\"urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0\" ");
		fw.print("xmlns:xlink=\"http://www.w3.org/1999/xlink\" ");
		fw.print("xmlns:dc=\"http://purl.org/dc/elements/1.1/\" ");
		fw
				.print("xmlns:meta=\"urn:oasis:names:tc:opendocument:xmlns:meta:1.0\" ");
		fw
				.print("xmlns:number=\"urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0\" ");
		fw
				.print("xmlns:svg=\"urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0\" ");
		fw
				.print("xmlns:chart=\"urn:oasis:names:tc:opendocument:xmlns:chart:1.0\" ");
		fw
				.print("xmlns:dr3d=\"urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0\" ");
		fw.print("xmlns:math=\"http://www.w3.org/1998/Math/MathML\" ");
		fw
				.print("xmlns:form=\"urn:oasis:names:tc:opendocument:xmlns:form:1.0\" ");
		fw
				.print("xmlns:script=\"urn:oasis:names:tc:opendocument:xmlns:script:1.0\" ");
		fw.print("xmlns:ooo=\"http://openoffice.org/2004/office\" ");
		fw.print("xmlns:ooow=\"http://openoffice.org/2004/writer\" ");
		fw.print("xmlns:oooc=\"http://openoffice.org/2004/calc\" ");
		fw.print("xmlns:dom=\"http://www.w3.org/2001/xml-events\" ");
		fw.println("office:version=\"1.2\">");

		writeFontFaces(fw, reportBook);

		writeDefaultStyles(fw, reportBook);

		writeAutomaticStyles(fw, reportBook);

		writeMasterStyles(fw, reportBook);

		fw.println("</office:document-styles>");
	}

	private void writeDefaultStyles(PrintWriter fw, ReportBook reportBook) {
		fw.println("<office:styles>");
		fw.println("<style:default-style style:family=\"table-cell\"  >");
		CellStyle style = CellStyle.getDefaultStyle().deriveWrapLine(false);
		writeCellProperties(fw, style, null);
		writeParagraphProperties(fw, style, null);
		writeTextProperties(fw, style, null);
		fw.println("</style:default-style>");
		fw
				.println("<style:style style:name=\"Default\" style:family=\"table-cell\"/>");
		fw.println("</office:styles>");
	}

	private void writeAutomaticStyles(PrintWriter fw, ReportBook reportBook) {
		fw.println("<office:automatic-styles>");
		for (int i = 0; i < reportBook.size(); i++) {
			fw.println("<style:page-layout style:name=\"pm" + (i + 1) + "\">");
			writePageStyles(fw, reportBook.getReportModel(i).getReportPage());
			fw.println("</style:page-layout>");
		}
		fw.println("</office:automatic-styles>");
	}

	private void writeMasterStyles(PrintWriter fw, ReportBook reportBook) {
		fw.println("<office:master-styles>");
		for (int i = 0; i < reportBook.size(); i++) {
			String name = i == 0 ? "Default" : "Page" + (i + 1);
			fw.println("<style:master-page style:name=\"" + name
					+ "\" style:page-layout-name=\"pm" + (i + 1) + "\">");
			fw.println("</style:master-page>");
		}
		fw.println("</office:master-styles>");
	}

	private void writePageStyles(PrintWriter fw, ReportPage reportPage) {
		fw.print("<style:page-layout-properties ");
		fw.print("fo:page-width=\"" + Utilities.round(reportPage.getWidth(), 2)
				+ "pt\" ");
		fw.print("fo:page-height=\""
				+ Utilities.round(reportPage.getHeight(), 2) + "pt\" ");

		fw.print("fo:margin-left=\""
				+ Utilities.round(reportPage.getLeftMargin(Units.PT), 2)
				+ "pt\" ");
		fw.print("fo:margin-right=\""
				+ Utilities.round(reportPage.getRightMargin(Units.PT), 2)
				+ "pt\" ");
		fw.print("fo:margin-top=\""
				+ Utilities.round(reportPage.getTopMargin(Units.PT), 2)
				+ "pt\" ");
		fw.print("fo:margin-bottom=\""
				+ Utilities.round(reportPage.getBottomMargin(Units.PT), 2)
				+ "pt\" ");
		if (reportPage.getOrientation() == ReportPage.LANDSCAPE)
			fw.print("style:print-orientation=\"landscape\" ");
		fw.println(">");
		fw.println("<style:background-image/>");
		fw.println("</style:page-layout-properties>");
	}

	public String write(String fileName, Object resource) throws SaveReportException {
		throw new SaveReportException("The method is not supported"); 
	}

}
