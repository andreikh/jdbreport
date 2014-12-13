/*
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2014 Andrey Kholmanskih
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
 * 
 */
class ColumnStyle extends CommonStyle {

	private double width;
	private boolean optimalWidth;

	/**
	 * 
	 * @param width
	 *            width of column in 1/72 of an inch
	 * @param brk break type
	 * @param after break_after
	 * @param optimalWidth optimal width flag
	 */
	public ColumnStyle(double width, Break brk, boolean after,
			boolean optimalWidth) {
		super(brk, after);
		this.width = width;
		this.optimalWidth = optimalWidth;
	}

	/**
	 * Returns the width of the column in 1/72nds of an inch.
	 * 
	 * @return the width of the column
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * specifies that the column width should be recalculated automatically if
	 * some content in the column changes
	 * 
	 * @return true if set automatically width
	 */
	public boolean isOptimalWidth() {
		return optimalWidth;
	}
}
