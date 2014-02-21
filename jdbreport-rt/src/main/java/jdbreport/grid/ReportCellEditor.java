/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2008 Andrey Kholmanskih. All rights reserved.
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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Keymap;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

import jdbreport.model.Cell;
import jdbreport.model.CellStyle;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class ReportCellEditor extends AbstractCellEditor implements
		TableCellEditor {

	private static final long serialVersionUID = -221787950269571884L;

	protected JTextPane editorComponent;

	private int clickCountToStart = 2;

	private EditorDelegate delegate;

	private boolean notWrapLine;

	public ReportCellEditor(String contentType) {
		editorComponent = new JTextPane();
		editorComponent.setContentType(contentType);
		if (contentType.equals(Cell.TEXT_HTML)) {
			editorComponent.setEditorKit(new NoWrapHTMLKit());
		} else {
			editorComponent.setEditorKit(new NoWrapEditorKit());
		}
		initKeyMap();
		delegate = new EditorDelegate() {
			
			private static final long serialVersionUID = 1L;
			
			public void setValue(Object value) {
				editorComponent
						.setText((value != null) ? value.toString() : ""); //$NON-NLS-1$
			}

			public Object getCellEditorValue() {
				return editorComponent.getText();
			}
		};
		editorComponent.addKeyListener(delegate);

	}

	public Component getComponent() {
		return editorComponent;
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		JReportGrid grid = (JReportGrid) table;
		Cell cell = grid.getReportModel().getReportCell(row, column);
		CellStyle style = grid.getReportModel().getStyles(cell.getStyleId());
		setWordWrap(style.isWrapLine());
		String s = value != null ? value.toString() : ""; //$NON-NLS-1$
		editorComponent.setBorder(new LineBorder(Color.black));
		editorComponent.setAutoscrolls(!notWrapLine);
		editorComponent.setText(s);
		editorComponent.getStyledDocument().setParagraphAttributes(0,
				s.length(), style.getAttributeSet(), true);
		editorComponent.select(0, s.length());

		return editorComponent;
	}

	public void setWordWrap(boolean wrap) {

		if (editorComponent.getEditorKit() instanceof NoWrapEditorKit) {

			NoWrapEditorKit kit = (NoWrapEditorKit) editorComponent
					.getEditorKit();

			kit.setWrap(wrap);

		}
	}

	public Object getCellEditorValue() {
		return editorComponent.getText();
	}

	public boolean isCellEditable(EventObject anEvent) {
		if (anEvent instanceof MouseEvent) {
			return ((MouseEvent) anEvent).getClickCount() >= clickCountToStart;
		}
		return true;
	}

	protected void insertLineBreak() {
		try {
			int offs = editorComponent.getCaretPosition();
			Document doc = editorComponent.getDocument();
			SimpleAttributeSet attrs;
			if (doc instanceof StyledDocument) {
				attrs = new SimpleAttributeSet(((StyledDocument) doc)
						.getCharacterElement(offs).getAttributes());
			} else {
				attrs = new SimpleAttributeSet();
			}
			doc.insertString(offs, "\n", attrs);
			editorComponent.setCaretPosition(offs + 1);
		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}
	}

	protected void initKeyMap() {
		Keymap kMap = editorComponent.getKeymap();
		Action a = new AbstractAction() {
			
			private static final long serialVersionUID = 1L;
			
			public void actionPerformed(ActionEvent e) {
				insertLineBreak();
			}
		};
		kMap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
				KeyEvent.SHIFT_MASK), a);
	}

	protected class EditorDelegate implements KeyListener, ItemListener,
			Serializable {

		private static final long serialVersionUID = 1L;
		
		/** The value of this cell. */
		protected Object value;

		/**
		 * Returns the value of this cell.
		 * 
		 * @return the value of this cell
		 */
		public Object getCellEditorValue() {
			return value;
		}

		/**
		 * Sets the value of this cell.
		 * 
		 * @param value
		 *            the new value of this cell
		 */
		public void setValue(Object value) {
			this.value = value;
		}

		/**
		 * Returns true if <code>anEvent</code> is <b>not</b> a
		 * <code>MouseEvent</code>. Otherwise, it returns true if the
		 * necessary number of clicks have occurred, and returns false
		 * otherwise.
		 * 
		 * @param anEvent
		 *            the event
		 * @return true if cell is ready for editing, false otherwise
		 */
		public boolean isCellEditable(EventObject anEvent) {
			if (anEvent instanceof MouseEvent) {
				return ((MouseEvent) anEvent).getClickCount() >= clickCountToStart;
			}
			return true;
		}

		/**
		 * Returns true to indicate that the editing cell may be selected.
		 * 
		 * @param anEvent
		 *            the event
		 * @return true
		 * @see #isCellEditable
		 */
		public boolean shouldSelectCell(EventObject anEvent) {
			return true;
		}

		/**
		 * Returns true to indicate that editing has begun.
		 * 
		 * @param anEvent
		 *            the event
		 */
		public boolean startCellEditing(EventObject anEvent) {
			return true;
		}

		/**
		 * Stops editing and returns true to indicate that editing has stopped.
		 * This method calls <code>fireEditingStopped</code>.
		 * 
		 * @return true
		 */
		public boolean stopCellEditing() {
			fireEditingStopped();
			return true;
		}

		/**
		 * Cancels editing. This method calls <code>fireEditingCanceled</code>.
		 */
		public void cancelCellEditing() {
			fireEditingCanceled();
		}

		/**
		 * When an action is performed, editing is ended.
		 * 
		 * @param e
		 *            the action event
		 * @see #stopCellEditing
		 */
		public void actionPerformed(ActionEvent e) {
			ReportCellEditor.this.stopCellEditing();
		}

		/**
		 * When an item's state changes, editing is ended.
		 * 
		 * @param e
		 *            the action event
		 * @see #stopCellEditing
		 */
		public void itemStateChanged(ItemEvent e) {
			ReportCellEditor.this.stopCellEditing();
		}

		public void keyTyped(KeyEvent e) {

		}

		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_TAB
					|| (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()))
				ReportCellEditor.this.stopCellEditing();

		}

		public void keyReleased(KeyEvent e) {

		}
	}


}
