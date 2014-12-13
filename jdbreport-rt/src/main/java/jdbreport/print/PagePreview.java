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
package jdbreport.print;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

import jdbreport.util.GraphicUtil;

/**
 * @version 2.0 12.12.2009
 * @author Andrey Kholmanskih
 * 
 */
class PagePreview extends JPanel {

	private static final long serialVersionUID = 1L;

	protected int imgWidth;

	protected int imgHeight;

	protected Image image;

	private Border defaultBorder = new MatteBorder(1, 1, 2, 2, Color.black);

	private Border focusBorder = new MatteBorder(1, 1, 2, 2, Color.blue);

	private boolean focus;

	private int pageIndex;

	private int widthPage;

	private int heightPage;

	public PagePreview(int w, int h, int scale, Image source, int pageIndex) {
		widthPage = w;
		heightPage = h;
		image = source;
		setScale(scale);
		this.pageIndex = pageIndex;
		setBackground(Color.white);
		setBorder(defaultBorder);
	}

	private void setScale(int scale) {
		setScaledSize(
				(int) (widthPage * scale / 100 * GraphicUtil.getScaleX()),
				(int) (heightPage * scale / 100 * GraphicUtil.getScaleY()));
	}

	private void setScaledSize(int w, int h) {
		if (imgWidth != w || imgHeight != h) {
			imgWidth = w;
			imgHeight = h;
		}
	}

	public Dimension getPreferredSize() {
		Insets ins = getInsets();
		return new Dimension(imgWidth + ins.left + ins.right, imgHeight
				+ ins.top + ins.bottom);
	}

	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public void paint(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		if (image != null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			Insets ins = getInsets();
			g2.drawImage(image, ins.left, ins.top, imgWidth, imgHeight, null);

		}
		paintBorder(g);
	}

	/**
	 * @param focus
	 *            the focus to set
	 */
	public void setFocus(boolean focus) {
		if (focus != this.focus) {
			this.focus = focus;
			if (focus) {
				setBorder(focusBorder);
			} else {
				setBorder(defaultBorder);
			}
			repaint();
		}
	}

	/**
	 * @return the focus
	 */
	public boolean isFocus() {
		return focus;
	}

	public int getPageIndex() {
		return pageIndex;
	}

}
