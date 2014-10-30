/*
 * Created on 20.04.2004
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2014 Andrey Kholmanskih. All rights reserved.
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

import jdbreport.model.event.TableRowModelEvent;
import jdbreport.model.event.TableRowModelListener;
import jdbreport.util.GraphicUtil;

import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @version 3.0 22.02.2014
 * 
 * @author Andrey Kholmanskih
 * 
 */
public class ReportRowModel implements TableRowModel, PropertyChangeListener,
		ListSelectionListener, Serializable {

	private static final long serialVersionUID = 5950835021431789112L;

	private int colcount = 0;

	private int totalRowHeight = -1;

	private double preferredHeight = 17;

	private static Units unit = Units.getDefaultUnit();

	protected int rowMargin = 0;

	protected ArrayList<TableRow> rowList;

	private RootGroup rootGroup;

	private int pageHeight;

	private boolean canUpdatePages = true;

	private boolean canHideGroup = false;

	static private NullReportRow nullRow = new NullReportRow();

	protected EventListenerList listenerList = new EventListenerList();

	/** Change event (only one needed) */
	transient protected ChangeEvent changeEvent = null;

	private int spanDisabled;

	private int updated = 0;

	private boolean dirtyHeader = true;

	/**
	 * Number of the first page
	 */
	private int firstPageNumber = 1;

	private List<PageNumber> pageNumberList = new ArrayList<>();

	private List<Integer> pageColumnList;

	private List<SplitData> splitCells;

	private boolean showPageNumber = true;

	private boolean printLeftToRight;

	public ReportRowModel() {
		rowList = new ArrayList<>();
		rootGroup = createRootGroup();
	}

	public int getColCount() {
		return colcount;
	}

	public void setColCount(int colcount) {
		this.colcount = colcount;
		for (int i = 0; i < rowList.size(); i++) {
			getRow(i).setColCount(colcount);
		}
	}

	public TableRow getRow(int row) {
		if (row >= 0 && row < rowList.size())
			return rowList.get(row);
		else
			return getNullRow();
	}

	protected TableRow getNullRow() {
		return nullRow;
	}

	public void addColumn(int column) {
		disableSpan();
		try {
            for (TableRow cells : getRootGroup()) {
                cells.addColumn(column);
            }
		} finally {
			enableSpan();
		}
	}

	public void removeColumn(int column) {
		List<Cell> list = new ArrayList<>();
		for (Iterator<TableRow> it = getRootGroup().iterator(); it.hasNext();) {
			TableRow tableRow = it.next();
			Cell cell = tableRow.getCellItem(column);
			if (cell.isChild()) {
				if (list.indexOf(cell.getOwner()) < 0) {
					list.add(cell.getOwner());
				}
			}

		}
		for (Cell cell : list) {
			cell.setColSpan(cell.getColSpan() - 1);
		}
		disableSpan();
		try {
            for (TableRow cells : getRootGroup()) {
                cells.removeColumn(column);
            }
			colcount--;
		} finally {
			enableSpan();
		}
	}

	public TableRow createTableRow() {
		return new ReportRow(colcount);
	}

	public int addRows(int count, int index) {
		if (count <= 0)
			return getRowCount();
		disableSpan();
		try {
			for (int i = 0; i < count; i++) {
				addRow(index);
			}
		} finally {
			enableSpan();
		}
		return getRowCount();
	}

	public TableRow addRow() {
		return addRow(-1);
	}

	public TableRow addRow(int row) {
		TableRow result = createTableRow();
		addRow(row, result);
		return result;
	}

	public int addRow(RowsGroup group, int indexInGroup) {
		return addRow(group, indexInGroup, createTableRow());
	}

	public int addRow(RowsGroup group, int indexInGroup, TableRow tableRow) {
		invalidateHeightCache();
		setDirtyHeader(true);
		if (indexInGroup < 0)
			indexInGroup = group.getChildCount();
		int row = getGroupRowIndex(group) + indexInGroup;
		if (row < 0 || row > rowList.size())
			row = rowList.size();
		rowList.add(row, tableRow);
		group.addRow(indexInGroup, tableRow);
		if (!isUpdate()) {
			tableRow.addPropertyChangeListener(this);
			fireRowAdded(new TableRowModelEvent(this, 0, row));
		}
		return row;
	}

	public int addRow(int arow, TableRow tableRow) {
		invalidateHeightCache();
		setDirtyHeader(true);
		int row = arow + 1;
		int r = arow;
		if (arow < 0 || arow >= rowList.size()) {
			r = rowList.size() - 1;
		}
		TableRow oldRow = getRow(r);
		RowsGroup group = getGroup(oldRow);
		int newIndexGroup = 0;
		if (group != null) {
			newIndexGroup = group.getChildIndex(oldRow) + 1;
		}
		if (arow < 0 || arow >= rowList.size()) {
			if (rowList.add(tableRow)) {
				row = rowList.size() - 1;
			} else
				row = -1;
		} else {
			rowList.add(row, tableRow);
		}
		if (row >= 0 && group != null) {
			group.addRow(newIndexGroup, tableRow);
			if (!isUpdate()) {
				tableRow.addPropertyChangeListener(this);
				fireRowAdded(new TableRowModelEvent(this, 0, row));
			}
		}
		return row;
	}

	public boolean isUpdate() {
		return updated > 0;
	}

	public void startUpdate() {
		if (updated == 0) {
			for (TableRow tableRow : rowList) {
				tableRow.removePropertyChangeListener(this);
			}
			disableSpan();
		}
		updated++;
	}

	public void endUpdate() {
		if (updated == 0) {
			return;
		}
		updated--;
		if (updated == 0) {
			for (TableRow tableRow : rowList) {
				tableRow.addPropertyChangeListener(this);
			}
			enableSpan();
			fireRowUpdated();
		}
	}

	public void removeRows() {
		rowList.clear();
		getRootGroup().clear();
	}

	/**
	 * @param group Group
	 */
	protected void removeGroup(Group group) {
		group.getParent().remove(group);
		for (TableRow tableRow : group) {
			rowList.remove(tableRow);
		}
	}

	public void removeRows(int count, int index) {
		if (index < 0 || count <= 0) {
			return;
		}
		disableSpan();
		try {
			if (count + index > getRowCount()) {
				count = getRowCount() - index;
			}
			for (int i = count - 1; i >= 0 && getRowCount() > 0; i--) {
				removeRow(index);
			}
		} finally {
			enableSpan();
		}
	}

	private TableRow removeRow(int row) {
		TableRow tableRow = rowList.remove(row);

		List<Cell> list = new ArrayList<>();
		for (Cell cell : tableRow) {
			if (cell.isChild()) {
				if (list.indexOf(cell.getOwner()) < 0) {
					list.add(cell.getOwner());
				}
			}
		}
		for (Cell cell : list) {
			cell.setRowSpan(cell.getRowSpan() - 1);
		}

		if (tableRow != null) {
			tableRow.removePropertyChangeListener(this);
			invalidateHeightCache();
			setDirtyHeader(true);
			Group group = getGroup(tableRow);
			if (group != null) {
				group.remove(tableRow);
			}
			fireRowRemoved(new TableRowModelEvent(this, row, 0));
		}
		return tableRow;
	}

	public int getRowCount() {
		return rowList.size();
	}

	public void setRowCount(int value) {
		if (value > getRowCount()) {
			addRows(value - getRowCount(), -1);
		} else if (value < getRowCount()) {
			for (int i = getRowCount() - 1; i >= value; i--) {
				removeRow(i);
			}
		}
	}

	public int getTotalRowHeight() {
		if (totalRowHeight == -1) {
			recalcHeightCache();

		}
		return totalRowHeight;
	}

	protected void recalcHeightCache() {
		totalRowHeight = 0;
		for (int row = 0; row < rowList.size(); row++) {
			totalRowHeight += getRow(row).getHeight();
		}
	}

	private void invalidateHeightCache() {
		totalRowHeight = -1;
	}

	public void addRowModelListener(TableRowModelListener x) {
		listenerList.add(TableRowModelListener.class, x);
	}

	public void removeRowModelListener(TableRowModelListener x) {
		listenerList.remove(TableRowModelListener.class, x);
	}

	public TableRowModelListener[] getRowModelListeners() {
		return (TableRowModelListener[]) listenerList
				.getListeners(TableRowModelListener.class);
	}

	public int getRowIndexAtY(int y) {
		if (y < 0) {
			return -1;
		}
		int rc = getRowCount();
		for (int row = 0; row < rc; row++) {
			y = y - getRow(row).getHeight();
			if (y < 0) {
				return row;
			}
		}
		return -1;

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (!isEnabledSpan()) {
			return;
		}
		String name = evt.getPropertyName();
		if (name.equals("height") || name.equals("preferredHeight")) {
			invalidateHeightCache();
			fireRowResizing((Integer) evt.getNewValue(), false);
		} else if (name.equals("tmpHeight")) {
			invalidateHeightCache();
			fireRowResizing((Integer) evt.getNewValue(), true);
		}
	}

	public void moveDraggedRow(int rowIndex, int newIndex) {
		if (rowIndex != newIndex && rowIndex >= 0 && newIndex >= 0) {
			TableRow tableRow = rowList.remove(rowIndex);
			if (newIndex < 0 || newIndex >= rowList.size()) {
				rowList.add(tableRow);
			} else
				rowList.add(newIndex, tableRow);
		}
		fireRowMoved(new TableRowModelEvent(this, rowIndex, newIndex, true));
	}

	public void moveRow(int rowIndex, int newIndex) {
		if (newIndex >= rowList.size()) {
			newIndex = rowList.size() - 1;
		} else if (newIndex < 0) {
			newIndex = 0;
		}
		if (rowIndex != newIndex) {
			TableRow tableRow = rowList.get(rowIndex);
			if (tableRow == null)
				return;
			Group group = getGroup(tableRow);

			Group otherGroup = getGroup(newIndex);
			disableSpan();
			try {
				rowList.remove(rowIndex);
				group.remove(tableRow);
				setDirtyHeader(true);
				int newIndexGroup = otherGroup.getChildIndex(getRow(newIndex));
				rowList.add(newIndex, tableRow);
				((BaseRowGroup) otherGroup).addRow(newIndexGroup, tableRow);

			} finally {
				enableSpan();
			}
		}
		fireRowMoved(new TableRowModelEvent(this, rowIndex, newIndex));
	}

	public boolean isCollapse(Group group) {
		if (group != null && (!group.isVisible() || group.getRowCount() == 1))
			return true;
		while (group != null && group.isVisible()) {
			group = group.getParent();
		}
		return (group != null && (!group.isVisible() || group.getRowCount() == 1));
	}

	public void moveColumn(int columnIndex, int newIndex) {
		if (columnIndex == newIndex)
			return;
		Cell oldCell;
		disableSpan();
		try {
            for (TableRow reportRow : getRootGroup()) {
                oldCell = reportRow.removeColumn(columnIndex);
                reportRow.addColumn(newIndex, oldCell);
            }
		} finally {
			enableSpan();
		}
	}

	public void enableSpan() {
		if (spanDisabled > 0)
			spanDisabled--;
		if (spanDisabled == 0) {
			enableGroupSpan(getRootGroup());
		}
	}

	private void enableGroupSpan(GroupsGroup group) {
		Iterator<Group> it = group.getGroupIterator();
		while (it.hasNext()) {
			Group childGroup = it.next();
			if (childGroup instanceof BaseRowGroup) {
				updateCellChild((BaseRowGroup) childGroup);
			} else {
				enableGroupSpan((GroupsGroup) childGroup);
			}
		}
	}

	public void disableSpan() {
		if (spanDisabled == 0) {
			clearUnion(0, 0, getRowCount() - 1, getColCount() - 1);
		}
		spanDisabled++;
	}

	boolean isEnabledSpan() {
		return spanDisabled == 0;
	}

	/**
	 * 
	 */
	public void clearUnion(int topRow, int leftCol, int bottomRow, int rightCol) {
		for (int row = topRow; row <= bottomRow; row++) {
			for (int column = leftCol; column <= rightCol; column++) {
				getRow(row).getCellItem(column).setOwner(null);
			}
		}
	}

	public void unionCells(int topRow, int leftColumn, int bottomRow,
			int rightColumn) {
		int rowSpan = bottomRow - topRow;
		int colSpan = rightColumn - leftColumn;
		TableRow tableRow = getRow(topRow);
		Cell cell = tableRow.createCellItem(leftColumn);
		Group group = getGroup(tableRow);
		int i = group.getChildIndex(tableRow);
		if (i + rowSpan >= group.getChildCount()) {
			rowSpan = group.getChildCount() - i - 1;
		}
		if (rowSpan <= 0 && colSpan <= 0) {
			clearUnion(topRow, leftColumn, topRow + cell.getRowSpan(),
					leftColumn + cell.getColSpan());
			cell.setRowSpan(0);
			cell.setColSpan(0);
		} else {
			cell.setRowSpan(rowSpan);
			cell.setColSpan(colSpan);
			updateCellChild(topRow, leftColumn);
		}
	}

	void updateCellChild(int row, int column) {
		TableRow tableRow = getRow(row);
		BaseRowGroup group = (BaseRowGroup) getGroup(tableRow);
		if (group == null)
			return;
		int r = group.getChildIndex(tableRow);
		group.updateCellChild(r, column);
	}

	protected void updateCellChild(BaseRowGroup group) {
		if (group == null)
			return;
		for (int row = 0; row < group.getChildCount(); row++) {
			for (int column = 0; column < getColCount(); column++) {
				group.updateCellChild(row, column);
			}
		}
	}

	/**
	 * Sets the row margin to <code>newMargin</code>. This method also posts a
	 * <code>rowMarginChanged</code> event to its listeners.
	 * 
	 * @param newMargin
	 *            the new margin width, in pixels
	 * @see #getRowMargin
	 * @see #getTotalRowHeight
	 */
	public void setRowMargin(int newMargin) {
		if (newMargin != rowMargin) {
			invalidateHeightCache();
			rowMargin = newMargin;
			// Post rowMarginChanged event notification.
			fireRowMarginChanged();
		}
	}

	//
	// Implementing ListSelectionListener interface
	//

	// implements javax.swing.event.ListSelectionListener
	/**
	 * A <code>ListSelectionListener</code> that forwards
	 * <code>ListSelectionEvents</code> when there is a column selection change.
	 * 
	 * @param e
	 *            the change event
	 */
	public void valueChanged(ListSelectionEvent e) {
		fireRowSelectionChanged(e);
	}

	protected void fireRowUpdated() {
		if (isUpdate())
			return;
		Object[] listeners = listenerList.getListenerList();
		TableRowModelEvent e = new TableRowModelEvent(this, 0,
				getRowCount() - 1);
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TableRowModelListener.class) {
				((TableRowModelListener) listeners[i + 1]).rowUpdated(e);
			}
		}
	}

	protected void fireRowResizing(int row, boolean dragging) {
		if (isUpdate())
			return;
		Object[] listeners = listenerList.getListenerList();
		TableRowModelEvent e = new TableRowModelEvent(this, row, row, dragging);
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TableRowModelListener.class) {
				((TableRowModelListener) listeners[i + 1]).rowResized(e);
			}
		}
	}

	/**
	 * 
	 * @param e
	 *            the event received
	 * @see EventListenerList
	 */
	protected void fireRowAdded(TableRowModelEvent e) {
		if (isUpdate())
			return;
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TableRowModelListener.class) {
				((TableRowModelListener) listeners[i + 1]).rowAdded(e);
			}
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @param e
	 *            the event received
	 */
	protected void fireRowRemoved(TableRowModelEvent e) {
		if (isUpdate())
			return;
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TableRowModelListener.class) {
				((TableRowModelListener) listeners[i + 1]).rowRemoved(e);
			}
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @param e
	 *            the event received
	 * @see EventListenerList
	 */
	protected void fireRowSelectionChanged(ListSelectionEvent e) {
		if (isUpdate())
			return;
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TableRowModelListener.class) {
				((TableRowModelListener) listeners[i + 1])
						.rowSelectionChanged(e);
			}
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	protected void fireRowMarginChanged() {
		if (isUpdate())
			return;
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TableRowModelListener.class) {
				if (changeEvent == null)
					changeEvent = new ChangeEvent(this);
				((TableRowModelListener) listeners[i + 1])
						.rowMarginChanged(changeEvent);
			}
		}
	}

	protected void fireRowMoved(TableRowModelEvent e) {
		if (isUpdate())
			return;
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 1; i >= 0; i -= 1) {
			if (listeners[i] == TableRowModelListener.class) {
				((TableRowModelListener) listeners[i + 1]).rowMoved(e);
			}
		}
	}

	public int getRowMargin() {
		return rowMargin;
	}

	public int getMinRowHeight() {
		return minHeight;
	}

	public int getMaxRowHeight() {
		return maxHeight;
	}

	public int getPreferredRowHeight() {
		return unit.getYPixels(preferredHeight);
	}

	public void setPreferredRowHeight(int preferredHeight) {
		this.preferredHeight = unit.setYPixels(preferredHeight);
	}

	public int getRowHeight(int row) {
		return getRow(row).getHeight();
	}

	public void setRowHeight(int row, int rowHeight) {
		getRow(row).setHeight(rowHeight);
		invalidateHeightCache();
	}

	public void setRowHeight(TableRow row, int newHeight) {
		row.setHeight(newHeight);
		invalidateHeightCache();
	}

	public RootGroup getRootGroup() {
		return rootGroup;
	}

	protected RootGroup createRootGroup() {
		return new RootRowGroup();
	}

	public RowsGroup getGroup(int row) {
		return getRootGroup().getGroup(getRow(row));
	}

	public RowsGroup getGroup(TableRow row) {
		if (row.getGroup() == null) {
			return getRootGroup().getGroup(row);
		} else {
			return row.getGroup();
		}
	}

    public void appendGroup(Group group) {
        getRootGroup().addGroup(Integer.MAX_VALUE, group);
        int row = getGroupRowIndex(group) - getRootGroup().getPageHeaderGroup().getRowCount()
                - getRootGroup().getPageFooterGroup().getRowCount();
        if (row > rowList.size()) row = rowList.size();
        for (TableRow tableRow : group) {
            rowList.add(row++, tableRow);
        }
    }

    public void showGroup(Group group) {
		if (group.getChildCount() == 0)
			return;
		int row = rowList.indexOf(group.getFirstGroupRow()) + 1;
		Iterator<TableRow> it = group.getVisibleRowIterator();
		if (it.hasNext()) {
			TableRow tableRow = it.next();
			if (row == 0) {
				rowList.add(row++, tableRow);
			}
		}
		while (it.hasNext())
			rowList.add(row++, it.next());
	}

	public void hideGroup(Group group) {
		if (group.getChildCount() == 0)
			return;
		Iterator<TableRow> it = group.iterator();
		if (it.hasNext())
			it.next();
		while (it.hasNext())
			rowList.remove(it.next());
	}

	public void setVisibleGroup(Group group, boolean b) {
		if (isCanHideGroup() && group.isVisible() != b) {
			invalidateHeightCache();
			group.setVisible(b);
			int row = rowList.indexOf(group.getFirstGroupRow());
			if (b) {
				showGroup(group);
				if (group == getRootGroup().getGroup(Group.ROW_NONE)) {
					updatePages(row, getPageHeight());
				}
				fireRowAdded(new TableRowModelEvent(this, row + 1, 0));
			} else {
				if (group == getRootGroup().getGroup(Group.ROW_NONE))
					clearPageHeader(row);
				hideGroup(group);
				fireRowRemoved(new TableRowModelEvent(this, 0, row + 1));
			}
		}
	}

	public int getRowIndex(TableRow row) {
		return rowList.indexOf(row);
	}

	public int getGroupRowIndex(Group group) {
		int result = getGroupIndexInParent(group);
		Group parent = group.getParent();
		while (parent != null) {
			result += getGroupIndexInParent(parent);
			parent = parent.getParent();
		}
		return result;
	}

	private int getGroupIndexInParent(Group group) {
		if (group == null)
			return 0;
		GroupsGroup parent = group.getParent();
		if (parent == null)
			return 0;
		int childIndex = parent.getChildIndex(group);
		int result = 0;
		for (int i = 0; i < childIndex; i++) {
			result += parent.getChild(i).getRowCount();
		}
		return result;
	}

	public Object getHeaderValue(int row) {
		if (isCanHideGroup()) {
			if (isDirtyHeader())
				updateHeaderValue();
			return getRow(row).getHeaderValue();
		}
		return " " + (row + 1);
	}

	protected void updateHeaderValue() {
		int i = 1;
		Iterator<RowsGroup> it = getRootGroup().getAllGroupIterator();
		while (it.hasNext()) {
			RowsGroup group = it.next();
			for (int n = 0; n < group.getChildCount(); n++) {
				TableRow row = group.getChild(n);
				row.setHeaderValue("" + i++);
			}
		}
		setDirtyHeader(false);
	}

	private int getScaledHeight(int imgHeight, double scaleFactor) {
		return (int) ((imgHeight - rootGroup.getPageHeaderGroup().getHeight() - rootGroup
				.getPageFooterGroup().getHeight()) / scaleFactor);
	}

	/**
	 * removes all page headers and page footers from report
	 * 
	 */
    public void clearPageHeader(int startRow) {
		startRow = 0;
		int i = startRow;
		RowsGroup pageHeader = getRootGroup().getPageHeaderGroup();
		RowsGroup pageFooter = getRootGroup().getPageFooterGroup();
		int count = pageHeader.getChildCount();
		if (count > 0) {
			while (i < rowList.size()) {
				TableRow row2 = rowList.get(i);
				for (int n = 0; n < count; n++) {
					TableRow row = pageHeader.getChild(n);
					if (row2 == row) {
						rowList.remove(i);
						i--;
						break;
					}
				}
				i++;
			}
		}
		count = pageFooter.getChildCount();
		i = startRow;
		if (count > 0) {
			while (i < rowList.size()) {
				TableRow row2 = rowList.get(i);
				for (int n = 0; n < count; n++) {
					TableRow row = pageFooter.getChild(n);
					if (row2 == row) {
						rowList.remove(i);
						i--;
						break;
					}
				}
				i++;
			}
		}
		while (i < rowList.size()) {
			TableRow row2 = rowList.get(i);
			if (row2.getGroup() != null
					&& row2.getGroup().getType() == Group.ROW_GROUP_HEADER) {
				if (((DetailGroup) row2.getGroup().getParent())
						.isRepeateHeader()) {
					rowList.remove(i);
					i--;
				}
			}
			i++;
		}
	}

	public CellCoord getCellPosition(Cell cell) {
		if (!cell.isNull()) {
			for (int row = 0; row < getRowCount(); row++) {
				TableRow tableRow = getRow(row);
				for (int column = 0; column < getColCount(); column++) {
					if (tableRow.getCellItem(column) == cell) {
						return new CellCoord(row, column);
					}
				}
			}
		}
		return new CellCoord(-1, -1);

	}

	public void updatePages(int startRow, int pageHeight) {
		if (!isCanUpdatePages())
			return;
		setCanUpdatePages(false);
		double oldScaleY = GraphicUtil.getScaleY();
		try {
			createSplitCells();
			setPageHeight(pageHeight);
			clearPageHeader(startRow);
			restoreSpanCells();
			RowsGroup pageHeaderGroup = rootGroup.getPageHeaderGroup();
			RowsGroup pageFooterGroup = rootGroup.getPageFooterGroup();
			PageNumber pageNumber;
			getPageNumberList().clear();
			if (pageColumnList != null) {
				pageColumnList.clear();
			}

			GraphicUtil.setScaleY(1);

			pageHeight = (int) (getPageHeight() / oldScaleY);
			int pHeight = pageHeight;
			int scaledHeight = getScaledHeight(pHeight, 1);
			int h = 0;
			int index = 0;
			int rowsInPage = 0;
			pageNumber = new PageNumber(index);

			int rowHeight = 0;

			for (TableRow tableRow : rootGroup.getTitleGroup()) {
				rowHeight = tableRow.getHeight();
				h += rowHeight;
				index++;
				rowsInPage++;
				if (h == pHeight || tableRow.isPageBreak()) {
					pageNumber = addPageNumber(pageNumber, index);
					h = 0;
					rowsInPage = 0;
				} else if (h > pHeight) {
					if (rowsInPage > 1) {
						pageNumber = addPageNumber(pageNumber, index - 1);
						h = rowHeight;
						rowsInPage = 1;
					} else {
						pageNumber = addPageNumber(pageNumber, index);
						h = 0;
						rowsInPage = 0;
					}
				}
			}

			pHeight = scaledHeight;

			int di = 0;
			if (h > pHeight) {
				if (rowsInPage > 1) {
					pageNumber = addPageNumber(pageNumber, index - 1);
					h = rowHeight;
					rowsInPage = 1;
					di = 1;
				} else {
					pageNumber = addPageNumber(pageNumber, index);
					h = 0;
					rowsInPage = 0;
				}
			}

			if (pageHeaderGroup.getChildCount() > 0) {
				for (int i = 0; i < pageHeaderGroup.getChildCount(); i++) {
					TableRow tableRow = pageHeaderGroup.getChild(i);
					rowList.add(index - di, tableRow);
					index++;
				}
			}

			for (Group group : rootGroup.getBodyGroups()) {

				RowsGroup header = null;
				if (group.getType() == Group.GROUP_DETAIL) {
					DetailGroup detailGroup = (DetailGroup) group;
					// Insertion of deleted titles of groups in the table
					// beginning
					if (detailGroup.getHeaderGroup() != null
							&& detailGroup.isRepeateHeader()) {
						header = detailGroup.getHeaderGroup();
						for (int i = 0; i < header.getChildCount(); i++) {
							TableRow tableRow = header.getChild(i);
							rowList.add(index++, tableRow);
							h += tableRow.getHeight();
							rowsInPage++;
						}
					}
				}

				for (TableRow tableRow : group) {
					if (header != null && tableRow.getGroup() == header) continue;
					rowHeight = tableRow.getHeight();
					h += rowHeight;
					rowsInPage++;
					index++;

					if (h == pHeight || tableRow.isPageBreak()) {

						pageNumber = addDetailPageNumber(pageNumber, index);
						index = pageNumber.getTopIndex()
								+ pageHeaderGroup.getChildCount();

						h = 0;
						rowsInPage = 0;

						int[] result = insertGroupHeader(index);
						h += result[0];
						index += result[1];

						if (tableRow.isPageBreak() && index >= rowList.size()) {
							pageNumber = null;
						}

					} else if (h > pHeight) {

						if (rowsInPage > 1) {

							pageNumber = addDetailPageNumber(pageNumber,
									index - 1);
							index = pageNumber.getTopIndex()
									+ pageHeaderGroup.getChildCount() + 1;

							h = rowHeight;
							rowsInPage = 1;

							int[] result = insertGroupHeader(index - 1);
							h += result[0];
							index += result[1];

						} else {

							pageNumber = addDetailPageNumber(pageNumber, index);
							index = pageNumber.getTopIndex()
									+ pageHeaderGroup.getChildCount();

							h = 0;
							rowsInPage = 0;

							int[] result = insertGroupHeader(index);
							h += result[0];
							index += result[1];

						}
					}
				}
			}

			if (h > 0) {
				if (pageFooterGroup.getChildCount() > 0) {
					for (int i = 0; i < pageFooterGroup.getChildCount(); i++) {
						rowList.add(index++, pageFooterGroup.getChild(i));
					}
				}
			}

			pHeight = pageHeight;

			for (TableRow tableRow : rootGroup.getFooterGroup()) {
				rowHeight = tableRow.getHeight();
				h += rowHeight;
				index++;
				rowsInPage++;
				if (h == pHeight || tableRow.isPageBreak()) {
					pageNumber = addPageNumber(pageNumber, index);
					h = 0;
					rowsInPage = 0;
				} else if (h > pHeight) {
					if (rowsInPage > 1) {
						pageNumber = addPageNumber(pageNumber, index - 1);
						h = rowHeight;
						rowsInPage = 1;
					} else {
						pageNumber = addPageNumber(pageNumber, index);
						h = 0;
						rowsInPage = 0;
					}
				}
			}

			if (pageNumber != null)
				getPageNumberList().add(pageNumber);

		} finally {
			GraphicUtil.setScaleY(oldScaleY);
			setCanUpdatePages(true);
		}
	}

	private int[] insertGroupHeader(int index) {
		int h = 0;
		int count = 0;
		TableRow tr = getRow(index);
		Group g = getGroup(tr);
		while (g != null) {
			while (g != null && g.getType() != Group.GROUP_DETAIL) {
				g = g.getParent();
			}
			if (g != null) {
				RowsGroup header =  ((DetailGroup) g)
						.getHeaderGroup();
				if (header != null && header.getChildCount() > 0) {

					if (((DetailGroup) g).isRepeateHeader()) {
						for (int i = 0; i < header.getChildCount(); i++) {
							TableRow tableRow = header.getChild(i);
							rowList.add(index++, tableRow);
							count++;
							h += tableRow.getHeight();
						}
					}

				}
				g = g.getParent();
			}
		}
        return new int[]{ h, count };
	}

	private PageNumber addPageNumber(PageNumber pageNumber, int index) {
		if (pageNumber != null)
			getPageNumberList().add(pageNumber);
		splitCellsByPage(index);
		pageNumber = new PageNumber(index);
		return pageNumber;
	}

	private PageNumber addDetailPageNumber(PageNumber pageNumber, int index) {
		RowsGroup headerGroup = rootGroup.getPageHeaderGroup();
		RowsGroup footerGroup = rootGroup.getPageFooterGroup();

		splitCellsByPage(index);

		if (footerGroup.getChildCount() > 0) {
			for (int i = 0; i < footerGroup.getChildCount(); i++) {
				rowList.add(index++, footerGroup.getChild(i));
			}
		}

		if (pageNumber != null)
			getPageNumberList().add(pageNumber);

		pageNumber = new PageNumber(index);

		if (headerGroup.getChildCount() > 0) {
			for (int i = 0; i < headerGroup.getChildCount(); i++) {
				rowList.add(index++, headerGroup.getChild(i));
			}
		}
		return pageNumber;
	}

	private static class SplitData {

		public Cell ownerCell;
		public Cell childCell;
		public int oldColSpan;
		public int oldRowSpan;
		public int newColSpan;
		public int newRowSpan;

		public SplitData(Cell ownerCell, Cell childCell, int oldRowSpan,
				int oldColSpan, int newRowSpan, int newColSpan) {
			this.ownerCell = ownerCell;
			this.childCell = childCell;
			this.oldRowSpan = oldRowSpan;
			this.oldColSpan = oldColSpan;
			this.newRowSpan = newRowSpan;
			this.newColSpan = newColSpan;
		}

		public boolean isChange() {
			return childCell.getValue() != null
					|| ownerCell.getRowSpan() != newRowSpan
					|| ownerCell.getColSpan() != newColSpan
					|| childCell.getRowSpan() != oldRowSpan - newRowSpan - 1;
		}

		public void restore() {
			ownerCell.setRowSpan(oldRowSpan);
			ownerCell.setColSpan(oldColSpan);
			childCell.setOwner(ownerCell);
		}
	}

	private List<SplitData> createSplitCells() {
		if (splitCells == null) {
			splitCells = new ArrayList<>();
		}
		return splitCells;
	}

	private void restoreSpanCells() {
		if (splitCells == null)
			return;
		for (SplitData splitData : splitCells) {
			if (!splitData.isChange()) {
				splitData.restore();
				CellCoord coord = getCellPosition(splitData.ownerCell);
				updateCellChild(coord.row, coord.column);
			}
		}
		splitCells.clear();
	}

	private void splitCellsByPage(int row) {
		TableRow tableRow = getRow(row);
		for (int column = 0; column < tableRow.getColCount(); column++) {
			Cell cell = tableRow.getCellItem(column);
			if (cell.isChild()) {
				Cell owner = cell.getOwner();
				int ownerRow = getOwnerRow(cell, row, column);
				if (row > ownerRow) {
					int rowSpan = owner.getRowSpan();
					int newSpan = ownerRow + rowSpan - row;
					splitCells.add(new SplitData(owner, cell, owner
							.getRowSpan(), owner.getColSpan(), rowSpan
							- newSpan - 1, owner.getColSpan()));
					owner.setRowSpan(rowSpan - newSpan - 1);
					cell.setOwner(null);
					cell.setRowSpan(newSpan);
					cell.setColSpan(owner.getColSpan());
					cell.setStyleId(owner.getStyleId());
					if (cell.isSpan())
						updateCellChild(row, column);
				}
			}
			column += cell.getColSpan();
		}

	}

	public int getOwnerRow(Cell cell, int row, int column) {
		if (cell == null || cell.getOwner() == null)
			return row;
		Cell ownerCell = cell.getOwner();
		if (ownerCell.getRowSpan() > 0) {
			Cell childCell = getRow(row).getCellItem(column);
			while (childCell.getOwner() == ownerCell)
				childCell = getRow(--row).getCellItem(column);
			if (childCell != ownerCell)
				row++;
			return row;
		} else
			return row;
	}

	public Group getGroup(int[] path) {
		Group g = getRootGroup();
		for (int i : path) {
			Object child = g.getChild(i);
			if (!(child instanceof Group))
				return g;
			g = (Group) child;
		}
		return g;
	}

	private List<PageNumber> getPageNumberList() {
		return pageNumberList;
	}

	public void addPageColumn(int column) {
		if (pageColumnList == null) {
			pageColumnList = new ArrayList<>();
		}
		pageColumnList.add(column);
	}

	/**
	 * @param pageHeight
	 *            The pageHeight to set.
	 */
	public void setPageHeight(int pageHeight) {
		this.pageHeight = pageHeight;
	}

	/**
	 * @return Returns the pageHeight.
	 */
	public int getPageHeight() {
		if (pageHeight <= 0)
			pageHeight = Integer.MAX_VALUE;
		return pageHeight;
	}

	public void setRowHeight(int row, double h) {
		preferredHeight = h;
		((ReportRow) getRow(row)).setHeight((float) h);
	}

	/**
	 * @param canHideGroup
	 *            the canHideGroup to set
	 */
	protected void setCanHideGroup(boolean canHideGroup) {
		this.canHideGroup = canHideGroup;
	}

	/**
	 * @return the canHideGroup
	 */
	public boolean isCanHideGroup() {
		return canHideGroup;
	}

	/**
	 * @param dirtyHeader
	 *            the dirtyHeader to set
	 */
	protected void setDirtyHeader(boolean dirtyHeader) {
		this.dirtyHeader = dirtyHeader;
	}

	/**
	 * @return the dirtyHeader
	 */
	protected boolean isDirtyHeader() {
		return dirtyHeader;
	}

	public boolean isCanUpdatePages() {
		return canUpdatePages;
	}

	public void setCanUpdatePages(boolean b) {
		canUpdatePages = b;
	}

	public int getFirstPageNumber() {
		return firstPageNumber;
	}

	public void setFirstPageNumber(int firstPageNumber) {
		this.firstPageNumber = firstPageNumber;
	}

	public Integer getPageNumber(int row, int column) {
		if (!showPageNumber) {
			return null;
		}

		if (isPrintLeftToRight()) {
			return getPageNumberLR(row, column);
		} else {
			return getPageNumberTD(row, column);
		}
	}

	private Integer getPageNumberTD(int row, int column) {
		int firstPage = getFirstPageNumber() - 1;
		int columnSize = pageColumnList != null ? pageColumnList.size() : 0;
		int pageNumber = getPageNumberList().size();
		int colNum = columnSize;
		if (columnSize > 0) {
			for (int i = 0; i < columnSize; i++) {
				int c = pageColumnList.get(i);
				if (c >= column) {
					colNum = i;
					break;
				}
			}
		}

		int rowNum = pageNumber;
		for (int p = 0; p < pageNumber; p++) {
			PageNumber pn = getPageNumberList().get(p);
			if (pn.getTopIndex() > row) {
				rowNum = p;
				break;
			}
		}

		return pageNumber * colNum + rowNum + firstPage;
	}

	private Integer getPageNumberLR(int row, int column) {
		int firstPage = getFirstPageNumber() - 1;
		int columnSize = pageColumnList != null ? pageColumnList.size() : 0;
		int pageNumber = getPageNumberList().size();
		int colNum = columnSize;
		if (columnSize > 0) {
			for (int i = 0; i < columnSize; i++) {
				int c = pageColumnList.get(i);
				if (c >= column) {
					colNum = i;
					break;
				}
			}
		}

		int rowNum = pageNumber;
		for (int p = 0; p < pageNumber; p++) {
			PageNumber pn = getPageNumberList().get(p);
			if (pn.getTopIndex() > row) {
				rowNum = p;
				break;
			}
		}

		return (columnSize + 1) * (rowNum - 1) + colNum + 1 + firstPage;

	}

	public boolean isPrintLeftToRight() {
		return printLeftToRight;
	}

	public void setPrintLeftToRight(boolean value) {
		this.printLeftToRight = value;

	}

	public int getPageCount() {
		int columnSize = pageColumnList != null ? pageColumnList.size() : 0;
		return getPageNumberList().size() * (columnSize + 1);
	}

	public Iterator<TableRow> iterator() {
		return rowList.iterator();
	}

	private static class PageNumber {

		private int topIndex;

		public PageNumber(int topIndex) {
			this.topIndex = topIndex;
		}

		public int getTopIndex() {
			return topIndex;
		}

	}

	public void setShowPageNumber(boolean show) {
		showPageNumber = show;
	}

}
