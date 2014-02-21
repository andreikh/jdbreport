/*
 * JDBReport Designer
 * 
 * Copyright (C) 2007-2011 Andrey Kholmanskih. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, write to the 
 * 
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
 */

package jdbreport.design.grid.undo;

import java.util.Map;

import jdbreport.design.model.CellFunctionObject;
import jdbreport.grid.undo.UndoItem;

/**
 * @version 2.0 21.01.2011
 * @author Andrey Kholmanskih
 * 
 */
public class FunctionUndo implements UndoItem {

	protected CellFunctionObject oldFunction;
	protected CellFunctionObject newFunction;
	private Map<String, CellFunctionObject> functionList;

	public FunctionUndo(Map<String, CellFunctionObject> functionList,
			CellFunctionObject oldValue, CellFunctionObject newValue) {
		this.oldFunction = oldValue;
		this.newFunction = newValue;
		this.functionList = functionList;
	}

	public void clear() {
	}

	public String getDescription() {
		return Messages.getString("FunctionUndo.0"); //$NON-NLS-1$
	}

	public UndoItem undo() {
		if (newFunction != null) {
			functionList.remove(newFunction.getFunctionName());
		}
		if (oldFunction != null) {
			functionList.put(oldFunction.getFunctionName(), oldFunction);
		}
		CellFunctionObject tmp = oldFunction;
		oldFunction = newFunction;
		newFunction = tmp;
		return this;
	}

}
