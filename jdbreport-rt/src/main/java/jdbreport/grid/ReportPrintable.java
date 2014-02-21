/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2011 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.grid;

import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.JTable.PrintMode;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import jdbreport.model.DetailGroup;
import jdbreport.model.Group;
import jdbreport.model.ReportModel;
import jdbreport.model.TableRow;
import jdbreport.model.TableRowModel;
import jdbreport.util.GraphicUtil;

/**
 * @version 2.0 12.05.2011
 * @author Andrey Kholmanskih
 * 
 */
public class ReportPrintable implements Printable {

	/** The table to print. */
	private JReportGrid table;

	/** For quick reference to the page header. */
	private Group pageHeader;

	/** For quick reference to the page footer. */
	private Group pageFooter;

	/** For quick reference to the table's column model. */
	private TableColumnModel colModel;

	/** To save multiple calculations of total column width. */
	private int totalColWidth;

	/** The printing mode of this printable. */
	private JTable.PrintMode printMode;

	/** The most recent page index asked to print. */
	private int last = -1;

	/** The next row to print. */
	private int row = 0;

	/** The next column to print. */
	private int col = 0;

	/** Used to store an area of the table to be printed. */
	private final Rectangle clip = new Rectangle(0, 0, 0, 0);

	/** Used to store an area of the page header to be printed. */
	private final Rectangle hclip = new Rectangle(0, 0, 0, 0);

	/** Used to store an area of the page footer to be printed. */
	private final Rectangle fclip = new Rectangle(0, 0, 0, 0);

	/** Saves the creation of multiple rectangles. */
	private final Rectangle tempRect = new Rectangle(0, 0, 0, 0);

	private Map<Integer, PageClip> pageClips = null;

	private int endCol;

	private int currentCol;

	private int endRow;

	private int currentRow;

	private ReportModel tableModel;

	private boolean ltr;

	public ReportPrintable(JReportGrid table, JTable.PrintMode printMode) {
		this(table, printMode, false);
	}

	/**
	 * Creates a new <code>TablePrintable<code> for the given
	 * <code>JTable</code>. Header and footer text can be specified using the
	 * two <code>MessageFormat</code> parameters. When called upon to provide a
	 * String, each format is given the current page number.
	 * 
	 * @param table
	 *            the table to print
	 * @param printMode
	 *            the printing mode for this printable
	 * @param isPreview
	 *            - if true, report will be preview
	 * @throws IllegalArgumentException
	 *             if passed an invalid print mode
	 */
	public ReportPrintable(JReportGrid table, JTable.PrintMode printMode,
			boolean isPreview) {

		this.table = table;

		this.printMode = printMode;

		ltr = table.getComponentOrientation().isLeftToRight();

		tableModel = table.getReportModel();

		if (isPreview) {
			pageClips = new HashMap<Integer, PageClip>();
		}
		init();

	}

	public ReportPrintable(ReportModel model) {

		ltr = ComponentOrientation.getOrientation(Locale.getDefault())
				.isLeftToRight();

		tableModel = model;

		this.printMode = tableModel.getReportPage().isShrinkWidth() ? PrintMode.FIT_WIDTH
				: PrintMode.NORMAL;

		pageClips = new HashMap<Integer, PageClip>();
		init();

	}

	private void init() {
		pageHeader = tableModel.getRowModel().getRootGroup()
				.getGroup(Group.ROW_PAGE_HEADER);
		if (pageHeader != null && pageHeader.getChildCount() == 0)
			pageHeader = null;
		pageFooter = tableModel.getRowModel().getRootGroup()
				.getGroup(Group.ROW_PAGE_FOOTER);
		if (pageFooter != null && pageFooter.getChildCount() == 0)
			pageFooter = null;

		colModel = tableModel.getColumnModel();
		totalColWidth = colModel.getTotalColumnWidth();

		if (pageHeader != null) {
			hclip.height = pageHeader.getHeight();
		}

		if (pageFooter != null) {
			fclip.height = pageFooter.getHeight();
		}
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		boolean showGrid = table.isShowGrid();
		double oldScaleX = GraphicUtil.getScaleX();
		double oldScaleY = GraphicUtil.getScaleY();
		try {
			table.setShowGrid(false);
			GraphicUtil.setScaleX(1);
			GraphicUtil.setScaleY(1);
			final int imgWidth = (int) pageFormat.getImageableWidth();
			final int imgHeight = (int) pageFormat.getImageableHeight();

			if (imgWidth <= 0) {
				throw new PrinterException(
						Messages.getString("ReportPrintable.0")); //$NON-NLS-1$
			}

			if (imgHeight <= 0) {
				throw new PrinterException(
						Messages.getString("ReportPrintable.1")); //$NON-NLS-1$
			}

			double sf = getScaleFactor(imgWidth);

			int scaledWidth = (int) (imgWidth / sf);
			int scaledHeight = (int) (imgHeight / sf);

			if (pageClips != null && pageClips.containsKey(pageIndex)) {
				PageClip pc = pageClips.get(pageIndex);
				col = pc.rightCol;
				row = pc.bottomRow;
				clip.x = pc.clip.x;
				clip.y = pc.clip.y;
				clip.height = pc.clip.height;
				clip.width = pc.clip.width;
			} else {
				if (tableModel.isPrintLeftToRight()) {
					if (!fromLeftToRight(scaledWidth, scaledHeight, pageIndex)) {
						return NO_SUCH_PAGE;
					}
				} else {
					if (!fromTopToDown(scaledWidth, scaledHeight, pageIndex)) {
						return NO_SUCH_PAGE;
					}
				}
			}
			Graphics2D g2d = (Graphics2D) graphics;
			int x = (int) pageFormat.getImageableX();
			int y = (int) pageFormat.getImageableY();
			g2d.translate(x, y);

			tempRect.x = 0;
			tempRect.y = 0;
			tempRect.width = imgWidth;
			tempRect.height = imgHeight;
			g2d.clip(tempRect);

			if (sf != 1.0D) {
				g2d.scale(sf, sf);
			}

			AffineTransform oldTrans = g2d.getTransform();
			Shape oldClip = g2d.getClip();
			table.setState(JReportGrid.PRINT);
			try {
				g2d.translate(-clip.x + 1, -clip.y + 1);
				g2d.clip(clip);
				table.print(g2d);

			} finally {
				table.setState(JReportGrid.PAINT);
				g2d.setTransform(oldTrans);
				g2d.setClip(oldClip);
			}

			return PAGE_EXISTS;

		} finally {
			GraphicUtil.setScaleX(oldScaleX);
			GraphicUtil.setScaleY(oldScaleY);
			table.setShowGrid(showGrid);
		}
	}

	/**
	 * 
	 * @param pageIndex
	 * @return
	 */
	private boolean fromTopToDown(int scaledWidth, int scaledHeight,
			int pageIndex) {
		while (last < pageIndex) {

			if (row == 0 && col >= colModel.getColumnCount()) {
				return false;
			}

			last++;

			findNextTDClip(scaledWidth, scaledHeight, last);

		}
		return true;
	}

	/**
	 * 
	 * @param pageIndex
	 * @return
	 */
	private boolean fromLeftToRight(int scaledWidth, int scaledHeight,
			int pageIndex) {
		while (last < pageIndex) {

			if (col == 0 && row >= tableModel.getRowCount()) {
				return false;
			}

			last++;

			findNextLRClip(scaledWidth, scaledHeight, last);

		}
		return true;
	}

	private double getScaleFactor(final int imgWidth) {
		double sf = 1.0D;
		if (printMode == JTable.PrintMode.FIT_WIDTH && totalColWidth > imgWidth) {

			assert imgWidth > 0;

			assert totalColWidth > 1;

			sf = (double) imgWidth / (double) totalColWidth;
			assert sf > 0;
		}
		return sf;
	}

	/**
	 * Calculate the area of the table to be printed for the next page. This
	 * should only be called if there are rows and columns left to print.
	 * 
	 * To avoid an infinite loop in printing, this will always put at least one
	 * cell on each page.
	 * 
	 * @param pw
	 *            the width of the area to print in
	 * @param ph
	 *            the height of the area to print in
	 */
	private void findNextTDClip(int pw, int ph, int pageIndex) {
		
		if (row == 0) {
			clip.y = 0;
			clip.height = 0;
			currentCol = endCol;
			if (ltr) {
				clip.x += clip.width;
			} else
				clip.x -= clip.width;
		}

		col = currentCol;

		int frow = row;
		int fcol = col; 
		
		if (col == 0) {
			if (ltr) {
				clip.x = 0;
			} else {
				clip.x = totalColWidth;
			}
			clip.width = 0;
		}

		clip.y += clip.height;
		clip.height = 0;
		int rowCount = tableModel.getRowCount();
		int rowHeight = tableModel.getRowHeight(row);
		do {
			clip.height += rowHeight;
			
			if (++row >= rowCount) {
				row = 0;
				break;
			}

			if (isEndPage())
				break;
			rowHeight = tableModel.getRowHeight(row);

		} while (clip.height + rowHeight <= ph);

		if (printMode == JTable.PrintMode.FIT_WIDTH) {
			clip.x = 0;
			clip.width = totalColWidth;
			col = colModel.getColumnCount();
			return;
		}

		clip.width = 0;

		int colCount = colModel.getColumnCount();
		TableColumn column = colModel.getColumn(col);
		double colWidth = column.getWidth();

		do {
			clip.width += colWidth;

			if (++col >= colCount) {
				break;
			}

			if (tableModel.isColumnBreak(col - 1)) {
				break;
			}

			colWidth = colModel.getColumn(col).getWidth();
		} while (clip.width + colWidth <= pw);

		endCol = col;

		if (pageClips != null) {
			pageClips.put(pageIndex, new PageClip(frow, fcol, row == 0 ? rowCount : row, col,  clip));
		}
	}

	/**
	 * Calculate the area of the table to be printed for the next page. This
	 * should only be called if there are rows and columns left to print.
	 * 
	 * To avoid an infinite loop in printing, this will always put at least one
	 * cell on each page.
	 * 
	 * @param pw
	 *            the width of the area to print in
	 * @param ph
	 *            the height of the area to print in
	 */
	private void findNextLRClip(int pw, int ph, int pageIndex) {

		if (col == 0) {
			currentRow = endRow;
			if (ltr) {
				clip.x = 0;
			} else {
				clip.x = totalColWidth;
			}
			clip.width = 0;
			clip.y += clip.height;
		}

		row = currentRow;

		int frow = row;
		int fcol = col; 
		
		if (row == 0) {
			clip.y = 0;
		}

		clip.height = 0;
		int rowCount = tableModel.getRowCount();
		int rowHeight = tableModel.getRowHeight(row);
		do {
			clip.height += rowHeight;
			if (++row >= rowCount) {
				break;
			}

			if (isEndPage())
				break;
			rowHeight = tableModel.getRowHeight(row);

		} while (clip.height + rowHeight <= ph);

		if (printMode == JTable.PrintMode.FIT_WIDTH) {
			clip.x = 0;
			clip.width = totalColWidth;
			col = colModel.getColumnCount();
			return;
		}

		if (ltr) {
			clip.x += clip.width;
		} else
			clip.x -= clip.width;
		clip.width = 0;

		int colCount = colModel.getColumnCount();
		TableColumn column = colModel.getColumn(col);
		double colWidth = column.getWidth();

		do {
			clip.width += colWidth;

			if (++col >= colCount) {
				col = 0;
				break;
			}

			if (tableModel.isColumnBreak(col - 1)) {
				break;
			}

			colWidth = colModel.getColumn(col).getWidth();
		} while (clip.width + colWidth <= pw);

		endRow = row;

		if (pageClips != null) {
			pageClips.put(pageIndex, new PageClip(frow, fcol, row, col == 0 ? colCount : col, clip));
		}
	}

	/**
	 * @return
	 */
	private boolean isEndPage() {
		TableRowModel rowModel = tableModel.getRowModel();
		TableRow tableRow = rowModel.getRow(row - 1);
		if (tableRow.isPageBreak()) {
			return true;
		}
		
		if (rowModel.isCanUpdatePages()) {

			TableRow tableRow0 = rowModel.getRow(row);

			Group group_1 = rowModel.getGroup(row - 1);
			Group group = rowModel.getGroup(row);
			
			if (group.getType() == Group.ROW_PAGE_HEADER 
					&& group_1.getType() != Group.ROW_PAGE_HEADER
					&& group_1.getType() != Group.ROW_TITLE
					&& !tableRow.isPageHeader()
					&& tableRow0.isPageHeader()) {
				return true;
			}
			if (group.getType() == Group.ROW_GROUP_HEADER
					&& ((DetailGroup) group.getParent()).isRepeateHeader()
					&& group_1.getParent() == group.getParent()
					&& !tableRow.isPageHeader()
					&& tableRow0.isPageHeader()) {
				return true;
			}
			if (group_1.getType() == Group.ROW_PAGE_FOOTER
					&& group.getType() != Group.ROW_FOOTER
					&& group.getType() != Group.ROW_PAGE_FOOTER) {
				return true;
			}
		}
		return false;
	}

	public int calcCountPage(PageFormat pageFormat) {
		double oldScaleX = GraphicUtil.getScaleX();
		double oldScaleY = GraphicUtil.getScaleY();
		int pageIndex = 0;
		try {
			GraphicUtil.setScaleX(1);
			GraphicUtil.setScaleY(1);

			final int imgWidth = (int) pageFormat.getImageableWidth();
			final int imgHeight = (int) pageFormat.getImageableHeight();

			double sf = getScaleFactor(imgWidth);

			int scaledWidth = (int) (imgWidth / sf);
			int scaledHeight = (int) (imgHeight / sf);

			if (tableModel.isPrintLeftToRight()) {
				while (true) {
					if (col == 0 && row >= tableModel.getRowCount()) {
						return pageIndex;
					}

					findNextLRClip(scaledWidth, scaledHeight, pageIndex);

					pageIndex++;
				}
			} else {
				while (true) {

					if (row == 0 && col >= colModel.getColumnCount()) {
						return pageIndex;
					}

					findNextTDClip(scaledWidth, scaledHeight, pageIndex);

					pageIndex++;
				}
			}
		} finally {
			GraphicUtil.setScaleX(oldScaleX);
			GraphicUtil.setScaleY(oldScaleY);
		}
	}

	public Map<Integer, PageClip> getPageClips() {
		return pageClips;
	}

	public static class PageClip {

		int bottomRow;
		int rightCol;
		int topRow;
		int leftCol;

		Rectangle clip;

		public PageClip(int frow, int fcol, int row, int col, Rectangle clip) {
			this.topRow = frow;
			this.leftCol = fcol;
			this.bottomRow = row;
			this.rightCol = col;
			this.clip = (Rectangle) clip.clone();
		}

		public int getTopRow() {
			return topRow;
		}

		public int getLeftCol() {
			return leftCol;
		}

		public int getBottomRow() {
			return bottomRow;
		}

		public int getRightCol() {
			return rightCol;
		}
		
		
	}
}
