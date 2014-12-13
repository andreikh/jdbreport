/**
 * Created 23.04.2007
 *
 * Copyright (C) 2007-2014 Andrey Kholmanskih
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
package jdbreport.util.finder;

import javax.swing.*;

/**
 * @author Andrey Kholmanskih
 * 
 * @version 3.0 13.12.2014
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
	 * @param findParams FindParams
	 * @return true if found
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
	 * @param findParams FindParams
	 * @return true if found
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
		int curRow;
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
		int curRow;
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
			boolean result = findParams.isIncremental() ? otherText.startsWith(text) : otherText.contains(text);
			if (result) {
				table.changeSelection(row, col, false, false);
				return true;
			}
		}
		return false;
	}

}
