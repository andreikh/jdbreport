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
package jdbreport.design.model;

import jdbreport.model.Cell;
import jdbreport.model.NullCell;

/**
 * @version 2.0 30.01.2012
 * @author Andrey Kholmanskih
 *
 */
public class NullCellObject extends NullCell implements CellObject {
	
	private static final long serialVersionUID = 1L;


	public NullCellObject() {
		super();
	}

	public String getFieldName() {
		return null;
	}

	public void setFieldName(String name) {

	}

	public String getDataSetId() {
		return null;
	}

	public void setDataSetId(String tableId) {

	}

	public int getType() {
		return 0;
	}

	public void setType(int type) {

	}

	public Cell createCellItem() {
		return null;
	}

	public boolean isNotRepeate() {
		return false;
	}

	public void setNotRepeate(boolean noRepeate) {
		
	}

	public String getFunctionName() {
		return null;
	}

	public void setFunctionName(String functionName) {
	}

	public boolean isOldEquals(Object value) {
		return false;
	}

	public void setOldValue(Object oldValue) {
	}

	public Expression[] getExpressions() {
		return null;
	}

	public void setExpressions(Expression[] expr) {
		
	}

	public String[] getDataSetIds() {
		return null;
	}

	public String[] getFieldNames(String dsId) {
		return null;
	}

	@Override
	public boolean isReplacement() {
		return false;
	}

	@Override
	public void setReplacement(boolean b) {
	}


}
