/*
 * Created 06.09.2007
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2008 Andrey Kholmanskih. All rights reserved.
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

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import jdbreport.grid.JReportGrid;
import jdbreport.grid.ReportCellRenderer;
import jdbreport.model.Cell;
import jdbreport.model.CellStyle;
import jdbreport.model.ReportModel;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class ColorReportRenderer extends DefaultTableCellRenderer implements
		ReportCellRenderer {

	private static final long serialVersionUID = 1L;

	private Cell cell;

	private boolean opaque;

	public void setCell(Cell cell) {
		this.cell = cell;
		opaque = (this.cell != null && this.cell.isSpan());
		if (cell != null && cell.isChild())
			this.cell = cell.getOwner();
	}

	public boolean isOpaque() {
		return (opaque || super.isOpaque());
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		CellStyle style = ((JReportGrid) table).getCellStyle(cell.getStyleId());
		if (isSelected) {
			super.setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		} else {
			Color color = style.getForegroundColor();

			super
					.setForeground((color != null) ? color : table
							.getForeground());

			color = ((ColorValue) cell.getValue()).getValue();

			super
					.setBackground((color != null) ? color : table
							.getBackground());
		}

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
		} else {
			setBorder(noFocusBorder);
		}

		return this;
	}

	public int getTextHeight(ReportModel model, int row, int column) {
		return 0;
	}

}