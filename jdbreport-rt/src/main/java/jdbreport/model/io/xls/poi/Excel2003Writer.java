/*
 * Excel2003Writer.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2009-2011 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.model.io.xls.poi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.table.TableColumnModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLDocument;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import and.util.Utilities;

import jdbreport.grid.JReportGrid.HTMLReportRenderer;
import jdbreport.model.Border;
import jdbreport.model.CellValue;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportColumn;
import jdbreport.model.ReportModel;
import jdbreport.model.TableRow;
import jdbreport.model.Units;
import jdbreport.model.Cell.Type;
import jdbreport.model.io.Content;
import jdbreport.model.io.ReportWriter;
import jdbreport.model.io.SaveReportException;
import jdbreport.model.print.ReportPage;
import jdbreport.model.print.ReportPage.PaperSize;
import jdbreport.util.Utils;

/**
 * @version 2.0 30.03.2012
 * @author Andrey Kholmanskih
 * 
 */
public class Excel2003Writer implements ReportWriter {

	private Map<Object, CellStyle> styleMap = new HashMap<Object, CellStyle>();
	private JTextComponent htmlReportRenderer;
	/**
	 * char width in points
	 */
	private static float char_width = 5.5f;
	private Drawing drawing;

	public void save(Writer writer, ReportBook reportBook)
			throws SaveReportException {
		throw new SaveReportException("The method is not supported");
	}

	public void save(File file, ReportBook reportBook)
			throws SaveReportException {
		try {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			try {
				save(out, reportBook);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			throw new SaveReportException(e);
		}
	}

	public void save(OutputStream out, ReportBook reportBook)
			throws SaveReportException {
		Workbook wb = createWorkbook();

		Set<String> titles = new HashSet<String>();
		for (ReportModel model : reportBook) {
			String reportTitle = model.getReportTitle();
			if (reportTitle.length() > 26)
				reportTitle = reportTitle.substring(0, 26);
			String title = reportTitle;
			int n = 1;
			while (titles.contains(reportTitle.toUpperCase())) {
				reportTitle = title + "(" + n++ + ")";
			}
			titles.add(reportTitle.toUpperCase());
			saveSheet(wb, model, reportBook, reportTitle);
		}

		try {
			wb.write(out);
		} catch (IOException e) {
			throw new SaveReportException(e);
		}

	}

	protected Workbook createWorkbook() {
		return new HSSFWorkbook();
	}

	private void saveSheet(Workbook wb, ReportModel model,
			ReportBook reportBook, String reportTitle)
			throws SaveReportException {

		CreationHelper createHelper = wb.getCreationHelper();

		Sheet sheet = wb.createSheet(reportTitle);
		sheet.setDisplayGridlines(reportBook.isShowGrid());
		sheet.setPrintGridlines(false);
		sheet.setFitToPage(model.isStretchPage());
		sheet.setDisplayRowColHeadings(model.isShowHeader()
				|| model.isShowRowHeader());
		ReportPage rp = model.getReportPage();
		sheet.setMargin(Sheet.TopMargin, rp.getTopMargin(Units.INCH));
		sheet.setMargin(Sheet.BottomMargin, rp.getBottomMargin(Units.INCH));
		sheet.setMargin(Sheet.LeftMargin, rp.getLeftMargin(Units.INCH));
		sheet.setMargin(Sheet.RightMargin, rp.getRightMargin(Units.INCH));
		sheet.getPrintSetup().setLandscape(
				rp.getOrientation() == ReportPage.LANDSCAPE);
		short paperSize = convertPaperSize(rp.getPaperSize());
		if (paperSize > 0) {
			sheet.getPrintSetup().setPaperSize(paperSize);
		}

		TableColumnModel cm = model.getColumnModel();

		for (int c = 0; c < model.getColumnCount(); c++) {
			if (model.isColumnBreak(c)) {
				sheet.setColumnBreak(c);
			}
			sheet.setColumnWidth(
					c,
					(int) ((((ReportColumn) cm.getColumn(c)).getNativeWidth() - 2)
							/ char_width * 256));
		}

		styleMap.clear();
		for (Object styleId : reportBook.getStyleList().keySet()) {
			jdbreport.model.CellStyle style = reportBook.getStyles(styleId);
			if (style != null) {
				styleMap.put(styleId, createStyle(style, wb));
			}
		}

		for (int row = 0; row < model.getRowCount(); row++) {
			TableRow tableRow = model.getRowModel().getRow(row);
			Row sheetRow = sheet.getRow(row);
			if (sheetRow == null) {
				sheetRow = sheet.createRow(row);
			}
			sheetRow.setHeightInPoints((float) (tableRow).getNativeHeight());
			if (model.isLastRowInPage(row)) {
				sheet.setRowBreak(row);
			}
		}

		drawing = sheet.createDrawingPatriarch();
		for (int row = 0; row < model.getRowCount(); row++) {
			saveRow(wb, sheet, reportBook, model, row, createHelper);
		}
		drawing = null;
	}

	private short convertPaperSize(PaperSize paperSize) {
		if (paperSize == PaperSize.Letter) {
			return PrintSetup.LETTER_PAPERSIZE;
		}
		if (paperSize == PaperSize.A4) {
			return PrintSetup.A4_PAPERSIZE;
		}
		if (paperSize == PaperSize.A5) {
			return PrintSetup.A5_PAPERSIZE;
		}
		return 0;
	}

	private void saveRow(Workbook wb, Sheet sheet, ReportBook reportBook,
			ReportModel model, int row, CreationHelper createHelper)
			throws SaveReportException {

		TableRow tableRow = model.getRowModel().getRow(row);
		Row sheetRow = sheet.getRow(row);

		for (int column = 0; column < tableRow.getColCount(); column++) {
			jdbreport.model.Cell cell = tableRow.getCellItem(column);
			if (!cell.isChild()) {
				Cell newCell = sheetRow.getCell(column);
				if (newCell == null) {
					newCell = sheetRow.createCell(column);
				}

				Object styleId = cell.getStyleId();
				if (styleId != null) {
					CellStyle newStyle = styleMap.get(styleId);
					if (newStyle != null) {
						newCell.setCellStyle(newStyle);
						if (cell.isSpan()) {
							for (int row1 = row; row1 <= row
									+ cell.getRowSpan(); row1++) {
								Row spanedRow = sheet.getRow(row1);
								if (spanedRow == null) {
									spanedRow = sheet.createRow(row1);
								}
								for (int column1 = column; column1 <= column
										+ cell.getColSpan(); column1++) {
									if (row1 != row || column1 != column) {
										Cell newCell1 = spanedRow
												.createCell(column1);
										newCell1.setCellStyle(newStyle);
									}
								}
							}
						}
					}
				}

				Object value = cell.getValue();

				if (value != null) {
					if (cell.getValueType() == Type.BOOLEAN) {
						newCell.setCellType(Cell.CELL_TYPE_BOOLEAN);
						newCell.setCellValue((Boolean) value);
					} else if (cell.getValueType() == Type.CURRENCY
							|| cell.getValueType() == Type.FLOAT) {
						newCell.setCellType(Cell.CELL_TYPE_NUMERIC);
						newCell.setCellValue(((Number) value).doubleValue());
					} else if (reportBook.getStyles(cell.getStyleId())
							.getDecimal() != -1) {
						newCell.setCellType(Cell.CELL_TYPE_NUMERIC);
						try {
							newCell.setCellValue(Utilities.parseDouble(value
									.toString()));
						} catch (Exception e) {
							newCell.setCellValue(0);
						}

					} else {
						String text = null;
						if (value instanceof CellValue<?>) {
							StringWriter strWriter = new StringWriter();
							PrintWriter printWriter = new PrintWriter(strWriter);
							if (!((CellValue<?>) value).write(printWriter,
									model, row, column, this, ReportBook.XLS)) {
								java.awt.Image img = ((CellValue<?>) cell
										.getValue()).getAsImage(model, row,
										column);
								if (img instanceof RenderedImage) {
									createImage(wb, model, cell,
											(RenderedImage) img, row, column,
											createHelper);
								}

							} else {
								text = strWriter.getBuffer().toString();
							}
						} else {
							newCell.setCellType(Cell.CELL_TYPE_STRING);

							if (jdbreport.model.Cell.TEXT_HTML.equals(cell
									.getContentType())) {

								HTMLDocument doc = getHTMLDocument(cell);
								List<Content> contentList = Content
										.getHTMLContentList(doc);
								if (contentList != null) {
									RichTextString richText = createRichTextFromContent(
											contentList, createHelper, wb,
											newCell.getCellStyle()
													.getFontIndex());
									if (richText != null) {
										newCell.setCellValue(richText);
									}
								}
							} else {
								text = model.getCellText(cell);
							}
						}
						if (text != null) {
							newCell.setCellValue(text);
						}
					}
				}

				if (cell.getPicture() != null) {
					createImage(
							wb,
							model,
							cell,
							Utils.getRenderedImage(cell.getPicture().getIcon()),
							row, column, createHelper);
				}

				if (cell.getCellFormula() != null) {
					newCell.setCellFormula(cell.getCellFormula());
				}

				if (cell.isSpan()) {
					sheet.addMergedRegion(new CellRangeAddress(row, row
							+ cell.getRowSpan(), column, column
							+ cell.getColSpan()));
					column += cell.getColSpan();
				}

			}
		}
	}

	private void createImage(Workbook wb, ReportModel model,
			jdbreport.model.Cell cell, RenderedImage image, int row,
			int column, CreationHelper createHelper) {
		int pictureIdx = createImage(wb, cell, image);
		if (pictureIdx > 0) {

			ClientAnchor anchor = createHelper.createClientAnchor();
			anchor.setCol1(column);
			anchor.setRow1(row);
			anchor.setCol2(column + cell.getColSpan());
			anchor.setRow2(row + cell.getRowSpan());
			Picture pict = drawing.createPicture(anchor, pictureIdx);
			double scale = 1;
			if (cell.isScaleIcon()) {
				Dimension size = model.getCellSize(cell, row, column, false);
				double hscale = 1.0 * size.height
						/ cell.getPicture().getHeight();
				double wscale = 1.0 * size.width / cell.getPicture().getWidth();
				scale = Math.min(hscale, wscale);
			}
			pict.resize(scale);
		}
	}

	private HTMLDocument getHTMLDocument(jdbreport.model.Cell cell) {
		getHTMLReportRenderer().setText(cell.getText());
		return (HTMLDocument) getHTMLReportRenderer().getDocument();
	}

	private RichTextString createRichTextFromContent(List<Content> contentList,
			CreationHelper createHelper, Workbook wb, short fontIndex) {
		StringBuffer text = new StringBuffer();
		int[] idx = new int[contentList.size()];
		int i = 0;
		for (Content content : contentList) {
			idx[i++] = text.length();
			text.append(content.getText());
		}
		RichTextString richText = createHelper.createRichTextString(text
				.toString());
		richText.applyFont(fontIndex);
		for (int n = 0; n < contentList.size(); n++) {
			Content content = contentList.get(n);
			Font font = getFont(fontIndex, content.getAttributeSet(), wb);
			if (font != null) {
				int end = (n < idx.length - 1) ? idx[n + 1] : text.length();
				richText.applyFont(idx[n], end, font);
			}
		}
		return richText;
	}

	private Font getFont(short fontIndex, AttributeSet attributeSet, Workbook wb) {
		Font font = null;
		String family = null;
		String sizeStr = null;
		short color = 0;
		boolean bold = false;
		boolean italic = false;
		boolean underline = false;
		boolean line_through = false;
		boolean sub = false;
		boolean sup = false;
		Enumeration<?> en = attributeSet.getAttributeNames();
		while (en.hasMoreElements()) {
			Object key = en.nextElement();
			String name = key.toString();
			String attribute = attributeSet.getAttribute(key).toString();

			if (name.equals("font-weight")) {
				bold = attribute.equals("bold");
			} else if (name.equals("font-style")) {
				italic = attribute.equals("italic");
			} else if (name.equals("text-decoration")) {
				if (attribute.equals("underline")) {
					underline = true;
				} else if (attribute.equals("line-through")) {
					line_through = true;
				}
			} else if (name.equals("font-family")) {
				family = attribute;
			} else if (name.equals("font-size")) {
				sizeStr = attribute;

			} else if (name.equals("color")) {
				Color fontColor = Utils.colorByName(attribute);
				if (fontColor == null) {
					try {
						fontColor = Utils.stringToColor(attribute);
					} catch (Exception e) {

					}
				}
				if (fontColor != null) {
					color = colorToIndex(wb, fontColor);
				}
			} else if (name.equals("vertical-align")) {
				if (attribute.equals("sub")) {
					sub = true;
				} else if (attribute.equals("sup")) {
					sup = true;
				}
			}
		}
		if (family != null || bold || italic || underline || line_through
				|| color > 0 || sizeStr != null || sub || sup) {

			font = wb.createFont();
			if (fontIndex > 0) {
				Font parentFont = wb.getFontAt(fontIndex);
				if (parentFont != null) {
					font.setBoldweight(parentFont.getBoldweight());
					font.setColor(parentFont.getColor());
					try {
						font.setCharSet(parentFont.getCharSet());
					} catch (Throwable e) {
					}
					font.setFontHeight(parentFont.getFontHeight());
					font.setFontName(parentFont.getFontName());
					font.setItalic(parentFont.getItalic());
					font.setStrikeout(parentFont.getStrikeout());
					font.setUnderline(parentFont.getUnderline());
					font.setTypeOffset(parentFont.getTypeOffset());
				}
			}
			if (family != null) {
				font.setFontName(family);
			}
			if (bold) {
				font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			}
			if (italic) {
				font.setItalic(italic);
			}
			if (underline) {
				font.setUnderline(Font.U_SINGLE);
			}
			if (line_through) {
				font.setStrikeout(line_through);
			}
			if (color > 0) {
				font.setColor(color);
			}
			if (sizeStr != null) {
				short size = (short) Float.parseFloat(sizeStr);
				if (sizeStr.charAt(0) == '+' || sizeStr.charAt(0) == '-') {
					size = (short) (Content.pointToSize(font
							.getFontHeightInPoints()) + size);
				}
				font.setFontHeightInPoints(Content.sizeToPoints(size));
			}
			if (sup) {
				font.setTypeOffset(Font.SS_SUPER);
			} else if (sub) {
				font.setTypeOffset(Font.SS_SUB);
			}
		}
		return font;
	}

	private JTextComponent getHTMLReportRenderer() {
		if (htmlReportRenderer == null) {
			htmlReportRenderer = new HTMLReportRenderer();
		}
		return htmlReportRenderer;
	}

	private int createImage(Workbook wb, jdbreport.model.Cell cell,
			RenderedImage image) {

		String format = cell.getImageFormat();
		if (format != null
				&& ("jpeg".equals(format.toLowerCase()) || "jpg".equals(format
						.toLowerCase()))) {
			format = "jpg";
		} else {
			format = "png";
		}

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, format, stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] bytes = stream.toByteArray();
		int pictureIdx = wb.addPicture(bytes,
				"jpg".equals(format) ? Workbook.PICTURE_TYPE_JPEG
						: Workbook.PICTURE_TYPE_PNG);
		return pictureIdx;
	}

	protected CellStyle createStyle(jdbreport.model.CellStyle style, Workbook wb) {

		CellStyle newStyle = wb.createCellStyle();
		newStyle.setAlignment(convertHorizontalAlign(style
				.getHorizontalAlignment()));
		newStyle.setVerticalAlignment(convertVerticalAlign(style
				.getVerticalAlignment()));

		Border border = style.getBorders(Border.LINE_BOTTOM);
		if (border != null) {
			newStyle.setBorderBottom(getBorder(border));
			newStyle.setBottomBorderColor(colorToIndex(wb, border.getColor()));
		}
		border = style.getBorders(Border.LINE_TOP);
		if (border != null) {
			newStyle.setBorderTop(getBorder(border));
			newStyle.setTopBorderColor(colorToIndex(wb, border.getColor()));
		}
		border = style.getBorders(Border.LINE_LEFT);
		if (border != null) {
			newStyle.setBorderLeft(getBorder(border));
			newStyle.setLeftBorderColor(colorToIndex(wb, border.getColor()));
		}
		border = style.getBorders(Border.LINE_RIGHT);
		if (border != null) {
			newStyle.setBorderRight(getBorder(border));
			newStyle.setRightBorderColor(colorToIndex(wb, border.getColor()));
		}

		Font font = wb.createFont();
		font.setFontName(style.getFamily());
		if (style.isBold()) {
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		}
		font.setItalic(style.isItalic());
		if (style.isUnderline()) {
			font.setUnderline(Font.U_SINGLE);
		}
		if (style.isStrikethrough()) {
			font.setStrikeout(true);
		}
		font.setFontHeightInPoints((short) style.getSize());
		if (style.getForegroundColor() != null
				&& !style.getForegroundColor().equals(Color.black)) {
			font.setColor(colorToIndex(wb, style.getForegroundColor()));
		}

		newStyle.setFont(font);

		if (style.getBackground() != null
				&& !style.getBackground().equals(Color.white)) {
			short colorIndex = colorToIndex(wb, style.getBackground());
			newStyle.setFillForegroundColor(colorIndex);
			newStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		}

		if (style.getAngle() != 0) {
			int angle = style.getAngle();
			if (angle > 90 && angle <= 180) {
				angle = 90;
			} else if (angle > 180 && angle <= 270) {
				angle = -90;
			} else if (angle > 270) {
				angle = -(360 - angle);
			}
			newStyle.setRotation((short) angle);
		}

		newStyle.setWrapText(style.isWrapLine());

		return newStyle;
	}

	protected short getBorder(Border border) {
		if (border.getLineWidth() <= 1f) {
			return CellStyle.BORDER_THIN;
		}
		if (border.getLineWidth() <= 2f) {
			return CellStyle.BORDER_MEDIUM;
		} else {
			return CellStyle.BORDER_THICK;
		}
	}

	protected short convertHorizontalAlign(int hAlignment) {
		switch (hAlignment) {
		case jdbreport.model.CellStyle.LEFT:
			return CellStyle.ALIGN_LEFT;
		case jdbreport.model.CellStyle.RIGHT:
			return CellStyle.ALIGN_RIGHT;
		case jdbreport.model.CellStyle.CENTER:
			return CellStyle.ALIGN_CENTER;
		case jdbreport.model.CellStyle.JUSTIFY:
			return CellStyle.ALIGN_JUSTIFY;
		}
		return 0;
	}

	protected short convertVerticalAlign(int vAlignment) {
		switch (vAlignment) {
		case jdbreport.model.CellStyle.TOP:
			return CellStyle.VERTICAL_TOP;
		case jdbreport.model.CellStyle.BOTTOM:
			return CellStyle.VERTICAL_BOTTOM;
		case jdbreport.model.CellStyle.CENTER:
			return CellStyle.VERTICAL_CENTER;
		}
		return 0;
	}

	protected short colorToIndex(Workbook wb, Color color) {
		if (color == null) {
			return 0;
		}
		if (Color.black.equals(color)) {
			return IndexedColors.BLACK.getIndex();
		}
		if (Color.white.equals(color)) {
			return IndexedColors.WHITE.getIndex();
		}
		if (Color.blue.equals(color) || Color.blue.darker().equals(color)
				|| Color.blue.brighter().equals(color)) {
			return IndexedColors.BLUE.getIndex();
		}
		if (Color.red.equals(color) || Color.red.darker().equals(color)
				|| Color.red.brighter().equals(color)) {
			return IndexedColors.RED.getIndex();
		}
		if (Color.LIGHT_GRAY.equals(color)) {
			return IndexedColors.GREY_25_PERCENT.getIndex();
		}
		if (Color.GRAY.equals(color)) {
			return IndexedColors.GREY_50_PERCENT.getIndex();
		}
		if (Color.DARK_GRAY.equals(color)) {
			return IndexedColors.GREY_80_PERCENT.getIndex();
		}
		if (Color.green.equals(color) || Color.green.brighter().equals(color)
				|| Color.green.darker().equals(color)) {
			return IndexedColors.GREEN.getIndex();
		}
		if (Color.magenta.equals(color) || Color.magenta.darker().equals(color)
				|| Color.magenta.brighter().equals(color)) {
			return IndexedColors.MAROON.getIndex();
		}
		if (Color.orange.equals(color) || Color.orange.darker().equals(color)
				|| Color.orange.brighter().equals(color)) {
			return IndexedColors.ORANGE.getIndex();
		}
		if (Color.pink.equals(color) || Color.pink.darker().equals(color)
				|| Color.pink.brighter().equals(color)) {
			return IndexedColors.PINK.getIndex();
		}
		if (Color.yellow.equals(color) || Color.yellow.darker().equals(color)
				|| Color.yellow.brighter().equals(color)) {
			return IndexedColors.YELLOW.getIndex();
		}

		byte r = (byte) color.getRed();
		byte g = (byte) color.getGreen();
		byte b = (byte) color.getBlue();
		HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette();
		HSSFColor hssColor = palette.findColor(r, g, b);

		try {
			if (hssColor == null) {
				hssColor = palette.addColor(r, g, b);
			}
			return hssColor.getIndex();
		} catch (RuntimeException e) {
			hssColor = palette.findSimilarColor(r, g, b);
			return hssColor != null ? hssColor.getIndex() : 0;
		}
	}

	public String write(String fileName, Object resource)
			throws SaveReportException {
		throw new SaveReportException("The method is not supported");
	}

}