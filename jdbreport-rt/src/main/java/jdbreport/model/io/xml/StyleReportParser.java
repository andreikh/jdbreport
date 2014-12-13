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
package jdbreport.model.io.xml;

import java.awt.Color;
import java.awt.Font;
import java.io.PrintWriter;

import jdbreport.model.Border;
import jdbreport.model.CellStyle;
import jdbreport.util.Utils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @version 3.0 13.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class StyleReportParser extends DefaultReportParser {

	private static final String BORDER = "Border";

	private static final String DEFAULT = "Default";

	private static final String BOTTOM = "Bottom";

	private static final String FONT_NAME = "FontName";

	private static final String SIZE = "Size";

	private static final String STRIKEOUT = "Strikeout";

	private static final String UNDERLINE = "Underline";

	private static final String ITALIC = "Italic";

	private static final String BOLD = "Bold";

	private static final String FONT = "Font";

	private static final String WIDTH = "Width";

	private static final String POSITION = "Position";

	private static final String HORIZONTAL = "Horizontal";

	private static final String ALIGNMENT = "Alignment";

	private static final String CENTER = "Center";

	private static final String VERTICAL = "Vertical";

	private static final String ROTATE = "Rotate";

	private static final String WRAP_TEXT = "WrapText";

	private static final String WRAP_LINE = "WrapLine";

	private static final String AUTO_HEIGHT = "AutoHeight";

	private static final String CARRY = "Carry";

	private static final String PATTERN = "Pattern";

	private static final String ROUND = "Round";

	private static final String FORMAT = "Format";

	private static final String LS = "ls";

	private static final String PARAGRAPH = "Paragraph";

	private static final String COLOR = "Color";

	private static final String BACKGROUND = "Background";

	private static final String ID = "ID";

	private boolean inStyle;

	private CellStyle currentStyle;

	public StyleReportParser(DefaultReaderHandler reportHandler) {
		super(reportHandler);
	}

	public boolean startElement(String name, Attributes attributes) {
		if (!inStyle && name.equals(STYLE)) {
			Object id = attributes.getValue(ID);
			if (id != null) {
				try {
					id = new Integer(id.toString());
				} catch (NumberFormatException ignored) {
				}
				currentStyle = (CellStyle) CellStyle.getDefaultStyle().clone();
				currentStyle.setId(id);
			}
			inStyle = true;
			return true;
		}
		if (inStyle && name.equals(FONT)) {
			int fontStyle = 0;
			String s = attributes.getValue(BOLD);
			if (s != null && s.equals("1")) {
				fontStyle |= Font.BOLD;
			}
			s = attributes.getValue(ITALIC);
			if (s != null && s.equals("1")) {
				fontStyle |= Font.ITALIC;
			}
			s = attributes.getValue(UNDERLINE);
			if (s != null && s.equals("1")) {
				fontStyle |= CellStyle.UNDERLINE;
			}
			s = attributes.getValue(STRIKEOUT);
			if (s != null && s.equals("1")) {
				fontStyle |= CellStyle.STRIKETHROUGH;
			}
			int size = 12;
			s = attributes.getValue(SIZE);
			if (s != null && s.length() > 0) {
				size = Integer.parseInt(s);
			}
			currentStyle = getCurrentStyle().deriveFont(
					attributes.getValue(FONT_NAME), fontStyle, size);
			currentStyle = getCurrentStyle().deriveFont(fontStyle);
			s = attributes.getValue(COLOR);
			if (s != null && s.length() > 0) {
				currentStyle = getCurrentStyle().deriveForeground(
						Utils.stringToColor(s));
			}
			return true;
		}
		if (inStyle && name.equals(BORDER)) {
			byte ind = Border.parsePosition(attributes.getValue(POSITION));
			if (ind > -1) {
				Border border = new Border(Utils.stringToColor(attributes
						.getValue(COLOR)), Float.parseFloat(attributes
						.getValue(WIDTH)), Integer.parseInt(attributes
						.getValue(STYLE)));
				currentStyle = getCurrentStyle().deriveBorder(ind, border);
			}
			return true;
		}
		if (inStyle && name.equals(ALIGNMENT)) {
			int flags;
			String s = attributes.getValue(HORIZONTAL);
			if (s != null) {
				switch (s) {
					case CENTER:
						flags = CellStyle.CENTER;
						break;
					case "Right":
						flags = CellStyle.RIGHT;
						break;
					case "Justify":
						flags = CellStyle.JUSTIFY;
						break;
					default:
						flags = CellStyle.LEFT;
						break;
				}
				currentStyle = getCurrentStyle().deriveHAlign(flags);
			}
			s = attributes.getValue(VERTICAL);
			if (s != null) {
				switch (s) {
					case CENTER:
						flags = CellStyle.CENTER;
						break;
					case BOTTOM:
						flags = CellStyle.BOTTOM;
						break;
					default:
						flags = CellStyle.TOP;
						break;
				}
				currentStyle = getCurrentStyle().deriveVAlign(flags);
			}

			s = attributes.getValue(ROTATE);
			if (s != null && s.length() > 0) {
				currentStyle = getCurrentStyle().deriveAngle(
						Integer.parseInt(s));
			}
			s = attributes.getValue(WRAP_TEXT);
			if (s != null && s.length() > 0) {
				boolean val = Integer.parseInt(s) == 1;
				if (getHandler().getVersion().compareTo("7") < 0) {
					currentStyle = getCurrentStyle().deriveAutoHeight(val);
				}
				currentStyle = getCurrentStyle().deriveWrapLine(val);
			}
			s = attributes.getValue(WRAP_LINE);
			if (s != null && s.length() > 0) {
				currentStyle = getCurrentStyle().deriveWrapLine(
						Boolean.parseBoolean(s));
			}
			s = attributes.getValue(AUTO_HEIGHT);
			if (s != null && s.length() > 0) {
				currentStyle = getCurrentStyle().deriveAutoHeight(
						Boolean.parseBoolean(s));
			}
			s = attributes.getValue(CARRY);
			if (s != null && s.length() > 0) {
				currentStyle = getCurrentStyle().deriveCarryRows(
						Integer.parseInt(s));
			}
			return true;
		}
		if (inStyle && name.equals(BACKGROUND)) {
			String s = attributes.getValue(COLOR);
			Color bgColor = null;
			int bgStyle = 0;
			if (s != null && s.length() > 0) {
				bgColor = Utils.stringToColor(s);
			}
			s = attributes.getValue(PATTERN);
			if (s != null && s.length() > 0) {
				bgStyle = Utils.strToBrushStyle(s);
			}
			if (bgColor != null) {
				currentStyle = getCurrentStyle().deriveBackground(bgColor,
						bgStyle);
			}
			return true;
		}

		if (inStyle && name.equals(FORMAT)) {
			String s = attributes.getValue(ROUND);
			if (s != null && s.length() > 0) {
				int round = Integer.parseInt(s);
				currentStyle = getCurrentStyle().deriveFormat(round);
			}
			return true;
		}

		if (inStyle && name.equals(PARAGRAPH)) {
			String s = attributes.getValue(LS);
			if (s != null && s.length() > 0) {
				float ls = Float.parseFloat(s);
				currentStyle = getCurrentStyle().deriveLineSpacing(ls);
			}
			return true;
		}
		return false;
	}

	public void endElement(String name, StringBuffer value) throws SAXException {
		if (inStyle && name.equals(STYLE)) {
			appendStyle(getCurrentStyle());
			currentStyle = null;
			inStyle = false;
			return;
		}
		if (!inStyle && name.equals(STYLES)) {
			getHandler().popHandler(name);
		}
	}

	protected Object appendStyle(CellStyle style) {
		return getReportBook().appendStyle(style);
	}

	private CellStyle getCurrentStyle() {
		if (currentStyle == null) {
			currentStyle = CellStyle.getDefaultStyle();
		}
		return currentStyle;
	}

	public static void save(PrintWriter writer, CellStyle style) {
		if (style == null)
			return;
		Object id = style.getId();
		if (id == null)
			id = DEFAULT;
		writer.print("<");
		writer.print(STYLE);
		writer.println(" ID=\"" + id + "\" >");
		if (style.getFamily() != null) {
			writer.println(fontToXML(style));
		}
		for (int i = Border.LINE_LEFT; i <= Border.LINE_BOTTOM; i++) {
			if (style.getBorders(i) != null
					&& style.getBorders(i).getLineWidth() > 0) {
				writer.println(lineToXML(i, style.getBorders(i)));
			}
		}
		if (style.getHorizontalAlignment() != CellStyle.LEFT
				|| style.getVerticalAlignment() != CellStyle.TOP
				|| style.getAngle() > 0 || style.isAutoHeight()
				|| !style.isWrapLine()) {
			writer.println(alignToXML(style));
		}

		if (style.getLineSpacing() != CellStyle.DEFAULT_LINE_SPACING) {
			writer.println(paragraphToXML(style));
		}

		if (!style.getBackground().equals(CellStyle.defaultBackground)) {
			writer.println(colorToXML(style));
		}
		if (style.getDecimal() >= 0) {
			writer.print("<");
			writer.print(FORMAT);
			writer.print(" ");
			writer.print(ROUND);
			writer.println("=\"" + style.getDecimal() + "\" />");
		}
		writer.print("</");
		writer.print(STYLE);
		writer.println(">");
	}

	private static String paragraphToXML(CellStyle style) {
		return "<" + PARAGRAPH + " " + LS + "=\"" + style.getLineSpacing() + "\" />";
	}

	private static Object colorToXML(CellStyle style) {
		return "<" + BACKGROUND + " " + COLOR + "=\"" + Utils.colorToHex(style.getBackground()) + "\" " + "/>";
	}

	private static String alignToXML(CellStyle style) {
		StringBuilder result = new StringBuilder("<");
		result.append(ALIGNMENT).append(" ");
		if (style.getVerticalAlignment() != CellStyle.TOP) {
			result.append(VERTICAL).append("=\"");
			result.append(CellStyle.alignToString(style.getVerticalAlignment()));
			result.append("\" ");
		}
		if (style.getHorizontalAlignment() != CellStyle.LEFT) {
			result.append(HORIZONTAL).append("=\"");
			result.append(CellStyle.alignToString(style
					.getHorizontalAlignment()));
			result.append("\" ");
		}
		if (style.getAngle() > 0) {
			result.append(ROTATE).append("=\"");
			result.append(style.getAngle());
			result.append("\" ");
		}
		if (style.isAutoHeight()) {
			result.append(AUTO_HEIGHT).append("=\"true\" ");
		}
		if (!style.isWrapLine())
			result.append(WRAP_LINE).append("=\"").append(style.isWrapLine()).append("\" ");
		if (style.getCarryRows() > 0) {
			result.append(CARRY).append("=\"");
			result.append(style.getCarryRows());
			result.append("\" ");
		}
		result.append("/>");
		return result.toString();
	}

	private static String fontToXML(CellStyle style) {
		StringBuilder result = new StringBuilder("<");
		result.append(FONT).append(" ");
		result.append(FONT_NAME).append("=\"");
		result.append(style.getFamily());
		result.append("\" ");
		result.append(SIZE).append("=\"");
		result.append(style.getSize());
		result.append("\" ");
		result.append(COLOR).append("=\"");
		result.append(Utils.colorToHex(style.getForegroundColor()));
		result.append("\" ");
		if (style.isBold()) {
			result.append(BOLD).append("=\"1\" ");
		}
		if (style.isItalic()) {
			result.append(ITALIC).append("=\"1\" ");
		}
		if (style.isUnderline()) {
			result.append(UNDERLINE).append("=\"1\" ");
		}
		if (style.isStrikethrough()) {
			result.append(STRIKEOUT).append("=\"1\" ");
		}
		result.append("/>");
		return result.toString();
	}

	private static String lineToXML(int pos, Border line) {
		return "<" + BORDER + " " + POSITION + "=\"" + Border.borderPosition[pos] + "\" " + WIDTH + "=\""
				+ line.getLineWidth() + "\" " + STYLE + "=\"" + line.getStyle() + "\" " + COLOR + "=\""
				+ Utils.colorToHex(line.getColor()) + "\" " + "/>";
	}

}
