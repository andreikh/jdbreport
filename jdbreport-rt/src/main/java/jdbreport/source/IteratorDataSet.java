/*
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

import java.util.Iterator;

import jdbreport.model.ReportException;


/**
 * @version 3.1 15.12.2014
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
