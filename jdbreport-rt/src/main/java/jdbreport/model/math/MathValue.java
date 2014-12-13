/*
 * Created	04.05.2011
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2011-2014 Andrey Kholmanskih
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
package jdbreport.model.math;

import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import jdbreport.util.xml.XMLParser;
import jdbreport.util.xml.XMLReaderHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import jdbreport.design.model.TemplateBook;
import jdbreport.model.AbstractValue;
import jdbreport.model.Cell;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportCell;
import jdbreport.model.ReportModel;
import jdbreport.model.io.ResourceReader;
import jdbreport.model.io.ResourceWriter;
import jdbreport.model.io.SaveReportException;

/**
 * @author Andrey Kholmanskih
 * 
 * @version 1.0 05.05.2011
 */
public class MathValue extends AbstractValue<MathML> {

	private static final Logger logger = Logger.getLogger(MathValue.class
			.getName());

	private static Boolean enableMathMl;

	public static boolean isEnableMathMl() {
		if (enableMathMl == null) {
			try {
				Class.forName("net.sourceforge.jeuclid.swing.JMathComponent");
				registerValue();
				enableMathMl = true;
			} catch (Throwable e) {
				enableMathMl = false;
			}
		}
		return enableMathMl;
	}

	public static void registerValue() {
		ReportCell.setDefaultCellValueClass(MathML.class, MathValue.class,
				MathReportRenderer.class.getName(),
				jdbreport.grid.NullCellEditor.class.getName());
	}

	private MathML math;
	private ResourceReader resourceReader;

	public boolean write(PrintWriter writer, ReportModel model, int row,
			int column) throws SaveReportException {
		return write(writer, model, row, column, null, ReportBook.JRPT);
	}

	public boolean write(PrintWriter writer, ReportModel model, int row,
			int column, ResourceWriter resourceWriter, String format)
			throws SaveReportException {
		if (!ReportBook.JRPT.equals(format)
				&& !TemplateBook.JDBR.equals(format)) {
			return false;
		}
		if (math == null)
			return true;
		if (resourceWriter == null) {
			writer.print("<value class=\"");
			writer.print(getClass().getName());
			writer.println("\">");
			writer.print("<mathml><![CDATA[");
			writer.print(math.getXML());
			writer.print("]]></mathml>");
			writer.println("</value>");
		} else {
			writer.print("<value class=\"");
			writer.print(getClass().getName());
			writer.println("\">");
			String fileName = "mathml_" + row + "_" + column + ".xml";
			writer.println("<mathres src=\"" + fileName + "\" />");
			resourceWriter.write(fileName, math.getXML());
			writer.println("</value>");
		}
		return true;
	}

	public MathML getValue() {
		return math;
	}

	public void setValue(MathML e) {
		math = e;
	}

	public XMLParser createParser(XMLReaderHandler handler,
			ResourceReader resourceReader) {
		this.resourceReader = resourceReader;
		return createParser(handler);
	}

	public boolean startElement(String name, Attributes attributes)
			throws SAXException {
		if (name.equals("mathml")) {
			return isEnableMathMl();
		} else if (name.equals("mathres") && resourceReader != null && isEnableMathMl()) {
			String fileName = attributes.getValue("src");
			InputStream is = resourceReader.getResource(fileName);
			try {
				MathML m = new MathML(new InputStreamReader(is, "UTF-8"));
				setValue(m);
			} catch (Throwable e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
				throw new SAXException(e.getMessage());
			}
			return false;
		}
		return false;
	}

	public void endElement(String name, StringBuffer value) throws SAXException {
		if (name.equals("mathml")) {
			if (value.length() > 0) {
				try {
					MathML m = new MathML(value.toString());
					setValue(m);
				} catch (Throwable e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
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
		if (math == null) return null;
		Cell cell = model.getReportCell(row, column);
		Dimension cellSize = model.getCellSize(cell, row, column, false);
		math.getComponent().setSize((int)cellSize.getWidth(), (int)cellSize.getHeight());
		math.setStyle(model.getStyles(cell.getStyleId()));
		try {
			return math.getImage();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}

	public String toString() {
		return math != null ? math.getXML() : super.toString(); 
	}
}
