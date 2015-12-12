/*
 * Created on 17.10.2004
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2014 Andrey Kholmanskih
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
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.table.TableCellRenderer;

import jdbreport.model.Group;
import jdbreport.model.TableRow;
import jdbreport.model.TableRowModel;

/**
 * @version 3.0 13.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class BasicTableRowHeaderUI extends TableRowHeaderUI {

	private static Cursor resizeCursor = Cursor
			.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);

	private static Cursor handCursor = Cursor
			.getPredefinedCursor(Cursor.HAND_CURSOR);

	private Cursor oldCursor;

	/** The RowHeader that is delegating the painting to this UI. */
	protected RowHeader header;

	protected CellRendererPane rendererPane;

	protected MouseInputListener mouseInputListener;

	/**
	 * This inner class is marked &quot;public&quot; due to a compiler bug. This
	 * class should be treated as a &quot;protected&quot; inner class.
	 * Instantiate it only within subclasses of BasicTableUI.
	 */
	public class MouseInputHandler implements MouseInputListener {

		private int mouseYOffset;
		private int draggedIndex;

		public void mouseClicked(MouseEvent e) {
		}

		private boolean canResize(TableRow row) {
			return (row != null) && header.getResizingAllowed();
		}

		private TableRow getResizingRow(Point p) {
			return getResizingRow(p, header.rowAtPoint(p));
		}

		private TableRow getResizingRow(Point p, int row) {
			if (row == -1) {
				return null;
			}
			Rectangle r = header.getHeaderRect(row);
			r.grow(0, -3);
			if (r.contains(p)) {
				return null;
			}
			Group group = header.getRowModel().getGroup(row);

			if (group != null) {
				int x = group.getLevel() * RowHeader.LEVEL_WIDTH;
				if (p.getX() < x) {
					return null;
				}
			}
			int midPoint = r.y + r.height / 2;
			int rowIndex = (p.y < midPoint) ? row - 1 : row;
			if (rowIndex == -1) {
				return null;
			}
			return header.getRowModel().getRow(rowIndex);
		}

		public void mousePressed(MouseEvent e) {
			header.setDraggedRow(null);
			header.setResizingRow(null);
			header.setDraggedDistance(0);

			Point p = e.getPoint();

			TableRowModel rowModel = header.getRowModel();

			Group group = headerGroup(p);
			if (group != null) {
				rowModel.setVisibleGroup(group, !group.isVisible());
				return;
			}

			int index = header.rowAtPoint(p);
			if (index != -1) {
				TableRow resizingRow = getResizingRow(p, index);
				if (canResize(resizingRow)) {
					header.setResizingRow(resizingRow);
					mouseYOffset = p.y - resizingRow.getHeight();
				} else if (header.getReorderingAllowed()) {
					TableRow hitRow = rowModel.getRow(index);
					draggedIndex = index;
					header.setDraggedRow(hitRow);
					mouseYOffset = p.y;
				}
			}
		}

		private void swapCursor(Cursor otherCursor) {
			if (header.getCursor() != otherCursor)
				header.setCursor(otherCursor);
		}

		public void mouseMoved(MouseEvent e) {
			if (header.getResizingRow() != null
					|| canResize(getResizingRow(e.getPoint()))) {
				swapCursor(resizeCursor);
				return;
			}
			if (headerGroup(e.getPoint()) != null) {
				swapCursor(handCursor);
				return;
			}
			swapCursor(oldCursor);
		}

		private Group headerGroup(Point p) {
			TableRowModel rowModel = header.getRowModel();
			int index = header.rowAtPoint(p);

			Group group = rowModel.getGroup(index);

			if (group != null) {
				TableRow aRow = rowModel.getRow(index);
				for (int i = group.getLevel(); i > 0; i--) {
					int x = i * RowHeader.LEVEL_WIDTH;
					if (p.getX() < x && p.getX() > x - RowHeader.LEVEL_WIDTH) {
						if (group.getFirstGroupRow() == aRow
								&& group.getParent().isVisible()
								&& (group.getChildCount() > 1 || !group
										.isVisible())) {
							return group;
						}
					}
					group = group.getParent();
				}
			}
			return null;
		}

		public void mouseDragged(MouseEvent e) {
			int mouseY = e.getY();

			TableRow resizingRow = header.getResizingRow();
			TableRow draggedRow = header.getDraggedRow();

			TableRowModel rm = header.getRowModel();
			if (resizingRow != null) {
				int oldHeight = resizingRow.getHeight();
				int newHeight = Math.max(0, mouseY - mouseYOffset);
				resizingRow.setHeight(newHeight, true);

				Container container;
				if ((header.getParent() == null)
						|| ((container = header.getParent().getParent()) == null)
						|| !(container instanceof JScrollPane)) {
					return;
				}

				JTable table = header.getTable();
				if (table != null) {
					int diff = newHeight - oldHeight;

					/* Resize a table */
					Dimension tableSize = table.getSize();
					tableSize.height += diff;
					table.setSize(tableSize);
				}
			} else if (draggedRow != null) {
				int draggedDistance = mouseY - mouseYOffset;
				int direction = (draggedDistance < 0) ? -1 : 1;
				int rowIndex = viewIndexForRow(draggedRow);
				int newRowIndex = rowIndex + direction;
				if (0 <= newRowIndex && newRowIndex < rm.getRowCount()) {
					int height = rm.getRow(newRowIndex).getHeight();
					if (Math.abs(draggedDistance) > (height / 2)) {
						mouseYOffset = mouseYOffset + direction * height;
						header.setDraggedDistance(draggedDistance - direction
								* height);
						rm.moveDraggedRow(rowIndex, newRowIndex);
						return;
					}
				}
				setDraggedDistance(draggedDistance, rowIndex);
			}
		}

		public void mouseReleased(MouseEvent e) {
			int index = viewIndexForRow(header.getDraggedRow());
			TableRow resizingRow = header.getResizingRow();
			if (index != -1) {
				header.getRowModel().moveDraggedRow(index, draggedIndex);
				header.table.moveRow(draggedIndex, index);
			} else if (resizingRow != null) {
				resizingRow.setHeight(resizingRow.getHeight(), false);
			}

			setDraggedDistance(0, index);
			draggedIndex = -1;
			header.setResizingRow(null);
			header.setDraggedRow(null);
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		//
		// Protected & Private Methods
		//

		private void setDraggedDistance(int draggedDistance, int row) {
			header.setDraggedDistance(draggedDistance);
			if (row != -1) {
				header.getRowModel().moveDraggedRow(row, row);
			}
		}
	}

	//
	// Factory methods for the Listeners
	//

	/**
	 * Creates the mouse listener for the JTable.
	 */
	protected MouseInputListener createMouseInputListener() {
		return new MouseInputHandler();
	}

	//
	// The installation/uninstall procedures and support
	//

	public static ComponentUI createUI(JComponent h) {
		return new BasicTableRowHeaderUI();
	}

	// Installation

	public void installUI(JComponent c) {
		header = (RowHeader) c;
		oldCursor = header.getCursor();
		rendererPane = new CellRendererPane();
		header.add(rendererPane);

		installDefaults();
		installListeners();
		installKeyboardActions();
	}

	/**
	 * Initialize RowHeader properties, e.g. font, foreground, and background.
	 * The font, foreground, and background properties are only set if their
	 * current value is either null or a UIResource, other properties are set if
	 * the current value is null.
	 * 
	 * @see #installUI
	 */
	protected void installDefaults() {
		LookAndFeel.installColorsAndFont(header, "TableHeader.background", //$NON-NLS-1$
				"TableHeader.foreground", "TableHeader.font"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Attaches listeners to the RowHeader.
	 */
	protected void installListeners() {
		mouseInputListener = createMouseInputListener();

		header.addMouseListener(mouseInputListener);
		header.addMouseMotionListener(mouseInputListener);
	}

	/**
	 * Register all keyboard actions on the RowHeader.
	 */
	protected void installKeyboardActions() {
	}

	// Uninstall methods

	public void uninstallUI(JComponent c) {
		uninstallDefaults();
		uninstallListeners();
		uninstallKeyboardActions();

		header.remove(rendererPane);
		rendererPane = null;
		header = null;
		oldCursor = null;
	}

	protected void uninstallDefaults() {
	}

	protected void uninstallListeners() {
		header.removeMouseListener(mouseInputListener);
		header.removeMouseMotionListener(mouseInputListener);

		mouseInputListener = null;
	}

	protected void uninstallKeyboardActions() {
	}

	public void paint(Graphics g, JComponent c) {
		if (header.getRowModel().getRowCount() <= 0) {
			return;
		}
		Rectangle clip = g.getClipBounds();
		Point top = clip.getLocation();
		Point bottom = new Point(clip.x, clip.y + clip.height - 1);
		TableRowModel rowModel = header.getRowModel();
		int rMin = header.rowAtPoint(top);
		int rMax = header.rowAtPoint(bottom);
		// This should never happen.
		if (rMin == -1) {
			rMin = 0;
		}
		// If the table does not have enough rows to fill the view we'll get
		// -1.
		// Replace this with the index of the last row.
		if (rMax == -1) {
			rMax = rowModel.getRowCount() - 1;
		}

		TableRow draggedRow = header.getDraggedRow();
		int rowHeight;
		int rowMargin = rowModel.getRowMargin();
		Rectangle cellRect = header.getHeaderRect(rMin);
		Rectangle groupRect = (Rectangle) cellRect.clone();
		int x0 = cellRect.x;
		int width0 = cellRect.width;
		TableRow aRow;
		Group group;
		Group oldGroup = null;
		int levelX;
		for (int row = rMin; row <= rMax; row++) {
			aRow = rowModel.getRow(row);
			group = rowModel.getGroup(row);
			if (group != null && group != oldGroup) {
				levelX = group.getLevel() * RowHeader.LEVEL_WIDTH;
				oldGroup = group;
				cellRect.x = x0 + levelX;
				cellRect.width = width0 - levelX;
				groupRect.y = cellRect.y;
				groupRect.width = RowHeader.LEVEL_WIDTH;
				int l = group.getLevel();
				Group gr = group;
				int n = l;
				while (gr.getParent() != null) {
					gr = gr.getParent();
					n--;
					if (!gr.isVisible()) {
						l = n;
						group = gr;
					}
				}
				for (int i = l; i >= 1; i--) {
					if (aRow != group.getFirstGroupRow())
						break;
					groupRect.x = x0 + (i - 1) * RowHeader.LEVEL_WIDTH;

					paintGroup(g, groupRect, group, row, rMax);
					group = group.getParent();
				}
			}
			rowHeight = aRow.getHeight();
			cellRect.height = rowHeight - rowMargin;
			if (aRow != draggedRow) {
				paintCell(g, cellRect, row);
			}
			cellRect.y += rowHeight;
		}
		// Paint the dragged row if we are dragging.
		if (draggedRow != null) {
			int draggedRowIndex = viewIndexForRow(draggedRow);
			Rectangle draggedCellRect = header.getHeaderRect(draggedRowIndex);

			group = rowModel.getGroup(draggedRowIndex);
			if (group != null && group != oldGroup) {
				levelX = group.getLevel() * RowHeader.LEVEL_WIDTH;
				draggedCellRect.x = x0 + levelX;
				draggedCellRect.width = width0 - levelX;
			}
			// Draw a gray well in place of the moving row.
			g.setColor(header.getParent().getBackground());
			g.fillRect(draggedCellRect.x, draggedCellRect.y,
					draggedCellRect.width, draggedCellRect.height);

			draggedCellRect.y += header.getDraggedDistance();

			// Fill the background.
			g.setColor(header.getBackground());
			g.fillRect(draggedCellRect.x, draggedCellRect.y,
					draggedCellRect.width, draggedCellRect.height);

			paintCell(g, draggedCellRect, draggedRowIndex);
		}

		rendererPane.removeAll();
	}

	private Component getHeaderRenderer(int rowIndex) {
		TableRow aRow = header.getRowModel().getRow(rowIndex);
		TableCellRenderer renderer = aRow.getHeaderRenderer();
		if (renderer == null) {
			renderer = header.getDefaultRenderer();
		}
		Object value = header.getRowModel().getHeaderValue(rowIndex);
		return renderer.getTableCellRendererComponent(header.getTable(), value,
				false, false, rowIndex, -1);
	}

	private void paintCell(Graphics g, Rectangle cellRect, int rowIndex) {
		Component component = getHeaderRenderer(rowIndex);
		rendererPane.paintComponent(g, component, header, cellRect.x,
				cellRect.y, cellRect.width, cellRect.height, true);
	}

	private void paintGroup(Graphics g, Rectangle groupRect, Group group,
			int rMin, int rMax) {
		if (group.getChildCount() <= 1
				|| !header.getRowModel().isCanHideGroup())
			return;
		if (!group.isVisible()) {
			header.getHideGroupImage().paintIcon(header, g, groupRect.x,
					groupRect.y);
			return;
		} else {
			header.getShowGroupImage().paintIcon(header, g, groupRect.x,
					groupRect.y);
		}
		int lastR = rMin + group.getRowCount() - 1;
		int lastRow = Math.min(rMax, lastR);
		Rectangle r = header.getHeaderRect(lastRow);
		groupRect.height = r.y + r.height - groupRect.y
				- header.getRowModel().getRowMargin();
		int x = groupRect.x + groupRect.width - RowHeader.LEVEL_WIDTH / 2;
		g.setColor(header.table.getGridColor());
		g.drawLine(x, groupRect.y + RowHeader.LEVEL_WIDTH, x, groupRect.y
				+ groupRect.height);
		if (lastR == lastRow)
			g.drawLine(x, groupRect.y + groupRect.height, groupRect.x
					+ groupRect.width - 2, groupRect.y + groupRect.height);
	}

	private int viewIndexForRow(TableRow aRow) {
		TableRowModel rm = header.getRowModel();
		for (int row = 0; row < rm.getRowCount(); row++) {
			if (rm.getRow(row) == aRow) {
				return row;
			}
		}
		return -1;
	}

	private int getHeaderWidth() {
		int width = 0;
		boolean accomodatedDefault = false;
		TableRowModel rowModel = header.getRowModel();
		for (int row = 0; row < rowModel.getRowCount(); row++) {
			TableRow aRow = rowModel.getRow(row);
			if (aRow.getHeaderRenderer() != null || !accomodatedDefault) {
				Component comp = getHeaderRenderer(row);
				int rendererWidth = comp.getPreferredSize().width;
				width = Math.max(width, rendererWidth);
				if (rendererWidth > 0) {
					accomodatedDefault = true;
				}
			}
		}
		return width;
	}

	private Dimension createHeaderSize(long height) {
		if (height > Integer.MAX_VALUE) {
			height = Integer.MAX_VALUE;
		}
		return new Dimension(getHeaderWidth(), (int) height);
	}

	/**
	 * Return the minimum size of the header.
	 */
	public Dimension getMinimumSize(JComponent c) {
		long height = header.getRowModel().getRowCount()
				* header.getRowModel().getMinRowHeight();
		return createHeaderSize(height);
	}

	/**
	 * Return the preferred size of the header.
	 */
	public Dimension getPreferredSize(JComponent c) {
		long height = header.getRowModel().getRowCount()
				* header.getRowModel().getPreferredRowHeight();
		return createHeaderSize(height);
	}

	/**
	 * Return the maximum size of the header.
	 */
	public Dimension getMaximumSize(JComponent c) {
		long height = header.getRowModel().getRowCount()
				* header.getRowModel().getMaxRowHeight();
		return createHeaderSize(height);
	}

}