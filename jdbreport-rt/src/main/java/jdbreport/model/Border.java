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
package jdbreport.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.io.Serializable;

/**
 * @version 2.0 03.05.2011
 * 
 * @author Andrey Kholmanskih
 * 
 */
public class Border implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	public static final int psSolid = 0;

	public static final int psDash = 1;

	public static final int psDot = 2;

	public static final int psDashDot = 3;

	public static final int psDashDotDot = 4;

	public static final int psDouble = 5;

	public static final byte LINE_LEFT = 0;

	public static final byte LINE_TOP = 1;

	public static final byte LINE_RIGHT = 2;

	public static final byte LINE_BOTTOM = 3;

	public static final byte LINE_VMIDDLE = 4;

	public static final byte LINE_HMIDDLE = 5;

	public static final byte LINE_BORDER = 6;

	public static final byte LINE_GRID = 7;

	public static final byte LINE_MIDDLE = 8;

	public static final String[] borderPosition = { "Left", "Top", "Right",
			"Bottom" };

	public static final String[] str_position = { "left", "top", "right",
			"bottom" };

	public static final float MAX_WIDTH = 3.0f;

	public static Border defaultBorder = new Border();

	private float width;

	private Color color;

	private int style;

	private transient Stroke stroke;

	public Border() {
		this(new Color(0, 0, 0), 0.5f);
	}

	public Border(Color color) {
		this(color, 0.5f);
	}

	public Border(Color color, float width) {
		this(color, width, 0);
	}

	public Border(Color color, float width, int style) {
		this.width = Math.min(width, MAX_WIDTH);
		if (color != null)
			this.color = color;
		else
			this.color = new Color(0, 0, 0);
		this.style = style;
	}

	public static byte parsePosition(String name) {
		for (byte i = LINE_LEFT; i <= LINE_BOTTOM; i++) {
			if (name.equals(borderPosition[i])) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the border's color
	 * 
	 * @return the border's color.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Returns the border's width in pixels
	 * 
	 * @return the border's width.
	 */
	public float getLineWidth() {
		return width;
	}

	/**
	 * Sets the border's style
	 * 
	 * @param style
	 *            The style to set.
	 */
	public void setStyle(int style) {
		this.style = style;
	}

	/**
	 * Returns the border's style
	 * 
	 * @return the style.
	 */
	public int getStyle() {
		return style;
	}

	public Stroke getStroke() {
		if (stroke == null) {
			float dash[];
			switch (style) {
			case psSolid:
				stroke = new BasicStroke(width, BasicStroke.CAP_SQUARE,
						BasicStroke.JOIN_MITER);
				break;
			case psDash:
				dash = new float[] { 6.0f, 6.0f };
				stroke = new BasicStroke(width, BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
				break;
			case psDot:
				dash = new float[] { 1.0f, 1f };
				stroke = new BasicStroke(width, BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
				break;
			case psDashDot:
				dash = new float[] { 2f, 6.0f, 2.0f, 1.0f };
				stroke = new BasicStroke(width, BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_MITER, 10.0f, dash, 11.0f);
				break;
			case psDashDotDot:
				dash = new float[] { 2f, 6.0f, 2f, 1f, 2f, 1f };
				stroke = new BasicStroke(width, BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
				break;
			case psDouble:
				stroke = new DoubleStroke(width);
				break;
			default:
				stroke = new BasicStroke(width, BasicStroke.CAP_SQUARE,
						BasicStroke.JOIN_MITER);
			}

		}
		return stroke;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + style;
		result = prime * result + Float.floatToIntBits(width);
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
		Border other = (Border) obj;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (style != other.style)
			return false;
		if (Float.floatToIntBits(width) != Float.floatToIntBits(other.width))
			return false;
		return true;
	}

	public Border clone() {
		Border o;
		try {
			o = (Border) super.clone();
			o.stroke = null;
			return o;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

}
