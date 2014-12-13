/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2014 Andrey Kholmanskih
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

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.accessibility.*;

import java.beans.PropertyChangeListener;

import java.io.ObjectOutputStream;
import java.io.IOException;

import jdbreport.grid.undo.ResizingRowUndoItem;
import jdbreport.model.RowsGroup;
import jdbreport.model.TableRow;
import jdbreport.model.TableRowModel;
import jdbreport.model.event.TableRowModelEvent;
import jdbreport.model.event.TableRowModelListener;

/**
 * @version 3.0 13.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class RowHeader extends JComponent implements TableRowModelListener,
		Accessible {
	private static final long serialVersionUID = 3348685427402534902L;

	private static final String uiClassID = "TableRowHeaderUI"; //$NON-NLS-1$

	public static final int LEVEL_WIDTH = 12;

	private static ImageIcon SHOW_GROUP_IMAGE;

	private static ImageIcon HIDE_GROUP_IMAGE;

	static {
		UIManager.put(uiClassID, "jdbreport.grid.BasicTableRowHeaderUI"); //$NON-NLS-1$
	}

	//
	// Instance Variables
	//
	protected JReportGrid table;

	protected TableRowModel rowModel;

	/** The index of the row being resized. <code>null</code> if not resizing. */
	transient protected TableRow resizingRow;

	/** The index of the row being dragged. <code>null</code> if not dragging. */
	transient protected TableRow draggedRow;

	/** The distance from its original position the row has been dragged. */
	transient protected int draggedDistance;

	/**
	 * The default renderer to be used when a <code>ReportRow</code> does not
	 * define a <code>headerRenderer</code>.
	 */
	private TableCellRenderer defaultRenderer;

	private boolean invalidWidth = true;

	private ResizingRowUndoItem undoItem;

	private Dimension oldDimension;

	//
	// Constructors
	//

	/**
	 * Constructs a <code>RowHeader</code> which is initialized with
	 * <code>cm</code> as the row model. If <code>cm</code> is
	 * <code>null</code> this method will initialize the table header with a
	 * default <code>RowModel</code>.
	 * 
	 */
	public RowHeader(JReportGrid table) {
		super();
		setFocusable(false);
		initializeLocalVars();
		this.table = table;
		if (table != null)
			setRowModel(table.getTableRowModel());
		updateUI();
	}

	public ImageIcon getShowGroupImage() {
		if (SHOW_GROUP_IMAGE == null) {
			SHOW_GROUP_IMAGE = new ImageIcon(getClass()
					.getResource(
							ReportResources.getInstance().getString(
									"group_show_image"))); //$NON-NLS-1$
		}
		return SHOW_GROUP_IMAGE;
	}

	public ImageIcon getHideGroupImage() {
		if (HIDE_GROUP_IMAGE == null) {
			HIDE_GROUP_IMAGE = new ImageIcon(getClass()
					.getResource(
							ReportResources.getInstance().getString(
									"group_hide_image"))); //$NON-NLS-1$
		}
		return HIDE_GROUP_IMAGE;
	}

	//
	// Local behavior attributes
	//

	/**
	 * Sets the table associated with this header.
	 * 
	 * @param table
	 *            the new table
	 */
	public void setTable(JReportGrid table) {
		JTable old = this.table;
		if (old != table) {
			this.table = table;
			firePropertyChange("table", old, table); //$NON-NLS-1$
		}
	}

	/**
	 * Returns the table associated with this header.
	 * 
	 * @return the <code>table</code> property
	 */
	public JTable getTable() {
		return table;
	}

	/**
	 * Sets whether the user can drag row headers to reorder rows.
	 * 
	 * @param reorderingAllowed
	 *            true if the table view should allow reordering; otherwise
	 *            false
	 */
	public void setReorderingAllowed(boolean reorderingAllowed) {
		table.getReportModel().setRowMoving(reorderingAllowed);
	}

	/**
	 * Returns true if the user is allowed to rearrange rows by dragging their
	 * headers, false otherwise. The default is true. You can rearrange rows
	 * programmatically regardless of this setting.
	 * 
	 * @return the <code>reorderingAllowed</code> property
	 * @see #setReorderingAllowed
	 */
	public boolean getReorderingAllowed() {
		return table.getReportModel().isRowMoving();
	}

	/**
	 * Sets whether the user can resize rows by dragging between headers.
	 * 
	 * @param resizingAllowed
	 *            true if table view should allow resizing
	 */
	public void setResizingAllowed(boolean resizingAllowed) {
		table.getReportModel().setRowSizing(resizingAllowed);
	}

	/**
	 * Returns true if the user is allowed to resize rows by dragging between
	 * their headers, false otherwise. The default is true. You can resize rows
	 * programmatically regardless of this setting.
	 * 
	 * @return the <code>resizingAllowed</code> property
	 * @see #setResizingAllowed
	 */
	public boolean getResizingAllowed() {
		return table.getReportModel().isRowSizing();
	}

	/**
	 * Returns the the dragged row, if and only if, a drag is in process,
	 * otherwise returns <code>null</code>.
	 * 
	 * @return the dragged row, if a drag is in process, otherwise returns
	 *         <code>null</code>
	 * @see #getDraggedDistance
	 */
	public TableRow getDraggedRow() {
		return draggedRow;
	}

	/**
	 * Returns the row's vertical distance from its original position, if and
	 * only if, a drag is in process. Otherwise, the the return value is
	 * meaningless.
	 * 
	 * @return the row's vertical distance from its original position, if a drag
	 *         is in process, otherwise the return value is meaningless
	 * @see #getDraggedRow
	 */
	public int getDraggedDistance() {
		return draggedDistance;
	}

	/**
	 * Returns the resizing row. If no row is being resized this method returns
	 * <code>null</code>.
	 * 
	 * @return the resizing row, if a resize is in process, otherwise returns
	 *         <code>null</code>
	 */
	public TableRow getResizingRow() {
		return resizingRow;
	}

	/**
	 * Sets the default renderer to be used when no <code>headerRenderer</code>
	 * is defined by a <code>TableRow</code>.
	 * 
	 * @param defaultRenderer
	 *            the default renderer
	 */
	public void setDefaultRenderer(TableCellRenderer defaultRenderer) {
		this.defaultRenderer = defaultRenderer;
	}

	/**
	 * Returns the default renderer used when no <code>headerRenderer</code>
	 * is defined by a <code>TableRow</code>.
	 * 
	 * @return the default renderer
	 */
	public TableCellRenderer getDefaultRenderer() {
		return defaultRenderer;
	}

	/**
	 * Returns the index of the row that <code>point</code> lies in, or -1 if
	 * it lies out of bounds.
	 * 
	 * @return the index of the row that <code>point</code> lies in, or -1 if
	 *         it lies out of bounds
	 */
	public int rowAtPoint(Point point) {
		return getRowModel().getRowIndexAtY(point.y);
	}

	public int getWidth() {
		if (!invalidWidth) {
			return super.getWidth();
		}
		Object s;
		int result = super.getWidth();
		int oldWidth = result;
		FontRenderContext frc = ((Graphics2D) getGraphics())
				.getFontRenderContext();
		Iterator<RowsGroup> it = getRowModel().getRootGroup()
				.getAllGroupIterator();
		int i = 0;
		while (it.hasNext()) {
			RowsGroup group = it.next();

			for (int n = 0; n < group.getChildCount(); n++) {
				s = getRowModel().getHeaderValue(i++);
				if (s != null) {
					int levelWidth = (group.getLevel() + 1) * LEVEL_WIDTH;
					TextLayout layout = new TextLayout(s.toString(), getFont(),
							frc);
					int w = (int) layout.getBounds().getWidth();
					result = Math.max(w + levelWidth, result);
				}
			}
		}

		invalidWidth = false;
		if (result > oldWidth) {
			setSize(result, getHeight());
			setPreferredSize(new Dimension(result, getHeight()));
		}
		return result;
	}

	/**
	 * Returns the rectangle containing the header tile at <code>row</code>.
	 * When the <code>row</code> parameter is out of bounds this method uses
	 * the same conventions as the <code>JTable</code> method
	 * <code>getCellRect</code>.
	 * 
	 * @return the rectangle containing the header tile at <code>row</code>
	 */
	public Rectangle getHeaderRect(int row) {
		Rectangle r = new Rectangle();
		TableRowModel rm = getRowModel();

		r.width = getWidth();

		if (row < 0) {
			// y = height = 0;
		} else if (row >= rm.getRowCount()) {
			r.y = getHeight();
		} else {
			for (int i = 0; i < row; i++) {
				r.y += rm.getRow(i).getHeight();
			}

			r.height = rm.getRow(row).getHeight();
		}
		return r;
	}

	/**
	 * Allows the renderer's tips to be used if there is text set.
	 * 
	 * @param event
	 *            the location of the event identifies the proper renderer and,
	 *            therefore, the proper tip
	 * @return the tool tip for this component
	 */
	public String getToolTipText(MouseEvent event) {
		String tip = null;
		Point p = event.getPoint();
		int row;

		// Locate the renderer under the event location
		if ((row = rowAtPoint(p)) != -1) {
			TableRow aRow = rowModel.getRow(row);
			TableCellRenderer renderer = aRow.getHeaderRenderer();
			String groupHeader = "" + (row + 1); //$NON-NLS-1$
			if (renderer == null) {
				renderer = defaultRenderer;
			}
			RowsGroup group = rowModel.getGroup(row);
			if (group != null) {
				groupHeader += " " + group.getHeaderValue(); //$NON-NLS-1$
			}
			Component component = renderer.getTableCellRendererComponent(
					getTable(), groupHeader, false, false, row, -1);

			// Now have to see if the component is a JComponent before
			// getting the tip
			if (component instanceof JComponent) {
				// Convert the event to the renderer's coordinate system
				MouseEvent newEvent;
				Rectangle cellRect = getHeaderRect(row);

				p.translate(-cellRect.x, -cellRect.y);
				newEvent = new MouseEvent(component, event.getID(), event
						.getWhen(), event.getModifiers(), p.x, p.y, event
						.getClickCount(), event.isPopupTrigger());

				tip = ((JComponent) component).getToolTipText(newEvent);
			}
		}

		// No tip from the renderer get our own tip
		if (tip == null)
			tip = getToolTipText();

		return tip;
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

	//
	// Managing TableHeaderUI
	//

	/**
	 * Returns the look and feel (L&F) object that renders this component.
	 * 
	 * @return the <code>TableRowHeaderUI</code> object that renders this
	 *         component
	 */
	public TableRowHeaderUI getUI() {
		return (TableRowHeaderUI) ui;
	}

	/**
	 * Sets the look and feel (L&F) object that renders this component.
	 * 
	 * @param ui
	 *            the <code>TableHeaderUI</code> L&F object
	 * @see UIDefaults#getUI
	 */
	public void setUI(TableRowHeaderUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	/**
	 * Notification from the <code>UIManager</code> that the look and feel
	 * (L&F) has changed. Replaces the current UI object with the latest version
	 * from the <code>UIManager</code>.
	 * 
	 * @see JComponent#updateUI
	 */
	public void updateUI() {
		setUI((TableRowHeaderUI) UIManager.getUI(this));
		resizeAndRepaint();
		invalidate();// PENDING
	}

	/**
	 * Returns the suffix used to construct the name of the look and feel (L&F)
	 * class used to render this component.
	 * 
	 * @return the string "TableRowHeaderUI"
	 * 
	 */
	public String getUIClassID() {
		return uiClassID;
	}

	//
	// Managing models
	//

	/**
	 * Sets the row model for this table to <code>newModel</code> and
	 * registers for listener notifications from the new row model.
	 * 
	 * @param rowModel
	 *            the new data source for this table
	 * @exception IllegalArgumentException
	 *                if <code>newModel</code> is <code>null</code>
	 * @see #getRowModel
	 */
	public void setRowModel(TableRowModel rowModel) {
		if (rowModel == null) {
			throw new IllegalArgumentException(Messages
					.getString("RowHeader.8")); //$NON-NLS-1$
		}
		TableRowModel old = this.rowModel;
		if (rowModel != old) {
			invalidWidth = true;
			if (old != null) {
				old.removeRowModelListener(this);
			}
			this.rowModel = rowModel;
			rowModel.addRowModelListener(this);

			firePropertyChange("rowModel", old, rowModel); //$NON-NLS-1$
			resizeAndRepaint();
		}
	}

	/**
	 * Returns the <code>TableRowModel</code> that contains all row
	 * information of this table RowHeader.
	 * 
	 * @return the <code>rowModel</code> property
	 */
	public TableRowModel getRowModel() {
		return rowModel;
	}

	//
	// Implementing TableRowModelListener interface
	//

	/**
	 * Invoked when a row is added to the table row model.
	 * <p>
	 * Application code will not use these methods explicitly, they are used
	 * internally by <code>JReportGrid</code>.
	 * 
	 * @param e
	 *            the event received
	 * @see TableRowModelListener
	 */
	public void rowAdded(TableRowModelEvent e) {
		invalidWidth = true;
		resizeAndRepaint();
	}

	public void rowUpdated(TableRowModelEvent e) {
		invalidWidth = true;
		resizeAndRepaint();
	}

	/**
	 * Invoked when a row is removed from the table row model.
	 * <p>
	 * Application code will not use these methods explicitly, they are used
	 * internally by <code>JReportGrid</code>.
	 * 
	 * @param e
	 *            the event received
	 * @see TableRowModelListener
	 */
	public void rowRemoved(TableRowModelEvent e) {
		resizeAndRepaint();
	}

	/**
	 * Invoked when a row is repositioned.
	 * <p>
	 * Application code will not use these methods explicitly, they are used
	 * internally by <code>JReportGrid</code>.
	 * 
	 * @param e
	 *            the event received
	 * @see TableRowModelListener
	 */
	public void rowMoved(TableRowModelEvent e) {
		repaint();
	}

	/**
	 * Invoked when a row is resized.
	 * <p>
	 * Application code will not use these methods explicitly, they are used
	 * internally by <code>JReportGrid</code>.
	 * 
	 * @param e
	 *            the event received
	 * @see TableRowModelListener
	 */
	public void rowResized(TableRowModelEvent e) {
		repaint();
	}

	/**
	 * Invoked when a row is moved due to a margin change.
	 * <p>
	 * Application code will not use these methods explicitly, they are used
	 * internally by <code>JReportGrid</code>.
	 * 
	 * @param e
	 *            the event received
	 * @see TableRowModelListener
	 */
	public void rowMarginChanged(ChangeEvent e) {
		resizeAndRepaint();
	}

	public void rowSelectionChanged(ListSelectionEvent e) {
	}

	//
	// Package Methods
	//

	/**
	 * Returns a default renderer to be used when no header renderer is defined
	 * by a <code>TableRow</code>.
	 * 
	 * @return the default table cell renderer
	 */
	protected TableCellRenderer createDefaultRenderer() {
		DefaultTableCellRenderer label = new UIResourceTableCellRenderer();

		label.setVerticalAlignment(JLabel.CENTER);
		label.setHorizontalAlignment(JLabel.LEFT);
		return label;
	}

	private class UIResourceTableCellRenderer extends DefaultTableCellRenderer
			implements UIResource {
		private static final long serialVersionUID = 4333976038294374872L;

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (table != null) {
				RowHeader header = ((JReportGrid) table).getRowHeader();
				if (header != null) {
					setForeground(header.getForeground());
					setBackground(header.getBackground());
					setFont(header.getFont());
					setPreferredSize(new Dimension(header.getWidth(), 0));
				}
			}

			setText((value == null) ? "" + (row + 1) : value.toString()); //$NON-NLS-1$
			setBorder(BorderUIResource.getEtchedBorderUIResource());
			return this;
		}
	}

	/**
	 * Initializes the local variables and properties with default values. Used
	 * by the constructor methods.
	 */
	protected void initializeLocalVars() {
		setOpaque(true);
		table = null;
		draggedRow = null;
		draggedDistance = 0;
		resizingRow = null;
		setForeground(UIManager.getColor("TableHeader.foreground")); //$NON-NLS-1$
		setBackground(UIManager.getColor("TableHeader.background")); //$NON-NLS-1$
		// I'm registered to do tool tips so we can draw tips for the
		// renderers
		ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
		toolTipManager.registerComponent(this);
		setDefaultRenderer(createDefaultRenderer());
	}

	/**
	 * Sizes the header and marks it as needing display. Equivalent to
	 * <code>revalidate</code> followed by <code>repaint</code>.
	 */
	public void resizeAndRepaint() {
		revalidate();
		repaint();
	}

	/**
	 * Sets the header's <code>draggedRow</code> to <code>aRow</code>.
	 * <p>
	 * Application code will not use this method explicitly, it is used
	 * internally by the row dragging mechanism.
	 * 
	 * @param aRow
	 *            the row being dragged, or <code>null</code> if no row is
	 *            being dragged
	 */
	public void setDraggedRow(TableRow aRow) {
		draggedRow = aRow;
	}

	/**
	 * Sets the header's <code>draggedDistance</code> to <code>distance</code>.
	 * 
	 * @param distance
	 *            the distance dragged
	 */
	public void setDraggedDistance(int distance) {
		draggedDistance = distance;
	}

	/**
	 * Sets the header's <code>resizingRow</code> to <code>aRow</code>.
	 * <p>
	 * Application code will not use this method explicitly, it is used
	 * internally by the row sizing mechanism.
	 * 
	 * @param aRow
	 *            the row being resized, or <code>null</code> if no row is
	 *            being resized
	 */
	public void setResizingRow(TableRow aRow) {
		if (aRow != null && resizingRow == null) {
			int[] rows = { getRowModel().getRowIndex(aRow) };
			int[] heights = { aRow.getHeight() };
			undoItem = new ResizingRowUndoItem(table, rows,
					heights);
		} else {
			if (aRow == null && resizingRow != null && undoItem != null) {
				if (undoItem.getHeights()[0] != resizingRow.getHeight()) {
					table.pushUndo(undoItem);
				}
				undoItem = null;
			}
		}
		resizingRow = aRow;
	}

	/**
	 * See <code>readObject</code> and <code>writeObject</code> in
	 * <code>JComponent</code> for more information about serialization in
	 * Swing.
	 */
	private void writeObject(ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();
		if ((ui != null) && (getUIClassID().equals(uiClassID))) {
			ui.installUI(this);
		}
	}

	/**
	 * Returns a string representation of this <code>RowHeader</code>. This
	 * method is intended to be used only for debugging purposes, and the
	 * content and format of the returned string may vary between
	 * implementations. The returned string may be empty but may not be
	 * <code>null</code>.
	 * <P>
	 * Overriding <code>paramString</code> to provide information about the
	 * specific new aspects of the JFC components.
	 * 
	 * @return a string representation of this <code>RowHeader</code>
	 */
	protected String paramString() {
		String reorderingAllowedString = (getReorderingAllowed() ? "true" //$NON-NLS-1$
				: "false"); //$NON-NLS-1$
		String resizingAllowedString = (getResizingAllowed() ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$

		return super.paramString() + ",draggedDistance=" + draggedDistance //$NON-NLS-1$
				+ ",reorderingAllowed=" + reorderingAllowedString //$NON-NLS-1$
				+ ",resizingAllowed=" + resizingAllowedString; //$NON-NLS-1$
	}

	// ///////////////
	// Accessibility support
	// //////////////

	/**
	 * Gets the AccessibleContext associated with this RowHeader. For
	 * RowHeaders, the AccessibleContext takes the form of an
	 * AccessibleRowHeader. A new AccessibleRowHeader instance is created if
	 * necessary.
	 * 
	 * @return an AccessibleRowHeader that serves as the AccessibleContext of
	 *         this RowHeader
	 */
	public AccessibleContext getAccessibleContext() {
		if (accessibleContext == null) {
			accessibleContext = new AccessibleRowHeader();
		}
		return accessibleContext;
	}

	//
	// *** should also implement AccessibleSelection?
	// *** and what's up with keyboard navigation/manipulation?
	//
	/**
	 * This class implements accessibility support for the
	 * <code>RowHeader</code> class. It provides an implementation of the Java
	 * Accessibility API appropriate to table header user-interface elements.
	 * <p>
	 * <strong>Warning: </strong> Serialized objects of this class will not be
	 * compatible with future Swing releases. The current serialization support
	 * is appropriate for short term storage or RMI between applications running
	 * the same version of Swing. As of 1.4, support for long term storage of
	 * all JavaBeans <sup><font size="-2">TM </font> </sup> has been added to
	 * the <code>java.beans</code> package. Please see
	 * {@link java.beans.XMLEncoder}.
	 */
	protected class AccessibleRowHeader extends AccessibleJComponent {

		private static final long serialVersionUID = 6360281910205713111L;

		/**
		 * Get the role of this object.
		 * 
		 * @return an instance of AccessibleRole describing the role of the
		 *         object
		 * @see AccessibleRole
		 */
		public AccessibleRole getAccessibleRole() {
			return AccessibleRole.PANEL;
		}

		/**
		 * Returns the Accessible child, if one exists, contained at the local
		 * coordinate Point.
		 * 
		 * @param p
		 *            The point defining the top-left corner of the Accessible,
		 *            given in the coordinate space of the object's parent.
		 * @return the Accessible, if it exists, at the specified location; else
		 *         null
		 */
		public Accessible getAccessibleAt(Point p) {
			int row;

			// Locate the renderer under the Point
			if ((row = RowHeader.this.rowAtPoint(p)) != -1) {
				TableRow aRow = RowHeader.this.rowModel.getRow(row);
				TableCellRenderer renderer = aRow.getHeaderRenderer();
				if (renderer == null && defaultRenderer == null) {
					return null;
				}
				return new AccessibleRowHeaderEntry(row, RowHeader.this,
						RowHeader.this.table);
			} else {
				return null;
			}
		}

		/**
		 * Returns the number of accessible children in the object. If all of
		 * the children of this object implement Accessible, than this method
		 * should return the number of children of this object.
		 * 
		 * @return the number of accessible children in the object.
		 */
		public int getAccessibleChildrenCount() {
			return RowHeader.this.rowModel.getRowCount();
		}

		/**
		 * Return the nth Accessible child of the object.
		 * 
		 * @param i
		 *            zero-based index of child
		 * @return the nth Accessible child of the object
		 */
		public Accessible getAccessibleChild(int i) {
			if (i < 0 || i >= getAccessibleChildrenCount()) {
				return null;
			} else {
				TableRow aRow = RowHeader.this.rowModel.getRow(i);
				TableCellRenderer renderer = aRow.getHeaderRenderer();
				if (renderer == null && defaultRenderer == null) {
					return null;
				}
				return new AccessibleRowHeaderEntry(i, RowHeader.this,
						RowHeader.this.table);
			}
		}

		/**
		 * This class provides an implementation of the Java Accessibility API
		 * appropropriate for JTableHeader entries.
		 */
		protected class AccessibleRowHeaderEntry extends AccessibleContext
				implements Accessible, AccessibleComponent {

			private RowHeader parent;

			private int row;

			private JReportGrid table;

			/**
			 * Constructs an AccessiblJTableHeaaderEntry
			 */
			public AccessibleRowHeaderEntry(int r, RowHeader p, JReportGrid t) {
				parent = p;
				row = r;
				table = t;
				this.setAccessibleParent(parent);
			}

			/**
			 * Get the AccessibleContext associated with this object. In the
			 * implementation of the Java Accessibility API for this class,
			 * returns this object, which serves as its own AccessibleContext.
			 * 
			 * @return this object
			 */
			public AccessibleContext getAccessibleContext() {
				return this;
			}

			private AccessibleContext getCurrentAccessibleContext() {
				TableRowModel trm = table.getTableRowModel();
				if (trm != null) {
					if (row < 0 || row >= trm.getRowCount()) {
						return null;
					}
					TableRow aRow = trm.getRow(row);
					String headerValue = "" + row; //$NON-NLS-1$
					RowsGroup group = trm.getGroup(row);
					if (group != null) {
						headerValue += " " + group.getHeaderValue(); //$NON-NLS-1$
					}
					TableCellRenderer renderer = aRow.getHeaderRenderer();
					if (renderer == null) {
						if (defaultRenderer != null) {
							renderer = defaultRenderer;
						} else {
							return null;
						}
					}
					Component c = renderer.getTableCellRendererComponent(
							RowHeader.this.getTable(), headerValue, false,
							false, -1, row);
					if (c instanceof Accessible) {
						return c.getAccessibleContext();
					}
				}
				return null;
			}

			private Component getCurrentComponent() {
				TableRowModel trm = table.getTableRowModel();
				if (trm != null) {
					if (row < 0 || row >= trm.getRowCount()) {
						return null;
					}
					TableRow aRow = trm.getRow(row);
					TableCellRenderer renderer = aRow.getHeaderRenderer();
					if (renderer == null) {
						if (defaultRenderer != null) {
							renderer = defaultRenderer;
						} else {
							return null;
						}
					}
					String headerValue = "" + row; //$NON-NLS-1$
					RowsGroup group = trm.getGroup(row);
					if (group != null) {
						headerValue += " " + group.getHeaderValue(); //$NON-NLS-1$
					}
					return renderer.getTableCellRendererComponent(
							RowHeader.this.getTable(), headerValue, false,
							false, -1, row);
				} else {
					return null;
				}
			}

			// AccessibleContext methods

			public String getAccessibleName() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					String name = ac.getAccessibleName();
					if (name != null && !name.isEmpty()) {
						return ac.getAccessibleName();
					}
				}
				if (accessibleName != null && !accessibleName.isEmpty()) {
					return accessibleName;
				} else {
					return table.getRowName(row);
				}
			}

			public void setAccessibleName(String s) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					ac.setAccessibleName(s);
				} else {
					super.setAccessibleName(s);
				}
			}

			//
			// *** should check toolTip text for desc. (needs MouseEvent)
			//
			public String getAccessibleDescription() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					return ac.getAccessibleDescription();
				} else {
					return super.getAccessibleDescription();
				}
			}

			public void setAccessibleDescription(String s) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					ac.setAccessibleDescription(s);
				} else {
					super.setAccessibleDescription(s);
				}
			}

			public AccessibleRole getAccessibleRole() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					return ac.getAccessibleRole();
				} else {
					return AccessibleRole.ROW_HEADER;
				}
			}

			public AccessibleStateSet getAccessibleStateSet() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					AccessibleStateSet states = ac.getAccessibleStateSet();
					if (isShowing()) {
						states.add(AccessibleState.SHOWING);
					}
					return states;
				} else {
					return new AccessibleStateSet(); // must be non null?
				}
			}

			public int getAccessibleIndexInParent() {
				return row;
			}

			public int getAccessibleChildrenCount() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					return ac.getAccessibleChildrenCount();
				} else {
					return 0;
				}
			}

			public Accessible getAccessibleChild(int i) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					Accessible accessibleChild = ac.getAccessibleChild(i);
					ac.setAccessibleParent(this);
					return accessibleChild;
				} else {
					return null;
				}
			}

			public Locale getLocale() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					return ac.getLocale();
				} else {
					return null;
				}
			}

			public void addPropertyChangeListener(PropertyChangeListener l) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					ac.addPropertyChangeListener(l);
				} else {
					super.addPropertyChangeListener(l);
				}
			}

			public void removePropertyChangeListener(PropertyChangeListener l) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac != null) {
					ac.removePropertyChangeListener(l);
				} else {
					super.removePropertyChangeListener(l);
				}
			}

			public AccessibleAction getAccessibleAction() {
				return getCurrentAccessibleContext().getAccessibleAction();
			}

			/**
			 * Get the AccessibleComponent associated with this object. In the
			 * implementation of the Java Accessibility API for this class,
			 * return this object, which is responsible for implementing the
			 * AccessibleComponent interface on behalf of itself.
			 * 
			 * @return this object
			 */
			public AccessibleComponent getAccessibleComponent() {
				return this; // to override getBounds()
			}

			public AccessibleSelection getAccessibleSelection() {
				return getCurrentAccessibleContext().getAccessibleSelection();
			}

			public AccessibleText getAccessibleText() {
				return getCurrentAccessibleContext().getAccessibleText();
			}

			public AccessibleValue getAccessibleValue() {
				return getCurrentAccessibleContext().getAccessibleValue();
			}

			// AccessibleComponent methods

			public Color getBackground() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					return ((AccessibleComponent) ac).getBackground();
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						return c.getBackground();
					} else {
						return null;
					}
				}
			}

			public void setBackground(Color c) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).setBackground(c);
				} else {
					Component cp = getCurrentComponent();
					if (cp != null) {
						cp.setBackground(c);
					}
				}
			}

			public Color getForeground() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					return ((AccessibleComponent) ac).getForeground();
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						return c.getForeground();
					} else {
						return null;
					}
				}
			}

			public void setForeground(Color c) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).setForeground(c);
				} else {
					Component cp = getCurrentComponent();
					if (cp != null) {
						cp.setForeground(c);
					}
				}
			}

			public Cursor getCursor() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					return ((AccessibleComponent) ac).getCursor();
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						return c.getCursor();
					} else {
						Accessible ap = getAccessibleParent();
						if (ap instanceof AccessibleComponent) {
							return ((AccessibleComponent) ap).getCursor();
						} else {
							return null;
						}
					}
				}
			}

			public void setCursor(Cursor c) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).setCursor(c);
				} else {
					Component cp = getCurrentComponent();
					if (cp != null) {
						cp.setCursor(c);
					}
				}
			}

			public Font getFont() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					return ((AccessibleComponent) ac).getFont();
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						return c.getFont();
					} else {
						return null;
					}
				}
			}

			public void setFont(Font f) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).setFont(f);
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						c.setFont(f);
					}
				}
			}

			public FontMetrics getFontMetrics(Font f) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					return ((AccessibleComponent) ac).getFontMetrics(f);
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						return c.getFontMetrics(f);
					} else {
						return null;
					}
				}
			}

			public boolean isEnabled() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					return ((AccessibleComponent) ac).isEnabled();
				} else {
					Component c = getCurrentComponent();
					return c != null && c.isEnabled();
				}
			}

			public void setEnabled(boolean b) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).setEnabled(b);
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						c.setEnabled(b);
					}
				}
			}

			public boolean isVisible() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					return ((AccessibleComponent) ac).isVisible();
				} else {
					Component c = getCurrentComponent();
					return c != null && c.isVisible();
				}
			}

			public void setVisible(boolean b) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).setVisible(b);
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						c.setVisible(b);
					}
				}
			}

			public boolean isShowing() {
				return isVisible() && RowHeader.this.isShowing();
			}

			public boolean contains(Point p) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					Rectangle r = ((AccessibleComponent) ac).getBounds();
					return r.contains(p);
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						Rectangle r = c.getBounds();
						return r.contains(p);
					} else {
						return getBounds().contains(p);
					}
				}
			}

			public Point getLocationOnScreen() {
				if (parent != null) {
					Point parentLocation = parent.getLocationOnScreen();
					Point componentLocation = getLocation();
					componentLocation.translate(parentLocation.x,
							parentLocation.y);
					return componentLocation;
				} else {
					return null;
				}
			}

			public Point getLocation() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					Rectangle r = ((AccessibleComponent) ac).getBounds();
					return r.getLocation();
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						Rectangle r = c.getBounds();
						return r.getLocation();
					} else {
						return getBounds().getLocation();
					}
				}
			}

			public void setLocation(Point p) {
			}

			public Rectangle getBounds() {
				Rectangle r = table.getCellRect(-1, row, false);
				r.y = 0;
				return r;
			}

			public void setBounds(Rectangle r) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).setBounds(r);
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						c.setBounds(r);
					}
				}
			}

			public Dimension getSize() {
				return getBounds().getSize();
			}

			public void setSize(Dimension d) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).setSize(d);
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						c.setSize(d);
					}
				}
			}

			public Accessible getAccessibleAt(Point p) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					return ((AccessibleComponent) ac).getAccessibleAt(p);
				} else {
					return null;
				}
			}

			public boolean isFocusTraversable() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					return ((AccessibleComponent) ac).isFocusTraversable();
				} else {
					Component c = getCurrentComponent();
					return c != null && c.isFocusable();
				}
			}

			public void requestFocus() {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).requestFocus();
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						c.requestFocus();
					}
				}
			}

			public void addFocusListener(FocusListener l) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).addFocusListener(l);
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						c.addFocusListener(l);
					}
				}
			}

			public void removeFocusListener(FocusListener l) {
				AccessibleContext ac = getCurrentAccessibleContext();
				if (ac instanceof AccessibleComponent) {
					((AccessibleComponent) ac).removeFocusListener(l);
				} else {
					Component c = getCurrentComponent();
					if (c != null) {
						c.removeFocusListener(l);
					}
				}
			}

		} // inner class AccessibleRowHeaderElement

	} // inner class AccessibleRowHeader

} // End of Class RowHeader

