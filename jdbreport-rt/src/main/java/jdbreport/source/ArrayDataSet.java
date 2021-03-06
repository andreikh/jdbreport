/*
 * ArrayDataSet.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2014 Andrey Kholmanskih
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
