/*
 * ResizingColumnUndoitem.java
 *
 * JDBReport Generator
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

package jdbreport.grid.undo;

import javax.swing.table.TableColumn;

import jdbreport.grid.JReportGrid;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class ResizingColumnUndoItem extends AbstractGridUndo {

	int[] columns;
	int[] widths;

	public ResizingColumnUndoItem(JReportGrid grid, int[] columns, int[] widths) {
		super(grid, null);
		this.columns = columns;
		this.widths = widths;
	}

	public void clear() {
		super.clear();
		columns = null;
		widths = null;
	}

	public String getDescription() {
		return UndoItem.RESIZING_COLUMN;
	}

	public UndoItem undo() {
		int[] oldWidths = new int[widths.length];
		System.arraycopy(widths, 0, oldWidths, 0, widths.length);
		int n = 0;
		for (int i : columns) {
			TableColumn tableColumn = getGrid().getColumnModel().getColumn(i);
			widths[n] = tableColumn.getWidth();
			tableColumn.setWidth(oldWidths[n]);
			n++;
		}
		return super.undo();
	}

	/**
	 * @return the columns
	 */
	public int[] getColumns() {
		return columns;
	}

	/**
	 * @return the widths
	 */
	public int[] getWidths() {
		return widths;
	}

}
