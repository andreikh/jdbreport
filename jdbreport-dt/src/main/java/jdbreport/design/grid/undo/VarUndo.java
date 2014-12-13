/*
 * VarUndo.java
 *
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

import jdbreport.design.grid.VarList;
import jdbreport.grid.undo.UndoItem;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class VarUndo implements UndoItem {

	protected Object oldValue;
	protected Object newValue;
	protected VarList varList;

	public VarUndo(VarList varList, Object oldValue, Object newValue) {
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.varList = varList;
	}

	public void clear() {
	}

	public String getDescription() {
		return Messages.getString("VarUndo.0"); //$NON-NLS-1$
	}

	public UndoItem undo() {
		Object value = null;
		if (newValue != null) {
			value = varList.getVars().get(newValue);
			varList.getVars().remove(newValue);
		}
		if (oldValue != null) {
			varList.getVars().put(oldValue, value);
		}
		Object tmp = oldValue;
		oldValue = newValue;
		newValue = tmp;
		varList.updateVarList();
		varList.setVisible(true);
		return this;
	}

}
