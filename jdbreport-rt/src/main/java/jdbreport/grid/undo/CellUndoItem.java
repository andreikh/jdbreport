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

import java.util.ArrayList;

import jdbreport.grid.JReportGrid;
import jdbreport.model.Cell;
import jdbreport.model.CellWrap;
import jdbreport.view.model.JReportModel;
import jdbreport.model.ReportModel;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class CellUndoItem extends AbstractGridUndo {

	private ArrayList<CellWrap> list;

	public CellUndoItem(JReportGrid grid, String descr) {
		super(grid, descr);
		fillCellList(grid.getReportModel());
	}

	private void fillCellList(ReportModel model) {
		list = new ArrayList<CellWrap>();
		if (selectedRect != null)
			for (int row = selectedRect.getTopRow(); row <= selectedRect
					.getBottomRow(); row++) {
				for (int column = selectedRect.getLeftCol(); column <= selectedRect
						.getRightCol(); column++) {
					list.add(new JReportModel.ReportCellWrap(row, column,
							(Cell) model.getReportCell(row, column).clone()));
				}
			}
	}

	public UndoItem undo() {
		ArrayList<CellWrap> oldList = list;
		fillCellList(getGrid().getReportModel());
		getGrid().getReportModel().getRowModel().disableSpan();
		try {
			for (CellWrap cellWrap : oldList) {
				getGrid().getReportModel().getRowModel().getRow(
						cellWrap.getRow()).setCellItem(cellWrap.getCell(),
						cellWrap.getColumn());
			}
		} finally {
			getGrid().getReportModel().getRowModel().enableSpan();
		}
		return super.undo();
	}

	public void clear() {
		super.clear();
		list.clear();
	}

}
