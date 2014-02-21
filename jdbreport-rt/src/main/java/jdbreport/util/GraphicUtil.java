/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2009 Andrey Kholmanskih. All rights reserved.
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

/**
 * @version 2.0 19.12.2009
 * @author Andrey Kholmanskih
 * 
 */
public class GraphicUtil {

	private static double scale_x = 1;

	private static double scale_y = 1;

	private static double screenScale_x;

	private static double screenScale_y;

	static {
		initResolution();
	}

	private static void initResolution() {
		try {
			java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			java.awt.GraphicsDevice gd = ge.getDefaultScreenDevice();
			java.awt.GraphicsConfiguration gc = gd.getDefaultConfiguration();
			java.awt.geom.AffineTransform at = gc.getNormalizingTransform();
			setScaleX(at.getScaleX());
			setScaleY(at.getScaleY());
			
			screenScale_x = getScaleX();
			screenScale_y = getScaleY();
		} catch (Throwable e) {

		}
	}

	public static double getScreenScaleX() {
		return screenScale_x;
	}
	
	public static double getScreenScaleY() {
		return screenScale_y;
	}

	
	public static double getScaleY() {
		return scale_y;
	}

	public static double getScaleX() {
		return scale_x;
	}

	public static void setScaleX(double scale) {
		scale_x = scale;
	}

	public static void setScaleY(double scale) {
		scale_y = scale;
	}

	public static void setScreenScaleX(double scale) {
		screenScale_x = scale;
	}

	public static void setScreenScaleY(double scale) {
		screenScale_y = scale;
	}

}
