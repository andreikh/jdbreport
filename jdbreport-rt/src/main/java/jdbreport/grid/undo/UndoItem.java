/*
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2018 Andrey Kholmanskih
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

/**
 * @version 3.1.3 29.06.2018
 * @author Andrey Kholmanskih
 * 
 */
public interface UndoItem {

	String RESIZING_COLUMN = Messages.getString("UndoItem.0"); //$NON-NLS-1$
	String RESIZING_ROW = Messages.getString("UndoItem.1"); //$NON-NLS-1$
	String ROW_MOVED = Messages.getString("UndoItem.2"); //$NON-NLS-1$
	String CELL_BACKGROUND = Messages.getString("UndoItem.3"); //$NON-NLS-1$
	String PASTE_CELLS = Messages.getString("UndoItem.4"); //$NON-NLS-1$
	String DELETE_CELLS = Messages.getString("UndoItem.5"); //$NON-NLS-1$
	String CELL_ALIGN = Messages.getString("UndoItem.6"); //$NON-NLS-1$
	String CELL_BORDER = Messages.getString("UndoItem.7"); //$NON-NLS-1$
	String REMOVE_CELL_BORDER = Messages.getString("UndoItem.8"); //$NON-NLS-1$
	String CELL_FONT_NAME = Messages.getString("UndoItem.9"); //$NON-NLS-1$
	String CELL_FONT_SIZE = Messages.getString("UndoItem.10"); //$NON-NLS-1$
	String CELL_FONT_STYLE = Messages.getString("UndoItem.11"); //$NON-NLS-1$
	String CELL_AUTOHEIGHT = Messages.getString("UndoItem.25");  //$NON-NLS-1$
	String DEC_DECIMAL = Messages.getString("UndoItem.12"); //$NON-NLS-1$
	String INC_DECIMAL = Messages.getString("UndoItem.13"); //$NON-NLS-1$
	String ROUND_TO_SIGNIFICANT = Messages.getString("UndoItem.rts");
	String UNION_CELLS = Messages.getString("UndoItem.14"); //$NON-NLS-1$
	String CLEAR_UNION_CELLS = Messages.getString("UndoItem.26"); //$NON-NLS-1$
	String COLUMN_MOVED = Messages.getString("UndoItem.15"); //$NON-NLS-1$
	String REMOVE_ROWS = Messages.getString("UndoItem.16"); //$NON-NLS-1$
	String ADD_ROWS = Messages.getString("UndoItem.17"); //$NON-NLS-1$
	String ADD_GROUP = Messages.getString("UndoItem.23"); //$NON-NLS-1$
	String REMOVE_COLUMNS = Messages.getString("UndoItem.18"); //$NON-NLS-1$
	String ADD_COLUMNS = Messages.getString("UndoItem.19"); //$NON-NLS-1$
	String DELETE_ICON = Messages.getString("UndoItem.20"); //$NON-NLS-1$
	String SCALE_ICON = Messages.getString("UndoItem.21"); //$NON-NLS-1$
	String INSERT_ICON = Messages.getString("UndoItem.22"); //$NON-NLS-1$
	String CHANGE_ROWTYPE = Messages.getString("UndoItem.change_row_type") ; //$NON-NLS-1$
	String TOTAL_FUNCTION = Messages.getString("UndoItem.24"); //$NON-NLS-1$
	String NOT_REPEATE = Messages.getString("UndoItem.27"); //$NON-NLS-1$

	
	UndoItem undo();

	void clear();
	
	String getDescription();
}
