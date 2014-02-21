/*
 * Created 13.12.2009
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2009-2010 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.model.svg;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.imageio.ImageIO;

import org.w3c.dom.Document;

/**
 * @version 1.0 28.02.2010
 * @author Andrey Kholmanskih
 * 
 */
public class SVGImage {

	private static Boolean enableSVG;

	public static boolean isEnableSVG() {
		if (enableSVG == null) {
			try {
				Class.forName("org.apache.batik.dom.svg.SAXSVGDocumentFactory");
				SVGValue.registerValue();
				enableSVG = true;
			} catch (Throwable e) {
				enableSVG = false;
			}
		}
		return enableSVG;
	}

	private String xml;
	private BufferedImage image;
	private int iconWidth;
	private int iconHeight;
	private String imageFormat = "png";

	public SVGImage() {

	}

	public SVGImage(String xml) {
		this.xml = xml;
	}

	public SVGImage(File file) throws IOException {
		this(new FileReader(file));
	}

	public SVGImage(Reader reader) throws IOException {
		try {
			StringBuffer sb = new StringBuffer();
			char[] buf = new char[1024 * 32];
			int l = 0;
			do {
				l = reader.read(buf);
				if (l > 0) {
					sb.append(buf, 0, l);
				}
			} while (l > 0);
			xml = sb.toString();
		} finally {
			reader.close();
		}
	}

	public void setXML(String xml) throws IOException {
		this.xml = xml;
		image = null;
	}

	public String getXML() {
		return xml;
	}

	public Image getImage() {
		return getImage(0, 0, "png");
	}
	
	public Image getImage(String format) {
		return getImage(0, 0, format);
	}

	public Document createDocument() throws IOException {
		String parser = org.apache.batik.util.XMLResourceDescriptor
				.getXMLParserClassName();
		org.apache.batik.dom.svg.SAXSVGDocumentFactory f = new org.apache.batik.dom.svg.SAXSVGDocumentFactory(
				parser);
		StringReader reader = new StringReader(xml);
		return f.createDocument(null, reader);
	}

	public Image getImage(int width, int height, String format) {
		if (xml == null)
			return null;
		if (image == null || width != iconWidth || height != iconHeight || !imageFormat.equals(format)) {

			if (format != null) {
				imageFormat = format;
			}
			
			String parser = org.apache.batik.util.XMLResourceDescriptor
					.getXMLParserClassName();
			org.apache.batik.dom.svg.SAXSVGDocumentFactory f = new org.apache.batik.dom.svg.SAXSVGDocumentFactory(
					parser);
			try {
				StringReader reader = new StringReader(xml);
				Document document = f.createDocument(null, reader);

				iconWidth = width;
				iconHeight = height;

				org.apache.batik.transcoder.image.ImageTranscoder t;
				if ("jpg".equals(imageFormat) || "jpeg".equals(imageFormat)) {
					t = new org.apache.batik.transcoder.image.JPEGTranscoder();
				} else if ("tiff".equals(imageFormat)) {
					t = new org.apache.batik.transcoder.image.TIFFTranscoder();
				} else {
					t = new org.apache.batik.transcoder.image.PNGTranscoder();
				}

				if (width > 0) {
					t
							.addTranscodingHint(
									org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_WIDTH,
									new Float(width));
				}
				if (height > 0) {
					t
							.addTranscodingHint(
									org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_HEIGHT,
									new Float(height));
				}

				org.apache.batik.transcoder.TranscoderInput input = new org.apache.batik.transcoder.TranscoderInput(
						document);
				ByteArrayOutputStream ostream = new ByteArrayOutputStream();
				org.apache.batik.transcoder.TranscoderOutput output = new org.apache.batik.transcoder.TranscoderOutput(
						ostream);

				t.transcode(input, output);
				ByteArrayInputStream is = new ByteArrayInputStream(ostream
						.toByteArray());
				image = ImageIO.read(is);
				is.close();
				ostream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return image;
	}
}
