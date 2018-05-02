/*
 * Created	15.10.2010
 *
 * Copyright (C) 2010-2014 Andrey Kholmanskih
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
package jdbreport.model;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.w3c.dom.Document;

/**
 * @author Andrey Kholmanskih
 * 
 * @version 3.0 13.12.2014
 */
public class PictureSVG extends Picture {

	public static final String TIFF = "tiff";
	public static final String SVG = "svg";

	/**
	 * 
	 */
	public PictureSVG(String format) {
		super(format);
	}

	/**
	 * @param buf picture bytes
	 */
	public PictureSVG(byte[] buf) {
		this(buf, SVG);
	}

	/**
	 * @param buf picture bytes
	 * @param format format string
	 */
	public PictureSVG(byte[] buf, String format) {
		super(buf, format);
	}

	public byte[] getBuf() {
		return buf;
	}

	@Override
	public void setBuf(byte[] buf) {
		super.setBuf(buf);
	}

	public String getXML() {
		return new String(getBuf());
	}

	protected ImageIcon createIcon(int w, int h) {
		if (buf == null)
			return null;
		icon = null;
		Image image = createImage(w, h, PNG);
		icon = new ImageIcon(image);
		iconWidth = w;
		iconHeight = h;
		return icon;
	}

	public BufferedImage createImage() {
		return createImage(0, 0, PNG);
	}
	
	public BufferedImage createImage(int width, int height, String format) {

		BufferedImage image = null;

		String parser = org.apache.batik.util.XMLResourceDescriptor
				.getXMLParserClassName();

		org.apache.batik.anim.dom.SAXSVGDocumentFactory f = new org.apache.batik.anim.dom.SAXSVGDocumentFactory(
				parser);
		try {
			Document document = f.createDocument(null,
					new ByteArrayInputStream(buf));


			org.apache.batik.transcoder.image.ImageTranscoder t;
			if (JPG.equals(format) || JPEG.equals(format)) {
				t = new org.apache.batik.transcoder.image.JPEGTranscoder();
			} else if (TIFF.equals(format)) {
				t = new org.apache.batik.transcoder.image.TIFFTranscoder();
			} else {
				t = new org.apache.batik.transcoder.image.PNGTranscoder();
			}

			if (width > 0) {
				t.addTranscodingHint(
						org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_WIDTH,
						(float) width);
			}
			if (height > 0) {
				t.addTranscodingHint(
						org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_HEIGHT,
						(float) height);
			}

			org.apache.batik.transcoder.TranscoderInput input = new org.apache.batik.transcoder.TranscoderInput(
					document);
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			org.apache.batik.transcoder.TranscoderOutput output = new org.apache.batik.transcoder.TranscoderOutput(
					ostream);

			t.transcode(input, output);
			ByteArrayInputStream is = new ByteArrayInputStream(
					ostream.toByteArray());
			image = ImageIO.read(is);
			is.close();
			ostream.close();
			iconWidth = image.getWidth();
			iconHeight = image.getHeight();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return image;
	}

}
