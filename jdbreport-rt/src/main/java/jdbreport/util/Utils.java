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

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class Utils {

	private static final Logger logger = Logger.getLogger(Utils.class
			.getName());
	
	private final static String[] BRUSH_STYLE_NAME = { "Solid", "Clear",
			"Horizontal", "Vertical", "FDiagonal", "BDiagonal", "Cross",
			"DiagCross" };

	private static char decimalSeparator = '\0';

	private static Map<String, Color> colorMap;
	
	private static void initColorMap() {
		colorMap = new HashMap<>();
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
		if (colorMap == null) initColorMap();
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
		int r;
		int g;
		int b;
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
			b = (c) & 0xff;
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
	 * @param icon ImageIcon
	 * @return RendereImage
	 */
	public static RenderedImage getRenderedImage(ImageIcon icon) {
		RenderedImage image;
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

	public static String getFileExtension(File f) {
		if (f != null) {
			return getFileExtension(f.getName());
		}
		return null;
	}

	public static String getFileExtension(String filename) {
		int i = filename.lastIndexOf('.');
		if (i > 0 && i < filename.length() - 1) {
			return filename.substring(i + 1).toLowerCase();
		}
		return "";
	}

	public static String changeFileExtension(String fileName,
											 String newExtension) {
		int i = fileName.lastIndexOf('.');
		if (i > 0 && i < fileName.length() - 1) {
			return fileName.substring(0, i) + newExtension;
		}
		return fileName + "." + newExtension;
	}

	public static String extractFileName(String filePath) {
		int i = filePath.lastIndexOf('/');
		if (i < 0)
			i = filePath.lastIndexOf('\\');
		if (i < 0)
			return filePath;
		return filePath.substring(i + 1);
	}

	public static String extractFilePath(String filePath) {
		int i = filePath.lastIndexOf('/');
		if (i < 0)
			i = filePath.lastIndexOf('\\');
		if (i < 0)
			return "";
		return filePath.substring(0, i + 1);
	}

	/**
	 * Turn of a point round other point counter-clockwise
	 *
	 * @param x0 coordinate x central point
	 * @param y0 coordinate y central point
	 * @param x coordinate x source point
	 * @param y coordinate y source point
	 * @param a angle of rotation in radians
	 * @return result point
	 */
	public static Point2D.Double rotatePoint(double x0, double y0, double x,
											 double y, double a) {
		Point2D.Double p = new Point2D.Double();
		double sina = Math.sin(a);
		double cosa = Math.cos(a);

		p.x = x0 + (x - x0) * cosa + (y0 - y) * sina;
		p.y = y0 + (x - x0) * sina + (y - y0) * cosa;
		return p;
	}

	public static String html2Plain(String text) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) != '&' || i >= text.length() - 2) {
				result.append(text.charAt(i));
			} else {
				i++;
				if (text.charAt(i) != '#') {
					result.append('&');
					result.append(text.charAt(i));
					continue;
				}
				int b = ++i;
				while (text.charAt(i) != ';' && i < text.length()) {
					i++;
				}
				if (text.charAt(i) == ';') {
					try {
						int ch = Integer.parseInt(text.substring(b, i));
						result.append((char) ch);
					} catch (Exception e) {
						result.append(text.substring(b - 2, i));
					}
				} else {
					result.append(text.substring(b - 2));
				}

			}
		}
		return result.toString();
	}

	public static char getDecimalSeparator() {
		if (decimalSeparator == '\0') {
			DecimalFormat df = new DecimalFormat();
			decimalSeparator = df.getDecimalFormatSymbols()
					.getDecimalSeparator();
		}
		return decimalSeparator;
	}

	public static double round(double value) {
		return round(value, 0);
	}

	public static double round(double value, int decimal) {
		long d = 1;
		int c = decimal < 0 ? -decimal : decimal;
		for (int i = 0; i < c; i++) {
			d = d * 10;
		}
		if (decimal >= 0) {
			return (double) Math.round(value * d) / d;
		}
		return Math.round(value / d) * d;
	}

	public static float round(float value, int decimal) {
		int d = 1;
		int c = decimal < 0 ? -decimal : decimal;
		for (int i = 0; i < c; i++) {
			d = d * 10;
		}
		if (decimal >= 0) {
			int v = Math.round(value * d);
			return 1.0f * v / d;
		}
		return Math.round(value / d) * d;
	}

	public static String roundStr(Double value, int decimal) {
		return String.format("%1." + decimal + "f", value);
	}

	public static double parseDouble(String s) {
		if (s == null)
			return 0;
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			char ds = s.indexOf(',') >= 0 ? '.' : ',';
			char oldChar = ds == ',' ? '.' : ',';
			s = s.replace(oldChar, ds);
			return Double.parseDouble(s);
		}
	}

	/**
	 * Fill array from stream
	 *
	 * @param in input stream
	 * @param b bytes
	 * @return count bytes reads from stream
	 * @throws java.io.IOException
	 */
	public static int readBytes(InputStream in, byte[] b) throws IOException {
		int l = b.length;
		int pos = 0;
		int n;
		do {
			n = in.read(b, pos, l);
			if (n > 0) {
				pos += n;
				l -= n;
			}
		} while (n > 0 && l > 0);
		return pos;
	}

}
