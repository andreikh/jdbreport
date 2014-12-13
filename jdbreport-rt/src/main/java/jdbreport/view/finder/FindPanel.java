/*
 * Created on 30.06.2005
 *
 * Copyright 2006-2014 Andrey Kholmanskih
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
package jdbreport.view.finder;


import jdbreport.actions.BaseAction;
import jdbreport.util.Resources;
import jdbreport.util.finder.FindParams;
import jdbreport.util.finder.Finder;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class FindPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField findField;
	private static Resources resource;
	private Finder finder;
	private JLabel messageLabel;
	private JCheckBox incrementalBox;
	private JCheckBox wrapSearchBox;
	private JLabel findLabel;

	public FindPanel(Finder finder) {
		this(Messages.getResources(), finder);
	}

	public FindPanel(Resources resource) {
		this(resource, true, true);
	}

	public FindPanel(Resources resource, boolean showIncremental,
			boolean showWrapsearch) {
		super();
		FindPanel.resource = resource;
		initControls();
		incrementalBox.setVisible(showIncremental);
		wrapSearchBox.setVisible(showWrapsearch);
	}

	public FindPanel(Resources resource, Finder finder) {
		this(resource);
		setFinder(finder);
	}

	public void requestFocus() {
		super.requestFocus();
		findField.requestFocus();
	}

	public boolean requestFocusInWindow() {
		return findField.requestFocusInWindow();
	}


	/**
	 * 
	 */
	private void initControls() {
		setLayout(new BorderLayout());
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		Action closeAction = new CloseAction();
		Action nextAction = new FindNextAction();
		Action prevAction = new FindPrevAction();
		Dimension HGAP5 = new Dimension(5, 5);

		toolBar.add(closeAction);

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createRigidArea(HGAP5));
		p.add(getFindLabel(), BorderLayout.WEST);
		p.add(Box.createRigidArea(HGAP5));
		p.add(getFindField(), BorderLayout.EAST);
		p.add(Box.createRigidArea(HGAP5));
		toolBar.add(p);
		toolBar.add(nextAction);
		toolBar.add(prevAction);
		toolBar.addSeparator();
		incrementalBox = new JCheckBox(resource.getString("find_incremental"));
		incrementalBox.setToolTipText(resource
				.getString("find_incremental_tooltip"));
		toolBar.add(incrementalBox);
		// caseBox = new JCheckBox(resource.getString("find_case_sensitive"));
		// toolBar.add(caseBox);
		toolBar.addSeparator();

		wrapSearchBox = new JCheckBox("");
		wrapSearchBox.setToolTipText(resource
				.getString("find_wrapsearch_tooltip"));
		toolBar.add(wrapSearchBox);
		JLabel label = new JLabel(resource.getIcon("wrapsearch.png"));
		label.setToolTipText(resource.getString("find_wrapsearch_tooltip"));
		toolBar.add(label);
		toolBar.addSeparator();

		messageLabel = new JLabel();
		messageLabel.setForeground(Color.RED);
		toolBar.add(messageLabel);
		toolBar.addSeparator();
		add(toolBar, BorderLayout.WEST);
	}

	public JLabel getFindLabel() {
		if (findLabel == null) {
			findLabel = new JLabel(resource.getString("find_field_label"));
			findLabel.setFont(getFont());
		}
		return findLabel;
	}

	public JTextField getFindField() {
		if (findField == null) {
			findField = new JTextField();
			findField.setFont(getFont());
			findField.setPreferredSize(new Dimension(150, 20));
			findField.addKeyListener(new KeyAdapter() {

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						if (e.isShiftDown()) {
							findPrev();
						} else if (e.getModifiers() == 0) {
							findNext();
						}
					}
				}

			});
			findField.getDocument().addDocumentListener(new DocumentListener() {

				private void update() {
					if (isIncremental()) {
						incrementalFind(FindParams.FORWARD);
					}
				}

				public void insertUpdate(DocumentEvent e) {
					update();
				}

				public void removeUpdate(DocumentEvent e) {
					update();
				}

				public void changedUpdate(DocumentEvent e) {
					update();
				}

			});
		}
		return findField;
	}

	/**
	 * @param finder
	 *            The finder to set.
	 */
	public void setFinder(Finder finder) {
		this.finder = finder;
	}

	/**
	 * @return Returns the finder.
	 */
	public Finder getFinder() {
		return finder;
	}

	private void findNext() {
		find(FindParams.FORWARD);
	}

	private void findPrev() {
		find(FindParams.BACKWARD);
	}

	private void incrementalFind(int direction) {
		messageLabel.setText("");
		int cp = findField.getCaretPosition();
		findField.setBackground(Color.white);
		if (findField.getText().length() == 0)
			return;
		if (!finder.incrementalFind(new FindParams(findField.getText(),
				direction, FindParams.SCOPE_SELECTED, true, false,
				isWrapSearch()))) {
			messageLabel.setText(resource.getString("not_found"));
			findField.setBackground(getNotFoundColor());
		}
		if (cp > findField.getText().length()) {
			cp = findField.getText().length();
		}
		findField.setCaretPosition(cp);
	}

	private void find(int direction) {
		messageLabel.setText("");
		int cp = findField.getCaretPosition();
		if (findField.getText().length() == 0) {
			if (!Color.white.equals(findField.getBackground()))
				findField.setBackground(Color.white);
			return;
		}
		if (!finder.find(new FindParams(findField.getText(), direction,
				FindParams.SCOPE_SELECTED, isIncremental(), false,
				isWrapSearch()))) {
			messageLabel.setText(resource.getString("not_found"));
			if (!getNotFoundColor().equals(findField.getBackground()))
				findField.setBackground(getNotFoundColor());
		} else if (!Color.white.equals(findField.getBackground()))
			findField.setBackground(Color.white);

		findField.requestFocus();
		if (cp > findField.getText().length()) {
			cp = findField.getText().length();
		}
		findField.setCaretPosition(cp);
	}

	private static Color getNotFoundColor() {
		return Color.red.brighter();
	}

	private abstract class FindPanelAction extends BaseAction {

		private static final long serialVersionUID = 1L;

		/**
		 * @param name
		 */
		public FindPanelAction(String name) {
			super(name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see and.actions.BaseAction#getResource()
		 */
		public Resources getResource() {
			return resource;
		}

	}

	private class CloseAction extends FindPanelAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public CloseAction() {
			super("find_close");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}

	}

	private class FindNextAction extends FindPanelAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public FindNextAction() {
			super("find_next");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent e) {
			findNext();
		}

	}

	private class FindPrevAction extends FindPanelAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4995649914439306884L;

		public FindPrevAction() {
			super("find_prev");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent e) {
			findPrev();
		}

	}

	/**
	 * @param b incremental
	 */
	public void setIncremental(boolean b) {
		incrementalBox.setSelected(b);
	}

	public boolean isIncremental() {
		return incrementalBox.isSelected();
	}

	public boolean isWrapSearch() {
		return wrapSearchBox.isSelected();
	}

	public void setWrapSearch(boolean b) {
		wrapSearchBox.setSelected(b);
	}

}
