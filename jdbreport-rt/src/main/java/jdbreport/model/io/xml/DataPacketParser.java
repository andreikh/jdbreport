/*
 * Created on 25.01.2005
 *
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

import java.awt.Font;
import java.util.ArrayList;

import jdbreport.model.Border;
import jdbreport.model.Cell;
import jdbreport.model.CellStyle;
import jdbreport.model.ReportModel;

import org.xml.sax.Attributes;

/**
 * @version 3.0 13.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class DataPacketParser extends DefaultReportParser {

	public static final String DATAPACKET = "DATAPACKET";
	private static final String ROW = "ROW";
	private static final String ROWDATA = "ROWDATA";
	private static final String FIELD = "FIELD";
	private static final String FIELDS = "FIELDS";
	private static final String METADATA = "METADATA";
	private ReportModel reportModel;
	private boolean inMetadata;
	private boolean inFields;
	private ArrayList<Field> fields;
	private boolean inRowdata;
	private int currentRow;
	private Object headerStyle;
	private Object valueStyle;
	private Object numericStyle;

	public DataPacketParser(DefaultReaderHandler reportHandler) {
		super(reportHandler);
		this.reportModel = getReportModel();
		fields = new ArrayList<>();
		createStyles();
	}

	private void createStyles() {
		CellStyle style = CellStyle.getDefaultStyle();
		style = style.deriveHAlign(CellStyle.CENTER);
		style = style.deriveAutoHeight(true);
		style = style.deriveBorder(Border.LINE_BORDER, new Border());
		style = style.deriveFont(Font.BOLD);
		headerStyle = reportModel.addStyle(style);

		style = CellStyle.getDefaultStyle();
		style = style.deriveAutoHeight(true);
		valueStyle = reportModel.addStyle(style);

		style = style.deriveHAlign(CellStyle.RIGHT);
		numericStyle = reportModel.addStyle(style);
	}

	public boolean startElement(String name, Attributes attributes) {
		if (inRowdata) {
			if (name.equals(DataPacketParser.ROW)) {
				if (reportModel.getRowCount() <= currentRow) {
					reportModel.getRowModel().addRow(-1);
				}
				for (int i = 0; i < attributes.getLength(); i++) {
					int column = getFieldIndex(attributes.getQName(i));
					if (column >= 0) {
						Cell cell = reportModel.createReportCell(currentRow,
								column);
						cell.setValue(attributes.getValue(i));
						if (fields.get(column).isNumeric())
							cell.setStyleId(numericStyle);
						else
							cell.setStyleId(valueStyle);
					}
				}
				currentRow++;
				return false;
			}
		} else if (inMetadata) {
			if (inFields) {
				if (name.equals(DataPacketParser.FIELD)) {
					String nameField = attributes.getValue("attrname");
					String typeField = attributes.getValue("fieldtype");
					fields.add(new Field(nameField, typeField));
					return false;
				}
			}
			if (name.equals(DataPacketParser.FIELDS)) {
				inFields = true;
				return true;
			}
		}
		if (name.equals(DataPacketParser.METADATA)) {
			inMetadata = true;
			return true;
		}
		if (name.equals(DataPacketParser.ROWDATA)) {
			currentRow = 1;
			inRowdata = true;
			return true;
		}
		return false;
	}

	private int getFieldIndex(String nameField) {
		for (int i = 0; i < fields.size(); i++) {
			if (nameField.equals(fields.get(i).name)) {
				return i;
			}
		}
		return -1;
	}

	public void endElement(String name, StringBuffer value) {
		if (inMetadata) {
			if (inFields) {
				if (name.equals(DataPacketParser.FIELDS)) {
					inFields = false;
					return;
				}
			}
			if (name.equals(DataPacketParser.METADATA)) {
				reportModel.setColumnCount(fields.size());
				reportModel.getRowModel().removeRows();
				if (reportModel.getRowCount() == 0)
					reportModel.getRowModel().addRow(-1);
				for (int column = 0; column < fields.size(); column++) {
					Cell cell = reportModel.createReportCell(0, column);
					cell.setValue(fields.get(column).name);
					cell.setStyleId(headerStyle);
				}
				inMetadata = false;
			}
		} else if (inRowdata) {
			if (name.equals(DataPacketParser.ROWDATA)) {
				inRowdata = false;
			}
		}
	}

	private class Field {
		public String name;
		public String type;

		Field(String name, String type) {
			this.name = name;
			this.type = type;
		}

		public boolean isNumeric() {
			return ("i2".equals(type) || "i4".equals(type) || "fixed"
					.equals(type));
		}

		public String toString() {
			return name;
		}
	}
}
