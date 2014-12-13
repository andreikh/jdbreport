/*
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
import jdbreport.model.GridRect;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public abstract class AbstractGridUndo implements UndoItem {

	protected GridRect selectedRect;
	private boolean hasUndo = false;
	private JReportGrid grid;
	private String descr;

	public AbstractGridUndo(JReportGrid grid, String descr) {
		this.grid = grid;
		this.descr = descr;
		initSelectedGrid(grid);
	}

	protected void initSelectedGrid(JReportGrid grid) {
		selectedRect = grid.getSelectionRect();
	}

	public UndoItem undo() {
		hasUndo = true;
		selectRect();
		getGrid().repaint();
		return this;
	}

	protected void selectRect() {
		if (selectedRect != null)
			grid.setSelectedRect(selectedRect);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AbstractGridUndo other = (AbstractGridUndo) obj;
		if (this.grid != other.grid)
			return false;
		if (selectedRect == null) {
			if (other.selectedRect != null)
				return false;
		} else if (!selectedRect.equals(other.selectedRect))
			return false;
		if (hasUndo != other.hasUndo)
			return false;
		return true;
	}

	public JReportGrid getGrid() {
		return grid;
	}

	public String getDescription() {
		return descr;
	}

	public void clear() {
		grid = null;
		descr = null;
	}
}
