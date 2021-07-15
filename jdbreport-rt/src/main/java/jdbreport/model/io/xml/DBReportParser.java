/*
 * JDBReport Generator
 * 
 * Copyright (C) 2005-2014 Andrey Kholmanskih
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
package jdbreport.model.io.xml;

import javax.swing.table.TableColumnModel;

import jdbreport.model.AbstractGroup;
import jdbreport.model.Cell;
import jdbreport.model.DetailGroup;
import jdbreport.model.Group;
import jdbreport.model.GroupsGroup;
import jdbreport.model.RowsGroup;
import jdbreport.model.TableRow;
import jdbreport.model.Units;
import jdbreport.model.io.ResourceReader;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @version 2.0 15.11.2010
 * @author Andrey Kholmanskih
 * 
 */
public class DBReportParser extends DefaultReportParser {

	public static final String REP_PAGE = "RepPage";

	public static final String GROUP = "group";

	public static final String DEFAULT_COL_WIDTH = "DefaultColWidth";

	public static final String DEFAULT_ROW_HEIGHT = "DefaultRowHeight";

	public static final String COL_W = "W";

	public static final String ROW_H = "H";

	public static final String COL_N = "N";

	public static final String RSPAN = "rSpan";

	public static final String CSPAN = "cSpan";

	public static final String COL = "col";

	public static final String COLUMN_PAGE_BREAK = "column-page-break";

	public static final String ROW_PAGE_BREAK = "page-break";

	public static final String ROWS = "Rows";

	public static final String COLS = "Cols";

	public static final String REPORTTITLE = "ReportTitle";

	public static final String COLCOUNT = "ColCount";

	public static final String ID = "ID";

	private boolean inTitle = false;

	private boolean inColCount = false;

	private boolean inCols = false;

	protected boolean inRows = false;

	private boolean inRow = false;

	private boolean inCell = false;

	protected Group currentGroup;

	private boolean inColumnPageBreak;

	private int rowIndex;

	private CellParser cellParser;

	private ResourceReader resourceReader;

	private static Units unit = Units.MMx10;

	public DBReportParser(DefaultReaderHandler reportHandler, ResourceReader resReader) {
		super(reportHandler);
		this.resourceReader = resReader;
	}

	public boolean startElement(String name, Attributes attributes) {
		if (inRow) {
			if (name.equals(CELL)) {
				inCell = true;
				int col = Integer.parseInt(attributes.getValue("c"));
				Cell cell = getReportModel().createReportCell(rowIndex, col);
				String cSpan = attributes.getValue(CSPAN);
				if (cSpan != null && cSpan.length() > 0) {
					cell.setColSpan(Integer.parseInt(cSpan) - 1);
				}
				String rSpan = attributes.getValue(RSPAN);
				if (rSpan != null && rSpan.length() > 0) {
					cell.setRowSpan(Integer.parseInt(rSpan) - 1);
				}
				String s = attributes.getValue(ID);
				if (s != null && s.length() > 0) {
					Object id;
					try {
						id = new Integer(s);
					} catch (Exception e) {
						id = s;
					}
					cell.setStyleId(id);
				}
				getHandler().pushHandler(createCellHandler(cell));
				return true;
			}
		}
		if (inRows) {
			if (name.equals(ROW)) {
				inRow = true;
				rowIndex = getReportModel().getRowModel().addRow(
						(RowsGroup) currentGroup, -1);
				TableRow currentRow = getReportModel().getRowModel().getRow(
						rowIndex);
				int height = unit.getYPixels(Double.parseDouble(attributes
						.getValue(ROW_H)));
				currentRow.setHeight(height);
				String s = attributes.getValue(ROW_PAGE_BREAK);
				if (s != null && s.length() > 0) {
					currentRow.setPageBreak(Boolean.parseBoolean(s));
				}
				return true;
			}
			if (name.equals(DBReportParser.GROUP)) {
				parseGroup(attributes);
				return true;
			}
		}
		if (inCols && name.equals(COL)) {
			int column = Integer.parseInt(attributes.getValue(COL_N));
			if (column >= 0) {
				TableColumnModel cm = getReportModel().getColumnModel();
				int width = unit.getXPixels(Double.parseDouble(attributes
						.getValue(COL_W)));
				cm.getColumn(column).setPreferredWidth(width);
				return true;
			}
			return false;
		}
		if (inColumnPageBreak && name.equals(COL)) {
			return true;
		}
		if (name.equals(ROWS)) {
			currentGroup = getReportModel().getRowModel().getRootGroup();
			getReportModel().removeRows();
			inRows = true;
			return true;
		}
		if (name.equals(COLS)) {
			inCols = true;
			return true;
		}
		if (name.equals(COLUMN_PAGE_BREAK)) {
			inColumnPageBreak = true;
			return true;
		}
		if (name.equals("Options")) {
			getReportModel().setRowSizing(
					Boolean.parseBoolean(attributes.getValue("RowSizing")));
			getReportModel().setColSizing(
					Boolean.parseBoolean(attributes.getValue("ColSizing")));
			getReportModel().setRowMoving(
					Boolean.parseBoolean(attributes.getValue("RowMoving")));
			getReportModel().setColMoving(
					Boolean.parseBoolean(attributes.getValue("ColMoving")));
			getReportModel().setEditable(
					Boolean.parseBoolean(attributes.getValue("Editing")));
			if (attributes.getValue("Visible") != null)
				getReportModel().setVisible(
						Boolean.parseBoolean(attributes.getValue("Visible")));
			getReportModel()
					.setCanUpdatePages(
							Boolean.parseBoolean(attributes
									.getValue("CanUpdatePages")));
			getReportModel().setPrintLeftToRight(
					Boolean.parseBoolean(attributes.getValue("printLR")));
			getReportModel().setHideFirstHeader(
					Boolean.parseBoolean(attributes.getValue("HideFirstHeader")));
			return true;
		}
		if (name.equals(REPORTTITLE)) {
			inTitle = true;
			return true;
		}
		if (name.equals(COLCOUNT)) {
			inColCount = true;
			return true;
		}
		if (name.equals(DEFAULT_ROW_HEIGHT)) {
			return true;
		}
		if (name.equals(DEFAULT_COL_WIDTH)) {
			return true;
		}
		if (name.equals(DBReportParser.REP_PAGE)) {
			getHandler().pushHandler(
					new ReportPageParser(getDefaultReportHandler(),
							getReportModel().getReportPage()));
			return true;
		}
		return false;
	}

	protected void parseGroup(Attributes attributes) {
		int typeGroup = AbstractGroup.stringToRowType(attributes
				.getValue("type"));
		currentGroup = ((GroupsGroup) currentGroup).addGroup(typeGroup);
		if (currentGroup.getType() == Group.GROUP_DETAIL && attributes
				.getValue("repeateHeader") != null) {
			((DetailGroup)currentGroup).setRepeateHeader(Boolean.parseBoolean(attributes
					.getValue("repeateHeader")));
		}
	}

	protected CellParser createCellHandler(Cell cell) {
		if (cellParser == null) {
			cellParser = new CellParser(getDefaultReportHandler(), cell, getResourceReader());
		}
		cellParser.setCell(cell);
		return cellParser;
	}

	public void endElement(String name, StringBuffer value) throws SAXException {
		if (inCell && name.equals(CELL)) {
			inCell = false;
			return;
		}
		if (inRows) {
			if (inRow && name.equals(ROW)) {
				inRow = false;
				return;
			}
			if (name.equals(DBReportParser.GROUP)) {
				currentGroup = currentGroup.getParent();
				return;
			}
			if (name.equals(ROWS)) {
				currentGroup = null;
				inRows = false;
				return;
			}
		}
		if (name.equals(COLS)) {
			inCols = false;
			return;
		}
		if (inTitle && name.equals(REPORTTITLE)) {
			inTitle = false;
			getReportModel().setReportTitle(value.toString());
			return;
		}
		if (inColCount && name.equals(COLCOUNT)) {
			inColCount = false;
			getReportModel().setColumnCount(Integer.parseInt(value.toString()));
			return;
		}
		if (inColumnPageBreak && name.equals(COL)) {
			getReportModel().setColumnBreak(Integer.parseInt(value.toString()),
					true);
			return;
		}
		if (inColumnPageBreak && name.equals(COLUMN_PAGE_BREAK)) {
			inColumnPageBreak = false;
			return;
		}
		if (name.equals(DEFAULT_ROW_HEIGHT)) {
			getReportModel().getRowModel().setPreferredRowHeight(
					Integer.parseInt(value.toString()));
			return;
		}
		if (name.equals(DEFAULT_COL_WIDTH)) {
			getReportModel().setDefaultColumnWidth(
					Integer.parseInt(value.toString()));
			return;
		}
		if (name.equals(SHEET)) {
			getHandler().popHandler(name);
			return;
		}
	}


	protected ResourceReader getResourceReader() {
		return resourceReader;
	}

}
