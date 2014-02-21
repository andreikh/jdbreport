/*
 * Created on 01.02.2005
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2005-2011 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import and.util.ErrorHandler;

/**
 * @version 2.0 20.01.2011
 * @author Andrey Kholmanskih
 * 
 */
public class Utils {

	private static final Logger logger = Logger.getLogger(Utils.class
			.getName());
	
	private final static String[] BRUSH_STYLE_NAME = { "Solid", "Clear",
			"Horizontal", "Vertical", "FDiagonal", "BDiagonal", "Cross",
			"DiagCross" };

	private static Map<String, Color> colorMap;
	
	private static void initColorMap() {
		colorMap = new HashMap<String, Color>();
		colorMap.put("black", Color.black);
		colorMap.put("silver", Color.lightGray);
		colorMap.put("gray", Color.gray);
		colorMap.put("white", Color.white);
		colorMap.put("maroon", new Color(128, 0, 0));
		colorMap.put("red", Color.red);
		colorMap.put("purple", new Color(128, 0, 128));
		colorMap.put("fuchsia", Color.magenta);
		colorMap.put("green", new Color(0, 128, 0));
		colorMap.put("lime", Color.green);
		colorMap.put("olive", new Color(128, 128, 0));
		colorMap.put("yellow", Color.yellow);
		colorMap.put("navy", new Color(0, 0, 128));
		colorMap.put("blue", Color.blue);
		colorMap.put("teal", new Color(0, 128, 128));
		colorMap.put("aqua", Color.cyan);
	}
	
	private static Map<String, Color> getColorMap() {
		if (colorMap == null) {
			initColorMap();
		}
		return colorMap;
	}
	
	public static ErrorHandler errorHandler = null;

	public static int colorToInt(Color color) {
		return (((color.getBlue() << 8) + color.getGreen()) << 8)
				+ color.getRed();
	}

	public static String colorToHex(Color color) {
		String red = Integer.toHexString(color.getRed());
		if (red.length() == 1) {
			red = "0" + red;
		}
		String green = Integer.toHexString(color.getGreen());
		if (green.length() == 1) {
			green = "0" + green;
		}
		String blue = Integer.toHexString(color.getBlue());
		if (blue.length() == 1) {
			blue = "0" + blue;
		}
		return "#" + red + green + blue;
		
	}
	
	public static String colorToString(Color color) {
		return "" + color.getRed() + "," + color.getGreen() + ","
				+ color.getBlue();
	}

	public static Color colorByName(String color) {
		return getColorMap().get(color.toLowerCase());
	}
	
	public static Color stringToColor(String color) {
		if (color == null)
			return null;
		int r = 0;
		int g = 0;
		int b = 0;
		int ind1 = color.indexOf(',');
		if (ind1 > 0) {
			r = Integer.parseInt(color.substring(0, ind1 - 1));
			int ind2 = color.indexOf(',', ++ind1);
			g = Integer.parseInt(color.substring(ind1, ind2 - 1));
			b = Integer.parseInt(color.substring(ind2 + 1));
		} else if (color.startsWith("#") || color.startsWith("0x") || color.startsWith("0X")) {
			int c = Integer.decode(color);
			r = (c >> 16) & 0xff;
			g = (c >> 8) & 0xff;
			b = (c >> 0) & 0xff;
		} else {
			int c = Integer.parseInt(color);
			b = (c >> 16) & 0xff;
			g = (c >> 8) & 0xff;
			r = (c >> 0) & 0xff;
		}
		return new Color(r, g, b);
	}

	public static int strToBrushStyle(String s) {
		for (int i = 0; i < BRUSH_STYLE_NAME.length; i++) {
			if (s.equals(BRUSH_STYLE_NAME[i]))
				return i;
		}
		return 0;
	}

	/**
	 * 
	 * @param icon
	 * @return RendereImage
	 */
	public static RenderedImage getRenderedImage(ImageIcon icon) {
		RenderedImage image = null;
		if (icon.getImage() instanceof RenderedImage) {
			image = (RenderedImage) icon.getImage();
		} else {
			image = new BufferedImage(icon.getIconWidth(), icon
					.getIconHeight(), BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D g = ((BufferedImage) image).createGraphics();
			icon.paintIcon(null, g, 0, 0);
		}
		return image;
	}

	/**
	 * Center screen
	 * 
	 * @param window
	 */
	public static void screenCenter(Window window) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = window.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		window.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
	}

	public static void screenCenter(Window window, Window parent) {
		Dimension screenSize;
		Point location;
		if (parent == null) {
			screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			location = new Point(0, 0);
		} else {
			screenSize = parent.getSize();
			location = parent.getLocation();
		}
		Dimension frameSize = window.getSize();
		window.setLocation(location.x + (screenSize.width - frameSize.width)
				/ 2, location.y + (screenSize.height - frameSize.height) / 2);
	}

	public static void showError(Throwable e) {
		logger.log(Level.SEVERE, e.getMessage(), e);
		if (errorHandler != null) {
			errorHandler.showError(e);
		}
	}

	public static void showError(String message) {
		logger.log(Level.SEVERE, message);
		if (errorHandler != null) {
			errorHandler.showError(message);
		}
	}

}
