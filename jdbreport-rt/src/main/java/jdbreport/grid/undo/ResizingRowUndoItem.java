/*
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

import jdbreport.grid.JReportGrid;
import jdbreport.model.TableRow;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class ResizingRowUndoItem extends AbstractGridUndo {

	int[] rows;
	int[] heights;

	public ResizingRowUndoItem(JReportGrid grid, int[] rows, int[] heights) {
		super(grid, null);
		this.rows = rows;
		this.heights = heights;
	}

	public void clear() {
		super.clear();
		rows = null;
		heights = null;
	}

	public String getDescription() {
		return UndoItem.RESIZING_ROW;
	}

	public UndoItem undo() {
		int[] oldHeights = new int[heights.length];
		System.arraycopy(heights, 0, oldHeights, 0, heights.length);
		int n = 0;
		for (int i : rows) {
			TableRow tableRow = getGrid().getReportModel().getRowModel()
					.getRow(i);
			heights[n] = tableRow.getHeight();
			tableRow.setHeight(oldHeights[n]);
			n++;
		}
		return super.undo();
	}

	/**
	 * @return the rows
	 */
	public int[] getRows() {
		return rows;
	}

	/**
	 * @return the heights
	 */
	public int[] getHeights() {
		return heights;
	}

}
