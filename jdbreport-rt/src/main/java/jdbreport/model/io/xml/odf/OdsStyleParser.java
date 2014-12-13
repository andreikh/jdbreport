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
package jdbreport.model.io.xml.odf;

import java.awt.Color;
import java.util.StringTokenizer;

import jdbreport.model.Border;
import jdbreport.model.CellStyle;
import jdbreport.model.Units;
import jdbreport.model.io.xml.DefaultReaderHandler;
import jdbreport.model.io.xml.odf.CommonStyle.Break;
import jdbreport.model.print.ReportPage;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @version 1.2 04/10/08
 * @author Andrey Kholmanskih
 * 
 */
class OdsStyleParser extends OdsReportParser {

	private boolean inStyle;

	private CellStyle currentStyle;

	private CellStyle defaultStyle;

	private boolean inColumnStyle;

	private boolean inRowStyle;

	public static final String[] LINE_STYLE = { "solid", "dash", "dot",
			"dashdot", "dashdotdot", "double" };

	private Object currentId;

	private boolean inPageLayout;

	private String currentPageId;

	private boolean inTableStyle;

	private TableStyle currentTableStyle;

	public OdsStyleParser(DefaultReaderHandler reportHandler) {
		super(reportHandler);
	}

	public boolean startElement(String name, Attributes attributes) {
		if (inStyle) {
			if (name.equals("style:table-cell-properties")) {
				addCellProperties(attributes);
			} else if (name.equals("style:text-properties")) {
				addTextProperties(attributes);
			} else if (name.equals("style:paragraph-properties")) {
				addParagraphProperties(attributes);
			}
			return false;
		}
		if (inColumnStyle) {
			if (name.equals("style:table-column-properties")) {
				addColumnStyle(attributes);
			}
			return false;
		}
		if (inRowStyle) {
			if (name.equals("style:table-row-properties")) {
				addRowStyle(attributes);
			}
			return false;
		}
		if (inTableStyle) {
			if (name.equals("style:table-properties")) {
				addTableStyle(attributes);
			}
			return false;
		}
		if (name.equals("style:style")) {
			currentId = attributes.getValue("style:name");
			if (attributes.getValue("style:family").equals("table-cell")) {
				if (attributes.getValue("style:parent-style-name") != null) {
					Object parentId = attributes
							.getValue("style:parent-style-name");
					CellStyle style = getReportBook().getStyles(parentId);
					if (style != null)
						currentStyle = (CellStyle) style.clone();
				} else {
					if (defaultStyle == null) {
						currentStyle = (CellStyle) CellStyle.getDefaultStyle()
								.deriveWrapLine(false);
					} else
						currentStyle = (CellStyle) defaultStyle.clone();
				}
				currentStyle.setId(currentId);
				inStyle = true;
				return true;
			}
			if (attributes.getValue("style:family").equals("table-column")) {
				inColumnStyle = true;
				return true;
			}
			if (attributes.getValue("style:family").equals("table-row")) {
				inRowStyle = true;
				return true;
			}
			if (attributes.getValue("style:family").equals("table")) {
				inTableStyle = true;
				String masterPageName = attributes
						.getValue("style:master-page-name");
				currentTableStyle = new TableStyle(attributes
						.getValue("style:name"), masterPageName);
				return true;
			}
		} else if (name.equals("style:default-style")) {
			currentId = "Default";
			if (attributes.getValue("style:family").equals("table-cell")) {
				defaultStyle = (CellStyle) CellStyle.getDefaultStyle()
						.deriveVAlign(CellStyle.BOTTOM);
				defaultStyle = defaultStyle.deriveWrapLine(false);
				currentStyle = defaultStyle;
				currentStyle.setId(currentId);
				inStyle = true;
				return true;
			}
		} else if (name.equals("style:page-layout")) {
			inPageLayout = true;
			currentPageId = attributes.getValue("style:name");
			return true;
		} else if (name.equals("style:master-page")) {
			addMasterPageStyle(attributes);
			return false;
		}
		if (inPageLayout) {
			if (name.equals("style:page-layout-properties")) {
				addPageStyle(attributes);
				return false;
			}
		}
		return false;
	}

	public void endElement(String name, StringBuffer value) throws SAXException {
		if (inStyle) {
			if (name.equals("style:default-style")) {
				defaultStyle = currentStyle;
				getReportBook().appendStyle(defaultStyle);
				currentStyle = null;
				inStyle = false;
				return;
			}
			if (name.equals("style:style")) {
				getReportBook().appendStyle(getCurrentStyle());
				currentStyle = null;
				inStyle = false;
				return;
			}
		} else if (inColumnStyle) {
			if (name.equals("style:style")) {
				inColumnStyle = false;
				return;
			}
		} else if (inRowStyle) {
			if (name.equals("style:style")) {
				inRowStyle = false;
				return;
			}
		} else if (inTableStyle) {
			if (name.equals("style:style")) {
				currentTableStyle = null;
				inTableStyle = false;
				return;
			}
		} else if (inPageLayout) {
			if (name.equals("style:page-layout")) {
				inPageLayout = false;
				return;
			}
		}

		if (name.equals("office:automatic-styles")) {
			getHandler().popHandler(name);
			return;
		}
		if (name.equals("office:styles")) {
			getHandler().popHandler(name);
			return;
		}
		if (name.equals("office:master-styles")) {
			getHandler().popHandler(name);
			return;
		}
	}

	private boolean addRowStyle(Attributes attributes)
			throws NumberFormatException {
		double height = 14;
		String s = attributes.getValue("style:row-height");
		if (s != null) {
			Units unit = Units.findUnits(s.substring(s.length() - 2));
			height = unit.setValue(Double.parseDouble(s.substring(0,
					s.length() - 2)));
		}
		boolean isAfter = false;
		Break brk = Break.auto;
		s = attributes.getValue("fo:break-before");
		if (s != null) {
			brk = Break.valueOf(s);
		} else {
			s = attributes.getValue("fo:break-after");
			if (s != null) {
				brk = Break.valueOf(s);
				isAfter = true;
			}
		}
		boolean optimalHeight = false;
		s = attributes.getValue("style:use-optimal-row-height");
		if (s != null) {
			optimalHeight = Boolean.parseBoolean(s);
		}
		RowStyle currentRowStyle = new RowStyle(height, brk, isAfter,
				optimalHeight);
		getRowStyles().put(currentId, currentRowStyle);
		return false;
	}

	private boolean addColumnStyle(Attributes attributes)
			throws NumberFormatException {
		double width = 64;
		String s = attributes.getValue("style:column-width");
		if (s != null) {
			Units unit = Units.findUnits(s.substring(s.length() - 2));
			width = unit.setValue(Double.parseDouble(s.substring(0,
					s.length() - 2)));
		}
		boolean isAfter = false;
		Break brk = Break.auto;
		s = attributes.getValue("fo:break-before");
		if (s != null) {
			brk = Break.valueOf(s);
		} else {
			s = attributes.getValue("fo:break-after");
			if (s != null) {
				brk = Break.valueOf(s);
				isAfter = true;
			}
		}
		boolean optimalWidth = false;
		s = attributes.getValue("style:use-optimal-column-width");
		if (s != null) {
			optimalWidth = Boolean.parseBoolean(s);
		}
		ColumnStyle currentColumnStyle = new ColumnStyle(width, brk, isAfter,
				optimalWidth);
		getColumnStyles().put(currentId, currentColumnStyle);
		return false;
	}

	private void addPageStyle(Attributes attributes)
			throws NumberFormatException {
		double width = 0;
		String s = attributes.getValue("fo:page-width");
		if (s != null) {
			Units unit = Units.findUnits(s.substring(s.length() - 2));
			width = unit.setValue(Double.parseDouble(s.substring(0,
					s.length() - 2)));
		}
		double height = 0;
		s = attributes.getValue("fo:page-height");
		if (s != null) {
			Units unit = Units.findUnits(s.substring(s.length() - 2));
			height = unit.setValue(Double.parseDouble(s.substring(0,
					s.length() - 2)));
		}
		double top = 56.7;
		s = attributes.getValue("fo:margin-top");
		if (s != null) {
			Units unit = Units.findUnits(s.substring(s.length() - 2));
			top = unit.setValue(Double.parseDouble(s.substring(0,
					s.length() - 2)));
		}
		double bottom = 56.7;
		s = attributes.getValue("fo:margin-bottom");
		if (s != null) {
			Units unit = Units.findUnits(s.substring(s.length() - 2));
			bottom = unit.setValue(Double.parseDouble(s.substring(0,
					s.length() - 2)));
		}
		double left = 56.7;
		s = attributes.getValue("fo:margin-left");
		if (s != null) {
			Units unit = Units.findUnits(s.substring(s.length() - 2));
			left = unit.setValue(Double.parseDouble(s.substring(0,
					s.length() - 2)));
		}
		double right = 56.7;
		s = attributes.getValue("fo:margin-right");
		if (s != null) {
			Units unit = Units.findUnits(s.substring(s.length() - 2));
			right = unit.setValue(Double.parseDouble(s.substring(0,
					s.length() - 2)));
		}
		int orientation = ReportPage.PORTRAIT;
		s = attributes.getValue("style:print-orientation");
		if (s != null && s.equals("landscape")) {
			orientation = ReportPage.LANDSCAPE;
		}
		PageStyle currentPageStyle = new PageStyle(width, height, top, bottom,
				left, right, orientation);

		s = attributes.getValue("style:first-page-number");
		if (s != null) {
			if (s.equals("continue"))
				currentPageStyle.setFirstPage(1);
			else
				currentPageStyle.setFirstPage(Integer.parseInt(s));
		}
		s = attributes.getValue("style:scale-to");
		if (s != null) {
			s = s.substring(0, s.length() - 1);
			currentPageStyle.setScale(Integer.parseInt(s));
		}

		getPageStyles().put(currentPageId, currentPageStyle);
	}

	private void addMasterPageStyle(Attributes attributes) {
		String name = attributes.getValue("style:name");
		String pageName = attributes.getValue("style:page-layout-name");
		getMasterPageStyles().put(name, new MasterPageStyle(name, pageName));
	}

	private void addTableStyle(Attributes attributes) {
		boolean display = true;
		String s = attributes.getValue("table:display");
		if (s != null) {
			display = Boolean.parseBoolean(s);
		}
		currentTableStyle.setDisplay(display);
		getTableStyles().put(currentTableStyle.getName(), currentTableStyle);
	}

	private CellStyle getCurrentStyle() {
		if (currentStyle == null) {
			currentStyle = defaultStyle == null ? CellStyle.getDefaultStyle()
					: defaultStyle;
		}
		return currentStyle;
	}

	private void addCellProperties(Attributes attributes) {
		String s;
		s = attributes.getValue("style:vertical-align");
		if (s != null) {
			int flags = CellStyle.BOTTOM;
			if ("bottom".equals(s))
				flags = CellStyle.BOTTOM;
			else if ("middle".equals(s))
				flags = CellStyle.CENTER;
			else if ("top".equals(s))
				flags = CellStyle.TOP;
			currentStyle = getCurrentStyle().deriveVAlign(flags);
		}

		s = attributes.getValue("fo:wrap-option");
		if (s != null) {
			if ("wrap".equals(s))
				currentStyle = getCurrentStyle().deriveWrapLine(true);
			else if ("no-wrap".equals(s))
				currentStyle = getCurrentStyle().deriveWrapLine(false);
		}

		s = attributes.getValue("style:rotation-angle");
		if (s != null) {
			int angle = Integer.parseInt(s);
			if (angle != 0)
				currentStyle = getCurrentStyle().deriveAngle(angle);
		}

		s = attributes.getValue("fo:background-color");
		if (s != null) {
			if ("transparent".equals(s)) {
				
			} else {
				try {
					Color bgColor = new Color(Integer.decode(s));
					currentStyle = getCurrentStyle().deriveBackground(bgColor);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}

		s = attributes.getValue("fo:border");
		if (s != null) {
			parseBorder(s, Border.LINE_BORDER);
		} else {
			s = attributes.getValue("fo:border-left");
			if (s != null) {
				parseBorder(s, Border.LINE_LEFT);
			}
			s = attributes.getValue("fo:border-top");
			if (s != null) {
				parseBorder(s, Border.LINE_TOP);
			}
			s = attributes.getValue("fo:border-right");
			if (s != null) {
				parseBorder(s, Border.LINE_RIGHT);
			}
			s = attributes.getValue("fo:border-bottom");
			if (s != null) {
				parseBorder(s, Border.LINE_BOTTOM);
			}
		}
	}

	private void parseBorder(String s, byte pos) {
		if ("none".equals(s))
			return;
		StringTokenizer token = new StringTokenizer(s);
		String ws = token.nextToken();
		double w = Double.parseDouble(ws.substring(0, ws.length() - 2));
		String u = ws.substring(ws.length() - 2);
		float width = 0.5f;
		Units unit = Units.findUnits(u);
		if (unit != null) {
			width = unit.getXPixels(w);
		}
		int style = Border.psSolid;
		String ss = token.nextToken();
		for (int i = 0; i < LINE_STYLE.length; i++) {
			if (LINE_STYLE.equals(ss)) {
				style = i;
				break;
			}
		}
		Color color = Color.getColor(token.nextToken());
		Border border = new Border(color, width, style);
		if (border != null)
			currentStyle = getCurrentStyle().deriveBorder(pos, border);
	}

	private void addTextProperties(Attributes attributes) {

		String fontName = attributes.getValue("style:font-name");
		if (fontName != null)
			currentStyle = getCurrentStyle().deriveFont(fontName);

		String s = attributes.getValue("fo:font-size");
		if (s != null) {
			String u = s.substring(s.length() - 2);
			Units unit = Units.findUnits(u);
			float size = Float.parseFloat(s.substring(0, s.length() - 2));
			if (unit != null) {
				size = (int) unit.getValue(size);
			}
			currentStyle = getCurrentStyle().deriveFont(size);
		}

		int fontStyle = 0;
		s = attributes.getValue("fo:font-weight");
		if ("bold".equals(s)) {
			fontStyle |= CellStyle.BOLD;
		}
		s = attributes.getValue("fo:font-style");
		if ("italic".equals(s)) {
			fontStyle |= CellStyle.ITALIC;
		}
		s = attributes.getValue("style:text-underline-style");
		if (s != null && !"none".equals(s)) {
			fontStyle |= CellStyle.UNDERLINE;
		}
		s = attributes.getValue("style:text-line-through-style");
		if (s != null && !"none".equals(s)) {
			fontStyle |= CellStyle.STRIKETHROUGH;
		}
		if (fontStyle > 0) {
			currentStyle = getCurrentStyle().deriveFont(fontStyle);
		}

		s = attributes.getValue("fo:color");
		if (s != null) {
			currentStyle = getCurrentStyle().deriveForeground(
					new Color(Integer.decode(s)));
		}
	}

	private void addParagraphProperties(Attributes attributes) {
		String s;
		s = attributes.getValue("fo:text-align");
		if (s != null) {
			int flags = CellStyle.LEFT;
			if ("center".equals(s))
				flags = CellStyle.CENTER;
			else if ("end".equals(s))
				flags = CellStyle.RIGHT;
			else if ("justify".equals(s))
				flags = CellStyle.JUSTIFY;
			currentStyle = getCurrentStyle().deriveHAlign(flags);
		}
	}

}
