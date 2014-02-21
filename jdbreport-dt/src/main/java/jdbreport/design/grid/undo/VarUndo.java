/*
 * VarUndo.java
 *
 * JDBReport Designer
 * 
 * Copyright (C) 2007-2008 Andrey Kholmanskih. All rights reserved.
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
