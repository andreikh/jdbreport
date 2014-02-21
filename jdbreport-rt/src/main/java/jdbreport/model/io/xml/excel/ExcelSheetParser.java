/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2009 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.model.io.xml.excel;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.print.attribute.standard.MediaSize;
import javax.swing.text.JTextComponent;

import jdbreport.grid.JReportGrid.HTMLReportRenderer;
import jdbreport.model.Cell;
import jdbreport.model.CellValue;
import jdbreport.model.JReportModel;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportColumn;
import jdbreport.model.Units;
import jdbreport.model.io.SaveReportException;
import jdbreport.model.io.xml.DefaultReaderHandler;
import jdbreport.model.io.xml.DefaultReportParser;
import jdbreport.model.print.ReportPage;
import jdbreport.util.GraphicUtil;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import and.util.Utilities;
import and.util.xml.XMLCoder;

/**
 * @version 2.0 30.03.2012
 * 
 * @author Andrey Kholmanskih
 * 
 */
public class ExcelSheetParser extends DefaultReportParser {

	private static final int A3 = 8;

	private static final int A4 = 9;

	private static final Units unit = Units.INCH;

	private static int oldCol = 0;

	private boolean inCell;

	private boolean inRow;

	private int currentCol;

	private int span;

	private int currentRow;

	private String cellType;

	private boolean inTable;

	private boolean existsTable;

	private boolean inPageSetup;

	private boolean inSheetOptions;

	private boolean inPrint;

	private JTextComponent htmlReportRenderer;

	public ExcelSheetParser(DefaultReaderHandler reportHandler) {
		super(reportHandler);
	}

	public ExcelSheetParser() {
		super(null);
	}

	public boolean startElement(String name, Attributes attributes) {
		if (inCell && name.equals("Data")) {
			cellType = attributes.getValue("ss:Type");
			return true;
		}
		if (inRow && "Cell".equals(name)) {
			if (attributes.getValue("ss:Index") != null) {
				currentCol = Integer.parseInt(attributes.getValue("ss:Index")) - 1;
			} else {
				currentCol += 1 + span;
			}
			Cell cell = getReportModel().createReportCell(currentRow,
					currentCol);
			String s = attributes.getValue("ss:StyleID");
			if (s == null) {
				s = "Default";
			}
			cell.setStyleId(s);
			if (attributes.getValue("ss:MergeAcross") != null) {
				span = Integer.parseInt(attributes.getValue("ss:MergeAcross"));
				cell.setColSpan(span);
			} else
				span = 0;
			if (attributes.getValue("ss:MergeDown") != null)
				cell.setRowSpan(Integer.parseInt(attributes
						.getValue("ss:MergeDown")));

			inCell = true;
			return true;
		}
		if (inTable) {
			if ("Row".equals( name )) {
				String s = attributes.getValue("ss:Index");
				if (s != null) {
					currentRow = Integer.parseInt(s) - 1;
				} else {
					currentRow++;
				}
				s = attributes.getValue("ss:Height");
				if (s != null) {
					double h = Double.parseDouble(s);
					getReportModel().setRowHeight(currentRow,
							(int) Math.round(h * GraphicUtil.getScaleY()));
				}
				currentCol = -1;
				span = 0;
				inRow = true;
				return true;
			}
			if ("Column".equals( name )) {
				String s = attributes.getValue("ss:Index");
				if (s != null)
					currentCol = Integer.parseInt(s) - 1;
				else
					currentCol++;
				s = attributes.getValue("ss:Width");
				double w = 0;
				if (s != null) {
					w = Double.parseDouble(s);
					getReportModel().getColumnModel().getColumn(currentCol)
							.setPreferredWidth(
									(int) Math.round(w
											* GraphicUtil.getScaleX()));
				}
				s = attributes.getValue("ss:Span");
				if (s != null) {
					int c = Integer.parseInt(s);
					if (w > 0)
						for (int i = 1; i <= c; i++) {
							getReportModel().getColumnModel().getColumn(
									currentCol + i).setPreferredWidth(
									(int) Math.round(w
											* GraphicUtil.getScaleX()));
						}
					currentCol += c;
				}
				return true;
			}
		}
		if ("Table".equals( name )) {
			int c = Integer.parseInt(attributes
					.getValue("ss:ExpandedColumnCount"));
			int r = Integer
					.parseInt(attributes.getValue("ss:ExpandedRowCount"));
			getReportModel().setColumnCount(c);
			getReportModel().getRowModel().addRows(r, -1);
			for (int i = 0; i < c; i++) {
				getReportModel().getColumnModel().getColumn(i)
						.setPreferredWidth(64);
			}
			for (int i = 0; i < r; i++) {
				getReportModel().setRowHeight(i, 17);
			}
			currentCol = -1;
			currentRow = -1;
			existsTable = true;
			inTable = true;
			return true;
		}
		if (inPageSetup) {
			if ("PageMargins".equals( name )) {
				ReportPage page = getReportModel().getReportPage();
				double left = page.getLeftMargin(unit);
				double top = page.getTopMargin(unit);
				double right = page.getRightMargin(unit);
				double bottom = page.getBottomMargin(unit);

				String s = attributes.getValue("x:Top");
				if (s != null) {
					top = Double.parseDouble(s);
				}
				s = attributes.getValue("x:Bottom");
				if (s != null)
					bottom = Double.parseDouble(s);
				s = attributes.getValue("x:Left");
				if (s != null) {
					left = Double.parseDouble(s);
				}
				s = attributes.getValue("x:Right");
				if (s != null) {
					right = Double.parseDouble(s);
				}
				page.setMargin(left, top, right, bottom, unit);
				return true;
			}
			if (name.equals("Layout")) {
				String s = attributes.getValue("x:Orientation");
				if ("Landscape".equals(s)) {
					getReportModel().getReportPage().setOrientation(
							ReportPage.LANDSCAPE);
				} else {
					getReportModel().getReportPage().setOrientation(
							ReportPage.PORTRAIT);
				}
				return true;
			}
		}
		if (inSheetOptions) {
			if ("PageSetup".equals(name)) {
				inPageSetup = true;
				return true;
			}
			if ("Print".equals( name )) {
				inPrint = true;
				return true;
			}
		}
		if (inPrint && "PaperSizeIndex".equals( name )) {
			return true;
		}
		if (existsTable && "WorksheetOptions".equals( name )) {
			inSheetOptions = true;
			return true;
		}

		return false;
	}

	public void endElement(String name, StringBuffer value) throws SAXException {
		if (inCell) {
			if ("Data".equals( name )) {
				String s;
				if ("DateTime".equals( cellType )) {
					s = convDateTime(value.toString());
				} else {
					s = value.toString();
				}

				getReportModel().createReportCell(currentRow, currentCol)
						.setValue(s);
				return;
			}
			if ("Cell".equals( name )) {
				inCell = false;
				return;
			}
		}
		if (inRow && "Row".equals( name )) {
			inRow = false;
			return;
		}
		if (inTable && "Table".equals( name )) {
			inTable = false;
			return;
		}
		if (inPageSetup && "PageSetup".equals( name )) {
			inPageSetup = false;
			return;
		}
		if (inPrint) {
			if ("PaperSizeIndex".equals( name )) {
				int i = Integer.parseInt(value.toString());
				ReportPage page = getReportModel().getReportPage();
				float[] size;
				switch (i) {
				case A3:
					size = MediaSize.ISO.A3.getSize(MediaSize.INCH);
					break;
				default:
					size = MediaSize.ISO.A4.getSize(MediaSize.INCH);
				}
				page.setSize(size[0], size[1], unit);
				return;
			}
			if ("Print".equals( name )) {
				inPrint = false;
				return;
			}
		}
		if (inSheetOptions && "WorksheetOptions".equals( name )) {
			inSheetOptions = false;
			return;
		}
		if ("Worksheet".equals( name )) {
			getReportModel().endUpdate();
			getHandler().popHandler(name);
			return;
		}
	}

	private String convDateTime(String value) {
		DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		try {
			Date d = f.parse(value);
			String format = "dd.MM.yyyy";
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d);
			if (calendar.get(Calendar.MINUTE) > 0
					&& calendar.get(Calendar.HOUR) > 0) {
				format += " HH:mm";
				if (calendar.get(Calendar.SECOND) > 0) {
					format += ":ss";
					if (calendar.get(Calendar.MILLISECOND) > 0) {
						format += ".SSS";
					}
				}
			}
			return new SimpleDateFormat(format).format(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return value;
	}

	public void saveSheet(PrintWriter writer, JReportModel model, String reportTitle) throws SaveReportException {
		writer.println("<Worksheet ss:Name=\""
				+ XMLCoder.replaceSpecChar(reportTitle) + "\">");
		writer.println("<Table ss:ExpandedColumnCount=\""
				+ model.getColumnCount() + "\" ss:ExpandedRowCount=\""
				+ model.getRowCount() + "\">");
		boolean isIndex = false;
		for (int i = 0; i < model.getColumnCount(); i++) {
			double w = ((ReportColumn)model.getColumnModel().getColumn(i)).getWidth();
			if (w != 64) {
				double v = Utilities.round((double)w /  GraphicUtil.getScaleX(), 4);
				String str = "ss:AutoFitWidth=\"0\" ss:Width=\"" + v + "\"";
				if (isIndex)
					str = "ss:Index=\"" + (i + 1) + "\" " + str;
				writer.println("<Column " + str + "/>");
				isIndex = false;
			} else
				isIndex = true;
		}

		for (int r = 0; r < model.getRowCount(); r++) {
			String str;
			if (model.getRowHeight(r) != 17) {
				double v = model.getRowHeight(r) / GraphicUtil.getScaleY();
				str = " ss:AutoFitHeight=\"0\" ss:Height=\"" + v + "\"";
			} else
				str = "";

			writer.println("<Row" + str + ">");
			int c = 0;
			oldCol = 0;
			while (c < model.getColumnCount()) {
				c = saveCell(writer, model, c, r);
			}
			writer.println("</Row>");
		}

		writer.println("</Table>");
		writer
				.println("<WorksheetOptions xmlns=\"urn:schemas-microsoft-com:office:excel\">");
		savePage(writer, model.getReportPage());
		writer.println("</WorksheetOptions>");
		writer.println("</Worksheet>");
	}

	@SuppressWarnings("unchecked")
	private int saveCell(PrintWriter writer, JReportModel model, int col,
			int row) throws SaveReportException {
		Cell cell = model.getReportCell(row, col);
		if (!cell.isNull() && !cell.isChild()) {
			String str;
			if (col > oldCol) {
				str = "ss:Index=\"" + (col + 1) + "\" ";
			} else
				str = "";
			if (cell.getColSpan() > 0)
				str += "ss:MergeAcross=\"" + cell.getColSpan() + "\" ";
			if (cell.getRowSpan() > 0)
				str += "ss:MergeDown=\"" + cell.getRowSpan() + "\" ";

			writer.println("<Cell " + str + "ss:StyleID=\""
					+ ExcelStyleParser.getStyleId(cell.getStyleId()) + "\">");
			if (cell.getValue() instanceof CellValue) {
				writer.print("<Data ss:Type=\"String\">");
				((CellValue<?>) cell.getValue()).write(writer, model, row, col,
						 ReportBook.EXCEL);
				writer.println("</Data>");

			} else {
				String s = cell.getText();
				if (!"".equals(s)) {
					writer.print("<Data ss:Type=\"String\">");
					if (Cell.TEXT_HTML.equals(cell.getContentType()))
						writer.print(getHTMLRenderedText(cell));
					else
						writer.print(XMLCoder.replaceSpecChar(s));
					writer.println("</Data>");
				}
			}
			writer.println("</Cell>");
			oldCol++;
			return col + cell.getColSpan() + 1;
		}
		return col + 1;
	}

	protected String getHTMLRenderedText(Cell cell) {
		if (cell.isNull() || cell.isChild())
			return ""; //$NON-NLS-1$
		JTextComponent tc = getHTMLReportRenderer();
		tc.setText(cell.getText());
		tc.selectAll();
		String result = tc.getSelectedText();
		if (result != null && result.length() > 0) {
			if (result.indexOf('\n') == 0) {
				result = result.substring(1);
			}
		}
		return XMLCoder.replaceSpecChar(result);
	}

	private JTextComponent getHTMLReportRenderer() {
		if (htmlReportRenderer == null) {
			htmlReportRenderer = new HTMLReportRenderer();
		}
		return htmlReportRenderer;
	}

	private void savePage(PrintWriter writer, ReportPage page) {
		writer.println("<PageSetup>");
		String str;
		if (page.getOrientation() == ReportPage.LANDSCAPE)
			str = "Landscape";
		else
			str = "Portrait";
		writer.println("<Layout x:Orientation=\"" + str + "\"/>");
		writer.println("<PageMargins x:Bottom=\"" + page.getBottomMargin(unit)
				+ "\" x:Left=\"" + page.getLeftMargin(unit) + "\" x:Right=\""
				+ page.getRightMargin(unit) + "\" x:Top=\""
				+ page.getTopMargin(unit) + "\"/>");
		writer.println("</PageSetup>");
		writer.println("<Print>");
		MediaSize m;
		if (page.getOrientation() == ReportPage.PORTRAIT) {
			m = new MediaSize((float) page.getWidth(unit), (float) page
					.getHeight(unit), MediaSize.INCH);

		} else
			m = new MediaSize((float) page.getHeight(unit), (float) page
					.getWidth(unit), MediaSize.INCH);
		if (m.equals(MediaSize.ISO.A3))
			str = "" + A3;
		else
			str = "" + A4;
		writer.println("<PaperSizeIndex>" + str + "</PaperSizeIndex>");
		writer.println("</Print>");
	}

}
