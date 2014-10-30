/*
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2010 Andrey Kholmanskih. All rights reserved.
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Map;

import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import jdbreport.model.print.ReportPage;

/**
 * The ReportModel interface specifies the methods of the report will use to
 * interrogate a grid data model.
 * 
 * @version 2.0 15.11.2010
 * @author Andrey Kholmanskih
 * 
 */
public interface ReportModel extends TableModel {

	/**
	 * Adds rows' count to the index of the model. The new rows will contain
	 * null values. Notification of the row being added will be generated.
	 * 
	 * @param count
	 *            rows' count
	 * @param index
	 *            the row index of the rows to be inserted
	 * @return rows' count in the model
	 */
	public int addRows(int count, int index);

    /**
     * Appends all rows from model
     * @param model other model
     */
    void appendModel(ReportModel model);

	/**
	 * Removes rows' count begining with the index from the model.
	 * 
	 * @param count
	 *            rows' count
	 * @param index
	 *            index of the first removed row
	 */
	public void removeRows(int count, int index);

	/**
	 * Removes all rows from the model.
	 * 
	 */
	public void removeRows();

	/**
	 * Adds columns' count to the model
	 * 
	 * @param count
	 *            columns' count
	 * @return the columns' count in the model
	 */
	public int addColumns(int count);

	/**
	 * Adds columns' count to the index of the model. The new columns will
	 * contain null values. Notification of the column being added will be
	 * generated.
	 * 
	 * @param count
	 *            columns' count
	 * @param index
	 *            the column index of the columns to be inserted
	 * @return columns' count in the model
	 */
	public int addColumns(int count, int index);

	/**
	 * Removes columns' count begining with the index from the model.
	 * 
	 * @param count
	 *            columns' count
	 * @param index
	 *            index of the first removed column
	 */
	public void removeColumns(int count, int index);

	/**
	 * Sets the columns' count in the model If the columns' count is bigger than
	 * parameter, the columns are removed from the model If the columns' count
	 * is smaller than parameter, the columns are added to the model
	 * 
	 * @param count
	 *            new columns' count
	 */
	public void setColumnCount(int count);

	/**
	 * Returns the Cell by row and column
	 * 
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 * @return the Cell
	 */
	public Cell getReportCell(int row, int column);

	/**
	 * Returns the Cell's owner by row and column If there is no owner, the Cell
	 * returns by itself.
	 * 
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 * @return the Cell
	 */
	public Cell getOwnerReportCell(int row, int column);

	/**
	 * Creates the Cell by row and column
	 * 
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 * @return the Cell
	 */
	public Cell createReportCell(int row, int column);

	/**
	 * Returns the row's number of the cell's owner If there is no owner, the
	 * row's number of the cell is returned.
	 * 
	 * @param cell
	 *            the cell
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 * @return the row's number
	 */
	public int getOwnerRow(Cell cell, int row, int column);

	/**
	 * Returns the column's number of the cell's owner If there is no owner, the
	 * column's number of the cell is returned.
	 * 
	 * @param cell
	 *            the cell
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 * @return the owner column's number
	 */
	public int getOwnerColumn(Cell cell, int row, int column);

	/**
	 * Returns the width of the column
	 * 
	 * @param column
	 *            the column's number
	 * @return the column's width
	 */
	public int getColumnWidth(int column);

	/**
	 * Returns the height of the row
	 * 
	 * @param row
	 *            the row's number
	 * @return the row's height
	 */
	public int getRowHeight(int row);

	/**
	 * Sets the row's height by row
	 * 
	 * @param row
	 *            the row's number
	 * @param rowHeight
	 *            a new row's height
	 */
	public void setRowHeight(int row, int rowHeight);

	/**
	 * Unions the cells by coordinates
	 * 
	 * @param topRow
	 *            the number of the top row
	 * @param leftColumn
	 *            the number of the left column
	 * @param bottomRow
	 *            the number of the bottom row
	 * @param rightColumn
	 *            the number of the right column
	 */
	public void unionCells(int topRow, int leftColumn, int bottomRow,
			int rightColumn);

	/**
	 * Clears the unions of the cell by row and column
	 * 
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 */
	public void clearUnion(int row, int column);

	/**
	 * Sets the borders at the specified positions for the cells by coordinates
	 * 
	 * @param topRow
	 *            the number of the top row
	 * @param leftColumn
	 *            the number of the left column
	 * @param bottomRow
	 *            the number of the bottom row
	 * @param rightColumn
	 *            the number of the right column
	 * @param positions
	 *            positions - boolean values, where true sets border, otherwise
	 *            does nothing. Position's index can be from Border.LINE_LEFT to
	 *            Border.LINE_HMIDDLE
	 * @param line
	 *            the border
	 */
	public void addBorder(int topRow, int leftColumn, int bottomRow,
			int rightColumn, boolean[] positions, Border line);

	/**
	 * @return the report's title.
	 */
	public String getReportTitle();

	/**
	 * @param reportTitle
	 *            a new report's title.
	 */
	public void setReportTitle(String reportTitle);

	/**
	 * @return the default column's width
	 */
	public int getDefaultColumnWidth();

	/**
	 * Sets a default column's width
	 * 
	 * @param defaultColumnWidth
	 *            a new default column's width
	 */
	public void setDefaultColumnWidth(int defaultColumnWidth);

	/**
	 * @return the reportPage.
	 */
	public ReportPage getReportPage();

	/**
	 * Returns true if the user is allowed to resize rows by dragging between
	 * their headers,false otherwise. The default is true. You can resize rows
	 * programmatically regardless of this setting.
	 * 
	 * @return true if rows can resizing
	 */
	public boolean isRowSizing();

	/**
	 * Sets whether the user can resize rows by dragging between headers.
	 * 
	 * @param b
	 *            true if report view should allow resizing
	 */
	public void setRowSizing(boolean b);

	/**
	 * Returns true if the user is allowed to resize columns by dragging between
	 * their headers,false otherwise. The default is true. You can resize
	 * columns programmatically regardless of this setting.
	 * 
	 * @return true if columns can resizing
	 */
	public boolean isColSizing();

	/**
	 * Sets whether the user can resize columns by dragging between headers.
	 * 
	 * @param b
	 *            true if report view should allow resizing
	 */
	public void setColSizing(boolean b);

	/**
	 * Returns true if the user is allowed to rearrange rows by dragging their
	 * headers, false otherwise. The default is true. You can rearrange rows
	 * programmatically regardless of this setting.
	 * 
	 * @return the rowMoving property
	 */
	public boolean isRowMoving();

	/**
	 * Sets whether the user can drag row headers to reorder rows.
	 * 
	 * @param b
	 *            true if the report view should allow reordering; otherwise
	 *            false
	 */
	public void setRowMoving(boolean b);

	/**
	 * Returns true if the user is allowed to rearrange columns by dragging
	 * their headers, false otherwise. The default is true. You can rearrange
	 * columns programmatically regardless of this setting.
	 * 
	 * @return the colMoving property
	 */
	public boolean isColMoving();

	/**
	 * Sets whether the user can drag column headers to reorder columns.
	 * 
	 * @param b
	 *            true if the report view should allow reordering; otherwise
	 *            false
	 */
	public void setColMoving(boolean b);

	/**
	 * Returns the boolean indicating whether this ReportModel is editable or
	 * not.
	 * 
	 * @return the boolean value
	 */
	public boolean isEditable();

	/**
	 * Sets the specified boolean to indicate whether or not this ReportModel
	 * should be editable.
	 * 
	 * @param b
	 *            the boolean to be set
	 */
	public void setEditable(boolean b);

	/**
	 * Printing mode that scales the output smaller, if necessary, to fit the
	 * report's entire width (and thereby all columns) on each page; Rows are
	 * spread across multiple pages as necessary.
	 * 
	 * @return if true sets printing mode in FIT_WIDTH otherwise in NORMAL.
	 */
	public boolean isStretchPage();

	/**
	 * Printing mode that scales the output smaller, if necessary, to fit the
	 * report's entire width (and thereby all columns) on each page; Rows are
	 * spread across multiple pages as necessary.
	 * 
	 * @param stretchPage
	 *            if true sets printing mode in FIT_WIDTH otherwise in NORMAL
	 * 
	 * @see javax.swing.JTable.PrintMode
	 */
	public void setStretchPage(boolean stretchPage);

	/**
	 * Returns the flag of the page's break by the row's number
	 * 
	 * @param row
	 *            the row's number
	 * @return the flag of the page's end
	 */
	public boolean isRowBreak(int row);

	/**
	 * Returns true if the row is last on page
	 * 
	 * @param row the row's number
	 * @return true if the row is last on page
	 * @since 1.3
	 */
	public boolean isLastRowInPage(int row);
	
	/**
	 * Sets the horizontal page break after row
	 * 
	 * @param row
	 *            the row's number
	 * @param b
	 *            a boolean value, where true sets the end page and false remove
	 *            it
	 */
	public void setRowBreak(int row, boolean b);

	/**
	 * Returns true if the sets vertical page break after column
	 * 
	 * @param column
	 *            the column's number
	 * @return columnBreak property
	 */
	public boolean isColumnBreak(int column);

	/**
	 * Sets the vertical page break after column
	 * 
	 * @param column
	 *            the column's number
	 * @param b
	 *            a boolean value, where true sets the page break and false
	 *            remove it
	 */
	public void setColumnBreak(int column, boolean b);

	/**
	 * @return TableRowModel
	 */
	public TableRowModel getRowModel();

	/**
	 * 
	 * @return TableColumnModel
	 */
	public TableColumnModel getColumnModel();

	/**
	 * Returns cells that do not have owner
	 * 
	 * @param rect
	 *            the region where the cells are selected
	 * @return iterator
	 */
	public Iterator<Cell> getSelectedCells(GridRect rect);

	/**
	 * Returns the CellStyle by the index
	 * 
	 * @param index
	 *            the id of the CellStyle
	 * @return the CellStyle
	 */
	public CellStyle getStyles(Object index);

	/**
	 * Looks for style in the list of styles if doesn't find, adds it to the
	 * list otherwise returns the id of the founded style If style's id is null,
	 * a new id is appropriated to the style
	 * 
	 * @param style
	 *            adding style
	 * @return the style's id
	 */
	public Object addStyle(CellStyle style);

	/**
	 * Returns a rectangle for the cell that lies at the intersection of row and
	 * column. If the cell's are spaned, rectangle is returned for all the
	 * spaned cells
	 * 
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 * @param includeSpacing
	 *            if false, return the true cell bounds - computed by
	 *            subtracting the intercell spacing from the height and widths
	 *            of the column and row models
	 * @param isLeftToRight
	 * @return the rectangle containing the cell at location row,column
	 */
	public Rectangle getCellRect(int row, int column, boolean includeSpacing,
			boolean isLeftToRight);

	/**
	 * Returns a size for the cell that lies at the intersection of row and
	 * column. If the cell's are spaned, size is returned for all the spaned
	 * cells
	 * 
	 * @param cell
	 *            the Cell for which size is returns
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 * @param includeSpacing
	 * @return the cell's size
	 */
	public Dimension getCellSize(Cell cell, int row, int column,
			boolean includeSpacing);

	/**
	 * Sets row's height by the content of the cell
	 * 
	 * @param hCalc
	 *            interface for calculation the cell's height
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 */
	public void updateRowHeight(HeighCalculator hCalc, int row, int column);

	/**
	 * Determines whether report should be visible
	 * 
	 * @return true if the report is visible, false otherwise
	 */
	public boolean isVisible();

	/**
	 * Shows or hides report depending on the value of parameter visible.
	 * 
	 * @param visible
	 *            if true, shows this report; otherwise, hides this report
	 */
	public void setVisible(boolean visible);

	/**
	 * Determines whether page's bounders will be calculated automatically
	 * 
	 * @return canUpdatePages property
	 */
	public boolean isCanUpdatePages();

	/**
	 * Sets the property that determines whether page's bounders will be
	 * calculated automatically
	 * 
	 * @param b
	 *            if true, page's bounders will be calculated automatically
	 */
	public void setCanUpdatePages(boolean b);

	/**
	 * Calculates pages' size
	 * 
	 * @param startRow
	 *            the first row for calculation
	 */
	public void updatePages(int startRow);

	public void updateRowAndPageHeight(HeighCalculator hCalc);
	
	/**
	 * Adds a PropertyChangeListener to the listener list. The listener is
	 * registered for all bound properties of this class
	 * 
	 * @param listener
	 *            the property change listener to be added
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Removes a PropertyChangeListener from the listener list. This method
	 * should be used to remove PropertyChangeListeners that were registered for
	 * all bound properties of this class.
	 * 
	 * If listener is null, no exception is thrown and no action is performed.
	 * 
	 * @param listener
	 *            the PropertyChangeListener to be removed
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Sets the font's name for the cells by the selRect.
	 * 
	 * @param selRect
	 *            coordinates for selected cells
	 * @param fontName
	 *            the font's name
	 */
	public void setFontName(GridRect selRect, String fontName);

	/**
	 * Sets the font's size for the cells by the selRect
	 * 
	 * @param selRect
	 *            coordinates for selected cells
	 * @param fontSize
	 *            the new font's size
	 */
	public void setFontSize(GridRect selRect, int fontSize);

	/**
	 * Determines whether font's style is in the cell
	 * 
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 * @param style
	 *            the font's style
	 * @return true, if font's style in the cell is determined
	 */
	public boolean isFontStyle(int row, int column, int style);

	/**
	 * Sets the font's style for the cells by coordinates selRect
	 * 
	 * @param selRect
	 *            coordinates of the selected cells
	 * @param fontStyle
	 *            the font's style The style argument is an integer bitmask that
	 *            may be PLAIN, or a bitwise union of BOLD, ITALIC, UNDERLINE,
	 *            STRIKETHROUGH
	 * @param enable
	 *            if true, the style is determined, otherwise the style is
	 *            removed.
	 */
	public void setFontStyle(GridRect selRect, int fontStyle, boolean enable);

	/**
	 * Sets background color for the selected cells
	 * 
	 * @param selectionRect
	 *            coordinates of the selected cells
	 * @param color
	 *            the new background color for the cells
	 */
	public void setBackground(GridRect selectionRect, Color color);

	/**
	 * Sets foreground color for the selected cells
	 * 
	 * @param selectionRect
	 *            coordinates of the selected cells
	 * @param color
	 *            the new foreground color for the cells
	 */
	public void setForeground(GridRect selectionRect, Color color);

	/**
	 * 
	 * Sets new decimal position of the numeric value for selected cells.
	 * 
	 * @param selectionRect
	 *            coordinates of the selected cells
	 * @param d
	 *            the decimal position of the numeric value.
	 */
	public void setDecimals(GridRect selectionRect, int d);

	/**
	 * Sets the horizontal alignment for the selected cells The horizontal
	 * alignment is a constant that may be LEFT, RIGHT, CENTER or JUSTIFY
	 * 
	 * 
	 * @param selectionRect
	 *            coordinates of the selected cells
	 * @param align
	 *            the new horizontal alignment for the cells
	 */
	public void setHorizontalAlignment(GridRect selectionRect, int align);

	/**
	 * Sets the vertical alignment for the selected cells The vertical alignment
	 * is a constant that may be TOP, BOTTOM or CENTER
	 * 
	 * @param selectionRect
	 *            coordinates of the selected cells
	 * @param align
	 *            the new vertical alignment for the cells
	 */
	public void setVerticalAlignment(GridRect selectionRect, int align);

	/**
	 * Removes the selected cells
	 * 
	 * @param selectionRect
	 *            coordinates of the selected cells
	 */
	public void delete(GridRect selectionRect);

	/**
	 * Returns the CellWrap object for the cell by row and column
	 * 
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 * @return the CellWrap object
	 */
	public CellWrap getCellWrap(int row, int column);

	/**
	 * Bans notification of listeners
	 * 
	 */
	public void startUpdate();

	public CellCoord getCellPosition(Cell cell);

	/**
	 * Allows notification of listeners
	 * 
	 */
	public void endUpdate();

	/**
	 * 
	 * @return if true, all notification are locked
	 */
	public boolean isUpdate();

	/**
	 * Returns the string to be used as the tooltip for the cell by row and
	 * column.
	 * 
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 * @return the tooltip string
	 */
	public String getToolTipText(int row, int column);

	public boolean isShowHeader();

	public void setShowHeader(boolean b);

	public boolean isShowRowHeader();

	public void setShowRowHeader(boolean b);

	public String getCellText(Cell cell);

	public void setColumnWidths(int[] widths);
	
	/**
	 * Searches for the right extreme column of page
	 * @param leftCol the column with which starts search
	 * @return column number
	 * @since 1.3
	 */
	public int findRightColumn(int leftCol);
	
	/**
	 * 
	 * @param map
	 * @since 2.0
	 */
	public void setStyleList(Map<Object, CellStyle> map);

	/**
	 * Direction of an output of pages on the printer. 
	 * If true that pages are printed from left to right, from top to down, 
	 * differently pages are printed from top to down, from left to right.
	 * 
	 * @return if true that pages are printed from left to right, otherwise from top to down
	 * @since 2.0
	 */
	public boolean isPrintLeftToRight();
	
	/**
	 * Direction of an output of pages on the printer. 
	 * If true that pages are printed from left to right, from top to down, 
	 * differently pages are printed from top to down, from left to right.
	 * 
	 * @param value
	 * @since 2.0
	 */
	public void setPrintLeftToRight(boolean value);

}