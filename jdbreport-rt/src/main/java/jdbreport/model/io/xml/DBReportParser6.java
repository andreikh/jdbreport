/*
 * Created on 25.01.2005
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2005-2008 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.model.io.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import and.util.xml.XMLParser;

import javax.swing.table.*;

import jdbreport.model.AbstractGroup;
import jdbreport.model.Cell;
import jdbreport.model.Group;
import jdbreport.model.GroupsGroup;
import jdbreport.model.RootGroup;
import jdbreport.model.RowsGroup;
import jdbreport.model.TableRow;
import jdbreport.model.Units;

/**
 * @version 1.4 26.02.2010
 * @author Andrey Kholmanskih
 * 
 */
public class DBReportParser6 extends DefaultReportParser {

	private static final String SHEET = "Sheet";

	private static final String DEFAULT_COL_WIDTH = "DefaultColWidth";

	private static final String DEFAULT_ROW_HEIGHT = "DefaultRowHeight";

	private static final String COL_W = "W";

	private static final String ROW_H = "H";

	private static final String ROW_N = "N";

	private static final String COL_N = "N";

	private static final String RSPAN = "rSpan";

	private static final String CSPAN = "cSpan";

	private static final String COL = "col";

	private static final String PAGELASTCOL = "pagelastcol";

	protected static final String ROW = "row";

	private static final String ROWS = "Rows";

	private static final String COLS = "Cols";

	private static final String REPORTTITLE = "ReportTitle";

	private static final String COLCOUNT = "ColCount";

	private static final String ROWCOUNT = "RowCount";

	private static final String ID = "ID";

	private boolean inTitle = false;

	private boolean inColCount = false;

	private boolean inRowCount = false;

	private boolean inCols = false;

	protected boolean inRows = false;

	private boolean inRow = false;

	private boolean inCell = false;

	protected RowsGroup currentGroup;

	private int currentGroupIndex = 0;

	private boolean inPageLastCols;

	private int oldType = -1;

	private List<Group> groupList;

	private static Units unit = Units.MMx10;

	private TableRow tableRow;

	public DBReportParser6(DefaultReaderHandler reportHandler) {
		super(reportHandler);
	}

	public boolean startElement(String name, Attributes attributes) {
		if (inRow) {
			if (name.equals(CELL)) {
				inCell = true;
				int col = Integer.parseInt(attributes.getValue("c")) - 1;
				Cell cell = tableRow.createCellItem(col);
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
					cell.setStyleId(new Integer(s));
				}
				getHandler().pushHandler(createCellHandler(cell));
				return true;
			}
			if (name.equals("item")) {
				String groupName = attributes.getValue("Type");
				int groupType = Group.ROW_TITLE;
				if (groupName != null && groupName.length() > 0)
					groupType = AbstractGroup.stringToRowType(groupName);
				String s = attributes.getValue("Group");
				int n = 0;
				if (s != null && s.length() > 0) {
					n = Integer.parseInt(s);
				}
				if (groupType != oldType || currentGroupIndex != n) {
					if (currentGroupIndex != n) {
						if (oldType == Group.ROW_GROUP_FOOTER
								&& (groupType == Group.ROW_GROUP_HEADER || groupType == Group.ROW_DETAIL)) {
							GroupsGroup group = (GroupsGroup) getReportModel()
									.getRowModel().getRootGroup().addGroup(
											Group.GROUP_DETAIL);
							currentGroup = (RowsGroup) group
									.addGroup(groupType);
						} else {
							if (groupType == Group.ROW_GROUP_HEADER
									|| groupType == Group.ROW_DETAIL
									|| groupType == Group.ROW_GROUP_FOOTER) {
								GroupsGroup group = (GroupsGroup) (currentGroup == null ?  getReportModel()
										.getRowModel().getRootGroup().addGroup(Group.GROUP_DETAIL) :  currentGroup
										.getParent().addGroup(
												Group.GROUP_DETAIL));
								currentGroup = (RowsGroup) group
										.addGroup(groupType);
							} else
								currentGroup = (RowsGroup) getReportModel()
										.getRowModel().getRootGroup().addGroup(
												groupType);
						}
					} else {
						if (groupType == Group.ROW_TITLE
								|| groupType == Group.ROW_FOOTER) {
							currentGroup = (RowsGroup) getReportModel()
									.getRowModel().getRootGroup().addGroup(
											groupType);
						} else
							currentGroup = (RowsGroup) currentGroup.getParent()
									.addGroup(groupType);
					}
					oldType = groupType;
					currentGroupIndex = n;
					if (groupType == Group.ROW_DETAIL) {
						putGroup(currentGroup.getParent(), n);
					}
				}
				s = attributes.getValue("EndPage");
				if (s != null && s.length() > 0) {
					tableRow.setPageBreak(Boolean.parseBoolean(s));
				}
				return true;
			}
		}
		if (inRows) {
			if (name.equals(ROW)) {
				int currentRow = Integer.parseInt(attributes.getValue(ROW_N)) - 1;
				if (currentRow >= 0) {
					inRow = true;
					tableRow = getReportModel().getRowModel().createTableRow();
					int height = unit.getYPixels(Double.parseDouble(attributes
							.getValue(ROW_H)));
					tableRow.setHeight(height);
					return true;
				}
				return false;
			}
		}
		if (inCols && name.equals(COL)) {
			int column = Integer.parseInt(attributes.getValue(COL_N)) - 1;
			if (column >= 0) {
				TableColumnModel cm = getReportModel().getColumnModel();
				int width = unit.getXPixels(Double.parseDouble(attributes
						.getValue(COL_W)));
				cm.getColumn(column).setPreferredWidth(width);
				return true;
			}
			return false;
		}
		if (inPageLastCols && name.equals(COL)) {
			return true;
		}
		if (name.equals(ROWS)) {
			getReportModel().startUpdate();
			inRows = true;
			return true;
		}
		if (name.equals(COLS)) {
			inCols = true;
			return true;
		}
		if (name.equals(PAGELASTCOL)) {
			inPageLastCols = true;
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
		if (name.equals(ROWCOUNT)) {
			inRowCount = true;
			return true;
		}
		if (name.equals(DEFAULT_ROW_HEIGHT)) {
			return true;
		}
		if (name.equals(DEFAULT_COL_WIDTH)) {
			return true;
		}
		if (name.equals("RepPage")) {
			getHandler().pushHandler(
					new ReportPageParser6(getDefaultReportHandler(),
							getReportModel().getReportPage()));
			return true;
		}
		return false;
	}

	private void putGroup(Group group, int n) {
		if (groupList == null) {
			groupList = new ArrayList<Group>();
		}
		while (groupList.size() <= n) {
			groupList.add(null);
		}
		groupList.set(n, group);
	}

	protected Group getGroup(int index) {
		if (groupList == null)
			return null;
		if (index >= 0 && index < groupList.size())
			return groupList.get(index);
		return null;
	}

	protected XMLParser createCellHandler(Cell cell) {
		return new CellParser6(getDefaultReportHandler(), cell);
	}

	public void endElement(String name, StringBuffer value) throws SAXException {
		if (name.equals(CELL) && inCell) {
			inCell = false;
			return;
		}
		if (inRows && name.equals(ROW)) {
			if (tableRow != null) {
				if (currentGroup == null) {
					currentGroup = ((RootGroup) getReportModel().getRowModel()
							.getRootGroup()).getTitleGroup();
				}
				getReportModel().getRowModel().addRow((RowsGroup) currentGroup,
						currentGroup.getChildCount(), tableRow);
				tableRow = null;
			}
			inRow = false;
			return;
		}
		if (name.equals(ROWS)) {
			getReportModel().endUpdate();
			inRows = false;
			return;
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
			getReportModel().setColumnCount(
					Integer.parseInt(value.toString()) - 1);
			return;
		}
		if (inRowCount && name.equals(ROWCOUNT)) {
			inRowCount = false;
			return;
		}
		if (inPageLastCols && name.equals(COL)) {
			getReportModel().setColumnBreak(Integer.parseInt(value.toString()),
					true);
			return;
		}
		if (inPageLastCols && name.equals(PAGELASTCOL)) {
			inPageLastCols = false;
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

}
