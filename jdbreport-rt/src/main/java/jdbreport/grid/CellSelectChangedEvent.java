/*
 * Created on 21.02.2005
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2005-2008 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.grid;

import java.util.EventObject;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 *
 */
public class CellSelectChangedEvent extends EventObject {

	private static final long serialVersionUID = 2960065237545503609L;

	private int row;

	private int column;

	public CellSelectChangedEvent(Object source, int row, int column) {
		super(source);
		this.row = row;
		this.column = column;
	}

	/**
	 * @return Returns the row.
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @return Returns the column.
	 */
	public int getColumn() {
		return column;
	}

}
