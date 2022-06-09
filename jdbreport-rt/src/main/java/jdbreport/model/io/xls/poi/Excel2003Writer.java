/*
 * Excel2003Writer.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2009-2016 Andrey Kholmanskih
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
package jdbreport.model.io.xls.poi;

import java.awt.Color;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

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
 * @author Andrey Kholmanskih
 * @version 3.1.3 13.10.2016
 */
public class Excel2003Writer implements ReportWriter {

    private final Map<Object, CellStyle> styleMap = new HashMap<>();
    private JTextComponent htmlReportRenderer;
    private Drawing drawing;

    public void save(Writer writer, ReportBook reportBook)
            throws SaveReportException {
        throw new SaveReportException("The method is not supported");
    }

    public void save(File file, ReportBook reportBook)
            throws SaveReportException {
        try {
            file.createNewFile();
            try (FileOutputStream out = new FileOutputStream(file)) {
                save(out, reportBook);
            }
        } catch (IOException e) {
            throw new SaveReportException(e);
        }
    }

    public void save(OutputStream out, ReportBook reportBook)
            throws SaveReportException {
        Workbook wb = createWorkbook();

        Set<String> titles = new HashSet<>();
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

            //char width in points
            float char_width = 5.5f;
            sheet.setColumnWidth(c, (int) ((((ReportColumn) cm.getColumn(c)).getNativeWidth() - 2)
                            / char_width * 256));
        }

        fillStyles(wb, reportBook);

        createRows(model, sheet);

        drawing = sheet.createDrawingPatriarch();
        for (int row = 0; row < model.getRowCount(); row++) {
            saveRow(wb, sheet, reportBook, model, row, createHelper);
        }
        drawing = null;
    }

    private void createRows(ReportModel model, Sheet sheet) {
        for (int row = 0; row < model.getRowCount(); row++) {
            TableRow tableRow = model.getRowModel().getRow(row);
            Row sheetRow = sheet.getRow(row);
            if (sheetRow == null) {
                sheetRow = sheet.createRow(row);
            }
            sheetRow.setHeightInPoints((tableRow).getNativeHeight());
            if (model.isLastRowInPage(row)) {
                sheet.setRowBreak(row);
            }
        }
    }

    private void fillStyles(Workbook wb, ReportBook reportBook) {
        styleMap.clear();
        for (Object styleId : reportBook.getStyleList().keySet()) {
            jdbreport.model.CellStyle style = reportBook.getStyles(styleId);
            if (style != null) {
                styleMap.put(styleId, createStyle(style, wb));
            }
        }
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
                        newCell.setCellValue((Boolean) value);
                    } else if (cell.getValueType() == Type.CURRENCY
                            || cell.getValueType() == Type.FLOAT) {
                        setDoubleValue(wb, createHelper, newCell, styleId, (Number) value);
                    } else if (cell.getValueType() == Type.DATE) {
                        newCell.setCellStyle(getStyle(styleId, Type.DATE, wb, createHelper));
                        newCell.setCellValue((Date) value);
                    } else if (reportBook.getStyles(cell.getStyleId())
                            .getDecimal() != -1) {
                         try {
                            setDoubleValue(wb, createHelper, newCell, styleId, Utils.parseDouble(value
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
                                    createImage(wb, cell,
                                            (RenderedImage) img, row, column,
                                            createHelper);
                                }

                            } else {
                                text = strWriter.getBuffer().toString();
                            }
                        } else {

                            if (jdbreport.model.Cell.TEXT_HTML.equals(cell
                                    .getContentType())) {

                                HTMLDocument doc = getHTMLDocument(cell);
                                List<Content> contentList = Content
                                        .getHTMLContentList(doc);
                                RichTextString richText = createRichTextFromContent(
                                        contentList, createHelper, wb,
                                        (short)newCell.getCellStyle()
                                                .getFontIndex());
                                newCell.setCellValue(richText);
                            } else {
                                text = model.getCellText(cell);
                                newCell.setCellStyle(getStyle(styleId, Type.STRING, wb, createHelper));
                            }
                        }
                        if (text != null) {
                            newCell.setCellValue(text);
                        }
                    }
                } else if (cell.getValueType() != null) {
                    newCell.setCellStyle(getStyle(styleId, cell.getValueType(), wb, createHelper));
                }

                if (cell.getPicture() != null) {
                    createImage(
                            wb,
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

    private CellStyle getStyle(Object styleId, Type cellType, Workbook wb, CreationHelper createHelper) {
        if (cellType == Type.DATE  || cellType == Type.FLOAT || cellType == Type.CURRENCY
                || cellType == Type.STRING) {
            String key = String.valueOf(styleId) + cellType;
            CellStyle style = styleMap.get(key);
            if (style == null) {
                style = wb.createCellStyle();
                CellStyle parentStyle = styleMap.get(styleId);
                if (parentStyle != null) {
                    style.cloneStyleFrom(parentStyle);
                }
                if (cellType == Type.DATE) {
                    style.setDataFormat(
                            createHelper.createDataFormat().getFormat("dd.mm.yyyy"));
                } else if (cellType == Type.STRING) {
                    style.setDataFormat(
                            createHelper.createDataFormat().getFormat("@"));
                } else {
                    style.setDataFormat(
                            createHelper.createDataFormat().getFormat("General"));
                }
                styleMap.put(key, style);
            }
            return style;
        }
        return styleMap.get(styleId);
    }

    private void setDoubleValue(Workbook wb, CreationHelper createHelper, Cell newCell, Object styleId, Number value) {
        newCell.setCellStyle(getStyle(styleId, Type.FLOAT, wb, createHelper));
        newCell.setCellValue(value.doubleValue());
    }

    private void createImage(Workbook wb,
                             jdbreport.model.Cell cell, RenderedImage image, int row,
                             int column, CreationHelper createHelper) {
        int pictureIdx = createImage(wb, cell, image);
        if (pictureIdx >= 0) {

            ClientAnchor anchor = createHelper.createClientAnchor();
            anchor.setCol1(column);
            anchor.setRow1(row);
            anchor.setCol2(column + cell.getColSpan());
            anchor.setRow2(row + cell.getRowSpan());
            Picture pict = drawing.createPicture(anchor, pictureIdx);
            if (!cell.isScaleIcon()) {
                pict.resize(Double.MAX_VALUE, Double.MAX_VALUE);
            } else {
                pict.resize(1.0);
            }
        }
    }

    private HTMLDocument getHTMLDocument(jdbreport.model.Cell cell) {
        getHTMLReportRenderer().setText(cell.getText());
        return (HTMLDocument) getHTMLReportRenderer().getDocument();
    }

    private RichTextString createRichTextFromContent(List<Content> contentList,
                                                     CreationHelper createHelper, Workbook wb, short fontIndex) {
        StringBuilder text = new StringBuilder();
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

            switch (name) {
                case "font-weight":
                    bold = attribute.equals("bold");
                    break;
                case "font-style":
                    italic = attribute.equals("italic");
                    break;
                case "text-decoration":
                    if (attribute.equals("underline")) {
                        underline = true;
                    } else if (attribute.equals("line-through")) {
                        line_through = true;
                    }
                    break;
                case "font-family":
                    family = attribute;
                    break;
                case "font-size":
                    sizeStr = attribute;
                    break;
                case "color":
                    Color fontColor = Utils.colorByName(attribute);
                    if (fontColor == null) {
                        try {
                            fontColor = Utils.stringToColor(attribute);
                        } catch (Exception ignored) {

                        }
                    }
                    if (fontColor != null) {
                        color = colorToIndex(wb, fontColor);
                    }
                    break;
                case "vertical-align":
                    if (attribute.equals("sub")) {
                        sub = true;
                    } else if (attribute.equals("sup")) {
                        sup = true;
                    }
                    break;
            }
        }
        if (family != null || bold || italic || underline || line_through
                || color > 0 || sizeStr != null || sub || sup) {

            font = wb.createFont();
            if (fontIndex > 0) {
                Font parentFont = wb.getFontAt(fontIndex);
                if (parentFont != null) {
                    font.setBold(parentFont.getBold());
                    font.setColor(parentFont.getColor());
                    try {
                        font.setCharSet(parentFont.getCharSet());
                    } catch (Throwable ignored) {
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
                font.setBold(true);
            }
            if (italic) {
                font.setItalic(true);
            }
            if (underline) {
                font.setUnderline(Font.U_SINGLE);
            }
            if (line_through) {
                font.setStrikeout(true);
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
        if (("jpeg".equalsIgnoreCase(format) || "jpg".equalsIgnoreCase(format))) {
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
        return wb.addPicture(bytes,
                "jpg".equals(format) ? Workbook.PICTURE_TYPE_JPEG
                        : Workbook.PICTURE_TYPE_PNG);
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
            font.setBold(true);
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
            newStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
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

    protected BorderStyle getBorder(Border border) {
        if (border.getLineWidth() <= 1f) {
            return BorderStyle.THIN;
        }
        if (border.getLineWidth() <= 2f) {
            return BorderStyle.MEDIUM;
        } else {
            return BorderStyle.THICK;
        }
    }

    protected HorizontalAlignment convertHorizontalAlign(int hAlignment) {
        switch (hAlignment) {
            case jdbreport.model.CellStyle.LEFT:
                return HorizontalAlignment.LEFT;
            case jdbreport.model.CellStyle.RIGHT:
                return HorizontalAlignment.RIGHT;
            case jdbreport.model.CellStyle.CENTER:
                return HorizontalAlignment.CENTER;
            case jdbreport.model.CellStyle.JUSTIFY:
                return HorizontalAlignment.JUSTIFY;
        }
        return HorizontalAlignment.LEFT;
    }

    protected VerticalAlignment convertVerticalAlign(int vAlignment) {
        switch (vAlignment) {
            case jdbreport.model.CellStyle.TOP:
                return VerticalAlignment.TOP;
            case jdbreport.model.CellStyle.BOTTOM:
                return VerticalAlignment.BOTTOM;
            case jdbreport.model.CellStyle.CENTER:
                return VerticalAlignment.CENTER;
        }
        return VerticalAlignment.TOP;
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
