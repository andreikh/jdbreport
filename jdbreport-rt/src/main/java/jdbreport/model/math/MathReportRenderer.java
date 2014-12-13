/*
 * Created 04.05.2011
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2011-2014 Andrey Kholmanskih
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