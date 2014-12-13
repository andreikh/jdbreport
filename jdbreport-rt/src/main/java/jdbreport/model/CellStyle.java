/*
 * Created on 11.03.2005
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2005-2014 Andrey Kholmanskih
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
package jdbreport.model;

import java.awt.Color;

import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import jdbreport.util.GraphicUtil;

/**
 * The cell attributes form the definition of a cell to be rendered.
 * 
 * @version 3.0 13.12.2014
 * 
 * @author Andrey Kholmanskih
 * 
 */
public class CellStyle implements Cloneable, SwingConstants {

	/**
	 * The plain style constant.
	 */
	public static final int PLAIN = 0;

	/**
	 * The bold style constant. This can be combined with the other style
	 * constants (except PLAIN) for mixed styles.
	 */
	public static final int BOLD = 1;

	/**
	 * The italicized style constant. This can be combined with the other style
	 * constants (except PLAIN) for mixed styles.
	 */
	public static final int ITALIC = 2;

	/**
	 * The underline style constant. This can be combined with the other style
	 * constants (except PLAIN) for mixed styles.
	 */
	public static final int UNDERLINE = 4;

	/**
	 * The strikethrough style constant. This can be combined with the other
	 * style constants (except PLAIN) for mixed styles.
	 * 
	 */
	public static final int STRIKETHROUGH = 8;

	/**
	 * Orientation constant used to specify the justify text in Cell.
	 */
	public static final int JUSTIFY = 16;

     /**
     * Superscript
     */
    public final static int SS_SUPER = 1;

    /**
     * Subscript
     */
    public final static int SS_SUB = 2;
	
	/**
	 * Default background color for Cell
	 */
	public static final Color defaultBackground = Color.WHITE;
	
	public static final float DEFAULT_LINE_SPACING = -0.1f; 

	protected static CellStyle defaultStyle;

	private int style = 0;

	private Color fontColor = Color.BLACK;

	private Color background = defaultBackground;

	private int verticalAlignment = TOP;

	private int horizontalAlignment = LEFT;

	private Border[] borders = new Border[4];

	private int angle;

	private int decimal = -1;

	private int bgStyle;

	private boolean wrapLine = true;

	private Object id;

	private float lineSpacing = DEFAULT_LINE_SPACING;

	private boolean autoHeight = false;

	private String name;

	private int size;

	private int carryRows;

	private int typeOffset;

	protected CellStyle() {

	}

	/**
	 * Creates a new CellStyle from the specified name, style, point size and
	 * foreground color.
	 * 
	 * @param name
	 *            the font name
	 * @param style
	 *            the font's style constants for the CellStyle The style
	 *            argument is an integer bitmask that may be PLAIN, or a bitwise
	 *            union of BOLD, ITALIC, UNDERLINE, STRIKETHROUGH
	 * @param size
	 *            the font's point size of the CellStyle
	 * @param color
	 *            the foreground color of the CellStyle
	 */
	public CellStyle(String name, int style, int size, Color color) {
		this.name = name;
		this.style = style;
		this.size = size;
		if (color != null) {
			this.fontColor = color;
		}
	}

	/**
	 * Creates a new CellStyle from the specified name, style and point size.
	 * 
	 * @param name
	 *            the font name
	 * @param style
	 *            the font's style constants for the CellStyle The style
	 *            argument is an integer bitmask that may be PLAIN, or a bitwise
	 *            union of BOLD, ITALIC, UNDERLINE, STRIKETHROUGH
	 * @param size
	 *            the font's point size of the CellStyle
	 */
	public CellStyle(String name, int style, int size) {
		this(name, style, size, null);
	}

	/**
	 * Indicates whether or not font's style is BOLD.
	 * 
	 * @return true if style is BOLD; false otherwise.
	 */
	public boolean isBold() {
		return (style & BOLD) != 0;
	}

	/**
	 * Indicates whether or not font's style is ITALIC.
	 * 
	 * @return true if style is ITALIC; false otherwise.
	 */
	public boolean isItalic() {
		return (style & ITALIC) != 0;
	}

	/**
	 * Indicates whether or not font's style is UNDERLINE.
	 * 
	 * @return true if style is UNDERLINE; false otherwise.
	 */
	public boolean isUnderline() {
		return (style & UNDERLINE) != 0;
	}

	/**
	 * Indicates whether or not font's style is STRIKETHROUGH.
	 * 
	 * @return true if style is STRIKETHROUGH; false otherwise.
	 */
	public boolean isStrikethrough() {
		return (style & STRIKETHROUGH) != 0;
	}

	/**
	 * Returns the verical alignment of this CellStyle The vertical alignment is
	 * a constant that may be TOP, BOTTOM or CENTER
	 * 
	 * @return the vertical alignment of the CellStyle
	 */
	public int getVerticalAlignment() {
		return verticalAlignment;
	}

	/**
	 * Returns the horizontal alignment of this CellStyle The horizontal
	 * alignment is a constant that may be LEFT, RIGHT, CENTER or JUSTIFY
	 * 
	 * @return the horizontal alignment of the CellStyle
	 */
	public int getHorizontalAlignment() {
		return horizontalAlignment;
	}

	/**
	 * Returns the foreground color of this CellStyle
	 * 
	 * @return the foreground color of the CellStyle
	 */
	public Color getForegroundColor() {
		return fontColor;
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj != null) {
			if (!(obj instanceof CellStyle)) return false;
			try {
				CellStyle otherStyle = (CellStyle) obj;

				return name.equals(otherStyle.name)
						&& size == otherStyle.size
						&& fontColor.equals(otherStyle.fontColor)
						&& style == otherStyle.style
						&& background.equals(otherStyle.background)
						&& bgStyle == otherStyle.bgStyle
						&& horizontalAlignment == otherStyle.horizontalAlignment
						&& verticalAlignment == otherStyle.verticalAlignment
						&& equalsBorders(otherStyle.borders)
						&& decimal == otherStyle.decimal
						&& angle == otherStyle.angle
						&& autoHeight == otherStyle.autoHeight
						&& wrapLine == otherStyle.wrapLine
						&& carryRows == otherStyle.carryRows
						&& typeOffset == otherStyle.typeOffset
						&& lineSpacing == otherStyle.lineSpacing;
			} catch (ClassCastException ignored) {
			}
		}
		return false;
	}

	private boolean equalsBorders(Border[] otherBorders) {
		if (borders == null && otherBorders == null) {
			return true;
		}
		if (borders == null || otherBorders == null) {
			return false;
		}
		for (int i = 0; i < borders.length; i++) {
			if (borders[i] == null && otherBorders[i] == null) {
				continue;
			}
			if (borders[i] == null && otherBorders[i] != null) {
				return false;
			}
			if (!borders[i].equals(otherBorders[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the font's style of this CellStyle. The style can be PLAIN, BOLD,
	 * ITALIC, BOLD+ITALIC, UNDERLINE or STRIKETHROUGH.
	 * 
	 * @return the font's style of this CellStyle.
	 */
	public int getStyle() {
		return style;
	}

	/**
	 * Returns the font's family name of this CellStyle.
	 * 
	 * @return a String that is the font's family name of this CellStyle.
	 */
	public String getFamily() {
		return name;
	}

	/**
	 * Returns the font's point size of this CellStyle,
	 * 
	 * @return the font's point size of this CellStyle in 1/72 of an inch units.
	 */
	public int getSize() {
		return size;
	}

	public static String fontStyleStr(int style) {
		if (style == PLAIN) {
			return "PLAIN";
		}
		if (style == BOLD) {
			return "BOLD";
		}
		if (style == ITALIC) {
			return "ITALIC";
		}
		if (style == ITALIC + BOLD) {
			return "BOLDITALIC";
		}
		return "PLAIN";
	}

	public CellStyle deriveBorder(byte position, Border border) {
		CellStyle newStyle = (CellStyle) clone();
		if (position == Border.LINE_BORDER) {
			newStyle.borders[Border.LINE_LEFT] = border;
			newStyle.borders[Border.LINE_TOP] = border;
			newStyle.borders[Border.LINE_RIGHT] = border;
			newStyle.borders[Border.LINE_BOTTOM] = border;
		} else
			newStyle.borders[position] = border;
		return newStyle;
	}

	/**
	 * Creates a new CellStyle object by replicating the current CellStyle
	 * object and applying a new vertical alignment to it. The vertical
	 * alignment is a constant that may be TOP, BOTTOM or CENTER
	 * 
	 * @param value
	 *            the vertical alignment for the new CellStyle
	 * @return a new CellStyle object.
	 */
	public CellStyle deriveVAlign(int value) {
		if (verticalAlignment == value)
			return this;

		CellStyle newStyle = (CellStyle) clone();
		newStyle.verticalAlignment = value;
		return newStyle;
	}

	/**
	 * Creates a new CellStyle object by replicating the current CellStyle
	 * object and applying a new horizontal alignment to it. The horizontal
	 * alignment is a constant that may be LEFT, RIGHT, CENTER or JUSTIFY
	 * 
	 * @param value
	 *            the horizontal alignment for the new CellStyle
	 * @return a new CellStyle object.
	 */
	public CellStyle deriveHAlign(int value) {
		if (value < 0 || horizontalAlignment == value)
			return this;

		CellStyle newStyle = (CellStyle) clone();
		newStyle.horizontalAlignment = value;
		return newStyle;
	}

	/**
	 * Creates a new CellStyle object by replicating the current CellStyle
	 * object and applying a new background color to it.
	 * 
	 * @param value
	 *            the background color for the new CellStyle
	 * @return a new CellStyle object.
	 */
	public CellStyle deriveBackground(Color value) {
		if (value == null)
			value = defaultBackground;
		if (value == null || background.equals(value))
			return this;

		CellStyle newStyle = (CellStyle) clone();
		newStyle.background = value;
		return newStyle;
	}

	/**
	 * Creates a new CellStyle object by replicating the current CellStyle
	 * object and applying a new background color and color style to it.
	 * 
	 * @param bgColor
	 *            the background color for the new CellStyle
	 * @param bgStyle
	 *            the color's style for the new CellStyle
	 * @return a new CellStyle object.
	 */
	public CellStyle deriveBackground(Color bgColor, int bgStyle) {
		if (bgColor == null)
			bgColor = defaultBackground;
		if (background.equals(bgColor) && this.bgStyle == bgStyle)
			return this;

		CellStyle newStyle = (CellStyle) clone();
		newStyle.background = bgColor;
		newStyle.bgStyle = bgStyle;
		return newStyle;
	}

	public int getBgStyle() {
		return bgStyle;
	}

	/**
	 * Creates a new CellStyle object by replicating the current CellStyle
	 * object and applying a new foreground color to it.
	 * 
	 * @param value
	 *            the foreground color for the new CellStyle
	 * @return a new CellStyle object.
	 */
	public CellStyle deriveForeground(Color value) {
		if (value == null) {
			value = new Color(0, 0, 0);
		}
		if (fontColor.equals(value))
			return this;

		CellStyle newStyle = (CellStyle) clone();
		newStyle.fontColor = value;
		return newStyle;
	}

	/**
	 * Creates a new CellStyle object by replicating this CellStyle object and
	 * applying a new font's name, style and size.
	 * 
	 * @param fontName -
	 *            the font's name for the new CellStyle
	 * @param style
	 *            the font's style for the new CellStyle
	 * @param size
	 *            the font's size for the new CellStyle.
	 * @return a new CellStyle object.
	 */
	public CellStyle deriveFont(String fontName, int style, int size) {
		CellStyle newStyle = (CellStyle) clone();
		newStyle.name = fontName;
		newStyle.size = size;
		newStyle.style = style;
		return newStyle;
	}

	/**
	 * Creates a new CellStyle object by replicating the current CellStyle
	 * object and applying a new size to it.
	 * 
	 * @param size
	 *            the font's size for the new CellStyle.
	 * @return a new CellStyle object.
	 */
	public CellStyle deriveFont(float size) {
		if ((int) size == this.size) {
			return this;
		}

		CellStyle newStyle = (CellStyle) clone();
		newStyle.size = (int) size;
		return newStyle;
	}

	/**
	 * Creates a new CellStyle object by replicating the current CellStyle
	 * object and applying a new font's style to it.
	 * 
	 * @param style
	 *            the font's style for the new CellStyle
	 * @return a new CellStyle object.
	 */
	public CellStyle deriveFont(int style) {
		if (style == this.style)
			return this;
		CellStyle newStyle = (CellStyle) clone();
		newStyle.style = style;
		return newStyle;
	}

	/**
	 * Creates a new CellStyle object by replicating the current CellStyle
	 * object and applying a new font's family name to it.
	 * 
	 * @param fontName
	 *            the font's family name for the new CellStyle.
	 * @return a new CellStyle object.
	 */
	public CellStyle deriveFont(String fontName) {
		if (fontName.equals(name))
			return this;
		CellStyle newStyle = (CellStyle) clone();
		newStyle.name = fontName;
		return newStyle;
	}

	/**
	 * Returns the background color of this CellStyle
	 * 
	 * @return the background color of the CellStyle
	 */
	public Color getBackground() {
		return background;
	}

	/**
	 * Returns Border's object of this CellStyle
	 * 
	 * @param pos
	 *            position of border (Border.LINE_LEFT, Border.LINE_RIGHT,
	 *            Border.LINE_TOP, Border.LINE_BOTTOM)
	 * @return Border's object of this CellStyle
	 */
	public Border getBorders(int pos) {
		return borders[pos];
	}

	public float getBorderWidth(int pos) {
		return borders[pos] != null ?  borders[pos].getLineWidth() : 0;
	}
	
	public Object clone() {
		CellStyle clone;
		try {
			clone = (CellStyle) super.clone();
			clone.borders = new Border[4];
			for (int i = 0; i < 4; i++) {
				if (borders[i] != null)
					clone.borders[i] = borders[i].clone();
			}
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.getMessage());
		}
		return clone;
	}

	/**
	 * Creates a new CellStyle object by replicating the current CellStyle
	 * object and applying a new angle of rotation to it.
	 * 
	 * @param angle
	 *            the angle of rotation for the new CellStyle.
	 * @return a new CellStyle object.
	 */
	public CellStyle deriveAngle(int angle) {
		if (this.angle == angle)
			return this;
		CellStyle newStyle = (CellStyle) clone();
		newStyle.angle = angle;
		return newStyle;
	}

	/**
	 * Creates a new CellStyle object by replicating the current CellStyle
	 * object and applying a new decimal position of the numeric value to it.
	 * 
	 * @param decimal
	 *            the decimal position of the numeric value.
	 * @return a new CellStyle object.
	 */
	public CellStyle deriveFormat(int decimal) {
		if (decimal < -1)
			decimal = -1;
		else if (decimal > 15)
			decimal = 15;
		if (this.decimal == decimal)
			return this;
		CellStyle newStyle = (CellStyle) clone();
		newStyle.decimal = decimal;
		return newStyle;
	}

	/**
	 * Converts to StyleConstants alignment
	 * 
	 * @return StyleConstants alignment
	 */
	public int getAlignment() {
		switch (getHorizontalAlignment()) {
		case LEFT:
			return StyleConstants.ALIGN_LEFT;
		case CENTER:
			return StyleConstants.ALIGN_CENTER;
		case RIGHT:
			return StyleConstants.ALIGN_RIGHT;
		case JUSTIFY:
			return StyleConstants.ALIGN_JUSTIFIED;
		}
		return 0;
	}

	/**
	 * Converts the alignment to a string representation
	 * 
	 * @param align
	 *            constant of the alignment
	 * @return the string representation of alignment
	 */
	public static String alignToString(int align) {
		switch (align) {
		case CellStyle.CENTER:
			return "Center";
		case CellStyle.LEFT:
			return "Left";
		case CellStyle.RIGHT:
			return "Right";
		case CellStyle.TOP:
			return "Top";
		case CellStyle.BOTTOM:
			return "Bottom";
		case CellStyle.JUSTIFY:
			return "Justify";
		}
		return "";
	}

	/**
	 * @return the angle of rotation.
	 */
	public int getAngle() {
		return angle;
	}

	/**
	 * @return the decimal position of the numeric value for this CellStyle.
	 */
	public int getDecimal() {
		return decimal;
	}

	/**
	 * Returns id for this CellStyle
	 * 
	 * @return id for this CellStyle
	 */
	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	/**
	 * Converts CellStyle's attributes to javax.swing.text.AttributeSet
	 * 
	 * @return the AttributeSet object
	 */
	public AttributeSet getAttributeSet() {
		MutableAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setSpaceAbove(attr, getSpaceAbove());
		StyleConstants.setFontFamily(attr, getFamily());
		int fs = (int) Math.round(GraphicUtil.getScaleY() * getSize());
		StyleConstants.setFontSize(attr, fs);
		StyleConstants.setLineSpacing(attr, getLineSpacing());
		StyleConstants.setAlignment(attr, getAlignment());
		StyleConstants.setBold(attr, isBold());
		StyleConstants.setItalic(attr, isItalic());
		StyleConstants.setUnderline(attr, isUnderline());
		StyleConstants.setStrikeThrough(attr, isStrikethrough());
		if (getForegroundColor() != null)
			StyleConstants.setForeground(attr, getForegroundColor());
		if (getBackground() != null)
			StyleConstants.setBackground(attr, getBackground());

		return attr;
	}

	public float getSpaceAbove() {
		return 0.0f;
	}

	public float getLineSpacing() {
		return lineSpacing;
	}
	
	/**
	 * Returns the default CellStyle
	 * 
	 * @return the default CellStyle
	 */
	public static synchronized CellStyle getDefaultStyle() {
		if (defaultStyle == null) {
			defaultStyle = new CellStyle();
			defaultStyle.name = "Default";
			defaultStyle.size = 10;
		}
		return defaultStyle;
	}

	/**
	 * Determines whether the row's height will be set automatically
	 * 
	 * @return the autoHeight of the CellStyle
	 */
	public boolean isAutoHeight() {
		return autoHeight;
	}

	/**
	 * Creates a new CellStyle object by replicating the current CellStyle
	 * object and applying a new autoHeight to it.
	 * 
	 * @param b
	 *            the autoHeight attribute for the new CellStyle.
	 * @return a new CellStyle object.
	 */
	public CellStyle deriveAutoHeight(boolean b) {
		if (autoHeight != b) {
			CellStyle newStyle = (CellStyle) clone();
			newStyle.autoHeight = b;
			return newStyle;
		}
		return this;
	}

	public boolean isWrapLine() {
		return wrapLine;
	}

	public CellStyle deriveWrapLine(boolean b) {
		if (wrapLine != b) {
			CellStyle newStyle = (CellStyle) clone();
			newStyle.wrapLine = b;
			return newStyle;
		}
		return this;
	}

	/**
	 * Creates a new CellStyle object by replicating the current CellStyle
	 * object and applying a new line spacing to it. 
	 * 
	 * @param ls
	 *            the line spacing for the new CellStyle
	 * @return a new CellStyle object.
	 */
	public CellStyle deriveLineSpacing(float ls) {
		if (lineSpacing != ls) {
			CellStyle newStyle = (CellStyle) clone();
			newStyle.lineSpacing = ls;
			return newStyle;
		}
		return this;
	}

	/**
	 * Carry rows
	 * 
	 * @return count rows
	 */
	public int getCarryRows() {
		return carryRows;
	}

	public CellStyle deriveCarryRows(int count) {
		if (carryRows != count) {
			CellStyle newStyle = (CellStyle) clone();
			newStyle.carryRows = count;
			return newStyle;
		}
		return this;
	}

    /**
     * Get normal,super or subscript.
     * @return offset type to use (none,super,sub)
     * @since 1.3
     */

    public int getTypeOffset() {
    	return typeOffset;
    }

    /**
 	 * Creates a new CellStyle object by replicating the current CellStyle
	 * object and applying a new typeOffset to it.
     * 
     * @param type normal,super or subscript
     * @return new style
     * @since 1.3
     */
	public CellStyle deriveTypeOffset(int type) {
		if (typeOffset != type) {
			CellStyle newStyle = (CellStyle) clone();
			newStyle.typeOffset = type;
			return newStyle;
		}
		return this;
	}

}
