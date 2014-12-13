/*
 * JDBReport Generator
 * 
 * Copyright (C) 2011-2014 Andrey Kholmanskih
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
