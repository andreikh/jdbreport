/*
 * ObjectDataSet.java 30.10.2006
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
