/*
 * Created on 22.09.2004
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2014 Andrey Kholmanskih
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
	 * @param draging draging flag
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