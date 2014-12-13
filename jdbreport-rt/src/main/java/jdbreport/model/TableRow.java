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

import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.table.TableCellRenderer;

/**
 * Interface for a row of the report
 * 
 * @version 2.0 11.05.2011
 * @author Andrey Kholmanskih
 * 
 */
public interface TableRow extends Iterable<Cell> {

	/**
	 * @return row's height in pixels
	 */
	public int getHeight();

	/**
	 * Sets the row's height
	 * 
	 * @param height
	 *            new row's height
	 */
	public void setHeight(int height);

	/**
	 * Sets the row's height when resizing
	 * 
	 * @param height
	 *            new row's height
	 * @param dragging
	 */
	public void setHeight(int height, boolean dragging);

	/**
	 * @return row's height in points
	 */
	public float getNativeHeight();
	
	/**
	 * Returns renderer for the row's header
	 * 
	 * @return TableCellRenderer
	 */
	public TableCellRenderer getHeaderRenderer();

	/**
	 * 
	 * @param column
	 * @return Cell for specified column
	 */
	public Cell getCellItem(int column);

	/**
	 * Sets Cell for specified column
	 * 
	 * @param cellItem -
	 *            a new Cell
	 * @param column
	 *            the column's index
	 */
	public void setCellItem(Cell cellItem, int column);

	/**
	 * Creates a new Cell for specified column in the current row
	 * 
	 * @param column
	 * @return created Cell
	 */
	public Cell createCellItem(int column);

	/**
	 * 
	 * @return column's count
	 */
	public int getColCount();

	/**
	 * Sets column's count
	 * 
	 * @param count
	 */
	public void setColCount(int count);

	/**
	 * Inserts the null cell at the specified position in this row
	 * 
	 * @param index
	 *            index at which the column is to be inserted.
	 */
	public void addColumn(int index);

	/**
	 * Inserts the Cell at the specified position in this row
	 * 
	 * @param index
	 *            index at which the specified Cell is to be inserted.
	 * @param cellItem
	 *            Cell to be inserted.
	 */
	public void addColumn(int index, Cell cellItem);

	/**
	 * Removes the cell at the specified position in this row Makes the columns'
	 * count smaller
	 * 
	 * @param index
	 *            the index of the cell to removed.
	 * @return the Cell previously at the specified position.
	 */
	public Cell removeColumn(int index);

	/**
	 * Replaces the cell at the specified position by NullCell
	 * 
	 * @param index
	 *            the index of the cell to removed.
	 * @return the Cell previously at the specified position.
	 */
	public Cell removeCell(int index);

	/**
	 * Returns true if this row is the end of the page.
	 * 
	 * @return pageBreak property
	 */
	public boolean isPageBreak();

	/**
	 * Sets pageBreak property
	 * 
	 * @param b
	 *            if true, the sets horizontal page break after row
	 */
	public void setPageBreak(boolean b);

	/**
	 * Adds a PropertyChangeListener to the listener list.
	 * 
	 * @param listener
	 *            the PropertyChangeListener to be added
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Removes a PropertyChangeListener from the listener list. This method
	 * should be used to remove PropertyChangeListeners that were registered for
	 * all bound properties of this class.
	 * 
	 * @param listener
	 *            the PropertyChangeListener to be removed
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * If true, the row is null
	 * 
	 * @return if true, the row is null
	 */
	public boolean isNull();

	/**
	 * Returns the value of the row's header
	 * 
	 * @return the value of the row's header
	 */
	public Object getHeaderValue();

	/**
	 * Sets the value of the row's header
	 * 
	 * @param value
	 *            new header's value
	 */
	public void setHeaderValue(Object value);

	/**
	 * Returns an iterator over the cells in this row in proper sequence.
	 * 
	 * @return iterator of cells
	 */
	public Iterator<Cell> iterator();

	/**
	 * Returns parental group
	 *	
	 * @return parental group
	 */
	public RowsGroup getGroup();

	/**
	 * Determines  an accessory of a row to page heading
	 * @return true, if row in page header, otherwise false
	 * @since 2.0
	 */
	public boolean isPageHeader();
	
}
