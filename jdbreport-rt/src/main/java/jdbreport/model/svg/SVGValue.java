/*
 * Created 13.12.2009
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2009-2014 Andrey Kholmanskih
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
package jdbreport.model.svg;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import jdbreport.util.xml.XMLParser;
import jdbreport.util.xml.XMLReaderHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import jdbreport.design.model.TemplateBook;
import jdbreport.model.AbstractValue;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportCell;
import jdbreport.model.ReportModel;
import jdbreport.model.io.ResourceReader;
import jdbreport.model.io.ResourceWriter;
import jdbreport.model.io.SaveReportException;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class SVGValue extends AbstractValue<SVGImage> {

	public static void registerValue() {
		ReportCell.setDefaultCellValueClass(SVGImage.class, SVGValue.class,
				SVGReportRenderer.class.getName(),
				jdbreport.grid.NullCellEditor.class.getName());
	}

	private SVGImage svg;
	private ResourceReader resourceReader;

	public SVGImage getValue() {
		return svg;
	}

	public void setValue(SVGImage e) {
		this.svg = e;

	}

	public boolean write(PrintWriter writer, ReportModel model, int row,
			int column, ResourceWriter resourceWriter, String format)
			throws SaveReportException {
		if (!ReportBook.JRPT.equals(format)
				&& !TemplateBook.JDBR.equals(format)) {
			return false;
		}
		if (svg == null)
			return true;
		if (resourceWriter == null) {
			writer.print("<value class=\"");
			writer.print(getClass().getName());
			writer.println("\">");
			writer.print("<svg><![CDATA[");
			writer.print(svg.getXML());
			writer.print("]]></svg>");
			writer.println("</value>");
		} else {
			writer.print("<value class=\"");
			writer.print(getClass().getName());
			writer.println("\">");
			String fileName = "image_" + row + "_" + column + ".svg";
			writer.println("<img src=\"" + fileName + "\" />");
			resourceWriter.write(fileName, svg.getXML());
			writer.println("</value>");
		}
		return true;
	}

	public boolean write(PrintWriter writer, ReportModel model, int row,
			int column) throws SaveReportException {
		return write(writer, model, row, column, null, ReportBook.JRPT);
	}

	public boolean startElement(String name, Attributes attributes)
			throws SAXException {
		if (name.equals("svg")) {
			return true;
		} else if (name.equals("img") && resourceReader != null) {
			String fileName = attributes.getValue("src");
			InputStream is = resourceReader.getResource(fileName);
			try {
				SVGImage image = new SVGImage(new InputStreamReader(is, "UTF-8"));
				setValue(image);
			} catch (IOException e) {
				throw new SAXException(e);
			}
			return false;
		}
		return false;
	}

	public void endElement(String name, StringBuffer value) throws SAXException {
		if (name.equals("svg")) {
			if (value.length() > 0) {
				try {
					SVGImage image = new SVGImage(value.toString());
					setValue(image);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return;
		}
		if (name.equals("value")) {
			getHandler().popHandler(name);
			handler = null;
		}

	}

	public Image getAsImage(ReportModel model, int row, int column) {
		return svg != null ? svg.getImage() : null;
	}

	public XMLParser createParser(XMLReaderHandler handler,
			ResourceReader resourceReader) {
		this.resourceReader = resourceReader;
		return createParser(handler);
	}

}
