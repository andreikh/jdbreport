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
class ColumnStyle extends CommonStyle {

	private double width;
	private boolean optimalWidth;

	/**
	 * 
	 * @param width
	 *            width of column in 1/72 of an inch
	 * @param brk
	 * @param after
	 * @param optimalWidth
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
