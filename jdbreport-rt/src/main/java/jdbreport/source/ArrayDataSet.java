/*
 * ArrayDataSet.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2010 Andrey Kholmanskih. All rights reserved.
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

package jdbreport.source;

import jdbreport.model.ReportException;

/**
 * @version 1.4 15.03.2010
 * @author Andrey Kholmanskih
 * 
 */
public class ArrayDataSet extends ReflectDataSet {

	private Object[] array;
	private int index;

	public ArrayDataSet(String id, Object[] array) {
		super(id);
		this.array = array;
		index = 0;
		if (index < array.length)
			current = array[index];
		reflect(current);
	}

	@Override
	public boolean next() throws ReportException {
		if (index < array.length - 1) {
			current = array[++index];
			return true;
		} else {
			return false;
		}
	}

	public boolean reopen() throws ReportException {
		index = 0;
		current = array[index];
		return array.length > 0;
	}


}