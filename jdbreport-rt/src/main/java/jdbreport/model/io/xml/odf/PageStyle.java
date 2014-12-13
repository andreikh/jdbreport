/*
 * PageStyle.java 04.11.2006
 *
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
