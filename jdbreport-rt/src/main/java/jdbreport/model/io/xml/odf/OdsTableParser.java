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
package jdbreport.model.io.xml.odf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import jdbreport.model.Cell;
import jdbreport.model.CellStyle;
import jdbreport.model.Group;
import jdbreport.model.ReportColumn;
import jdbreport.model.RowsGroup;
import jdbreport.model.TableRow;
import jdbreport.model.TableRowModel;
import jdbreport.model.io.xml.DefaultReaderHandler;
import jdbreport.model.io.xml.odf.CommonStyle.Break;

import jdbreport.util.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
class OdsTableParser extends OdsReportParser {

	private ArrayList<String> columnCellStyles = new ArrayList<>();

	private ArrayList<String> rowCellStyles = new ArrayList<>();

	private boolean inRow;

	private int currentRow;

	private RowsGroup currentGroup;

	private boolean inCell;

	private int currentColumn;

	private Cell currentCell;

	private boolean inCellText;

	private boolean inDrawFrame;

	private boolean inDrawImage;

	private StringBuffer currentValue = new StringBuffer();

	private int countRepeated;

	private int countRepeatColumn;

	private List<Cell> currentCells = new ArrayList<>();

	private RowStyle currentRowStyle;

	public OdsTableParser(DefaultReaderHandler reportHandler) {
		super(reportHandler);
		TableRowModel rowModel = getReportModel().getRowModel();
		rowModel.removeRows();
		getReportModel().setColumnCount(0);
	}

	public void characters(StringBuffer value) {
		if (inCellText) {
			currentValue.append(value);
			value.setLength(0);
		}
	}

	public boolean startElement(String name, Attributes attributes) {
		if (inRow) {
			if (inCell) {
				if (inCellText) {
					if (name.equals("text:span") || name.equals("text:s")) {
						return true;
					}
				} else if (name.equals("text:p")) {
					inCellText = true;
					return true;
				}
				if (inDrawFrame) {
					if (name.equals("draw:image")) {
						addImage(attributes);
						inDrawImage = true;
						return true;
					}
				} else if (name.equals("draw:frame")) {
					addDrawFrame(attributes);
					inDrawFrame = true;
					return true;
				}
			} else {
				if (name.equals("table:table-cell")) {
					addCell(attributes);
					inCell = true;
					return true;
				}
				if (name.equals("table:covered-table-cell")) {
					addCoveredCell(attributes);
					return false;
				}
			}
		}
		if (name.equals("table:table-column")) {
			addColumns(attributes);
			return false;
		}
		if (name.equals("table:table-row")) {
			addRow(attributes);
			inRow = true;
			return true;
		}
		return false;
	}

	public void endElement(String name, StringBuffer value) throws SAXException {
		if (inRow) {
			if (inCell) {
				if (inCellText) {
					if (name.equals("text:p")) {
						if (currentValue.length() > 0) {
							if (currentCell.getValue() == null)
								currentCell.setValue(currentValue.toString());
							else {
								if (currentCell.getText().length() > 0)
									currentCell.setValue(currentCell.getText()
											+ '\n' + currentValue.toString());
								else
									currentCell.setValue(currentValue
											.toString());
							}
							currentValue.setLength(0);
							if (countRepeatColumn > 0) {
								for (Cell cell : currentCells) {
									cell.setValue(currentCell.getValue());
								}
							}
						}
						inCellText = false;
						return;
					}
				}
				if (inDrawFrame) {
					if (inDrawImage) {
						if (name.equals("draw:image")) {
							inDrawImage = false;
							return;
						}
					}
					if (name.equals("draw:frame")) {
						inDrawFrame = false;
						return;
					}
				}
				if (name.equals("table:table-cell")) {
					inCell = false;
					return;
				}
			}
			if (name.equals("table:table-row")) {
				if (countRepeated > 1000)
					countRepeated = 0;
				if (countRepeated > 1) {
					int h = getReportModel().getRowModel().getRowHeight(
							currentRow);
					TableRow row = getReportModel().getRowModel().getRow(
							currentRow);
					for (int i = 1; i < countRepeated; i++) {
						currentRow = getReportModel().getRowModel().addRow(
								getCurrentGroup(), -1);
						getReportModel().getRowModel().setRowHeight(currentRow,
								h);
						rowCellStyles.add(rowCellStyles.get(rowCellStyles
								.size() - 1));
						for (int c = 0; c < row.getColCount(); c++) {
							getReportModel()
									.createReportCell(currentRow, c)
									.setStyleId(row.getCellItem(c).getStyleId());
						}
					}
				}
				inRow = false;
				return;
			}
		}
		if (name.equals("table:table")) {
			currentGroup = null;
			getHandler().popHandler(name);
		}
	}

	private void addCell(Attributes attributes) {
		String s;
		s = attributes.getValue("table:style-name");
		if (s == null) {
			s = rowCellStyles.get(currentRow);
			if (s == null) {
				s = columnCellStyles.get(currentColumn);
			}
		}
		currentCell = getReportModel().createReportCell(currentRow,
				currentColumn);
		currentCell.setStyleId(s);
		if (currentRowStyle != null && currentRowStyle.isOptimalHeight()) {
			CellStyle style = getReportModel().getStyles(s);
			style = style.deriveAutoHeight(currentRowStyle.isOptimalHeight());
			currentCell.setStyleId(getReportModel().addStyle(style));
		}
		s = attributes.getValue("table:number-columns-spanned");
		if (s != null) {
			currentCell.setColSpan(Integer.parseInt(s) - 1);
		}

		s = attributes.getValue("table:number-rows-spanned");
		if (s != null) {
			currentCell.setRowSpan(Integer.parseInt(s) - 1);
		}

		countRepeatColumn = 0;
		s = attributes.getValue("table:number-columns-repeated");
		if (s != null) {
			currentCells.clear();
			countRepeatColumn = Integer.parseInt(s);
			for (int i = 1; i < countRepeatColumn; i++) {
				Cell cell = getReportModel().createReportCell(currentRow,
						currentColumn + i);
				currentCells.add(cell);
				cell.setStyleId(currentCell.getStyleId());
			}
			currentColumn += countRepeatColumn;
		} else
			currentColumn++;

		s = attributes.getValue("office:value-type");
		if (s != null) {
			currentCell.setValueType(Cell.Type.valueOf(s.toUpperCase()));
			if (countRepeatColumn > 0) {
				for (Cell cell : currentCells) {
					cell.setValueType(currentCell.getValueType());
				}
			}
		}

		s = attributes.getValue("office:value");
		if (s != null) {
			currentCell.setValue(s);
			if (countRepeatColumn > 0) {
				for (Cell cell : currentCells) {
					cell.setValue(currentCell.getValue());
				}
			}
		}
		
		s = attributes.getValue("table:formula");
		if (s != null) {
			if (s.startsWith("of:=")) {
				s = s.substring(4);
			}
			currentCell.setCellFormula(s);
			if (countRepeatColumn > 0) {
				for (Cell cell : currentCells) {
					cell.setCellFormula(currentCell.getCellFormula());
				}
			}
		}

		

	}

	private void addCoveredCell(Attributes attributes) {
		String s = attributes.getValue("table:number-columns-repeated");
		if (s != null) {
			currentColumn += Integer.parseInt(s);
		} else
			currentColumn++;
	}

	private void addDrawFrame(Attributes attributes) {
	}

	private void addImage(Attributes attributes) {
		String s;
		s = attributes.getValue("xlink:href");
		if (s != null) {
			File file = new File(getBasePath() + '/' + s);
			try {
				BufferedImage image = ImageIO.read(file);
				currentCell.setIcon(new ImageIcon(image));
				currentCell.setImageFormat(Utils.getFileExtension(s));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void addColumns(Attributes attributes) throws NumberFormatException {
		String s;
		s = attributes.getValue("table:style-name");
		ColumnStyle columnStyle = getColumnStyles().get(s);
		String cellStyleId = attributes
				.getValue("table:default-cell-style-name");
		double w = columnStyle.getWidth();
		int count = 1;
		s = attributes.getValue("table:number-columns-repeated");
		if (s != null)
			count = Integer.parseInt(s);
		int oldCount = getReportModel().getColumnCount();
		int newCount = getReportModel().addColumns(count);
		if (columnStyle.getBreak() == Break.page) {
			int c = columnStyle.isAfter() ? oldCount : oldCount - 1;
			if (c > 0) {
				((ReportColumn) getReportModel().getColumnModel().getColumn(c))
						.setPageBreak(true);
			}
		}
		for (int c = oldCount; c < newCount; c++) {
			ReportColumn column = (ReportColumn) getReportModel()
					.getColumnModel().getColumn(c);
			column.setWidth(w);
			columnCellStyles.add(cellStyleId);
		}
	}

	private RowsGroup getCurrentGroup() {
		if (currentGroup == null) {
			currentGroup = (RowsGroup) getReportModel().getRowModel()
					.getRootGroup().addGroup(Group.ROW_NONE);
		}
		return currentGroup;
	}

	private void addRow(Attributes attributes) throws NumberFormatException {
		String s;
		s = attributes.getValue("table:style-name");
		currentRowStyle = getRowStyles().get(s);
		String cellStyleId = attributes
				.getValue("table:default-cell-style-name");
		double h = currentRowStyle.getHeight();
		countRepeated = 1;
		s = attributes.getValue("table:number-rows-repeated");
		if (s != null)
			countRepeated = Integer.parseInt(s);
		currentRow = getReportModel().getRowModel().addRow(getCurrentGroup(),
				-1);
		getReportModel().getRowModel().setRowHeight(currentRow, h);
		rowCellStyles.add(cellStyleId);

		if (currentRowStyle.getBreak() == Break.page) {
			int r = currentRowStyle.isAfter() ? currentRow : currentRow - 1;
			if (r > 0) {
				(getReportModel().getRowModel().getRow(r))
						.setPageBreak(true);
			}
		}
		currentColumn = 0;
	}

}
