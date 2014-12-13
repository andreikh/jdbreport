/*
 * NullCellEditor.java
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

import java.awt.Component;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class NullCellEditor extends AbstractCellEditor implements
		TableCellEditor {

	private static final long serialVersionUID = 1L;

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		return null;
	}

	public void addCellEditorListener(CellEditorListener l) {
	}

	public void cancelCellEditing() {
	}

	public Object getCellEditorValue() {
		return null;
	}

	public boolean isCellEditable(EventObject anEvent) {
		return false;
	}

	public void removeCellEditorListener(CellEditorListener l) {
	}

	public boolean shouldSelectCell(EventObject anEvent) {
		return false;
	}

	public boolean stopCellEditing() {
		return false;
	}

}
