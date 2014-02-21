/*
 * ObjectDataSet.java 30.10.2006
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
public class ObjectDataSet extends ReflectDataSet {

	public ObjectDataSet(Object object) {
		this(object.getClass().getName(), object);
	}

	public ObjectDataSet(String id, Object object) {
		super(id);
		this.current = object;
		reflect(object);
	}

	/**
	 * Does nothing
	 */
	public boolean reopen() throws ReportException {
		return true;
	}

}
