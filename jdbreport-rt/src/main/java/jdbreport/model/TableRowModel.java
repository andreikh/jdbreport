/*
 * Created on 22.09.2004
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2009 Andrey Kholmanskih. All rights reserved.
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

import jdbreport.model.event.TableRowModelListener;

/**
 * Defines the requirements for a report row model object suitable for use with
 * ReportModel.
 * 
 * @version 1.4 16.08.2009
 * @author Andrey Kholmanskih
 * 
 */
public interface TableRowModel extends Iterable<TableRow> {

	public static int minHeight = 2;

	public static int maxHeight = 4096;

	/**
	 * Adds the row at the specified position
	 * 
	 * @param row
	 *            index at which the row is to be inserted.
	 * 
	 * @return the inserted TableRow
	 */
	public TableRow addRow(int row);

	/**
	 * Adds the new row
	 * 
	 * @return the index of the inserted row
	 */
	public TableRow addRow();

	/**
	 * Adds the row at the specified position
	 * 
	 * @param row
	 *            the row's number
	 * @param tableRow
	 *            the TableRow object
	 * @return the index of the inserted row
	 */
	public int addRow(int row, TableRow tableRow);

	/**
	 * Adds the row to the group
	 * 
	 * @param group
	 * @param indexInGroup
	 *            index in group
	 * @return row's index in rows list
	 */
	public int addRow(RowsGroup group, int indexInGroup);

	/**
	 * Adds the row to the group
	 * 
	 * @param group
	 * @param indexInGroup
	 * @param tableRow
	 * @return row's index in the list
	 */
	public int addRow(RowsGroup group, int indexInGroup, TableRow tableRow);

	/**
	 * Adds column in the model
	 * 
	 * @param column
	 *            the specified position in this model
	 */
	public void addColumn(int column);

	/**
	 * Removes column from the model
	 * 
	 * @param column
	 *            the index of the removing column
	 */
	public void removeColumn(int column);

	/**
	 * Returns columns' count in the model
	 * 
	 * @return the columns count
	 */
	public int getColCount();

	/**
	 * Sets the columns' count in the model If the columns' count is bigger than
	 * parameter, the columns are removed from the model If the columns' count
	 * is smaller than parameter, the columns are added to the model
	 * 
	 * @param count
	 *            new columns' count
	 */
	public void setColCount(int count);

	/**
	 * Returns rows' count in the model
	 * 
	 * @return the rows count
	 */
	public int getRowCount();

	/**
	 * Returns the TableRow from the specified position
	 * 
	 * @param row
	 *            the row's number
	 * @return the TableRow
	 */
	public TableRow getRow(int row);

	/**
	 * Returns row's position in the model
	 * 
	 * @param tableRow
	 *            TableRow to search for.
	 * @return the index in this model of the first occurrence of the specified
	 *         tableRow, or -1 if this model does not contain this tableRow.
	 */
	public int getRowIndex(TableRow tableRow);

	/**
	 * Returns the index of the row that lies on the vertical point, y; or -1 if
	 * it lies outside the any of the row's bounds.
	 * 
	 * @param y
	 *            y coordinate of point
	 * @return the index of the row; or -1 if no row is found
	 */
	public int getRowIndexAtY(int y);

	/**
	 * Adds a listener for report row model events.
	 * 
	 * @param x
	 *            a TableRowModelListener object
	 */
	public void addRowModelListener(TableRowModelListener x);

	/**
	 * Removes a listener for report row model events.
	 * 
	 * @param x
	 *            a TableRowModelListener object
	 */
	public void removeRowModelListener(TableRowModelListener x);

	/**
	 * Returns the total height of all the rows.
	 * 
	 * @return the total computed height of all rows
	 */
	public int getTotalRowHeight();

	/**
	 * Moves the row and its header at rowIndex to newIndex. The old row at
	 * rowIndex will now be found at newIndex. The row that used to be at
	 * newIndex is shifted top or bottom to make room. This will not move any
	 * rows if rowIndex equals newIndex. This method posts a rowMoved event to
	 * its listeners.
	 * 
	 * @param rowIndex
	 *            the index of row to be moved
	 * @param newIndex
	 *            index of the row's new location
	 */
	public void moveRow(int rowIndex, int newIndex);

	/**
	 * Moves the dragged row and its header at rowIndex to newIndex.
	 * 
	 * @param rowIndex
	 *            the index of row to be moved
	 * @param newIndex
	 *            index of the row's new location
	 */
	void moveDraggedRow(int rowIndex, int newIndex);

	/**
	 * Returns the row between the cells in each row.
	 * 
	 * @return the margin, in pixels, between the cells
	 */
	public int getRowMargin();

	/**
	 * Returns the minimum height of a report row, in pixels. The default
	 * minimum row height is 2.0.
	 * 
	 * @return the mimimum height in pixels of a report row
	 */
	public int getMinRowHeight();

	/**
	 * Returns the maximum height of a report row, in pixels. The default
	 * maximum row height is 4096.0.
	 * 
	 * @return the maximum height in pixels of a report row
	 */
	public int getMaxRowHeight();

	/**
	 * Returns the preferred height of a report row, in pixels. The default row
	 * height is 17.0.
	 * 
	 * @return the preferred height in pixels of a report row
	 */
	public int getPreferredRowHeight();

	/**
	 * Sets the preferred height for row.
	 * 
	 * @param preferredHeight
	 *            new preferred row height, in pixels
	 */
	public void setPreferredRowHeight(int preferredHeight);

	/**
	 * Moves the column at columnIndex to newIndex.
	 * 
	 * @param columnIndex
	 *            the index of column to be moved
	 * @param newIndex
	 *            index of the column's new location
	 */
	public void moveColumn(int columnIndex, int newIndex);

	/**
	 * Returns the height, in pixels, of the row. The default row height is
	 * 17.0.
	 * 
	 * @param row
	 *            the row whose height is to be returned
	 * @return the height in pixels of a report row
	 */
	public int getRowHeight(int row);

	/**
	 * Sets the height for row to rowHeight. The height of the cells in this row
	 * will be equal to the row height minus the row margin.
	 * 
	 * @param row
	 *            the row whose height is being changed
	 * @param rowHeight
	 *            new row height, in pixels
	 */
	public void setRowHeight(int row, int rowHeight);

	/**
	 * Sets the height for tableRow to newHeight. The height of the cells in
	 * this row will be equal to the row height minus the row margin.
	 * 
	 * @param tableRow
	 *            the row whose height is being changed
	 * @param newHeight
	 *            new row height, in pixels
	 */
	public void setRowHeight(TableRow tableRow, int newHeight);

	/**
	 * Sets the height for row to h. The height of the cells in this row will be
	 * equal to the row height minus the row margin.
	 * 
	 * @param row
	 *            the row whose height is being changed
	 * @param h
	 *            new row height, in 1/72 of inch
	 */
	public void setRowHeight(int row, double h);

	/**
	 * 
	 * @return the RootGroup of repoprt model
	 */
	public RootGroup getRootGroup();

	/**
	 * Returns the group which contains the TableRow by specified row index
	 * 
	 * @param row
	 *            the row's index in rowList
	 * @return the RowsGroup object
	 */
	public RowsGroup getGroup(int row);

	/**
	 * Returns the group which contains the tableRow
	 * 
	 * @param tableRow
	 *            the TableRow object
	 * @return the RowsGroup object
	 */
	public RowsGroup getGroup(TableRow tableRow);

    /**
     * append group
     * @param group Group object
     */
    public void appendGroup(Group group);

	/**
	 * If parameter b is true, sets the group that is visible in report,
	 * otherwise removes rows containing in the group from report
	 * 
	 * @param group
	 *            Group object
	 * @param b
	 *            visible property
	 */
	public void setVisibleGroup(Group group, boolean b);

	/**
	 * Returns the index of the first group's row in rowList
	 * 
	 * @param group
	 *            the Group object
	 * @return index first group's row in rowList
	 */
	public int getGroupRowIndex(Group group);

	/**
	 * Returns value for row's header
	 * 
	 * @param row
	 *            the row's number
	 * @return value for row's header
	 */
	public Object getHeaderValue(int row);

	/**
	 * Calculates pages' size
	 * 
	 * @param startRow
	 *            the first row for calculation
	 * @param pageHeight
	 *            page height in pixels
	 */
	public void updatePages(int startRow, int pageHeight);

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
	 * Clears the unions of the selected cells
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
	public void clearUnion(int topRow, int leftColumn, int bottomRow,
			int rightColumn);

	/**
	 * Clears all cells unions before moving of rows or columns
	 */
	public void disableSpan();

	/**
	 * Restores all cells unions after moving of rows or columns
	 */
	public void enableSpan();

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
	 * Removes all rows
	 * 
	 */
	public void removeRows();

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
	 * Creates new default TableRow
	 * 
	 * @return the TableRow object
	 */
	public TableRow createTableRow();

	/**
	 * Bans notification of listeners before updates of model
	 * 
	 */
	public void startUpdate();

	/**
	 * Allows notification of listeners after updates of model
	 */
	public void endUpdate();

	/**
	 * Returns the possibility of calculation of pages' size
	 * 
	 * @return if true, pages' size are calculated automatically
	 */
	public boolean isCanUpdatePages();

	/**
	 * Sets canUpdatePages property
	 * 
	 * @param b
	 *            the canUpdatePages property
	 */
	public void setCanUpdatePages(boolean b);

	public boolean isCanHideGroup();

	/**
	 * Returns number of the first page
	 * @return number of the first page
	 * @since 1.4
	 */
	public int getFirstPageNumber();
	
	/**
	 * Sets number of the first page
	 * @param firstPageNumber number of the first page
	 * @since 1.4
	 */
	public void setFirstPageNumber(int firstPageNumber);

	/**
	 * Returns number of page for a cell on a row and a column
	 * 
	 * @param row
	 * @param column
	 * @return number of page
	 */
	public Integer getPageNumber(int row, int column);

	/**
	 * Returns count of pages
	 * 
	 * @return count of pages
	 */
	public int getPageCount();

	public Group getGroup(int[] path);

	public void setShowPageNumber(boolean show);

	/**
	 * Direction of an output of pages on the printer. 
	 * If true that pages are printed from left to right, from top to down, 
	 * differently pages are printed from top to down, from left to right.
	 * 
	 * @return true if pages are printed from left to right, otherwise false
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
