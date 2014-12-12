/*
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2013 Andrey Kholmanskih. All rights reserved.
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
 * 
 */
package jdbreport.model.io.xml.odf;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jdbreport.model.Cell;
import jdbreport.model.CellStyle;
import jdbreport.model.CellValue;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportColumn;
import jdbreport.model.ReportModel;
import jdbreport.model.RowsGroup;
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
class OdsContentWriter extends OdfBaseWriter {

	protected ImageWriter iconWriter;
	protected Map<String, String> rowStylesMap;

	public OdsContentWriter(ImageWriter iconWriter) {
		super();
		this.iconWriter = iconWriter;
	}

	public void save(Writer writer, ReportBook reportBook)
			throws SaveReportException {
		PrintWriter fw;
		if (writer instanceof PrintWriter)
			fw = (PrintWriter) writer;
		else
			fw = new PrintWriter(writer);
		fw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		
		fw.print("<office:document-content ");
		fw.print("xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\" ");
		fw.print("xmlns:style=\"urn:oasis:names:tc:opendocument:xmlns:style:1.0\" ");
		fw.print("xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" ");
		fw.print("xmlns:table=\"urn:oasis:names:tc:opendocument:xmlns:table:1.0\" ");
		fw.print("xmlns:draw=\"urn:oasis:names:tc:opendocument:xmlns:drawing:1.0\" ");
		fw.print("xmlns:fo=\"urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0\" ");
		fw.print("xmlns:xlink=\"http://www.w3.org/1999/xlink\" ");
		fw.print("xmlns:dc=\"http://purl.org/dc/elements/1.1/\" ");
		fw.print("xmlns:meta=\"urn:oasis:names:tc:opendocument:xmlns:meta:1.0\" ");
		fw.print("xmlns:number=\"urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0\" ");
		fw.print("xmlns:presentation=\"urn:oasis:names:tc:opendocument:xmlns:presentation:1.0\" ");
		fw.print("xmlns:svg=\"urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0\" ");
		fw.print("xmlns:chart=\"urn:oasis:names:tc:opendocument:xmlns:chart:1.0\" ");
		fw.print("xmlns:dr3d=\"urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0\" ");
		fw.print("xmlns:math=\"http://www.w3.org/1998/Math/MathML\" ");
		fw.print("xmlns:form=\"urn:oasis:names:tc:opendocument:xmlns:form:1.0\" ");
		fw.print("xmlns:script=\"urn:oasis:names:tc:opendocument:xmlns:script:1.0\" ");
		fw.print("xmlns:ooo=\"http://openoffice.org/2004/office\" ");
		fw.print("xmlns:ooow=\"http://openoffice.org/2004/writer\" ");
		fw.print("xmlns:oooc=\"http://openoffice.org/2004/calc\" ");
		fw.print("xmlns:dom=\"http://www.w3.org/2001/xml-events\" ");
		fw.print("xmlns:xforms=\"http://www.w3.org/2002/xforms\" ");
		fw.print("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ");
		fw.print("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
		fw.print("xmlns:rpt=\"http://openoffice.org/2005/report\" ");
		fw.print("xmlns:of=\"urn:oasis:names:tc:opendocument:xmlns:of:1.2\" ");
		fw.print("xmlns:xhtml=\"http://www.w3.org/1999/xhtml\" ");
		fw.print("xmlns:grddl=\"http://www.w3.org/2003/g/data-view#\" ");
		fw.print("xmlns:tableooo=\"http://openoffice.org/2009/table\" ");
		fw.print("xmlns:drawooo=\"http://openoffice.org/2010/draw\" ");
		fw.print("xmlns:calcext=\"urn:org:documentfoundation:names:experimental:calc:xmlns:calcext:1.0\" ");
		fw.print("xmlns:field=\"urn:openoffice:names:experimental:ooo-ms-interop:xmlns:field:1.0\" ");
		fw.print("xmlns:formx=\"urn:openoffice:names:experimental:ooxml-odf-interop:xmlns:form:1.0\" ");
		fw.print("xmlns:css3t=\"http://www.w3.org/TR/css3-text/\" ");
		fw.println(" office:version=\"1.2\">");
		fw.println("<office:scripts/>");

		writeFontFaces(fw, reportBook);

		writeStyles(fw, reportBook);

		writeBody(fw, reportBook);

		fw.println("</office:document-content>");
	}

	protected void writeBody(PrintWriter fw, ReportBook reportBook) throws SaveReportException {
		fw.println("<office:body>");
		fw.println("<office:spreadsheet>");
		Set<String> titles = new HashSet<>();
		int n = 1;
		for (ReportModel model : reportBook) {
			String reportTitle = model.getReportTitle();
			String title = model.getReportTitle();
			int k = 1;
			while (titles.contains(reportTitle.toUpperCase())) {
				reportTitle = title + "(" + k++ + ")";
			}
			titles.add(reportTitle.toUpperCase());
			
			fw.println("<table:table table:name=\"" + reportTitle
					+ "\" table:style-name=\"ta" + n + "\">");
			for (int c = 1; c <= model.getColumnCount(); c++) {
				fw.println("<table:table-column table:style-name=\"co" + n + c
						+ "\"/>");
			}
			for (int r = 0; r < model.getRowCount(); r++) {
				String styleName = "ro" + n + (r + 1);
				fw.println("<table:table-row table:style-name=\""
						+ rowStylesMap.get(styleName) + "\">");
				writeRow(fw, n, model, r);
				fw.println("</table:table-row>");
			}
			fw.println("</table:table>");
			n++;
		}
		fw.println("</office:spreadsheet>");
		fw.println("</office:body>");
	}

	protected void writeStyles(PrintWriter fw, ReportBook reportBook) {
		fw.println("<office:automatic-styles>");
		
		writeColumnStyles(fw, reportBook);
		writeRowStyles(fw, reportBook);
		
		for (int i = 0; i < reportBook.size(); i++) {
			String pageName = i == 0 ? "Default" : "Page" + (i + 1);
			fw.println("<style:style style:name=\"ta" + (i + 1)
					+ "\" style:family=\"table\" style:master-page-name=\""
					+ pageName + "\">");
			fw.println("<style:table-properties table:display=\""
					+ reportBook.getReportModel(i).isVisible()
					+ "\" style:writing-mode=\"lr-tb\"/>");
			fw.println("</style:style>");
		}
		
		for (Object key : reportBook.getStyleList().keySet()) {
			if (!"Default".equals(key)) {
				CellStyle style = reportBook.getStyles(key);
				fw
						.println("<style:style style:name=\""
								+ key
								+ "\" style:family=\"table-cell\" style:parent-style-name=\"Default\" >");
				writeCellProperties(fw, style);
				writeParagraphProperties(fw, style, CellStyle.getDefaultStyle());
				writeTextProperties(fw, style, CellStyle.getDefaultStyle());
				fw.println("</style:style>");
			}
		}
		writeTextStyles(fw, reportBook);
		fw.println("</office:automatic-styles>");
	}

	protected void writeRowStyles(PrintWriter fw, ReportBook reportBook) {
		int n = 1;
		List<RowStyle> rowStyles = new ArrayList<>();
		rowStylesMap = new HashMap<>();

		for (ReportModel model : reportBook) {
			for (int r = 1; r <= model.getRowCount(); r++) {
				String styleName = "ro" + n + r;
				RowStyle rowStyle = new RowStyle(styleName);
				rowStyle.setBreakBefore(r > 1 && model.isLastRowInPage(r - 2));
				rowStyle.setHeight(Utils.round((model
						.getRowModel().getRow(r - 1)).getNativeHeight(), 4));
				int i = rowStyles.indexOf(rowStyle);
				if (i >= 0) {
					rowStyle = rowStyles.get(i);
				} else {
					rowStyles.add(rowStyle);

					fw.print("<style:style style:name=\"" + styleName
							+ "\" style:family=\"table-row\">");
					fw.print("<style:table-row-properties fo:break-before=");
					if (rowStyle.isBreakBefore()) {
						fw.print("\"page\" ");
					} else
						fw.print("\"auto\" ");
					fw.print("style:row-height=\"" + rowStyle.getHeight()
							+ "pt\"/>");
					fw.println("</style:style>");
				}
				rowStylesMap.put(styleName, rowStyle.getName());
			}
			n++;
		}
	}

	protected void writeColumnStyles(PrintWriter fw, ReportBook reportBook) {
		int n = 1;
		for (ReportModel model : reportBook) {
			for (int c = 1; c <= model.getColumnCount(); c++) {
				fw.print("<style:style style:name=\"co" + n + c
						+ "\" style:family=\"table-column\">");
				fw.print("<style:table-column-properties fo:break-before=");
				if (c > 1 && model.isColumnBreak(c - 2)) {
					fw.print("\"page\" ");
				} else
					fw.print("\"auto\" ");
				fw.print("style:column-width=\""
						+ Utils.round(((ReportColumn) model
								.getColumnModel().getColumn(c - 1))
								.getNativeWidth(), 4) + "pt\"/>");
				fw.println("</style:style>");
			}
			n++;
		}
	}

	@SuppressWarnings("unchecked")
	protected void writeRow(PrintWriter fw, int modelIndex, ReportModel model,
			int row) throws SaveReportException {
		for (int c = 0; c < model.getColumnCount(); c++) {
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
				buf.append(cell.getStyleId() == null ? "Default" : cell
						.getStyleId());
				buf.append("\" ");
				if (cell.getCellFormula() != null) {
					buf.append("table:formula=\"of:=").append(XMLCoder.replaceSpecChar(cell.getCellFormula())).append('"').append(' ');
				}

				Type valueType = cell.getValueType();
				
				if (valueType != Type.FLOAT && model.getStyles(cell.getStyleId())
						.getDecimal() != -1) {
					valueType = Type.FLOAT;
				}
				
				buf.append("office:value-type=\"");
				buf.append(valueType.toString().toLowerCase());
				buf.append("\" ");
				if (cell.getValue() != null && valueType == Type.FLOAT) {
					double value;
					try {
						value = Utils.parseDouble(cell.getValue()
								.toString());
					} catch (Exception e) {
						value = 0;
					}

					buf.append("office:value=\"");
					buf.append(value);
					buf.append("\" ");
				}
				
				if (cell.getColSpan() > 0) {
					buf.append("table:number-columns-spanned=\"");
					buf.append("").append(cell.getColSpan() + 1);
					buf.append("\" ");
				}
				if (cell.getRowSpan() > 0) {
					RowsGroup group = model.getRowModel().getRow(row)
							.getGroup();
					int r = 1;
					while (r <= cell.getRowSpan()) {
						if (group != model.getRowModel().getRow(row + r)
								.getGroup()) {
							break;
						}
						r++;
					}
					if (r > 1) {
						buf.append("table:number-rows-spanned=\"");
						buf.append("").append(r);
						buf.append("\" ");
					}
				}
				buf.append('>');
				fw.println(buf.toString());
				if (cell.getValue() != null) {
					fw.print("<text:p>");

					RenderedImage image = null;
					
					if (cell.getValue() instanceof CellValue) {
						if (!((CellValue<?>) cell.getValue()).write(fw, model, row, c,
								iconWriter, ReportBook.ODS)) {
							Image img = ((CellValue<?>) cell.getValue()).getAsImage(model, row, c);
							if (img instanceof RenderedImage) {
								image = (RenderedImage) img;
							}
							
						}
					} else {
						if (Cell.TEXT_HTML.equals(cell.getContentType())) {
							fw.print(getHTMLRenderedText(model.getStyles(cell
									.getStyleId()), cell));
						} else
							fw.print(XMLCoder.replaceSpecChar(model
									.getCellText(cell)));
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
		StringBuffer buff = new StringBuffer("<draw:frame ");
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
		buff.append("<text:p/>");
		buff.append("</draw:image>");
		buff.append("</draw:frame>");
		return buff;
	}

	private static class RowStyle {

		private String name;
		private boolean breakBefore;
		private float height;

		public RowStyle(String name) {
			super();
			this.name = name;
		}

		public void setBreakBefore(boolean breakBefore) {
			this.breakBefore = breakBefore;
		}

		public void setHeight(float height) {
			this.height = height;
		}

		public String getName() {
			return name;
		}

		public boolean isBreakBefore() {
			return breakBefore;
		}

		public float getHeight() {
			return height;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (breakBefore ? 1231 : 1237);
			result = prime * result + Float.floatToIntBits(height);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RowStyle other = (RowStyle) obj;
			if (breakBefore != other.breakBefore)
				return false;
			return Float.floatToIntBits(height) == Float
					.floatToIntBits(other.height);
		}

	}
}
