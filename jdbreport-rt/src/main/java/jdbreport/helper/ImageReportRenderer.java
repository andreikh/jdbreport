/*
 * Created 06.09.2007
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2014 Andrey Kholmanskih
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
 * @version 3.0.13.12.2014
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
				color = UIManager.getColor("Table.focusCellForeground");
				if (color != null) {
					super.setForeground(color);
				}
				color = UIManager.getColor("Table.focusCellBackground");
				if (color != null) {
					super.setBackground(color);
				}
			}
		}

		setText("");
		StyledDocument doc = getStyledDocument();

		if (value != null) {
			doc.setParagraphAttributes(0, 0, style.getAttributeSet(), true);

			prepareIcon(((JReportGrid) table).getReportModel(), row, column);
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

	protected void prepareIcon(ReportModel model, int row, int column
			) {
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