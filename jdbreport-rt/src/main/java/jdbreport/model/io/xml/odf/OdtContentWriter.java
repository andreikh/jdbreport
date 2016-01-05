/*
 * OdtContentWriter.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2014 Andrey Kholmanskih
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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.PrintWriter;

import javax.swing.table.TableColumnModel;

import jdbreport.model.Cell;
import jdbreport.model.CellStyle;
import jdbreport.model.CellValue;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportColumn;
import jdbreport.model.ReportModel;
import jdbreport.model.Units;
import jdbreport.model.Cell.Type;
import jdbreport.model.io.SaveReportException;
import jdbreport.util.Utils;
import jdbreport.util.xml.XMLCoder;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
class OdtContentWriter extends OdsContentWriter {

	public OdtContentWriter(ImageWriter iconWriter) {
		super(iconWriter);
	}

	protected void writeBody(PrintWriter fw, ReportBook reportBook) throws SaveReportException {
		fw.println("<office:body>");
		fw.println("<office:text>");
		int n = 1;
		for (ReportModel model : reportBook) {
			int topRow = 0;
			int r;
			do {
				int leftCol = 0;
				do {
					r = topRow;
					int rightCol = model.findRightColumn(leftCol);
					fw.println("<table:table table:name=\""
							+ model.getReportTitle()
							+ "\" table:style-name=\"ta" + n + "_"
							+ (leftCol + 1) + "\">");
					for (int c = leftCol + 1; c <= rightCol + 1; c++) {
						fw.println("<table:table-column table:style-name=\""
								+ getColStyleName(n, c) + "\"/>");
					}
					while (r < model.getRowCount()) {
						String styleName = getRowStyleName(n, (r + 1));
						fw.println("<table:table-row table:style-name=\""
								+ rowStylesMap.get(styleName) + "\">");
						writeRow(fw, n, model, r, leftCol, rightCol);
						fw.println("</table:table-row>");
						r++;
						if (model.isLastRowInPage(r - 1)) {
							break;
						}
					}
					fw.println("</table:table>");
					leftCol = rightCol + 1;
				} while (leftCol < model.getColumnCount());
				topRow = r;
			} while (topRow < model.getRowCount());
			n++;
		}
		fw.println("</office:text>");
		fw.println("</office:body>");
	}

	protected void writeStyles(PrintWriter fw, ReportBook reportBook) {
		fw.println("<office:automatic-styles>");
		
		writeColumnStyles(fw, reportBook);
		writeRowStyles(fw, reportBook);
		
		for (int i = 0; i < reportBook.size(); i++) {
			ReportModel model = reportBook.getReportModel(i);
			TableColumnModel columnModel = model.getColumnModel();
			int leftCol = 0;
			do {
				int rightCol = model.findRightColumn(leftCol);
				float width = 0;
				for (int c = leftCol; c <= rightCol; c++) {
					width += ((ReportColumn) columnModel.getColumn(c))
							.getNativeWidth();
				}
				String pageName = i == 0 ? "Default" : "Page" + (i + 1);
				fw.println("<style:style style:name=\"ta" + (i + 1) + "_"
						+ (leftCol + 1)
						+ "\" style:family=\"table\" style:master-page-name=\""
						+ pageName + "\">");
				fw
						.println("<style:table-properties table:display=\""
								+ model.isVisible()
								+ "\""
								+ " style:width=\""
								+ width
								+ "pt"
								+ "\""
								+ " table:align=\"left\" style:writing-mode=\"lr-tb\" />");
				fw.println("</style:style>");
				leftCol = rightCol + 1;
			} while (leftCol < model.getColumnCount());
		}
		for (Object key : reportBook.getStyleList().keySet()) {
			if (!"Default".equals(key)) {
				CellStyle style = reportBook.getStyles(key);
				fw.print("<style:style style:name=\"Sheet" + "." + key
						+ "\" style:family=\"table-cell\" ");
				fw.println(">");
				writeCellProperties(fw, style);
				fw.println("</style:style>");
			}
		}
		for (Object key : reportBook.getStyleList().keySet()) {
			if (!"Default".equals(key)) {
				CellStyle style = reportBook.getStyles(key);
				fw.print("<style:style style:name=\"" + key
						+ "\" style:family=\"paragraph\" ");
				fw.println(">");
				writeParagraphProperties(fw, style, CellStyle.getDefaultStyle());
				writeTextProperties(fw, style, CellStyle.getDefaultStyle());
				fw.println("</style:style>");
			}
		}
		
		writeTextStyles(fw, reportBook);
		fw.println("</office:automatic-styles>");
	}

	@SuppressWarnings("unchecked")
	protected void writeRow(PrintWriter fw, int modelIndex, ReportModel model,
			int row, int leftCol, int rightCol) throws SaveReportException {
		for (int c = leftCol; c <= rightCol; c++) {
			Cell cell = model.getReportCell(row, c);
			if (cell.isNull())
				fw.println("<table:table-cell/>");
			else if (cell.isChild()) {
				fw.print("<table:covered-table-cell ");
				int spanC = cell.getOwner().getColSpan();
				if (spanC > 0) {
					int ownC = model.getOwnerColumn(cell, row, c);
					int rep = ownC + spanC - c + 1;
					c = ownC + spanC;
					if (rep > 1)
						fw.print("table:number-columns-repeated=\"" + rep
								+ "\" ");
				}
				fw.println("/>");
			} else {
				StringBuilder buf = new StringBuilder("<table:table-cell ");
				buf.append("table:style-name=\"");
				buf.append("Sheet");
				buf.append(".");
				buf.append(cell.getStyleId() == null ? "Default" : cell
						.getStyleId());
				buf.append("\" ");
				buf.append("office:value-type=\"");
				buf.append(cell.getValueType().toString().toLowerCase());
				buf.append("\" ");
				if (cell.getValue() != null
						&& cell.getValueType() == Type.FLOAT) {
					buf.append("office:value=\"");
					buf.append(XMLCoder.replaceSpecChar(cell.getValue()
							.toString()));
					buf.append("\" ");
				}
				if (cell.getColSpan() > 0) {
					buf.append("table:number-columns-spanned=\"");
					buf.append("").append(cell.getColSpan() + 1);
					buf.append("\" ");
				}
				if (cell.getRowSpan() > 0) {
					buf.append("table:number-rows-spanned=\"");
					buf.append("").append(cell.getRowSpan() + 1);
					buf.append("\" ");
				}
				buf.append('>');
				fw.println(buf.toString());
				if (cell.getValue() != null) {
					String headTag = "<text:p text:style-name=\"";
					headTag += cell.getStyleId() == null ? "Default" : String.valueOf(cell
							.getStyleId());
					headTag += "\">";
					fw.print(headTag);
					
					RenderedImage image = null;
					
					if (cell.getValue() instanceof CellValue) {
						if (!((CellValue<?>) cell.getValue()).write(fw, model, row, c,
								iconWriter, ReportBook.ODT)) {
							Image img = ((CellValue<?>) cell.getValue()).getAsImage(model, row, c);
							if (img instanceof RenderedImage) {
								image = (RenderedImage) img;
							}
						}
					} else {
						if (Cell.TEXT_HTML.equals(cell.getContentType())) {
							fw.print(getHTMLRenderedText(model.getStyles(cell
									.getStyleId()), cell));
						} else { 
							String text = XMLCoder.replaceSpecChar(model
									.getCellText(cell));
							text = text.replaceAll("\n", "</text:p>" + headTag);
							fw.print(text);
						}
					}
					fw.println("</text:p>");

					if (image != null) {
						Dimension r;
						if (cell.isScaleIcon()) {
							r = model.getCellSize(cell, row, c, false);
						} else {
							r = new Dimension(image.getWidth(), image.getHeight());
						}
						StringBuffer buff = imageToXML("image_" + modelIndex + "_"
								+ row + "_" + c + "_1", cell, r, image);
						fw.println(buff);
					}
					
				}
				
				if (cell.getPicture() != null) {
					Dimension r;
					RenderedImage image = Utils.getRenderedImage(cell.getPicture().getIcon());
					if (cell.isScaleIcon()) {
						r = model.getCellSize(cell, row, c, false);
					} else {
						r = new Dimension(cell.getPicture().getWidth(), cell.getPicture().getHeight());
					}
					StringBuffer buff = imageToXML("image_" + modelIndex + "_"
							+ row + "_" + c, cell, r, image);
					fw.println(buff);
				}
				fw.println("</table:table-cell>");
			}
		}
	}

	protected StringBuffer imageToXML(String fileName, Cell cell, Dimension r, RenderedImage image) {
		StringBuffer buff = new StringBuffer("<text:p text:style-name=\"");
		buff.append(cell.getStyleId() == null ? "Default" : cell.getStyleId());
		buff.append("\">");
		buff.append("<draw:frame ");
		buff.append("svg:x=\"0pt\" svg:y=\"0pt\" ");
		buff.append("svg:width=\"");
		buff.append(Utils.round(Units.PT.setXPixels(r.width), 2));
		buff.append("pt\" ");
		buff.append("svg:height=\"");
		buff.append(Utils.round(Units.PT.setYPixels(r.height), 2));
		buff.append("pt\" ");
		buff.append('>');
		buff.append("<draw:image xlink:href=\"Pictures/");
		String ext = cell.getImageFormat();
		if (ext != null) {
			if (ext.equalsIgnoreCase("gif") || ext.equalsIgnoreCase("wmf"))
				ext = "png";
		} else {
			ext = "png";
		}
		fileName += "." + ext;
		iconWriter.writeIcon(fileName, image);
		buff.append(fileName);
		buff.append('"');
		buff
				.append(" xlink:type=\"simple\" xlink:show=\"embed\" xlink:actuate=\"onLoad\">");
		buff.append("</draw:image>");
		buff.append("</draw:frame>");
		buff.append("</text:p>");
		return buff;
	}

}
