/*
 * Created on 19.02.2005
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2005-2009 Andrey Kholmanskih. All rights reserved.
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
