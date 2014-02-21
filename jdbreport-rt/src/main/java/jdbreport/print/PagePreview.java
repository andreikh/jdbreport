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
