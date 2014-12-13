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
package jdbreport.model.io.xml;

import java.io.PrintWriter;

import jdbreport.model.Units;
import jdbreport.model.print.ReportPage;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @version 3.0 13.12.2014
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
			if ("Landscape".equals(s))
                reportPage.setOrientation(ReportPage.LANDSCAPE);
            else if ("Reverse_Landscape".equals(s))
                reportPage.setOrientation(ReportPage.REVERSE_LANDSCAPE);
            else
                reportPage.setOrientation(ReportPage.PORTRAIT);
		}
		if (name.equals("ShrinkWidth")) {
			reportPage.setShrinkWidth(Boolean.parseBoolean(value.toString()));
			return;
		}
		if (name.equals("RepPage")) {
			getHandler().popHandler(name);
		}
	}

	public static void save(PrintWriter writer, ReportPage reportPage) {
		writer.println("<RepPage>");

		if (reportPage.getOrientation() == ReportPage.LANDSCAPE)
			writer.println("<Orientation>Landscape</Orientation>");
		else if (reportPage.getOrientation() == ReportPage.REVERSE_LANDSCAPE)
			writer.println("<Orientation>Reverse_Landscape</Orientation>");

		StringBuilder params = new StringBuilder();
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
