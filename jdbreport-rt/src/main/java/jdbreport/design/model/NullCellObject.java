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
 * @version 3.1 14.12.2014
 * @author Andrey Kholmanskih
 *
 */
public class NullCellObject extends NullCell implements CellObject {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Cell createCellItem() {
		return null;
	}

	@Override
	public boolean isNotRepeat() {
		return false;
	}

	@Override
	public void setNotRepeat(boolean noRepeat) {
		
	}

	@Override
	public String getFunctionName() {
		return null;
	}

	@Override
	public void setFunctionName(String functionName) {
	}

	@Override
	public int getTotalFunction() {
		return 0;
	}

	@Override
	public void setTotalFunction(int func) {

	}

	@Override
	public boolean isOldEquals(Object value) {
		return false;
	}

	@Override
	public void setOldValue(Object oldValue) {
	}

	@Override
	public Expression[] getExpressions() {
		return null;
	}

	@Override
	public void setExpressions(Expression[] expr) {
		
	}

	@Override
	public String[] getDataSetIds() {
		return null;
	}

	@Override
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
