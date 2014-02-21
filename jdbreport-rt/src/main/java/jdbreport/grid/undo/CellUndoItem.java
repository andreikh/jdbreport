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
 * 
 */
package jdbreport.grid.undo;

import java.util.ArrayList;

import jdbreport.grid.JReportGrid;
import jdbreport.model.Cell;
import jdbreport.model.CellWrap;
import jdbreport.model.JReportModel;
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
