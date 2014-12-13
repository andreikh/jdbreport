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
