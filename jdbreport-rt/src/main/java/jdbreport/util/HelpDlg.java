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
