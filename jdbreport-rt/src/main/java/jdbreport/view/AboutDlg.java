/*
 * AboutDlg.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2014 Andrey Kholmanskih
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

package jdbreport.view;

import javax.swing.JDialog;
import java.awt.Dimension;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;

import javax.swing.WindowConstants;
import javax.swing.JLabel;

import jdbreport.grid.ReportResources;
import jdbreport.view.model.JReportModel;
import jdbreport.util.Utils;

/**
 * @version 3.0 13.12.2014
 * @author Andrey Kholmanskih
 * 
 */
final class AboutDlg extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel bottomPanel = null;
	private JButton jButton = null;
	private JPanel centerPanel = null;

	/**
	 * This method initializes
	 * 
	 */
	public AboutDlg(Frame owner) {
		super(owner, true);
		initialize();
		setVisible(true);
	}

	public AboutDlg(Dialog owner) {
		super(owner, true);
		initialize();
		setVisible(true);
	}
	
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(new Dimension(350, 300));
		this.setResizable(false);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setTitle(Messages.getString("AboutDlg.0")); //$NON-NLS-1$
		this.setContentPane(getJContentPane());
		Utils.screenCenter(this, getOwner());
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
			jContentPane.add(getCenterPanel(), BorderLayout.CENTER);
			jContentPane.add(getBottomPanel(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes bottomPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getBottomPanel() {
		if (bottomPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagConstraints.ipadx = 30;
			gridBagConstraints.weightx = 0.0;
			gridBagConstraints.insets = new Insets(16, 28, 12, 28);
			bottomPanel = new JPanel();
			bottomPanel.setLayout(new GridBagLayout());
			bottomPanel.add(getJButton(), gridBagConstraints);
		}
		return bottomPanel;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("OK");
			jButton.addActionListener(e -> setVisible(false));
		}
		return jButton;
	}

	/**
	 * This method initializes centerPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getCenterPanel() {
		if (centerPanel == null) {
			centerPanel = new JPanel();
			centerPanel.setLayout(new GridBagLayout());
			centerPanel.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.RAISED));

			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.insets = new Insets(4, 4, 4, 4);
			JLabel logoLabel = new JLabel();
			logoLabel.setIcon(ReportResources.getInstance().getIcon(
					"logo100x66.png")); //$NON-NLS-1$
			centerPanel.add(logoLabel, gridBagConstraints);

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.insets = new Insets(20, 12, 4, 4);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.weightx = 0.1;
			JLabel nameLabel = new JLabel();
			nameLabel.setFont(new Font("Tahoma", Font.BOLD, 22)); //$NON-NLS-1$
			nameLabel.setText("JDBReport"); //$NON-NLS-1$
			centerPanel.add(nameLabel, gridBagConstraints);

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.CENTER;
			gridBagConstraints.insets = new Insets(8, 4, 8, 4);
			gridBagConstraints.gridy = 1;
			gridBagConstraints.gridwidth = 2;
			JLabel titleLabel = new JLabel();
			titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18)); //$NON-NLS-1$
			titleLabel.setText(Messages.getString("AboutDlg.10")); //$NON-NLS-1$
			centerPanel.add(titleLabel, gridBagConstraints);

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.CENTER;
			gridBagConstraints.insets = new Insets(8, 4, 8, 4);
			gridBagConstraints.gridy = 2;
			gridBagConstraints.gridwidth = 2;
			JLabel versionLabel = new JLabel();
			versionLabel
					.setText(Messages.getString("AboutDlg.2") + " " + JReportModel.VERSION);
																								// //$NON-NLS-1$
																								// //$NON-NLS-2$
			versionLabel.setFont(nameLabel.getFont().deriveFont((float) 14.0));
			centerPanel.add(versionLabel, gridBagConstraints);

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.CENTER;
			gridBagConstraints.insets = new Insets(8, 4, 4, 4);
			gridBagConstraints.gridy = 3;
			gridBagConstraints.gridwidth = 2;
			JLabel crLabel = new JLabel();
			crLabel
					.setText("<html>(C) 2007-2014 Andrey Kholmanskih.<br>All rights reserved.</html>"); //$NON-NLS-1$
			crLabel.setFont(nameLabel.getFont().deriveFont((float) 12));
			centerPanel.add(crLabel, gridBagConstraints);

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.CENTER;
			gridBagConstraints.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints.gridy = 4;
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.weighty = 0.1;
			gridBagConstraints.weightx = 1;
			JLabel linkLabel = new JLabel();
			linkLabel.setText("http://www.jdbreport.com"); //$NON-NLS-1$
			linkLabel.setFont(crLabel.getFont());
			centerPanel.add(linkLabel, gridBagConstraints);
		}
		return centerPanel;
	}

}
