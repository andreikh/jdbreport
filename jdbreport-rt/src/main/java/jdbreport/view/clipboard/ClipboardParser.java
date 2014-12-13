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
package jdbreport.view.clipboard;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.table.TableColumnModel;

import jdbreport.model.Cell;
import jdbreport.model.CellStyle;
import jdbreport.model.GridRect;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportColumn;
import jdbreport.model.ReportModel;
import jdbreport.model.TableRow;
import jdbreport.model.Units;
import jdbreport.model.io.SaveReportException;
import jdbreport.model.io.xml.CellParser;
import jdbreport.model.io.xml.DBReportParser;
import jdbreport.model.io.xml.DefaultReportParser;
import jdbreport.model.io.xml.StyleReportParser;

import org.xml.sax.Attributes;

/**
 * @version 3.0 13.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class ClipboardParser extends DefaultReportParser {

	private CellParser cellHandler;
	private GridRect selectionRect;
	private Map<Object, Object> recodeMap;
	private boolean inSheet;
	private boolean inRow;
	private boolean inCell;
	private int rowIndex;
	private boolean inRows;
	private boolean inCols;
	private boolean inColCount;
	private int startCol;
	private int oldColCount;
	private int oldRowCount;
	private static Units unit = Units.MMx10;

	public ClipboardParser() {
		super(null);
	}

	public ClipboardParser(FragmentHandler reportHandler, int startRow,
			int startCol) {
		super(reportHandler);
		this.startCol = startCol;
		rowIndex = startRow;
		oldRowCount = reportHandler.getReportModel().getRowCount();
		oldColCount = reportHandler.getReportModel().getColumnCount();
	}

	public boolean startElement(String name, Attributes attributes) {
		if (name.equals(STYLES)) {
			getHandler().pushHandler(
					new ClipStyleParser(getDefaultReportHandler()) {

						protected Object appendStyle(CellStyle style) {
							Object oldId = style.getId();
							Object newId = super.appendStyle(style);
							if (!newId.equals(oldId)) {
								getRecodeMap().put(oldId, newId);
							}
							return newId;
						}

					});
			return true;
		}
		if (name.equals(SHEET)) {
			inSheet = true;
			return true;
		}
		if (inSheet) {
			if (inRow) {
				if (name.equals(CELL)) {
					inCell = true;
					int col = Integer.parseInt(attributes.getValue("c"))
							+ startCol;
					Cell cell = getReportModel()
							.createReportCell(rowIndex, col);
					cell.clear();
					String cSpan = attributes.getValue(DBReportParser.CSPAN);
					if (cSpan != null && cSpan.length() > 0) {
						cell.setColSpan(Integer.parseInt(cSpan) - 1);
					}
					String rSpan = attributes.getValue(DBReportParser.RSPAN);
					if (rSpan != null && rSpan.length() > 0) {
						cell.setRowSpan(Integer.parseInt(rSpan) - 1);
					}
					String s = attributes.getValue(DBReportParser.ID);
					if (s != null && s.length() > 0) {
						Object id;
						try {
							id = new Integer(s);
						} catch (Exception e) {
							id = s;
						}
						if (getRecodeMap().containsKey(id))
							id = getRecodeMap().get(id);
						cell.setStyleId(id);
					}
					getHandler().pushHandler(createCellHandler(cell));
					return true;
				}
			}
			if (inRows) {
				if (name.equals(ROW)) {
					inRow = true;
					if (rowIndex >= getReportModel().getRowCount()) {
						getReportModel().getRowModel().addRow();
						rowIndex = getReportModel().getRowCount() - 1;
					}
					if (rowIndex >= oldRowCount) {
						TableRow currentRow = getReportModel().getRowModel()
								.getRow(rowIndex);
						int height = unit.getYPixels(Double
								.parseDouble(attributes
										.getValue(DBReportParser.ROW_H)));
						currentRow.setHeight(height);
					}
					return true;
				}
			}
			if (inCols && name.equals(DBReportParser.COL)) {
				int column = Integer.parseInt(attributes
						.getValue(DBReportParser.COL_N))
						+ startCol;
				if (column >= 0 && column >= oldColCount) {
					TableColumnModel cm = getReportModel().getColumnModel();
					int width = unit.getXPixels(Double.parseDouble(attributes
							.getValue(DBReportParser.COL_W)));
					cm.getColumn(column).setPreferredWidth(width);
				}
				return true;
			}
			if (name.equals(DBReportParser.ROWS)) {
				inRows = true;
				return true;
			}
			if (name.equals(DBReportParser.COLS)) {
				inCols = true;
				return true;
			}
			if (name.equals(DBReportParser.COLCOUNT)) {
				inColCount = true;
				return true;
			}

		}
		return false;
	}

	public void endElement(String name, StringBuffer value) {
		if (inSheet) {
			if (inCell && name.equals(CELL)) {

				inCell = false;
				return;
			}
			if (inRows) {
				if (inRow && name.equals(ROW)) {
					rowIndex++;
					inRow = false;
					return;
				}
				if (name.equals(DBReportParser.ROWS)) {
					inRows = false;
					return;
				}
			}
			if (name.equals(DBReportParser.COLS)) {
				inCols = false;
				return;
			}
			if (inColCount && name.equals(DBReportParser.COLCOUNT)) {
				inColCount = false;
				int colCount = Integer.parseInt(value.toString());
				if (colCount + startCol > getReportModel().getColumnCount()) {
					getReportModel().setColumnCount(colCount + startCol);
				}
				return;
			}
		}
		if (name.equals(SHEET)) inSheet = false;
	}

	protected CellParser createCellHandler(Cell cell) {
		return new CellParser(getDefaultReportHandler(), cell, null);
	}

	protected Map<Object, Object> getRecodeMap() {
		if (recodeMap == null) {
			recodeMap = new HashMap<>();
		}
		return recodeMap;
	}

	protected String getRootName() {
		return "DocReport";
	}

	public void save(Writer writer, ReportModel model, GridRect selectionRect)
			throws SaveReportException {
		this.selectionRect = selectionRect;
		PrintWriter fw;
		if (writer instanceof PrintWriter)
			fw = (PrintWriter) writer;
		else
			fw = new PrintWriter(writer);
		fw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		fw.println("<jdbreport Version=\"" + ReportBook.CURRENT_VERSION
				+ "\" >");
		fw.println("<fragment>");
		fw.println("<" + getRootName() + ">");
		writeStyles(model, fw);
		fw.println("<Sheet>");
		fw.println("<" + getSheetName() + ">");
		saveSheet(fw, model);
		fw.println("</" + getSheetName() + ">");
		fw.println("</Sheet>");
		fw.println("</" + getRootName() + ">");
		fw.println("</fragment>");
		fw.println("</jdbreport>");
	}

	protected String getSheetName() {
		return "ReportGrid";
	}

	protected void writeStyles(ReportModel model, PrintWriter fw) {
		fw.println("<Styles>");
		ArrayList<Object> list = new ArrayList<>();
		Iterator<Cell> cellit = model.getSelectedCells(selectionRect);
		while (cellit.hasNext()) {
			Object key = cellit.next().getStyleId();
			if (key != null && list.indexOf(key) < 0) {
				StyleReportParser.save(fw, model.getStyles(key));
				list.add(key);
			}
		}
		fw.println("</Styles>");
	}

	protected void saveSheet(PrintWriter writer, ReportModel model) throws SaveReportException {
		writer.println("<DefaultColWidth>" + ReportColumn.DEFAULT_COLUMN_WIDTH
				+ "</DefaultColWidth>");
		writer.println("<DefaultRowHeight>"
				+ model.getRowModel().getPreferredRowHeight()
				+ "</DefaultRowHeight>");
		writer
				.println("<ColCount>"
						+ (selectionRect.getRightCol()
								- selectionRect.getLeftCol() + 1)
						+ "</ColCount>");
		writer
				.println("<RowCount>"
						+ (selectionRect.getBottomRow()
								- selectionRect.getTopRow() + 1)
						+ "</RowCount>");
		writer.println("<Cols>");
		for (int c = selectionRect.getLeftCol(); c <= selectionRect
				.getRightCol(); c++) {
			writer.println("<col N=\"" + (c - selectionRect.getLeftCol())
					+ "\" W=\"" + unit.setXPixels(model.getColumnWidth(c))
					+ "\" />");
		}
		writer.println("</Cols>");
		writer.println("<Rows>");
		writeRows(writer, model);
		writer.println("</Rows>");

	}

	protected void writeRows(PrintWriter writer, ReportModel model) throws SaveReportException {
		writer.println("<group>");
		for (int row = selectionRect.getTopRow(); row <= selectionRect
				.getBottomRow(); row++)
			writeRow(writer, model.getRowModel().getRow(row), model, row);
		writer.println("</group>");
	}

	protected void writeRow(PrintWriter writer, TableRow row,
			ReportModel model, int currentRow) throws SaveReportException {
		String params = "";
		if (row.isPageBreak()) {
			params += DBReportParser.ROW_PAGE_BREAK + "=\"true\" ";
		}
		writer.println("<row H=\"" + unit.setYPixels(row.getHeight()) + "\" "
				+ params + ">");

		for (int c = selectionRect.getLeftCol(); c <= selectionRect
				.getRightCol(); c++) {
			writeCell(writer, model, row.getCellItem(c), currentRow, c
					- selectionRect.getLeftCol());
		}
		writer.println("</row>");
	}

	protected void writeCell(PrintWriter writer, ReportModel model, Cell cell,
			int r, int c) throws SaveReportException {
		getCellHandler().save(writer, model, cell, r, c);
	}

	protected CellParser getCellHandler() {
		if (cellHandler == null) {
			cellHandler = createCellHandler();
		}
		return cellHandler;
	}

	protected CellParser createCellHandler() {
		return new CellParser(getDefaultReportHandler(), null);
	}

}
