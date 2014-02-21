/*
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
 * 
 */
package jdbreport.source;

import java.util.Iterator;

import jdbreport.model.ReportException;


/**
 * @version 1.4 15.03.2010
 * @author Andrey Kholmanskih
 * 
 */
public class IteratorDataSet extends ReflectDataSet {

	private Iterator<?> it;


	public IteratorDataSet(String id, Iterator<?> it) {
		super(id);
		this.it = it;
		setIterator(it);
		reflect(current);
	}

	@Override
	public boolean next() throws ReportException {
		if (it.hasNext()) {
			current = it.next();
			return true;
		} else {
			return false;
		}
	}


	protected void setIterator(Iterator<?> it) {
		this.it = it;
		if (it.hasNext()) 
			current = it.next();
		else
			current = null;
	}

	/**
	 * Does nothing
	 */
	public boolean reopen() throws ReportException {
		return current != null;
	}

}
