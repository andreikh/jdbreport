/*
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
package jdbreport.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * @version 3.0 13.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class JColorBox extends JComboBox<JColorBox.NamedColor> {

	private static final long serialVersionUID = 1L;
	private boolean showText;

	public JColorBox() {
		this(true);
	}

	public JColorBox(boolean showText) {
		super();
		this.showText = showText;
		NamedColor[] colors = getColorItems();
		for (NamedColor color : colors) this.addItem(color);
		this.setRenderer(new ColorCellRenderer(showText));
		setSelectedIndex(0);
	}

	public NamedColor getColor() {
		return (NamedColor) getSelectedItem();
	}

	public void setColor(Color value) {
		if (!(value instanceof NamedColor)) {
			value = new NamedColor(value);
		}
		setSelectedItem(value);
	}

	private NamedColor[] getColorItems() {
		NamedColor white = new NamedColor(Color.WHITE, "white");
		NamedColor aqua = new NamedColor(new Color(127, 255, 212), "aqua");
		NamedColor beige = new NamedColor(new Color(245, 245, 220), "beige");
		NamedColor black = new NamedColor(Color.black, "black");
		NamedColor blue = new NamedColor(Color.blue, "blue");
		NamedColor darkblue = new NamedColor(Color.blue.darker(), "darkblue");
		NamedColor jfcblue = new NamedColor(new Color(153, 153, 204),
				"jfcblue");
		NamedColor green = new NamedColor(Color.green, "green");
		NamedColor cybergreen = new NamedColor(Color.green.brighter(),
				"darkgreen");
		NamedColor darkgreen = new NamedColor(Color.green.darker(),
				"darkgreen");
		NamedColor gray = new NamedColor(Color.gray, "gray");
		NamedColor orange = new NamedColor(new Color(255, 165, 0), "orange");
		NamedColor purple = new NamedColor(new Color(160, 32, 240), "purple");
		NamedColor red = new NamedColor(Color.red, "red");
		NamedColor darkred = new NamedColor(Color.red.darker(), "darkred");
		NamedColor sunpurple = new NamedColor(new Color(100, 100, 255),
				"sunpurple");
		NamedColor suspectpink = new NamedColor(new Color(255, 105, 180),
				"suspectpink");
		NamedColor violet = new NamedColor(new Color(238, 130, 238), "violet");
		NamedColor yellow = new NamedColor(Color.yellow, "yellow");
		NamedColor cyan = new NamedColor(Color.cyan, "cyan");
		NamedColor magenta = new NamedColor(Color.magenta, "magenta");
		return new NamedColor[] { white, black, aqua, beige, blue, 
				darkblue, jfcblue, cybergreen, darkgreen,  gray,
				green, orange, purple, red, darkred, sunpurple, suspectpink,
				 violet, yellow, cyan, magenta };
	}

	/**
	 * @param showText
	 *            The showText to set.
	 */
	public void setShowText(boolean showText) {
		if (this.showText != showText) {
			this.showText = showText;
			setRenderer(new ColorCellRenderer(showText));
		}
	}

	/**
	 * @return Returns the showText.
	 */
	public boolean isShowText() {
		return showText;
	}

	private class ColorCellRenderer extends JComponent implements ListCellRenderer {

		private static final long serialVersionUID = 1L;
		private boolean visibleText = true;
		private String text;
		private Color color;

		public ColorCellRenderer(boolean showText) {
			setOpaque(true);
			this.visibleText = showText;
		}

		public Dimension getPreferredSize() {
			return new Dimension(Math.max(JColorBox.this.getWidth() - 20, visibleText ? 100 : 60), Math.max(JColorBox.this.getHeight(), 20));
		}
		
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			color = (Color) value;
			if (visibleText) {
				text = value.toString();
			} else {
				text = "";
			}
			setBackground(isSelected ? Color.BLUE.darker() : Color.white);
			setForeground(isSelected ? Color.white : color);
			return this;
		}
		
		public void paint(Graphics g) {
            int w = getWidth();
            int h = getHeight();
			Graphics2D g2 = (Graphics2D)g;
			g2.setColor(getBackground());
			g2.fillRect(0, 0, w, h);
			if (visibleText) {
				g2.setBackground(getBackground());
				g2.setColor(getForeground());
				Rectangle2D r = getFont().getStringBounds(text, 0, text.length(), g2.getFontRenderContext());
				float x = (float) ((float)w / 2 - r.getWidth() / 2);
				float y = (float) ((float)h / 2 + r.getHeight() / 3);
				g2.drawString(text, x, y);
			} else {
				g2.setColor(color);
				g2.fillRect(4, 4, w - 8, h - 8);
			}
		}

	}

	public class NamedColor extends Color {
		private static final long serialVersionUID = 1L;
		String name;

		public NamedColor(Color color, String name) {
			super(color.getRGB());
			this.name = name;
		}

		public NamedColor(Color value) {
			this(value, "");
		}

		public String toString() {
			return name;
		}
	}
}
