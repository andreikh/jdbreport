/*
 * Created on 27.05.2005
 *
 * Copyright (C) 2005-2012 Andrey Kholmanskih. All rights reserved.
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
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
 */
package jdbreport.design.grid.dialogs;

import jdbreport.source.JdbcSource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

/**
 * @author Andrey Kholmanskih
 * 
 * @version 3.0 12.12.2014
 */
public class DBSourcePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JdbcSource dbSource;
	private JTextField aliasText;
	private JTextField dataSourceText;
	private JTextField driverText;
	private JTextField urlText;
	private JTextField loginText;
	private JTextField passwordText;
	private JComboBox driversBox;
	private JTable table;
	private PropertiesTableModel settingsModel;
	private boolean editable;

	public DBSourcePanel(JdbcSource dbSource) {
		this(dbSource, true);
	}
	
	public DBSourcePanel(JdbcSource dbSource, boolean editable) {
		this.editable = editable;
		this.dbSource = dbSource;
		initialize();
		initValues();
	}

	protected void initialize() {
		setLayout(new BorderLayout());
		add(createClientPanel(), BorderLayout.NORTH);
		add(createTablePanel(), BorderLayout.CENTER);
	}

	protected JPanel createClientPanel() {
		JPanel clientPanel = new JPanel();
		clientPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		clientPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints constr = new GridBagConstraints();
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.insets = new Insets(2, 2, 2, 2);
		constr.anchor = GridBagConstraints.WEST;
		JLabel aliasLabel = new JLabel(Messages.getString("DBSourceDialog.7")); //$NON-NLS-1$
		aliasLabel.setFont(getFont());
		clientPanel.add(aliasLabel, constr);
		
		constr.gridx = 1;
		constr.anchor = GridBagConstraints.NORTHWEST;
		aliasText = new JTextField();
		aliasText.setEditable(editable);
		aliasText.setFont(getFont());
		clientPanel.add(aliasText, constr);

		constr.anchor = GridBagConstraints.WEST;
		constr.gridx = 0;
		constr.gridy = 1;
		JLabel dataSourceLabel = new JLabel(Messages
				.getString("DBSourceDialog.8")); //$NON-NLS-1$
		dataSourceLabel.setFont(getFont());
		clientPanel.add(dataSourceLabel, constr);
		
		constr.anchor = GridBagConstraints.NORTHWEST;
		constr.gridx = 1;
		dataSourceText = new JTextField();
		dataSourceText.setEditable(editable);
		dataSourceText.setFont(getFont());
		clientPanel.add(dataSourceText, constr);

		constr.anchor = GridBagConstraints.WEST;
		constr.gridx = 0;
		constr.gridy = 2;
		JLabel dbLabel = new JLabel(Messages.getString("DBSourceDialog.db")); //$NON-NLS-1$
		dbLabel.setFont(getFont());
		clientPanel.add(dbLabel, constr);
		
		constr.anchor = GridBagConstraints.NORTHWEST;
		constr.gridx = 1;
		clientPanel.add(getDriversBox(), constr);
		
		constr.anchor = GridBagConstraints.WEST;
		constr.gridx = 0;
		constr.gridy = 3;
		JLabel driverLabel = new JLabel(Messages.getString("DBSourceDialog.9")); //$NON-NLS-1$
		driverLabel.setFont(getFont());
		clientPanel.add(driverLabel, constr);
		
		constr.anchor = GridBagConstraints.NORTHWEST;
		constr.gridx = 1;
		driverText = new JTextField();
		driverText.setEditable(editable);
		driverText.setFont(getFont());
		clientPanel.add(driverText, constr);

		constr.anchor = GridBagConstraints.WEST;
		constr.gridx = 0;
		constr.gridy = 4;
		JLabel urlLabel = new JLabel(Messages.getString("DBSourceDialog.10")); //$NON-NLS-1$
		urlLabel.setFont(getFont());
		clientPanel.add(urlLabel, constr);
		
		constr.anchor = GridBagConstraints.NORTHWEST;
		constr.gridx = 1;
		urlText = new JTextField();
		urlText.setEditable(editable);
		urlText.setFont(getFont());
		clientPanel.add(urlText, constr);

		constr.anchor = GridBagConstraints.WEST;
		constr.gridx = 0;
		constr.gridy = 5;
		JLabel loginLabel = new JLabel(Messages.getString("DBSourceDialog.11")); //$NON-NLS-1$
		loginLabel.setFont(getFont());
		clientPanel.add(loginLabel, constr);
		
		constr.anchor = GridBagConstraints.NORTHWEST;
		constr.gridx = 1;
		loginText = new JTextField();
		loginText.setEditable(editable);
		loginText.setFont(getFont());
		clientPanel.add(loginText, constr);

		constr.anchor = GridBagConstraints.WEST;
		constr.gridx = 0;
		constr.gridy = 6;
		JLabel passwordLabel = new JLabel(Messages
				.getString("DBSourceDialog.12")); //$NON-NLS-1$
		passwordLabel.setFont(getFont());
		clientPanel.add(passwordLabel, constr);
		
		constr.anchor = GridBagConstraints.NORTHWEST;
		constr.gridx = 1;
		constr.weightx = 0.1;
		constr.weighty = 0.1;
		passwordText = new JPasswordField();
		passwordText.setEditable(editable);
		passwordText.setFont(getFont());
		clientPanel.add(passwordText, constr);

		return clientPanel;
	}

	private JComboBox getDriversBox() {
		if (driversBox == null) {
			driversBox = new JComboBox();
			driversBox.setEnabled(editable);
			driversBox.setFont(getFont());
			for (JdbcDrivers d : JdbcDrivers.getDrivers()) {
				driversBox.addItem(d);
			}
			driversBox.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					updateDriverField();
				}

			});
		}
		return driversBox;
	}

	protected void updateDriverField() {
		JdbcDrivers d = (JdbcDrivers) driversBox.getSelectedItem();
		if (d != null) {
			driverText.setText(d.getDriver());
			if (!urlText.getText().startsWith(d.getUrl()))
				urlText.setText(d.getUrl());
		}
		driverText.setEditable(d == null);
	}

	protected JPanel createTablePanel() {
		JPanel tablePanel = new JPanel() {

			private static final long serialVersionUID = 1L;

			Insets insets = new Insets(10, 10, 10, 10);

			public Insets getInsets() {
				return insets;
			}
		};
		tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));

		JPanel tblHPanel = new JPanel();

		tblHPanel.setLayout(new BoxLayout(tblHPanel, BoxLayout.X_AXIS));
		JLabel settingsLabel = new JLabel(Messages
				.getString("DBSourceDialog.2")); //$NON-NLS-1$
		settingsLabel.setFont(driverText.getFont());
		tblHPanel.add(settingsLabel, BorderLayout.WEST);
		tblHPanel.add(Box.createRigidArea(new Dimension(50, 5)));

		JButton addButton = new JButton("+"); //$NON-NLS-1$
		addButton.setToolTipText(Messages.getString("DBSourceDialog.4")); //$NON-NLS-1$
		addButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				settingsModel.addRow();
			}

		});

		JButton delButton = new JButton("-"); //$NON-NLS-1$
		delButton.setToolTipText(Messages.getString("DBSourceDialog.6")); //$NON-NLS-1$
		delButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int[] rows = table.getSelectedRows();
				if (rows.length > 0)
					settingsModel.delRow(rows[0], rows[rows.length - 1]);
			}

		});
		tblHPanel.add(addButton, BorderLayout.CENTER);
		tblHPanel.add(delButton, BorderLayout.CENTER);

		Dimension HGAP5 = new Dimension(5, 5);
		tablePanel.add(tblHPanel, BorderLayout.NORTH);
		tablePanel.add(Box.createRigidArea(HGAP5));
		Properties map = null;
		if (dbSource != null) {
			map = dbSource.getProperties();
		}
		settingsModel = new PropertiesTableModel(map, editable);
		table = new JTable(settingsModel);
		table.setFont(getFont());
		table.getTableHeader().setFont(getFont());
		JScrollPane scroll = new JScrollPane(table);
		tablePanel.add(scroll, BorderLayout.CENTER);
		return tablePanel;
	}

	protected void initValues() {
		if (dbSource == null)
			return;

		aliasText.setText(dbSource.getAlias());
		dataSourceText.setText(dbSource.getJndiName());
		driverText.setText(dbSource.getDriverName());
		if (dbSource.getDriverName() != null) {
			for (int i = 1; i < driversBox.getItemCount(); i++) {
				if (dbSource.getDriverName().equals(
						((JdbcDrivers)driversBox.getItemAt(i)).getDriver())) {
					driversBox.setSelectedIndex(i);
					break;
				}
			}
		}
		if (driversBox.getSelectedIndex() < 0) {
			driversBox.setSelectedIndex(0);
		}
		driverText.setEditable(driversBox.getSelectedIndex() <= 0);
		urlText.setText(dbSource.getUrl());
		loginText.setText(dbSource.getUser());
		passwordText.setText(""); //$NON-NLS-1$
	}

	public void saveProperty() {
		if (editable) {
			dbSource.setAlias(aliasText.getText());
			dbSource.setJndiName(dataSourceText.getText());
			dbSource.setDriverName(driverText.getText());
			dbSource.setUrl(urlText.getText());
			dbSource.setUser(loginText.getText());
			if (passwordText.getText() != null
					&& passwordText.getText().length() > 0)
				dbSource.setPassword(passwordText.getText());
			settingsModel.getProperties(dbSource.getProperties());
		}
	}

	public JdbcSource getDbSource() {
		return dbSource;
	}

}
