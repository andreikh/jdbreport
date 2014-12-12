/**
 * Created 23.04.2007
 *
 * Copyright (C) 2007 Andrey Kholmanskih. All rights reserved.
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
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
 */
package jdbreport.util.finder;

import javax.swing.*;

/**
 * @author Andrey Kholmanskih
 * 
 * @version 1.1 04/30/08
 */
public class TableFinder implements Finder {

	private boolean cancelFind;
	private JTable table;

	public TableFinder(JTable table) {
		this.table = table;
	}

	public JTable getTable() {
		return table;
	}

	public boolean find(FindParams findParams) {
		cancelFind = false;
		if (findParams.getDirection() == FindParams.FORWARD)
			return findNext(findParams);
		return findPrevious(findParams);
	}

	public boolean incrementalFind(FindParams findParams) {
		if (findParams.getDirection() == FindParams.FORWARD)
			return findNextIncr(findParams);
		return findPrevIncr(findParams);
	}

	/**
	 * @param findParams
	 * @return
	 */
	private boolean findNextIncr(FindParams findParams) {
		int curRow = table.getSelectedRow();
		if (curRow < 0) curRow = 0;
		else if (curRow > table.getRowCount()) {
			curRow = table.getRowCount() - 1;
		}
		int col = 0;
		if (findParams.isAllColumn() && table.getColumnSelectionAllowed()) {
			col = table.getSelectedColumn();
		}
		int lastRow = table.getRowCount() - 1;
		if (findParams.isWrapSearch()) {
			if (curRow  > 0) lastRow = curRow - 1;
		}
		while (!cancelFind && curRow < table.getRowCount()) {
			if (findParams.isAllColumn()) {
				while (col < table.getColumnCount()) {
					if (checkEquals(curRow, col, findParams)) {
						return true;
					}
					col++;
				}
				col = 0;
			} else {
				if (checkEquals(curRow, findParams.getColumn(), findParams)) {
					return true;
				}
			}
			if (findParams.isWrapSearch()) {
				if (curRow == lastRow) {
					break;
				}
				if (curRow == table.getRowCount() - 1) {
					curRow = -1;
				}
					
			} else
			if (curRow == table.getRowCount() - 1) {
				break;
			}
			curRow++;
		}
		return false;
	}

	/**
	 * @param findParams
	 * @return
	 */
	private boolean findPrevIncr(FindParams findParams) {
		int curRow = table.getSelectionModel().getAnchorSelectionIndex();
		int col = table.getColumnCount() - 1;
		if (findParams.isAllColumn() && table.getColumnSelectionAllowed()) {
			col = table.getSelectedColumn();
		}
		int lastRow = 0;
		if (findParams.isWrapSearch()) {
			if (curRow  < table.getRowCount() - 1) lastRow = curRow + 1;
		}
		while (!cancelFind) {
			if (findParams.isAllColumn()) {
				while (col >= 0) {
					if (checkEquals(curRow, col, findParams)) {
						return true;
					}
					col--;
				}
				col = table.getColumnCount() - 1;
			} else {
				if (checkEquals(curRow, findParams.getColumn(), findParams)) {
					return true;
				}
			}
			if (findParams.isWrapSearch()) {
				if (curRow == lastRow) {
					break;
				}
				if (curRow == 0) {
					curRow = table.getRowCount();
				}
					
			} else
			if (curRow == 0) {
				break;
			}
			curRow--;
		}
		return false;
	}

	private boolean findNext(FindParams findParams) {
		int curRow = 0;
		int col = 0;
		if (findParams.getScope() == FindParams.SCOPE_ALL) {
			curRow = 0;
		} else {
			curRow = table.getSelectionModel().getAnchorSelectionIndex();
			if (!findParams.isAllColumn() || !table.getColumnSelectionAllowed()) {
				curRow++;
			} else
				col = table.getSelectedColumn() + 1;
		}
		int lastRow = table.getRowCount() - 1;
		if (findParams.isWrapSearch()) {
			if (curRow  > 0) lastRow = curRow - 1;
		}
		while (!cancelFind) {
			if (findParams.isAllColumn()) {
				while (col < table.getColumnCount()) {
					if (checkEquals(curRow, col, findParams)) {
						return true;
					}
					col++;
				}
				col = 0;
			} else {
				if (checkEquals(curRow, findParams.getColumn(), findParams)) {
					return true;
				}
			}
			if (findParams.isWrapSearch()) {
				if (curRow == lastRow) {
					break;
				}
				if (curRow == table.getRowCount() - 1) {
					curRow = -1;
				}
					
			} else
			if (curRow == table.getRowCount() - 1) {
				break;
			}
			curRow++;
		} 
		
		return false;
	}

	private boolean findPrevious(FindParams findParams) {
		int curRow = 0;
		int col = table.getColumnCount() - 1;
		if (findParams.getScope() == FindParams.SCOPE_ALL)
			curRow = table.getRowCount() - 1;
		else {
			curRow = table.getSelectionModel().getAnchorSelectionIndex();
			if (!findParams.isAllColumn() || !table.getColumnSelectionAllowed()) {
				curRow--;
			} else
				col = table.getSelectedColumn() - 1;
		}
		int lastRow = 0;
		if (findParams.isWrapSearch()) {
			if (curRow  < table.getRowCount() - 1) lastRow = curRow + 1;
		}
		while (!cancelFind) {
			if (findParams.isAllColumn()) {
				while (col >= 0) {
					if (checkEquals(curRow, col, findParams)) {
						return true;
					}
					col--;
				}
				col = table.getColumnCount() - 1;
			} else {
				if (checkEquals(curRow, findParams.getColumn(), findParams)) {
					return true;
				}
			}
			if (findParams.isWrapSearch()) {
				if (curRow == lastRow) {
					break;
				}
				if (curRow == 0) {
					curRow = table.getRowCount();
				}
					
			} else
			if (curRow == 0) {
				break;
			}
			curRow--;
		}
		return false;
	}

	protected boolean checkEquals(int row, int col, FindParams findParams) {
		String text;
		if (findParams.isCaseSensitive())
			text = findParams.getFindText();
		else
			text = findParams.getFindText().toLowerCase();
		Object value = table.getValueAt(row, col);
		if (value != null) {
			String otherText;
			if (findParams.isCaseSensitive())
				otherText = value.toString();
			else
				otherText = value.toString().toLowerCase();
			boolean result = findParams.isIncremental() ? otherText.startsWith(text) : otherText.indexOf(text) >= 0;
			if (result) {
				table.changeSelection(row, col, false, false);
				return true;
			}
		}
		return false;
	}

}
