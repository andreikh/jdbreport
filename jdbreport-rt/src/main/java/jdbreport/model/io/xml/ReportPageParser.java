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
package jdbreport.model.io.xml;

import java.io.PrintWriter;

import jdbreport.model.Units;
import jdbreport.model.print.ReportPage;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class ReportPageParser extends DefaultReportParser {

	private static final Units unit = Units.INCH;
	private ReportPage reportPage;

	public ReportPageParser(DefaultReaderHandler reportHandler,
			ReportPage reportPage) {
		super(reportHandler);
		this.reportPage = reportPage;
	}

	public boolean startElement(String name, Attributes attributes) {
		if (name.equals("PageMargins")) {
			double top = reportPage.getTopMargin(unit);
			double left = reportPage.getLeftMargin(unit);
			double right = reportPage.getRightMargin(unit);
			double bottom = reportPage.getBottomMargin(unit);
			double width = reportPage.getWidth(unit);
			double height = reportPage.getHeight(unit);

			String s = attributes.getValue("Top");
			if (s != null) {
				top = Double.parseDouble(s);
			}
			s = attributes.getValue("Left");
			if (s != null) {
				left = Double.parseDouble(s);
			}
			s = attributes.getValue("Bottom");
			if (s != null) {
				bottom = Double.parseDouble(s);
			}
			s = attributes.getValue("Right");
			if (s != null) {
				right = Double.parseDouble(s);
			}
			s = attributes.getValue("Width");
			if (s != null) {
				width = Double.parseDouble(s);
			}
			s = attributes.getValue("Height");
			if (s != null) {
				height = Double.parseDouble(s);
			}
			reportPage.setSize(width, height, unit);
			s = attributes.getValue("PaperSize");
			if (s != null) {
				reportPage.setPaperSize(ReportPage.PaperSize.valueOf(s));
			}

			reportPage.setMargin(left, top, right, bottom, unit);

			return true;
		}
		return true;
	}

	public void endElement(String name, StringBuffer value) throws SAXException {
		if (name.equals("Orientation")) {
			String s = value.toString();
			if (s != null) {
				if ("Landscape".equals(s))
					reportPage.setOrientation(ReportPage.LANDSCAPE);
				else if ("Reverse_Landscape".equals(s))
					reportPage.setOrientation(ReportPage.REVERSE_LANDSCAPE);
				else
					reportPage.setOrientation(ReportPage.PORTRAIT);
			} else
				reportPage.setOrientation(ReportPage.PORTRAIT);
		}
		if (name.equals("ShrinkWidth")) {
			reportPage.setShrinkWidth(Boolean.parseBoolean(value.toString()));
			return;
		}
		if (name.equals("RepPage")) {
			getHandler().popHandler(name);
			return;
		}
	}

	public static void save(PrintWriter writer, ReportPage reportPage) {
		writer.println("<RepPage>");

		if (reportPage.getOrientation() == ReportPage.LANDSCAPE)
			writer.println("<Orientation>Landscape</Orientation>");
		else if (reportPage.getOrientation() == ReportPage.REVERSE_LANDSCAPE)
			writer.println("<Orientation>Reverse_Landscape</Orientation>");

		StringBuffer params = new StringBuffer();
		params.append("Top=\"");
		params.append(reportPage.getTopMargin(unit));
		params.append("\" Left=\"");
		params.append(reportPage.getLeftMargin(unit));
		params.append("\" Bottom=\"");
		params.append(reportPage.getBottomMargin(unit));
		params.append("\" Right=\"");
		params.append(reportPage.getRightMargin(unit));
		params.append("\" Width=\"");
		params.append(reportPage.getWidth(unit));
		params.append("\" Height=\"");
		params.append(reportPage.getHeight(unit));
		params.append("\" PaperSize=\"");
		params.append(reportPage.getPaperSize());
		params.append("\" ");
		writer.println("<PageMargins " + params + " />");

		if (reportPage.isShrinkWidth()) {
			writer.println("<ShrinkWidth>" + reportPage.isShrinkWidth()
					+ "</ShrinkWidth>");
		}
		writer.println("</RepPage>");

	}
}
