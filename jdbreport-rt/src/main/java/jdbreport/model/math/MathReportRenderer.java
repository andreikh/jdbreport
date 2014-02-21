/*
 * Created 04.05.2011
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2011 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.model.math;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JTable;

import jdbreport.grid.JReportGrid;
import jdbreport.grid.ReportCellRenderer;
import jdbreport.model.Cell;
import jdbreport.model.CellStyle;
import jdbreport.model.ReportModel;

/**
 * @version 1.0 05.05.2011
 * @author Andrey Kholmanskih
 * 
 */
public class MathReportRenderer extends JComponent implements ReportCellRenderer {

	private static final long serialVersionUID = 1L;

	private Cell cell;

	public MathReportRenderer() {
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
		JComponent component = this;
		MathValue mvalue = (MathValue) cell.getValue();
		if (mvalue != null) {
			MathML m = mvalue.getValue();
			if (m != null) {
				m.setStyle(style);
				component = m.getComponent();
				component.setSize(getSize());
			}
		} 
		
		if (isSelected && !((JReportGrid) table).isPrintState()) {
			component.setForeground(table.getSelectionForeground());
			component.setBackground(table.getSelectionBackground());
		} else {
			Color color = style.getForegroundColor();

			component.setForeground((color != null) ? color : table.getForeground());

			color = style.getBackground();

			component.setBackground((color != null) ? color : table.getBackground());
		}

		return component;
	}

	public int getTextHeight(ReportModel model, int row, int column) {
		return 0;
	}


}