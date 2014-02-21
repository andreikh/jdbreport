/*
 * Created on 19.09.2004
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2012 Andrey Kholmanskih. All rights reserved.
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

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicTableUI;

import jdbreport.model.Border;
import jdbreport.model.Cell;
import jdbreport.model.CellStyle;
import jdbreport.model.Group;
import jdbreport.model.TableRow;
import jdbreport.model.TableRowModel;

/**
 * @version 2.0 18.04.2012
 * @author Andrey Kholmanskih
 * 
 */
public class BasicReportUI extends BasicTableUI {

	private JReportGrid report;
	private boolean isLeftToRight = true;
	private int rm = 2;
	private int cm = 2;
	private TableColumnModel columnModel;
	private TableRowModel rowModel;

	public static ComponentUI createUI(JComponent c) {
		return new BasicReportUI();
	}

	public void installUI(JComponent c) {
		super.installUI(c);
		if (c instanceof JReportGrid)
			report = (JReportGrid) c;
		else
			report = null;
	}

	@Override
	public void uninstallUI(JComponent c) {
		report = null;
		super.uninstallUI(c);
	}

	public void paint(Graphics g, JComponent c) {
		if (report == null) {
			super.paint(g, c);
			return;
		}
		if (table.getRowCount() <= 0 || table.getColumnCount() <= 0) {
			return;
		}

		isLeftToRight = c.getComponentOrientation().isLeftToRight();

		Rectangle clip = g.getClipBounds();
		Point upperLeft = clip.getLocation();
		Point lowerRight = new Point(clip.x + clip.width - 2, clip.y
				+ clip.height - 2);
		int rMin = table.rowAtPoint(upperLeft);
		int rMax = table.rowAtPoint(lowerRight);
		if (rMin == -1) {
			rMin = 0;
		}
		if (rMax == -1) {
			rMax = table.getRowCount() - 1;
		}

		int cMin = table.columnAtPoint(isLeftToRight ? upperLeft : lowerRight);
		int cMax = table.columnAtPoint(isLeftToRight ? lowerRight : upperLeft);
		if (cMin == -1) {
			cMin = 0;
		}
		if (cMax == -1) {
			cMax = table.getColumnCount() - 1;
		}

		// Paint the cells.
		paintCells(g, rMin, rMax, cMin, cMax);
		
		
	}

	private int viewIndexForColumn(TableColumn aColumn) {
		for (int column = 0; column < columnModel.getColumnCount(); column++) {
			if (columnModel.getColumn(column) == aColumn) {
				return column;
			}
		}
		return -1;
	}

	private int viewIndexForRow(TableRow aRow) {
		for (int row = 0; row < rowModel.getRowCount(); row++) {
			if (rowModel.getRow(row) == aRow) {
				return row;
			}
		}
		return -1;
	}

	private void paintCells(Graphics g, int rMin, int rMax, int cMin, int cMax) {
		if (report.isPrintState()) {
			printCells(g, rMin, rMax, cMin, cMax);
			return;
		}

		JTableHeader header = table.getTableHeader();
		columnModel = table.getColumnModel();
		cm = columnModel.getColumnMargin();
		rowModel = report.getReportModel().getRowModel();

		TableColumn draggedColumn = (header == null) ? null : header
				.getDraggedColumn();
		RowHeader rowHeader = report.getRowHeader();
		TableRow draggedRow = (rowHeader == null) ? null : rowHeader
				.getDraggedRow();

		int pageHeaderIndex = -1;
		int pageFooterIndex = -1;
		Rectangle rect = new Rectangle();
		for (int i = 0; i < rMin; i++) {
			rect.y += rowModel.getRow(i).getHeight();
		}

		for (int row = rMin; row <= rMax; row++) {
			TableRow tableRow = rowModel.getRow(row);
			if (tableRow != draggedRow) {
				int type = Group.ROW_NONE;
				if (tableRow.getGroup() != null) {
					type = tableRow.getGroup().getType();
				}
				if (type == Group.ROW_PAGE_HEADER
						|| type == Group.ROW_PAGE_FOOTER) {
					if (pageHeaderIndex < 0) {
						pageHeaderIndex = row;
					}
					pageFooterIndex = row;
				} else {
					paintRow(g, rMin, cMin, cMax, draggedColumn, row, tableRow,
							rect);
				}
			}
			rect.y += tableRow.getHeight();
		}

		if (pageHeaderIndex >= 0) {
			rect.y = 0;
			for (int i = 0; i < pageHeaderIndex; i++) {
				rect.y += report.getReportModel().getRowHeight(i);
			}
			for (int row = pageHeaderIndex; row <= pageFooterIndex; row++) {
				TableRow aRow = rowModel.getRow(row);
				Group group = aRow.getGroup();
				if (group != null
						&& (group.getType() == Group.ROW_PAGE_HEADER || group
								.getType() == Group.ROW_PAGE_FOOTER)) {
					paintRow(g, rMin, cMin, cMax, draggedColumn, row, aRow,
							rect);
				}
				rect.y += aRow.getHeight();
			}
		}

		if (!report.isPrintState()) {
			for (int column = cMin; column <= cMax; column++) {
				if (report.getReportModel().isColumnBreak(column)) {
					if (header != null && rowHeader != null) {
						Rectangle r = rowHeader.getHeaderRect(rMax);
						Rectangle rc = header.getHeaderRect(column);
						paintColEndPage(g, rc.x + rc.width - 1, r.y + r.height);
					}
				}
			}
		}

		if (draggedColumn != null && header != null) {
			paintDraggedArea(g, rMin, rMax, draggedColumn, header
					.getDraggedDistance());
		} else if (draggedRow != null && rowHeader != null) {
			paintRowDraggedArea(g, cMin, cMax, draggedRow, rowHeader
					.getDraggedDistance());
		}

		rendererPane.removeAll();
		columnModel = null;
		rowModel = null;
	}

	private void printCells(Graphics g, int rMin, int rMax, int cMin, int cMax) {
		columnModel = table.getColumnModel();
		cm = columnModel.getColumnMargin();
		rowModel = report.getReportModel().getRowModel();

		Rectangle rect = new Rectangle();
		for (int i = 0; i < rMin; i++) {
			rect.y += rowModel.getRow(i).getHeight();
		}

		for (int row = rMin; row <= rMax; row++) {
			TableRow tableRow = rowModel.getRow(row);
			printRow(g, rMin, cMin, cMax, row, tableRow, rect);
			rect.y += tableRow.getHeight();
		}

		printTopBorders(g, rMin, rMax, cMin, cMax);
		printBottomBorders(g, rMin, rMax, cMin, cMax);
		printLeftBorders(g, rMin, rMax, cMin, cMax);
		printRightBorders(g, rMin, rMax, cMin, cMax);


		rendererPane.removeAll();
		columnModel = null;
		rowModel = null;
	}

	private void printTopBorders(Graphics g, int rMin, int rMax, int cMin,
			int cMax) {
		
		int position = Border.LINE_TOP;
		
		int y0 = 0;
		for (int i = 0; i < rMin; i++) {
			y0 += rowModel.getRow(i).getHeight();
		}
		
		int x0 = 0;
		if (isLeftToRight) {
			for (int i = 0; i < cMin; i++) {
				x0 += columnModel.getColumn(i).getWidth();
			}
		} else {
			for (int i = columnModel.getColumnCount() - 1; i > cMin; i--) {
				x0 += columnModel.getColumn(i).getWidth();
			}
		}
		
		int x1 = 0;
		int y1 = 0;
		int x2 = 0;
		y1 = y0;
		
		
		CellStyle[] oldStyles = new CellStyle[cMax - cMin + 1];
		
		for (int row = rMin; row <= rMax; row++) {
			Border line = null;
			x1 = x0;
			x2 = x0;
			for (int col = cMin; col <= cMax; col++) {
				Border newLine = null;
				CellStyle style = null;
				Cell cell = report.getReportModel().getReportCell(row, col);
				if (cell.isChild()) {
					int r = report.getReportModel().getOwnerRow(cell, row, col);
					if (r == row) {
						style = report.getCellStyle(cell.getOwner().getStyleId());
						newLine = style.getBorders(position);
					}
				} else {
					style = report.getCellStyle(cell.getStyleId());
					newLine = style.getBorders(position);
				}
				if (newLine != null && oldStyles[col - cMin] != null) {
					Border oldLine = oldStyles[col - cMin].getBorders(Border.LINE_BOTTOM);
					if (newLine.equals(oldLine)) {
						newLine = null;
					}
				}
				if (line != null && !line.equals(newLine)) {
					drawLine((Graphics2D) g, line, x1, y1, x2, y1);
					x1 = x2;
					line = newLine;
				} else if (line == null) {
					line = newLine;
					x1 = x2;
				}
				
				x2 += columnModel.getColumn(col).getWidth();
				oldStyles[col - cMin] = style;
			}
			if (line != null) {
				drawLine((Graphics2D) g, line, x1, y1, x2, y1);
			}
			y1 += rowModel.getRowHeight(row);
		}
	}

	private void printBottomBorders(Graphics g, int rMin, int rMax, int cMin,
			int cMax) {
		
		int position = Border.LINE_BOTTOM;
		
		int y0 = 0;
		for (int i = 0; i < rMin; i++) {
			y0 += rowModel.getRow(i).getHeight();
		}
		
		int x0 = 0;
		if (isLeftToRight) {
			for (int i = 0; i < cMin; i++) {
				x0 += columnModel.getColumn(i).getWidth();
			}
		} else {
			for (int i = columnModel.getColumnCount() - 1; i > cMin; i--) {
				x0 += columnModel.getColumn(i).getWidth();
			}
		}
		
		int x1 = 0;
		int y1 = 0;
		int x2 = 0;
		y1 = y0;
		
		for (int row = rMin; row <= rMax; row++) {
			Border line = null;
			x1 = x0;
			x2 = x0;
			int rowHeight = rowModel.getRowHeight(row);
			y1 += rowHeight;
			if (row == rMax) y1--;
			for (int col = cMin; col <= cMax; col++) {
				Border newLine = null;
				Cell cell = report.getReportModel().getReportCell(row, col);
				if (cell.isChild()) {
					int r = report.getReportModel().getOwnerRow(cell, row, col);
					if (r + cell.getOwner().getRowSpan() == row) {
						CellStyle style = report.getCellStyle(cell.getOwner().getStyleId());
						newLine = style.getBorders(position);
					}
				} else {
					if (cell.getRowSpan() == 0) {
						CellStyle style = report.getCellStyle(cell.getStyleId());
						newLine = style.getBorders(position);
					} else {
						newLine = null;
					}
				}
				if (line != null && !line.equals(newLine)) {
					drawLine((Graphics2D) g, line, x1, y1, x2, y1);
					x1 = x2;
					line = newLine;
				} else if (line == null) {
					line = newLine;
					x1 = x2;
				}
				
				x2 += columnModel.getColumn(col).getWidth();
			}
			if (line != null) {
				drawLine((Graphics2D) g, line, x1, y1, x2, y1);
			}
			
		}
	}

	private void printLeftBorders(Graphics g, int rMin, int rMax, int cMin,
			int cMax) {
		
		int position = Border.LINE_LEFT;
		
		int y0 = 0;
		for (int i = 0; i < rMin; i++) {
			y0 += rowModel.getRow(i).getHeight();
		}
		
		int x0 = 0;
		if (isLeftToRight) {
			for (int i = 0; i < cMin; i++) {
				x0 += columnModel.getColumn(i).getWidth();
			}
		} else {
			for (int i = columnModel.getColumnCount() - 1; i > cMin; i--) {
				x0 += columnModel.getColumn(i).getWidth();
			}
		}
		
		int x1 = 0;
		int y1 = 0;
		int y2 = 0;
		x1 = x0;
		
		for (int col = cMin; col <= cMax; col++) {
			Border line = null;
			y1 = y0;
			y2 = y1;
			for (int row = rMin; row <= rMax; row++) {
				Border newLine = null;
				Cell cell = report.getReportModel().getReportCell(row, col);
				if (cell.isChild()) {
					int c = report.getReportModel().getOwnerColumn(cell, row, col);
					if (c == col) {
						CellStyle style = report.getCellStyle(cell.getOwner().getStyleId());
						newLine = style.getBorders(position);
					}
				} else {
					CellStyle style = report.getCellStyle(cell.getStyleId());
					newLine = style.getBorders(position);
				}
				if (line != null && !line.equals(newLine)) {
					drawLine((Graphics2D) g, line, x1, y1, x1, y2);
					y1 = y2;
					line = newLine;
				} else if (line == null) {
					line = newLine;
					y1 = y2;
				}
				
				y2 += rowModel.getRowHeight(row);
			}
			if (line != null) {
				drawLine((Graphics2D) g, line, x1, y1, x1, y2);
			}
			x1 += columnModel.getColumn(col).getWidth();
		}
	}

	private void printRightBorders(Graphics g, int rMin, int rMax, int cMin,
			int cMax) {
		
		int position = Border.LINE_RIGHT;
		
		int y0 = 0;
		for (int i = 0; i < rMin; i++) {
			y0 += rowModel.getRow(i).getHeight();
		}
		
		int x0 = 0;
		if (isLeftToRight) {
			for (int i = 0; i < cMin; i++) {
				x0 += columnModel.getColumn(i).getWidth();
			}
		} else {
			for (int i = columnModel.getColumnCount() - 1; i > cMin; i--) {
				x0 += columnModel.getColumn(i).getWidth();
			}
		}
		
		int x1 = x0;
		int y1 = 0;
		int y2 = 0;
		y1 = y0;
		
		for (int col = cMin; col <= cMax; col++) {
			Border line = null;
			y1 = y0;
			y2 = y1;
			int colWidth = columnModel.getColumn(col).getWidth();
			x1 += colWidth;
			if (col == cMax) {
				x1--;
			}
			for (int row = rMin; row <= rMax; row++) {
				Border newLine = null;
				Cell cell = report.getReportModel().getReportCell(row, col);
				if (cell.isChild()) {
					int c = report.getReportModel().getOwnerColumn(cell, row, col);
					if (c + cell.getOwner().getColSpan() == col) {
						CellStyle style = report.getCellStyle(cell.getOwner().getStyleId());
						newLine = style.getBorders(position);
					}
				} else {
					if (cell.getColSpan() == 0) {
						CellStyle style = report.getCellStyle(cell.getStyleId());
						newLine = style.getBorders(position);
					} else {
						newLine = null;
					}
				}
				if (line != null && !line.equals(newLine)) {
					drawLine((Graphics2D) g, line, x1, y1, x1, y2);
					y1 = y2;
					line = newLine;
				} else if (line == null) {
					line = newLine;
					y1 = y2;
				}
				
				y2 += rowModel.getRow(row).getHeight();
			}
			if (line != null) {
				drawLine((Graphics2D) g, line, x1, y1, x1, y2);
			}
			
		}
	}
	
	private Rectangle getCellRect(Rectangle rect, int row, int column, Cell cell) {
		Rectangle r = new Rectangle(rect);

		if (cell.isChild()) {
			int oldRow = row;
			int oldColumn = column;
			row = report.getReportModel().getOwnerRow(cell, row, column);
			column = report.getReportModel().getOwnerColumn(cell, row, column);

			for (int i = row; i < oldRow; i++) {
				r.y -= report.getReportModel().getRowHeight(i);
			}
			if (isLeftToRight) {
				for (int i = column; i < oldColumn; i++) {
					r.x -= columnModel.getColumn(i).getWidth();
				}
			} else {
				r.x = 0;
				for (int i = columnModel.getColumnCount() - 1; i > column; i--) {
					r.x += columnModel.getColumn(i).getWidth();
				}
			}

			cell = cell.getOwner();
		}

		Dimension rc = report.getReportModel().getCellSize(cell, row, column,
				true);
		r.height = rc.height;

		r.width = rc.width;

		r.setBounds(r.x + cm / 2, r.y + rm / 2, r.width - cm, r.height - rm);
		return r;
	}

	/**
	 * @param g
	 * @param rMin
	 * @param cMin
	 * @param cMax
	 * @param draggedColumn
	 * @param row
	 * @param aRow
	 */
	private void paintRow(Graphics g, int rMin, int cMin, int cMax,
			TableColumn draggedColumn, int row, TableRow aRow, Rectangle rect) {

		rect.x = 0;
		if (isLeftToRight) {
			for (int i = 0; i < cMin; i++) {
				rect.x += columnModel.getColumn(i).getWidth();
			}
		} else {
			for (int i = columnModel.getColumnCount() - 1; i > cMin; i--) {
				rect.x += columnModel.getColumn(i).getWidth();
			}
		}

		for (int column = cMin; column <= cMax; column++) {
			Cell cell = report.getReportModel().getReportCell(row, column);
			TableColumn aColumn = columnModel.getColumn(column);
			if (aColumn != draggedColumn) {
				if (!(report.isPrintState() && cell.isNotPrint())) {
					if (!cell.isChild() || row == rMin || column == cMin) {
						paintCell(g, getCellRect(rect, row, column, cell), row,
								column, report.getCellStyle(cell.getStyleId()));
					}
				}
			}
			rect.x += aColumn.getWidth();
		}
		if (aRow.isPageBreak() && !report.isPrintState()) {
			if (table.getTableHeader() != null) {
				Rectangle r = report.getRowHeader().getHeaderRect(row);
				Rectangle rc = table.getTableHeader().getHeaderRect(cMax);
				paintEndPage(g, rc.x + rc.width, r.y + r.height - 1);
			}
		}
	}

	private void printRow(Graphics g, int rMin, int cMin, int cMax, int row,
			TableRow aRow, Rectangle rect) {

		rect.x = 0;
		if (isLeftToRight) {
			for (int i = 0; i < cMin; i++) {
				rect.x += columnModel.getColumn(i).getWidth();
			}
		} else {
			for (int i = columnModel.getColumnCount() - 1; i > cMin; i--) {
				rect.x += columnModel.getColumn(i).getWidth();
			}
		}

		for (int column = cMin; column <= cMax; column++) {
			Cell cell = report.getReportModel().getReportCell(row, column);
			TableColumn aColumn = columnModel.getColumn(column);

			if (!cell.isNotPrint()) {
				if (!cell.isChild() || column == cMin) {
					printCell(g, getCellRect(rect, row, column, cell), row,
							column, report.getCellStyle(cell.getStyleId()));
				}
			}
			rect.x += aColumn.getWidth();
		}
	}

	private void paintEndPage(Graphics g, int x, int y) {
		g.setColor(Color.RED);
		for (int x1 = 0; x1 < x; x1 += 12) {
			g.drawLine(x1, y, x1 + 4, y);
		}
	}

	private void paintColEndPage(Graphics g, int x, int y) {
		g.setColor(Color.RED);
		for (int y1 = 0; y1 < y; y1 += 12) {
			g.drawLine(x, y1, x, y1 + 4);
		}
	}

	private void paintDraggedArea(Graphics g, int rMin, int rMax,
			TableColumn draggedColumn, int distance) {
		int draggedColumnIndex = viewIndexForColumn(draggedColumn);

		Rectangle vacatedColumnRect = new Rectangle();

		for (int r = 0; r < rMin; r++) {
			vacatedColumnRect.y += rowModel.getRow(r).getHeight();
		}

		for (int r = rMin; r <= rMax; r++) {
			vacatedColumnRect.height += rowModel.getRow(r).getHeight();
		}

		for (int c = 0; c < draggedColumnIndex; c++) {
			vacatedColumnRect.x += columnModel.getColumn(c).getWidth();
		}

		vacatedColumnRect.width = columnModel.getColumn(draggedColumnIndex)
				.getWidth();

		g.setColor(table.getParent().getBackground());
		g.fillRect(vacatedColumnRect.x, vacatedColumnRect.y,
				vacatedColumnRect.width, vacatedColumnRect.height);

		vacatedColumnRect.x += distance;

		g.setColor(table.getBackground());
		g.fillRect(vacatedColumnRect.x, vacatedColumnRect.y,
				vacatedColumnRect.width, vacatedColumnRect.height);

		for (int row = rMin; row <= rMax; row++) {
			Cell cell = report.getReportModel().getReportCell(row,
					draggedColumnIndex);
			paintCell(g, getCellRect(vacatedColumnRect, row,
					draggedColumnIndex, cell), row, draggedColumnIndex, null);
			vacatedColumnRect.y += rowModel.getRow(row).getHeight();
		}
	}

	private void paintRowDraggedArea(Graphics g, int cMin, int cMax,
			TableRow draggedRow, int distance) {
		int draggedRowIndex = viewIndexForRow(draggedRow);

		Rectangle vacatedRowRect = new Rectangle();

		for (int c = 0; c < cMin; c++) {
			vacatedRowRect.x += columnModel.getColumn(c).getWidth();
		}

		for (int c = cMin; c <= cMax; c++) {
			vacatedRowRect.width += columnModel.getColumn(c).getWidth();
		}

		for (int r = 0; r < draggedRowIndex; r++) {
			vacatedRowRect.y += rowModel.getRow(r).getHeight();
		}

		vacatedRowRect.height = rowModel.getRow(draggedRowIndex).getHeight();

		// Paint a gray well in place of the moving row.
		g.setColor(table.getParent().getBackground());
		g.fillRect(vacatedRowRect.x, vacatedRowRect.y, vacatedRowRect.width,
				vacatedRowRect.height);

		// Move to the where the cell has been dragged.
		vacatedRowRect.y += distance;

		// Fill the background.
		g.setColor(table.getBackground());
		g.fillRect(vacatedRowRect.x, vacatedRowRect.y, vacatedRowRect.width,
				vacatedRowRect.height);

		for (int column = cMin; column <= cMax; column++) {
			Cell cell = report.getReportModel().getReportCell(draggedRowIndex,
					column);
			paintCell(g, getCellRect(vacatedRowRect, draggedRowIndex, column,
					cell), draggedRowIndex, column, null);
			vacatedRowRect.x += columnModel.getColumn(column).getWidth();
		}
	}

	protected KeyListener createKeyListener() {
		return new ReportKeyHandler();
	}

	private void paintCell(Graphics g, Rectangle cellRect, int row, int column,
			CellStyle style) {

		if (table.isEditing() && table.getEditingRow() == row
				&& table.getEditingColumn() == column) {
			Component component = table.getEditorComponent();
			component.setBounds(cellRect);
			component.validate();
		} else {
			TableCellRenderer renderer = table.getCellRenderer(row, column);
			Component component = table.prepareRenderer(renderer, row, column);
			if (style != null
					&& !style.getBackground().equals(
							CellStyle.getDefaultStyle().getBackground())) {
				g.setColor(style.getBackground());
				g.fillRect(cellRect.x - 1, cellRect.y - 1, cellRect.width + 1,
						cellRect.height + 1);
			}
			rendererPane.paintComponent(g, component, table, cellRect.x,
					cellRect.y, cellRect.width, cellRect.height, false);
		}
		paintCellGrid(g, row, column, style, cellRect);
	}

	private void printCell(Graphics g, Rectangle cellRect, int row, int column,
			CellStyle style) {

		TableCellRenderer renderer = table.getCellRenderer(row, column);
		Component component = table.prepareRenderer(renderer, row, column);
		if (style != null
				&& !style.getBackground().equals(
						CellStyle.getDefaultStyle().getBackground())) {
			g.setColor(style.getBackground());
			g.fillRect(cellRect.x - 1, cellRect.y - 1, cellRect.width + 2,
					cellRect.height + 2);
		}
		rendererPane.paintComponent(g, component, table, cellRect.x,
				cellRect.y, cellRect.width, cellRect.height, false);
		
	}

	private void paintCellGrid(Graphics g, int row, int column,
			CellStyle style, Rectangle r) {
		r.grow(1, 1);
		int cellWidth = r.x + r.width - 1;
		int cellHeight = r.y + r.height - 1;
		if (report.isShowGrid()) {
			g.setColor(table.getGridColor());
			g.drawLine(r.x, cellHeight, cellWidth, cellHeight);
			g.drawLine(cellWidth, r.y, cellWidth, cellHeight);
		}
		if (style != null) {
			for (byte position = jdbreport.model.Border.LINE_LEFT; position <= jdbreport.model.Border.LINE_BOTTOM; position++) {
				Border line = style.getBorders(position);
				if (line != null && line.getLineWidth() > 0) {
					switch (position) {
					case Border.LINE_LEFT:
						drawLine((Graphics2D) g, line, r.x, r.y, r.x,
								cellHeight);
						break;
					case Border.LINE_TOP:
						drawLine((Graphics2D) g, line, r.x, r.y, cellWidth, r.y);
						break;
					case Border.LINE_RIGHT:
						drawLine((Graphics2D) g, line, cellWidth, r.y,
								cellWidth, cellHeight);
						break;
					case Border.LINE_BOTTOM:
						drawLine((Graphics2D) g, line, r.x, cellHeight,
								cellWidth, cellHeight);
						break;
					}
				}
			}
		}
	}

	private void drawLine(Graphics2D g2, Border line, int x1, int y1, int x2,
			int y2) {
		g2.setColor(line.getColor());
		if (line.getLineWidth() != 1.0 || line.getStyle() != Border.psSolid) {
			Stroke stroke = g2.getStroke();
			g2.setStroke(line.getStroke());
			g2.draw(new Line2D.Float(x1, y1, x2, y2));
			g2.setStroke(stroke);
		} else {
			g2.drawLine(x1, y1, x2, y2);
		}
	}

	protected class ReportKeyHandler implements KeyListener {

		public void keyTyped(KeyEvent e) {
		}

		public void keyPressed(KeyEvent e) {
			if (e.getModifiers() == 0) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_DOWN:
					if (report.downCell())
						e.consume();
					return;
				case KeyEvent.VK_RIGHT:
					if (report.rightCell())
						e.consume();
					return;
				case KeyEvent.VK_TAB:
					if (report.getCellEditor() != null) {
						report.getCellEditor().stopCellEditing();
						report.removeEditor();
					}
					if (report.nextCell())
						e.consume();
					return;
				case KeyEvent.VK_ENTER:
					if (report.getCellEditor() != null) {
						report.getCellEditor().stopCellEditing();
						report.removeEditor();
					}
					return;
				}
			} else {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (e.isControlDown()) {
						report.showCellEditor();
					} else if (e.isAltDown()) {
						report.showCellProperty();
					}
				}
			}

		}

		public void keyReleased(KeyEvent e) {
		}

	}

}
