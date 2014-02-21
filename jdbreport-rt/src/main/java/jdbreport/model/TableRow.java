/*
 * Created on 22.09.2004
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2011 Andrey Kholmanskih. All rights reserved.
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
