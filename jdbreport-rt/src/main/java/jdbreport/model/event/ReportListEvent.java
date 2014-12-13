/*
 * Created on 01.03.2005
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2005-2014 Andrey Kholmanskih
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
