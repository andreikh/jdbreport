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

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class PasteUndoItem extends CellUndoItem {

	private int oldRowCount;

	private int oldColumnCount;

	public PasteUndoItem(JReportGrid grid, String descr) {
		super(grid, descr);
		oldRowCount = grid.getRowCount();
		oldColumnCount = grid.getColumnCount();
	}

	public UndoItem undo() {
		int count = oldRowCount - getGrid().getRowCount();
		if (count > 0) {
			getGrid().getReportModel().getRowModel().removeRows(count,
					getGrid().getRowCount() - count);
		}
		count = oldColumnCount - getGrid().getColumnCount();
		if (count > 0) {
			while (count > 0) {
				getGrid().getColumnModel().removeColumn(
						getGrid().getColumnModel()
								.getColumn(
										getGrid().getColumnModel()
												.getColumnCount() - 1));
				count--;
			}
		}
		return super.undo();
	}

}
