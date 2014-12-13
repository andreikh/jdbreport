/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2014 Andrey Kholmanskih
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
package jdbreport.design.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

import javax.swing.Icon;

import jdbreport.model.Border;
import jdbreport.model.Cell;
import jdbreport.model.CellCoord;
import jdbreport.model.CellStyle;
import jdbreport.model.GridRect;
import jdbreport.util.Utils;
import jdbreport.view.model.JReportModel;
import jdbreport.model.Picture;
import jdbreport.model.PictureFactory;
import jdbreport.model.ReportException;
import jdbreport.model.print.ReportPage;
import jdbreport.source.ArrayDataSet;
import jdbreport.source.IterableDataSet;
import jdbreport.source.IteratorDataSet;
import jdbreport.source.ObjectDataSet;
import jdbreport.source.ReportDataSet;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public abstract class AbstractCellFunction implements CellFunction {

	private TemplateBook provider;
	private Cell cell;
	private int column;
	private int row;

	public void setProvider(TemplateBook provider, int row, int column) {
		setProvider(provider);
		setPosition(row, column);
	}

	public void addColumns(int count, int index) {
		getModel().addColumns(count, index);
		CellCoord p = getModel().getCellPosition(cell);
		column = p.getColumn();
		row = p.getRow();
	}

	public void addRows(int count, int index) {
		getModel().addRows(count, index);
		CellCoord p = getModel().getCellPosition(cell);
		column = p.getColumn();
		row = p.getRow();
	}

	public void deleteCell(int row, int column) {
		getModel().deleteCell(row, column);
	}

	public Dimension getCellSize() {
		return getModel().getCellSize(getCell(), getRow(), getColumn());
	}

	public Dimension getCellSize(int row, int column) {
		return getModel().getCellSize(getCell(), row, column);
	}

	public int getColumnCount() {
		return getModel().getColumnCount();
	}

	public int getColumnWidth() {
		return getModel().getColumnWidth(getColumn());
	}

	public int getColumnWidth(int column) {
		return getModel().getColumnWidth(column);
	}

	public String getFontName() {
		CellStyle cellStyle = getModel().getStyles(getCell().getStyleId());
		return cellStyle.getFamily();
	}

	public String getFontName(int row, int column) {
		CellStyle cellStyle = getModel().getStyles(
				getCell(row, column).getStyleId());
		return cellStyle.getFamily();
	}

	public String getReportTitle() {
		return getModel().getReportTitle();
	}

	public int getRowCount() {
		return getModel().getRowCount();
	}

	public int getRowHeight() {
		return getModel().getRowHeight(getRow());
	}

	public int getRowHeight(int row) {
		return getModel().getRowHeight(row);
	}

	public void removeColumns(int count, int index) {
		getModel().removeColumns(count, index);
		CellCoord p = getModel().getCellPosition(cell);
		column = p.getColumn();
		row = p.getRow();
	}

	public void removeRows(int count, int index) {
		getModel().removeRows(count, index);
		CellCoord p = getModel().getCellPosition(cell);
		column = p.getColumn();
		row = p.getRow();
	}

	public void setBackground(Color color, int row, int column) {
		getModel().setBackground(new GridRect(row, column, row, column), color);
	}

	public void setBackground(Color color) {
		getModel().setBackground(
				new GridRect(getRow(), getColumn(), getRow(), getColumn()),
				color);
	}

	public void setBorder(Border line, boolean[] positions, int row, int column) {
		getModel().addBorder(row, column, row, column, positions, line);
	}

	public void setBorder(Border line, boolean[] positions) {
		getModel().addBorder(getRow(), getColumn(), getRow(), getColumn(),
				positions, line);
	}

	public void setColumnWidth(int width, int column) {
		getModel().getColumnModel().getColumn(column).setPreferredWidth(width);
	}

	public void setColumnWidth(int width) {
		getModel().getColumnModel().getColumn(getColumn())
				.setPreferredWidth(width);
	}

	public void setDecimal(int d, int row, int column) {
		getModel().setDecimals(new GridRect(row, column, row, column), d);
	}

	public void setDecimal(int d) {
		setDecimal(d, getRow(), getColumn());
	}

	public void setFont(String fontName, int style, int size, int row,
			int column) {
		GridRect rect = new GridRect(row, column, row, column);
		if (fontName != null) {
			getModel().setFontName(rect, fontName);
		}
		if (style >= 0) {
			getModel().setFontStyle(rect, style, true);
		}
		if (size > 0) {
			getModel().setFontSize(rect, size);
		}
	}

	public void setFont(String fontName, int style, int size) {
		setFont(fontName, style, size, getRow(), getColumn());
	}

	public void setForeground(Color color, int row, int column) {
		getModel().setForeground(new GridRect(row, column, row, column), color);
	}

	public void setForeground(Color color) {
		setForeground(color, getRow(), getColumn());
	}

	public void setHorizontalAlignment(int align, int row, int column) {
		getModel().setHorizontalAlignment(
				new GridRect(row, column, row, column), align);
	}

	public void setHorizontalAlignment(int align) {
		setHorizontalAlignment(align, getRow(), getColumn());
	}

	public void setRowBreak(int row, boolean b) {
		getModel().setRowBreak(row, b);
	}

	public void setReportTitle(String reportTitle) {
		getModel().setReportTitle(reportTitle);
	}

	public void setRowHeight(int height, int row) {
		getModel().setRowHeight(row, height);
	}

	public void setRowHeight(int height) {
		setRowHeight(height, getRow());
	}

	public void setSheetVisible(boolean visible) {
		getModel().setVisible(visible);
	}

	public void setStretchPage(boolean stretchPage) {
		getModel().setStretchPage(stretchPage);
	}

	public void setVerticalAlignment(int align, int row, int column) {
		getModel().setVerticalAlignment(new GridRect(row, column, row, column),
				align);
	}

	public void setVerticalAlignment(int align) {
		setVerticalAlignment(align, getRow(), getColumn());
	}

	public void unionCells(int topRow, int leftColumn, int bottomRow,
			int rightColumn) {
		getModel().unionCells(topRow, leftColumn, bottomRow, rightColumn);
	}

	private void setPosition(int row, int column) {
		this.row = row;
		this.column = column;
		this.cell = getModel().getReportCell(row, column);
	}

	public void setProvider(TemplateBook provider) {
		this.provider = provider;
	}

	public Cell getCell() {
		return cell;
	}

	public Cell getCell(int row, int column) {
		return getModel().getReportCell(row, column);
	}

	public int getColumn() {
		return column;
	}
	
	public String asSymbol(int c) {
		if (c > 25) {
			int i = c % 26;
			c = (c / 26) - 1;
			return String.valueOf((char)('A' + c)) + String.valueOf((char)('A' + i));
		}
		return String.valueOf((char)('A' + c));
	}

	public ReportDataSet getDataSet(Object key) {
		return provider.getDataSet(key);
	}

	public void setDataSet(ReportDataSet ds) {
		provider.setDataSet(ds);
	}

	public void setDataSet(String alias, Iterable<?> ds) {
		setDataSet(new IterableDataSet(alias, ds));
	}

	public void setDataSet(String alias, Iterator<?> ds) {
		setDataSet(new IteratorDataSet(alias, ds));
	}

	public void setDataSet(String alias, Object ds) {
		setDataSet(new ObjectDataSet(alias, ds));
	}

	public void setDataSet(String alias, Object[] ds) {
		setDataSet(new ArrayDataSet(alias, ds));
	}

	public int getRow() {
		return row;
	}

	public Object getVarValue(Object name) {
		return provider.getVarValue(name);
	}

	public Integer getVarValue(Object name, Integer def) {
		Object value = provider.getVarValue(name);
		if (value == null)
			return def;
		if (value instanceof Integer)
			return (Integer) value;
		try {
			return new Integer(value.toString());
		} catch (NumberFormatException e) {
			return def;
		}
	}

	public Double getVarValue(Object name, Double def) {
		Object value = provider.getVarValue(name);
		if (value == null)
			return def;
		if (value instanceof Double)
			return (Double) value;
		try {
			return new Double(value.toString());
		} catch (NumberFormatException e) {
			return def;
		}
	}

	public Boolean getVarValue(Object name, Boolean def) {
		Object value = provider.getVarValue(name);
		if (value == null)
			return def;
		if (value instanceof Boolean)
			return (Boolean) value;
		return Boolean.valueOf(value.toString());
	}

	public void runFunction(String functionName) throws ReportException {
		CellFunction cellFunction = provider.findCellFunction(functionName,
				row, column);
		if (cellFunction != null) {
			cellFunction.run();
		}
	}

	public void runFunction(String functionName, int row, int column)
			throws ReportException {
		int oldColumn = this.column;
		int oldRow = this.row;
		Cell oldCell = cell;
		try {
			CellFunction cellFunction = provider.findCellFunction(functionName,
					row, column);
			cellFunction.run();
		} finally {
			this.row = oldRow;
			this.column = oldColumn;
			cell = oldCell;
		}
	}

	public void setVarValue(Object name, Object value) {
		provider.setVarValue(name, value);
	}

	public String getText() {
		return getCell().getText();
	}

	public Object getValue() {
		return getCell().getValue();
	}

	public void setValue(Object value) {
		getCell().setValue(value);
	}

	public void setImage(Object value) {
		setImage(value, null);
	}

	public void setImage(Object value, String format) {
		if (value != null) {
			if (value instanceof byte[]) {
				Picture picture = PictureFactory.createPicture(format);
				picture.setBuf((byte[]) value);
				picture.setScale(getCell().isScaleIcon());
				getCell().setValue(picture);
			} else if (value instanceof String) {
				Picture picture = PictureFactory.createPicture(format);
				picture.setBuf(((String) value).getBytes());
				picture.setScale(getCell().isScaleIcon());
				getCell().setValue(picture);
			} else if (value instanceof Image || value instanceof Icon) {
				getCell().setValue(value);
			} else if (value instanceof InputStream) {
				try {
					Picture picture = PictureFactory.createPicture(format);
					picture.load((InputStream) value);
					picture.setScale(getCell().isScaleIcon());
					getCell().setValue(picture);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (value instanceof File) {
				try {
					if (format == null || format.length() == 0) {
						format = Utils.getFileExtension(((File) value));
					}
					Picture picture = PictureFactory.createPicture(format);
					picture.load((File) value);
					picture.setScale(getCell().isScaleIcon());
					getCell().setValue(picture);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			getCell().setValue(null);
		}
	}

	private JReportModel getModel() {
		return provider.newModel;
	}

	public ReportPage getReportPage() {
		return getModel().getReportPage();
	}

	public void setFormula(Object value) {
		if (value != null && jdbreport.model.math.MathValue.isEnableMathMl()) {
			try {
				if (value instanceof String) {
					jdbreport.model.math.MathML m = new jdbreport.model.math.MathML(
							(String) value);
					getCell().setValue(m);
				} else if (value instanceof Reader) {
					jdbreport.model.math.MathML m = new jdbreport.model.math.MathML(
							(Reader) value);
					getCell().setValue(m);
				} else if (value instanceof InputStream) {
					jdbreport.model.math.MathML m = new jdbreport.model.math.MathML(
							new InputStreamReader((InputStream) value, "UTF-8"));
					getCell().setValue(m);
				} else if (value instanceof File) {
                    try (FileReader reader = new FileReader((File) value)) {
                        jdbreport.model.math.MathML m = new jdbreport.model.math.MathML(
                                reader);
                        getCell().setValue(m);
                    }
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else {
			getCell().setValue(null);
		}

	}
	
}
