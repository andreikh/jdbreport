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
 * @version 3.0 13.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class ReportCellEditor extends AbstractCellEditor implements
		TableCellEditor {

	private static final long serialVersionUID = -221787950269571884L;

	protected JTextPane editorComponent;

	private final int clickCountToStart = 2;

	public ReportCellEditor(String contentType) {
		editorComponent = new JTextPane();
		editorComponent.setContentType(contentType);
		if (contentType.equals(Cell.TEXT_HTML)) {
			editorComponent.setEditorKit(new NoWrapHTMLKit());
		} else {
			editorComponent.setEditorKit(new NoWrapEditorKit());
		}
		initKeyMap();
		EditorDelegate delegate = new EditorDelegate() {

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
		String s = value != null ? value.toString() : "";
		editorComponent.setBorder(new LineBorder(Color.black));
		editorComponent.setAutoscrolls(true);
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
		return !(anEvent instanceof MouseEvent) || ((MouseEvent) anEvent).getClickCount() >= clickCountToStart;
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
		 *
		 * @return true to indicate that editing has begun
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
