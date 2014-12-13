/*
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
