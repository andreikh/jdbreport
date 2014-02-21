/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2008 Andrey Kholmanskih. All rights reserved.
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import jdbreport.design.model.CellObject;

/**
 * @version 1.1 03/09/08
 * 
 * @author Andrey Kholmanskih
 * 
 */
public class BaseRowGroup extends AbstractGroup implements RowsGroup {

	private int type = Group.ROW_NONE;
	private List<TableRow> childList;

	public BaseRowGroup(GroupsGroup parent, int type) {
		super(parent);
		this.type = type;
	}

	protected List<TableRow> getChildList() {
		if (childList == null) {
			childList = new ArrayList<TableRow>();
		}
		return childList;
	}

	public int getChildIndex(Object child) {
		return getChildList().indexOf(child);
	}

	public int getChildCount() {
		if (childList == null)
			return 0;
		else
			return childList.size();
	}

	public boolean remove(Object child) {
		((ReportRow) child).setGroup(null);
		boolean result = getChildList().remove(child);
		if (getChildCount() == 0 && getParent() != null) {
			getParent().remove(this);
		}
		return result;
	}

	public TableRow remove(int index) {
		ReportRow result = (ReportRow) getChildList().remove(index);
		result.setGroup(null);
		if (getChildCount() == 0 && getParent() != null) {
			getParent().remove(this);
		}
		return result;
	}

	public TableRow getFirstGroupRow() {
		if (getChildCount() == 0)
			return null;
		return getChildList().get(0);
	}

	public TableRow getLastGroupRow() {
		if (getChildCount() == 0)
			return null;
		if (!isVisible())
			return getFirstGroupRow();
		return getChildList().get(getChildCount() - 1);
	}

	public int getType() {
		return type;
	}

	public String getTypeName() {
		return Group.typeNames[getType()];
	}

	public Object getHeaderValue() {
		return Group.typeNames[type];
	}

	public int getRowCount() {
		int count = getChildCount();
		if (!isVisible())
			return Math.min(1, count);
		return count;
	}

	public void addRow(int index, TableRow row) {
		((ReportRow) row).setGroup(this);
		if (index < 0) {
			getChildList().add(row);
		} else {
			getChildList().add(index, row);
		}
	}

	public boolean addRow(TableRow row) {
		((ReportRow) row).setGroup(this);
		return getChildList().add(row);
	}

	public int getHeight() {
		int result = 0;
		Iterator<TableRow> it = iterator();
		while (it.hasNext()) {
			result += it.next().getHeight();
		}
		return result;
	}

	protected TableRow getRow(int row) {
		return (TableRow) getChild(row);
	}

	public void updateCellChild(int row, int column) {
		TableRow tableRow = getRow(row);
		Cell cell = tableRow.getCellItem(column);
		if (cell.isSpan()) {
			if (row + cell.getRowSpan() >= getChildCount()) {
				cell.setRowSpan(getChildCount() - row - 1);
			}
			if (column + cell.getColSpan() >= tableRow.getColCount()) {
				cell.setColSpan(tableRow.getColCount() - column - 1);
			}
			for (int r = 0; r <= cell.getRowSpan(); r++) {
				for (int c = 0; c <= cell.getColSpan(); c++) {
					getRow(row + r).createCellItem(column + c).setOwner(cell);
				}
			}
		}
	}

	public Iterator<TableRow> iterator() {
		return new RowIterator(getChildList());
	}

	public Iterator<TableRow> getVisibleRowIterator() {
		return new VisibleRowIterator(getChildList());
	}

	public TableRow getChild(int index) {
		return getChildList().get(index);
	}

	public RowsGroup getGroup(TableRow row) {
		return row.getGroup();
	}

	public void clear() {
		for (TableRow child : getChildList())
			((ReportRow) child).setGroup(null);
		getChildList().clear();
	}

	public double getTotalResult(int func, int column) {
		return getTotalResult(func, iterator(), column);
	}

	protected double getTotalResult(int func, Iterator<TableRow> it, int column) {
		double result = 0;
		switch (func) {
		case CellObject.AF_SUM:
			result = 0;
			while (it.hasNext()) {
				TableRow row = it.next();
				try {
					String text = row.getCellItem(column).getText();
					if (text != null && text.length() > 0)
						result += Double.parseDouble(text);
				} catch (NumberFormatException e) {
				}
			}
			break;
		case CellObject.AF_MAX:
			result = Double.MIN_VALUE;
			while (it.hasNext()) {
				TableRow row = it.next();
				try {
					String text = row.getCellItem(column).getText();
					if (text != null && text.length() > 0)
						result = Math.max(Double.parseDouble(text), result);
				} catch (NumberFormatException e) {
				}
			}
			break;
		case CellObject.AF_MIN:
			result = Double.MAX_VALUE;
			while (it.hasNext()) {
				TableRow row = it.next();
				try {
					String text = row.getCellItem(column).getText();
					if (text != null && text.length() > 0)
						result = Math.min(Double.parseDouble(text), result);
				} catch (NumberFormatException e) {
				}
			}
			break;
		}

		return result;
	}

	private class RowIterator implements Iterator<TableRow> {

		List<TableRow> list;
		int index = 0;
		TableRow current;

		public RowIterator(List<TableRow> list) {
			this.list = list;
		}

		public boolean hasNext() {
			return index < list.size();
		}

		public TableRow next() {
			if (index < list.size()) {
				current = list.get(index++);
				return current;
			}
			throw new NoSuchElementException();
		}

		public void remove() {
			if (current != null) {
				index--;
				ReportRow row = (ReportRow) list.remove(index);
				row.setGroup(null);
				current = null;
			} else
				throw new IllegalStateException();
		}

	}

	private class VisibleRowIterator implements Iterator<TableRow> {

		List<TableRow> list;
		int index = 0;
		TableRow current;

		public VisibleRowIterator(List<TableRow> list) {
			this.list = list;
		}

		public boolean hasNext() {
			return index < list.size() && (isVisible() || index < 1);
		}

		public TableRow next() {
			if (hasNext()) {
				current = list.get(index++);
				return current;
			}
			throw new NoSuchElementException();
		}

		public void remove() {
			if (current != null) {
				index--;
				ReportRow row = (ReportRow) list.remove(index);
				row.setGroup(null);
				current = null;
			} else
				throw new IllegalStateException();
		}

	}

}
