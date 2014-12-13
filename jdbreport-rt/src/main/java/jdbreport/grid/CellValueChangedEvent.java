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
package jdbreport.grid;

import java.util.EventObject;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class CellValueChangedEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	private Object oldValue;
	private int row;
	private int column;

	public CellValueChangedEvent(Object source, Object oldValue, int row,
			int column) {
		super(source);
		this.oldValue = oldValue;
		this.row = row;
		this.column = column;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}
}
