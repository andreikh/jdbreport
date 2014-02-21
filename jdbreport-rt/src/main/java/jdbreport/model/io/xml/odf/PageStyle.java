/*
 * PageStyle.java 04.11.2006
 *
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
package jdbreport.model.io.xml.odf;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 */
class PageStyle {

	private double width;
	private double height;
	private double top;
	private double bottom;
	private double left;
	private double right;
	private int orientation;
	private int firstPage = 1;
	private int scale = 100;

	public PageStyle(double width, double height, double top, double bottom,
			double left, double right, int orientation) {
		this.width = width;
		this.height = height;
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
		this.orientation = orientation;
	}

	/**
	 * @return the bottom
	 */
	public double getBottom() {
		return bottom;
	}

	/**
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * @return the left
	 */
	public double getLeft() {
		return left;
	}

	/**
	 * @return the orientation
	 */
	public int getOrientation() {
		return orientation;
	}

	/**
	 * @return the right
	 */
	public double getRight() {
		return right;
	}

	/**
	 * @return the top
	 */
	public double getTop() {
		return top;
	}

	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	public int getFirstPage() {
		return firstPage;
	}

	public void setFirstPage(int firstPage) {
		this.firstPage = firstPage;

	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

}
