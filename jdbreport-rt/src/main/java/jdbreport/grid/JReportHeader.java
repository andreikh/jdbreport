/*
 * Created on 19.02.2005
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2005-2014 Andrey Kholmanskih
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
package jdbreport.grid;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import jdbreport.grid.undo.ResizingColumnUndoItem;

/**
 * @version 1.2 02/08/09
 * @author Andrey Kholmanskih
 * 
 */
public class JReportHeader extends JTableHeader {

	private static final long serialVersionUID = 5710050270955020136L;
	private ResizingColumnUndoItem undoItem;
	private Dimension oldDimension;

	public JReportHeader(TableColumnModel cm) {
		super(cm);
	}

	public void columnMoved(TableColumnModelEvent e) {
		int minc = Math.min(e.getFromIndex(), e.getToIndex());
		int maxc = Math.max(e.getFromIndex(), e.getToIndex());
		for (int c = minc; c <= maxc; c++) {
			getColumnModel().getColumn(c).setModelIndex(c);
		}
		repaint();
	}

	protected TableCellRenderer createDefaultRenderer() {
		DefaultTableCellRenderer label = new UIResourceTableCellRenderer();
		label.setHorizontalAlignment(JLabel.CENTER);
		return label;
	}

	private class UIResourceTableCellRenderer extends DefaultTableCellRenderer
			implements UIResource {
		private static final long serialVersionUID = 4333976038294374872L;

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (table != null) {
				JTableHeader header = table.getTableHeader();
				if (header != null) {
					setForeground(header.getForeground());
					setBackground(header.getBackground());
					setFont(header.getFont());
				}
			}

			setText((value == null) ? "" + (column + 1) : value.toString()); //$NON-NLS-1$
			setBorder(BorderUIResource.getEtchedBorderUIResource());
			return this;
		}
	}

	@Override
	public void setResizingColumn(TableColumn aColumn) {
		if (aColumn != null && getResizingColumn() == null) {
			int[] columns = { table.getColumnModel().getColumnIndex(
					aColumn.getIdentifier()) };
			int[] widths = { aColumn.getWidth() };
			undoItem = new ResizingColumnUndoItem((JReportGrid) table, columns,
					widths);
		} else {
			if (aColumn == null && getResizingColumn() != null
					&& undoItem != null) {
				if (undoItem.getWidths()[0] != getResizingColumn().getWidth()) {
					((JReportGrid) table).pushUndo(undoItem);
				}
				undoItem = null;
			}
		}
		super.setResizingColumn(aColumn);
	}

	@Override
	public void setVisible(boolean flag) {
		if (flag != isVisible()) {
			super.setVisible(flag);

			if (!flag) {
				oldDimension = getPreferredSize();
				setPreferredSize(new Dimension(0, 0));
			} else {
				if (oldDimension != null)
					setPreferredSize(oldDimension);
			}
		}
	}

}
