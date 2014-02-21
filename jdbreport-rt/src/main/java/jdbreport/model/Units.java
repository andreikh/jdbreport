/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2008 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.model;

import jdbreport.util.GraphicUtil;

/**
 * @version 1.1 03/09/08
 * 
 * @author Andrey Kholmanskih
 * 
 */
public enum Units {
	PT(1), INCH(72), MM(2.83464566929), CM(28.3464566929), MMx10(0.283464566929);

	private final static String[] units_name = { "pt", "in", "mm", "cm" };

	private final double kf;

	private static Units defaultUnit = PT;

	Units(double kf) {
		this.kf = kf;
	}

	public static Units findUnits(String name) {
		for (int i = 0; i < units_name.length; i++) {
			if (units_name[i].equals(name))
				return Units.values()[i];
		}
		return Units.valueOf(name);
	}

	public static Units getDefaultUnit() {
		return defaultUnit;
	}

	public static void setDefaultUnit(Units unit) {
		defaultUnit = unit;
	}

	/**
	 * 
	 * @param value
	 *            in 1/72 of an inch
	 * @return Returns result in selected units
	 */
	public double getValue(double value) {
		return value / kf;
	}

	/**
	 * 
	 * @param value
	 *            in selected units
	 * @return result in 1/72 of an inch
	 */
	public double setValue(double value) {
		return value * kf;
	}

	/**
	 * 
	 * @param value
	 *            in selected units
	 * @return result in pixels
	 */
	public int getXPixels(double value) {
		return (int) Math.round(setValue(value) * GraphicUtil.getScaleX());
	}

	/**
	 * 
	 * @param value
	 *            in pixels
	 * @return result in selected units
	 */
	public double setXPixels(int value) {
		return getValue(value / GraphicUtil.getScaleX());
	}

	/**
	 * 
	 * @param value
	 *            in selected units
	 * @return Returns result in pixels
	 */
	public int getYPixels(double value) {
		return (int) Math.round(setValue(value) * GraphicUtil.getScaleY());
	}

	/**
	 * 
	 * @param value
	 *            in pixels
	 * @return Returns result in selected units
	 */
	public double setYPixels(int value) {
		return getValue(value / GraphicUtil.getScaleY());
	}

}