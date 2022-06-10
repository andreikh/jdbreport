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
	int addRows(int count, int index);

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
	void removeRows(int count, int index);

	/**
	 * Removes all rows from the model.
	 * 
	 */
	void removeRows();

	/**
	 * Adds columns' count to the model
	 * 
	 * @param count
	 *            columns' count
	 * @return the columns' count in the model
	 */
	int addColumns(int count);

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
	int addColumns(int count, int index);

	/**
	 * Removes columns' count begining with the index from the model.
	 * 
	 * @param count
	 *            columns' count
	 * @param index
	 *            index of the first removed column
	 */
	void removeColumns(int count, int index);

	/**
	 * Create and copy column from src to dest
	 * @param src index template column
	 * @param dest index for insert new column
	 * @return columns count
	 */
	int cloneColumn(int src, int dest);

	/**
	 *  Copy cell
	 * @param srcRow source cell' row
	 * @param srcCol source cell' column
	 * @param dstRow destination cell row
	 * @param dstCol destination cell column
	 * @return destination cell
	 */
	Cell copyCell(int srcRow, int srcCol, int dstRow, int dstCol);

	/**
	 * Sets the columns' count in the model If the columns' count is bigger than
	 * parameter, the columns are removed from the model If the columns' count
	 * is smaller than parameter, the columns are added to the model
	 * 
	 * @param count
	 *            new columns' count
	 */
	void setColumnCount(int count);

	/**
	 * Returns the Cell by row and column
	 * 
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 * @return the Cell
	 */
	Cell getReportCell(int row, int column);

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
	Cell getOwnerReportCell(int row, int column);

	/**
	 * Creates the Cell by row and column
	 * 
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 * @return the Cell
	 */
	Cell createReportCell(int row, int column);

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
	int getOwnerRow(Cell cell, int row, int column);

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
	int getOwnerColumn(Cell cell, int row, int column);

	/**
	 * Returns the width of the column
	 * 
	 * @param column
	 *            the column's number
	 * @return the column's width
	 */
	int getColumnWidth(int column);

	/**
	 * Returns the height of the row
	 * 
	 * @param row
	 *            the row's number
	 * @return the row's height
	 */
	int getRowHeight(int row);

	/**
	 * Sets the row's height by row
	 * 
	 * @param row
	 *            the row's number
	 * @param rowHeight
	 *            a new row's height
	 */
	void setRowHeight(int row, int rowHeight);

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
	void unionCells(int topRow, int leftColumn, int bottomRow,
			int rightColumn);

	/**
	 * Clears the unions of the cell by row and column
	 * 
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 */
	void clearUnion(int row, int column);

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
	void addBorder(int topRow, int leftColumn, int bottomRow,
			int rightColumn, boolean[] positions, Border line);

	/**
	 * @return the report's title.
	 */
	String getReportTitle();

	/**
	 * @param reportTitle
	 *            a new report's title.
	 */
	void setReportTitle(String reportTitle);

	/**
	 * @return the default column's width
	 */
	int getDefaultColumnWidth();

	/**
	 * Sets a default column's width
	 * 
	 * @param defaultColumnWidth
	 *            a new default column's width
	 */
	void setDefaultColumnWidth(int defaultColumnWidth);

	/**
	 * @return the reportPage.
	 */
	ReportPage getReportPage();

	/**
	 * Returns true if the user is allowed to resize rows by dragging between
	 * their headers,false otherwise. The default is true. You can resize rows
	 * programmatically regardless of this setting.
	 * 
	 * @return true if rows can resizing
	 */
	boolean isRowSizing();

	/**
	 * Sets whether the user can resize rows by dragging between headers.
	 * 
	 * @param b
	 *            true if report view should allow resizing
	 */
	void setRowSizing(boolean b);

	/**
	 * Returns true if the user is allowed to resize columns by dragging between
	 * their headers,false otherwise. The default is true. You can resize
	 * columns programmatically regardless of this setting.
	 * 
	 * @return true if columns can resizing
	 */
	boolean isColSizing();

	/**
	 * Sets whether the user can resize columns by dragging between headers.
	 * 
	 * @param b
	 *            true if report view should allow resizing
	 */
	void setColSizing(boolean b);

	/**
	 * Returns true if the user is allowed to rearrange rows by dragging their
	 * headers, false otherwise. The default is true. You can rearrange rows
	 * programmatically regardless of this setting.
	 * 
	 * @return the rowMoving property
	 */
	boolean isRowMoving();

	/**
	 * Sets whether the user can drag row headers to reorder rows.
	 * 
	 * @param b
	 *            true if the report view should allow reordering; otherwise
	 *            false
	 */
	void setRowMoving(boolean b);

	/**
	 * Returns true if the user is allowed to rearrange columns by dragging
	 * their headers, false otherwise. The default is true. You can rearrange
	 * columns programmatically regardless of this setting.
	 * 
	 * @return the colMoving property
	 */
	boolean isColMoving();

	/**
	 * Sets whether the user can drag column headers to reorder columns.
	 * 
	 * @param b
	 *            true if the report view should allow reordering; otherwise
	 *            false
	 */
	void setColMoving(boolean b);

	/**
	 * Returns the boolean indicating whether this ReportModel is editable or
	 * not.
	 * 
	 * @return the boolean value
	 */
	boolean isEditable();

	/**
	 * Sets the specified boolean to indicate whether or not this ReportModel
	 * should be editable.
	 * 
	 * @param b
	 *            the boolean to be set
	 */
	void setEditable(boolean b);

	/**
	 * Printing mode that scales the output smaller, if necessary, to fit the
	 * report's entire width (and thereby all columns) on each page; Rows are
	 * spread across multiple pages as necessary.
	 * 
	 * @return if true sets printing mode in FIT_WIDTH otherwise in NORMAL.
	 */
	boolean isStretchPage();

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
	void setStretchPage(boolean stretchPage);

	/**
	 * Returns the flag of the page's break by the row's number
	 * 
	 * @param row
	 *            the row's number
	 * @return the flag of the page's end
	 */
	boolean isRowBreak(int row);

	/**
	 * Returns true if the row is last on page
	 * 
	 * @param row the row's number
	 * @return true if the row is last on page
	 * @since 1.3
	 */
	boolean isLastRowInPage(int row);
	
	/**
	 * Sets the horizontal page break after row
	 * 
	 * @param row
	 *            the row's number
	 * @param b
	 *            a boolean value, where true sets the end page and false remove
	 *            it
	 */
	void setRowBreak(int row, boolean b);

	/**
	 * Returns true if the sets vertical page break after column
	 * 
	 * @param column
	 *            the column's number
	 * @return columnBreak property
	 */
	boolean isColumnBreak(int column);

	/**
	 * Sets the vertical page break after column
	 * 
	 * @param column
	 *            the column's number
	 * @param b
	 *            a boolean value, where true sets the page break and false
	 *            remove it
	 */
	void setColumnBreak(int column, boolean b);

	/**
	 * @return TableRowModel
	 */
	TableRowModel getRowModel();

	/**
	 * 
	 * @return TableColumnModel
	 */
	TableColumnModel getColumnModel();

	/**
	 * Returns cells that do not have owner
	 * 
	 * @param rect
	 *            the region where the cells are selected
	 * @return iterator
	 */
	Iterator<Cell> getSelectedCells(GridRect rect);

	/**
	 * Returns the CellStyle by the index
	 * 
	 * @param index
	 *            the id of the CellStyle
	 * @return the CellStyle
	 */
	CellStyle getStyles(Object index);

	/**
	 * Looks for style in the list of styles if doesn't find, adds it to the
	 * list otherwise returns the id of the founded style If style's id is null,
	 * a new id is appropriated to the style
	 * 
	 * @param style
	 *            adding style
	 * @return the style's id
	 */
	Object addStyle(CellStyle style);

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
	 * @param isLeftToRight leftToRight flag
	 * @return the rectangle containing the cell at location row,column
	 */
	Rectangle getCellRect(int row, int column, boolean includeSpacing,
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
	 * @param includeSpacing include spacing flag
	 * @return the cell's size
	 */
	Dimension getCellSize(Cell cell, int row, int column,
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
	void updateRowHeight(HeightCalculator hCalc, int row, int column);

	/**
	 * Determines whether report should be visible
	 * 
	 * @return true if the report is visible, false otherwise
	 */
	boolean isVisible();

	/**
	 * Shows or hides report depending on the value of parameter visible.
	 * 
	 * @param visible
	 *            if true, shows this report; otherwise, hides this report
	 */
	void setVisible(boolean visible);

	/**
	 * Determines whether page's bounders will be calculated automatically
	 * 
	 * @return canUpdatePages property
	 */
	boolean isCanUpdatePages();

	/**
	 * Sets the property that determines whether page's bounders will be
	 * calculated automatically
	 * 
	 * @param b
	 *            if true, page's bounders will be calculated automatically
	 */
	void setCanUpdatePages(boolean b);

	/**
	 * Calculates pages' size
	 * 
	 * @param startRow
	 *            the first row for calculation
	 */
	void updatePages(int startRow);

	void updateRowAndPageHeight(HeightCalculator hCalc);
	
	/**
	 * Adds a PropertyChangeListener to the listener list. The listener is
	 * registered for all bound properties of this class
	 * 
	 * @param listener
	 *            the property change listener to be added
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);

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
	void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Sets the font's name for the cells by the selRect.
	 * 
	 * @param selRect
	 *            coordinates for selected cells
	 * @param fontName
	 *            the font's name
	 */
	void setFontName(GridRect selRect, String fontName);

	/**
	 * Sets the font's size for the cells by the selRect
	 * 
	 * @param selRect
	 *            coordinates for selected cells
	 * @param fontSize
	 *            the new font's size
	 */
	void setFontSize(GridRect selRect, int fontSize);

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
	boolean isFontStyle(int row, int column, int style);

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
	void setFontStyle(GridRect selRect, int fontStyle, boolean enable);

	/**
	 * Sets background color for the selected cells
	 * 
	 * @param selectionRect
	 *            coordinates of the selected cells
	 * @param color
	 *            the new background color for the cells
	 */
	void setBackground(GridRect selectionRect, Color color);

	/**
	 * Sets foreground color for the selected cells
	 * 
	 * @param selectionRect
	 *            coordinates of the selected cells
	 * @param color
	 *            the new foreground color for the cells
	 */
	void setForeground(GridRect selectionRect, Color color);

	/**
	 * 
	 * Sets new decimal position of the numeric value for selected cells.
	 * 
	 * @param selectionRect
	 *            coordinates of the selected cells
	 * @param d
	 *            the decimal position of the numeric value.
	 */
	void setDecimals(GridRect selectionRect, int d);

	/**
	 *
	 * Sets new round to significant property for selected cells.
	 *
	 * @param selectionRect
	 *            coordinates of the selected cells
	 * @param round
	 *            the new round to significant property.
	 */
	void setRoundToSignificant(GridRect selectionRect, boolean round);

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
	void setHorizontalAlignment(GridRect selectionRect, int align);

	/**
	 * Sets the vertical alignment for the selected cells The vertical alignment
	 * is a constant that may be TOP, BOTTOM or CENTER
	 * 
	 * @param selectionRect
	 *            coordinates of the selected cells
	 * @param align
	 *            the new vertical alignment for the cells
	 */
	void setVerticalAlignment(GridRect selectionRect, int align);

	/**
	 * Removes the selected cells
	 * 
	 * @param selectionRect
	 *            coordinates of the selected cells
	 */
	void delete(GridRect selectionRect);

	/**
	 * Returns the CellWrap object for the cell by row and column
	 * 
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 * @return the CellWrap object
	 */
	CellWrap getCellWrap(int row, int column);

	/**
	 * Bans notification of listeners
	 * 
	 */
	void startUpdate();

	CellCoord getCellPosition(Cell cell);

	/**
	 * Allows notification of listeners
	 * 
	 */
	void endUpdate();

	/**
	 * 
	 * @return if true, all notification are locked
	 */
	boolean isUpdate();

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
	String getToolTipText(int row, int column);

	boolean isShowHeader();

	void setShowHeader(boolean b);

	boolean isShowRowHeader();

	void setShowRowHeader(boolean b);

	String getCellText(Cell cell);

	void setColumnWidths(int[] widths);
	
	/**
	 * Searches for the right extreme column of page
	 * @param leftCol the column with which starts search
	 * @return column number
	 * @since 1.3
	 */
	int findRightColumn(int leftCol);
	
	/**
	 * 
	 * @param map styles
	 * @since 2.0
	 */
	void setStyleList(Map<Object, CellStyle> map);

	/**
	 * Direction of an output of pages on the printer. 
	 * If true that pages are printed from left to right, from top to down, 
	 * differently pages are printed from top to down, from left to right.
	 * 
	 * @return if true that pages are printed from left to right, otherwise from top to down
	 * @since 2.0
	 */
	boolean isPrintLeftToRight();
	
	/**
	 * Direction of an output of pages on the printer. 
	 * If true that pages are printed from left to right, from top to down, 
	 * differently pages are printed from top to down, from left to right.
	 * 
	 * @param value print left to right flag
	 * @since 2.0
	 */
	void setPrintLeftToRight(boolean value);

	boolean isHideFirstHeader();

	void setHideFirstHeader(boolean hideFirstHeader);

}