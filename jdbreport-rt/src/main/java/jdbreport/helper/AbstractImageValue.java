/*
 * Created	13.02.2009
 *
/*
 * AbstractImageValue.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2009 Andrey Kholmanskih. All rights reserved.
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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import jdbreport.model.AbstractValue;
import jdbreport.model.ReportModel;
import jdbreport.model.io.ResourceReader;
import jdbreport.model.io.ResourceWriter;
import jdbreport.model.io.SaveReportException;

import jdbreport.util.xml.XMLCoder;
import jdbreport.util.xml.XMLParser;
import jdbreport.util.xml.XMLReaderHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Andrey Kholmanskih
 * 
 * @version 3.0 12.12.2014
 */
public abstract class AbstractImageValue<E> extends AbstractValue<E> {

	private static final long serialVersionUID = 1L;
	private String format;
	protected ImageIcon icon;

	public abstract void setValue(Image image);
	
	public Icon getIcon() {
		return icon;
	}

	public Image getImage() {
		return icon.getImage();
	}
	
	public void setImageFormat(String format) {
		this.format = format;
	}

	public String getImageFormat() {
		return format;
	}

	public boolean write(PrintWriter writer, ReportModel model, int row, int column) {
		RenderedImage image = null;
		Image img = getImage();
		if (img instanceof RenderedImage)
			image = (RenderedImage) img;
		else {
			Icon icon = getIcon();
			image = new BufferedImage(getIcon().getIconWidth(), getIcon()
					.getIconHeight(), BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D g = ((BufferedImage) image).createGraphics();
			icon.paintIcon(null, g, 0, 0);
		}

		if (image != null) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			try {
				String format = getImageFormat();
				if (format == null || !checkImageWriterFormat(format)) {
					format = "png";
				}
				if (ImageIO.write(image, format, stream)) {
					String iconStr = new String(XMLCoder.base64Encode(stream
							.toByteArray()));
					String params = "type=\"" + format + "\" ";
					writer.print("<value class=\"");
					writer.print(getClass().getName());
					writer.println("\">");
					writer.print("<img " + params + " >");
					writer.print(iconStr);
					writer.println("</img>");
					writer.println("</value>");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private boolean checkImageWriterFormat(String format) {
		String[] formats = ImageIO.getWriterFormatNames();
		for (int i = 0; i < formats.length; i++) {
			if (format.equals(formats[i])) {
				return true;
			}
		}
		return false;
	}

	public boolean startElement(String name, Attributes attributes)
			throws SAXException {
		if (name.equals("img")) {
			return true;
		}
		return false;
	}

	public void endElement(String name, StringBuffer value) throws SAXException {
		if (name.equals("img")) {
			if (value.length() > 0) {
				try {
					Image image = ImageIO.read(new ByteArrayInputStream(
							XMLCoder.base64Decode(value.toString().trim()
									.getBytes())));
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

	public Image getAsImage() {
		return getImage();
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
