/*
 * Created 13.12.2009
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2009-2010 Andrey Kholmanskih. All rights reserved.
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
 */
package jdbreport.model.svg;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.print.PrintTranscoder;
import org.w3c.dom.svg.SVGDocument;

import and.util.Utilities;

import jdbreport.grid.JReportGrid;
import jdbreport.grid.ReportCellRenderer;
import jdbreport.model.Cell;
import jdbreport.model.CellStyle;
import jdbreport.model.ReportModel;
import jdbreport.util.GraphicUtil;

/**
 * @version 1.0 28.02.2010
 * @author Andrey Kholmanskih
 * 
 */
public class SVGReportRenderer extends JComponent implements ReportCellRenderer {

	private static final long serialVersionUID = 1L;

	private static final Border NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);

	private Cell cell;

	private int verticalAlignment;

	private int horizontalAlignment;

	private int angle;

	protected int rowMargin = JReportGrid.ROW_MARGIN;

	private boolean printState;

	private PrintSVG svgPrinter;

	public SVGReportRenderer() {
		setOpaque(true);
		setDoubleBuffered(false);
		svgPrinter = new PrintSVG();
	}

	public void setCell(Cell cell) {
		this.cell = cell;
		if (cell != null && cell.isChild()) {
			this.cell = cell.getOwner();
		}
	}

	public void invalidate() {
	}

	public void validate() {
	}

	public void revalidate() {
	}

	public void repaint(long tm, int x, int y, int width, int height) {
	}

	public void repaint(Rectangle r) {
	}

	public void repaint() {
	}

	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
	}

	public void firePropertyChange(String propertyName, boolean oldValue,
			boolean newValue) {
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		CellStyle style = ((JReportGrid) table).getCellStyle(cell.getStyleId());
		verticalAlignment = style.getVerticalAlignment();
		horizontalAlignment = style.getHorizontalAlignment();
		angle = style.getAngle();
		printState = ((JReportGrid) table).isPrintState();

		if (isSelected && !((JReportGrid) table).isPrintState()) {
			setForeground(table.getSelectionForeground());
			setBackground(table.getSelectionBackground());
		} else {
			Color color = style.getForegroundColor();

			setForeground((color != null) ? color : table.getForeground());

			color = style.getBackground();

			setBackground((color != null) ? color : table.getBackground());
		}
		setBorder(NO_FOCUS_BORDER);

		return this;
	}

	public void paint(Graphics g) {

		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());

		if (angle != 0) {
			paintRotate(g);
		}

		SVGValue svgValue = (SVGValue) this.cell.getValue();
		if (svgValue != null) {
			SVGImage img = svgValue.getValue();
			if (img != null) {
				paint((Graphics2D) g, img);
			}
		}

	}

	private void paintRotate(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		int a = -angle;
		int cellWidth = getWidth();
		int cellHeight = getHeight();

		if ((angle > 45 && angle < 135) || (angle > 225 && angle < 315)) {
			setSize(getHeight(), getWidth());
		}
		Rectangle r = getTextRect();
		if (r != null) {
			setSize(r.width + r.x + rowMargin, r.height + r.y + rowMargin);
			int x = 0;
			int y = 0;
			Point2D[] points = new Point2D[3];
			points[0] = Utilities.rotatePoint(x, y, x + getWidth(), y, Math
					.toRadians(a));
			points[1] = Utilities.rotatePoint(x, y, x + getWidth(), y
					+ getHeight(), Math.toRadians(a));
			points[2] = Utilities.rotatePoint(x, y, x, y + getHeight(), Math
					.toRadians(a));
			int miny = getHeight();
			int maxy = -miny;
			int minx = getWidth();
			int maxx = -minx;
			for (Point2D p : points) {
				miny = (int) Math.min(miny, p.getY());
				maxy = (int) Math.max(maxy, p.getY());
				minx = (int) Math.min(minx, p.getX());
				maxx = (int) Math.max(maxx, p.getX());
			}
			int x_ = 0;
			int y_ = 0;

			switch (verticalAlignment) {
			case CellStyle.TOP:
				y_ = -miny - rowMargin;
				break;
			case CellStyle.CENTER:
				y_ = cellHeight / 2 - maxy + (maxy - miny) / 2;
				break;
			default:// BOTTOM
				y_ = cellHeight - maxy;
			}

			switch (horizontalAlignment) {
			case CellStyle.LEFT:
			case CellStyle.JUSTIFY:
				x_ = -minx - rowMargin;
				break;
			case CellStyle.CENTER:
				x_ = cellWidth / 2 - maxx + (maxx - minx) / 2;
				break;
			default:// RIGHT
				x_ = cellWidth - maxx;
			}

			g2.translate(x_, y_);
			g2.rotate(Math.toRadians(a), x, y);
		}
	}

	private Rectangle getTextRect() {
		return new Rectangle(0, 0, getWidth(), getHeight());
	}

	private void paint(Graphics2D g2, SVGImage img) {

		if (img != null) {
			if (printState) {
				try {
					svgPrinter.print(g2, (SVGDocument) img.createDocument());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				paintImage(g2, img);
			}
			return;
		}
	}

	protected void paintImage(Graphics2D g2, SVGImage img) {
		if (printState) {
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		}

		if (cell.isScaleIcon()) {
			BufferedImage image = (BufferedImage) img.getImage(getWidth(),
					getHeight(), "png");
			g2.drawImage(image, 0, 0, null);
		} else {

			BufferedImage image = (BufferedImage) img.getImage();
			int iconWidth = image.getWidth();
			int iconHeight = image.getHeight();

			if (printState) {
				double kx = 1.0 / GraphicUtil.getScreenScaleX();
				double ky = 1.0 / GraphicUtil.getScreenScaleY();
				iconWidth = (int) (kx * iconWidth);
				iconHeight = (int) (ky * iconHeight);
			}

			if (verticalAlignment == CellStyle.BOTTOM) {
				int p = iconHeight + rowMargin;
				g2.translate(0, getHeight() - p);
			} else if (verticalAlignment == CellStyle.CENTER) {
				int p = iconHeight + rowMargin;
				g2.translate(0, (getHeight() - p) / 2);
			}

			if (horizontalAlignment == CellStyle.RIGHT) {
				int p = iconWidth + rowMargin;
				g2.translate(getWidth() - p, 0);
			} else if (horizontalAlignment == CellStyle.CENTER) {
				int p = iconWidth + rowMargin;
				g2.translate((getWidth() - p) / 2, 0);
			}

			if (printState) {
				g2.drawImage(image, 0, 0, iconWidth, iconHeight, null);

			} else {
				g2.drawImage(image, 0, 0, null);
			}
		}
	}

	public int getTextHeight(ReportModel model, int row, int column) {
		return 0;
	}

	private class PrintSVG extends PrintTranscoder {

		public PrintSVG() {
			addTranscodingHint(PrintTranscoder.KEY_SCALE_TO_PAGE, false);
		}

		public void print(Graphics g, SVGDocument document) {

			if (cell.isScaleIcon()) {
				addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, getWidth());
				addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT,
						getHeight());
			}

			try {
				super.transcode(document, null, null);
			} catch (TranscoderException e1) {
				e1.printStackTrace();
			}

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);

			if (printState) {
				g2.setRenderingHint(RenderingHintsKeyExt.KEY_TRANSCODING,
						RenderingHintsKeyExt.VALUE_TRANSCODING_PRINTING);
			}

			AffineTransform t = g2.getTransform();
			Shape clip = g2.getClip();

			if (!cell.isScaleIcon()) {
				int iconWidth = Math.round(width);
				int iconHeight = Math.round(height);

				if (printState) {
					double kx = 1 / GraphicUtil.getScreenScaleX();
					double ky = 1 / GraphicUtil.getScreenScaleY();
					iconWidth = (int) Math.round(kx * width);
					iconHeight = (int) Math.round(ky * height);
					AffineTransform at = AffineTransform.getScaleInstance(kx,
							ky);
					g2.transform(at);
				}

				if (verticalAlignment == CellStyle.BOTTOM) {
					int p = iconHeight + rowMargin;
					g2.translate(0, getHeight() - p);
				} else if (verticalAlignment == CellStyle.CENTER) {
					int p = iconHeight + rowMargin;
					g2.translate(0, (getHeight() - p) / 2);
				}

				if (horizontalAlignment == CellStyle.RIGHT) {
					int p = iconWidth + rowMargin;
					g2.translate(getWidth() - p, 0);
				} else if (horizontalAlignment == CellStyle.CENTER) {
					int p = iconWidth + rowMargin;
					g2.translate((getWidth() - p) / 2, 0);
				}
			}

			try {
				root.paint(g2);
			} catch (Exception e) {
				e.printStackTrace();
			}

			g2.setTransform(t);
			g2.setClip(clip);

		}

	}

}