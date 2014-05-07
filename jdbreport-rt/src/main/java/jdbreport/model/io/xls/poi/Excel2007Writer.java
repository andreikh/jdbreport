/*
 * Excel2007Writer.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2009 Andrey Kholmanskih. All rights reserved.
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

import jdbreport.model.Border;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @version 1.0 15.08.2009
 * @author Andrey Kholmanskih
 * 
 */
public class Excel2007Writer extends Excel2003Writer {

	protected Workbook createWorkbook() {
		return new XSSFWorkbook();
	}

	protected CellStyle createStyle(jdbreport.model.CellStyle style, Workbook wb) {

		XSSFCellStyle newStyle = (XSSFCellStyle) wb.createCellStyle();
		newStyle.setAlignment(convertHorizontalAlign(style
				.getHorizontalAlignment()));
		newStyle.setVerticalAlignment(convertVerticalAlign(style
				.getVerticalAlignment()));

		Border border = style.getBorders(Border.LINE_BOTTOM);
		if (border != null) {
			newStyle.setBorderBottom(getBorder(border));
			newStyle.setBottomBorderColor(new XSSFColor(border.getColor()));
		}
		border = style.getBorders(Border.LINE_TOP);
		if (border != null) {
			newStyle.setBorderTop(getBorder(border));
			newStyle.setTopBorderColor(new XSSFColor(border.getColor()));
		}
		border = style.getBorders(Border.LINE_LEFT);
		if (border != null) {
			newStyle.setBorderLeft(getBorder(border));
			newStyle.setLeftBorderColor(new XSSFColor(border.getColor()));
		}
		border = style.getBorders(Border.LINE_RIGHT);
		if (border != null) {
			newStyle.setBorderRight(getBorder(border));
			newStyle.setRightBorderColor(new XSSFColor(border.getColor()));
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
			newStyle.setFillForegroundColor(new XSSFColor(style.getBackground()));
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

		XSSFColor xssfColor = new XSSFColor(color);
		return xssfColor.getIndexed();
	}

}
