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

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 *
 */
public class RowBreakUndoItem extends AbstractGridUndo {

	private boolean endPage;
	private int row;

	public RowBreakUndoItem(JReportGrid grid, int row) {
		super(grid, "End Page");
		this.row = row;
		this.endPage = grid.getReportModel().isRowBreak(row);
	}

	@Override
	public UndoItem undo() {
		getGrid().getReportModel().setRowBreak(row, endPage);
		endPage = !endPage;
		return super.undo();
	}

	
}
