/*
 * ColorValue.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2009 Andrey Kholmanskih. All rights reserved.
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

package jdbreport.helper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import and.util.xml.XMLParser;
import and.util.xml.XMLReaderHandler;

import jdbreport.model.AbstractValue;
import jdbreport.model.Cell;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportCell;
import jdbreport.model.ReportModel;
import jdbreport.model.io.ResourceReader;
import jdbreport.model.io.ResourceWriter;
import jdbreport.model.io.SaveReportException;
import jdbreport.util.Utils;

/**
 * @version 2.0 12.12.2009
 * @author Andrey Kholmanskih
 * 
 */
public class ColorValue extends AbstractValue<Color> {

	private static final long serialVersionUID = 1L;
	private Color color;

	public static void registerValue() {
		ReportCell.setDefaultCellValueClass(Color.class, ColorValue.class,
				ColorReportRenderer.class.getName(),
				jdbreport.grid.NullCellEditor.class.getName());
	}

	public ColorValue() {

	}

	public ColorValue(Color color) {
		this();
		this.color = color;
	}

	public boolean write(PrintWriter writer, ReportModel model, int row, int column) {
		writer.print("<value class=\"");
		writer.print(getClass().getName());
		writer.println("\">");
		writer.print(Utils.colorToHex(getValue()));
		writer.println("</value>");
		return true;
	}
	
	public boolean write(PrintWriter writer, ReportModel model, int row,
			int column, String format) throws SaveReportException {
		if (ReportBook.HTML.equals(format)) {
			writer.print("<div style=\"background:" + Utils.colorToHex(color) + "\">&nbsp;</div>");
			return true;
		} else {
			return super.write(writer, model, row, column, format);
		}
	}

	/**
	 * @return the color
	 */
	public Color getValue() {
		return color;
	}

	public void endElement(String name, StringBuffer value) throws SAXException {
		if (name.equals("value")) {
			if (value.length() > 0) {
				setValue(Utils.stringToColor(value.toString().trim()));
			}
			getHandler().popHandler(name);
			handler = null;
		}
	}

	public boolean startElement(String name, Attributes attributes)
			throws SAXException {
		return false;
	}

	public void setValue(Color color) {
		this.color = color;
	}


	@Override
	public Image getAsImage(ReportModel model, int row,
			int column) {
		Cell cell = model.getReportCell(row, column);
		Dimension cellSize = model.getCellSize(cell, row, column, false);
		BufferedImage image = new BufferedImage((int)cellSize.getWidth(), (int)cellSize
				.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = image.createGraphics();
		
		g.setColor(getValue());
		g.fillRect(0, 0, (int)cellSize.getWidth(), (int)cellSize.getHeight());
		return image;	
	}

	public boolean write(PrintWriter writer, ReportModel model, int row,
			int column, ResourceWriter resourceWriter, String format) throws SaveReportException {
		return write(writer, model, row, column, format);
	}

	public XMLParser createParser(XMLReaderHandler handler,
			ResourceReader resourceReader) {
		return createParser(handler);
	}

}
