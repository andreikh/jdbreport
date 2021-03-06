/*
 * Created on 30.01.2005
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2005-2017 Andrey Kholmanskih
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

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

import jdbreport.model.Cell;
import jdbreport.model.CellValue;
import jdbreport.model.Picture;
import jdbreport.model.PictureFactory;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportModel;
import jdbreport.model.io.ResourceReader;
import jdbreport.model.io.ResourceWriter;
import jdbreport.model.io.SaveReportException;

import jdbreport.util.xml.XMLCoder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @version 3.1.3 19.03.2017
 * @author Andrey Kholmanskih
 * 
 */
public class CellParser extends DefaultReportParser {

	private Cell cell;
	private ResourceWriter resourceWriter;
	private ResourceReader resourceReader;

	public CellParser(DefaultReaderHandler reportHandler,
			ResourceWriter resIO) {
		super(reportHandler);
		this.resourceWriter = resIO;
	}

	public CellParser(DefaultReaderHandler reportHandler, Cell cell, ResourceReader resReader) {
		super(reportHandler);
		this.setCell(cell);
		this.resourceReader = resReader;
	}

	public boolean startElement(String name, Attributes attributes)
			throws SAXException {
		if (name.equals("t")) {
			return true;
		}
		if (name.equals("vt")) {
			return true;
		}
		if (name.equals("cf")) {
			return true;
		}
		if (name.equals("print")) {
			return true;
		}
		if (name.equals("editable")) {
			return true;
		}
		if (name.equals("ExtFlags")) {
			return true;
		}
		if (name.equals("img") || name.equals("picture")) {
			String format = attributes.getValue("type");
			Picture picture = PictureFactory.createPicture(format);
			cell.setPicture(picture);
			if (attributes.getValue("stretch") != null) {
				cell.setScaleIcon(
						Boolean.parseBoolean(attributes.getValue("stretch")));
			}
			
			String s = attributes.getValue("src"); 
			if (s != null) {
				InputStream is = resourceReader.getResource(s);
				try {
					picture.load(is);
				} catch (IOException e) {
					throw new SAXException(e);
				}
				return false;
			}
			return true;
		}
		if (name.equals("value")) {
			String className = attributes.getValue("class");
			if (className != null) {
				CellValue<?> value;
				try {
					value = (CellValue<?>) Class.forName(className).newInstance();
				} catch (Exception e) {
					throw new SAXException(e);
				}
				getCell().setValue(value);
				getHandler().pushHandler(value.createParser(getHandler(), resourceReader));
				return true;
			}

		}

		return false;
	}

	protected Image readImage(String s) throws SAXException {
		if (checkImageWriterFormat(getCell().getImageFormat())) {
			InputStream is = resourceReader.getResource(s);
			try {
				return ImageIO.read(is);
			} catch (IOException e) {
				throw new SAXException(e);
			}
		}
		return null;
	}

	
	public void endElement(String name, StringBuffer value) {
		if (name.equals("t")) {
			getCell().setValue(value.toString());
			return;
		}
		if (name.equals(CELL)) {
			try {
				getHandler().popHandler(name);
			} catch (SAXException e) {
				e.printStackTrace();
			}
			return;
		}
		if (name.equals("ExtFlags")) {
			getCell().setExtFlags(Integer.parseInt(value.toString()));
			return;
		}
		if (name.equals("vt")) {
			try {
				getCell().setValueType(Cell.Type.valueOf(value.toString()));
			} catch (Exception e) {
				getCell().setValueType(null);
			}
			return;
		}
		if (name.equals("cf")) {
			getCell().setCellFormula(value.toString());
			return;
		}
		if (name.equals("print")) {
			getCell().setNotPrint(!Boolean.parseBoolean(value.toString()));
			return;
		}
		if (name.equals("editable")) {
			getCell().setEditable(Boolean.parseBoolean(value.toString()));
			return;
		}
		if (name.equals("img") || name.equals("picture")) {
			if (value.length() > 0) {
				try {
					cell.getPicture().setBuf(XMLCoder.base64Decode(value.toString().trim()
							.getBytes()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (cell.getPicture().getBuf() == null) {
				cell.setPicture(null);
			}
		}

	}

	public void save(PrintWriter writer, ReportModel model, Cell cell, int row,
			int col) throws SaveReportException {
		if (cell.isNull() || cell.isChild())
			return;
		writer.println("<cell" + getParams(cell, col) + ">");

		writeElements(writer, model, cell, row, col);

		writer.println("</cell>");
	}

	private boolean checkImageWriterFormat(String format) {
		String[] formats = ImageIO.getWriterFormatNames();
		for (String format1 : formats) {
			if (format.equals(format1)) {
				return true;
			}
		}
		return false;
	}

	protected void writeElements(PrintWriter writer, ReportModel model,
			Cell cell, int row, int column) throws SaveReportException {

		RenderedImage image = null;

		if (cell.getValue() instanceof CellValue) {
			if (!((CellValue<?>) cell.getValue())
					.write(writer, model, row, column, resourceWriter, ReportBook.JRPT)) {
				Image img = ((CellValue<?>) cell.getValue()).getAsImage(model,
						row, column);
				if (img instanceof RenderedImage) {
					image = (RenderedImage) img;
				}
			}
		} else {
			if (cell.toString().length() > 0)
				writer.println("<t>"
						+ XMLCoder.replaceSpecChar(cell.toString()) + "</t>");
		}

		if (cell.getValueType() != Cell.DEFAULT_TYPE) {
			writer.println("<vt>" + cell.getValueType() + "</vt>");
		}

		if (cell.getCellFormula() != null) {
			writer.println("<cf>" + XMLCoder.replaceSpecChar(cell.getCellFormula()) + "</cf>");
		}

		if (cell.getExtFlags() > 0) {
			writer.println("<ExtFlags>" + cell.getExtFlags() + "</ExtFlags>");
		}

		if (cell.isNotPrint()) {
			writer.println("<print>false</print>");
		}

		if (!cell.isEditable()) {
			writer.println("<editable>false</editable>");
		}

		if (image != null) {
			if (resourceWriter != null) {
				String format = cell.getImageFormat();
				String fileName = resourceWriter.write("image_" + row + "_" + column
					+ "_"	+ image.hashCode() + "." + format, image);
				String params = " src=\"" + fileName + "\" ";
				params += "type=\"" + format + "\" ";
				if (cell.isScaleIcon()) {
					params += " stretch=\"true\"";
				}
				writer.print("<img " + params + " />");
			} else {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				try {
					String format = cell.getImageFormat();
					if (format == null || !checkImageWriterFormat(format)) {
						format = "png";
					}
					if (ImageIO.write(image, format, stream)) {
						String iconStr = new String(XMLCoder
								.base64Encode(stream.toByteArray()));
						String params = "type=\"" + format + "\" ";
						if (cell.isScaleIcon()) {
							params += " stretch=\"true\"";
						}
						writer.print("<img " + params + " >");
						writer.print(iconStr);
						writer.println("</img>");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else	if (cell.getPicture() != null) {
			if (resourceWriter != null) {
				String format = cell.getImageFormat();
				String fileName = resourceWriter.write("image_" + row + "_" + column
						+ "_"	+ cell.getPicture().hashCode() + "." + format, cell.getPicture().getBuf());
				String params = " src=\"" + fileName + "\" ";
				params += "type=\"" + format + "\" ";
				if (cell.isScaleIcon()) {
					params += " stretch=\"true\"";
				}
				writer.print("<picture " + params + " />");
			} else {
					String format = cell.getImageFormat();
					if (format == null || !checkImageWriterFormat(format)) {
						format = "png";
					}
						String iconStr = new String(XMLCoder
								.base64Encode(cell.getPicture().getBuf()));
						String params = "type=\"" + format + "\" ";
						if (cell.isScaleIcon()) {
							params += " stretch=\"true\"";
						}
						writer.print("<picture " + params + " >");
						writer.print(iconStr);
						writer.println("</picture>");
					
			}
		}
		else if (cell.isScaleIcon()) {
			writer.print("<img stretch=\"true\" />");
		}
	}

	protected String getParams(Cell cell, int col) {
		String params = " c=\"" + col + "\"";
		if (cell.getStyleId() != null)
			params += " ID=\"" + cell.getStyleId() + "\"";

		if (cell.getColSpan() > 0) {
			params += " cSpan=\"" + (cell.getColSpan() + 1) + "\"";
		}
		if (cell.getRowSpan() > 0) {
			params += " rSpan=\"" + (cell.getRowSpan() + 1) + "\"";
		}
		return params;
	}

	/**
	 * @param cell
	 *            The cell to set.
	 */
	public void setCell(Cell cell) {
		this.cell = cell;
	}

	/**
	 * @return Returns the cell.
	 */
	public Cell getCell() {
		return cell;
	}

}
