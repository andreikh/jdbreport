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
class RowStyle extends CommonStyle {

	private double height;
	private boolean optimalHeight;

	/**
	 * @param height -
	 *            height of row in 1/72 of an inch
	 * @param brk break type
	 * @param after break_after
	 * @param optimalHeight optimal width flag
	 */
	public RowStyle(double height, Break brk, boolean after,
			boolean optimalHeight) {
		super(brk, after);
		this.height = height;
		this.optimalHeight = optimalHeight;
	}

	/**
	 * Returns the height of the row in 1/72nds of an inch.
	 * 
	 * @return row height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Specifies that the row height should be recalculated automatically if
	 * some content in the row changes
	 * 
	 * @return true if set automatically height
	 */
	public boolean isOptimalHeight() {
		return optimalHeight;
	}
}
