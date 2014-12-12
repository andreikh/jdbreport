/*
 * Created on 27.05.2005
 *
 * Copyright (C) 2005-2011 Andrey Kholmanskih. All rights reserved.
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
import jdbreport.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class DBSourceDialog extends JDialog {

	private static final long serialVersionUID = 5248366872311585276L;

	static public final int OK = 1;

	private JdbcSource dbItem;

	private int exitCode;

	private DBSourcePanel dbPanel;

	public DBSourceDialog(Window owner, JdbcSource dbItem) throws Exception {
		super(owner, DEFAULT_MODALITY_TYPE);
		init(dbItem);
	}

	private void init(JdbcSource dbItm) throws Exception {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(Messages.getString("DBSourceDialog.0")); //$NON-NLS-1$
		if (dbItm == null) {
			throw new Exception("JdbcSource is null"); //$NON-NLS-1$
		}
		this.dbItem = dbItm;
		initControls();
		Utils.screenCenter(this);
	}

	public JdbcSource getDbItem() {
		return dbItem;
	}

	public int getExitCode() {
		return exitCode;
	}

	protected void initControls() {
		setSize(400, 450);
		JPanel panel = new JPanel(new BorderLayout());
		getContentPane().add(panel);
		dbPanel = createDbSourcePanel();
		panel.add(dbPanel, BorderLayout.CENTER);
		panel.add(createBottomPanel(), BorderLayout.SOUTH);
	}

	protected DBSourcePanel createDbSourcePanel() {
		return  new DBSourcePanel(dbItem);
	}


	protected JPanel createBottomPanel() {
		JPanel bottomPanel = new JPanel(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton(Messages.getString("DBSourceDialog.13")); //$NON-NLS-1$
		buttonPanel.add(okButton);
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dbPanel.saveProperty();
				exitCode = OK;
				close();
			}

		});

		JButton cancelButton = new JButton(Messages
				.getString("DBSourceDialog.14")); //$NON-NLS-1$
		cancelButton.setFont(getFont());
		buttonPanel.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				close();
			}

		});

		bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

		return bottomPanel;
	}

	private void close() {
		setVisible(false);
	}

	public boolean isOk() {
		return exitCode == OK;
	}
}
