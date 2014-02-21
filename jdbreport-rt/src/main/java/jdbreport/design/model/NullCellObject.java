/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2012 Andrey Kholmanskih. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, write to the 
 *
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
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
