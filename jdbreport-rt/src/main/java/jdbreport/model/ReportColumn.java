/*
 * ReportColumn.java
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

package jdbreport.model;

/**
 * @version 1.1 03/09/08
 * 
 * @author Andrey Kholmanskih
 * 
 */
import javax.swing.table.*;

public class ReportColumn extends TableColumn {

	public static final int DEFAULT_COLUMN_WIDTH = 75;

	private static final long serialVersionUID = 6313794685838794165L;

	private static Units unit = Units.PT;

	/**
	 * Column width in units 
	 */
	private double width = DEFAULT_COLUMN_WIDTH;

	private boolean pageBreak;

	/** Creates a new instance of ReportColumn */
	public ReportColumn(int modelIndex, int width) {
		super(modelIndex, width);
	}

	public Object getHeaderValue() {
		return "" + (modelIndex + 1);
	}

	public boolean isPageBreak() {
		return pageBreak;
	}

	public void setPageBreak(boolean b) {
		this.pageBreak = b;
	}

	@Override
	public int getPreferredWidth() {
		return unit.getXPixels(width);
	}

	@Override
	public void setPreferredWidth(int preferredWidth) {
		if (preferredWidth < 1) {
			preferredWidth = 1;
		}
		super.setPreferredWidth(preferredWidth);
		this.width = unit.setXPixels(preferredWidth);
	}

	@Override
	public int getWidth() {
		return unit.getXPixels(width);
	}

	@Override
	public void setWidth(int width) {
		if (width < 1) {
			width = 1;
		}
		super.setWidth(width);
		this.width = unit.setXPixels(width);
	}

	/**
	 * 
	 * @param w
	 *            new width of a column in 1/72 inches
	 */
	public void setWidth(double w) {
		if (w < 2) {
			w = 2;
		}
		this.width = w;
		super.setWidth(unit.getXPixels(w));
		super.setPreferredWidth(unit.getXPixels(w));
	}

	/**
	 * 
	 * @return width of a column in 1/72 inches
	 */
	public double getNativeWidth() {
		return this.width;
	}

}
