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
 * 
 */
package jdbreport.util;

import javax.swing.JPanel;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.JDialog;

import and.properties.Properties;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class HelpDlg extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private HelpPane helpPane;

	public HelpDlg(Frame owner, URL url) {
		super(owner, false);
		init(url);
	}

	public HelpDlg(Dialog owner, URL url) {
		super(owner, false);
		init(url);
	}

	private void init(URL url) {
		this.addWindowListener(new java.awt.event.WindowAdapter() {

			public void windowClosing(java.awt.event.WindowEvent e) {
				saveProperties();
			}
		});

		setTitle(Messages.getString("HelpDlg.0")); //$NON-NLS-1$
		initialize();
		initProperties();
		getHelpPane().setUrl(url);
		setVisible(true);
	}

	protected void initProperties() {
		int state = Properties.getInt(HelpPane.WINDOW_STATE, Frame.NORMAL);
		Rectangle r = getBounds();
		if (state == Frame.MAXIMIZED_BOTH) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			r.x = 0;
			r.y = 0;
			r.width = screenSize.width;
			r.height = screenSize.height;
		} else {
			r.x = Properties.getInt(HelpPane.POS_X, r.x);
			r.y = Properties.getInt(HelpPane.POS_Y, r.y);
			r.width = Properties.getInt(HelpPane.SIZE_WIDTH, r.width);
			r.height = Properties.getInt(HelpPane.SIZE_HEIGHT, r.height);
			setBounds(r);
		}
	}

	protected void saveProperties() {
		Rectangle r = getBounds();
		Properties.put(HelpPane.POS_X, r.x);
		Properties.put(HelpPane.POS_Y, r.y);
		Properties.put(HelpPane.SIZE_WIDTH, r.width);
		Properties.put(HelpPane.SIZE_HEIGHT, r.height);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
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
			jContentPane.add(getHelpPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	private HelpPane getHelpPane() {
		if (helpPane == null) {
			helpPane = new HelpPane();
		}
		return helpPane;
	}

}
