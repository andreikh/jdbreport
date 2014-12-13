/*
 * StyleUndoItem.java
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

package jdbreport.grid.undo;

import jdbreport.grid.JReportGrid;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class StyleUndoItem extends AbstractGridUndo {

	private Object[][] stylesId = null;

	public StyleUndoItem(JReportGrid grid, String descr) {
		super(grid, descr);
		fillStylesId();
	}

	private void fillStylesId() {
		int rowCount = selectedRect.getBottomRow() - selectedRect.getTopRow()
				+ 1;
		int colCount = selectedRect.getRightCol() - selectedRect.getLeftCol()
				+ 1;
		stylesId = new Object[rowCount][colCount];
		for (int r = selectedRect.getTopRow(); r <= selectedRect.getBottomRow(); r++) {
			for (int c = selectedRect.getLeftCol(); c <= selectedRect
					.getRightCol(); c++) {
				stylesId[r - selectedRect.getTopRow()][c
						- selectedRect.getLeftCol()] = getGrid()
						.getReportModel().getReportCell(r, c).getStyleId();
			}

		}
	}

	public void clear() {
		super.clear();
		stylesId = null;
	}

	public UndoItem undo() {
		Object[][] oldStylesId = stylesId;
		fillStylesId();
		for (int r = 0; r < oldStylesId.length; r++) {
			for (int c = 0; c < oldStylesId[r].length; c++) {
				getGrid().getReportModel().getReportCell(
						r + selectedRect.getTopRow(),
						c + selectedRect.getLeftCol()).setStyleId(
						oldStylesId[r][c]);
			}
		}
		return super.undo();
	}

}
