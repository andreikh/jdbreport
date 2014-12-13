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