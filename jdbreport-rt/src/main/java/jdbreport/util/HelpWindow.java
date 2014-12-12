/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2010 Andrey Kholmanskih. All rights reserved.
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

import jdbreport.util.xml.XMLProperties;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JFrame;

import java.net.URL;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class HelpWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private HelpPane helpPane;

	private XMLProperties properties;

	protected HelpWindow(XMLProperties properties) {
		this(properties, null);
	}

	/**
	 * This is the default constructor
	 */
	public HelpWindow(XMLProperties properties, URL url) {
		super();
		this.properties = properties;
		initialize();
		this.addWindowListener(new java.awt.event.WindowAdapter() {

			public void windowClosing(java.awt.event.WindowEvent e) {
				saveProperties();
			}
		});
		initProperties();
		if (url != null)
			getHelpPane().setUrl(url);
	}

	protected void initProperties() {
		int state = properties.getInt(HelpPane.WINDOW_STATE, Frame.NORMAL);
		if (state == Frame.MAXIMIZED_BOTH) {
			setExtendedState(state);
		} else {
			if (state == Frame.NORMAL) {
				Rectangle r = getBounds();
				r.x = properties.getInt(HelpPane.POS_X, r.x);
				r.y = properties.getInt(HelpPane.POS_Y, r.y);
				r.width = properties.getInt(HelpPane.SIZE_WIDTH, r.width);
				r.height = properties.getInt(HelpPane.SIZE_HEIGHT, r.height);
				setBounds(r);
			}
		}
	}

	protected void saveProperties() {
		if ((getExtendedState() & Frame.ICONIFIED) == 0)
			properties.put(HelpPane.WINDOW_STATE, "" + getExtendedState()); //$NON-NLS-1$
		if (getState() == Frame.NORMAL) {
			Rectangle r = getBounds();
			properties.put(HelpPane.POS_X, r.x);
			properties.put(HelpPane.POS_Y, r.y);
			properties.put(HelpPane.SIZE_WIDTH, r.width);
			properties.put(HelpPane.SIZE_HEIGHT, r.height);
		}
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(800, 600);
		this.setContentPane(getJContentPane());
		this.setTitle(Messages.getString("HelpWindow.0")); //$NON-NLS-1$
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

	public void setUrl(URL url) {
		getHelpPane().setUrl(url);
	}

}
