/*
 * JDBReport Designer
 * 
 * Copyright (C) 2007-2014 Andrey Kholmanskih
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
