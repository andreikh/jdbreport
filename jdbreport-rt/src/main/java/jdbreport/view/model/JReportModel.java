/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2016 Andrey Kholmanskih
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
package jdbreport.view.model;

import jdbreport.model.*;
import jdbreport.model.print.ReportPage;
import jdbreport.util.GraphicUtil;
import jdbreport.util.Utils;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @version 3.1.2 31.03.2016
 * @author Andrey Kholmanskih
 * 
 */
public class JReportModel extends AbstractTableModel implements ReportModel {

	public static final String VERSION = "3.1.2";

	private static final long serialVersionUID = -2926872019856806971L;

	private TableRowModel rowModel;

	private TableColumnModel columnModel;

	private Map<Object, CellStyle> styleList;

	private String reportTitle;

	private double defaultColumnWidth = ReportColumn.DEFAULT_COLUMN_WIDTH;

	private ReportPage reportPage;

	private boolean rowSizing = true;

	private boolean colSizing = true;

	private boolean rowMoving = true;

	private boolean colMoving = true;

	private boolean editable = true;

	private PropertyChangeSupport changeSupport;

	private boolean stretchPage;

	private boolean visible = true;

	private int update;

	private boolean showHeader = true;

	private boolean showRowHeader = true;

	private static Units unit = Units.PT;

	public JReportModel(int rowCount, int columnCount,
			Map<Object, CellStyle> styleList) {
		rowModel = createRowModel();
		setColumnCount(columnCount);
		getRowModel().addRows(rowCount, 0);
		columnModel = createColumnModel();
		this.styleList = styleList;
	}

	/**
	 * Creates TableRowModel by default
	 * 
	 * @return TableRowModel
	 */
	protected TableRowModel createRowModel() {
		return new ReportRowModel();
	}

	public JReportModel(Map<Object, CellStyle> listStyle) {
		this(0, 4, listStyle);
	}

	public int addRows(int count, int index) {
		return getRowModel().addRows(count, index);
	}

    public void appendModel(ReportModel model) {
        Iterator<Group> it = model.getRowModel().getRootGroup().getGroupIterator();
        ((ReportRowModel)getRowModel()).clearPageHeader(0);
        while (it.hasNext()) {
            Group group = it.next();
            if (group.getType() != Group.ROW_PAGE_HEADER && group.getType() != Group.ROW_PAGE_FOOTER
                    && group.getRowCount() > 0) {
                getRowModel().appendGroup(group);
            }
        }
        updatePages(0);
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#removeRows(int, int)
	 */
	public void removeRows(int count, int index) {
		getRowModel().removeRows(count, index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#addColumns(int)
	 */
	public int addColumns(int count) {
		return addColumns(count, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#addColumns(int, int)
	 */
	public int addColumns(int count, int index) {
		getRowModel().startUpdate();
		try {
			if (index < 0) {
				index = getColumnCount();
			}
			for (int i = 0; i < count; i++) {
				TableColumn tc = createDefaultColumn(index + i);
				((ReportColumnModel) columnModel).addColumn(tc, index + i);
				getRowModel().setColCount(columnModel.getColumnCount());
				getRowModel().moveColumn(columnModel.getColumnCount() - 1,
						index + i);
			}
		} finally {
			getRowModel().endUpdate();
		}
		return getColumnCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#removeColumns(int, int)
	 */
	public void removeColumns(int count, int index) {
		if (index < 0 || count <= 0)
			return;
			if (count + index > columnModel.getColumnCount()) {
				count = columnModel.getColumnCount() - index;
			}
			for (int i = 0; i < count; i++) {
				columnModel.removeColumn(columnModel.getColumn(index));
				getRowModel().removeColumn(index);
			}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#getColumnCount()
	 */
	public int getColumnCount() {
		return rowModel.getColCount();
	}

	private TableColumnModel createColumnModel() {
		TableColumnModel cm = new ReportColumnModel();
		for (int i = 0; i < getColumnCount(); i++) {
			cm.addColumn(createDefaultColumn(i));
		}
		return cm;
	}

	public TableColumn createDefaultColumn(int column) {
		TableColumn tc = new ReportColumn(column, getDefaultColumnWidth());
		tc.setHeaderValue(getColumnName(column));
		return tc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#setColumnCount(int)
	 */
	public void setColumnCount(int value) {
		getRowModel().setColCount(value);
		if (columnModel != null) {
			if (value > columnModel.getColumnCount()) {
				for (int i = columnModel.getColumnCount(); i < value; i++)
					columnModel.addColumn(new ReportColumn(i,
							getDefaultColumnWidth()));
			} else if (value < columnModel.getColumnCount()) {
				for (int i = columnModel.getColumnCount() - 1; i >= value; i--)
					columnModel.removeColumn(columnModel.getColumn(i));
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#getRowCount()
	 */
	public int getRowCount() {
		return getRowModel().getRowCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
		return "" + (column + 1);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return isEditable()
				&& getReportCell(rowIndex, columnIndex).isEditable();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		return getRowModel().getRow(rowIndex).getCellItem(columnIndex)
				.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (rowIndex >= getRowModel().getRowCount()
				|| columnIndex >= getColumnCount())
			return;
		if (aValue instanceof Cell) {
			getRowModel().getRow(rowIndex).setCellItem((Cell) aValue,
					columnIndex);
			return;
		}
		Cell cell = getRowModel().getRow(rowIndex).getCellItem(columnIndex);
		if (cell.isNull()) {
			if (aValue != null)
				createReportCell(rowIndex, columnIndex).setValue(aValue);
		} else {
			if ((aValue != null && !aValue.equals(cell.getValue()))
					|| cell.getValue() != null
					&& !cell.getValue().equals(aValue))
				cell.setValue(aValue);
		}
		if (cell.getContentType().equals(Cell.TEXT_HTML)) {
			cell.setValue(Utils.html2Plain(cell.getText()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#getReportCell(int, int)
	 */
	public Cell getReportCell(int row, int column) {
		return getRowModel().getRow(row).getCellItem(column);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#getOwnerReportCell(int, int)
	 */
	public Cell getOwnerReportCell(int row, int column) {
		Cell cell = getReportCell(row, column);
		if (cell.isChild())
			cell = cell.getOwner();
		return cell;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#createReportCell(int, int)
	 */
	public Cell createReportCell(int row, int column) {
		return getRowModel().getRow(row).createCellItem(column);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#getOwnerRow(jdbreport.interfaces.Cell,
	 * int, int)
	 */
	public int getOwnerRow(Cell cell, int row, int column) {
		if (cell == null || cell.getOwner() == null)
			return row;
		Cell ownerCell = cell.getOwner();
		if (ownerCell.getRowSpan() > 0) {
			Cell childCell = getReportCell(row, column);
			while (childCell.getOwner() == ownerCell)
				childCell = getReportCell(--row, column);
			if (childCell != ownerCell)
				row++;
			return row;
		} else
			return row;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jdbreport.model.ReportModel#getOwnerColumn(jdbreport.interfaces.Cell,
	 * int, int)
	 */
	public int getOwnerColumn(Cell cell, int row, int column) {
		if (cell == null || cell.getOwner() == null
				|| cell.getOwner().getColSpan() == 0)
			return column;
		Cell ownerCell = cell.getOwner();
		Cell chilCell = getReportCell(row, column);
		while (chilCell.getOwner() == ownerCell)
			chilCell = getReportCell(row, --column);
		if (chilCell != ownerCell)
			column++;
		return column;
	}

	public Dimension getCellSize(Cell cell, int row, int column) {
		return getCellSize(cell, row, column, true);
	}

	public Dimension getCellSize(Cell cell, int row, int column,
			boolean includeSpacing) {
		Dimension result = new Dimension();
		if (!cell.isNull()) {
			if (cell.isChild()) {
				row = getOwnerRow(cell, row, column);
				column = getOwnerColumn(cell, row, column);
				cell = cell.getOwner();
			}
			result.height = getRowHeight(row);
			result.width = getColumnWidth(column);
			if (cell.isSpan()) {
				for (int r = 1; r <= cell.getRowSpan(); r++)
					result.height += getRowHeight(row + r);
				for (int c = 1; c <= cell.getColSpan(); c++) {
					result.width += getColumnWidth(column + c);
				}
			}
		} else {
			result.height = getRowHeight(row);
			result.width = getColumnWidth(column);
		}
		if (!includeSpacing) {
			int rm = 2;
			int cm = getColumnModel().getColumnMargin();
			result.height -= rm;
			result.width -= cm;
		}
		return result;
	}

	public int getCellHeight(Cell cell, int row, int column) {
		int height;
		if (!cell.isNull()) {

			if (cell.isChild()) {
				row = getOwnerRow(cell, row, column);
				cell = cell.getOwner();
			}
			height = getRowHeight(row);
			if (cell.isSpan()) {
				for (int r = 1; r <= cell.getRowSpan(); r++)
					height += getRowHeight(row + r);
			}
		} else {
			height = getRowHeight(row);
		}
		return height;
	}

	public TableRowModel getRowModel() {
		return rowModel;
	}

	public void removeRows() {
		getRowModel().removeRows();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#getColumnWidth(int)
	 */
	public int getColumnWidth(int column) {

		if (columnModel != null && column >= 0
				&& column < columnModel.getColumnCount()) {
			return columnModel.getColumn(column).getPreferredWidth();
		} else
			return 0;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#getRowHeight(int)
	 */
	public int getRowHeight(int row) {
		return getRowModel().getRow(row).getHeight();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#setRowHeight(int, int)
	 */
	public void setRowHeight(int row, int rowHeight) {
		getRowModel().setRowHeight(row, rowHeight);
	}

	public TableColumnModel getColumnModel() {
		return columnModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#unionCells(int, int, int, int)
	 */
	public void unionCells(int topRow, int leftColumn, int bottomRow,
			int rightColumn) {
		getRowModel().unionCells(topRow, leftColumn, bottomRow, rightColumn);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#clearUnion(int, int)
	 */
	public void clearUnion(int row, int column) {
		Cell cell = getReportCell(row, column);
		if (cell.isSpan()) {
			getRowModel().clearUnion(row, column, row + cell.getRowSpan(),
					column + cell.getColSpan());
		}
	}

	public CellStyle getStyles(Object index) {
		if (index != null) {
			Object o = getStyleList().get(index);
			if (o != null)
				return (CellStyle) o;
		}
		return CellStyle.getDefaultStyle();
	}

	public Object addStyle(CellStyle style) {
		for (Object key : getStyleList().keySet()) {
			if (getStyleList().get(key).equals(style))
				return key;
		}
		int id = getStyleList().size();
		while (getStyleList().containsKey(id)) {
			id++;
		}
		style.setId(id);
		getStyleList().put(id, style);
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#addBorder(int, int, int, int, boolean[],
	 * jdbreport.model.Border)
	 */
	public void addBorder(int topRow, int leftColumn, int bottomRow,
			int rightColumn, boolean[] positions, Border line) {
		if (positions[Border.LINE_LEFT]) {
			for (int row = topRow; row <= bottomRow; row++) {
				setLineIndex(row, leftColumn, Border.LINE_LEFT, line);
			}
		}
		if (positions.length > Border.LINE_LEFT && positions[Border.LINE_TOP]) {
			for (int column = leftColumn; column <= rightColumn; column++) {
				setLineIndex(topRow, column, Border.LINE_TOP, line);
			}
		}
		if (positions.length > Border.LINE_TOP && positions[Border.LINE_RIGHT]) {
			for (int row = topRow; row <= bottomRow; row++) {
				setLineIndex(row, rightColumn, Border.LINE_RIGHT, line);
			}
		}
		if (positions.length > Border.LINE_RIGHT &&  positions[Border.LINE_BOTTOM]) {
			for (int column = leftColumn; column <= rightColumn; column++) {
				setLineIndex(bottomRow, column, Border.LINE_BOTTOM, line);
			}
		}
		if (positions.length > Border.LINE_BOTTOM && positions[Border.LINE_VMIDDLE]) {
			for (int column = leftColumn; column < rightColumn; column++) {
				for (int row = topRow; row <= bottomRow; row++) {
					setLineIndex(row, column, Border.LINE_RIGHT, line);
				}
			}
		}
		if (positions.length > Border.LINE_VMIDDLE && positions[Border.LINE_HMIDDLE]) {
			for (int column = leftColumn; column <= rightColumn; column++) {
				for (int row = topRow; row < bottomRow; row++) {
					setLineIndex(row, column, Border.LINE_BOTTOM, line);
				}
			}
		}
	}

	private void setLineIndex(int row, int column, byte position, Border line) {
		Cell cell = getReportCell(row, column);
		if (cell.isNull()) {
			if (line != null) {
				cell = createReportCell(row, column);
			} else
				return;
		}
		CellStyle newStyle = getStyles(cell.getStyleId()).deriveBorder(
				position, line);
		cell.setStyleId(addStyle(newStyle));
	}

	public void setHorizontalAlignment(GridRect selRect, int align) {
		Iterator<Cell> it = getSelectedCells(selRect);
		while (it.hasNext()) {
			Cell cell = it.next();
			CellStyle style = getStyles(cell.getStyleId());
			Object index = addStyle(style.deriveHAlign(align));
			cell.setStyleId(index);
		}
	}

	public void setVerticalAlignment(GridRect selRect, int align) {
		Iterator<Cell> it = getSelectedCells(selRect);
		while (it.hasNext()) {
			Cell cell = it.next();
			CellStyle style = getStyles(cell.getStyleId());
			Object index = addStyle(style.deriveVAlign(align));
			cell.setStyleId(index);
		}
	}

	public void setFontName(GridRect selRect, String fontName) {
		Iterator<Cell> it = getSelectedCells(selRect);
		while (it.hasNext()) {
			Cell cell = it.next();
			CellStyle style = getStyles(cell.getStyleId());
			Object index = addStyle(style.deriveFont(fontName));
			cell.setStyleId(index);
		}
	}

	public void setFontSize(GridRect selRect, int fontSize) {
		Iterator<Cell> it = getSelectedCells(selRect);
		while (it.hasNext()) {
			Cell cell = it.next();
			CellStyle style = getStyles(cell.getStyleId());
			Object index = addStyle(style.deriveFont((float) fontSize));
			cell.setStyleId(index);
		}
	}

	public void setFontStyle(GridRect selRect, int fontStyle, boolean enable) {
		Iterator<Cell> it = getSelectedCells(selRect);
		while (it.hasNext()) {
			Cell cell = it.next();
			CellStyle style = getStyles(cell.getStyleId());
			int styleIndex = style.getStyle();
			styleIndex = enable ? (styleIndex | fontStyle)
					: (styleIndex & ~fontStyle);
			Object index = addStyle(style.deriveFont(styleIndex));
			cell.setStyleId(index);
		}
	}

	public boolean isFontStyle(int row, int column, int style) {
		CellStyle cellStyle = getStyles(getReportCell(row, column).getStyleId());
		return (cellStyle != null && (cellStyle.getStyle() & style) != 0);
	}

	public void setBackground(GridRect selectionRect, Color color) {
		Iterator<Cell> it = getSelectedCells(selectionRect);
		while (it.hasNext()) {
			Cell cell = it.next();
			CellStyle style = getStyles(cell.getStyleId());
			cell.setStyleId(addStyle(style.deriveBackground(color)));
		}
	}

	public void setForeground(GridRect selectionRect, Color color) {
		Iterator<Cell> it = getSelectedCells(selectionRect);
		while (it.hasNext()) {
			Cell cell = it.next();
			CellStyle style = getStyles(cell.getStyleId());
			cell.setStyleId(addStyle(style.deriveForeground(color)));
		}
	}

	public void setDecimals(GridRect selectionRect, int d) {
		Iterator<Cell> it = getSelectedCells(selectionRect);
		while (it.hasNext()) {
			Cell cell = it.next();
			CellStyle style = getStyles(cell.getStyleId());
			cell.setStyleId(addStyle(style.deriveFormat(d)));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#setReportTitle(java.lang.String)
	 */
	public void setReportTitle(String reportTitle) {
		String old = this.reportTitle;
		this.reportTitle = reportTitle;
		firePropertyChange("reportTitle", old, reportTitle);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#getReportTitle()
	 */
	public String getReportTitle() {
		return reportTitle == null ? "" : reportTitle;
	}

	public void setStyleList(Map<Object, CellStyle> list) {
		this.styleList = list;
	}

	public Map<Object, CellStyle> getStyleList() {
		return styleList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#setDefaultColumnWidth(int)
	 */
	public void setDefaultColumnWidth(int defaultColumnWidth) {
		this.defaultColumnWidth = unit.setXPixels(defaultColumnWidth);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#getDefaultColumnWidth()
	 */
	public int getDefaultColumnWidth() {
		return unit.getXPixels(defaultColumnWidth);
	}

	/**
	 * @param reportPage
	 *            The reportPage to set.
	 */
	public void setReportPage(ReportPage reportPage) {
		this.reportPage = reportPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#getReportPage()
	 */
	public ReportPage getReportPage() {
		if (reportPage == null) {
			reportPage = new ReportPage();
		}
		return reportPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#setRowSizing(boolean)
	 */
	public void setRowSizing(boolean b) {
		rowSizing = b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#isRowSizing()
	 */
	public boolean isRowSizing() {
		return rowSizing;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#setColSizing(boolean)
	 */
	public void setColSizing(boolean b) {
		if (this.colSizing != b) {
			boolean old = this.colSizing;
			this.colSizing = b;
			firePropertyChange("colSizing", old, this.colSizing);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#isColSizing()
	 */
	public boolean isColSizing() {
		return colSizing;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#setRowMoving(boolean)
	 */
	public void setRowMoving(boolean b) {
		this.rowMoving = b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#isRowMoving()
	 */
	public boolean isRowMoving() {
		return rowMoving;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#setColMoving(boolean)
	 */
	public void setColMoving(boolean b) {
		if (this.colMoving != b) {
			boolean old = this.colMoving;
			this.colMoving = b;
			firePropertyChange("colMoving", old, this.colMoving);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#isColMoving()
	 */
	public boolean isColMoving() {
		return colMoving;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#setEditing(boolean)
	 */
	public void setEditable(boolean b) {
		this.editable = b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#isEditing()
	 */
	public boolean isEditable() {
		return editable;
	}

	public synchronized void addPropertyChangeListener(
			PropertyChangeListener listener) {
		if (listener == null) {
			return;
		}
		if (changeSupport == null) {
			changeSupport = new PropertyChangeSupport(this);
		}
		changeSupport.addPropertyChangeListener(listener);
	}

	public synchronized void removePropertyChangeListener(
			PropertyChangeListener listener) {
		if (listener == null || changeSupport == null) {
			return;
		}
		changeSupport.removePropertyChangeListener(listener);
	}

	public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
		if (changeSupport == null) {
			return new PropertyChangeListener[0];
		}
		return changeSupport.getPropertyChangeListeners();
	}

	public synchronized void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		if (listener == null) {
			return;
		}
		if (changeSupport == null) {
			changeSupport = new PropertyChangeSupport(this);
		}
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public synchronized void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		if (listener == null || changeSupport == null) {
			return;
		}
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

	public synchronized PropertyChangeListener[] getPropertyChangeListeners(
			String propertyName) {
		if (changeSupport == null) {
			return new PropertyChangeListener[0];
		}
		return changeSupport.getPropertyChangeListeners(propertyName);
	}

	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		PropertyChangeSupport changeSupport = this.changeSupport;
		if (changeSupport == null
				|| (oldValue != null && newValue != null && oldValue
						.equals(newValue))) {
			return;
		}
		changeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	protected void firePropertyChange(String propertyName, boolean oldValue,
			boolean newValue) {
		PropertyChangeSupport changeSupport = this.changeSupport;
		if (changeSupport == null || oldValue == newValue) {
			return;
		}
		changeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	protected void firePropertyChange(String propertyName, int oldValue,
			int newValue) {
		PropertyChangeSupport changeSupport = this.changeSupport;
		if (changeSupport == null || oldValue == newValue) {
			return;
		}
		changeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Checks use of style in model
	 * 
	 * @param styleId
	 *            the identifier of style
	 * @return true if style is used
	 */
	public boolean findStyleID(Object styleId) {
		for (int row = 0; row < getRowCount(); row++) {
			for (int column = 0; column < getColumnCount(); column++) {
				Object index = getReportCell(row, column).getStyleId();
				if (index != null && index.equals(styleId)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Replaces styles
	 * 
	 * @param oldStyleId
	 *            old style's id
	 * @param newStyleId
	 *            new style's id
	 */
	public void replaceStyleID(Object oldStyleId, Object newStyleId) {
		for (int row = 0; row < getRowCount(); row++) {
			for (int column = 0; column < getColumnCount(); column++) {
				Cell cell = getReportCell(row, column);
				if (cell.isChild() || cell.isNull()) continue;
				Object index = cell.getStyleId();
				if (index != null && index.equals(oldStyleId)) {
					cell.setStyleId(newStyleId);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#setStretchPage(boolean)
	 */
	public void setStretchPage(boolean stretchPage) {
		this.stretchPage = stretchPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.ReportModel#isStretchPage()
	 */
	public boolean isStretchPage() {
		return stretchPage;
	}

	public boolean isRowBreak(int row) {
		return getRowModel().getRow(row).isPageBreak();
	}

	public void setRowBreak(int row, boolean end) {
		getRowModel().getRow(row).setPageBreak(end);
	}

	public boolean isColumnBreak(int column) {
		TableColumn reportColumn = getColumnModel().getColumn(column);
        return reportColumn instanceof ReportColumn && ((ReportColumn) reportColumn).isPageBreak();
    }

	public void setColumnBreak(int column, boolean end) {
		if (column < 0 || column >= getColumnModel().getColumnCount())
			return;
		TableColumn reportColumn = getColumnModel().getColumn(column);
		if (reportColumn instanceof ReportColumn) {
			((ReportColumn) reportColumn).setPageBreak(end);
		}
	}

	public Iterator<Cell> getSelectedCells(GridRect rect) {
		return new SelectedCellIterator(rect);
	}

	private class SelectedCellIterator implements Iterator<Cell> {

		GridRect rect;

		Cell current = null;

		int row;

		int column;

		public SelectedCellIterator(GridRect rect) {
			this.rect = rect;
			if (rect != null) {
				row = rect.getTopRow();
				column = rect.getLeftCol();
			}
		}

		public boolean hasNext() {
			if (rect == null)
				return false;
			if (current != null)
				return true;
			while (row <= rect.getBottomRow() && column <= rect.getRightCol()) {
				Cell cell = createReportCell(row, column);
				column++;
				if (column > rect.getRightCol()) {
					column = rect.getLeftCol();
					row++;
				}
				if (!cell.isChild()) {
					current = cell;
					return true;
				}
			}
			return false;
		}

		public Cell next() {
			if (current != null) {
				Cell result = current;
				current = null;
				return result;
			}
			throw new NoSuchElementException();
		}

		public void remove() {
			throw new IllegalStateException();
		}

	}

	public CellWrap getCellWrap(int row, int column) {
		return new ReportCellWrap(row, column, getReportCell(row, column));
	}

	public static class ReportCellWrap implements CellWrap {

		private int row;

		private int column;

		private Cell cell;

		public ReportCellWrap(int row, int column, Cell cell) {
			super();
			this.row = row;
			this.column = column;
			this.cell = cell;
		}

		public int getRow() {
			return row;
		}

		public int getColumn() {
			return column;
		}

		public Cell getCell() {
			return cell;
		}
	}

	public String getToolTipText(Point point) {
		int row = getRowModel().getRowIndexAtY(point.y);
		int column = getColumnModel().getColumnIndexAtX(point.x);
		return getToolTipText(row, column);
	}

	public String getToolTipText(int row, int column) {
		return null;
	}

	public void deleteCell(int row, int column) {
		TableRow tableRow = getRowModel().getRow(row);
		Cell cell = tableRow.removeCell(column);
		if (cell.isSpan()) {
			getRowModel().clearUnion(row, column, row + cell.getRowSpan(),
					column + cell.getColSpan());
			if (cell.getOwner() != null) {
				tableRow.createCellItem(column).setOwner(cell.getOwner());
			}
		}
	}

	public void delete(GridRect selectionRect) {
		for (int row = selectionRect.getTopRow(); row <= selectionRect
				.getBottomRow(); row++) {
			TableRow tableRow = getRowModel().getRow(row);
			for (int column = selectionRect.getLeftCol(); column <= selectionRect
					.getRightCol(); column++) {
				Cell cell = tableRow.removeCell(column);
				if (cell.isSpan()) {
					getRowModel()
							.clearUnion(row, column, row + cell.getRowSpan(),
									column + cell.getColSpan());
					if (cell.getOwner() != null) {
						tableRow.createCellItem(column).setOwner(
								cell.getOwner());
					}
				}
			}
		}
	}

	public Rectangle getCellRect(int row, int column, boolean includeSpacing,
			boolean isLeftToRight) {
		Rectangle r = new Rectangle();
		boolean valid = true;
		Cell cell = getReportCell(row, column);
		if (cell.isChild()) {
			row = getOwnerRow(cell, row, column);
			column = getOwnerColumn(cell, row, column);
			cell = cell.getOwner();
		}

		Dimension rc = getCellSize(cell, row, column);
		if (row < 0) {
			// y = height = 0;
			valid = false;
		} else if (row >= getRowCount()) {
			r.y = getRowModel().getTotalRowHeight();
			valid = false;
		} else {
			r.height = rc.height;
			for (int i = 0; i < row; i++) {
				r.y += getRowHeight(i);
			}
		}

		if (column < 0) {
			if (!isLeftToRight) {
				r.x = getColumnModel().getTotalColumnWidth();
			}
			// otherwise, x = width = 0;
			valid = false;
		} else if (column >= getColumnCount()) {
			if (isLeftToRight) {
				r.x = getColumnModel().getTotalColumnWidth();
			}
			// otherwise, x = width = 0;
			valid = false;
		} else {
			TableColumnModel cm = getColumnModel();
			if (isLeftToRight) {
				for (int i = 0; i < column; i++) {
					r.x += cm.getColumn(i).getWidth();
				}
			} else {
				for (int i = cm.getColumnCount() - 1; i > column; i--) {
					r.x += cm.getColumn(i).getWidth();
				}
			}
			r.width = rc.width;
		}

		if (valid && !includeSpacing) {
			int rm = 2;
			int cm = getColumnModel().getColumnMargin();
			r
					.setBounds(r.x + cm / 2, r.y + rm / 2, r.width - cm,
							r.height - rm);
		}
		return r;
	}

	public void updateCarryRows(HeightCalculator hCalc, int row, int column) {
		Cell cell = getReportCell(row, column);
		if (!cell.isChild() && getStyles(cell.getStyleId()).getCarryRows() > 0
				&& cell.getValue() != null) {
			calcRowWidths(hCalc.getStringMetrics(), cell, row, column);
		}
	}

	public void updateRowHeight(HeightCalculator hCalc, int row, int column) {
		Cell cell = getReportCell(row, column);
		if (!cell.isChild() && getStyles(cell.getStyleId()).isAutoHeight()
				&& (cell.getValue() != null || cell.getPicture() != null)) {
			int newH = hCalc.calcRowHeight(this, cell, row, column);
			int oldH = getCellHeight(cell, row, column);
			int delta = newH - oldH;
			if (delta > 0) {
				setRowHeight(row, getRowHeight(row) + delta);
			}
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		if (visible != this.visible) {
			boolean old = this.visible;
			this.visible = visible;
			firePropertyChange("visible", old, visible);
		}
	}

	public boolean isCanUpdatePages() {
		return getRowModel().isCanUpdatePages();
	}

	public void setCanUpdatePages(boolean b) {
		getRowModel().setCanUpdatePages(b);
	}

	public void updatePages(int startRow) {
		if (isUpdate())
			return;

		getRowModel().updatePages(startRow,
				Units.PT.getYPixels((int)getReportPage().getImageableHeight()));

		TableColumnModel cm = getColumnModel();
		int pageWidth = Units.PT.getYPixels((int)getReportPage().getImageableWidth());
		int w = 0;
		for (int c = 0; c < cm.getColumnCount(); c++) {
			ReportColumn column = (ReportColumn) cm.getColumn(c);
			w += column.getWidth();
			if (column.isPageBreak()) {
				((ReportRowModel) getRowModel()).addPageColumn(c);
				w = 0;
			} else 
				if (!getReportPage().isShrinkWidth()) {
					if (w > pageWidth && c > 0) {
						((ReportRowModel) getRowModel()).addPageColumn(c - 1);
						w = column.getWidth();
					}
				}
		}
	}

	public CellCoord getCellPosition(Cell cell) {
		if (!cell.isNull()) {
			TableRowModel rowModel = getRowModel(); 
			for (int row = 0; row < getRowCount(); row++) {
				TableRow tableRow = rowModel.getRow(row); 
				for (int column = 0; column < tableRow.getColCount(); column++) {
					if (tableRow.getCellItem(column) == cell) {
						return new CellCoord(row, column);
					}
				}
			}
		}
		return new CellCoord(-1, -1);

	}

	public void startUpdate() {
		update++;
		getRowModel().startUpdate();
	}

	public void endUpdate() {
		if (update > 0)
			update--;
		getRowModel().endUpdate();
	}

	public boolean isUpdate() {
		return update > 0;
	}

	private boolean inCharSet(char c) {
		return (c == ' ' || c == '-' || c == '\t' || c == '\n' || c == '\r');
	}

	public int calcRowWidths(StringMetrics metrics, Cell cell, int row,
			int column) {
		CellStyle style = getStyles(cell.getStyleId());
		metrics.setStyle(style);
		char[] buf = metrics.toViewCharArray(cell);
		int maxCount = style.getCarryRows();
		int b = 0;
		int e = 0;
		int i = 0;
		int count = 0;
		int w;
		Dimension size = getCellSize(cell, row, column, false);
		while (i < buf.length && count < maxCount) {
			boolean in = false;
			while (i < buf.length) {
				in = inCharSet(buf[i]);
				if (!in) {
					i++;
				} else
					break;
			}
			int newLine = 0;
			if (in) {
				if (buf[i] == '\n' || buf[i] == '\r') {
					newLine++;
				}
				i++;
				if (buf[i - 1] == '\n' && buf[i] == '\r') {
					newLine++;
					i++;
				}
			}
			w = metrics.charsWidth(buf, b, i - b);
			if (w < size.width && newLine == 0) {
				e = i;
			} else {
				if (e > b && newLine == 0) {
					i = e;
				} else
					e = i;
				cell.setValue(new String(buf, b, i - b - newLine));
				row++;
				count++;
				cell = getReportCell(row, column);
				if (cell.isChild()) {
					column = getOwnerColumn(cell, row, column);
					cell = cell.getOwner();
				}
				metrics.setStyle(getStyles(cell.getStyleId()));
				size = getCellSize(cell, row, column, false);
				b = e;
			}
		}
		if (b < buf.length - 1) {
			cell.setValue(new String(buf, b, buf.length - b));
		}
		return count;
	}

	public boolean isShowHeader() {
		return showHeader;
	}

	public boolean isShowRowHeader() {
		return showRowHeader;
	}

	public void setShowHeader(boolean b) {
		boolean old = this.showHeader;
		if (old != b) {
			this.showHeader = b;
			firePropertyChange("showHeader", old, this.showHeader);
		}
	}

	public void setShowRowHeader(boolean b) {
		boolean old = this.showRowHeader;
		if (old != b) {
			this.showRowHeader = b;
			firePropertyChange("showRowHeader", old, this.showRowHeader);
		}
	}

	public String getCellText(Cell cell) {
		CellStyle style = getStyles(cell.getStyleId());
		
		if (cell.getValue() instanceof Date) {
			return ReportBook.getDateFormatter().format(cell.getValue());
		} else {
			String str = cell.getText();
			if (str != null && style.getDecimal() > 0) {
				try {
					str = Utils.roundStr(new Double(str.replace(',', '.')),
							style.getDecimal());
				} catch (Exception ignored) {
				}
			}
			return str;
		}
		
	}

	public void setColumnWidths(int[] widths) {
		TableColumnModel cm = getColumnModel();
		int l = cm.getColumnCount();
		for (int c = 0; c < widths.length && c < l; c++) {
			if (widths[c] >= 0)
				cm.getColumn(c).setWidth(widths[c]);
		}
	}

	public void updateRowAndPageHeight(HeightCalculator hCalc) {
		getRowModel().startUpdate();
		double scaleY = GraphicUtil.getScaleY();
		double scaleX = GraphicUtil.getScaleX();
		try {
			GraphicUtil.setScaleX(1.0);
			GraphicUtil.setScaleY(1.0);
			if (hCalc != null) {

				for (int row = 0; row < getRowCount(); row++) {
					for (int column = 0; column < getColumnCount(); column++) {
						updateCarryRows(hCalc, row, column);
						updateRowHeight(hCalc, row, column);
					}
				}
			}
			boolean old = isCanUpdatePages();
			setCanUpdatePages(true);
			try {
				getRowModel().updatePages(
						0,
						Units.PT.getYPixels(getReportPage()
								.getImageableHeight()));
			} finally {
				setCanUpdatePages(old);
			}
		} finally {
			GraphicUtil.setScaleX(scaleX);
			GraphicUtil.setScaleY(scaleY);
			getRowModel().endUpdate();
		}

	}

	public int findRightColumn(int leftCol) {
		int columnCount = getColumnCount();
		for (int col = leftCol; col < columnCount; col++) {
			if (isColumnBreak(col)) {
				return col;
			}
		}
		return columnCount - 1;
	}

	public boolean isLastRowInPage(int row) {
		TableRow tableRow = getRowModel().getRow(row);
		if (tableRow.isPageBreak())
			return true;
		if (isCanUpdatePages()) {
			RowsGroup group = tableRow.getGroup();
			TableRow nextRow = getRowModel().getRow(row + 1);
			RowsGroup nextGroup = nextRow.getGroup();
			if (group != null && nextGroup != null && group != nextGroup) {
				if (group.getType() == Group.ROW_PAGE_FOOTER
						&& nextGroup.getType() != Group.ROW_FOOTER) {
					return true;
				}
				if (nextGroup.getType() == Group.ROW_PAGE_HEADER
						&& group.getType() != Group.ROW_TITLE) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isPrintLeftToRight() {
		return getRowModel().isPrintLeftToRight();
	}

	public void setPrintLeftToRight(boolean value) {
		getRowModel().setPrintLeftToRight(value);
	}

}
