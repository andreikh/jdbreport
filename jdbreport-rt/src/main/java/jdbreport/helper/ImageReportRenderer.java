/*
 * Created 06.09.2007
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2009 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.helper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.text.StyledDocument;

import jdbreport.grid.JReportGrid;
import jdbreport.grid.JReportGrid.TextReportRenderer;
import jdbreport.model.CellStyle;
import jdbreport.model.ReportModel;

/**
 * @version 1.2 02/13/09
 * @author Andrey Kholmanskih
 * 
 */
public class ImageReportRenderer extends TextReportRenderer {

	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		this.hasFocus = hasFocus;

		CellStyle style = ((JReportGrid) table).getCellStyle(cell.getStyleId());
		if (isSelected && !((JReportGrid) table).isPrintState()) {
			super.setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		} else {
			Color color = style.getForegroundColor();

			super
					.setForeground((color != null) ? color : table
							.getForeground());

			color = style.getBackground();

			super
					.setBackground((color != null) ? color : table
							.getBackground());
		}
		setBorder(noFocusBorder);

		if (hasFocus) {
			if (!isSelected && table.isCellEditable(row, column)) {
				Color color;
				color = UIManager.getColor("Table.focusCellForeground"); //$NON-NLS-1$
				if (color != null) {
					super.setForeground(color);
				}
				color = UIManager.getColor("Table.focusCellBackground"); //$NON-NLS-1$
				if (color != null) {
					super.setBackground(color);
				}
			}
		}

		setText(""); //$NON-NLS-1$
		StyledDocument doc = getStyledDocument();

		if (value != null) {
			doc.setParagraphAttributes(0, 0, style.getAttributeSet(), true);

			prepareIcon(((JReportGrid) table).getReportModel(), row, column,
					doc);
		}
		return this;
	}

	public int getTextHeight(ReportModel model, int row, int column) {
		if (cell.getValue() != null && !cell.isScaleIcon()
				&& cell.getValue() instanceof ImageValue) {
			AbstractImageValue<?> v = (AbstractImageValue<?>) cell.getValue();
			return v.getIcon().getIconHeight()
					+ model.getRowModel().getRowMargin();
		}
		return 0;
	}

	protected void prepareIcon(ReportModel model, int row, int column,
			StyledDocument doc) {
		Icon icon = ((AbstractImageValue<?>) cell.getValue()).getIcon();
		if (icon != null) {
			if (cell.isScaleIcon() && icon instanceof ImageIcon) {
				Image image = ((ImageIcon) icon).getImage();
				Rectangle r = model.getCellRect(row, column, false,
						getComponentOrientation().isLeftToRight());
				image = image.getScaledInstance((int) r.getWidth() - 2, (int) r
						.getHeight() - 2, Image.SCALE_FAST);
				icon = new ImageIcon(image);
			}
			insertIcon(icon);
		}
	}

}