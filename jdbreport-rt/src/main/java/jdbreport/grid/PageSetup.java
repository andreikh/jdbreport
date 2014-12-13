/*
 *
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

import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import javax.swing.JCheckBox;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import jdbreport.grid.undo.UndoItem;
import jdbreport.model.Units;
import jdbreport.model.print.ReportPage;
import jdbreport.model.print.ReportPage.PaperSize;
import jdbreport.util.Utils;

/**
 * @version 2.0 13.03.2011
 * @author Andrey Kholmanskih
 * 
 */
public class PageSetup extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static final Units unit = Units.MM;
	private JPanel jContentPane = null;
	private JPanel buttonPanel = null;
	private JPanel centerPanel = null;
	private JPanel orientationPanel = null;
	private JRadioButton portraitButton = null;
	private JRadioButton landscapeButton = null;
	private JPanel marginPanel = null;
	private JTextField leftField = null;
	private JTextField rightField = null;
	private JTextField topField = null;
	private JTextField bottomField = null;
	private JCheckBox shrinkWidthBox = null;
	private JPanel jPanel = null;
	private JLabel imageLabel = null;
	private Icon portraitIcon;
	private Icon landscapeIcon;
	private JButton okButton = null;
	private JButton cancelButton = null;
	private ReportPage page;
	private InputVerifier numericVerifier;
	private UndoListener undoListener;
	private JComboBox formatBox;
	private JTextField widthField;
	private JTextField heightField;
	private PaperSize currentPaperSize;

	public PageSetup(ReportPage page) throws HeadlessException {
		super();
		setModal(true);
		this.page = page;
		initialize();
	}

	public PageSetup(Frame owner, ReportPage page) throws HeadlessException {
		super(owner, true);
		this.page = page;
		initialize();
	}

	public PageSetup(Dialog owner, ReportPage page) throws HeadlessException {
		super(owner, true);
		this.page = page;
		initialize();
	}

	private void initialize() {
		this.setSize(300, 360);
		this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
		this
				.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setContentPane(getJContentPane());
		Utils.screenCenter(this);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJPanel(), java.awt.BorderLayout.SOUTH);
			jContentPane.add(getJPanel2(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (buttonPanel == null) {
			FlowLayout flowLayout1 = new FlowLayout();
			flowLayout1.setAlignment(java.awt.FlowLayout.RIGHT);
			flowLayout1.setHgap(10);
			buttonPanel = new JPanel();
			buttonPanel.setLayout(flowLayout1);
			buttonPanel.add(getOkButton());
			buttonPanel.add(getCancelButton());
		}
		return buttonPanel;
	}

	/**
	 * This method initializes centerPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel2() {
		if (centerPanel == null) {
			centerPanel = new JPanel(new GridBagLayout());
			
			JPanel panel = new JPanel(new GridBagLayout());
			panel.setBorder(BorderFactory.createTitledBorder(Messages.getString("PageSetup.2"))); //$NON-NLS-1$

			GridBagConstraints constr = new GridBagConstraints();
			constr.insets = new Insets(2, 2, 2, 2);
			constr.anchor = GridBagConstraints.WEST;
			panel.add(new JLabel(Messages.getString("PageSetup.2")), constr); //$NON-NLS-1$
			
			constr = new GridBagConstraints();
			constr.insets = new Insets(2, 2, 2, 2);
			constr.anchor = GridBagConstraints.NORTHWEST;
			constr.fill = GridBagConstraints.HORIZONTAL;
			constr.gridx = 1;
			panel.add(getFormatBox(), constr);

			constr = new GridBagConstraints();
			constr.insets = new Insets(2, 2, 2, 16);
			constr.anchor = GridBagConstraints.WEST;
			constr.gridy = 1;
			panel.add(new JLabel(Messages.getString("PageSetup.width")), constr); //$NON-NLS-1$
			
			constr = new GridBagConstraints();
			constr.insets = new Insets(2, 2, 2, 2);
			constr.anchor = GridBagConstraints.NORTHWEST;
			constr.fill = GridBagConstraints.HORIZONTAL;
			constr.gridx = 1;
			constr.gridy = 1;
			panel.add(getWidthField(), constr);

			constr = new GridBagConstraints();
			constr.insets = new Insets(2, 2, 2, 16);
			constr.anchor = GridBagConstraints.WEST;
			constr.gridy = 2;
			panel.add(new JLabel(Messages.getString("PageSetup.height")), constr); //$NON-NLS-1$
			
			constr = new GridBagConstraints();
			constr.insets = new Insets(2, 2, 2, 2);
			constr.anchor = GridBagConstraints.NORTHWEST;
			constr.fill = GridBagConstraints.HORIZONTAL;
			constr.gridx = 1;
			constr.gridy = 2;
			constr.weightx = 0.1;
			constr.weighty = 0.1;
			panel.add(getHeightField(), constr);
			
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.anchor = GridBagConstraints.NORTHWEST;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.insets = new Insets(4, 4, 4, 4);
			constraints.gridwidth = 2;
			centerPanel.add(panel, constraints);
			
			constraints = new GridBagConstraints();
			constraints.anchor = GridBagConstraints.NORTHWEST;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.insets = new Insets(4, 4, 4, 4);
			constraints.gridy = 1;
			centerPanel.add(getOrientationPanel(), constraints);

			constraints = new GridBagConstraints();
			constraints.anchor = GridBagConstraints.NORTHWEST;
			constraints.insets = new Insets(4, 4, 4, 4);
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.gridy = 1;
			constraints.gridx = 1;
			centerPanel.add(getMarginPanel(), constraints);
			
			constraints = new GridBagConstraints();
			constraints.anchor = GridBagConstraints.NORTHWEST;
			constraints.insets = new Insets(4, 4, 4, 4);
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.gridy = 2;
			constraints.gridwidth = 2;
			constraints.weightx = 0.1;
			constraints.weighty = 0.1;
			centerPanel.add(getShrinkWidthBox(), constraints);
		}
		return centerPanel;
	}

	/**
	 * This method initializes formatBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getFormatBox() {
		if (formatBox == null) {
			formatBox = new JComboBox();
			currentPaperSize = page.getPaperSize();
			formatBox.setModel(new DefaultComboBoxModel(ReportPage.PaperSize
					.values()));
			formatBox.setSelectedIndex(page.getPaperSize().ordinal());
			formatBox.setActionCommand("format"); //$NON-NLS-1$
			formatBox.addActionListener(this);
		}
		return formatBox;
	}

	/**
	 * This method initializes orientationPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getOrientationPanel() {
		if (orientationPanel == null) {

			orientationPanel = new JPanel();
			orientationPanel
					.setBorder(javax.swing.BorderFactory
							.createTitledBorder(Messages.getString("PageSetup.1"))); //$NON-NLS-1$
			orientationPanel.setLayout(new BoxLayout(orientationPanel,
					BoxLayout.Y_AXIS));
			orientationPanel.add(getPortraitButton());
			orientationPanel.add(getLandscapeButton());
			orientationPanel.add(getJPanel4());
		}
		return orientationPanel;
	}

	/**
	 * This method initializes portraitButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getPortraitButton() {
		if (portraitButton == null) {
			portraitButton = new JRadioButton();
			portraitButton.setText(Messages.getString("PageSetup.3")); //$NON-NLS-1$
			portraitButton
					.setSelected(page.getOrientation() == ReportPage.PORTRAIT);
			portraitButton
					.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			portraitButton
					.addChangeListener(new javax.swing.event.ChangeListener() {
						public void stateChanged(javax.swing.event.ChangeEvent e) {
							if (portraitButton.isSelected()) {
								getLandscapeButton().setSelected(false);
								getImageLabel().setIcon(portraitIcon);
							}
						}
					});
		}
		return portraitButton;
	}

	/**
	 * This method initializes landscapeButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getLandscapeButton() {
		if (landscapeButton == null) {
			landscapeButton = new JRadioButton();
			landscapeButton.setText(Messages.getString("PageSetup.4")); //$NON-NLS-1$
			landscapeButton
					.setSelected(page.getOrientation() == ReportPage.LANDSCAPE);
			landscapeButton
					.addChangeListener(new javax.swing.event.ChangeListener() {
						public void stateChanged(javax.swing.event.ChangeEvent e) {
							if (landscapeButton.isSelected()) {
								getPortraitButton().setSelected(false);
								getImageLabel().setIcon(landscapeIcon);
							}
						}
					});
		}
		return landscapeButton;
	}

	/**
	 * This method initializes marginPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMarginPanel() {
		if (marginPanel == null) {
			JLabel jLabel6 = new JLabel();
			jLabel6.setText(Messages.getString("PageSetup.5")); //$NON-NLS-1$
			JLabel jLabel5 = new JLabel();
			jLabel5.setText(Messages.getString("PageSetup.6")); //$NON-NLS-1$
			JLabel jLabel4 = new JLabel();
			jLabel4.setText(Messages.getString("PageSetup.7")); //$NON-NLS-1$
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(4);
			gridLayout.setHgap(5);
			gridLayout.setVgap(5);
			gridLayout.setColumns(2);
			JLabel jLabel3 = new JLabel();
			jLabel3.setText(Messages.getString("PageSetup.8")); //$NON-NLS-1$
			marginPanel = new JPanel();
			marginPanel.setLayout(gridLayout);
			marginPanel.setBounds(new java.awt.Rectangle(150, 81, 130, 112));
			marginPanel
					.setBorder(javax.swing.BorderFactory
							.createTitledBorder(Messages.getString("PageSetup.0"))); //$NON-NLS-1$
			marginPanel.add(jLabel3);
			marginPanel.add(getLeftField());
			marginPanel.add(jLabel4);
			marginPanel.add(getRightField());
			marginPanel.add(jLabel5);
			marginPanel.add(getTopField());
			marginPanel.add(jLabel6);
			marginPanel.add(getBottomField());
		}
		return marginPanel;
	}

	private JTextField getWidthField() {
		if (widthField == null) {
			widthField = new JTextField();
			widthField.setInputVerifier(getNumericVerifier());
			widthField.setText("" + Utils.round(page.getWidth(unit), 1)); //$NON-NLS-1$
			widthField.setEditable(page.getPaperSize().equals(PaperSize.User));
		}
		return widthField;
	}

	private JTextField getHeightField() {
		if (heightField == null) {
			heightField = new JTextField();
			heightField.setInputVerifier(getNumericVerifier());
			heightField.setText("" + Utils.round(page.getHeight(unit), 1)); //$NON-NLS-1$
			heightField.setEditable(page.getPaperSize().equals(PaperSize.User));
			
		}
		return heightField;
	}
	
	/**
	 * This method initializes leftField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getLeftField() {
		if (leftField == null) {
			leftField = new JTextField();
			leftField.setInputVerifier(getNumericVerifier());
			leftField.setText("" + Utils.round(page.getLeftMargin(unit), 1)); //$NON-NLS-1$
		}
		return leftField;
	}

	/**
	 * This method initializes rightField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getRightField() {
		if (rightField == null) {
			rightField = new JTextField();
			rightField.setInputVerifier(getNumericVerifier());
			rightField
					.setText("" + Utils.round(page.getRightMargin(unit), 1)); //$NON-NLS-1$
		}
		return rightField;
	}

	/**
	 * This method initializes topField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTopField() {
		if (topField == null) {
			topField = new JTextField();
			topField.setInputVerifier(getNumericVerifier());
			topField.setText("" + Utils.round(page.getTopMargin(unit), 1)); //$NON-NLS-1$
		}
		return topField;
	}

	/**
	 * This method initializes bottomField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getBottomField() {
		if (bottomField == null) {
			bottomField = new JTextField();
			bottomField.setInputVerifier(getNumericVerifier());
			bottomField
					.setText("" + Utils.round(page.getBottomMargin(unit), 1)); //$NON-NLS-1$
		}
		return bottomField;
	}

	private InputVerifier getNumericVerifier() {
		if (numericVerifier == null) {
			numericVerifier = new NumericVerifier();
		}
		return numericVerifier;
	}

	/**
	 * This method initializes shrinkWidthBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getShrinkWidthBox() {
		if (shrinkWidthBox == null) {
			shrinkWidthBox = new JCheckBox();
			shrinkWidthBox.setText(Messages.getString("PageSetup.13")); //$NON-NLS-1$
			shrinkWidthBox.setSelected(page.isShrinkWidth());
		}
		return shrinkWidthBox;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel4() {
		if (jPanel == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setVgap(10);
			imageLabel = new JLabel();
			portraitIcon = ReportResources.getInstance()
					.getIcon("portrait.png"); //$NON-NLS-1$
			landscapeIcon = ReportResources.getInstance().getIcon(
					"landscape.png"); //$NON-NLS-1$
			if (page.getOrientation() == ReportPage.PORTRAIT)
				imageLabel.setIcon(portraitIcon);
			else
				imageLabel.setIcon(landscapeIcon);

			imageLabel.setText(""); //$NON-NLS-1$
			jPanel = new JPanel();
			jPanel.setLayout(flowLayout);
			jPanel.add(imageLabel, null);
		}
		return jPanel;
	}

	private JLabel getImageLabel() {
		return imageLabel;
	}

	/**
	 * This method initializes okButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText(Messages.getString("PageSetup.17")); //$NON-NLS-1$
			okButton.setActionCommand("ok"); //$NON-NLS-1$
			okButton.addActionListener(this);
		}
		return okButton;
	}

	private void apply() {
		pushUndo(new PageUndo(page));
		if (portraitButton.isSelected())
			page.setOrientation(ReportPage.PORTRAIT);
		else
			page.setOrientation(ReportPage.LANDSCAPE);
		double x = 0;
		double y = 0;
		double right = 0;
		double bottom = 0;
		double height = 0;
		double width = 0;
		String s = leftField.getText();
		if (s.length() > 0)
			x = Double.parseDouble(s);
		s = rightField.getText();
		if (s.length() > 0)
			right = Double.parseDouble(s);
		s = topField.getText();
		if (s.length() > 0)
			y = Double.parseDouble(s);
		s = bottomField.getText();
		if (s.length() > 0)
			bottom = Double.parseDouble(s);

		s = widthField.getText();
		if (s.length() > 0)
			width = Double.parseDouble(s);
		s = heightField.getText();
		if (s.length() > 0)
			height = Double.parseDouble(s);
		
		if (width > 0 && height > 0 && 
				PaperSize.User.equals(getFormatBox().getSelectedItem())) {
			page.setSize(width, height, unit);
		}
		page.setMargin(x, y, right, bottom, unit);
		page.setShrinkWidth(shrinkWidthBox.isSelected());
		page.setPaperSize((PaperSize) getFormatBox().getSelectedItem());
		
	}

	public void addUndoListener(UndoListener l) {
		undoListener = l;
	}

	public void removeUndoListener(UndoListener l) {
		if (undoListener == l)
			undoListener = null;
	}

	protected void pushUndo(UndoItem undo) {
		if (undoListener != null) {
			undoListener.pushUndo(new UndoEvent(this, undo));
		}
	}

	/**
	 * This method initializes cancelButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText(Messages.getString("PageSetup.18")); //$NON-NLS-1$
			cancelButton.setActionCommand("cancel"); //$NON-NLS-1$
			cancelButton.addActionListener(this);
		}
		return cancelButton;
	}

	public void actionPerformed(ActionEvent e) {
		if ("format".equals(e.getActionCommand())) { //$NON-NLS-1$
			ReportPage.PaperSize ps = (PaperSize) getFormatBox().getSelectedItem();
			if (!ps.equals(currentPaperSize)) {
				boolean editable = ps.equals(PaperSize.User);
				if (!editable)  {
					heightField.setText("" + Utils.round(unit.getValue(ps.getHeight()), 1));  //$NON-NLS-1$
					widthField.setText("" + Utils.round(unit.getValue(ps.getWidth()), 1)); //$NON-NLS-1$
				}
				heightField.setEditable(editable);
				widthField.setEditable(editable);
				currentPaperSize = ps;
			}
		} else if ("cancel".equals(e.getActionCommand())) { //$NON-NLS-1$
			setVisible(false);	
		} else if ("ok".equals(e.getActionCommand())) { //$NON-NLS-1$
			apply();
			setVisible(false);
		}
		
	}
	
	private static class NumericVerifier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {
			JTextField tf = (JTextField) input;
			if (!tf.getText().trim().equals("")) //$NON-NLS-1$
				try {
					Double.parseDouble(tf.getText().trim());
				} catch (Exception e) {
					jdbreport.util.Utils.showError(e);
					return false;
				}
			return true;
		}

	}

	private static class PageUndo implements UndoItem {

		private ReportPage page;
		private ReportPage oldPage;

		public PageUndo(ReportPage page) {
			this.page = page;
			oldPage = (ReportPage) page.clone();
		}

		public void clear() {
			page = null;
			oldPage = null;
		}

		public String getDescription() {
			return Messages.getString("PageSetup.undo_descr"); //$NON-NLS-1$
		}

		public UndoItem undo() {
			ReportPage tmpPage = oldPage;
			oldPage = (ReportPage) page.clone();
			page.setCopies(tmpPage.getCopies());
			Units unit = Units.PT;
			page.setMargin(tmpPage.getLeftMargin(unit), tmpPage
					.getTopMargin(unit), tmpPage.getRightMargin(unit), tmpPage
					.getBottomMargin(unit), unit);
			page.setOrientation(tmpPage.getOrientation());
			page.setPaper(tmpPage.getPaper());
			page.setShrinkWidth(tmpPage.isShrinkWidth());
			page.setSize(tmpPage.getWidth(unit), tmpPage.getHeight(unit), unit);

			return this;
		}

	}


}
