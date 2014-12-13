/**
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

import java.awt.Graphics2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import jdbreport.util.GraphicUtil;

import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.wmf.tosvg.WMFPainter;
import org.apache.batik.transcoder.wmf.tosvg.WMFRecordStore;
/**
 * @author Andrey Kholmanskih
 *
 * @version	1.0 15.10.2010
 */
public class PictureWMF extends PictureSVG {

	private WMFRecordStore currentStore;

	/**
	 * @param format
	 */
	public PictureWMF(String format) {
		super(format);
	}

	/**
	 * @param buf
	 */
	public PictureWMF(byte[] buf) {
		super(buf);
	}

	/**
	 * @param buf
	 * @param format
	 */
	public PictureWMF(byte[] buf, String format) {
		super(buf, format);
	}

	@Override
	public void setBuf(byte[] buf) {
		super.setBuf(buf);
		createRecordStore();
	}

	@Override
	public BufferedImage createImage(int width, int height, String format) {
		DataInputStream is = new DataInputStream(new ByteArrayInputStream(buf));
		WMFRecordStore currentStore = new WMFRecordStore();
		try {
			currentStore.read(is);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (width == 0) {
			width = currentStore.getWidthPixels();
		}
		if (height == 0) {
			height = currentStore.getHeightPixels();
		}
		
		float scalew = (float)width / (float)currentStore.getWidthPixels() / (float)GraphicUtil.getScreenScaleX();
		
		float scaleh = (float)height / (float)currentStore.getHeightPixels()  / (float)GraphicUtil.getScreenScaleY();
		
		float scale = Math.min(scalew, scaleh);
		
		WMFPainter painter = new WMFPainter(currentStore, scale);
		PNGTranscoder t = new org.apache.batik.transcoder.image.PNGTranscoder();
		BufferedImage image = t.createImage(width, height);
		painter.paint(image.getGraphics());
		return image;

	}
	
	@Override
	public String getXML() {
		return null;
	}
	
	@Override
	public void paint(Graphics2D g2, int width, int height) {
		if (buf == null) return;
		
		if (currentStore == null) {
			createRecordStore();
		}
		
		if (width == 0) {
			width = currentStore.getWidthPixels();
		}
		if (height == 0) {
			height = currentStore.getHeightPixels();
		}
		
		float scalew = (float)width / (float)currentStore.getWidthPixels();
		
		float scaleh = (float)height  / (float)currentStore.getHeightPixels() ; 
		
		float scale = Math.min(scalew, scaleh);
		
		WMFPainter painter = new WMFPainter(currentStore, scale);
		painter.paint(g2);
	}

	protected void createRecordStore() {
		currentStore = null;
		if (buf == null) return;
		DataInputStream is = new DataInputStream(new ByteArrayInputStream(buf));
		currentStore = new WMFRecordStore();
		try {
			currentStore.read(is);
			width = currentStore.getWidthPixels();
			height = currentStore.getHeightPixels();
			iconWidth = width;
			iconHeight = height;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void paint(Graphics2D g2) {
		this.paint(g2, 0, 0);
	}

	@Override
	public void paint(java.awt.Graphics2D g2, AffineTransformOp aop ) {
		g2.transform(aop.getTransform());
		this.paint(g2);
	}

}
