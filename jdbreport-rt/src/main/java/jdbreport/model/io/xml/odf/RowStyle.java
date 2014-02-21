/*
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2008 Andrey Kholmanskih. All rights reserved.
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
 * 
 */
class RowStyle extends CommonStyle {

	private double height;
	private boolean optimalHeight;

	/**
	 * @param height -
	 *            height of row in 1/72 of an inch
	 * @param brk
	 * @param after
	 * @param optimalHeight
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
