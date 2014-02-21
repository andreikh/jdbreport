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
package jdbreport.model.print;

import java.awt.print.PageFormat;
import java.awt.print.Paper;

import jdbreport.model.Units;

/**
 * @version 1.1 03/09/08
 * 
 * @author Andrey Kholmanskih
 * 
 */
public class ReportPage extends PageFormat { 

	public enum PaperSize {
		A3(841.9, 1190.6), A4(595.3, 841.9), A5(419.5, 595.3), Letter(612, 792), User(
				612, 792);

		private PaperSize(double width, double height) {
			this.width = width;
			this.height = height;
		}

		private double width;

		private double height;

		public double getWidth() {
			return width;
		}

		public double getHeight() {
			return height;
		}

	}

	public static PaperSize findPaperSize(double width, double height) {
		for (PaperSize ps : PaperSize.values()) {
			if (Math.abs(ps.getWidth() - width) <= 1  && 
					Math.abs(ps.getHeight() - height) <= 1)
				return ps;
		}
		return PaperSize.User;
	}

	private boolean shrinkWidth;
	private int copies = 1;
	private PaperSize paperSize = PaperSize.User;

	public boolean isShrinkWidth() {
		return shrinkWidth;
	}

	public void setShrinkWidth(boolean shrinkWidth) {
		this.shrinkWidth = shrinkWidth;
	}

	public double getHeight(Units unit) {
		return unit.getValue(getHeight());
	}

	public double getImageableHeight(Units unit) {
		return unit.getValue(getImageableHeight());
	}

	public double getImageableWidth(Units unit) {
		return unit.getValue(getImageableWidth());
	}

	public double getWidth(Units unit) {
		return unit.getValue(getWidth());
	}

	public double getLeftMargin(Units unit) {
		return unit.getValue(getImageableX());
	}

	public double getTopMargin(Units unit) {
		return unit.getValue(getImageableY());
	}

	public double getRightMargin(Units unit) {
		return unit.getValue(getWidth()
				- (getImageableX() + getImageableWidth()));
	}

	public double getBottomMargin(Units unit) {
		return unit.getValue(getHeight()
				- (getImageableY() + getImageableHeight()));
	}

	public void setSize(double width, double height, Units unit) {
		if (width > 0 && height > 0) {
			width = unit.setValue(width);
			height = unit.setValue(height);
			this.paperSize = findPaperSize(width, height);
			setSize(width, height);
		}
	}

	/**
	 * Sets size in 1/72 inch
	 * 
	 * @param width
	 * @param height
	 */
	private void setSize(double width, double height) {
		Paper paper = getPaper();
		paper.setSize(Math.min(width, height), Math.max(width, height));
		setPaper(paper);
	}

	public void setMargin(double left, double top, double right, double bottom,
			Units unit) {
		Paper paper = getPaper();
		if (getOrientation() == PORTRAIT) {
			paper.setImageableArea(unit.setValue(left), unit.setValue(top),
					paper.getWidth() - unit.setValue(left + right), paper
							.getHeight()
							- unit.setValue(top + bottom));
		} else if (getOrientation() == LANDSCAPE) {
			paper.setImageableArea(unit.setValue(top), unit.setValue(right),
					paper.getWidth() - unit.setValue(top + bottom), paper
							.getHeight()
							- unit.setValue(left + right));
		}
		setPaper(paper);
	}

	public int getCopies() {
		return copies;
	}

	public void setCopies(int copies) {
		this.copies = copies;
	}

	public PaperSize getPaperSize() {
		return paperSize;
	}

	public void setPaperSize(PaperSize paperSize) {
		if (paperSize == null)
			return;
		this.paperSize = paperSize;
		if (paperSize != PaperSize.User)
			setSize(paperSize.getWidth(), paperSize.getHeight());
	}

}
