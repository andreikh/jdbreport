/*
 * Created on 21.02.2005
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
package jdbreport.grid;

import java.util.EventObject;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 *
 */
public class CellSelectChangedEvent extends EventObject {

	private static final long serialVersionUID = 2960065237545503609L;

	private int row;

	private int column;

	public CellSelectChangedEvent(Object source, int row, int column) {
		super(source);
		this.row = row;
		this.column = column;
	}

	/**
	 * @return Returns the row.
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @return Returns the column.
	 */
	public int getColumn() {
		return column;
	}

}
