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
 * 
 */
package jdbreport.model.io.xml.odf;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLDocument;

import and.util.xml.XMLCoder;

import jdbreport.grid.JReportGrid.HTMLReportRenderer;
import jdbreport.model.Border;
import jdbreport.model.Cell;
import jdbreport.model.CellStyle;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportModel;
import jdbreport.model.TableRow;
import jdbreport.model.io.Content;
import jdbreport.model.io.ReportWriter;
import jdbreport.model.io.SaveReportException;

/**
 * @version 1.3 15.08.2009
 * @author Andrey Kholmanskih
 * 
 */
abstract class OdfBaseWriter implements ReportWriter {

	protected TreeMap<String, FontStyle> fontMap;
	private JTextComponent htmlReportRenderer;
	protected List<CellStyle> textStyles;

	protected static String colorToStr(Color c) {
		return "#" + Integer.toHexString(c.getRGB()).substring(2, 8);
	}

	public OdfBaseWriter() {
		super();
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
		PrintWriter fw = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(out, java.nio.charset.Charset
						.forName("UTF-8"))));
		save(fw, reportBook);
		fw.flush();
	}

	public String getDescription() {
		return null;
	}

	protected void writeFontFaces(PrintWriter fw, ReportBook reportBook) {
		findFontStyles(reportBook.getStyleList().values());
		fw.println("<office:font-face-decls>");
		for (FontStyle style : fontMap.values()) {
			fw.println("<style:font-face style:name=\""
					+ and.util.xml.XMLCoder.replaceSpecChar(style.getName())
					+ "\" svg:font-family=\""
					+ and.util.xml.XMLCoder.replaceSpecChar(style.getFamily())
					+ "\" />");
		}
		fw.println("</office:font-face-decls>");
	}

	private void findFontStyles(Collection<CellStyle> styles) {
		fontMap = new TreeMap<String, FontStyle>();
		for (CellStyle style : styles) {
			if (!fontMap.containsKey(style.getFamily())) {
				fontMap.put(style.getFamily(), new FontStyle(style.getFamily(),
						style.getFamily(), null, null));
			}

		}
	}

	protected String getBorderStr(byte pos, Border b) {
		return " fo:border-" + Border.str_position[pos] + "=\""
				+ b.getLineWidth() + "pt "
				+ OdsStyleParser.LINE_STYLE[b.getStyle()] + " "
				+ colorToStr(b.getColor()) + "\" ";
	}

	public String[] getExtensions() {
		return null;
	}

	protected void writeParagraphProperties(PrintWriter fw, CellStyle style,
			CellStyle defaultStyle) {
		StringBuffer buf = new StringBuffer();
		if (defaultStyle == null
				|| style.getHorizontalAlignment() != defaultStyle
						.getHorizontalAlignment()) {
			String s = "left";
			switch (style.getHorizontalAlignment()) {
			case CellStyle.CENTER:
				s = "center";
				break;
			case CellStyle.RIGHT:
				s = "end";
				break;
			case CellStyle.JUSTIFY:
				s = "justify";
				break;
			}
			buf.append("fo:text-align=\"" + s + "\" ");
		}
		if (buf.length() > 0) {
			fw.println("<style:paragraph-properties " + buf.toString() + "/>");
		}
	}

	protected void writeTextProperties(PrintWriter fw, CellStyle style,
			CellStyle defaultStyle) {
		StringBuffer buf = new StringBuffer();
		if (style.getTypeOffset() == CellStyle.SS_SUB) {
			buf.append(" style:text-position=\"-33% 58%\" ");
		} else if (style.getTypeOffset() == CellStyle.SS_SUPER) {
			buf.append(" style:text-position=\"33% 58%\" ");
		}
		if (defaultStyle == null
				|| !style.getFamily().equals(defaultStyle.getFamily()))
			buf.append("style:font-name=\"" + style.getFamily() + "\" ");
		buf.append("fo:font-size=\"" + style.getSize() + "pt\" ");

		if (style.getStyle() != CellStyle.getDefaultStyle().getStyle()) {
			if (style.isBold())
				buf.append("fo:font-weight=\"bold\" ");
			if (style.isItalic())
				buf.append("fo:font-style=\"italic\" ");
			if (style.isUnderline())
				buf
						.append("style:text-underline-style=\"solid\" style:text-underline-width=\"auto\" style:text-underline-color=\"font-color\" ");
			if (style.isStrikethrough())
				buf.append("style:text-line-through-style=\"solid\" ");
		}
		if (defaultStyle != null
				&& style.getForegroundColor() != null
				&& !style.getForegroundColor().equals(
						defaultStyle.getForegroundColor())) {
			buf.append("fo:color=\"" + colorToStr(style.getForegroundColor())
					+ "\" ");
		}
		if (buf.length() > 0) {
			fw.println("<style:text-properties " + buf.toString() + "/>");
		}
	}

	protected void writeCellProperties(PrintWriter fw, CellStyle style,
			CellStyle defaultStyle) {
		StringBuffer buf = new StringBuffer();
		if (style.getVerticalAlignment() == CellStyle.TOP)
			buf.append("style:vertical-align =\"top\" ");
		else if (style.getVerticalAlignment() == CellStyle.CENTER)
			buf.append("style:vertical-align =\"middle\" ");
		else if (style.getVerticalAlignment() == CellStyle.BOTTOM)
			buf.append("style:vertical-align =\"bottom\" ");

		
		buf.append("fo:padding=\"");
		buf.append("1pt");
		buf.append("\" ");
		
		if (style.isWrapLine()) {
			buf.append("fo:wrap-option=\"wrap\" ");
		}
		if (style.getAngle() != 0) {
			buf.append("style:rotation-angle=\"" + style.getAngle() + "\" ");
		}

		if (style.getBackground() != Color.white) {
			buf.append("fo:background-color=\""
					+ colorToStr(style.getBackground()) + "\" ");
		}
		for (byte i = Border.LINE_LEFT; i <= Border.LINE_BOTTOM; i++) {
			if (style.getBorders(i) != null) {
				buf.append(getBorderStr(i, style.getBorders(i)));
			}
		}
		if (buf.length() > 0) {
			fw.println("<style:table-cell-properties " + buf.toString() + "/>");
		}
	}

	protected void writeTextStyles(PrintWriter fw, ReportBook reportBook) {
		textStyles = new ArrayList<CellStyle>();
		for (ReportModel model : reportBook) {
			for (TableRow tableRow : model.getRowModel()) {
				for (Cell cell : tableRow) {
					if (!cell.isChild() && !cell.isNull()) {
						if (Cell.TEXT_HTML.equals(cell.getContentType())) {
							addTextStyle(reportBook
									.getStyles(cell.getStyleId()), cell,
									textStyles);
						}
					}

				}
			}
		}
		if (textStyles.size() > 0) {
			for (int i = 0; i < textStyles.size(); i++) {
				CellStyle style = textStyles.get(i);
				fw.println("<style:style style:name=\"T" + (i + 1)
						+ "\" style:family=\"text\">");
				writeTextProperties(fw, style, CellStyle.getDefaultStyle());
				fw.println("</style:style>");
			}
		}
	}

	private void addTextStyle(CellStyle parentStyle, Cell cell,
			List<CellStyle> styles) {
		JTextComponent tc = getHTMLReportRenderer();
		tc.setText(cell.getText());
		List<Content> contentList = Content
				.getHTMLContentList((HTMLDocument) tc.getDocument());
		if (contentList != null) {
			for (Content content : contentList) {
				CellStyle newStyle = content.createTextStyle(parentStyle);
				if (newStyle != null) {
					if (styles.indexOf(newStyle) < 0) {
						styles.add(newStyle);
					}
				}
			}
		}
	}

	protected String getHTMLRenderedText(CellStyle parentStyle, Cell cell) {
		if (cell.isNull() || cell.isChild())
			return ""; //$NON-NLS-1$
		JTextComponent tc = getHTMLReportRenderer();
		tc.setText(cell.getText());
		List<Content> contentList = Content
				.getHTMLContentList((HTMLDocument) tc.getDocument());
		if (contentList != null) {
			StringBuffer result = new StringBuffer();
			for (Content content : contentList) {
				if ("\n".equals(content.getText())) {
					result.append("</text:p><text:p>");
				} else {
					result.append("<text:span");
					CellStyle newStyle = content.createTextStyle(parentStyle);
					if (newStyle != null) {
						int i = textStyles.indexOf(newStyle);
						if (i >= 0) {
							result.append(" text:style-name=\"T" + (i + 1)
									+ "\"");
						}
					}
					result.append(">");
					result.append(XMLCoder.replaceSpecChar(content.getText()));
					result.append("</text:span>");
				}
			}
			return result.toString();
		}

		return "";
	}

	private JTextComponent getHTMLReportRenderer() {
		if (htmlReportRenderer == null) {
			htmlReportRenderer = new HTMLReportRenderer();
		}
		return htmlReportRenderer;
	}

	public String write(String fileName, Object resource) throws SaveReportException {
		throw new SaveReportException("The method is not supported"); 
	}

}