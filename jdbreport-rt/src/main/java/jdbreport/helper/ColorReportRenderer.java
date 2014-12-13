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