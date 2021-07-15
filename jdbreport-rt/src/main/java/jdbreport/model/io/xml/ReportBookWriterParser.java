/*
 * JDBReport Generator
 * 
 * Copyright (C) 2010-2014 Andrey Kholmanskih
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

import java.io.PrintWriter;
import java.util.Iterator;

import jdbreport.util.xml.XMLCoder;

import jdbreport.model.Cell;
import jdbreport.model.DetailGroup;
import jdbreport.model.Group;
import jdbreport.model.GroupsGroup;
import jdbreport.model.ReportColumn;
import jdbreport.model.ReportModel;
import jdbreport.model.RowsGroup;
import jdbreport.model.TableRow;
import jdbreport.model.Units;
import jdbreport.model.io.SaveReportException;

/**
 * @author Andrey Kholmanskih
 *
 * @version	3.0 12.12.2014
 */
public abstract class ReportBookWriterParser extends DefaultReportParser {

	protected CellParser cellHandler;
	protected int currentRow;
	protected static Units unit = Units.MMx10;

	public ReportBookWriterParser(DefaultReaderHandler reportHandler) {
		super(reportHandler);
	}

	protected void writeGroup(PrintWriter writer, Group group, ReportModel model) throws SaveReportException {
		if (group.getChildCount() == 0)
			return;
		writer.print("<group type=\"" + group.getTypeName() +  "\"" );
		if (group instanceof DetailGroup && ((DetailGroup)group).isRepeateHeader()) {
			writer.print(" repeateHeader=\"true\"");
		}
		writer.println(" >");
		writeGroupChild(writer, group, model);
		writer.println("</group>");
	}

	protected void writeGroupChild(PrintWriter writer, Group group,
			ReportModel model) throws SaveReportException {
		for (int i = 0; i < group.getChildCount(); i++) {
			if (group instanceof RowsGroup) {
				writeRow(writer, ((RowsGroup) group).getChild(i), model);
			} else
				writeGroup(writer, ((GroupsGroup) group).getChild(i), model);
		}
	}

	
	protected void writeRow(PrintWriter writer, TableRow row, ReportModel model) throws SaveReportException {
		String params = "";
		if (row.isPageBreak()) {
			params += DBReportParser.ROW_PAGE_BREAK + "=\"true\" ";
		}
		writer.println("<row H=\"" + unit.setYPixels(row.getHeight()) + "\" "
				+ params + ">");

		for (int c = 0; c < row.getColCount(); c++) {
			writeCell(writer, model, row.getCellItem(c), currentRow, c);
		}
		writer.println("</row>");
		currentRow++;
	}

	protected void writeCell(PrintWriter writer, ReportModel model, Cell cell,
			int row, int col) throws SaveReportException {
		getCellHandler().save(writer, model, cell, row, col);
	}

	protected CellParser getCellHandler() {
		if (cellHandler == null) {
			cellHandler = createCellHandler();
		}
		return cellHandler;
	}

	public void endElement(String name, StringBuffer value) {
	}

	public void writeSheet(PrintWriter writer, ReportModel model) throws SaveReportException {
		writer.println("<Sheet>");
		writer.println("<" + getSheetName() + ">");
		saveSheet(writer, model);
		writer.println("</" + getSheetName() + ">");
		writer.println("</Sheet>");
	}

	protected void writeRows(PrintWriter writer, ReportModel model) throws SaveReportException {
		Iterator<Group> it = model.getRowModel().getRootGroup()
				.getGroupIterator();
		while (it.hasNext()) {
			writeGroup(writer, it.next(), model);
		}
	}
	
	protected void saveSheet(PrintWriter writer, ReportModel model) throws SaveReportException {
		currentRow = 0;
		writer.println("<DefaultColWidth>" + ReportColumn.DEFAULT_COLUMN_WIDTH
				+ "</DefaultColWidth>");
		writer.println("<DefaultRowHeight>"
				+ model.getRowModel().getPreferredRowHeight()
				+ "</DefaultRowHeight>");
		writer.print("<Options " + "RowSizing=\"" + model.isRowSizing() + "\" "
				+ "ColSizing=\"" + model.isColSizing() + "\" "
				+ "RowMoving=\"" + model.isRowMoving() + "\" "
				+ "ColMoving=\""+ model.isColMoving() + "\" "
				+ "Editing=\"" 	+ model.isEditable() + "\" "
				+ "printLR=\""  + model.isPrintLeftToRight() + "\" ");
		if (!model.isVisible()) {
			writer.print("Visible=\"" + model.isVisible() + "\" ");
		}
		if (model.isHideFirstHeader()) {
			writer.print("HideFirstHeader=\"" + model.isHideFirstHeader() + "\" ");
		}
		writer
				.println("CanUpdatePages=\"" + model.isCanUpdatePages()
						+ "\" />");
		ReportPageParser.save(writer, model.getReportPage());
		writer.println("<ReportTitle>"
				+ XMLCoder.replaceSpecChar(model.getReportTitle())
				+ "</ReportTitle>");
		writer.println("<ColCount>" + (model.getColumnCount()) + "</ColCount>");
		writer.println("<RowCount>" + (model.getRowCount()) + "</RowCount>");
		writer.println("<Cols>");
		for (int c = 0; c < model.getColumnCount(); c++) {
			writer.println("<col N=\"" + c + "\" W=\""
					+ unit.setXPixels(model.getColumnWidth(c)) + "\" />");
		}
		writer.println("</Cols>");
		writer.println("<Rows>");
		writeRows(writer, model);
		writer.println("</Rows>");
		StringBuilder s = new StringBuilder();
		for (int c = 0; c < model.getColumnCount(); c++) {
			if (model.isColumnBreak(c)) {
				s.append("<col>").append(c).append("</col>");
			}
		}
		if (s.length() > 0) {
			writer.println("<" + DBReportParser.COLUMN_PAGE_BREAK + ">" + s
					+ "</" + DBReportParser.COLUMN_PAGE_BREAK + ">");
		}
	}

	protected abstract String getSheetName();
	
	protected abstract CellParser createCellHandler();
}
