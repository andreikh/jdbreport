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
package jdbreport.model.io.html;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import javax.imageio.ImageIO;

import jdbreport.model.Border;
import jdbreport.model.Cell;
import jdbreport.model.CellStyle;
import jdbreport.model.CellValue;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportModel;
import jdbreport.model.io.ReportWriter;
import jdbreport.model.io.SaveReportException;
import jdbreport.util.Utils;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class HTMLWriter implements ReportWriter {

	private String fileName = null;


	public HTMLWriter() {
		super();
	}

	public void save(OutputStream out, ReportBook reportBook)
			throws SaveReportException {
		try (PrintWriter fw = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(out, java.nio.charset.Charset
						.forName("UTF-8"))))) {
			save(fw, reportBook);
		}

	}

	public void save(File file, ReportBook reportBook)
			throws SaveReportException {
		try {
			setFileName(file.getPath());
			file.createNewFile();
			try (FileOutputStream out = new FileOutputStream(file)) {
				save(out, reportBook);
			}
		} catch (IOException e) {
			throw new SaveReportException(e);
		}
	}

	public void save(Writer writer, ReportBook reportBook)
			throws SaveReportException {
		PrintWriter fw;
		if (writer instanceof PrintWriter)
			fw = (PrintWriter) writer;
		else
			fw = new PrintWriter(writer);

		fw.println("<html>");
		fw.println("<head>");
		fw.println("<meta name=\"generator\" content=\"JDBReport\">");
		String d = String.format("%1$tY-%1$tm-%1$teT%1$tH:%1$tM:%1$tS", System
				.currentTimeMillis());
		fw.println("<meta name=\"date\" content=\"" + d + "\">");
		fw
				.println("<meta http-equiv=Content-Type content=\"text/html; charset=UTF-8\">");
		fw.println("<title>" + reportBook.getReportCaption() + "</title>");
		fw.println("<style type=\"text/css\">");
		fw.println("<!--");
		fw.println(getCss(CellStyle.getDefaultStyle()));
		for (Object o : reportBook.getStyleList().keySet()) {
			fw.println(getCss(reportBook.getStyles(o)));
		}
		fw.println("-->");
		fw.println("</style>");
		fw.println("</head>");
		fw.println("<body>");
		saveBody(reportBook, fw, false);
		fw.println("</body>");
		fw.println("</html>");
	}

	protected void saveBody(ReportBook reportBook, PrintWriter fw, boolean saveStyle)
			throws SaveReportException {
		for (int i = 0; i < reportBook.size(); i++)
			saveToHTML(fw, reportBook.getReportModel(i), saveStyle);
	}

	/**
	 * @param fileName
	 *            The fileName to set.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return Returns the fileName.
	 */
	public String getFileName() {
		return fileName;
	}

	private String getCss(CellStyle style) {
		StringBuilder s = new StringBuilder();
		s.append(".s");
		if (style.getId() == null || style.getId().equals(-1))
			s.append("Default");
		else
			s.append(style.getId());
		s.append('\t');
		s.append('{');
		s.append(styleToStr(style));

		s.append('}');
		return s.toString();
	}

	private String styleToStr(CellStyle style) {
		StringBuffer s = new StringBuffer();
		if (style.getForegroundColor() != Color.BLACK) {
			s.append("color: rgb(");
			s.append(Utils.colorToString(style.getForegroundColor()));
			s.append(')');
			s.append(';');
		}
		s.append("font-size:");
		s.append(style.getSize());
		s.append("pt;");
		if (style.isBold())
			s.append("font-weight:bold;");
		else
			s.append("font-weight:normal;");
		if (style.isItalic())
			s.append("font-style:italic;");
		else
			s.append("font-style:normal;");
		if (style.isUnderline())
			s.append("text-decoration:underline;");
		else if (style.isStrikethrough())
			s.append("text-decoration:line-through;");
		s.append("font-family:");
		s.append(style.getFamily());
		s.append(';');
		appendLineStyle(style.getBorders(Border.LINE_LEFT), "left", s);
		appendLineStyle(style.getBorders(Border.LINE_TOP), "top", s);
		appendLineStyle(style.getBorders(Border.LINE_RIGHT), "right", s);
		appendLineStyle(style.getBorders(Border.LINE_BOTTOM), "bottom", s);

		if (style.getBackground() != Color.WHITE) {
			s.append("background-color: rgb(");
			s.append(Utils.colorToString(style.getBackground()));
			s.append(')');
			s.append(';');
		}
		if (style.getHorizontalAlignment() == CellStyle.RIGHT)
			s.append("text-align:right;");
		else if (style.getHorizontalAlignment() == CellStyle.CENTER)
			s.append("text-align:center;");
		if (style.getVerticalAlignment() == CellStyle.BOTTOM)
			s.append("vertical-align:bottom;");
		else if (style.getVerticalAlignment() == CellStyle.CENTER)
			s.append("vertical-align:middle;");
		return s.toString();
	}

	private void appendLineStyle(Border border, String position, StringBuffer s) {
		if (border != null && border.getLineWidth() > 0) {
			s.append("border-");
			s.append(position);
			s.append(": ");
			float w = border.getLineWidth();
			if (w > 0 && w < 1f) w = 1f;
			s.append(w);
			s.append("pt ");
			switch (border.getStyle()) {
			case Border.psDash:
			case Border.psDashDot:
				s.append("dashed ");
				break;
			case Border.psDashDotDot:
			case Border.psDot:
				s.append("dotted ");
				break;
			case Border.psDouble:
				s.append("double ");
				break;
			default:
				s.append("solid ");
			}
			s.append("rgb(");
			s.append(Utils.colorToString(border.getColor()));
			s.append(')');
			s.append(';');
		}
	}

	@SuppressWarnings("unchecked")
	private void saveToHTML(PrintWriter fw, ReportModel model, boolean saveStyle) throws SaveReportException {
		int colCount = model.getColumnCount();
		int[] colWidths = new int[colCount];
		int leftCol;
		int rightCol = -1;
		do {
			rightCol++;
			leftCol = rightCol;
			while (rightCol < colCount - 1 && !model.isColumnBreak(rightCol))
				rightCol++;
			int tableWidth = 0;
			for (int i = leftCol; i <= rightCol; i++) {
				tableWidth += model.getColumnWidth(i);
				colWidths[i] = model.getColumnWidth(i);
			}
			String tableHeader = "<table CELLSPACING=0 WIDTH=" + tableWidth
					+ " >";
			fw.println(tableHeader);
			for (int curRow = 0; curRow < model.getRowCount(); curRow++) {
				int curCol = leftCol;
				String heightRow = " HEIGHT=" + model.getRowHeight(curRow);
				fw.println("<TR VALIGN=TOP>");
				while (curCol <= rightCol) {
					String widthCol = "";
					if (colWidths[curCol] > 0)
						widthCol = " WIDTH=" + colWidths[curCol];
					Cell cell = model.getReportCell(curRow, curCol);
					if (!cell.isChild()) {
						if (cell.isNull()) {
							fw.println("<TD>&nbsp");
						} else {
							String imgTag = "";
							String colSpan = "";
							String parametrs = "";

							if (cell.getRowSpan() > 0) {
								parametrs += " ROWSPAN="
										+ (cell.getRowSpan() + 1);
							} else {
								parametrs += heightRow;
								heightRow = "";
							}

							if (cell.getStyleId() != null
									&& !cell.getStyleId().equals(-1))
								if (saveStyle) {
									parametrs += " style=\"" + styleToStr(model.getStyles(cell.getStyleId())) + "\"";
								} else {
									parametrs += " class=s" + cell.getStyleId();
								}
							else {
								if (saveStyle) {
									parametrs += " style=\"" + styleToStr(CellStyle.getDefaultStyle()) + "\"";
								} else {
									parametrs += " class=sDefault";
								}
							}
							
							if (cell.getColSpan() > 0)
								if ("".equals(colSpan)) {
									colSpan = " COLSPAN="
											+ (cell.getColSpan() + 1);
								}

							if (colSpan.length() > 0)
								parametrs += colSpan;
							else {
								parametrs += widthCol;
								colWidths[curCol] = 0;
							}

							if (cell.getPicture() != null) {
								imgTag = getImg(model,
										curCol,
										curRow,
										cell,cell
										.getPicture()
												.getRenderedImage());
							}

							fw.print("<TD" + parametrs + ">" + imgTag);
							if (cell.getValue() instanceof CellValue) {
								if (!((CellValue<?>) cell.getValue()).write(fw,
										model, curRow, curCol, this, ReportBook.HTML)) {
									Image img = ((CellValue<?>) cell.getValue())
											.getAsImage(model, curRow, curCol);
									if (img instanceof RenderedImage) {
										String iTag = getImg(model, curCol, curRow,
												cell, (RenderedImage) img);
										fw.print(iTag);
									}
								} else {
									fw.print("&nbsp;");
								}
							} else {
								String text;
								if (Cell.TEXT_HTML
										.equals(cell.getContentType())) {
									text = cell.getText().trim();
									int i = text.toLowerCase()
											.indexOf("<body>");
									if (i >= 0) {
										text = text.substring(i + 6);
										i = text.toLowerCase().indexOf(
												"</body>");
										if (i >= 0) {
											text = text.substring(0, i);
										}
									} else {
										if (text.toLowerCase().startsWith(
												"<html>")) {
											text = text.substring(6);
											if (text.toLowerCase().endsWith(
													"</html>")) {
												text = text.substring(0, text
														.length() - 7);
											}
											int i1 = text.toLowerCase()
													.indexOf("<head>");
											int i2 = text.toLowerCase()
													.indexOf("</head>");
											if (i1 >= 0 && i2 >= 0) {
												text = text.substring(i2 + 7);
											}
										}
									}

								} else {
									text = model.getCellText(cell);
									text = formatText(text);
								}
								if ("".equals(text.trim())) {
									if ("".equals(imgTag))
										text = "&nbsp;";
								} else {
									if (!imgTag.equals("")) {
										text = "<br>" + text;
									}
								}
								fw.println(text);
							}

						}
					}
					if (cell.getColSpan() > 0) {
							curCol += cell.getColSpan();
					}
					curCol++;
				}
				fw.println("</TR>");
				if (model.getRowModel().getRow(curRow).isPageBreak()
						&& curRow < model.getRowCount()) {
					fw.println("</table>");
					fw.println(tableHeader);
					for (int i = leftCol; i <= rightCol; i++)
						colWidths[i] = model.getColumnWidth(i);
				}
			}
			fw.println("</table>");
		} while (rightCol < (colCount - 1));

	}

	private String formatText(String text) {
		boolean isPrevSpace = true;
		StringBuilder result = new StringBuilder();
		int n = 0;
		while (n < text.length()) {
			if (text.charAt(n) == ' ') {
				if (isPrevSpace) {
					result.append("&nbsp;");
					isPrevSpace = false;
				} else {
					result.append(' ');
					isPrevSpace = true;
				}
			} else {
				isPrevSpace = false;
				if (text.charAt(n) == 10) {
					result.append("<br>");
					isPrevSpace = true;
				} else if (text.charAt(n) == 13) {
					if (n < text.length() - 1 && text.charAt(n + 1) == 10)
						n++;
					result.append("<br>");
					isPrevSpace = true;
				} else if (text.charAt(n) == '\t')
					result.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp");
				else
					result.append(text.charAt(n));
			}
			n++;
		}
		return result.toString();
	}

	private String getImg(ReportModel model, final int col, final int row, Cell cell, RenderedImage image) {
		if (fileName == null)
			return "";
		StringBuilder imgTag = new StringBuilder(Utils.changeFileExtension(
				Utils.extractFileName(fileName), ""));
		String ext = cell.getImageFormat();
		if (ext != null) {
			if (ext.equalsIgnoreCase("gif") || ext.equalsIgnoreCase("wmf"))
				ext = "png";
		} else {
			ext = "png";
		}
		imgTag.append("_").append(row);
		imgTag.append("_").append(col);
		imgTag.append('.');
		imgTag.append(ext);
		String ImgFile = Utils.extractFilePath(fileName) + imgTag;
		try {
			ImageIO.write(image, ext, new File(ImgFile));
		} catch (IOException e) {
			Utils.showError(e);
		}
		imgTag.insert(0, "<IMG src=\"");
		imgTag.append('"');
		CellStyle style = model.getStyles(cell.getStyleId());
		if (style.getHorizontalAlignment() == CellStyle.RIGHT)
			imgTag.append(" ALIGN=RIGHT");
		if (style.getVerticalAlignment() == CellStyle.BOTTOM)
			imgTag.append(" ALIGN=ABSBOTTOM");
		else if (style.getVerticalAlignment() == CellStyle.CENTER)
			imgTag.append(" ALIGN=MIDDLE");
		if (cell.isScaleIcon()) {
			Dimension size = model.getCellSize(cell, row, col, false);
			imgTag.append(" WIDTH=" + size.getWidth());
			imgTag.append(" HEIGHT=" + size.getHeight());
		}
		imgTag.append('>');
		return imgTag.toString();
	}

	static class BackupStyles {
		public Cell cell;
		public Object styleId;

		public BackupStyles(Cell cell, Object styleId) {
			this.cell = cell;
			this.styleId = styleId;
		}

	}

	public String write(String fileName, Object resource)
			throws SaveReportException {
		return fileName;
	}
}
