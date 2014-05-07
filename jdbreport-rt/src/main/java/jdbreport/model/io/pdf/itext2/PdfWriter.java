/*
 * JDBReport Generator
 * 
 * Copyright (C) 2009-2012 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.model.io.pdf.itext2;

import java.awt.print.Paper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLDocument;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;


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
 * 
 * @version 2.1 03.07.2012
 * 
 */
public class PdfWriter extends ITextWriter {

	public void save(OutputStream out, ReportBook reportBook)
			throws SaveReportException {

		double oldScaleX = GraphicUtil.getScaleX();
		double oldScaleY = GraphicUtil.getScaleY();
		try {
			GraphicUtil.setScaleX(1);
			GraphicUtil.setScaleY(1);

			ReportPage pageFormat = null;
			int i = 0;
			while (i < reportBook.size() - 1 && !reportBook.getReportModel(i).isVisible()) {
				i++;
			}
			
			pageFormat = reportBook.getReportModel(i)
					.getReportPage();
			
			Paper paper = pageFormat.getPaper();
			Rectangle pageSize = new Rectangle(Math.round((float) paper.getWidth()),
					Math.round((float) paper.getHeight()));
			if (pageFormat.getOrientation() == ReportPage.LANDSCAPE) {
				pageSize = pageSize.rotate();
			}
			Document document = new Document(pageSize, Math.round((float) pageFormat
					.getLeftMargin(Units.PT)), Math.round((float) pageFormat
					.getRightMargin(Units.PT)), Math.round((float) pageFormat
					.getTopMargin(Units.PT)), Math.round((float) pageFormat
					.getBottomMargin(Units.PT)));
			try {
				com.lowagie.text.pdf.PdfWriter.getInstance(document, out);
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
			throws DocumentException, BadElementException, IOException,
			SaveReportException {

		model.updatePages(0);

		ReportPage pageFormat = model.getReportPage();
		Paper paper = pageFormat.getPaper();
		Rectangle pageSize = new Rectangle(Math.round((float) paper.getWidth()),
				Math.round((float) paper.getHeight()));

		if (pageFormat.getOrientation() == ReportPage.LANDSCAPE) {
			pageSize = pageSize.rotate();
		}
		document.setPageSize(pageSize);
		document.setMargins(Math.round((float) pageFormat
				.getLeftMargin(Units.PT)), Math.round((float) pageFormat
				.getRightMargin(Units.PT)), Math.round((float) pageFormat
				.getTopMargin(Units.PT)), Math.round((float) pageFormat
				.getBottomMargin(Units.PT)));
		Rectangle viewPageSize = new Rectangle(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin(),
				document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin());
		
		if (listCount > 0) {
			document.newPage();
		}
		listCount++;

		//float columnMargin = (float) Units.PT.setXPixels(model.getColumnModel().getColumnMargin());

		ReportPrintable printable = new ReportPrintable(model);
		int pageCount = printable.calcCountPage(model.getReportPage());
		Map<Integer, PageClip> clips = printable.getPageClips();
		
		int leftCol = 0;
		int topRow = 0;
		int rightCol = 0;
		int bottomRow = 0;
		
		for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) { 
			PageClip pageClip = clips.get(pageIndex);
			if (pageClip == null) break;

				leftCol = pageClip.getLeftCol();
				rightCol = pageClip.getRightCol();
				
				topRow = pageClip.getTopRow();
				bottomRow = pageClip.getBottomRow();
			
			int columnCount = rightCol - leftCol;

			float[] widths = new float[columnCount + 1];
			widths[0] = 1;
			for (int c = 0; c < columnCount; c++) {
				ReportColumn column = (ReportColumn) model
						.getColumnModel()
						.getColumn(leftCol + c);
				widths[c + 1] = (float) Math.round(column.getNativeWidth());
			}
			
			PdfPTable table = createPdfTable(columnCount + 1,
					widths, viewPageSize);
			
			fillTable(model, leftCol, topRow, rightCol, bottomRow,
					table);

			document.add(table);
			
			if (pageIndex < clips.size()) {
				document.newPage();
			}
			
		}
		return listCount;
	}

	private void fillTable(ReportModel model, 
			int leftCol, int topRow, int rightCol, int bottomRow,
			PdfPTable table) throws BadElementException, IOException,
			SaveReportException {
		TableRowModel rowModel = model.getRowModel();
		for (int row = topRow; row < bottomRow; row++) {
			TableRow reportRow = rowModel.getRow(row);

			//Inserts first cell for calculate row height
			PdfPCell firstCell = new PdfPCell();
			firstCell.setBorder(0);
			table.addCell(firstCell);
			
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
	}

	private PdfPTable createPdfTable(int columnCount, float[] widths,
			Rectangle pageSize) throws DocumentException {
		PdfPTable table;
		table = new PdfPTable(columnCount);
		table.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
		table.setTotalWidth(widths);
		float widthPercentage = table.getTotalWidth() / (pageSize.getRight() - pageSize.getLeft()) * 100f;
		table.setWidthPercentage(widthPercentage);
		return table;
	}

	private void assignBorders(CellStyle style, PdfPCell pdfCell) {
		pdfCell.setBorderWidth(0);
		Border border = style.getBorders(Border.LINE_LEFT);
		if (border != null) {
			pdfCell.setBorderWidthLeft(border.getLineWidth());
			pdfCell.setBorderColorLeft(border.getColor());
		}
		border = style.getBorders(Border.LINE_RIGHT);
		if (border != null) {
			pdfCell.setBorderWidthRight(border.getLineWidth());
			pdfCell.setBorderColorRight(border.getColor());
		}
		border = style.getBorders(Border.LINE_TOP);
		if (border != null) {
			pdfCell.setBorderWidthTop(border.getLineWidth());
			pdfCell.setBorderColorTop(border.getColor());
		}
		border = style.getBorders(Border.LINE_BOTTOM);
		if (border != null) {
			pdfCell.setBorderWidthBottom(border.getLineWidth());
			pdfCell.setBorderColorBottom(border.getColor());
		}
	}

	private PdfPCell writeCell(ReportModel model, jdbreport.model.Cell srcCell,
			int row, int col) throws BadElementException, IOException,
			SaveReportException {

		CellStyle style = model.getStyles(srcCell.getStyleId());

		java.awt.Rectangle rect = model.getCellRect(row, col, true, true);

		float h = Math.round((float) Units.PT.setYPixels((int) rect.getHeight()));
		float w = Math.round((float) Units.PT.setXPixels((int) rect.getWidth()));

		PdfPCell pdfCell = null;

		if (srcCell.getPicture() != null) {
			java.awt.Image awtImage = srcCell.getPicture().getImage();
			com.lowagie.text.Image image = awtImageToImage(awtImage, srcCell,
					w, h);
			pdfCell = new PdfPCell(image);
		} else {
			
			String text = null;

			if (srcCell.getValue() instanceof CellValue<?>) {

				StringWriter strWriter = new StringWriter();
				PrintWriter printWriter = new PrintWriter(strWriter);

				if (!((CellValue<?>) srcCell.getValue()).write(printWriter,
						model, row, col, this, ReportBook.PDF)) {
					java.awt.Image awtImage = ((CellValue<?>) srcCell
							.getValue()).getAsImage(model, row, col);
					if (awtImage != null) {
						com.lowagie.text.Image image = awtImageToImage(
								awtImage, srcCell, w, h);
						pdfCell = new PdfPCell(image);
					}
				} else {
					text = strWriter.getBuffer().toString();
				}

			} else {
				if (jdbreport.model.Cell.TEXT_HTML.equals(srcCell
						.getContentType())) {
					pdfCell = new PdfPCell();
					writeHTMLText(model.getStyles(srcCell.getStyleId()),
							srcCell, pdfCell);
				} else {
					text = model.getCellText(srcCell);
				}
			}

			if (pdfCell == null) {
				pdfCell = new PdfPCell();
			}
			
			if (text != null && text.length() > 0) {
				com.lowagie.text.Font font;
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
				if (p != null) {
					pdfCell.setPhrase(p);
				}
				pdfCell.setPadding(1);
				pdfCell.setLeading(0f, 1.1f);
			} else {
				pdfCell.setPadding(0);
			}
		}

		pdfCell.setFixedHeight(h);
		pdfCell.setBackgroundColor(style.getBackground());
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

	private com.lowagie.text.Image awtImageToImage(java.awt.Image awtImage,
			jdbreport.model.Cell srcCell, float w, float h)
			throws BadElementException, IOException {
		
		com.lowagie.text.Image image = com.lowagie.text.Image.getInstance(
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
								.deriveFont((float) ((float) newStyle.getSize() / 2));
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
							.setBackground(newStyle
									.getBackground());
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
