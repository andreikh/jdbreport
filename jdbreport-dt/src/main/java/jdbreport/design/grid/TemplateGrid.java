/*
 * JDBReport Designer
 * 
 * Copyright (C) 2006-2011 Andrey Kholmanskih
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
package jdbreport.design.grid;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import jdbreport.design.grid.undo.FunctionUndoItem;
import jdbreport.design.grid.undo.TemplateGridHandler;
import jdbreport.design.grid.undo.TemplateGridParser;
import jdbreport.design.model.CellObject;
import jdbreport.design.model.TemplateModel;
import jdbreport.design.view.clipboard.TemplateClipboardParser;
import jdbreport.design.view.clipboard.TemplateFragmentHandler;
import jdbreport.grid.CellPropertiesDlg;
import jdbreport.grid.JReportGrid;
import jdbreport.grid.ReportCellRenderer;
import jdbreport.grid.RowHeader;
import jdbreport.grid.undo.BackupItem;
import jdbreport.grid.undo.CellUndoItem;
import jdbreport.grid.undo.GridParser;
import jdbreport.grid.undo.UndoItem;
import jdbreport.model.Cell;
import jdbreport.model.GridRect;
import jdbreport.model.TableRow;
import jdbreport.view.clipboard.ClipboardParser;
import jdbreport.util.Utils;

/**
 * @version 3.1 14.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class TemplateGrid extends JReportGrid {

	private static final long serialVersionUID = 1L;

	private static Icon functionIcon;

	private static Icon totalIcon;

	private transient ReportCellRenderer templateReportRenderer;

	public TemplateGrid(TableModel tm) {
		super(tm);
	}

	protected static Icon getTotalIcon() {
		if (totalIcon == null) {
			totalIcon = TemplateReportResources.getInstance().getIcon(
					"total_ovr.png");
		}
		return totalIcon;
	}

	protected static Icon getFunctionIcon() {
		if (functionIcon == null) {
			functionIcon = TemplateReportResources.getInstance().getIcon(
					"function.gif");
		}
		return functionIcon;
	}

	/**
	 * Sets the function's name for the selected cells
	 * 
	 * @param functionName
	 *            - the function name
	 */
	public void setFunctionName(String functionName) {
		pushUndo(new FunctionUndoItem(this));
		getTemplateModel().setFunctionName(getSelectionRect(), functionName);
		repaint();
	}

	/**
	 * 
	 * @return the TemplateModel
	 */
	public TemplateModel getTemplateModel() {
		return (TemplateModel) getReportModel();
	}

	protected boolean canEdit(Cell cell) {
		return true;
	}

	/**
	 * 
	 * @return the first selected CellObject
	 */
	public CellObject getSelectedCellObject() {
		return getTemplateModel().getCellObject(getSelectedRow(),
				getSelectedColumn());
	}

	protected void createDefaultRenderers() {
		super.createDefaultRenderers();
		setLazyRenderer(Object.class,
				"jdbreport.design.grid.TemplateGrid$TemplateReportRenderer"); //$NON-NLS-1$
	}

	protected RowHeader createDefaultRowHeader() {
		return new TemplateRowHeader(this);
	}

	protected JTableHeader createDefaultTableHeader() {
		return new TemplateHeader(columnModel);
	}

	public ReportCellRenderer getTextReportRenderer() {
		if (templateReportRenderer == null) {
			templateReportRenderer = new TemplateReportRenderer();
		}
		return templateReportRenderer;
	}

	/**
	 * Adds rows' count of the defined type to the index of the model. The new
	 * rows will contain null values. Notification of the row being added will
	 * be generated.
	 * 
	 * @param count
	 *            - rows' count
	 * @param index
	 *            - the row index of the rows to be inserted
	 * @param rowType
	 *            - the row's type
	 */
	public void addRows(int count, int index, int rowType) {
		try {
			pushUndo(new BackupItem(this, UndoItem.ADD_ROWS));
		} catch (Throwable e) {
			Utils.showError(e);
		}
		int row = getTemplateModel().addRows(count, index, rowType);
		if (row >= 0)
			setSelectedRect(new GridRect(row, 0, row + count - 1,
					getColumnCount() - 1));
		else
			getSelectionModel().clearSelection();
	}

	/**
	 * Sets type of the selected rows
	 * 
	 * @param rows
	 *            - the selected rows
	 * @param rowType
	 *            - the row's type
	 */
	public void setRowType(int[] rows, int rowType) {
		if (rows.length == 0) {
			return;
		}
		try {
			pushUndo(new BackupItem(this, UndoItem.CHANGE_ROWTYPE));
		} catch (Throwable e) {
			Utils.showError(e);
		}
		getTemplateModel().setRowType(addSpanedRows(rows), rowType);
		getRowHeader().repaint();
		repaint();
	}

	private int[] addSpanedRows(int[] rows) {
		int l = rows.length;
		for (int i = 0; i < rows.length; i++) {
			TableRow tableRow = getReportModel().getRowModel().getRow(rows[i]);
			for (Cell cell : tableRow) {
				if (cell.isSpan() && i + cell.getRowSpan() >= rows.length) {
					l = i + cell.getRowSpan() + 1;
				}
			}
		}
		if (l > rows.length) {
			int[] newrows = new int[l];
			System.arraycopy(rows, 0, newrows, 0, rows.length);
			for (int i = rows.length; i < newrows.length; i++) {
				newrows[i] = newrows[i - 1] + 1;
			}
			return newrows;
		}
		return rows;
	}

	/**
	 * Inserts detail group
	 * 
	 */
	public void insertDetailGroup() {
		try {
			pushUndo(new BackupItem(this, UndoItem.ADD_GROUP));
		} catch (Throwable e) {
			Utils.showError(e);
		}
		int row = getTemplateModel().insertDetailGroup(getSelectedRow());
		if (row >= 0) {
			setSelectedRect(new GridRect(row, 0, row + 2, getColumnCount() - 1));
		} else {
			getSelectionModel().clearSelection();
		}
	}

	/**
	 * Sets total functions for the selected cells between CellObject.AF_NONE
	 * and CellObject.AF_AVG
	 * 
	 * @param kind
	 *            - the total functions kind
	 */
	public void setAgrFunc(int kind) {
		GridRect selectionRect = getSelectionRect();
		if (selectionRect == null)
			return;
		pushUndo(new CellUndoItem(this, UndoItem.TOTAL_FUNCTION));
		CellObject cell = (CellObject) getSelectedCell();
		if (cell.getTotalFunction() == kind)
			kind = CellObject.AF_NONE;
		Iterator<Cell> it = getReportModel().getSelectedCells(selectionRect);
		while (it.hasNext()) {
			((CellObject) it.next()).setTotalFunction(kind);
		}
		repaint();
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		int row = getReportModel().getRowModel().getRowIndexAtY(
				event.getPoint().y);
		int column = getColumnModel().getColumnIndexAtX(event.getPoint().x);
		String result = getReportModel().getToolTipText(row, column);
		if (!"".equals(result))
			return result;
		return super.getToolTipText(event);
	}

	protected ClipboardParser createClipboardWriter() {
		return new TemplateClipboardParser();
	}

	protected DefaultHandler createPasteHandler(XMLReader reader,
			int selectRow, int selectCol) {
		return new TemplateFragmentHandler(getTemplateModel(), reader,
				selectRow, selectCol);
	}

	public GridParser createGridWriter() {
		return new TemplateGridParser();
	}

	public DefaultHandler createGridHandler(XMLReader reader) {
		return new TemplateGridHandler(getReportModel(), reader);
	}

	public void setNotRepeate() {
		GridRect selectionRect = getSelectionRect();
		if (selectionRect == null)
			return;
		pushUndo(new CellUndoItem(this, UndoItem.NOT_REPEATE));
		CellObject cell = (CellObject) getSelectedCell();
		boolean notrepeate = !cell.isNotRepeat();
		Iterator<Cell> it = getReportModel().getSelectedCells(selectionRect);
		while (it.hasNext()) {
			((CellObject) it.next()).setNotRepeat(notrepeate);
		}
	}

	@Override
	protected CellPropertiesDlg createCellProperties() throws HeadlessException {
		Window w = SwingUtilities.getWindowAncestor(this);
		if (w instanceof Frame) {
			return new TemplCellPropertiesDlg((Frame) w, this);
		} else {
			return new TemplCellPropertiesDlg((Dialog) w, this);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getReportModel()) {
			if (evt.getPropertyName().equals("colSizing")) { //$NON-NLS-1$
				return;
			} else if (evt.getPropertyName().equals("colMoving")) { //$NON-NLS-1$
				return;
			}
		}
		super.propertyChange(evt);
	}

	protected static class TemplateReportRenderer extends TextReportRenderer {

		private static final long serialVersionUID = 1L;

		public TemplateReportRenderer() {
			super();
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			super.getTableCellRendererComponent(table, value, isSelected,
					hasFocus, row, column);
			CellObject cellObject = (CellObject) cell;
			setSelectionStart(0);
			setSelectionEnd(0);
			if (cellObject.getFunctionName() != null) {
				insertIcon(TemplateGrid.getFunctionIcon());
			}
			if (cellObject.getTotalFunction() != CellObject.AF_NONE) {
				insertIcon(TemplateGrid.getTotalIcon());
			}
			return this;
		}

	}

}
