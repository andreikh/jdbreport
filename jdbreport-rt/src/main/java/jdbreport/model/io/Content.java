/*
 * Content.java
 *
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
package jdbreport.model.io;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import jdbreport.model.CellStyle;
import jdbreport.util.Utils;


/**
 * @author  Andrey Kholmanskih
 * @version 1.0 10.08.2009
 */
public class Content {
	
	private String text;
	private AttributeSet attributeSet;

	public Content(String text, AttributeSet attrs) {
		super();
		this.text = text;
		this.attributeSet = attrs;
	}

	public String getText() {
		return text;
	}

	public AttributeSet getAttributeSet() {
		return attributeSet;
	}

	public static List<Content> getHTMLContentList(HTMLDocument doc) {
		List<Content> contentList = new ArrayList<Content>();
		Element rootElement = doc.getDefaultRootElement();
		for (int i = 0; i < rootElement.getElementCount(); i++) {
			Element el = rootElement.getElement(i);
			if ("body".equals(el.getName())) {
				for (int n = 0; n < el.getElementCount(); n++) {
					extractElements(el.getElement(n), contentList);
				}
			}
		}
		return contentList;
	}

	private static void extractElements(Element el, List<Content> contentList) {
		if (el != null) {
			if ("content".equals(el.getName())) {
				try {
					String text = el.getDocument().getText(el.getStartOffset(),
							el.getEndOffset() - el.getStartOffset());
					if (text.trim().length() > 0) {
						Content content = new Content(text, el.getAttributes());
						contentList.add(content);
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			} else if ("br".equals(el.getName())) {
				Content content = new Content("\n", el.getAttributes());
				contentList.add(content);
			}
			for (int i = 0; i < el.getElementCount(); i++) {
				extractElements(el.getElement(i), contentList);
			}
		}

	}
	
	public CellStyle createTextStyle(CellStyle parentStyle) {
		return createTextStyle(parentStyle, CellStyle.getDefaultStyle());
	}
	
	public CellStyle createTextStyle(CellStyle parentStyle, CellStyle style) {
		if (attributeSet == null) return null; 
		String family = null;
		String sizeStr = null;
		Color fontColor = null;
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

			if (name.equals("font-weight")) {
				bold = attribute.equals("bold");
			} else if (name.equals("font-style")) {
				italic = attribute.equals("italic");
			} else if (name.equals("text-decoration")) {
				if (attribute.equals("underline")) {
					underline = true;
				} else if (attribute.equals("line-through")) {
					line_through = true;
				}
			} else if (name.equals("font-family")) {
				family = attribute;
			} else if (name.equals("font-size")) {
				sizeStr = attribute;

			} else if (name.equals("color")) {
				fontColor = Utils.colorByName(attribute);
				if (fontColor == null) {
					try {
						fontColor = Utils.stringToColor(attribute);
					} catch (Exception e) {

					}
				}
			} else if (name.equals("vertical-align")) {
				if (attribute.equals("sub")) {
					sub = true;
				} else if (attribute.equals("sup")) {
					sup = true;
				}
			}
		}
		if (family != null || bold || italic || underline || line_through
				|| fontColor != null || sizeStr != null || sub || sup) {

			if (family != null) {
				style = style.deriveFont(family);
			}
			int fontStyle = 0;
			if (bold) {
				fontStyle = fontStyle | CellStyle.BOLD;
			}
			if (italic) {
				fontStyle = fontStyle | CellStyle.ITALIC;
			}
			if (underline) {
				fontStyle = fontStyle | CellStyle.UNDERLINE;
			}
			if (line_through) {
				fontStyle = fontStyle | CellStyle.STRIKETHROUGH;
			}
			if (fontStyle > 0) {
				style = style.deriveFont(fontStyle);
			}
			if (fontColor != null) {
				style = style.deriveForeground(fontColor);
			}
			if (sizeStr != null) {
				short size = (short) Float.parseFloat(sizeStr);
				if (sizeStr.charAt(0) == '+' || sizeStr.charAt(0) == '-') {
					size = (short) (Content.pointToSize((short) parentStyle
							.getSize()) + size);
				}
				style = style.deriveFont((float)Content.sizeToPoints(size));
			}
			if (sup) {
				style = style.deriveTypeOffset(CellStyle.SS_SUPER);
			} else if (sub) {
				style = style.deriveTypeOffset(CellStyle.SS_SUB);
			}
			return style;
		}
		return null;
	}

	public static short sizeToPoints(short size) {
		switch (size) {
		case 1:return 6;
		case 2:return 7;
		case 3:return 9;
		case 4:return 11;
		case 5:return 14;
		case 6:return 18;
		case 7:return 26;
		}
		if (size > 7) return 26; 
		else return 0;
	}

	public static short pointToSize(short point) {
		if (point <= 6) return 1;
		if (point <= 7) return 2;
		if (point <= 9) return 3;
		if (point <= 11) return 4;
		if (point <= 14) return 5;
		if (point <= 18) return 6;
		return 7;
	}

}