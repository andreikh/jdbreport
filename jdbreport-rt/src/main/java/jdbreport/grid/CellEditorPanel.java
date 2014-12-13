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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.Window;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JScrollPane;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;

import jdbreport.model.Cell;
import jdbreport.model.CellWrap;
import jdbreport.model.math.MathML;
import jdbreport.model.math.MathValue;
import jdbreport.util.Utils;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class CellEditorPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final String BIG = "big";

	private static final String EMPTY_STRING = "";

	private static final String SUP = "sup";

	private static final String SMALL = "small";

	public final static int CANCEL = 0;

	public final static int OK = 1;

	private JEditorPane textPane = null;

	private JDialog dialog;

	private JPanel jContentPane;

	private int exitCode;

	private JPanel buttonPanel;

	private JButton cancelButton;

	private JButton okButton;

	private JToggleButton contentButton;

	private String text;

	private JToolBar toolBar;

	private Action boldAction;

	private Action italicAction;

	private Action underlineAction;

	private Action subAction;

	private Action supAction;

	private Action smallAction;

	private Action bigAction;
	
	private boolean editable = true;

	public CellEditorPanel(LayoutManager layout) {
		super(layout);
		initialize();
	}

	public CellEditorPanel() {
		super();
		initialize();
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		getTextPane().setEditable(editable);
		getOKButton().setEnabled(editable);
	}

	public void setCell(Cell cell) {
		updateContent(cell.getContentType());
		text = cell.getText();
		getTextPane().setText(text);
		getToolBar().setVisible(false);
	}

	private void updateContent(String content) {
		textPane.setContentType(content);
		if (content.equals(Cell.TEXT_HTML)) {
			if (!contentButton.isSelected())
				contentButton.setSelected(true);
			contentButton.setText(Messages.getString("CellEditorPanel.Plain")); //$NON-NLS-1$
			getToolBar().setVisible(false);
		} else {
			if (contentButton.isSelected())
				contentButton.setSelected(false);
			contentButton.setText(Messages.getString("CellEditorPanel.HTML")); //$NON-NLS-1$
			getToolBar().setVisible(true);
		}
	}

	private JToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = new JToolBar();
			toolBar.setFloatable(false);
			toolBar.add(getBoldAction());
			toolBar.add(getItalicAction());
			toolBar.add(getUnderlineAction());
			toolBar.addSeparator();
			toolBar.add(getSubAction());
			toolBar.add(getSupAction());
			toolBar.addSeparator();
			toolBar.add(getSmallAction());
			toolBar.add(getBigAction());
		}
		return toolBar;
	}

	private Action getBoldAction() {
		if (boldAction == null) {
			boldAction = new ReportAction.BasedAction("font-bold") {

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					setTag("b");
				}

			};
		}
		return boldAction;
	}

	private Action getItalicAction() {
		if (italicAction == null) {
			italicAction = new ReportAction.BasedAction("font-italic") {

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					setTag("i");
				}

			};
		}
		return italicAction;
	}

	private Action getUnderlineAction() {
		if (underlineAction == null) {
			underlineAction = new ReportAction.BasedAction("font-underline") {

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					setTag("u");
				}

			};
		}
		return underlineAction;
	}

	private Action getSubAction() {
		if (subAction == null) {
			subAction = new ReportAction.BasedAction("text-sub") {

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					if (findTag(SMALL)) {
						setTag(SMALL);
						setTag("sub");
					} else {
						setTag("sub");
						setTag(SMALL);
					}
				}

			};
		}
		return subAction;
	}

	private Action getSupAction() {
		if (supAction == null) {
			supAction = new ReportAction.BasedAction("text-sup") {

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					if (findTag(SMALL)) {
						setTag(SMALL);
						setTag(SUP);
					} else {
						setTag(SUP);
						setTag(SMALL);
					}
				}

			};
		}
		return supAction;
	}

	private Action getSmallAction() {
		if (smallAction == null) {
			smallAction = new ReportAction.BasedAction("text-small") {

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					setTag(SMALL);
				}

			};
		}
		return smallAction;
	}

	private Action getBigAction() {
		if (bigAction == null) {
			bigAction = new ReportAction.BasedAction("text-big") {

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					setTag(BIG);
				}

			};
		}
		return bigAction;
	}

	private boolean findTag(String tag) {
		String btag = "<" + tag + ">";
		String etag = "</" + tag + ">";
		int b = textPane.getSelectionStart();
		int e = textPane.getSelectionEnd();
		String text = textPane.getText();
		int bt = text.indexOf(btag, b - btag.length());
		int et = text.indexOf(etag, e);
		return (bt == b - btag.length() && et == e);
	}

	private boolean setTag(String tag) {
		String btag = "<" + tag + ">";
		String etag = "</" + tag + ">";
		String t = textPane.getSelectedText();
		if (t == null)
			t = EMPTY_STRING;
		int b = textPane.getSelectionStart();
		int e = textPane.getSelectionEnd();
		String text = textPane.getText();
		int bt = text.indexOf(btag, b - btag.length());
		int et = text.indexOf(etag, e);
		if (bt == b - btag.length() && et == e) {
			textPane.setSelectionStart(e);
			textPane.setSelectionEnd(e + etag.length());
			textPane.replaceSelection(EMPTY_STRING);
			textPane.setSelectionStart(b - btag.length());
			textPane.setSelectionEnd(b);
			textPane.replaceSelection(EMPTY_STRING);
			textPane.setSelectionStart(b - btag.length());
			textPane.setSelectionEnd(textPane.getSelectionStart() + t.length());
			return false;
		} else {
			textPane.replaceSelection(btag + t + etag);
			textPane.setSelectionStart(b + btag.length());
			textPane.setSelectionEnd(textPane.getSelectionStart() + t.length());
			return true;
		}
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(340, 260);
		this.add(getToolBar(), BorderLayout.NORTH);
		this.add(new JScrollPane(getTextPane()), java.awt.BorderLayout.CENTER);
	}

	/**
	 * This method initializes textPane
	 * 
	 * @return javax.swing.JTextPane
	 */
	private JEditorPane getTextPane() {
		if (textPane == null) {
			textPane = new JEditorPane();
			textPane.setFont(Consts.defaultFont);
			textPane.setComponentPopupMenu(createPopupMenu(textPane));
		}
		return textPane;
	}

	public int showDialog(Window owner, CellWrap cell, boolean editable) {
		if (dialog == null) {
			if (owner instanceof Frame) {
				dialog = new JDialog((Frame) owner, true);
			} else {
				dialog = new JDialog((Dialog) owner, true);
			}
			dialog.setSize(340, 260);
			dialog.setContentPane(getJContentPane());
			Utils.screenCenter(dialog, owner);
		}
		exitCode = CANCEL;
		setEditable(editable);
		setCell(cell.getCell());
		dialog
				.setTitle(String
						.format(
								Messages.getString("CellEditorPanel.title"), cell.getRow() + 1, cell.getColumn() + 1)); //$NON-NLS-1$
		getTextPane().requestFocus();
		dialog.setVisible(true);
		return exitCode;
	}

	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(this, java.awt.BorderLayout.CENTER);
			jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getOKButton());
			buttonPanel.add(getCancelButton());
			buttonPanel.add(getContentButton());
		}
		return buttonPanel;
	}

	private JToggleButton getContentButton() {
		if (contentButton == null) {
			contentButton = new JToggleButton();
			contentButton.setText(Messages.getString("CellEditorPanel.HTML")); //$NON-NLS-1$
			contentButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					String content = textPane.getContentType();
					text = textPane.getText();
					if (content.equals(Cell.TEXT_HTML)) {
						text = Utils.html2Plain(text);
						contentButton.setText(Messages
								.getString("CellEditorPanel.Plain")); //$NON-NLS-1$
						textPane.setContentType(Cell.TEXT_PLAIN);
						getToolBar().setVisible(true);
					} else {
						contentButton.setText(Messages
								.getString("CellEditorPanel.HTML")); //$NON-NLS-1$
						textPane.setContentType(Cell.TEXT_HTML);
						getToolBar().setVisible(false);
					}
					textPane.setText(text);
					textPane.setSelectionStart(0);
					textPane.setSelectionEnd(0);
				}

			});
		}
		return contentButton;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText(Messages
					.getString("CellEditorPanel.cancel_text")); //$NON-NLS-1$
			cancelButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					dialog.setVisible(false);
				}

			});
		}
		return cancelButton;
	}

	private JButton getOKButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setMnemonic(KeyEvent.VK_ENTER);
			okButton.setText(Messages.getString("CellEditorPanel.ok_text")); //$NON-NLS-1$
			okButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					exitCode = OK;
					dialog.setVisible(false);
				}

			});
		}
		return okButton;
	}

	public int getExitCode() {
		return exitCode;
	}

	public Object getValue() {
		if (MathValue.isEnableMathMl()) { 
			String s = textPane.getText().trim().toLowerCase();
			if (s.endsWith("</math>") && s.contains("<math")) {
				return new MathML(s);
			}
		}
		return textPane.getText();
	}

	public static JPopupMenu createPopupMenu(JTextComponent text) {
		JPopupMenu popupMenu = new JPopupMenu();
		Action cutAction = null;
		Action copyAction = null;
		Action pasteAction = null;
		Action selectAction = null;

		Action[] actions = text.getActions();
		for (Action action : actions) {
			if ("cut-to-clipboard".equals(action.getValue(Action.NAME))) {
				cutAction = action;
			} else if ("copy-to-clipboard".equals(action.getValue(Action.NAME))) {
				copyAction = action;
			} else if ("paste-from-clipboard".equals(action
					.getValue(Action.NAME))) {
				pasteAction = action;
			} else if ("select-all".equals(action.getValue(Action.NAME))) {
				selectAction = action;
			}
		}

		if (cutAction != null) {
			JMenuItem cutItem = popupMenu.add(cutAction);
			cutItem.setText(Messages.getString("TextField.cut"));
			cutItem.setAccelerator(KeyStroke.getKeyStroke("ctrl X"));
		}
		if (copyAction != null) {
			JMenuItem copyItem = popupMenu.add(copyAction);
			copyItem.setText(Messages.getString("TextField.copy"));
			copyItem.setAccelerator(KeyStroke.getKeyStroke("ctrl C"));
		}
		if (pasteAction != null) {
			JMenuItem pasteItem = popupMenu.add(pasteAction);
			pasteItem.setText(Messages.getString("TextField.paste"));
			pasteItem.setAccelerator(KeyStroke.getKeyStroke("ctrl V"));
		}
		if (selectAction != null) {
			JMenuItem item = popupMenu.add(selectAction);
			item.setText(Messages.getString("TextField.select"));
			item.setAccelerator(KeyStroke.getKeyStroke("ctrl A"));
		}
		popupMenu.addPopupMenuListener(new TextPopupMenuListener(text));
		return popupMenu;
	}

	public static class TextPopupMenuListener implements PopupMenuListener {

		private JTextComponent parent;

		public TextPopupMenuListener(JTextComponent parent) {
			this.parent = parent;
		}

		public void popupMenuCanceled(PopupMenuEvent e) {
		}

		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		}

		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			String s = parent.getSelectedText();
			JPopupMenu popupMenu = parent.getComponentPopupMenu();
			for (int n = 0; n < popupMenu.getComponentCount(); n++) {
				JMenuItem item = (JMenuItem) popupMenu.getComponent(n);
				if ("cut-to-clipboard".equals(item.getAction().getValue(
						Action.NAME))) {
					item.setEnabled(parent.isEditable() && s != null
							&& s.length() > 0);
				} else if ("copy-to-clipboard".equals(item.getAction()
						.getValue(Action.NAME))) {
					item.setEnabled(s != null && s.length() > 0);
				} else if ("paste-from-clipboard".equals(item.getAction()
						.getValue(Action.NAME))) {
					item.setEnabled(parent.isEditable());
				}
			}
		}
	}

}
