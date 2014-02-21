/*
 * Created on 01.03.2005
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
package jdbreport.model.event;

import java.util.EventObject;

import jdbreport.model.ReportBook;

/**
 * @version 1.1 03/09/08
 * 
 * @author Andrey Kholmanskih
 * 
 */
public class ReportListEvent extends EventObject {

	private static final long serialVersionUID = -7884866448852224538L;

	/** The index of the report from where it was moved or removed */
	protected int fromIndex;

	/** The index of the report to where it was moved or added from */
	protected int toIndex;

	public ReportListEvent(ReportBook source, int from, int to) {
		super(source);
		fromIndex = from;
		toIndex = to;
	}

	/** Returns the fromIndex. Valid for removed or moved events */
	public int getFromIndex() {
		return fromIndex;
	}

	/** Returns the toIndex. Valid for add and moved events */
	public int getToIndex() {
		return toIndex;
	}

}
