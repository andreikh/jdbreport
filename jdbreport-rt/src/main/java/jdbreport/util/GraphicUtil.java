/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2014 Andrey Kholmanskih
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
package jdbreport.util;

import java.awt.*;

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
		} catch (Throwable ignored) {

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

	public static void setChildFont(Container parent, Font font) {
		for (int i = 0; i < parent.getComponentCount(); i++) {
			Component c = parent.getComponent(i);
			c.setFont(font);
			if (c instanceof Container) {
				setChildFont((Container) c, font);
			}
		}
	}


}
