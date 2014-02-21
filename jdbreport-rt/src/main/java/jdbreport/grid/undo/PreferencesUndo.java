/*
 * PreferencesUndo.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2012 Andrey Kholmanskih. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;

import jdbreport.design.model.ReplaceItem;
import jdbreport.design.model.TemplateBook;
import jdbreport.grid.JReportGrid;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportModel;

/**
 * @version 2.0 15.02.2012
 * @author Andrey Kholmanskih
 * 
 */
public class PreferencesUndo extends AbstractGridUndo {

	private ReportBook book;

	private static class Preferences {
		private String reportTitle;
		private String reportCaption;
		private boolean showGrid;
		private boolean canUpdatePages;
		private boolean colSizing;
		private boolean rowSizing;
		private boolean colMoving;
		private boolean rowMoving;
		private boolean editable;
		private boolean visible;
		private List<ReplaceItem> replList;

		public Preferences(ReportModel reportModel, ReportBook book) {
			reportTitle = reportModel.getReportTitle();
			reportCaption = book.getReportCaption();
			showGrid = book.isShowGrid();
			canUpdatePages = reportModel.isCanUpdatePages();
			colSizing = reportModel.isColSizing();
			rowSizing = reportModel.isRowSizing();
			colMoving = reportModel.isColMoving();
			rowMoving = reportModel.isRowMoving();
			editable = reportModel.isEditable();
			visible = reportModel.isVisible();
			if (book instanceof TemplateBook) {
				List<ReplaceItem> list = ((TemplateBook)book).getReplacePatterns();
				replList = copyReplaceList(list);
			}
		}
		
		public List<ReplaceItem> copyReplaceList(List<ReplaceItem> list) {
			if (list != null && list.size() > 0) {
				ArrayList<ReplaceItem> replList = new ArrayList<ReplaceItem>();
				for (ReplaceItem item : list) {
					replList.add(item.clone());
				}
				return replList;
			}
			return null;
		}
		
	}

	
	private Preferences preferences;

	public PreferencesUndo(JReportGrid grid, ReportBook book) {
		super(grid, Messages.getString("PreferencesUndo.0")); //$NON-NLS-1$
		this.book = book;
		preferences = new Preferences(getGrid().getReportModel(), book);
	}

	public UndoItem undo() {
		Preferences old = preferences;
		preferences = new Preferences(getGrid().getReportModel(), book);

		ReportModel reportModel = getGrid().getReportModel();
		book.setShowGrid(old.showGrid);
		reportModel.setReportTitle(old.reportTitle);
		reportModel.setCanUpdatePages(old.canUpdatePages);
		reportModel.setRowSizing(old.rowSizing);
		reportModel.setColSizing(old.colSizing);
		reportModel.setRowMoving(old.rowMoving);
		reportModel.setColMoving(old.colMoving);
		reportModel.setEditable(old.editable);
		reportModel.setVisible(old.visible);
		book.setReportCaption(old.reportCaption);
		if (book  instanceof TemplateBook) {
			((TemplateBook)book).clearReplacePatterns();
			if (old.replList != null) {
				for (ReplaceItem item : old.replList) {
					((TemplateBook)book).addReplacePattern(item.getRegex(), item.getReplacement());
				}
			}
		}
		return super.undo();
	}

	public void clear() {
		book = null;
		preferences = null;
		super.clear();
	}

}
