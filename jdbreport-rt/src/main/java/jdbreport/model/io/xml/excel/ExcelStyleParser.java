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
package jdbreport.model.io.xml.excel;

import java.awt.Color;
import java.awt.Font;
import java.io.PrintWriter;

import jdbreport.model.Border;
import jdbreport.model.CellStyle;
import jdbreport.model.io.xml.DefaultReaderHandler;
import jdbreport.model.io.xml.DefaultReportParser;
import jdbreport.util.Utils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @version 3,0 13.12.2014
 * 
 * @author Andrey Kholmanskih
 * 
 */
public class ExcelStyleParser extends DefaultReportParser {

	private final static String[] BRUSH_STYLE_NAME = { "Solid", "Clear",
			"Horizontal", "Vertical", "FDiagonal", "BDiagonal", "Cross",
			"DiagCross" };

	private boolean inStyle;

	private CellStyle defaultStyle;

	private CellStyle currentStyle;

	private boolean inBorders;

	public ExcelStyleParser(DefaultReaderHandler reportHandler) {
		super(reportHandler);
	}

	private CellStyle getCurrentStyle() {
		if (currentStyle == null) {
			currentStyle = defaultStyle == null ? CellStyle.getDefaultStyle()
					: defaultStyle;
		}
		return currentStyle;
	}

	public boolean startElement(String name, Attributes attributes) {
		if (!inStyle && name.equals("Style")) {
			Object id;
			id = attributes.getValue("ss:ID");
			if (id != null) {
				if (attributes.getValue("ss:Parent") != null) {
					Object parentId = attributes.getValue("ss:Parent");
					CellStyle style = getReportBook().getStyles(parentId);
					currentStyle = (CellStyle) style.clone();
				} else {
					if (defaultStyle == null) {
						currentStyle = (CellStyle) CellStyle.getDefaultStyle()
								.clone();
					} else {
						currentStyle = (CellStyle) defaultStyle.clone();
					}
				}
				currentStyle.setId(id);
			}
			inStyle = true;
			return true;
		}
		if (inStyle) {
			if (name.equals("Borders")) {
				inBorders = true;
				return true;
			}
			if (inBorders && name.equals("Border")) {
				addLine(attributes);
				return true;
			}
			if (name.equals("Font")) {
				addFont(attributes);
				return true;
			}
			if (name.equals("Alignment")) {
				addFlags(attributes);
				return true;
			}
			if (name.equals("Interior")) {
				addBackground(attributes);
				return true;
			}
			if (name.equals("NumberFormat")) {
				addFormat(attributes.getValue("ss:Format"));
				return true;
			}
			return false;
		}
		return false;
	}

	public void endElement(String name, StringBuffer value) throws SAXException {
		if (inBorders && name.equals("Borders")) {
			inBorders = false;
			return;
		}
		if (inStyle && name.equals("Style")) {
			getReportBook().appendStyle(getCurrentStyle());
			if (currentStyle.getId().equals("Default"))
				defaultStyle = currentStyle;
			currentStyle = null;
			inStyle = false;
			return;
		}
		if (!inStyle && name.equals("Styles")) {
			getHandler().popHandler(name);
		}
	}

	private void addLine(Attributes attributes) {
		if (attributes.getValue("ss:Weight") == null)
			return;
		float width = Float.parseFloat(attributes.getValue("ss:Weight"));
		int style = Border.psSolid;
		Color color = Color.BLACK;
		if ("Continuous".equals(attributes.getValue("ss:LineStyle")))
			style = Border.psSolid;
		else if ("Dot".equals(attributes.getValue("ss:LineStyle")))
			style = Border.psDot;
		else if ("Dash".equals(attributes.getValue("ss:LineStyle")))
			style = Border.psDash;
		else if ("DashDot".equals(attributes.getValue("ss:LineStyle")))
			style = Border.psDashDot;
		else if ("DashDotDot".equals(attributes.getValue("ss:LineStyle")))
			style = Border.psDashDotDot;

		if (attributes.getValue("ss:Color") != null) {
			color = new Color(Integer.decode(attributes.getValue("ss:Color")));
		}

		byte ind = Border.parsePosition(attributes.getValue("ss:Position"));
		Border border = new Border(color, width, style);
		currentStyle = getCurrentStyle().deriveBorder(ind, border);
	}

	private void addFont(Attributes attributes) {

		int size = 10;
		String s = attributes.getValue("ss:Size");
		if (s != null && s.length() > 0) {
			size = Integer.parseInt(s);
		}

		int fontStyle = 0;
		s = attributes.getValue("ss:Bold");
		if (s != null && s.equals("1")) {
			fontStyle |= Font.BOLD;
		}
		s = attributes.getValue("ss:Italic");
		if (s != null && s.equals("1")) {
			fontStyle |= Font.ITALIC;
		}
		s = attributes.getValue("ss:Underline");
		if (s != null && s.equals("1")) {
			fontStyle |= CellStyle.UNDERLINE;
		}
		s = attributes.getValue("ss:StrikeThrough");
		if (s != null && s.equals("1")) {
			fontStyle |= CellStyle.STRIKETHROUGH;
		}

		String fontName = attributes.getValue("ss:FontName");
		if (fontName == null)
			fontName = "Arial";
		currentStyle = getCurrentStyle().deriveFont(fontName, fontStyle, size);

		s = attributes.getValue("ss:Color");
		if (s != null) {
			currentStyle = getCurrentStyle().deriveForeground(
					new Color(Integer.decode(s)));
		}
	}

	private void addFlags(Attributes attributes) {
		int flags = 0;
		String s = attributes.getValue("ss:Vertical");
		if (s != null) {
			if ("Bottom".equals(s))
				flags = CellStyle.BOTTOM;
			else if ("Center".equals(s))
				flags = CellStyle.CENTER;
			else if ("Top".equals(s))
				flags = CellStyle.TOP;
			currentStyle = getCurrentStyle().deriveVAlign(flags);
		}
		s = attributes.getValue("ss:Horizontal");
		if (s != null) {
			if ("Center".equals(s))
				flags = CellStyle.CENTER;
			else if ("Right".equals(s))
				flags = CellStyle.RIGHT;
			else if ("Justify".equals(s))
				flags = CellStyle.JUSTIFY;
			else
				flags = CellStyle.LEFT;
			currentStyle = getCurrentStyle().deriveHAlign(flags);
		}

		s = attributes.getValue("ss:WrapText");
		if ("1".equals(s)) {
		}
		s = attributes.getValue("ss:Rotate");
		if (s != null) {
			int angle = Integer.parseInt(s);
			if (angle < 0)
				angle = 360 + angle;
			currentStyle = getCurrentStyle().deriveAngle(angle);
		}

	}

	private void addFormat(String format) {
		if (format != null) {
			int flags;
			if (format.equals("Short Date") || format.equals("Short Time"))
				flags = CellStyle.RIGHT;
			else
				return;

			if (flags != getCurrentStyle().getHorizontalAlignment()) {
				currentStyle = getCurrentStyle().deriveHAlign(flags);
			}
		}
	}

	private void addBackground(Attributes attributes) {
		String s = attributes.getValue("ss:Color");
		if (s != null) {
			Color bgColor = new Color(Integer.decode(s));
			s = attributes.getValue("ss:Pattern");
			int bgStyle = 0;
			if (s != null)
				bgStyle = Utils.strToBrushStyle(s);

			currentStyle = getCurrentStyle().deriveBackground(bgColor, bgStyle);
		}
	}

	/**
	 * save style to XML
	 * 
	 * @param writer PrintWriter
	 * @param style CellStyle
	 */
	public static void saveStyle(PrintWriter writer, CellStyle style) {

		Object id = getStyleId(style.getId());
		writer.println("<Style ss:ID=\"" + id + "\">");
		if (style.getHorizontalAlignment() != CellStyle.LEFT
				|| style.getVerticalAlignment() != CellStyle.BOTTOM
				|| style.getAngle() > 0) {
			writer.println(alignToXML(style));
		}
		saveBorders(writer, style);
		writer.println(fontToXML(style));
		String s = backgrToStr(style);
		if (s != null)
			writer.println("<Interior " + s + "/>");
		writer.println("</Style>");
	}

	public static Object getStyleId(Object id) {
		if (id == null || (id instanceof Integer && (Integer) id == -1)) {
			id = "Default";
		}
		return id;
	}

	private static String alignToXML(CellStyle style) {
		StringBuilder result = new StringBuilder("<Alignment ");
		if (style.getVerticalAlignment() != CellStyle.BOTTOM) {
			result.append("ss:Vertical=\"");
			result
					.append(CellStyle.alignToString(style
							.getVerticalAlignment()));
			result.append("\" ");
		}
		if (style.getHorizontalAlignment() != CellStyle.LEFT) {
			result.append("ss:Horizontal=\"");
			result.append(CellStyle.alignToString(style
					.getHorizontalAlignment()));
			result.append("\" ");
		}
		if (style.getAngle() > 0) {
			result.append("ss:Rotate=\"");
			result.append(style.getAngle());
			result.append("\" ");
		}
		result.append("ss:WrapText=\"1\" ");
		result.append("/>");
		return result.toString();
	}

	private static String fontToXML(CellStyle font) {
		StringBuilder style = new StringBuilder("<Font ");
		style.append("ss:FontName=\"");
		style.append(font.getFamily());
		style.append("\" ");
		style.append("ss:Size=\"");
		style.append(font.getSize());
		style.append("\" ");
		if (!font.getForegroundColor().equals(Color.BLACK)) {
			style.append("ss:Color=\"");
			style.append(colorToStr(font.getForegroundColor()));
			style.append("\" ");
		}
		if (font.isBold()) {
			style.append("ss:Bold=\"1\" ");
		}
		if (font.isItalic()) {
			style.append("ss:Italic=\"1\" ");
		}
		if (font.isUnderline()) {
			style.append("ss:Underline=\"Single\" ");
		}
		if (font.isStrikethrough()) {
			style.append("ss:StrikeThrough=\"1\" ");
		}
		style.append("/>");
		return style.toString();
	}

	private static void saveBorders(PrintWriter writer, CellStyle style) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			Border b = style.getBorders(i);
			if (b != null && b.getLineWidth() > 0) {
				str.append("<Border ss:Position=\"");
				str.append(Border.borderPosition[i]);
				str.append('"');
				str.append(' ');
				str.append(lineToStr(b));
				str.append("/>");

			}
		}
		if (str.length() > 0) {
			writer.println("<Borders>");
			writer.println(str.toString());
			writer.println("</Borders>");
		}
	}

	private static String lineToStr(Border line) {
		StringBuilder result = new StringBuilder();

		switch (line.getStyle()) {
		case Border.psSolid:
			result.append("Continuous");
			break;
		case Border.psDash:
			result.append("Dash");
			break;
		case Border.psDot:
			result.append("Dot");
			break;
		case Border.psDashDot:
			result.append("DashDot");
			break;
		case Border.psDashDotDot:
			result.append("DashDotDot");
			break;
		default:
			result.append("Continuous");
		}
		result.insert(0, "ss:LineStyle=\"");
		result.append("\" ss:Weight=\"");
		result.append("").append(line.getLineWidth() > 3 ? 3 : line.getLineWidth());
		result.append('"');
		if (!line.getColor().equals(Color.BLACK)) {
			result.append(" ss:Color=\"");
			result.append(colorToStr(line.getColor()));
			result.append('"');
			result.append(' ');
		}
		return result.toString();
	}

	private static String colorToStr(Color c) {
		return "#" + Integer.toHexString(c.getRGB()).substring(2, 8);
	}

	private static String backgrToStr(CellStyle style) {
		if (style.getBackground() != Color.WHITE) {
			StringBuilder result = new StringBuilder();
			result.append("ss:Color=\"");
			result.append(colorToStr(style.getBackground()));
			result.append('"');
			result.append(" ss:Pattern=\"");
			result.append(BRUSH_STYLE_NAME[style.getBgStyle()]);
			result.append('"');
			result.append(' ');
			return result.toString();
		}
		return null;
	}

}
