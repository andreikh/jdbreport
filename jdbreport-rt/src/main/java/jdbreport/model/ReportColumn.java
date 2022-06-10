/*
 * ReportColumn.java
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
