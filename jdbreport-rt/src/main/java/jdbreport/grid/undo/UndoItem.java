/*
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2012 Andrey Kholmanskih. All rights reserved.
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

/**
 * @version 2.0 18.04.2012
 * @author Andrey Kholmanskih
 * 
 */
public interface UndoItem {

	public static final String RESIZING_COLUMN = Messages.getString("UndoItem.0"); //$NON-NLS-1$
	public static final String RESIZING_ROW = Messages.getString("UndoItem.1"); //$NON-NLS-1$
	public static final String ROW_MOVED = Messages.getString("UndoItem.2"); //$NON-NLS-1$
	public static final String CELL_BACKGROUND = Messages.getString("UndoItem.3"); //$NON-NLS-1$
	public static final String PASTE_CELLS = Messages.getString("UndoItem.4"); //$NON-NLS-1$
	public static final String DELETE_CELLS = Messages.getString("UndoItem.5"); //$NON-NLS-1$
	public static final String CELL_ALIGN = Messages.getString("UndoItem.6"); //$NON-NLS-1$
	public static final String CELL_BORDER = Messages.getString("UndoItem.7"); //$NON-NLS-1$
	public static final String REMOVE_CELL_BORDER = Messages.getString("UndoItem.8"); //$NON-NLS-1$
	public static final String CELL_FONT_NAME = Messages.getString("UndoItem.9"); //$NON-NLS-1$
	public static final String CELL_FONT_SIZE = Messages.getString("UndoItem.10"); //$NON-NLS-1$
	public static final String CELL_FONT_STYLE = Messages.getString("UndoItem.11"); //$NON-NLS-1$
	public static final String CELL_AUTOHEIGHT = Messages.getString("UndoItem.25");  //$NON-NLS-1$
	public static final String DEC_DECIMAL = Messages.getString("UndoItem.12"); //$NON-NLS-1$
	public static final String INC_DECIMAL = Messages.getString("UndoItem.13"); //$NON-NLS-1$
	public static final String UNION_CELLS = Messages.getString("UndoItem.14"); //$NON-NLS-1$
	public static final String CLEAR_UNION_CELLS = Messages.getString("UndoItem.26"); //$NON-NLS-1$
	public static final String COLUMN_MOVED = Messages.getString("UndoItem.15"); //$NON-NLS-1$
	public static final String REMOVE_ROWS = Messages.getString("UndoItem.16"); //$NON-NLS-1$
	public static final String ADD_ROWS = Messages.getString("UndoItem.17"); //$NON-NLS-1$
	public static final String ADD_GROUP = Messages.getString("UndoItem.23"); //$NON-NLS-1$
	public static final String REMOVE_COLUMNS = Messages.getString("UndoItem.18"); //$NON-NLS-1$
	public static final String ADD_COLUMNS = Messages.getString("UndoItem.19"); //$NON-NLS-1$
	public static final String DELETE_ICON = Messages.getString("UndoItem.20"); //$NON-NLS-1$
	public static final String SCALE_ICON = Messages.getString("UndoItem.21"); //$NON-NLS-1$
	public static final String INSERT_ICON = Messages.getString("UndoItem.22"); //$NON-NLS-1$
	public static final String CHANGE_ROWTYPE = Messages.getString("UndoItem.change_row_type") ; //$NON-NLS-1$
	public static final String TOTAL_FUNCTION = Messages.getString("UndoItem.24"); //$NON-NLS-1$
	public static final String NOT_REPEATE = Messages.getString("UndoItem.27"); //$NON-NLS-1$

	
	UndoItem undo();

	void clear();
	
	String getDescription();
}
