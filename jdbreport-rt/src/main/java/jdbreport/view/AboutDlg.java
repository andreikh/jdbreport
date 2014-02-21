/*
 * AboutDlg.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2012 Andrey Kholmanskih. All rights reserved.
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
import jdbreport.model.JReportModel;
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
					.setText("<html>(C) 2007-2013 Andrey Kholmanskih.<br>All rights reserved.</html>"); //$NON-NLS-1$
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
