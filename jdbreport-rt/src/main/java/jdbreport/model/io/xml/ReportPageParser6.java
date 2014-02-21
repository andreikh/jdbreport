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
public class ReportPageParser6 extends DefaultReportParser {

	private static final Units unit = Units.INCH;
	private ReportPage reportPage;
	double top;
	double left;
	double right;
	double bottom;
	double width;
	double height;

	public ReportPageParser6(DefaultReaderHandler reportHandler,
			ReportPage reportPage) {
		super(reportHandler);
		this.reportPage = reportPage;
		top = reportPage.getTopMargin(unit);
		left = reportPage.getLeftMargin(unit);
		right = reportPage.getRightMargin(unit);
		bottom = reportPage.getBottomMargin(unit);
		width = reportPage.getWidth(unit);
		height = reportPage.getHeight(unit);
	}

	public boolean startElement(String name, Attributes attributes) {
		return true;
	}

	public void endElement(String name, StringBuffer value) throws SAXException {
		if (name.equals("TopMargin")) {
			top = Integer.parseInt(value.toString());
			return;
		}
		if (name.equals("BottomMargin")) {
			bottom = Integer.parseInt(value.toString());
			return;
		}
		if (name.equals("LeftMargin")) {
			left = Integer.parseInt(value.toString());
			return;
		}
		if (name.equals("RightMargin")) {
			right = Integer.parseInt(value.toString());
			return;
		}
		if (name.equals("Length")) {
			height = Integer.parseInt(value.toString());
			return;
		}
		if (name.equals("Width")) {
			width = Integer.parseInt(value.toString());
			return;
		}
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
			reportPage.setMargin(left, top, right, bottom, Units.MMx10);
			reportPage.setSize(width, height, Units.MMx10);
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
		params.append("\" ");
		writer.println("<PageMargins " + params + " />");

		if (reportPage.isShrinkWidth()) {
			writer.println("<ShrinkWidth>" + reportPage.isShrinkWidth()
					+ "</ShrinkWidth>");
		}
		writer.println("</RepPage>");

	}
}
