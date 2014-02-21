/*
 * Created on 25.12.2004
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
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.table.TableCellRenderer;

/**
 * @version 2.0 12.05.2011
 * 
 * @author Andrey Kholmanskih
 * 
 */
public class NullReportRow implements TableRow {

	private static final ArrayList<Cell> list = new ArrayList<Cell>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.TableRow#getHeight()
	 */
	public int getHeight() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.TableRow#setHeight(int)
	 */
	public void setHeight(int height) {

	}

	public void setHeight(int height, boolean dragging) {

	}
	
	public float getNativeHeight() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.TableRow#getHeaderRenderer()
	 */
	public TableCellRenderer getHeaderRenderer() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.TableRow#getHeaderValue()
	 */
	public Object getHeaderValue() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.TableRow#getCellItem(int)
	 */
	public Cell getCellItem(int column) {
		return createCellItem(column);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.TableRow#setCellItem(jdbreport.interfaces.Cell,
	 *      int)
	 */
	public void setCellItem(Cell cellItem, int column) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.TableRow#getColCount()
	 */
	public int getColCount() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.TableRow#setColCount(int)
	 */
	public void setColCount(int count) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.TableRow#isNull()
	 */
	public boolean isNull() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.TableRow#addColumn(int,
	 *      jdbreport.interfaces.Cell)
	 */
	public void addColumn(int index, Cell cellItem) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.TableRow#removeColumn(int)
	 */
	public Cell removeColumn(int index) {
		return ReportRow.nullCell;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.TableRow#addColumn(int)
	 */
	public void addColumn(int index) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.TableRow#isEndPage()
	 */
	public boolean isPageBreak() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.TableRow#setEndPage(boolean)
	 */
	public void setPageBreak(boolean end) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.TableRow#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.TableRow#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {

	}

	public Cell createCellItem(int column) {
		return ReportRow.nullCell;
	}

	public Cell removeCell(int index) {
		return ReportRow.nullCell;
	}

	public void setHeaderValue(Object value) {
	}

	public Iterator<Cell> iterator() {
		return list.iterator();
	}

	public RowsGroup getGroup() {
		return null;
	}

	public boolean isPageHeader() {
		return false;
	}

}
