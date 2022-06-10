/*
 * Created on 22.09.2004
 *
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

import jdbreport.model.event.TableRowModelListener;

/**
 * Defines the requirements for a report row model object suitable for use with
 * ReportModel.
 *
 * @author Andrey Kholmanskih
 * @version 1.4 16.08.2009
 */
public interface TableRowModel extends Iterable<TableRow> {

    int minHeight = 2;

    int maxHeight = 4096;

    /**
     * Adds the row at the specified position
     *
     * @param row index at which the row is to be inserted.
     * @return the inserted TableRow
     */
    TableRow addRow(int row);

    /**
     * Adds the new row
     *
     * @return the index of the inserted row
     */
    TableRow addRow();

    /**
     * Adds the row at the specified position
     *
     * @param row      the row's number
     * @param tableRow the TableRow object
     * @return the index of the inserted row
     */
    int addRow(int row, TableRow tableRow);

    /**
     * Adds the row to the group
     *
     * @param group        RowsGroup
     * @param indexInGroup index in group
     * @return row's index in rows list
     */
    int addRow(RowsGroup group, int indexInGroup);

    /**
     * Adds the row to the group
     *
     * @param group        RowsGroup
     * @param indexInGroup index in group
     * @param tableRow     TableRow
     * @return row's index in the list
     */
    int addRow(RowsGroup group, int indexInGroup, TableRow tableRow);

    /**
     * Adds column in the model
     *
     * @param column the specified position in this model
     */
    void addColumn(int column);

    /**
     * Removes column from the model
     *
     * @param column the index of the removing column
     */
    void removeColumn(int column);

    /**
     * Returns columns' count in the model
     *
     * @return the columns count
     */
    int getColCount();

    /**
     * Sets the columns' count in the model If the columns' count is bigger than
     * parameter, the columns are removed from the model If the columns' count
     * is smaller than parameter, the columns are added to the model
     *
     * @param count new columns' count
     */
    void setColCount(int count);

    /**
     * Returns rows' count in the model
     *
     * @return the rows count
     */
    int getRowCount();

    /**
     * Returns the TableRow from the specified position
     *
     * @param row the row's number
     * @return the TableRow
     */
    TableRow getRow(int row);

    /**
     * Returns row's position in the model
     *
     * @param tableRow TableRow to search for.
     * @return the index in this model of the first occurrence of the specified
     * tableRow, or -1 if this model does not contain this tableRow.
     */
    int getRowIndex(TableRow tableRow);

    /**
     * Returns the index of the row that lies on the vertical point, y; or -1 if
     * it lies outside the any of the row's bounds.
     *
     * @param y y coordinate of point
     * @return the index of the row; or -1 if no row is found
     */
    int getRowIndexAtY(int y);

    /**
     * Adds a listener for report row model events.
     *
     * @param x a TableRowModelListener object
     */
    void addRowModelListener(TableRowModelListener x);

    /**
     * Removes a listener for report row model events.
     *
     * @param x a TableRowModelListener object
     */
    void removeRowModelListener(TableRowModelListener x);

    /**
     * Returns the total height of all the rows.
     *
     * @return the total computed height of all rows
     */
    int getTotalRowHeight();

    /**
     * Moves the row and its header at rowIndex to newIndex. The old row at
     * rowIndex will now be found at newIndex. The row that used to be at
     * newIndex is shifted top or bottom to make room. This will not move any
     * rows if rowIndex equals newIndex. This method posts a rowMoved event to
     * its listeners.
     *
     * @param rowIndex the index of row to be moved
     * @param newIndex index of the row's new location
     */
    void moveRow(int rowIndex, int newIndex);

    /**
     * Move row
     *
     * @param group    old group
     * @param index    old row index in group
     * @param newGroup new group
     * @param newIndex new row index in group
     * @since version 3.1
     */
    void moveRow(Group group, int index, Group newGroup, int newIndex);

    /**
     * Move group
     *
     * @param group    moved group
     * @param newIndex new index for group in parent
     * @param parent   group parent
     * @return group index
     */
    int moveGroup(Group group, int newIndex, TreeRowGroup parent);

    /**
     * Moves the dragged row and its header at rowIndex to newIndex.
     *
     * @param rowIndex the index of row to be moved
     * @param newIndex index of the row's new location
     */
    void moveDraggedRow(int rowIndex, int newIndex);

    /**
     * Returns the row between the cells in each row.
     *
     * @return the margin, in pixels, between the cells
     */
    int getRowMargin();

    /**
     * Returns the minimum height of a report row, in pixels. The default
     * minimum row height is 2.0.
     *
     * @return the mimimum height in pixels of a report row
     */
    int getMinRowHeight();

    /**
     * Returns the maximum height of a report row, in pixels. The default
     * maximum row height is 4096.0.
     *
     * @return the maximum height in pixels of a report row
     */
    int getMaxRowHeight();

    /**
     * Returns the preferred height of a report row, in pixels. The default row
     * height is 17.0.
     *
     * @return the preferred height in pixels of a report row
     */
    int getPreferredRowHeight();

    /**
     * Sets the preferred height for row.
     *
     * @param preferredHeight new preferred row height, in pixels
     */
    void setPreferredRowHeight(int preferredHeight);

    /**
     * Moves the column at columnIndex to newIndex.
     *
     * @param columnIndex the index of column to be moved
     * @param newIndex    index of the column's new location
     */
    void moveColumn(int columnIndex, int newIndex);

    /**
     * Returns the height, in pixels, of the row. The default row height is
     * 17.0.
     *
     * @param row the row whose height is to be returned
     * @return the height in pixels of a report row
     */
    int getRowHeight(int row);

    /**
     * Sets the height for row to rowHeight. The height of the cells in this row
     * will be equal to the row height minus the row margin.
     *
     * @param row       the row whose height is being changed
     * @param rowHeight new row height, in pixels
     */
    void setRowHeight(int row, int rowHeight);

    /**
     * Sets the height for tableRow to newHeight. The height of the cells in
     * this row will be equal to the row height minus the row margin.
     *
     * @param tableRow  the row whose height is being changed
     * @param newHeight new row height, in pixels
     */
    void setRowHeight(TableRow tableRow, int newHeight);

    /**
     * Sets the height for row to h. The height of the cells in this row will be
     * equal to the row height minus the row margin.
     *
     * @param row the row whose height is being changed
     * @param h   new row height, in 1/72 of inch
     */
    void setRowHeight(int row, double h);

    /**
     * @return the RootGroup of repoprt model
     */
    RootGroup getRootGroup();

    /**
     * Returns the group which contains the TableRow by specified row index
     *
     * @param row the row's index in rowList
     * @return the RowsGroup object
     */
    RowsGroup getGroup(int row);

    /**
     * Returns the group which contains the tableRow
     *
     * @param tableRow the TableRow object
     * @return the RowsGroup object
     */
    RowsGroup getGroup(TableRow tableRow);

    /**
     * append group
     *
     * @param group Group object
     */
    void appendGroup(Group group);

    /**
     * If parameter b is true, sets the group that is visible in report,
     * otherwise removes rows containing in the group from report
     *
     * @param group Group object
     * @param b     visible property
     */
    void setVisibleGroup(Group group, boolean b);

    /**
     * Returns the index of the first group's row in rowList
     *
     * @param group the Group object
     * @return index first group's row in rowList
     */
    int getGroupRowIndex(Group group);

    /**
     * Returns value for row's header
     *
     * @param row the row's number
     * @return value for row's header
     */
    Object getHeaderValue(int row);

    /**
     * Calculates pages' size
     *
     * @param startRow   the first row for calculation
     * @param pageHeight page height in pixels
     */
    void updatePages(int startRow, int pageHeight);

    /**
     * Unions the cells by coordinates
     *
     * @param topRow      the number of the top row
     * @param leftColumn  the number of the left column
     * @param bottomRow   the number of the bottom row
     * @param rightColumn the number of the right column
     */
    void unionCells(int topRow, int leftColumn, int bottomRow,
                    int rightColumn);

    /**
     * Clears the unions of the selected cells
     *
     * @param topRow      the number of the top row
     * @param leftColumn  the number of the left column
     * @param bottomRow   the number of the bottom row
     * @param rightColumn the number of the right column
     */
    void clearUnion(int topRow, int leftColumn, int bottomRow,
                    int rightColumn);

    /**
     * Clears all cells unions before moving of rows or columns
     */
    void disableSpan();

    /**
     * Restores all cells unions after moving of rows or columns
     */
    void enableSpan();

    /**
     * Adds rows' count to the index of the model. The new rows will contain
     * null values. Notification of the row being added will be generated.
     *
     * @param count rows' count
     * @param index the row index of the rows to be inserted
     * @return rows' count in the model
     */
    int addRows(int count, int index);

    /**
     * Removes all rows
     */
    void removeRows();

    /**
     * Removes rows' count begining with the index from the model.
     *
     * @param count rows' count
     * @param index index of the first removed row
     */
    void removeRows(int count, int index);

    /**
     * Remove row
     *
     * @param row table row
     * @since version 3.1
     */
    void removeRow(TableRow row);

    /**
     * Remove group and all group rows
     *
     * @param group Group
     * @since version 3.1
     */
    void removeGroupRows(Group group);

    /**
     * Creates new default TableRow
     *
     * @return the TableRow object
     */
    TableRow createTableRow();

    /**
     * Bans notification of listeners before updates of model
     */
    void startUpdate();

    /**
     * Allows notification of listeners after updates of model
     */
    void endUpdate();

    /**
     * Returns the possibility of calculation of pages' size
     *
     * @return if true, pages' size are calculated automatically
     */
    boolean isCanUpdatePages();

    /**
     * Sets canUpdatePages property
     *
     * @param b the canUpdatePages property
     */
    void setCanUpdatePages(boolean b);

    boolean isCanHideGroup();

    /**
     * Returns number of the first page
     *
     * @return number of the first page
     * @since 1.4
     */
    int getFirstPageNumber();

    /**
     * Sets number of the first page
     *
     * @param firstPageNumber number of the first page
     * @since 1.4
     */
    void setFirstPageNumber(int firstPageNumber);

    /**
     * Returns number of page for a cell on a row and a column
     *
     * @param row    row index
     * @param column column index
     * @return number of page
     */
    Integer getPageNumber(int row, int column);

    /**
     * Returns count of pages
     *
     * @return count of pages
     */
    int getPageCount();

    Group getGroup(int[] path);

    void setShowPageNumber(boolean show);

    /**
     * Direction of an output of pages on the printer.
     * If true that pages are printed from left to right, from top to down,
     * differently pages are printed from top to down, from left to right.
     *
     * @return true if pages are printed from left to right, otherwise false
     * @since 2.0
     */
    boolean isPrintLeftToRight();

    /**
     * Direction of an output of pages on the printer.
     * If true that pages are printed from left to right, from top to down,
     * differently pages are printed from top to down, from left to right.
     *
     * @param value left to right flag
     * @since 2.0
     */
    void setPrintLeftToRight(boolean value);

    boolean isHideFirstHeader();

    void setHideFirstHeader(boolean hideFirstHeader);

}
