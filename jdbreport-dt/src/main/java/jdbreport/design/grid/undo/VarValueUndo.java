/*
 * VarValueUndo.java
 *
 * JDBReport Designer
 * 
 * Copyright (C) 2007-20014 Andrey Kholmanskih
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

import jdbreport.design.grid.VarList;
import jdbreport.grid.undo.UndoItem;

/**
 * @version 1.0 17.07.2007
 * @author Andrey Kholmanskih
 * 
 */
public class VarValueUndo implements UndoItem {

	protected Object oldValue;
	protected Object newValue;
	protected VarList varList;
	private Object var;

	public VarValueUndo(VarList varList, Object var, Object oldValue) {
		this.varList = varList;
		this.var = var;
		this.oldValue = oldValue;
		this.newValue = varList.getVars().get(var);
	}

	public String getDescription() {
		return Messages.getString("VarValueUndo.0"); //$NON-NLS-1$
	}

	public UndoItem undo() {
		varList.getVars().put(var, oldValue);
		Object tmp = oldValue;
		oldValue = newValue;
		newValue = tmp;
		varList.updateVarList();
		varList.setVisible(true);
		return this;
	}

	public void clear() {
	}

}
