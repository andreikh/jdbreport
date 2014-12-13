/*
 * PreferencesUndo.java
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
