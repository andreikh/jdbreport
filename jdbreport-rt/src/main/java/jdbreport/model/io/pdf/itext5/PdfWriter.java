/*
 * JDBReport Generator
 * 
 * Copyright (C) 2009-2014 Andrey Kholmanskih
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
package jdbreport.model.io.pdf.itext5;

import java.awt.print.Paper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLDocument;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import jdbreport.grid.ReportPrintable;
import jdbreport.grid.ReportPrintable.PageClip;
import jdbreport.model.Border;
import jdbreport.model.CellStyle;
import jdbreport.model.CellValue;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportColumn;
import jdbreport.model.ReportModel;
import jdbreport.model.TableRow;
import jdbreport.model.TableRowModel;
import jdbreport.model.Units;
import jdbreport.model.io.Content;
import jdbreport.model.io.SaveReportException;
import jdbreport.model.print.ReportPage;
import jdbreport.util.GraphicUtil;

/**
 * @author Andrey Kholmanskih
 * @version 3.0.13.12.2014
 */
public class PdfWriter extends ITextWriter {

    public void save(OutputStream out, ReportBook reportBook)
            throws SaveReportException {

        double oldScaleX = GraphicUtil.getScaleX();
        double oldScaleY = GraphicUtil.getScaleY();
        try {
            GraphicUtil.setScaleX(1);
            GraphicUtil.setScaleY(1);


            ReportPage pageFormat;
            int i = 0;
            while (i < reportBook.size() - 1 && !reportBook.getReportModel(i).isVisible()) {
                i++;
            }
            pageFormat = reportBook.getReportModel(i)
                    .getReportPage();

            Paper paper = pageFormat.getPaper();
            Rectangle pageSize = new Rectangle((float) paper.getWidth(),
                    (float) paper.getHeight());
            if (pageFormat.getOrientation() == ReportPage.LANDSCAPE) {
                pageSize = pageSize.rotate();
            }
            Document document = new Document(pageSize, (float) pageFormat
                    .getLeftMargin(Units.PT), (float) pageFormat
                    .getRightMargin(Units.PT), (float) pageFormat
                    .getTopMargin(Units.PT), (float) pageFormat
                    .getBottomMargin(Units.PT));
            try {
                com.itextpdf.text.pdf.PdfWriter.getInstance(document, out);
                document.addTitle(reportBook.getReportCaption());
                document.addCreator("JDBReport using iText");
                document.addAuthor(System.getProperty("user.name"));
                document.open();
                int listCount = 0;
                try {
                    for (ReportModel model : reportBook) {
                        if (model.isVisible()) {
                            listCount = saveSheet(document, listCount, model);
                        }
                    }
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (DocumentException e) {
                throw new SaveReportException(e);
            }
        } finally {
            GraphicUtil.setScaleX(oldScaleX);
            GraphicUtil.setScaleY(oldScaleY);
        }
    }

    private int saveSheet(Document document, int listCount, ReportModel model)
            throws DocumentException, IOException,
            SaveReportException {

        model.updatePages(0);

        ReportPage pageFormat = model.getReportPage();
        Paper paper = pageFormat.getPaper();
        Rectangle pageSize = new Rectangle((float) paper.getWidth(),
                (float) paper.getHeight());

        if (pageFormat.getOrientation() == ReportPage.LANDSCAPE) {
            pageSize = pageSize.rotate();
        }
        document.setPageSize(pageSize);
        document.setMargins((float) pageFormat
                .getLeftMargin(Units.PT), (float) pageFormat
                .getRightMargin(Units.PT), (float) pageFormat
                .getTopMargin(Units.PT), (float) pageFormat
                .getBottomMargin(Units.PT));
        if (listCount > 0) {
            document.newPage();
        }
        listCount++;


        TableRowModel rowModel = model.getRowModel();

        float columnMargin = (float) Units.PT.setXPixels(model
                .getColumnModel().getColumnMargin());

        ReportPrintable printable = new ReportPrintable(model);
        int pageCount = printable.calcCountPage(model.getReportPage());
        Map<Integer, PageClip> clips = printable.getPageClips();

        int leftCol;
        int topRow;
        int rightCol;
        int bottomRow;

        for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
            PageClip pageClip = clips.get(pageIndex);
            if (pageClip == null) break;

            leftCol = pageClip.getLeftCol();
            rightCol = pageClip.getRightCol();

            topRow = pageClip.getTopRow();
            bottomRow = pageClip.getBottomRow();

            int columnCount = rightCol - leftCol;

            float[] widths = new float[columnCount];
            for (int c = 0; c < widths.length; c++) {
                ReportColumn column = (ReportColumn) model
                        .getColumnModel()
                        .getColumn(leftCol + c);
                widths[c] = (float) column.getNativeWidth()
                        + columnMargin;
            }
            PdfPTable table = createPdfTable(columnCount,
                    widths, document.getPageSize());

            for (int row = topRow; row < bottomRow; row++) {
                TableRow reportRow = rowModel.getRow(row);

                for (int col = leftCol; col < rightCol; col++) {
                    jdbreport.model.Cell srcCell = reportRow
                            .getCellItem(col);
                    if (!srcCell.isChild()) {
                        PdfPCell pdfCell = writeCell(model,
                                srcCell, row, col);
                        table.addCell(pdfCell);
                    }
                }


            }

            document.add(table);

            if (pageIndex < clips.size()) {
                document.newPage();
            }

        }
        return listCount;
    }

    private PdfPTable createPdfTable(int columnCount, float[] widths,
                                     Rectangle pageSize) throws DocumentException {
        PdfPTable table;
        table = new PdfPTable(columnCount);
        table.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
        table.setTotalWidth(widths);
        table.setWidthPercentage(widths, pageSize);
        return table;
    }

    private void assignBorders(CellStyle style, PdfPCell pdfCell) {
        pdfCell.setBorderWidth(0);
        Border border = style.getBorders(Border.LINE_LEFT);
        if (border != null) {
            pdfCell.setBorderWidthLeft(border.getLineWidth());
            pdfCell.setBorderColorLeft(new BaseColor(border.getColor()));
        }
        border = style.getBorders(Border.LINE_RIGHT);
        if (border != null) {
            pdfCell.setBorderWidthRight(border.getLineWidth());
            pdfCell.setBorderColorRight(new BaseColor(border.getColor()));
        }
        border = style.getBorders(Border.LINE_TOP);
        if (border != null) {
            pdfCell.setBorderWidthTop(border.getLineWidth());
            pdfCell.setBorderColorTop(new BaseColor(border.getColor()));
        }
        border = style.getBorders(Border.LINE_BOTTOM);
        if (border != null) {
            pdfCell.setBorderWidthBottom(border.getLineWidth());
            pdfCell.setBorderColorBottom(new BaseColor(border.getColor()));
        }
    }

    private PdfPCell writeCell(ReportModel model, jdbreport.model.Cell srcCell,
                               int row, int col) throws BadElementException, IOException,
            SaveReportException {

        CellStyle style = model.getStyles(srcCell.getStyleId());

        java.awt.Rectangle rect = model.getCellRect(row, col, true, true);

        float h = (float) Units.PT.setYPixels((int) rect.getHeight());
        float w = (float) Units.PT.setXPixels((int) rect.getWidth());

        PdfPCell pdfCell;

        if (srcCell.getPicture() != null) {
            Icon icon = srcCell.getPicture().getIcon();
            java.awt.Image awtImage = ((ImageIcon) icon).getImage();

            com.itextpdf.text.Image image = awtImageToImage(awtImage, srcCell,
                    w, h);
            pdfCell = new PdfPCell(image);
        } else {
            pdfCell = new PdfPCell();

            String text = null;

            if (srcCell.getValue() instanceof CellValue<?>) {

                StringWriter strWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(strWriter);

                if (!((CellValue<?>) srcCell.getValue()).write(printWriter,
                        model, row, col, this, ReportBook.PDF)) {
                    java.awt.Image awtImage = ((CellValue<?>) srcCell
                            .getValue()).getAsImage(model, row, col);
                    if (awtImage != null) {
                        com.itextpdf.text.Image image = awtImageToImage(
                                awtImage, srcCell, w, h);
                        pdfCell.setImage(image);
                    }
                } else {
                    text = strWriter.getBuffer().toString();
                }

            } else {
                if (jdbreport.model.Cell.TEXT_HTML.equals(srcCell
                        .getContentType())) {
                    writeHTMLText(model.getStyles(srcCell.getStyleId()),
                            srcCell, pdfCell);
                } else {
                    text = model.getCellText(srcCell);
                }
            }

            if (text != null && text.length() > 0) {
                com.itextpdf.text.Font font;
                if (fonts.containsKey(style.getId())) {
                    font = fonts.get(style.getId());
                } else {
                    font = getFontMapper().styleToPdf(style);
                    fonts.put(style.getId(), font);
                }
                Paragraph p;
                if (font != null) {
                    p = new Paragraph(text, font);
                } else {
                    p = new Paragraph(text);
                }
                pdfCell.setPhrase(p);
            }
        }

        pdfCell.setFixedHeight(h);
        pdfCell.setPadding(1);
        pdfCell.setBackgroundColor(new BaseColor(style.getBackground()));
        pdfCell.setHorizontalAlignment(toPdfHAlignment(style
                .getHorizontalAlignment()));
        pdfCell.setVerticalAlignment(toPdfVAlignment(style
                .getVerticalAlignment()));

        if (style.getAngle() != 0) {
            pdfCell.setRotation(roundAngle(style.getAngle()));
        }

        assignBorders(style, pdfCell);
        pdfCell.setNoWrap(!style.isWrapLine());

        if (srcCell.getColSpan() > 0) {
            pdfCell.setColspan(srcCell.getColSpan() + 1);
        }
        if (srcCell.getRowSpan() > 0) {
            pdfCell.setRowspan(srcCell.getRowSpan() + 1);
        }

        return pdfCell;
    }

    private com.itextpdf.text.Image awtImageToImage(java.awt.Image awtImage,
                                                    jdbreport.model.Cell srcCell, float w, float h)
            throws BadElementException, IOException {
        com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(
                awtImage, null);
        boolean scaled = srcCell.isScaleIcon();

        double kx = 1.0 / GraphicUtil.getScreenScaleX();
        double ky = 1.0 / GraphicUtil.getScreenScaleY();

        float imageWidth = (float) (image.getWidth() * kx);
        float imageHeight = (float) (image.getHeight() * ky);

        if (!scaled) {
            if (imageWidth < w) {
                w = imageWidth;
            }
            if (imageHeight < h) {
                h = imageHeight;
            }
        }

        if (imageWidth >= w || imageHeight >= h) {
            scaled = true;
        }

        if (scaled) {
            image.scaleAbsolute(w - 2, h - 2);
        }
        return image;
    }

    protected void writeHTMLText(CellStyle parentStyle,
                                 jdbreport.model.Cell cell, PdfPCell pdfCell) {
        if (cell.isNull() || cell.isChild())
            return;

        JTextComponent tc = getHTMLReportRenderer();
        tc.setText(cell.getText());
        List<Content> contentList = Content
                .getHTMLContentList((HTMLDocument) tc.getDocument());
        if (contentList != null) {
            Phrase phrase = new Phrase();
            for (Content content : contentList) {
                CellStyle newStyle = content.createTextStyle(parentStyle,
                        parentStyle);
                if (newStyle == null) {
                    newStyle = parentStyle;
                }
                if (newStyle != null) {
                    if (newStyle.getTypeOffset() == CellStyle.SS_SUPER
                            || newStyle.getTypeOffset() == CellStyle.SS_SUB) {
                        newStyle = newStyle
                                .deriveFont((float) newStyle.getSize() / 2);
                    }
                    int i = textStyles.indexOf(newStyle);
                    if (i < 0) {
                        textStyles.add(newStyle);
                        i = textStyles.size() - 1;
                    }
                    Font font;
                    String styleId = "T" + (i + 1);
                    if (fonts.containsKey(styleId)) {
                        font = fonts.get(styleId);
                    } else {
                        font = getFontMapper().styleToPdf(newStyle);
                        fonts.put(styleId, font);
                    }
                    Chunk chunk = new Chunk(content.getText(), font);
                    chunk
                            .setBackground(new BaseColor(newStyle
                                    .getBackground()));
                    if (newStyle.getTypeOffset() == CellStyle.SS_SUPER) {
                        chunk.setTextRise(newStyle.getSize() / 2);
                    } else if (newStyle.getTypeOffset() == CellStyle.SS_SUB) {
                        chunk.setTextRise(-newStyle.getSize() / 2);
                    }
                    phrase.add(chunk);
                } else {
                    phrase.add(new Chunk(content.getText()));
                }
            }
            pdfCell.setPhrase(phrase);
        }
    }

}
