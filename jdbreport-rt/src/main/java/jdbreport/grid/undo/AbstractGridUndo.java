/*
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2008 Andrey Kholmanskih. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, write to the 
 *
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
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
