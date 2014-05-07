/*
 * JDBReport Designer
 * 
 * Copyright (C) 2006-2011 Andrey Kholmanskih. All rights reserved.
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

import jdbreport.design.grid.undo.DsAliasUndoItem;
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
 * @version 2.0 04.03.2011
 * @author Andrey Kholmanskih
 * 
 */
public class TemplateGrid extends JReportGrid {

	private static final long serialVersionUID = 1L;

	private static Icon functionIcon;

	private static Icon varIcon;

	private static Icon fieldIcon;

	private static Icon totalIcon;

	private transient ReportCellRenderer templateReportRenderer;

	public TemplateGrid(TableModel tm) {
		super(tm);
	}

	protected static Icon getTotalIcon() {
		if (totalIcon == null) {
			totalIcon = TemplateReportResources.getInstance().getIcon(
					"total_ovr.png"); //$NON-NLS-1$
		}
		return totalIcon;
	}

	protected static Icon getFunctionIcon() {
		if (functionIcon == null) {
			functionIcon = TemplateReportResources.getInstance().getIcon(
					"function.gif"); //$NON-NLS-1$
		}
		return functionIcon;
	}

	protected static Icon getVarIcon() {
		if (varIcon == null) {
			varIcon = TemplateReportResources.getInstance().getIcon(
					"vars_ovr.gif"); //$NON-NLS-1$
		}
		return varIcon;
	}

	protected static Icon getFieldIcon() {
		if (fieldIcon == null) {
			fieldIcon = TemplateReportResources.getInstance().getIcon(
					"field_ovr.gif"); //$NON-NLS-1$
		}
		return fieldIcon;
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

	public void setCellField(GridRect selectionRect, boolean isField) {
		int type = isField ? CellObject.TYPE_FIELD : CellObject.TYPE_NONE;
		Iterator<Cell> it = getReportModel().getSelectedCells(selectionRect);
		while (it.hasNext()) {
			((CellObject) it.next()).setType(type);
		}
		repaint();
	}

	/**
	 * Sets the DataSet's alias to the selected cells
	 * 
	 * @param alias
	 *            - the DataSet's alias
	 */
	public void setCellDsAlias(String alias) {
		GridRect r = getSelectionRect();
		if (r == null) {
			return;
		}
		if (alias != null && alias.trim().length() == 0) {
			alias = null;
		}
		int type;
		pushDsAliasUndo();
		Iterator<Cell> it = getReportModel().getSelectedCells(r);
		while (it.hasNext()) {
			CellObject cell = (CellObject) it.next();
			cell.setDataSetId(alias);
			if (alias == null) {
				type = getTemplateModel().getVars().containsKey(cell.getText()) ? CellObject.TYPE_VAR
						: CellObject.TYPE_NONE;
			} else {
				type = CellObject.TYPE_FIELD;
			}
			cell.setType(type);
		}
		repaint();
	}

	private void pushDsAliasUndo() {
		unionUndo(new DsAliasUndoItem(this));
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
		if (!"".equals(result)) //$NON-NLS-1$
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
		boolean notrepeate = !cell.isNotRepeate();
		Iterator<Cell> it = getReportModel().getSelectedCells(selectionRect);
		while (it.hasNext()) {
			((CellObject) it.next()).setNotRepeate(notrepeate);
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
			if (cellObject.getType() == CellObject.TYPE_VAR) {
				insertIcon(TemplateGrid.getVarIcon());
			} else if (cellObject.getType() == CellObject.TYPE_FIELD) {
				insertIcon(TemplateGrid.getFieldIcon());
			}
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
