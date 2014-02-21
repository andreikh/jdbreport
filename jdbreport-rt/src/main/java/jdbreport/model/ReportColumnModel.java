/*
 * Created on 20.04.2004
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2008 Andrey Kholmanskih. All rights reserved.
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

import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.*;

/**
 * @version 1.1 03/09/08
 * 
 * @author Andrey Kholmanskih
 * 
 */
public class ReportColumnModel extends DefaultTableColumnModel {

	private static final long serialVersionUID = -1312356804702385964L;

	public ReportColumnModel() {
		super();
	}

	public int getColumnMargin() {
		return 2;
	}

	public void moveColumn(int columnIndex, int newIndex) {
		if ((columnIndex < 0) || (columnIndex >= getColumnCount())
				|| (newIndex < 0) || (newIndex >= getColumnCount()))
			throw new IllegalArgumentException(
					"moveColumn() - Index out of range");

		TableColumn aColumn;

		if (columnIndex == newIndex) {
			fireColumnMoved(new TableColumnModelEvent(this, columnIndex,
					newIndex));
			return;
		}
		TableColumnModelEvent event = new TableColumnModelEvent(this,
				columnIndex, newIndex);
		fireColumnMoving(event);

		aColumn = (TableColumn) tableColumns.elementAt(columnIndex);

		tableColumns.removeElementAt(columnIndex);
		boolean selected = selectionModel.isSelectedIndex(columnIndex);
		selectionModel.removeIndexInterval(columnIndex, columnIndex);

		tableColumns.insertElementAt(aColumn, newIndex);
		aColumn.setModelIndex(newIndex);

		((TableColumn) tableColumns.elementAt(columnIndex))
				.setModelIndex(columnIndex);
		selectionModel.insertIndexInterval(newIndex, 1, true);
		if (selected) {
			selectionModel.addSelectionInterval(newIndex, newIndex);
		} else {
			selectionModel.removeSelectionInterval(newIndex, newIndex);
		}

		fireColumnMoved(event);
	}

	public void addColumn(TableColumn aColumn, int index) {
		if (aColumn == null) {
			throw new IllegalArgumentException("Object is null");
		}
		if (index < 0)
			index = 0;
		else if (index > getColumnCount())
			index = getColumnCount();

		tableColumns.insertElementAt(aColumn, index);
		aColumn.setModelIndex(index);
		for (int i = index + 1; i < getColumnCount(); i++) {
			tableColumns.get(i).setModelIndex(i);
		}
		aColumn.addPropertyChangeListener(this);
		totalColumnWidth = -1;

		fireColumnAdded(new TableColumnModelEvent(this, index,
				getColumnCount() - 1));
	}

	/**
	 * Runs before column moved
	 * 
	 * @param e
	 */
	protected void fireColumnMoving(TableColumnModelEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ReportColumnModelListener.class) {
				((ReportColumnModelListener) listeners[i + 1]).columnMoving(e);
			}
		}
	}

	public void addColumnModelListener(TableColumnModelListener x) {
		if (x instanceof ReportColumnModelListener)
			listenerList.add(ReportColumnModelListener.class,
					(ReportColumnModelListener) x);
		listenerList.add(TableColumnModelListener.class, x);
	}

	public void removeColumnModelListener(TableColumnModelListener x) {
		if (x instanceof ReportColumnModelListener)
			listenerList.remove(ReportColumnModelListener.class,
					(ReportColumnModelListener) x);
		listenerList.remove(TableColumnModelListener.class, x);
	}

}
