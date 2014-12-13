/*
 * Created on 25.12.2004
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
