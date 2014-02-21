/*
 * JDBReport Generator
 * 
 * Copyright (C) 2011 Andrey Kholmanskih. All rights reserved.
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

import java.util.Collection;
import java.util.Map;

import jdbreport.model.ReportException;

/**
 * @author Andrey Kholmanskih
 *
 * @version	1.0 07.02.2011
 */
public class MapDataSet extends AbstractDataSet {

	private Map<String, Object> map;

	public MapDataSet(String id, Map<String, Object> map) {
		super(id);
		this.map = map;
	}

	public Object getValue(String name) throws ReportException {
		return map.get(name);
	}

	public Collection<String> getColumnNames() throws ReportException {
		return map.keySet();
	}

	public Object getCurrentObject() {
		return map;
	}

	public boolean reopen() throws ReportException {
		return true;
	}

	public boolean hasNext() {
		return false;
	}

}
