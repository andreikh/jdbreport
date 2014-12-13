/*
 * AboutDlg.java
 *
 * JDBReport Designer
 * 
 * Copyright (C) 2007-2014 Andrey Kholmanskih. All rights reserved.
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

package jdbreport.design.view;

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

import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

import jdbreport.view.model.JReportModel;
import jdbreport.util.Utils;

/**
 * @version 2.0 21.05.2012
 * @author Andrey Kholmanskih
 * 
 */
final class AboutDlg extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel bottomPanel = null;
	private JButton jButton = null;
	private JPanel centerPanel = null;
	private JTabbedPane tabbedPane;
	private JPanel firstPanel;

	/**
	 * This method initializes
	 * 
	 */
	AboutDlg(Frame owner) {
		super(owner, true);
		initialize();
		setVisible(true);
	}

	AboutDlg(Dialog owner) {
		super(owner, true);
		initialize();
		setVisible(true);
	}
	
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(new Dimension(410, 345));
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
			jContentPane.add(getTabbedPanel(), BorderLayout.CENTER);
			jContentPane.add(getBottomPanel(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	private JTabbedPane getTabbedPanel() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.add(Messages.getString("AboutDlg.0"), getFirstPanel()); //$NON-NLS-1$
		}
		return tabbedPane;
	}



	private JPanel getFirstPanel() {
		if (firstPanel == null) {
			firstPanel = new JPanel();
			firstPanel.setLayout(new BorderLayout());
			firstPanel.add(getCenterPanel(), BorderLayout.CENTER);
		}
		return firstPanel;
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
			jButton.setText("OK"); //$NON-NLS-1$
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setVisible(false);
				}
			});
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
			centerPanel.setLayout(null);
			centerPanel.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.RAISED)); // Generated

			JLabel titleLabel = new JLabel();
			titleLabel.setFont(new Font("Tahoma", Font.BOLD, 22)); //$NON-NLS-1$
			titleLabel.setText(Messages.getString("AboutDlg.10")); //$NON-NLS-1$
			titleLabel.setBounds(12, 35, 300, 32);
			centerPanel.add(titleLabel);

			JLabel nameLabel = new JLabel();
			nameLabel.setFont(new Font("Tahoma", Font.BOLD, 22)); //$NON-NLS-1$
			nameLabel.setText("JDBReport Designer"); //$NON-NLS-1$
			nameLabel.setBounds(12, 70, 300, 24);
			centerPanel.add(nameLabel);

			JLabel versionLabel = new JLabel();
			versionLabel
					.setText(Messages.getString("AboutDlg.2") + " " + JReportModel.VERSION); //$NON-NLS-1$ //$NON-NLS-2$
			versionLabel.setFont(nameLabel.getFont().deriveFont((float) 14.0));
			versionLabel.setBounds(12, 135, 300, 24);
			centerPanel.add(versionLabel);

			JLabel crLabel = new JLabel();
			crLabel
					.setText("<html>Copyright (C) 2007-2014 Andrey Kholmanskih.<br>All rights reserved.</html>"); //$NON-NLS-1$
			crLabel.setFont(nameLabel.getFont().deriveFont((float) 12));
			crLabel.setBounds(12, 155, 350, 40);
			centerPanel.add(crLabel);

			JLabel linkLabel = new JLabel();
			linkLabel.setText("http://www.jdbreport.com"); //$NON-NLS-1$
			linkLabel.setFont(crLabel.getFont());
			linkLabel.setBounds(12, 190, 300, 24);
			centerPanel.add(linkLabel);

			JLabel logoLabel = new JLabel();
			logoLabel.setIcon(new ImageIcon(getClass().getResource(
					"/jdbreport/design/resources/jdbrlogo.png"))); //$NON-NLS-1$
			logoLabel.setBounds(0, 0, 400, 240);
			centerPanel.add(logoLabel);
		}
		return centerPanel;
	}

}
