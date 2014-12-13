/*
 * Created 27.05.2007
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

import jdbreport.design.grid.TemplateGrid;
import jdbreport.grid.undo.AbstractGridUndo;
import jdbreport.grid.undo.UndoItem;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class FunctionUndoItem extends AbstractGridUndo {

	public static final String CELL_FUNCTION = Messages
			.getString("FunctionUndoItem.0"); //$NON-NLS-1$

	private String[][] functions;

	public FunctionUndoItem(TemplateGrid grid) {
		super(grid, null);
		fillFunctions(grid);
	}

	public void clear() {
		super.clear();
		functions = null;
	}

	public String getDescription() {
		return CELL_FUNCTION;
	}

	public UndoItem undo() {
		String[][] oldFunctions = functions;
		fillFunctions((TemplateGrid) getGrid());
		for (int r = 0; r < oldFunctions.length; r++) {
			for (int c = 0; c < oldFunctions[r].length; c++) {
				((TemplateGrid) getGrid()).getTemplateModel().getCellObject(
						r + selectedRect.getTopRow(),
						c + selectedRect.getLeftCol()).setFunctionName(
						oldFunctions[r][c]);
			}
		}
		return super.undo();
	}

	private void fillFunctions(TemplateGrid grid) {
		int rowCount = selectedRect.getBottomRow() - selectedRect.getTopRow()
				+ 1;
		int colCount = selectedRect.getRightCol() - selectedRect.getLeftCol()
				+ 1;
		functions = new String[rowCount][colCount];
		for (int r = selectedRect.getTopRow(); r <= selectedRect.getBottomRow(); r++) {
			for (int c = selectedRect.getLeftCol(); c <= selectedRect
					.getRightCol(); c++) {
				functions[r - selectedRect.getTopRow()][c
						- selectedRect.getLeftCol()] = grid.getTemplateModel()
						.getCellObject(r, c).getFunctionName();
			}

		}
	}

}
