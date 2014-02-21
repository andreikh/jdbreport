/*
 * Created on 22.09.2004
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2008 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.model.event;

import jdbreport.model.TableRowModel;

/**
 * @version 1.1 03/09/08
 * 
 * @author Andrey Kholmanskih
 * 
 */
public class TableRowModelEvent extends java.util.EventObject {
	//
	// Instance Variables
	//

	private static final long serialVersionUID = -4136129857601995808L;

	/** The index of the row from where it was moved or removed */
	protected int fromIndex;

	/** The index of the row to where it was moved or added from */
	protected int toIndex;

	private boolean draging;

	//
	// Constructors
	//

	/**
	 * Constructs a TableRowModelEvent object.
	 * 
	 * @param source
	 *            the TableRowModel that originated the event (typically
	 *            <code>this</code>)
	 * @param from
	 *            an int specifying the first row in a range of affected rows
	 * @param to
	 *            an int specifying the last row in a range of affected rows
	 */
	public TableRowModelEvent(TableRowModel source, int from, int to) {
		this(source, from, to, false);
	}

	/**
	 * Constructs a TableRowModelEvent object.
	 * 
	 * @param source
	 *            the TableRowModel that originated the event (typically
	 *            <code>this</code>)
	 * @param from
	 *            an int specifying the first row in a range of affected rows
	 * @param to
	 *            an int specifying the last row in a range of affected rows
	 * @param draging
	 * 
	 */
	public TableRowModelEvent(TableRowModel source, int from, int to,
			boolean draging) {
		super(source);
		fromIndex = from;
		toIndex = to;
		this.draging = draging;
	}

	/** Returns the fromIndex. Valid for removed or moved events */
	public int getFromIndex() {
		return fromIndex;
	}

	/** Returns the toIndex. Valid for add and moved events */
	public int getToIndex() {
		return toIndex;
	}

	public boolean isDraging() {
		return draging;
	}
}