/*
 * Created on 15.03.2005
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
package jdbreport.design.model;

import jdbreport.model.Cell;
import jdbreport.model.ReportRow;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 *
 */
public class TemplateRow extends ReportRow {
	
	static final CellObject nullCellObject = new NullCellObject();
	
	
	public TemplateRow(int colcount) {
		super(colcount);
	}
	

	protected Cell getNullCell() {
		return nullCellObject;
	}
	

	/**
	 * Returns the CellObject by column
	 * @param column - the column's number
	 * @return the CellObject
	 */
	public CellObject getCellObject(int column) {
		if (column >= 0 && column < colList.size())
			return (CellObject)colList.get(column);
		return nullCellObject;
	}

	protected Cell createDefaultCell() {
		return new TemplateReportCell();
	}

	
}
