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

import java.util.logging.Logger;

import jdbreport.model.ReportException;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public abstract class AbstractDataSet implements ReportDataSet {

	protected static final Logger logger = Logger.getLogger(AbstractDataSet.class
			.getName());

	private String id;

	protected AbstractDataSet() {
	}

	public AbstractDataSet(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public boolean next() throws ReportException {
		return false;
	}

	public DataSetParams getParams() throws ReportException {
		return null;
	}

	public String getMasterId() {
		return null;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
