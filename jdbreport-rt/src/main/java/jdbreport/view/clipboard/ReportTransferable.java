/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2010 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.view.clipboard;

import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * @version 2.0 01.06.2010
 * @author Andrey Kholmanskih
 * 
 */
public class ReportTransferable implements Transferable, ClipboardOwner {

	public static final String FLAVOR_MIME_TYPE = "application/x-java-serialized-object; class=\"[B\"";//"text/xml; class=java.lang.String; charset=Unicode";
	public static final String FLAVOR_XML_MIME_TYPE = "text/xml; class=java.lang.String; charset=Unicode";
	public static final String FLAVOR_XML_BYTE_MIME_TYPE = "text/xml; class=\"[B\"; charset=UTF-8";

	
	public enum TypeFlavor {
		xml, text, image
	}

	private List<DataFlavor> dataFlavor;
	private List<Object> copyData;

	public ReportTransferable() {
		super();
		dataFlavor = new ArrayList<DataFlavor>();
		copyData = new ArrayList<Object>();
	}

	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] result = new DataFlavor[dataFlavor.size()];
		for (int i = 0; i < dataFlavor.size(); i++) {
			result[i] = dataFlavor.get(i);
		}
		return result;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (flavor.getRepresentationClass() == java.lang.String.class || flavor
				.getRepresentationClass() == Image.class);
	}

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		int i = dataFlavor.indexOf(flavor);
		if (flavor.getPrimaryType().equals("image")) {
			if (flavor.getSubType().equals("bmp")
					|| flavor.getSubType().equals("jpeg")) {
				ByteArrayOutputStream pipeOut = new ByteArrayOutputStream();
				ImageIO.write((RenderedImage) copyData.get(i), flavor
						.getSubType(), pipeOut);
				ByteArrayInputStream pipeIn = new ByteArrayInputStream(pipeOut
						.toByteArray());
				return pipeIn;
			} else
				return copyData.get(i);

		} else
			return copyData.get(i);
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		if (contents != this) {
			copyData.clear();
			dataFlavor.clear();
		}
	}

	/**
	 * @param copyData
	 *            The copyData to set.
	 */
	public void addCopyData(Object copyData, TypeFlavor type) {
		if (type == TypeFlavor.xml) {
			this.copyData.add(copyData);
			try {
				DataFlavor flavor;
				if (copyData instanceof String) {
					flavor = new DataFlavor(FLAVOR_XML_MIME_TYPE);
				} else {
					flavor = new DataFlavor(FLAVOR_MIME_TYPE);	
				}
				dataFlavor.add(flavor);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else if (type == TypeFlavor.text) {
			this.copyData.add(copyData);
			try {
				DataFlavor flavor = new DataFlavor(
						"text/plain;class=java.lang.String");
				dataFlavor.add(flavor);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else if (type == TypeFlavor.image) {
			this.copyData.add(copyData);
			dataFlavor.add(DataFlavor.imageFlavor);
			try {
				DataFlavor flavor = new DataFlavor(
						"image/bmp;class=java.io.InputStream");
				flavor.setHumanPresentableName("image/bmp");
				dataFlavor.add(flavor);
				this.copyData.add(copyData);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}
