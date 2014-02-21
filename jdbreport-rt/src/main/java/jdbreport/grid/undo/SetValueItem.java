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

import jdbreport.grid.JReportGrid;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class SetValueItem extends AbstractGridUndo {

	public static final String CELL_VALUE = Messages
			.getString("SetValueItem.0"); //$NON-NLS-1$
	private Object value;
	private int row;
	private int column;

	public SetValueItem(JReportGrid grid, Object value, int row, int column) {
		super(grid, null);
		this.value = value;
		this.row = row;
		this.column = column;
	}

	public UndoItem undo() {
		getGrid().getSelectionModel().clearSelection();
		getGrid().getColumnModel().getSelectionModel().clearSelection();
		getGrid().getSelectionModel().setSelectionInterval(row, row);
		getGrid().getColumnModel().getSelectionModel().setSelectionInterval(
				column, column);
		Object oldValue = getGrid().getValueAt(row, column);
		getGrid().setValueAt(value, row, column);
		value = oldValue;
		return super.undo();
	}

	public void clear() {
		super.clear();
		value = null;
	}

	public String getDescription() {
		return CELL_VALUE;
	}
}
