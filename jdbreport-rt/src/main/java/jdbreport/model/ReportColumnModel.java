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

import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.*;

/**
 * @version 3.0 13.12.2014
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

		aColumn = tableColumns.elementAt(columnIndex);

		tableColumns.removeElementAt(columnIndex);
		boolean selected = selectionModel.isSelectedIndex(columnIndex);
		selectionModel.removeIndexInterval(columnIndex, columnIndex);

		tableColumns.insertElementAt(aColumn, newIndex);
		aColumn.setModelIndex(newIndex);

		tableColumns.elementAt(columnIndex)
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
	 * @param e TableColumnModelEvent
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
