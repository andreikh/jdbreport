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
 * 
 */
package jdbreport.design.grid.undo;

import jdbreport.design.grid.TemplateGrid;
import jdbreport.design.model.CellObject;
import jdbreport.grid.undo.AbstractGridUndo;
import jdbreport.grid.undo.UndoItem;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 */
public class DsAliasUndoItem extends AbstractGridUndo {

	public static final String DATA_SET_ALIAS = Messages
			.getString("DsAliasUndoItem.0"); //$NON-NLS-1$

	private String[][] dsAlias;

	public DsAliasUndoItem(TemplateGrid grid) {
		super(grid, null);
		fillAlias();
	}

	public void clear() {
		super.clear();
		dsAlias = null;
	}

	public String getDescription() {
		return DATA_SET_ALIAS;
	}

	public UndoItem undo() {
		String[][] oldAlias = dsAlias;
		fillAlias();
		for (int r = 0; r < oldAlias.length; r++) {
			for (int c = 0; c < oldAlias[r].length; c++) {
				String alias = oldAlias[r][c];
				int type;
				CellObject cell = ((TemplateGrid) getGrid()).getTemplateModel()
						.getCellObject(r + selectedRect.getTopRow(),
								c + selectedRect.getLeftCol());
				cell.setDataSetId(alias);
				if (alias == null) {
					type = ((TemplateGrid) getGrid()).getTemplateModel()
							.getVars().containsKey(cell.getText()) ? CellObject.TYPE_VAR
							: CellObject.TYPE_NONE;
				} else
					type = CellObject.TYPE_FIELD;
				cell.setType(type);
			}
		}
		return super.undo();
	}

	private void fillAlias() {
		int rowCount = selectedRect.getBottomRow() - selectedRect.getTopRow()
				+ 1;
		int colCount = selectedRect.getRightCol() - selectedRect.getLeftCol()
				+ 1;
		dsAlias = new String[rowCount][colCount];
		for (int r = selectedRect.getTopRow(); r <= selectedRect.getBottomRow(); r++) {
			for (int c = selectedRect.getLeftCol(); c <= selectedRect
					.getRightCol(); c++) {
				dsAlias[r - selectedRect.getTopRow()][c
						- selectedRect.getLeftCol()] = ((TemplateGrid) getGrid())
						.getTemplateModel().getCellObject(r, c).getDataSetId();
			}

		}
	}

}
