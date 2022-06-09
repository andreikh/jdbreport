/*
 * Created	14.10.2010
 * JDBReport Generator
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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import jdbreport.util.Utils;

/**
 * @author Andrey Kholmanskih
 * 
 * @version 3.0 12.12.2014
 */
public class Picture {

	public static final String PNG = "png";
	public static final String JPG = "jpg";
	public static final String JPEG = "jpeg";
	
	protected byte[] buf;

	protected String format = PNG;
	private boolean scale;

	protected ImageIcon icon;

	protected int iconHeight;

	protected int iconWidth;

	protected int height;

	protected int width;

	public Picture() {
	}

	public Picture(String format) {
		if (format != null) {
			this.format = format;
		}
	}

	public Picture(ImageIcon value) {
		this.icon =  value;
		width = icon.getIconWidth();
		height = icon.getIconHeight();
		iconWidth = width;
		iconHeight = height;
	}

	public Picture(Image image) {
		icon = new ImageIcon(image);
		width = icon.getIconWidth();
		height = icon.getIconHeight();
		iconWidth = width;
		iconHeight = height;
	}

	public Picture(byte[] buf) {
		this.buf = buf;
	}
	
	public Picture(byte[] buf, String format) {
		super();
		this.format = format;
		setBuf(buf);
	}

	public byte[] getBuf() {
		if (buf == null && icon != null) {
			createBuf();
		}
		return buf;
	}

	protected void createBuf() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		if (format == null || !checkImageWriterFormat(format)) {
			format = PNG;
		}
		RenderedImage image = Utils.getRenderedImage(icon);
		try {
			if (ImageIO.write(image, format, stream)) {
				buf = stream.toByteArray();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setBuf(byte[] buf) {
		this.buf = buf;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public boolean isScale() {
		return scale;
	}

	public void setScale(boolean scale) {
		this.scale = scale;
	}

	public synchronized ImageIcon getIcon(int w, int h) {
		if (iconHeight != h || iconWidth != w) {
			icon = createIcon(w, h);
		}
		return icon;
	}
	
	public ImageIcon getIcon() {
		if (icon == null) {
			Image image = createImage();
			icon = new ImageIcon(image);
			width = icon.getIconWidth();
			height = icon.getIconHeight();
			iconWidth = width;
			iconHeight = height;
		} else if (iconWidth != width || iconHeight != height){
			icon = getIcon(width, height); 
		}
		return icon;
	}

	public Image getImage() {
		return getIcon().getImage();
	}

	public RenderedImage getRenderedImage(int w, int h) {
		return getRenderedImage(getIcon(w, h));
	}

	public RenderedImage getRenderedImage() {
		return getRenderedImage(getIcon());
	}

	private RenderedImage getRenderedImage(ImageIcon icon) {
		RenderedImage image;
		if (icon.getImage() instanceof RenderedImage) {
			image = (RenderedImage) icon.getImage();
		} else {
			image = new BufferedImage(icon.getIconWidth(),
					icon.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g = ((BufferedImage) image).createGraphics();
			icon.paintIcon(null, g, 0, 0);
			icon.setImage((BufferedImage) image);
		}
		return image;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}


	protected ImageIcon createIcon(int w, int h) {
		buf = getBuf();
		if (buf == null) return null;
		icon = null;
		Image image = createImage();
		if (w != width || h != height) {
			image = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
		}
		icon = new ImageIcon(image);
		iconWidth = w;
		iconHeight = h;
		return icon;
	}

	public BufferedImage createImage() {
		ByteArrayInputStream stream = new ByteArrayInputStream(getBuf());
		try {
			BufferedImage image = ImageIO.read(stream);
			width = image.getWidth();
			height = image.getHeight();
			return image;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected boolean checkImageWriterFormat(String format) {
		String[] formats = ImageIO.getWriterFormatNames();
		for (String format1 : formats) {
			if (format.equals(format1)) {
				return true;
			}
		}
		return false;
	}

	public void saveToFile(File file, String format) throws IOException {
		RenderedImage image;
		
		if (icon == null || width != iconWidth || height != iconHeight) {
			image = createImage();
		} else {
			image = getRenderedImage();
		}
		ImageIO.write(image, format, file);
	}

	public void load(InputStream is) throws IOException {
			byte[] b = new byte[1024 * 1024];
			byte[] image = new byte[0];
			int i;
			while ((i = Utils.readBytes(is, b)) > 0) {
				byte[] image2 = image;
				image = new byte[image2.length + i];
				if (image2.length > 0) {
					System.arraycopy(image2, 0, image, 0, image2.length);
				}
				System.arraycopy(b, 0, image, image2.length, i);
			}
			icon = null;
			width = 0;
			height = 0;
			iconWidth = 0;
			iconHeight = 0;
			setBuf(image);
	}

	public void load(File file) throws IOException {
		try (InputStream is = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
			load(is);
		}
	}

	public void paint(Graphics2D g2, int width, int height) {
		RenderedImage img = getRenderedImage(width, height);
		g2.drawImage((BufferedImage) img, 0, 0, null);
	}
	
	public void paint(Graphics2D g2) {
		g2.drawImage(getImage(), 0, 0, null);
	}

	public void paint(java.awt.Graphics2D g2, AffineTransformOp aop ) {
		BufferedImage img = (BufferedImage) getRenderedImage();
		g2.drawImage(img, aop, 0, 0);
	}

}
