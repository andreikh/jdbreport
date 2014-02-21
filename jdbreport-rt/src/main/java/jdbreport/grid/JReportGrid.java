/*
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2012 Andrey Kholmanskih. All rights reserved.
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

package jdbreport.grid;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.RenderedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.MediaName;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.*;
import javax.swing.UIDefaults.ProxyLazyValue;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.UIResource;
import javax.swing.table.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import jdbreport.grid.undo.BackupItem;
import jdbreport.grid.undo.CellUndoItem;
import jdbreport.grid.undo.ColumnBreakUndoItem;
import jdbreport.grid.undo.RowBreakUndoItem;
import jdbreport.grid.undo.GridHandler;
import jdbreport.grid.undo.ResizingColumnUndoItem;
import jdbreport.grid.undo.ResizingRowUndoItem;
import jdbreport.grid.undo.RowMovedUndoItem;
import jdbreport.grid.undo.SetValueItem;
import jdbreport.grid.undo.StyleUndoItem;
import jdbreport.grid.undo.UndoItem;
import jdbreport.grid.undo.GridParser;
import jdbreport.model.Cell;
import jdbreport.model.CellStyle;
import jdbreport.model.CellValueInfo;
import jdbreport.model.CellWrap;
import jdbreport.model.GridRect;
import jdbreport.model.Group;
import jdbreport.model.HeighCalculator;
import jdbreport.model.PageCount;
import jdbreport.model.PageNumber;
import jdbreport.model.Picture;
import jdbreport.model.PictureFactory;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportCell;
import jdbreport.model.ReportColumnModel;
import jdbreport.model.ReportColumnModelListener;
import jdbreport.model.ReportModel;
import jdbreport.model.StringMetrics;
import jdbreport.model.TableRow;
import jdbreport.model.TableRowModel;
import jdbreport.model.Units;
import jdbreport.model.clipboard.ClipboardParser;
import jdbreport.model.clipboard.FragmentHandler;
import jdbreport.model.clipboard.ReportTransferable;
import jdbreport.model.event.CellSelectListener;
import jdbreport.model.event.CellValueChangeListener;
import jdbreport.model.event.TableRowModelEvent;
import jdbreport.model.event.TableRowModelListener;
import jdbreport.model.io.SaveReportException;
import jdbreport.model.print.ReportPage;
import jdbreport.model.svg.SVGImage;
import jdbreport.model.svg.SVGValue;
import jdbreport.util.GraphicUtil;
import jdbreport.util.Utils;
import jdbreport.view.ReportPane;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import and.finder.FindParams;
import and.finder.Finder;
import and.swing.ExtensionFileFilter;
import and.swing.ImagePreview;
import and.util.Utilities;

/**
 * @version 2.1 16.05.2012
 * 
 * @author Andrey Kholmanskih
 * 
 */
public class JReportGrid extends JTable implements TableRowModelListener,
		PropertyChangeListener, HeighCalculator, ReportColumnModelListener,
		Finder {

	private static final long serialVersionUID = 1265975931384658496L;

	private static final String uiClassID = "ReportUI"; //$NON-NLS-1$

	static final int PAINT = 0;

	static final int PRINT = 1;

	public static final int ROW_MARGIN = 2;

	static {
		UIManager.put(uiClassID, "jdbreport.grid.BasicReportUI"); //$NON-NLS-1$
	}

	private static final Logger logger = Logger.getLogger(JReportGrid.class
			.getName());

	protected ReportCellRenderer htmlReportRenderer;

	protected ReportCellRenderer textReportRenderer;

	private TableRowModel rowModel;

	private RowHeader rowHeader;

	private boolean rowSelectionAdjusting;

	protected UndoListener undoListener;

	private CellEditorPanel cellEditorPanel;

	private CellPropertiesDlg cellPropertyDialog;

	private boolean showPrintDialog = true;

	private int state;

	private TableCellEditor htmlEditor;

	private Finder finder;

	public JReportGrid() {
		super();
		setBackground(CellStyle.getDefaultStyle().getBackground());
	}

	public JReportGrid(TableModel tm) {
		super(tm, ((ReportModel) tm).getColumnModel());
		setRowSelectionAllowed(true);
		setColumnSelectionAllowed(true);
		autoResizeMode = AUTO_RESIZE_OFF;
		setBackground(CellStyle.getDefaultStyle().getBackground());
		setSize(100, 20);
	}

	public void addAction(Action action) {
		Object name = action.getValue(Action.ACTION_COMMAND_KEY);
		getActionMap().put(name, action);
		getInputMap().put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY),
				name);
	}

	public void removeAction(Action action) {
		Object name = action.getValue(Action.ACTION_COMMAND_KEY);
		getActionMap().remove(name);
		getInputMap().remove(
				(KeyStroke) action.getValue(Action.ACCELERATOR_KEY));
	}

	public int getRowMargin() {
		return ROW_MARGIN;
	}

	//
	// Implementing TableRowModelListener interface
	//
	public void rowUpdated(TableRowModelEvent e) {
		if (isEditing()) {
			removeEditor();
		}
		updatePages(Math.min(e.getFromIndex(), e.getToIndex()));
		resizeAndRepaint();
	}

	public void rowAdded(TableRowModelEvent e) {
		rowUpdated(e);
	}

	/** Tells listeners that a row was removed from the model. */
	public void rowRemoved(TableRowModelEvent e) {
		rowUpdated(e);
	}

	/** Tells listeners that a row was repositioned. */
	public void rowMoved(TableRowModelEvent e) {
		if (isEditing()) {
			cellEditor.stopCellEditing();
		}
		if (e.getFromIndex() != e.getToIndex() && !e.isDraging()) {
			updatePages(Math.min(e.getFromIndex(), e.getToIndex()));
		}
		repaint();
	}

	/** Tells listeners that a row was resized. */
	public void rowResized(TableRowModelEvent e) {
		if (!e.isDraging()) {
			updatePages(Math.min(e.getFromIndex(), e.getToIndex()));
			firePropertyChange("rowSize", 0, getReportModel().getRowModel()
					.getTotalRowHeight());
		}
		repaint();
	}

	void pushRowMovedUndo(int fromIndex, int toIndex) {
		if (fromIndex != toIndex && canUndo())
			try {
				pushUndo(new RowMovedUndoItem(this, UndoItem.ROW_MOVED,
						fromIndex, toIndex));
			} catch (Exception e1) {
				Utils.showError(e1);
			}
	}

	/** Tells listeners that a row was moved due to a margin change. */
	public void rowMarginChanged(ChangeEvent e) {
		if (isEditing()) {
			removeEditor();
		}
		updatePages(0);
		resizeAndRepaint();
	}

	/**
	 * Tells listeners that the selection model of the TableRowModel changed.
	 */
	public void rowSelectionChanged(ListSelectionEvent e) {
		boolean isAdjusting = e.getValueIsAdjusting();
		if (rowSelectionAdjusting && !isAdjusting) {
			rowSelectionAdjusting = false;
			return;
		}
		rowSelectionAdjusting = isAdjusting;
		if (getColumnCount() <= 0 || getRowCount() <= 0) {
			return;
		}
		int firstIndex = limit(e.getFirstIndex(), 0, getRowCount() - 1);
		int lastIndex = limit(e.getLastIndex(), 0, getRowCount() - 1);
		int minColumn = 0;
		int maxColumn = getColumnCount() - 1;
		if (getColumnSelectionAllowed()) {
			minColumn = selectionModel.getMinSelectionIndex();
			maxColumn = selectionModel.getMaxSelectionIndex();
			if (minColumn == -1 || maxColumn == -1) {
				return;
			}
		}
		Rectangle firstRowRect = getCellRect(firstIndex, minColumn, false);
		Rectangle lastRowRect = getCellRect(lastIndex, maxColumn, false);
		Rectangle dirtyRegion = firstRowRect.union(lastRowRect);
		repaint(dirtyRegion);

	}

	private int limit(int i, int a, int b) {
		return Math.min(b, Math.max(i, a));
	}

	/**
	 * 
	 * @return the ReportModel
	 */
	public ReportModel getReportModel() {
		if (dataModel instanceof ReportModel)
			return (ReportModel) dataModel;
		else
			return null;
	}

	/**
	 * 
	 * @return the TableRowModel
	 */
	public TableRowModel getTableRowModel() {
		return rowModel;
	}

	public int getRowHeight(int row) {
		ReportModel model = getReportModel();
		return (model == null) ? getRowHeight() : model.getRowHeight(row);
	}

	private int getRegionHeight(int startRow, int endRow) {
		int height = 0;
		for (int i = startRow; i <= endRow; i++) {
			height += getTableRowModel().getRowHeight(i);
		}
		return height;
	}

	public void setRowHeight(int rowHeight) {
		this.rowHeight = rowHeight;
	}

	public void setRowHeight(int row, int rowHeight) {
		if (getReportModel() != null) {
			getReportModel().setRowHeight(row, rowHeight);
		}
		super.setRowHeight(row, rowHeight);
	}

	public int rowAtPoint(Point point) {
		int y = point.y;
		int result = (getTableRowModel() == null) ? y / getRowHeight()
				: getTableRowModel().getRowIndexAtY(y);
		if (result < 0) {
			return -1;
		} else if (result >= getRowCount()) {
			return -1;
		} else {
			return result;
		}
	}

	public String getUIClassID() {
		return uiClassID;
	}

	public void updateUI() {
		// Update the UI of the row header
		if (rowHeader != null && rowHeader.getParent() == null) {
			rowHeader.updateUI();
		}

		super.updateUI();
	}

	public Rectangle getCellRect(int row, int column, boolean includeSpacing) {
		return getReportModel().getCellRect(row, column, includeSpacing,
				getComponentOrientation().isLeftToRight());
	}

	public void tableChanged(TableModelEvent e) {
		if (e == null || e.getFirstRow() == TableModelEvent.HEADER_ROW
				|| e.getType() == TableModelEvent.INSERT
				|| e.getType() == TableModelEvent.DELETE) {
			super.tableChanged(e);
			return;
		}
		int modelColumn = e.getColumn();
		int start = e.getFirstRow();
		int end = e.getLastRow();

		Rectangle dirtyRegion;
		if (modelColumn == TableModelEvent.ALL_COLUMNS) {
			dirtyRegion = new Rectangle(0, getRegionHeight(0, start),
					getColumnModel().getTotalColumnWidth(), 0);
		} else {
			int column = convertColumnIndexToView(modelColumn);
			dirtyRegion = getCellRect(start, column, false);
		}

		if (end != Integer.MAX_VALUE) {
			dirtyRegion.height = getRegionHeight(start, end);
			repaint(dirtyRegion.x, dirtyRegion.y, dirtyRegion.width,
					dirtyRegion.height);
		} else {
			clearSelection();
			resizeAndRepaint();
		}
	}

	public void setModel(TableModel dataModel) {
		ReportModel old = this.getReportModel();
		if (old != null) {
			old.removePropertyChangeListener(this);
		}
		super.setModel(dataModel);
		if (getReportModel() != null) {
			updatePages(0);
			setColumnModel(getReportModel().getColumnModel());
			setColumnSelectionAllowed(true);
			setRowModel(getReportModel().getRowModel());
			if (getTableHeader() != null) {
				getTableHeader().setResizingAllowed(
						getReportModel().isColSizing());
				getTableHeader().setReorderingAllowed(
						getReportModel().isColMoving());
			}
			getReportModel().addPropertyChangeListener(this);
		}
	}

	public void setTableHeader(JTableHeader tableHeader) {
		super.setTableHeader(tableHeader);
		if (tableHeader == null)
			return;
		if (getReportModel() == null)
			return;
		tableHeader.setResizingAllowed(getReportModel().isColSizing());
		tableHeader.setReorderingAllowed(getReportModel().isColMoving());
	}

	/**
	 * Sets the TableRowModel
	 * 
	 * @param rowModel
	 *            the TableRowModel
	 */
	public void setRowModel(TableRowModel rowModel) {
		if (rowModel == null) {
			throw new IllegalArgumentException(
					Messages.getString("JReportGrid.5")); //$NON-NLS-1$
		}
		TableRowModel old = this.rowModel;
		if (rowModel != old) {
			if (old != null) {
				old.removeRowModelListener(this);
			}
			this.rowModel = rowModel;
			rowModel.addRowModelListener(this);

			if (rowHeader != null) {
				rowHeader.setRowModel(rowModel);
			}
			firePropertyChange("rowModel", old, rowModel); //$NON-NLS-1$
			resizeAndRepaint();
		}
	}

	/**
	 * Returns the row's header value
	 * 
	 * @param row
	 *            the row's number
	 * @return the row's header value
	 */
	public String getRowName(int row) {
		return "" + row;
	}

	/**
	 * Calculates pages' size
	 * 
	 * @param startRow
	 *            the first row for calculation
	 */
	public void updatePages(int startRow) {
		getReportModel().updatePages(startRow);
	}

	public void createDefaultColumnsFromModel() {
		if (getReportModel() != null)
			setColumnModel(getReportModel().getColumnModel());
		else
			super.createDefaultColumnsFromModel();
	}

	/**
	 * 
	 * @param rowHeader
	 *            the RowHeader
	 */
	public void setRowHeader(RowHeader rowHeader) {
		if (this.rowHeader != rowHeader) {
			RowHeader old = this.rowHeader;
			if (old != null) {
				old.setTable(null);
			}
			this.rowHeader = rowHeader;
			if (rowHeader != null) {
				rowHeader.setTable(this);
			}
			firePropertyChange("rowHeader", old, rowHeader); //$NON-NLS-1$
		}
	}

	/**
	 * 
	 * @return the RowHeader
	 */
	public RowHeader getRowHeader() {
		return rowHeader;
	}

	public boolean isShowGrid() {
		return (getShowHorizontalLines() && getShowVerticalLines());
	}

	protected TableColumnModel createDefaultColumnModel() {
		return new ReportColumnModel();
	}

	protected JTableHeader createDefaultTableHeader() {
		return new JReportHeader(columnModel);
	}

	public int convertColumnIndexToModel(int viewColumnIndex) {
		return viewColumnIndex;
	}

	public void changeSelection(int rowIndex, int columnIndex, boolean toggle,
			boolean extend) {
		ListSelectionModel rsm = getSelectionModel();
		ListSelectionModel csm = getColumnModel().getSelectionModel();

		boolean selected = isCellSelected(rowIndex, columnIndex);

		changeSelectionModel(csm, columnIndex, toggle, extend, selected);
		changeSelectionModel(rsm, rowIndex, toggle, extend, selected);

		Cell cell = getReportModel().getReportCell(rowIndex, columnIndex);
		if (cell.isChild()) {
			rowIndex = getReportModel()
					.getOwnerRow(cell, rowIndex, columnIndex);
			columnIndex = getReportModel().getOwnerColumn(cell, rowIndex,
					columnIndex);
			changeSelectionModel(csm, columnIndex, toggle, extend, selected);
			changeSelectionModel(rsm, rowIndex, toggle, extend, selected);
		}

		if (getAutoscrolls()) {
			Rectangle cellRect = getCellRect(rowIndex, columnIndex, false);
			if (cellRect != null) {
				scrollRectToVisible(cellRect);
			}
		}
		fireCellSelectChanged(new CellSelectChangedEvent(this,
				rsm.getMinSelectionIndex(), csm.getMinSelectionIndex()));
	}

	public boolean isCellSelected(int row, int column) {
		if (!getRowSelectionAllowed() && !getColumnSelectionAllowed()) {
			return false;
		}
		boolean result = (!getRowSelectionAllowed() || isRowSelected(row))
				&& (!getColumnSelectionAllowed() || isColumnSelected(column));
		if (!result) {
			Cell cell = getReportModel().getReportCell(row, column);
			if (cell.isChild()) {
				row = getReportModel().getOwnerRow(cell, row, column);
				column = getReportModel().getOwnerColumn(cell, row, column);
				result = (!getRowSelectionAllowed() || isRowSelected(row))
						&& (!getColumnSelectionAllowed() || isColumnSelected(column));
			}
		}
		return result;
	}

	public void columnMoving(TableColumnModelEvent e) {
		try {
			if (e.getFromIndex() != e.getToIndex() && canUndo()) {
				pushUndo(new BackupItem(this, UndoItem.COLUMN_MOVED)); //$NON-NLS-1$
			}
		} catch (Exception e1) {
			Utils.showError(e1);
		}
	}

	public void columnMoved(TableColumnModelEvent e) {
		if (isEditing()) {
			cellEditor.stopCellEditing();
		}
		getTableRowModel().moveColumn(e.getFromIndex(), e.getToIndex());
		repaint();
	}

	/**
	 * Unions the selected cells
	 * 
	 */
	public void unionCell() {
		GridRect r = getSelectionRect();
		if (r == null)
			return;
		Cell cell = getReportModel().getReportCell(r.getTopRow(),
				r.getLeftCol());
		if (cell.isSpan()
				&& cell.getRowSpan() == r.getBottomRow() - r.getTopRow()
				&& cell.getColSpan() == r.getRightCol() - r.getLeftCol()) {
			if (canUndo())
				pushUndo(new CellUndoItem(this, UndoItem.CLEAR_UNION_CELLS));
			getReportModel().clearUnion(r.getTopRow(), r.getLeftCol());
			cell.setRowSpan(0);
			cell.setColSpan(0);
		} else {
			pushUndo(new CellUndoItem(this, UndoItem.UNION_CELLS));
			getReportModel().unionCells(r.getTopRow(), r.getLeftCol(),
					r.getBottomRow(), r.getRightCol());
		}
		repaint();
	}
	
	/**
	 * Auto height row
	 * 
	 */
	public void autoHeightCell() {
		GridRect rect = getSelectionRect();
		if (rect == null)
			return;
		if (canUndo())
			pushUndo(new StyleUndoItem(this, UndoItem.CELL_AUTOHEIGHT));
		CellStyle style = getReportModel().getStyles(getSelectedCell().getStyleId());
		boolean b = !style.isAutoHeight();
		
		Iterator<Cell> it = getReportModel().getSelectedCells(rect);
		while (it.hasNext()) {
			Cell cell = it.next();
			style = getReportModel().getStyles(cell.getStyleId());
			Object index = getReportModel().addStyle(style.deriveAutoHeight(b));
			cell.setStyleId(index);
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		boolean isAdjusting = e.getValueIsAdjusting();
		if (rowSelectionAdjusting && !isAdjusting) {
			rowSelectionAdjusting = false;
			return;
		}
		rowSelectionAdjusting = isAdjusting;
		if (getRowCount() <= 0 || getColumnCount() <= 0) {
			return;
		}
		int firstIndex = limit(e.getFirstIndex(), 0, getRowCount() - 1);
		int lastIndex = limit(e.getLastIndex(), 0, getRowCount() - 1);
		int lastRow = lastIndex;
		int firstRow = firstIndex;
		for (int column = 0; column < getColumnCount(); column++) {
			Cell cell = getReportModel().getReportCell(lastIndex, column);
			if (cell.isChild()) {
				int row = getReportModel().getOwnerRow(cell, lastIndex, column);
				firstRow = Math.min(firstRow, row);
				lastRow = Math.max(lastRow, row + cell.getOwner().getRowSpan());
			} else if (cell.getRowSpan() > 0) {
				lastRow = Math.max(lastRow, lastIndex + cell.getRowSpan());
			}
		}
		firstIndex = firstRow;
		lastIndex = lastRow;
		Rectangle firstRowRect = getCellRect(firstIndex, 0, false);
		Rectangle lastRowRect = getCellRect(lastIndex, getColumnCount() - 1,
				false);
		Rectangle dirtyRegion = firstRowRect.union(lastRowRect);
		repaint(dirtyRegion);
	}

	/**
	 * Returns the CellStyle by index
	 * 
	 * @param index
	 *            the CellStyle's id
	 * @return the CellStyle
	 */
	public CellStyle getCellStyle(Object index) {
		CellStyle style = getReportModel().getStyles(index);
		if (style == null)
			return CellStyle.getDefaultStyle();
		return style;
	}

	/**
	 * Sets the horizontal alignment for the selected cells The horizontal
	 * alignment is a constant that may be LEFT, RIGHT, CENTER or JUSTIFY
	 * 
	 * @param align
	 *            the new horizontal alignment for the cells
	 */
	public void setHorzAlign(int align) {
		GridRect rect = getSelectionRect();
		if (rect == null)
			return;
		if (canUndo())
			pushUndo(new StyleUndoItem(this, UndoItem.CELL_ALIGN));
		getReportModel().setHorizontalAlignment(rect, align);
		repaint();
	}

	/**
	 * Sets the vertical alignment for the selected cells The vertical alignment
	 * is a constant that may be TOP, BOTTOM or CENTER
	 * 
	 * @param align
	 *            the new vertical alignment for the cells
	 */
	public void setVertAlign(int align) {
		GridRect rect = getSelectionRect();
		if (rect == null)
			return;
		if (canUndo())
			pushUndo(new StyleUndoItem(this, UndoItem.CELL_ALIGN));
		getReportModel().setVerticalAlignment(rect, align);
		repaint();
	}

	/**
	 * Sets the borders at the specified positions for the selected cells
	 * 
	 * @param positions
	 *            positions - boolean values, where true sets border, otherwise
	 *            does nothing. Position's index can be from Border.LINE_LEFT to
	 *            Border.LINE_HMIDDLE
	 */
	public void addBorder(boolean[] positions) {
		GridRect r = getSelectionRect();
		if (r == null)
			return;
		if (canUndo())
			pushUndo(new StyleUndoItem(this, UndoItem.CELL_BORDER));
		getReportModel().addBorder(r.getTopRow(), r.getLeftCol(),
				r.getBottomRow(), r.getRightCol(), positions,
				jdbreport.model.Border.defaultBorder);
		repaint();
	}

	/**
	 * Removes the borders at the specified positions for the selected cells
	 * 
	 * @param positions
	 *            positions - boolean values, where true sets border, otherwise
	 *            does nothing. Position's index can be from Border.LINE_LEFT to
	 *            Border.LINE_HMIDDLE
	 */
	public void removeBorder(boolean[] positions) {
		GridRect r = getSelectionRect();
		if (r == null)
			return;
		if (canUndo())
			pushUndo(new StyleUndoItem(this, UndoItem.REMOVE_CELL_BORDER));
		getReportModel().addBorder(r.getTopRow(), r.getLeftCol(),
				r.getBottomRow(), r.getRightCol(), positions, null);
		repaint();
	}

	/**
	 * Sets the font's name for the selected cells.
	 * 
	 * @param fontName
	 *            the font's name
	 */
	public void setFontName(String fontName) {
		GridRect rect = getSelectionRect();
		if (rect == null)
			return;
		if (canUndo())
			pushUndo(new StyleUndoItem(this, UndoItem.CELL_FONT_NAME));
		getReportModel().setFontName(rect, fontName);
		repaint();
	}

	/**
	 * Sets the font's size for the selected cells
	 * 
	 * @param fontSize
	 *            the new font's size
	 */
	public void setFontSize(int fontSize) {
		GridRect rect = getSelectionRect();
		if (rect == null)
			return;
		if (canUndo())
			pushUndo(new StyleUndoItem(this, UndoItem.CELL_FONT_SIZE));
		getReportModel().setFontSize(rect, fontSize);
		repaint();
	}

	/**
	 * Changes the font's style for the selected cells on the inverse one
	 * 
	 * @param fontStyle
	 *            the font's style The style argument is an integer bitmask that
	 *            may be PLAIN, or a bitwise union of BOLD, ITALIC, UNDERLINE,
	 *            STRIKETHROUGH
	 */
	public void setFontStyle(int fontStyle) {
		GridRect gr = getSelectionRect();
		if (gr == null)
			return;
		this.setFontStyle(
				fontStyle,
				!getReportModel().isFontStyle(gr.getTopRow(), gr.getLeftCol(),
						fontStyle));
	}

	/**
	 * Sets the font's style for the selected cells
	 * 
	 * @param fontStyle
	 *            the font's style The style argument is an integer bitmask that
	 *            may be PLAIN, or a bitwise union of BOLD, ITALIC, UNDERLINE,
	 *            STRIKETHROUGH
	 * @param enable
	 *            if true, the style is determined, otherwise the style is
	 *            removed.
	 */
	private void setFontStyle(int fontStyle, boolean enable) {
		if (canUndo())
			pushUndo(new StyleUndoItem(this, UndoItem.CELL_FONT_STYLE));
		getReportModel().setFontStyle(getSelectionRect(), fontStyle, enable);
		repaint();
	}

	/**
	 * Sets background color for the selected cells
	 * 
	 * @param color
	 *            the new background color for the cells
	 */
	public void setCellBackground(Color color) {
		GridRect rect = getSelectionRect();
		if (rect == null)
			return;
		if (canUndo())
			pushUndo(new StyleUndoItem(this, UndoItem.CELL_BACKGROUND));
		getReportModel().setBackground(rect, color);
		repaint();
	}

	/**
	 * Increments decimal position of the numeric value for selected cells.
	 */
	public void incDecimals() {
		GridRect rect = getSelectionRect();
		if (rect == null)
			return;
		if (canUndo())
			pushUndo(new StyleUndoItem(this, UndoItem.INC_DECIMAL));
		int d = getCellStyle(getSelectedCell().getStyleId()).getDecimal() + 1;
		if (d == 0)
			d = 1;
		getReportModel().setDecimals(rect, d);
		repaint();
	}

	/**
	 * Decrements decimal position of the numeric value for selected cells.
	 */
	public void decDecimals() {
		GridRect rect = getSelectionRect();
		if (rect == null)
			return;
		if (canUndo())
			pushUndo(new StyleUndoItem(this, UndoItem.DEC_DECIMAL));
		getReportModel().setDecimals(rect,
				getCellStyle(getSelectedCell().getStyleId()).getDecimal() - 1);
		repaint();
	}

	/**
	 * Returns the first selected cell
	 * 
	 * @return the first selected cell
	 */
	public Cell getSelectedCell() {
		return getReportModel().getReportCell(getSelectedRow(),
				getSelectedColumn());
	}

	private GridRect getSelectedGrid() {
		int minRow = selectionModel.getMinSelectionIndex();
		int maxRow = selectionModel.getMaxSelectionIndex();
		if (minRow == -1 || maxRow == -1) {
			return null;
		}
		int minColumn = getColumnModel().getSelectionModel()
				.getMinSelectionIndex();
		int maxColumn = getColumnModel().getSelectionModel()
				.getMaxSelectionIndex();
		if (minColumn == -1 || maxColumn == -1) {
			return null;
		}
		return new GridRect(minRow, minColumn, maxRow, maxColumn);
	}

	/**
	 * 
	 * @return the selected coordinates
	 */
	public GridRect getSelectionRect() {
		return adjustSpaned(getSelectedGrid());
	}

	private GridRect adjustSpaned(GridRect rect) {
		if (rect == null)
			return null;
		int right = rect.getRightCol();
		int bottom = rect.getBottomRow();
		TableRow tableRow = null;
		for (int r = rect.getTopRow(); r <= rect.getBottomRow(); r++) {
			tableRow = getReportModel().getRowModel().getRow(r);
			for (int c = rect.getLeftCol(); c <= rect.getRightCol(); c++) {
				Cell cell = tableRow.getCellItem(c);
				if (cell.isSpan()) {
					if (r + cell.getRowSpan() > bottom) {
						bottom = r + cell.getRowSpan();
					}
					if (c + cell.getColSpan() > right) {
						right = c + cell.getColSpan();
					}
				}
			}
		}
		if (rect.getBottomRow() < bottom) {
			Group group = getReportModel().getRowModel().getGroup(tableRow);
			for (int n = rect.getBottomRow() + 1; n <= bottom; n++) {
				Group newGroup = getReportModel().getRowModel().getGroup(n);
				if (group != newGroup) {
					bottom = rect.getBottomRow();
				}
			}
		}
		if (right > rect.getRightCol() || bottom > rect.getBottomRow()) {
			return new GridRect(rect.getTopRow(), rect.getLeftCol(), bottom,
					right);
		}
		return rect;
	}

	/**
	 * Selects cells by rect
	 * 
	 * @param rect
	 *            coordinates
	 */
	public void setSelectedRect(GridRect rect) {
		if (rect == null) {
			selectionModel.clearSelection();
			getColumnModel().getSelectionModel().clearSelection();
			return;
		}
		changeSelection(rect.getTopRow(), rect.getLeftCol(), false, false);
		selectionModel.setSelectionInterval(rect.getTopRow(),
				rect.getBottomRow());
		getColumnModel().getSelectionModel().setSelectionInterval(
				rect.getLeftCol(), rect.getRightCol());
	}

	private void changeSelectionModel(ListSelectionModel sm, int index,
			boolean toggle, boolean extend, boolean selected) {
		if (extend) {
			if (toggle) {
				sm.setAnchorSelectionIndex(index);
			} else {
				sm.setLeadSelectionIndex(index);
			}
		} else {
			if (toggle) {
				if (selected) {
					sm.removeSelectionInterval(index, index);
				} else {
					sm.addSelectionInterval(index, index);
				}
			} else {
				sm.setSelectionInterval(index, index);
			}
		}
	}

	public TableCellEditor getCellEditor(int row, int column) {
		Cell cell = getReportModel().getReportCell(row, column);
		if (cell.getValue() == null)
			return getDefaultEditor(Object.class);
		else if (cell.getContentType().equals(Cell.TEXT_HTML)) {
			return getHTMLEditor();
		}
		return getDefaultEditor(cell.getValue().getClass());
	}

	private TableCellEditor getHTMLEditor() {
		if (htmlEditor == null) {
			htmlEditor = new HTMLEditor();
		}
		return htmlEditor;
	}

	protected void initializeLocalVars() {
		super.initializeLocalVars();
		setRowHeader(createDefaultRowHeader());
	}

	protected RowHeader createDefaultRowHeader() {
		return new RowHeader(this);
	}

	protected void configureEnclosingScrollPane() {
		Container p = getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				JViewport viewport = scrollPane.getViewport();
				if (viewport == null || viewport.getView() != this) {
					return;
				}
				scrollPane.setColumnHeaderView(getTableHeader());
				scrollPane.setRowHeaderView(getRowHeader());
				Border border = scrollPane.getBorder();
				if (border == null || border instanceof UIResource) {
					scrollPane.setBorder(UIManager
							.getBorder("Table.scrollPaneBorder")); //$NON-NLS-1$
				}
			}
		}
	}

	public void setSize(Dimension newSize) {
		super.setSize(newSize);
		if (getRowHeader() != null) {
			Dimension headerSize = new Dimension(getRowHeader().getWidth(),
					newSize.height);
			getRowHeader().setSize(headerSize);
			getRowHeader().setPreferredSize(headerSize);
		}
	}

	protected void unconfigureEnclosingScrollPane() {
		Container p = getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				JViewport viewport = scrollPane.getViewport();
				if (viewport == null || viewport.getView() != this) {
					return;
				}
				scrollPane.setColumnHeaderView(null);
				scrollPane.setRowHeaderView(null);
			}
		}
	}

	public void addUndoListener(UndoListener l) {
		undoListener = l;
	}

	public void removeUndoListener(UndoListener l) {
		if (undoListener == l)
			undoListener = null;
	}

	protected void pushUndo(UndoItem undo) {
		if (undoListener != null) {
			undoListener.pushUndo(new UndoEvent(this, undo));
		}
	}

	protected void unionUndo(UndoItem undo) {
		if (undoListener != null) {
			undoListener.unionUndo(new UndoEvent(this, undo));
		}
	}

	protected boolean canUndo() {
		return undoListener != null;
	}

	public void addCellSelectListener(CellSelectListener l) {
		listenerList.add(CellSelectListener.class, l);
	}

	/**
	 * @param l
	 *            CellSelectListener
	 */
	public void removeCellSelectListener(CellSelectListener l) {
		listenerList.remove(CellSelectListener.class, l);
	}

	public void fireCellSelectChanged() {
		int r = getSelectedRow();
		int c = getSelectedColumn();
		if (r >= 0 && c >= 0)
			fireCellSelectChanged(new CellSelectChangedEvent(this, r, c));
	}

	public void fireCellSelectChanged(CellSelectChangedEvent e) {
		if (getReportModel().isUpdate())
			return;
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CellSelectListener.class) {
				((CellSelectListener) listeners[i + 1]).cellSelectedChange(e);
			}
		}
	}

	public boolean print(boolean showPrintDialog, PrintRequestAttributeSet attr)
			throws PrinterException {
		return print(
				getReportModel().getReportPage().isShrinkWidth() ? PrintMode.FIT_WIDTH
						: PrintMode.NORMAL, null, null, showPrintDialog, attr,
				!GraphicsEnvironment.isHeadless());
	}

	public boolean print() throws PrinterException {
		return print(
				getReportModel().getReportPage().isShrinkWidth() ? PrintMode.FIT_WIDTH
						: PrintMode.NORMAL, null, null, isShowPrintDialog(),
				getPrintAttributes(), !GraphicsEnvironment.isHeadless());
	}

	public PrintRequestAttributeSet getPrintAttributes() {
		PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();

		ReportPage page = getReportModel().getReportPage();

		if (page.getOrientation() == PageFormat.LANDSCAPE) {
			attr.add(OrientationRequested.LANDSCAPE);
		} else if (page.getOrientation() == PageFormat.REVERSE_LANDSCAPE) {
			attr.add(OrientationRequested.REVERSE_LANDSCAPE);
		} else {
			attr.add(OrientationRequested.PORTRAIT);
		}

		if (page.getCopies() > 1) {
			attr.add(new javax.print.attribute.standard.Copies(page.getCopies()));
		}

		Attribute a = null;
		if (page.getPaperSize() == ReportPage.PaperSize.A4) {
			a = MediaName.ISO_A4_WHITE;
		} else if (page.getPaperSize() == ReportPage.PaperSize.Letter) {
			a = MediaName.NA_LETTER_WHITE;
		}
		if (a != null)
			attr.add(a);

		final Units unit = Units.MM;
		a = null;
		if (page.getOrientation() == PageFormat.PORTRAIT) {
			a = new MediaPrintableArea((float) page.getLeftMargin(unit),
					(float) page.getTopMargin(unit),
					(float) page.getImageableWidth(unit),
					(float) page.getImageableHeight(unit),
					MediaPrintableArea.MM);
		} else if (page.getOrientation() == PageFormat.LANDSCAPE) {
			a = new MediaPrintableArea((float) page.getTopMargin(unit),
					(float) page.getRightMargin(unit),
					(float) page.getImageableHeight(unit),
					(float) page.getImageableWidth(unit), MediaPrintableArea.MM);
		} else if (page.getOrientation() == PageFormat.REVERSE_LANDSCAPE) {
			a = new MediaPrintableArea((float) page.getTopMargin(unit),
					(float) page.getLeftMargin(unit),
					(float) page.getImageableHeight(unit),
					(float) page.getImageableWidth(unit), MediaPrintableArea.MM);
		}
		if (a != null) {
			attr.add(a);
		}

		String s = getReportModel().getReportTitle();
		if (s.length() > 25) {
			s = s.substring(0, 25) + "...";
		}
		attr.add(new JobName(s, null));

		return attr;
	}

	private boolean isShowPrintDialog() {
		return showPrintDialog;
	}

	public void setShowPrintDialog(boolean show) {
		this.showPrintDialog = show;
	}

	public Printable getPrintable(PrintMode printMode,
			MessageFormat headerFormat, MessageFormat footerFormat) {
		return new ReportPrintable(this, printMode);
	}

	public Printable getReportPrintable(boolean isPreview) {
		return new ReportPrintable(this, getReportModel().getReportPage()
				.isShrinkWidth() ? PrintMode.FIT_WIDTH : PrintMode.NORMAL,
				isPreview);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getReportModel()) {
			if (evt.getPropertyName().equals("colSizing")) { //$NON-NLS-1$
				tableHeader.setResizingAllowed(getReportModel().isColSizing());
			} else if (evt.getPropertyName().equals("colMoving")) { //$NON-NLS-1$
				tableHeader
						.setReorderingAllowed(getReportModel().isColMoving());
			} else if (evt.getPropertyName().equals("reportTitle")) { //$NON-NLS-1$
				firePropertyChange("reportTitle", evt.getOldValue(), evt //$NON-NLS-1$
						.getNewValue());
			} else if (evt.getPropertyName().equals("visible")) { //$NON-NLS-1$
				firePropertyChange("visible", evt.getOldValue(), evt //$NON-NLS-1$
						.getNewValue());
			} else if (evt.getPropertyName().equals("showHeader")) { //$NON-NLS-1$
				tableHeader.setVisible((Boolean) evt.getNewValue());
			} else if (evt.getPropertyName().equals("showRowHeader")) { //$NON-NLS-1$
				rowHeader.setVisible((Boolean) evt.getNewValue());
			}
		}
	}

	public void editingStopped(ChangeEvent e) {
		TableCellEditor editor = getCellEditor();
		if (editor != null) {
			Object value = editor.getCellEditorValue();
			int row = editingRow;
			int column = editingColumn;
			Cell cell = getReportModel().getOwnerReportCell(row, column);
			if (cell.isNull() && value != null)
				cell = getReportModel().createReportCell(row, column);
			Object oldValue = cell.getValue();
			cell.setValue(value);
			removeEditor();
			fireCellValueChanged(new CellValueChangedEvent(this, oldValue, row,
					column));
		}
		this.requestFocus();
	}

	public void addCellValueChangeListener(CellValueChangeListener l) {
		listenerList.add(CellValueChangeListener.class, l);
	}

	/**
	 * @param l
	 *            CellValueChangeListener
	 */
	public void removeCellValueChangeListener(CellValueChangeListener l) {
		listenerList.remove(CellValueChangeListener.class, l);
	}

	protected void fireCellValueChanged(CellValueChangedEvent e) {
		if (getReportModel().isUpdate())
			return;
		Cell cell = getReportModel().getReportCell(e.getRow(), e.getColumn());
		if ((e.getOldValue() != null && !e.getOldValue()
				.equals(cell.getValue()))
				|| (cell.getValue() != null && !cell.getValue().equals(
						e.getOldValue()))) {

			if (canUndo()) {
				pushUndo(new SetValueItem(this, e.getOldValue(), e.getRow(),
						e.getColumn()));
			}
			Object[] listeners = listenerList.getListenerList();
			for (int i = listeners.length - 2; i >= 0; i -= 2) {
				if (listeners[i] == CellValueChangeListener.class) {
					((CellValueChangeListener) listeners[i + 1])
							.cellValueChange(e);
				}
			}
		}
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		if (orientation == SwingConstants.HORIZONTAL) {
			return 100;
		}
		return 16;
	}

	public boolean downCell() {
		int row = getSelectedRow();
		int column = getSelectedColumn();
		if (row < 0 || column < 0)
			return false;
		Cell cell = getReportModel().getReportCell(row, column);
		row = row + cell.getRowSpan() + 1;
		if (row < getReportModel().getRowCount()) {
			if (isEditing())
				getCellEditor().stopCellEditing();
			changeSelection(row, column, false, false);
			return true;
		}
		return false;
	}

	public boolean rightCell() {
		int row = getSelectedRow();
		int column = getSelectedColumn();
		if (row < 0 || column < 0) {
			return false;
		}
		Cell cell = getReportModel().getReportCell(row, column);
		column = column + cell.getColSpan() + 1;
		if (column < getColumnModel().getColumnCount()) {
			if (isEditing()) {
				getCellEditor().stopCellEditing();
			}
			changeSelection(row, column, false, false);
			return true;
		}
		return false;
	}

	public boolean nextCell() {
		int row = getSelectedRow();
		int column = getSelectedColumn();
		if (row < 0 || column < 0)
			return false;
		Cell cell = getReportModel().getReportCell(row, column);
		column = column + cell.getColSpan() + 1;
		if (column < getColumnModel().getColumnCount()) {
			if (isEditing())
				getCellEditor().stopCellEditing();
			changeSelection(row, column, false, false);
			return true;
		} else {
			column = 0;
			cell = getReportModel().getReportCell(row, column);
			row = row + cell.getRowSpan() + 1;
			if (row >= getReportModel().getRowCount()) {
				row = 0;
			}
			if (row < getReportModel().getRowCount()) {
				if (isEditing()) {
					getCellEditor().stopCellEditing();
				}
				changeSelection(row, column, false, false);
				return true;
			}
		}
		return false;
	}

	public void horizontalPageBreak() {
		int row = getSelectedRow();
		if (row < 0) {
			return;
		}
		boolean end = getReportModel().isRowBreak(row);
		if (canUndo()) {
			pushUndo(new RowBreakUndoItem(this, row));
		}
		getReportModel().setRowBreak(row, !end);
		repaint();
	}

	public void verticalPageBreak() {
		int column = getSelectedColumn();
		if (column < 0)
			return;
		int row = getSelectedRow();
		Cell cell = getReportModel().getReportCell(row, column);
		column = getReportModel().getOwnerColumn(cell, row, column);
		cell = getReportModel().getReportCell(row, column);
		if (cell.getColSpan() > 0) {
			column += cell.getColSpan();
		}
		boolean end = getReportModel().isColumnBreak(column);
		if (canUndo())
			pushUndo(new ColumnBreakUndoItem(this, column));
		getReportModel().setColumnBreak(column, !end);
		repaint();
	}

	public void delete() {
		if (getCellEditor() != null) {
			Component editor = ((ReportCellEditor) getCellEditor())
					.getComponent();
			if (editor instanceof JTextComponent) {
				((JTextComponent) editor).replaceSelection(""); //$NON-NLS-1$
				return;
			}
		}
		GridRect rect = getSelectionRect();
		if (rect == null)
			return;
		if (canUndo())
			pushUndo(new CellUndoItem(this, UndoItem.DELETE_CELLS));
		getReportModel().delete(rect);
		repaint();
	}

	@Override
	public Component prepareEditor(TableCellEditor editor, int row, int column) {
		Component comp = super.prepareEditor(editor, row, column);
		if (comp instanceof JComponent) {
			((JComponent) comp).setComponentPopupMenu(getComponentPopupMenu());
		}
		return comp;
	}

	private String getRendererText(int row, int column) {
		Cell cell = getReportModel().getReportCell(row, column);
		return getRenderedText(cell);
	}

	private String getRenderedText(Cell cell) {

		if (cell.isNull() || cell.isChild())
			return ""; //$NON-NLS-1$
		TableCellRenderer renderer = getCellRenderer(cell);
		if (renderer instanceof JTextComponent) {
			JTextComponent tc = (JTextComponent) renderer;
			tc.setText(cell.getText());
			tc.selectAll();
			String result = tc.getSelectedText();
			if (result != null && result.length() > 0) {
				if (result.indexOf('\n') == 0) {
					result = result.substring(1);
				}
			}
			return result;
		}
		return cell.getText();
	}

	public String copyText(GridRect selectionRect) throws SaveReportException {
		StringBuffer result = new StringBuffer();
		for (int row = selectionRect.getTopRow(); row <= selectionRect
				.getBottomRow(); row++) {
			String text = getRendererText(row, selectionRect.getLeftCol());
			if (text != null) {
				result.append(text);
			}
			for (int col = selectionRect.getLeftCol() + 1; col <= selectionRect
					.getRightCol(); col++) {
				result.append('\t');
				text = getRendererText(row, col);
				if (text != null) {
					result.append(text);
				}

			}
			result.append("\r\n"); //$NON-NLS-1$
		}
		return result.toString();
	}

	public Image copyImage(GridRect selectionRect) {
		if (selectionRect == null)
			return null;
		Cell cell = getReportModel().getReportCell(selectionRect.getTopRow(),
				selectionRect.getLeftCol());
		if (cell.getPicture() != null) {
			return cell.getPicture().getImage();
		}
		return null;
	}

	public void copy() {
		if (getCellEditor() != null) {
			Component editor = ((ReportCellEditor) getCellEditor())
					.getComponent();
			if (editor instanceof JTextComponent) {
				((JTextComponent) editor).copy();
				return;
			}
		}
		GridRect rect = getSelectionRect();
		if (rect == null) {
			return;
		}
		try {
			Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
			ReportTransferable reportTransferable = new ReportTransferable();
			Image image = copyImage(getSelectionRect());
			if (image != null) {
				reportTransferable.addCopyData(image,
						ReportTransferable.TypeFlavor.image);
			}
			String copyData = copyText(getSelectionRect());
			reportTransferable.addCopyData(copyData,
					ReportTransferable.TypeFlavor.text);
			copyData = copy(getSelectionRect());
			reportTransferable.addCopyData(copyData, ReportTransferable.TypeFlavor.xml);
			reportTransferable.addCopyData(copyData.getBytes("UTF-8"),	ReportTransferable.TypeFlavor.xml);
			clip.setContents(reportTransferable, reportTransferable);
		} catch (SaveReportException e) {
			Utils.showError(e);
		} catch (UnsupportedEncodingException e) {
			Utils.showError(e);
		}
	}

	public String copy(GridRect selectionRect) throws SaveReportException {
		ClipboardParser writer = createClipboardWriter();
		StringWriter pw = new StringWriter();
		writer.save(pw, getReportModel(), selectionRect);
		return pw.getBuffer().toString();
	}

	protected ClipboardParser createClipboardWriter() {
		return new ClipboardParser();
	}

	public void pasteText(String data, int selectedRow, int selectedColumn)
			throws IOException {
		if (canUndo()) {
			try {
				pushUndo(new BackupItem(this, UndoItem.PASTE_CELLS));
			} catch (Exception e) {
				Utils.showError(e);
			}
		}
		LineNumberReader reader = new LineNumberReader(new StringReader(data));
		String s;
		int row = selectedRow;
		while ((s = reader.readLine()) != null) {
			if (s.length() > 0) {
				if (getRowCount() <= row) {
					addRows(1, -1);
				}
				StringTokenizer st = new StringTokenizer(s, "\t\n\r\f"); //$NON-NLS-1$
				int col = selectedColumn;
				while (st.hasMoreTokens()) {
					if (col >= getColumnCount()) {
						getReportModel().setColumnCount(col + 1);
					}
					getReportModel().createReportCell(row, col).setValue(
							st.nextToken());
					col++;
				}
				row++;
			}
		}
	}

	public void pasteImage(Image image, int selectedRow, int selectedColumn) {
		if (canUndo()) {
			pushUndo(new CellUndoItem(this, UndoItem.INSERT_ICON)); //$NON-NLS-1$
		}
		getReportModel().createReportCell(selectedRow, selectedColumn).setIcon(
				new ImageIcon(image));
	}

	public void paste(String data, int selectRow, int selectCol)
			throws ParserConfigurationException, SAXException, IOException {
		if (canUndo()) {
			try {
				pushUndo(new BackupItem(this, UndoItem.PASTE_CELLS));
			} catch (Exception e) {
				Utils.showError(e);
			}
		}
		startUpdate();
		try {
			loadFragment(new StringReader(data), selectRow, selectCol);
		} finally {
			endUpdate();
		}

	}

	public GridParser createGridWriter() {
		return new GridParser();
	}

	public DefaultHandler createGridHandler(XMLReader reader) {
		return new GridHandler(getReportModel(), reader);
	}

	/**
	 * @param reader
	 * @param selectRow
	 * @param selectCol
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void loadFragment(Reader reader, int selectRow, int selectCol)
			throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser;
		saxParser = factory.newSAXParser();
		org.xml.sax.helpers.DefaultHandler handler = createPasteHandler(
				saxParser.getXMLReader(), selectRow, selectCol);
		saxParser.parse(new InputSource(reader), handler);
	}

	protected DefaultHandler createPasteHandler(XMLReader reader,
			int selectRow, int selectCol) {
		return new FragmentHandler(getReportModel(), reader, selectRow,
				selectCol);
	}

	public void cut() {
		if (getCellEditor() != null) {
			Component editor = ((ReportCellEditor) getCellEditor())
					.getComponent();
			if (editor instanceof JTextComponent) {
				((JTextComponent) editor).cut();
				return;
			}
		}
		copy();
		delete();
	}

	public void paste() {
		if (getCellEditor() != null) {
			Component editor = ((ReportCellEditor) getCellEditor())
					.getComponent();
			if (editor instanceof JTextComponent) {
				((JTextComponent) editor).paste();
				return;
			}
		}
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		try {
			DataFlavor[] dataFlavors = clip.getAvailableDataFlavors();
			int reportFlavorIndex = -1;
			int xmlIndex = -1;
			int textIndex = -1;
			int imageIndex = -1;
			for (int i = 0; i < dataFlavors.length; i++) {
				if (dataFlavors[i].getMimeType().equals(
						ReportTransferable.FLAVOR_XML_MIME_TYPE)) {
					String data = (String) clip.getData(dataFlavors[i]);
					if (data.startsWith("<?xml") //$NON-NLS-1$
							&& data.contains(FragmentHandler.FRAGMENT)) {
						xmlIndex = i;
						break;
					}
				} else if (dataFlavors[i].getMimeType().equals(
						ReportTransferable.FLAVOR_XML_BYTE_MIME_TYPE)) {
					byte[] buf = (byte[]) clip.getData(dataFlavors[i]);
					String data = new String(buf, "UTF-8");
					if (data.startsWith("<?xml") //$NON-NLS-1$
							&& data.contains(FragmentHandler.FRAGMENT)) {
						reportFlavorIndex = i;
						break;
					}
				} else
				if (dataFlavors[i].getMimeType().equals(
						ReportTransferable.FLAVOR_MIME_TYPE)) {
					byte[] buf = (byte[]) clip.getData(dataFlavors[i]);
					String data = new String(buf, "UTF-8");
					if (data.startsWith("<?xml") //$NON-NLS-1$
							&& data.contains(FragmentHandler.FRAGMENT)) {
						reportFlavorIndex = i;
						break;
					}
				} else if (dataFlavors[i].equals(DataFlavor.stringFlavor)) {
					String data = (String) clip.getData(dataFlavors[i]);
					if (data.startsWith("<?xml")) //$NON-NLS-1$
						xmlIndex = i;
					else
						textIndex = i;
				} else if (dataFlavors[i].equals(DataFlavor.imageFlavor)) {
					imageIndex = i;
				}
			}
			if (imageIndex >= 0) {
				Image image = (Image) clip.getData(dataFlavors[imageIndex]);
				if (image != null) {
					pasteImage(image, getSelectedRow(), getSelectedColumn());
					repaint();
				}
				return;
			}
			if (reportFlavorIndex >= 0) {
				String data = new String(
						(byte[]) clip.getData(dataFlavors[reportFlavorIndex]),
						"UTF-8");
				if (data != null) {
					paste(data, getSelectedRow(), getSelectedColumn());
					repaint();
				}
				return;
			}
			if (xmlIndex >= 0) {
				String data = (String) clip.getData(dataFlavors[xmlIndex]);
				if (data != null) {
					paste(data, getSelectedRow(), getSelectedColumn());
					repaint();
				}
				return;
			}
			if (textIndex >= 0) {
				String data = (String) clip.getData(dataFlavors[textIndex]);
				if (data != null) {
					pasteText(data, getSelectedRow(), getSelectedColumn());
					repaint();
				}
				return;
			}
		} catch (UnsupportedFlavorException e) {
			Utils.showError(e);
		} catch (IOException e) {
			Utils.showError(e);
		} catch (ParserConfigurationException e) {
			Utils.showError(e);
		} catch (SAXException e) {
			Utils.showError(e);
		}
	}

	public void insertIcon() {
		JFileChooser fileChooser = new JFileChooser(ReportPane.CURRENT_IMAGE_PATH); //$NON-NLS-1$
		String[] exts = { ".bmp", ".gif", ".png", ".jpg", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				".jpeg" }; //$NON-NLS-1$
		FileFilter filter = new ExtensionFileFilter(exts,
				Messages.getString("JReportGrid.27"));

		fileChooser.addChoosableFileFilter(filter);
		if (ReportBook.isEnableSVG()) {
			fileChooser.addChoosableFileFilter(new ExtensionFileFilter(".svg",
					"SVG image"));
			fileChooser.addChoosableFileFilter(new ExtensionFileFilter(new String[] {".wmf"},
					"WMF image"));
		}
		fileChooser.setFileFilter(filter);
		ImagePreview ip = new ImagePreview(fileChooser);
		ip.setPreferredSize(new Dimension(200, 100));
		fileChooser.setAccessory(ip);

		int status = fileChooser.showOpenDialog(null);
		if (status == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			
			ReportPane.CURRENT_IMAGE_PATH = and.util.Utilities
			.extractFilePath(selectedFile.getPath());
			
			Cell cell = getSelectedCell();
			if (cell.isNull()) {
				cell = getReportModel().createReportCell(getSelectedRow(),
						getSelectedColumn());
			}

			String format = and.util.Utilities.getFileExtension(selectedFile);
			try {
				Picture picture = PictureFactory.createPicture(format);
				picture.load(selectedFile);
				picture.setScale(cell.isScaleIcon());
				cell.setPicture(picture);
				if (canUndo()) {
					pushUndo(new CellUndoItem(this, UndoItem.INSERT_ICON)); //$NON-NLS-1$
				}
			} catch (IOException e) {
				Utils.showError(e);
			}
		}
	}

	public void saveIcon() {
		Cell cell = getSelectedCell();
		if (cell.getPicture() == null && !(cell.getValue() instanceof SVGValue)) {
			return;
		}
		final boolean enableSvg = ReportBook.isEnableSVG()
				&& (cell.getValue() instanceof SVGValue);

		JFileChooser fileChooser = new JFileChooser("."); //$NON-NLS-1$
		ExtensionFileFilter jpegFilter = new ExtensionFileFilter(new String[] {
				".jpeg", ".jpg", ".jpe" }, "JPEG image");
		ExtensionFileFilter pngFilter = new ExtensionFileFilter(".png",
				"PNG image");
		ExtensionFileFilter svgFilter = new ExtensionFileFilter(".svg",
				"SVG image");

		if (enableSvg) {
			fileChooser.addChoosableFileFilter(svgFilter);
			fileChooser.addChoosableFileFilter(pngFilter);
			fileChooser.addChoosableFileFilter(jpegFilter);
		} else {
			fileChooser.addChoosableFileFilter(jpegFilter);
			fileChooser.addChoosableFileFilter(pngFilter);
			fileChooser.addChoosableFileFilter(new ExtensionFileFilter(".gif",
					"GIF image"));
			fileChooser.addChoosableFileFilter(new ExtensionFileFilter(".bmp",
					"Windows BMP"));
		}

		if (enableSvg) {
			fileChooser.setFileFilter(svgFilter);
		} else {
			if ("jpg".equals(cell.getImageFormat())) {
				fileChooser.setFileFilter(jpegFilter);
			} else {
				fileChooser.setFileFilter(pngFilter);
			}
		}

		fileChooser.setAcceptAllFileFilterUsed(false);

		int status = fileChooser.showSaveDialog(this);
		if (status == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();

			String format = and.util.Utilities.getFileExtension(selectedFile);
			if (format.length() == 0) {
				FileFilter filter = fileChooser.getFileFilter();
				if (filter instanceof ExtensionFileFilter) {
					format = ((ExtensionFileFilter) filter).getExtension();
					selectedFile = new File(selectedFile.getPath() + format);
					format = format.substring(1);
				} else {
					format = cell.getImageFormat();
					if (format == null && enableSvg) {
						format = "svg";
						selectedFile = new File(selectedFile.getPath() + ".svg");
					}
				}
			}

			try {
				if (format.equals("svg") && enableSvg) {
					SVGImage image = ((SVGValue) cell.getValue()).getValue();
					PrintWriter pw = new PrintWriter(selectedFile);
					try {
						pw.write(image.getXML());
					} finally {
						pw.close();
					}
				} else {
					RenderedImage image;
					if (cell.getValue() instanceof SVGValue) {
						SVGImage svgImage = ((SVGValue) cell.getValue())
								.getValue();
						image = (RenderedImage) svgImage.getImage(format);
						ImageIO.write(image, format, selectedFile);
					} else {
						cell.getPicture().saveToFile(selectedFile, format);
					}
				}
			} catch (IOException e) {
				Utils.showError(e);
			}
		}
	}

	public void scaleIcon() {
		if (canUndo()) {
			pushUndo(new CellUndoItem(this, UndoItem.SCALE_ICON)); //$NON-NLS-1$
		}
		Cell cell = getSelectedCell();
		if (cell.isNull()) {
			cell = getReportModel().createReportCell(getSelectedRow(),
					getSelectedColumn());
		}
		cell.setScaleIcon(!cell.isScaleIcon());
	}

	public void deleteIcon() {
		if (canUndo()) {
			pushUndo(new CellUndoItem(this, UndoItem.DELETE_ICON)); //$NON-NLS-1$
		}
		Cell cell = getSelectedCell();
		cell.setIcon(null);
		cell.setValue(null);
	}

	public void pageSetup() {
		PageSetup dlg = new PageSetup(getReportModel().getReportPage());
		if (canUndo()) {
			dlg.addUndoListener(undoListener);
		}
		dlg.setVisible(true);
	}

	public void addColumns() {
		if (canUndo()) {
			try {
				pushUndo(new BackupItem(this, UndoItem.ADD_COLUMNS)); //$NON-NLS-1$
			} catch (Exception e) {
				Utils.showError(e);
			}
		}
		getReportModel().addColumns(1, getSelectedColumn() + 1);
	}

	public void removeColumns() {
		int[] cols = getSelectedColumns();
		if (cols.length > 0) {
			if (canUndo()) {
				try {
					pushUndo(new BackupItem(this, UndoItem.REMOVE_COLUMNS)); //$NON-NLS-1$
				} catch (Exception e) {
					Utils.showError(e);
				}
			}
			getReportModel().removeColumns(cols.length, cols[0]);
		}
	}

	public void addRows(int count, int index) {
		if (canUndo())
			try {
				pushUndo(new BackupItem(this, UndoItem.ADD_ROWS)); //$NON-NLS-1$
			} catch (Throwable e) {
				Utils.showError(e);
			}
		getReportModel().addRows(count, index);
	}

	public void removeRows(int count, int index) {
		if (canUndo())
			try {
				pushUndo(new BackupItem(this, UndoItem.REMOVE_ROWS)); //$NON-NLS-1$
			} catch (Exception e) {
				Utils.showError(e);
			}
		getReportModel().removeRows(count, index);
	}

	public void setColumnsWidth(int minColumn, int maxColumn, int width) {
		int[] columns = new int[maxColumn - minColumn + 1];
		int[] widths = new int[columns.length];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = minColumn + i;
			widths[i] = getReportModel().getColumnModel().getColumn(columns[i])
					.getWidth();
		}
		if (canUndo())
			pushUndo(new ResizingColumnUndoItem(this, columns, widths));
		for (int i = minColumn; i <= maxColumn; i++) {
			getReportModel().getColumnModel().getColumn(i)
					.setPreferredWidth(width);
		}
	}

	public void setRowsHeight(int minRow, int maxRow, int height) {
		int[] rows = new int[maxRow - minRow + 1];
		int[] heights = new int[rows.length];
		for (int i = 0; i < rows.length; i++) {
			rows[i] = minRow + i;
			heights[i] = getReportModel().getRowModel().getRow(rows[i])
					.getHeight();
		}
		if (canUndo())
			pushUndo(new ResizingRowUndoItem(this, rows, heights));
		for (int r = minRow; r <= maxRow; r++) {
			setRowHeight(r, height);
		}
	}

	public int calcRowHeight(ReportModel model, Cell cell, int row, int column) {
		ReportCellRenderer renderer = (ReportCellRenderer) getCellRenderer(cell);
		return renderer.getTextHeight(model, row, column);
	}

	/**
	 * Compares text's height and row's height.
	 * 
	 * @param model
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 */
	public void updateRowHeight(ReportModel model, int row, int column) {
		model.updateRowHeight(this, row, column);
	}

	public void startUpdate() {
		getReportModel().startUpdate();
	}

	public void endUpdate() {
		if (!getReportModel().isUpdate())
			return;
		getReportModel().endUpdate();
	}

	public void updateRowHeight(ReportModel model) {
		if (getReportModel().isUpdate())
			return;
		startUpdate();
		try {
			int columnCount = model.getColumnCount();
			int rowCount = model.getRowCount();
			for (int row = 0; row < rowCount; row++) {
				for (int column = 0; column < columnCount; column++) {
					updateRowHeight(model, row, column);
				}
			}
		} finally {
			endUpdate();
		}
	}

	private CellEditorPanel getCellEditorPanel() {
		if (cellEditorPanel == null) {
			cellEditorPanel = new CellEditorPanel();
		}
		return cellEditorPanel;
	}

	protected boolean canEdit(Cell cell) {
		return getReportModel().isEditable() && cell.isEditable();
	}

	public void showCellEditor() {
		int row = getSelectedRow();
		int column = getSelectedColumn();
		CellWrap cell = getReportModel().getCellWrap(row, column);
		if (cell == null)
			return;
		getCellEditorPanel().showDialog(SwingUtilities.getWindowAncestor(this),
				cell, canEdit(cell.getCell()));
		if (getCellEditorPanel().getExitCode() == CellEditorPanel.OK) {
			row = cell.getRow();
			column = cell.getColumn();
			Object oldValue = getReportModel().getReportCell(row, column)
					.getValue();
			Cell newCell = getReportModel().createReportCell(row, column);
			newCell.setValue(getCellEditorPanel().getValue());
			fireCellValueChanged(new CellValueChangedEvent(this, oldValue, row,
					column));
			double scaleY = GraphicUtil.getScaleY();
			double scaleX = GraphicUtil.getScaleX();
			try {
				GraphicUtil.setScaleX(1.0);
				GraphicUtil.setScaleY(1.0);
				updateRowHeight(getReportModel(), row, column);
			} finally {
				GraphicUtil.setScaleX(scaleX);
				GraphicUtil.setScaleY(scaleY);
			}
		}
	}

	public void showCellProperty() {
		GridRect grid = getSelectionRect();
		if (grid == null)
			return;
		if (cellPropertyDialog == null) {
			cellPropertyDialog = createCellProperties();
		} else
			cellPropertyDialog.setCell(grid);
		cellPropertyDialog.setVisible(true);
		if (cellPropertyDialog.isOk()) {
			repaint();
		}
	}

	/**
	 * @throws HeadlessException
	 */
	protected CellPropertiesDlg createCellProperties() throws HeadlessException {
		Window w = SwingUtilities.getWindowAncestor(this);
		if (w instanceof Frame) {
			return new CellPropertiesDlg((Frame) w, this);
		} else {
			return new CellPropertiesDlg((Dialog) w, this);
		}
	}

	void setState(int state) {
		this.state = state;
	}

	public boolean isPrintState() {
		return state == PRINT;
	}

	protected Finder getFinder() {
		if (finder == null) {
			finder = new and.swing.TableFinder(this);
		}
		return finder;
	}

	/* Finder */

	public boolean find(FindParams findParams) {
		return getFinder().find(findParams);
	}

	public boolean incrementalFind(FindParams findParams) {
		return getFinder().incrementalFind(findParams);
	}

	/* End Finder */

	public ReportCellRenderer getHTMLReportRenderer() {
		if (htmlReportRenderer == null) {
			htmlReportRenderer = new HTMLReportRenderer();
		}
		return htmlReportRenderer;
	}

	public ReportCellRenderer getTextReportRenderer() {
		if (textReportRenderer == null) {
			textReportRenderer = new TextReportRenderer();
		}
		return textReportRenderer;
	}

	private void setLazyValue(Hashtable<Class<?>, ProxyLazyValue> h,
			Class<?> c, String s) {
		h.put(c, new UIDefaults.ProxyLazyValue(s));
	}

	protected void setLazyRenderer(Class<?> c, String s) {
		setLazyValue(defaultRenderersByColumnClass, c, s);
	}

	public TableCellRenderer getCellRenderer(int row, int column) {
		Cell cell = getReportModel().getReportCell(row, column);
		return getCellRenderer(cell);
	}

	public TableCellRenderer getCellRenderer(Cell cell) {
		TableCellRenderer renderer = null;
		String content = cell.getContentType();
		if (content.equals(Cell.TEXT_HTML)) {
			renderer = getHTMLReportRenderer();
		} else {
			if (cell.getValue() != null)
				renderer = getDefaultRenderer(cell.getValue().getClass());
			if (renderer == null) {
				renderer = getTextReportRenderer();
			}
		}
		if (renderer instanceof ReportCellRenderer) {
			((ReportCellRenderer) renderer).setCell(cell);
		}
		return renderer;
	}

	protected void createDefaultRenderers() {
		defaultRenderersByColumnClass = new UIDefaults();

		// Objects
		setLazyRenderer(Object.class,
				"jdbreport.grid.JReportGrid$TextReportRenderer"); //$NON-NLS-1$

		// PageNumber
		setLazyRenderer(jdbreport.model.PageNumber.class,
				"jdbreport.grid.JReportGrid$PageNumberRenderer"); //$NON-NLS-1$

		// Booleans
		setLazyRenderer(Boolean.class, "javax.swing.JTable$BooleanRenderer"); //$NON-NLS-1$

		setLazyRenderer(Date.class, "jdbreport.grid.JReportGrid$DateRenderer"); //$NON-NLS-1$

		for (Class<?> c : ReportCell.defaultValuesByClass.keySet()) {
			CellValueInfo vi = ReportCell.defaultValuesByClass.get(c);
			if (vi != null) {
				setLazyRenderer(vi.getCellValueClass(), vi.getRendererClass());
			}
		}

	}

	/**
	 * Direction of an output of pages on the printer. 
	 * If true that pages are printed from left to right, from top to down, 
	 * differently pages are printed from top to down, from left to right.
	 * @return true if pages are printed from left to right, otherwise false
	 * @since 2.0
	 */
	public boolean isPrintLeftToRight() {
		return getReportModel().isPrintLeftToRight();
	}
	
	protected abstract static class AbstractReportRenderer extends JTextPane
			implements ReportCellRenderer, Serializable {

		private static final long serialVersionUID = -770321762562547996L;

		protected static final Border noFocusBorder = new EmptyBorder(1, 1, 1,
				1);

		protected Cell cell;

		protected int verticalAlignment;

		protected int horizontalAlignment;

		protected boolean hasFocus;

		protected int angle;

		protected int rowMargin = ROW_MARGIN;

		protected boolean printState;

		public AbstractReportRenderer() {
			super();
			setOpaque(true);
		}

		public void updateUI() {
			super.updateUI();
		}

		public void setCell(Cell cell) {
			this.cell = cell;
			if (cell != null && cell.isChild())
				this.cell = cell.getOwner();
		}

		public void invalidate() {
		}

		public void validate() {
		}

		public void revalidate() {
		}

		public void repaint(long tm, int x, int y, int width, int height) {
		}

		public void repaint(Rectangle r) {
		}

		public void repaint() {
		}

		public void firePropertyChange(String propertyName, boolean oldValue,
				boolean newValue) {
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			this.hasFocus = hasFocus;

			CellStyle style = ((JReportGrid) table).getCellStyle(cell
					.getStyleId());
			setCellStyle(style);

			rowMargin = ((JReportGrid) table).getRowMargin();

			if (isSelected && !((JReportGrid) table).isPrintState()) {
				String lf = UIManager.getLookAndFeel().getName();
				if (lf.equals("Nimbus") || lf.startsWith("GTK")) {
					Color selFG = getSelectedTextColor();
					Color selBG = getSelectionColor();
					setUI(new javax.swing.plaf.basic.BasicEditorPaneUI());
					setSelectedTextColor(selFG);
					setSelectionColor(selBG);
				}
				super.setForeground(table.getSelectionForeground());
				super.setBackground(table.getSelectionBackground());
			} else {
				Color color = style.getForegroundColor();

				super.setForeground((color != null) ? color : table
						.getForeground());

				color = style.getBackground();

				super.setBackground((color != null) ? color : table
						.getBackground());
			}
			setBorder(noFocusBorder);

			if (hasFocus) {
				if (!isSelected && table.isCellEditable(row, column)) {
					Color color;
					color = UIManager.getColor("Table.focusCellForeground"); //$NON-NLS-1$
					if (color != null) {
						super.setForeground(color);
					}
					color = UIManager.getColor("Table.focusCellBackground"); //$NON-NLS-1$
					if (color != null) {
						super.setBackground(color);
					}
				}
			}

			return this;
		}

		private Rectangle getTextRect() {
			int l = getDocument().getLength();
			Rectangle r = null;
			try {
				r = modelToView(0);
				if (r != null) {
					for (int i = 1; i <= l; i++) {
						Rectangle r1 = modelToView(i);
						if (r1 != null) {
							r.width = Math.max(r.width, r1.x + r1.width);
							r.x = Math.min(r1.x, r.x);
						}
					}
					Rectangle r1 = modelToView(l - 1);
					if (r1 != null)
						r.height = r1.y + r1.height;
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			return r;
		}

		protected void setCellStyle(CellStyle style) {
			setWordWrap(style.isWrapLine());
			verticalAlignment = style.getVerticalAlignment();
			horizontalAlignment = style.getHorizontalAlignment();
			angle = style.getAngle();
		}

		@Override
		public void paintComponent(Graphics g) {
			if (!printState) {
				g.setColor(getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());
			}
			paintBorder(g);
			hasFocus = false;
			if (angle != 0) {
				paintRotate(g);
			} else {

				if (cell.getPicture() == null) {
					try {
						if (verticalAlignment == CellStyle.BOTTOM) {
							Rectangle r = modelToView(getStyledDocument()
									.getLength());
							if (r != null) {
								int p = r.y + r.height + rowMargin;
								g.translate(0, getHeight() - p);
							}
						} else if (verticalAlignment == CellStyle.CENTER) {
							Rectangle r = modelToView(getStyledDocument()
									.getLength());
							if (r != null) {
								int p = r.y + r.height + rowMargin;
								g.translate(0, (getHeight() - p) / 2);
							}
						} 
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
			}
			
			super.paintComponent(g);

			paintIcon(g);

		}

		private void paintIcon(Graphics g) {

			if (cell.getPicture() != null && cell.getPicture().getIcon() != null) {
				Graphics2D g2 = (Graphics2D) g;


				if (cell.isScaleIcon()) {
					cell.getPicture().paint(g2, getWidth(), getHeight());
				} else {

					int iconWidth = cell.getPicture().getWidth();
					int iconHeight = cell.getPicture().getHeight();
					double kx = 1.0;
					double ky = 1.0;
					if (printState) {
						kx = 1.0 / GraphicUtil.getScreenScaleX();
						ky = 1.0 / GraphicUtil.getScreenScaleY();
					}

					if (verticalAlignment == CellStyle.BOTTOM) {
						int p = (int) (iconHeight * ky + rowMargin);
						g.translate(0, getHeight() - p);
					} else if (verticalAlignment == CellStyle.CENTER) {
						int p = (int) (iconHeight * ky + rowMargin);
						g.translate(0, (getHeight() - p) / 2);
					}

					if (horizontalAlignment == CellStyle.RIGHT) {
						int p = (int) (iconWidth * kx + rowMargin);
						g.translate(getWidth() - p, 0);
					} else if (horizontalAlignment == CellStyle.CENTER) {
						int p = (int) (iconWidth * kx + rowMargin);
						g.translate((getWidth() - p) / 2, 0);
					}

					if (printState) {
						AffineTransform at = AffineTransform.getScaleInstance(
								kx, ky);
						AffineTransformOp aop = new AffineTransformOp(at,
								AffineTransformOp.TYPE_BICUBIC);
						cell.getPicture().paint(g2, aop);
					} else {
						cell.getPicture().paint(g2);
					}
				}
			}
		}

		private void paintRotate(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			int a = -angle;
			int cellWidth = getWidth();
			int cellHeight = getHeight();
			MutableAttributeSet attr = new SimpleAttributeSet();
			StyleConstants.setAlignment(attr, StyleConstants.ALIGN_LEFT);
			setParagraphAttributes(attr, true);

			if ((angle > 45 && angle < 135) || (angle > 225 && angle < 315)) {
				setSize(getHeight(), getWidth());
			}
			Rectangle r = getTextRect();
			if (r != null) {
				setSize(r.width + r.x + rowMargin, r.height + r.y + rowMargin);
				int x = 0;
				int y = 0;
				Point2D[] points = new Point2D[3];
				points[0] = Utilities.rotatePoint(x, y, x + getWidth(), y,
						Math.toRadians(a));
				points[1] = Utilities.rotatePoint(x, y, x + getWidth(), y
						+ getHeight(), Math.toRadians(a));
				points[2] = Utilities.rotatePoint(x, y, x, y + getHeight(),
						Math.toRadians(a));
				int miny = getHeight();
				int maxy = -miny;
				int minx = getWidth();
				int maxx = -minx;
				for (Point2D p : points) {
					miny = (int) Math.min(miny, p.getY());
					maxy = (int) Math.max(maxy, p.getY());
					minx = (int) Math.min(minx, p.getX());
					maxx = (int) Math.max(maxx, p.getX());
				}
				int x_ = 0;
				int y_ = 0;

				switch (verticalAlignment) {
				case CellStyle.TOP:
					y_ = -miny + rowMargin;
					break;
				case CellStyle.CENTER:
					y_ = cellHeight / 2 - maxy + (maxy - miny) / 2;
					break;
				default:// BOTTOM
					y_ = cellHeight - maxy;
				}

				switch (horizontalAlignment) {
				case CellStyle.LEFT:
				case CellStyle.JUSTIFY:
					x_ = -minx + rowMargin;
					break;
				case CellStyle.CENTER:
					x_ = cellWidth / 2 - maxx + (maxx - minx) / 2;
					break;
				default:// RIGHT
					x_ = cellWidth - maxx;
				}

				g2.translate(x_, y_);
				g2.rotate(Math.toRadians(a), x, y);
			}
		}

		protected void paintBorder(Graphics g) {
			if (hasFocus) {
				Border border = UIManager
						.getBorder("Table.focusCellHighlightBorder"); //$NON-NLS-1$
				if (border != null) {
					border.paintBorder(this, g, 0, 0, getWidth(), getHeight());
				}
			}
		}

		public int getTextHeight(ReportModel model, int row, int column) {
			int p = 0;
			if (cell.getPicture() != null && !cell.isScaleIcon()) {
				return (int) Math.round((double) cell.getPicture().getHeight()
						/ GraphicUtil.getScreenScaleY());
			} else if (cell.getValue() != null) {
				Rectangle oldR = getBounds();
				try {
					try {
						CellStyle style = model.getStyles(cell.getStyleId());
						setWordWrap(style.isWrapLine());
						setSize(model.getCellSize(cell, row, column, false));
						setText(cell.getText());
						StyledDocument doc = getStyledDocument();
						doc.setParagraphAttributes(0, doc.getLength(),
								style.getAttributeSet(), true);

						Rectangle r = modelToView(doc.getLength());
						if (r != null) {
							p = r.y + r.height + rowMargin;
						}
					} catch (Exception e1) {
						logger.log(Level.SEVERE, e1.getMessage(), e1);
						e1.printStackTrace();
					}
				} finally {
					setBounds(oldR);
				}
			}
			return p;
		}

		public void setWordWrap(boolean wrap) {

		}
	} // End AbstractReportRenderer

	public static class HTMLReportRenderer extends AbstractReportRenderer {

		private static final long serialVersionUID = -1L;

		public HTMLReportRenderer() {
			super();
			setContentType("text/html"); //$NON-NLS-1$
			setEditorKit(new NoWrapHTMLKit());
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			super.getTableCellRendererComponent(table, value, isSelected,
					hasFocus, row, column);

			String str = (value == null) ? "" : value.toString(); //$NON-NLS-1$

			if (!str.equals(getText())) {
				setText(str);
			}
			if (value != null) {
				CellStyle style = ((JReportGrid) table).getCellStyle(cell
						.getStyleId());
				HTMLDocument doc = (HTMLDocument) getDocument();
				AttributeSet attr = style.getAttributeSet();
				StyleConstants.setForeground((MutableAttributeSet) attr,
						getForeground());
				StyleConstants.setBackground((MutableAttributeSet) attr,
						getBackground());
				doc.setParagraphAttributes(0, str.length(), attr, true);
			}
			return this;
		}

		public void setWordWrap(boolean wrap) {
			if (getEditorKit() instanceof NoWrapHTMLKit) {
				NoWrapHTMLKit kit = (NoWrapHTMLKit) getEditorKit();
				kit.setWrap(wrap);
			}
		}

	} // End HTMLReportRenderer

	public static class TextReportRenderer extends AbstractReportRenderer {

		private static final long serialVersionUID = 1L;

		protected static final Insets insets = new Insets(2, 2, 2, 2);

		private ValueFormatter formatter = new ValueFormatter();

		public TextReportRenderer() {
			super();
			this.setMargin(insets);
			setEditorKit(new NoWrapEditorKit());
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			super.getTableCellRendererComponent(table, value, isSelected,
					hasFocus, row, column);

			CellStyle style = ((JReportGrid) table).getCellStyle(cell
					.getStyleId());

			String str;

			if (style.getDecimal() >= 0) {
				str = formatter.format(value, style.getDecimal());
			} else {
				str = formatter.format(value);
			}

			setText(str);

			if (value != null && str != null) {
				StyledDocument doc = getStyledDocument();
				AttributeSet attr = style.getAttributeSet();

				StyleConstants.setForeground((MutableAttributeSet) attr,
						getForeground());
				StyleConstants.setBackground((MutableAttributeSet) attr,
						getBackground());
				doc.setParagraphAttributes(0, str.length(), attr, true);
			}

			printState = ((JReportGrid) table).isPrintState();
			return this;
		}

		public void setWordWrap(boolean wrap) {
			if (getEditorKit() instanceof NoWrapEditorKit) {
				NoWrapEditorKit kit = (NoWrapEditorKit) getEditorKit();
				kit.setWrap(wrap);
			}
		}

	} // End TextReportRenderer

	/**
	 * PageNumber Renderer
	 */
	public static class PageNumberRenderer extends TextReportRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (value instanceof PageNumber) {
				((PageNumber) value).setValue(((JReportGrid) table)
						.getReportModel().getRowModel()
						.getPageNumber(row, column));
			}
			return super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
		}

	} // End PageNumberRenderer

	/**
	 * PageCount Renderer
	 */
	public static class PageCountRenderer extends TextReportRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (value instanceof PageCount) {
				((PageCount) value).setValue(((JReportGrid) table)
						.getReportModel().getRowModel()
						.getPageCount());
			}
			return super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
		}

	} // End PageCountRenderer

	public static class DateRenderer extends TextReportRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			if (value instanceof Date) {
				value = ReportBook.getDateFormatter().format(value);
			}
			return super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
		}

	} // End DateRenderer

	private void setLazyEditor(Class<?> c, String s) {
		setLazyValue(defaultEditorsByColumnClass, c, s);
	}

	protected void createDefaultEditors() {
		defaultEditorsByColumnClass = new UIDefaults();

		setLazyEditor(Object.class, "jdbreport.grid.JReportGrid$StyledEditor"); //$NON-NLS-1$

		// Booleans
		setLazyEditor(Boolean.class, "javax.swing.JTable$BooleanEditor"); //$NON-NLS-1$

		for (Class<?> c : ReportCell.defaultValuesByClass.keySet()) {
			CellValueInfo vi = ReportCell.defaultValuesByClass.get(c);
			if (vi != null) {
				setLazyEditor(vi.getCellValueClass(), vi.getEditorClass());
			}
		}
	}

	/**
	 * 
	 * Default editors
	 * 
	 */
	public static class StyledEditor extends ReportCellEditor {

		private static final long serialVersionUID = -8537839531049303511L;

		public StyledEditor() {
			super(Cell.TEXT_PLAIN);
		}
	}

	public static class HTMLEditor extends ReportCellEditor {

		private static final long serialVersionUID = -8537839531049303511L;

		public HTMLEditor() {
			super(Cell.TEXT_HTML);
		}

		@Override
		public Object getCellEditorValue() {
			return and.util.Utilities.html2Plain((String) super
					.getCellEditorValue());
		}

		public void setWordWrap(boolean wrap) {

			if (editorComponent.getEditorKit() instanceof NoWrapHTMLKit) {

				NoWrapHTMLKit kit = (NoWrapHTMLKit) editorComponent
						.getEditorKit();

				kit.setWrap(wrap);

			}
		}
	}

	class StringMetricsImpl implements StringMetrics {

		private FontMetrics fm;

		public void setStyle(CellStyle style) {
			Font font = Font.decode(style.getFamily() + "-" //$NON-NLS-1$
					+ CellStyle.fontStyleStr(style.getStyle()) + "-" //$NON-NLS-1$
					+ style.getSize());
			fm = getFontMetrics(font);
		}

		public int charsWidth(char[] data, int off, int len) {
			return fm.charsWidth(data, off, len);
		}

		public char[] toViewCharArray(Cell cell) {
			String text = getRenderedText(cell);
			if (text != null) {
				return text.toCharArray();
			} 
			return new char[0]; 
		}

	}

	public StringMetrics getStringMetrics() {
		return new StringMetricsImpl();
	}

	private static class ValueFormatter {

		public String formatFloating(Number value) {
			String v = value.toString();
			if (Utilities.getDecimalSeparator() != '.') {
				return v.replace('.', Utilities.getDecimalSeparator());
			}
			return v;
		}

		public String format(Object value) {
			if (value == null)
				return "";

			if (value instanceof Double) {
				return formatFloating((Double) value);
			} else if (value instanceof Float) {
				return formatFloating((Float) value);
			} else if (value instanceof BigDecimal) {
				return formatFloating((BigDecimal) value);
			}

			return value.toString();
		}

		public String format(Object value, int decimal) {
			if (value == null) {
				return "";
			}

			try {
				return format(new Double(value.toString().replace(',', '.')),
						decimal);
			} catch (Exception e) {
			}

			return format(value);
		}

		public String format(Double value, int decimal) {
			if (decimal == 0) {
				return String.valueOf(Math.round(value));
			}
			return String.format("%1." + decimal + "f", value);
		}

	}


} // End JReportGrid

