/*
 * ResizingColumnUndoitem.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2008 Andrey Kholmanskih. All rights reserved.
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
