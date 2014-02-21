/*
 * Created 27.05.2007
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
