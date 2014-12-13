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
