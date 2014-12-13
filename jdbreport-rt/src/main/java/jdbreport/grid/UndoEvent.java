/*
 * UndoEvent.java
 *
 * JDBReport Generator
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

package jdbreport.grid;

import java.util.EventObject;

import jdbreport.grid.undo.UndoItem;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 *
 */
public class UndoEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	
	private UndoItem undoItem;

	public UndoEvent(Object source, UndoItem undoItem) {
		super(source);
		this.undoItem = undoItem;
	}

	/**
	 * @return the undoItem
	 */
	public UndoItem getUndoItem() {
		return undoItem;
	}

	/**
	 * @param undoItem the undoItem to set
	 */
	public void setUndoItem(UndoItem undoItem) {
		this.undoItem = undoItem;
	}

}
