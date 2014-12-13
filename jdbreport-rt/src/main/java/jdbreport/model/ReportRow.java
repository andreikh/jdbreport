/*
 * Created on 20.04.2004
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

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.util.*;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * @version 2.0 12.05.2011
 * 
 * @author Andrey Kholmanskih
 * 
 */
public class ReportRow implements TableRow {

	private float height = 17;

	private boolean pageBreak;

	private SwingPropertyChangeSupport changeSupport;

	static final Cell nullCell = new NullCell();

	private static Units unit = Units.PT;

	/** The header value of the row. */
	protected Object headerValue;

	/** The renderer used to draw the header of the row. */
	protected TableCellRenderer headerRenderer;

	protected ArrayList<Cell> colList;

	private RowsGroup group;

	public ReportRow(int colcount) {
		colList = new ArrayList<>(colcount);
		for (int i = 0; i < colcount; i++) {
			addColumn(getNullCell());
		}
	}

	public ReportRow() {
		this(2);
	}

	protected Cell getNullCell() {
		return nullCell;
	}

	protected Units getDefaultUnit() {
		return unit;
	}

	public int getHeight() {
		return getDefaultUnit().getYPixels(height);
	}

	public float getNativeHeight() {
		return height;
	}

	public void setHeight(int height) {
		int old = getHeight();
		if (old != height) {
			this.height = (float)getDefaultUnit().setYPixels(
					Math.min(Math.max(height, TableRowModel.minHeight),
							TableRowModel.maxHeight));
			firePropertyChange("height", old, height);
		}
	}

	public void setHeight(int height, boolean dragging) {
		int old = getHeight();
		if (old != height || !dragging) {
			this.height = (float)getDefaultUnit().setYPixels(
					Math.min(Math.max(height, TableRowModel.minHeight),
							TableRowModel.maxHeight));
			if (dragging)
				firePropertyChange("tmpHeight", 0, height);
			else
				firePropertyChange("height", 0, height);
		}
	}

	public void addColumn(int index) {
		addColumn(index, getNullCell());
	}

	public void addColumn(Cell cellItem) {
		colList.add(cellItem);
	}

	public void addColumn(int index, Cell cellItem) {
		if (index < 0 || index >= colList.size()) {
			colList.add(cellItem);
		} else {
			colList.add(index, cellItem);
		}
	}

	public Cell removeColumn(int index) {
		return colList.remove(index);
	}

	public Cell removeCell(int index) {
		return colList.set(index, getNullCell());
	}

	public Cell getCellItem(int column) {
		if (column >= 0 && column < colList.size())
			return colList.get(column);
		return getNullCell();
	}

	public void setCellItem(Cell cellItem, int column) {
		if (column >= 0 && column < colList.size()) {
			colList.set(column, cellItem);
		}
	}

	public int getColCount() {
		return colList.size();
	}

	public void setColCount(int value) {
		if (value < colList.size()) {
			while (value < colList.size())
				colList.remove(colList.size() - 1);
		} else
			while (value > colList.size()) {
				addColumn(getNullCell());
			}
	}

	public TableCellRenderer getHeaderRenderer() {
		return headerRenderer;
	}

	/**
	 * Sets the <code>TableCellRenderer</code> used to draw the
	 * <code>TableRow</code>'s header to <code>headerRenderer</code>.
	 * 
	 * @param headerRenderer
	 *            the new headerRenderer
	 * 
	 */
	public void setHeaderRenderer(TableCellRenderer headerRenderer) {
		TableCellRenderer old = this.headerRenderer;
		this.headerRenderer = headerRenderer;
		firePropertyChange("headerRenderer", old, headerRenderer);
	}

	public Object getHeaderValue() {
		return headerValue;
	}

	/**
	 * Sets the <code>Object</code> whose string representation will be used
	 * as the value for the <code>headerRenderer</code>. When the
	 * <code>TableRow</code> is created, the default <code>headerValue</code>
	 * is <code>null</code>.
	 * 
	 * @param headerValue
	 *            the new headerValue
	 */
	public void setHeaderValue(Object headerValue) {
		this.headerValue = headerValue;
	}

	private void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		if (changeSupport != null) {
			changeSupport.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	private void firePropertyChange(String propertyName, int oldValue,
			int newValue) {
		firePropertyChange(propertyName, new Integer(oldValue), new Integer(
				newValue));
	}

	public synchronized void addPropertyChangeListener(
			PropertyChangeListener listener) {
		if (changeSupport == null) {
			changeSupport = new SwingPropertyChangeSupport(this);
		}
		changeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Removes a <code>PropertyChangeListener</code> from the listener list.
	 * The <code>PropertyChangeListener</code> to be removed was registered
	 * for all properties.
	 * 
	 * @param listener
	 *            the listener to be removed
	 * 
	 */

	public synchronized void removePropertyChangeListener(
			PropertyChangeListener listener) {
		if (changeSupport != null) {
			changeSupport.removePropertyChangeListener(listener);
		}
	}

	/**
	 * Returns an array of all the <code>PropertyChangeListener</code>s added
	 * to this TableRow with addPropertyChangeListener().
	 * 
	 * @return all of the <code>PropertyChangeListener</code>s added or an
	 *         empty array if no listeners have been added
	 * 
	 */
	public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
		if (changeSupport == null) {
			return new PropertyChangeListener[0];
		}
		return changeSupport.getPropertyChangeListeners();
	}

	//
	// Protected Methods
	//

	/**
	 * 
	 * @return the default header renderer
	 */
	protected TableCellRenderer createDefaultHeaderRenderer() {
		DefaultTableCellRenderer label = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				if (table != null) {
					JTableHeader header = table.getTableHeader();
					if (header != null) {
						setForeground(header.getForeground());
						setBackground(header.getBackground());
						setFont(header.getFont());
					}
				}
				setText((value == null) ? "" : value.toString());
				setBorder(UIManager.getBorder("TableHeader.cellBorder"));
				return this;
			}
		};
		label.setHorizontalAlignment(JLabel.CENTER);
		return label;
	}

	public boolean isNull() {
		return false;
	}

	public boolean isPageBreak() {
		return pageBreak;
	}

	public void setPageBreak(boolean end) {
		pageBreak = end;
	}

	protected Cell createDefaultCell() {
		return new ReportCell();
	}

	public Cell createCellItem(int column) {
		Cell result = colList.get(column);
		if (result == null || result.isNull()) {
			result = createDefaultCell();
			setCellItem(result, column);
		}
		return result;
	}

	/**
	 * 
	 * @param h -
	 *            new height of a row in 1/72 of inches
	 */
	public void setHeight(float h) {
		double old = this.height;
		if (old != h) {
			this.height = h;
			firePropertyChange("height", unit.getXPixels(old), getHeight());
		}
	}

	public Iterator<Cell> iterator() {
		return colList.iterator();
	}

	public RowsGroup getGroup() {
		return group;
	}

	void setGroup(RowsGroup group) {
		this.group = group;
	}

	public boolean isPageHeader() {
		if (group != null) {
			if (group.getType() == Group.ROW_PAGE_HEADER)
				return true;
			if (group.getType() == Group.ROW_GROUP_HEADER
					&& ((DetailGroup) group.getParent()).isRepeateHeader())
				return true;
		}
		return false;
	}

}
