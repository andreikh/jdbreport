/*
 * Created on 30.06.2005
 *
 * 
 * Copyright (C) 2005-2010 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.actions;

import jdbreport.util.Resources;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @version 1.2 18.04.2010
 * @author Andrey Kholmanskih
 * 
 */
public abstract class BaseAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	private String delimiter = "_";

	private ActionListener actionListener;

	public BaseAction(String name) {
		this(name, "_", null);
	}
	
	public BaseAction(String name, ActionListener l) {
		this(name, "_", l);
	}

	public BaseAction(String name, String delimiter) {
		this(name, delimiter, null);
	}
	
	public BaseAction(String name, String delimiter, ActionListener l) {
		super(name);
		this.actionListener = l;
		this.delimiter = delimiter;
		String shortDescr = getResourceValue(name, Action.SHORT_DESCRIPTION);
		if (shortDescr != null) {
			putValue(Action.SHORT_DESCRIPTION, shortDescr);
		}

		String longDescr = getResourceValue(name, Action.LONG_DESCRIPTION);
		if (longDescr == null || longDescr.length() == 0)
			longDescr = shortDescr;
		if (longDescr != null)
			putValue(Action.LONG_DESCRIPTION, longDescr);

		String actionKey = getResourceValue(name, Action.ACTION_COMMAND_KEY);
		if (actionKey == null || actionKey.length() == 0)
			actionKey = name;
		putValue(Action.ACTION_COMMAND_KEY, actionKey);

		setStringValue(name, Action.NAME);
		setKeyStrokeValue(name, Action.ACCELERATOR_KEY);
		setIntValue(name, Action.MNEMONIC_KEY);

		String iconName = getResourceValue(name, Action.SMALL_ICON);
		if (iconName != null && iconName.length() > 0)
				putValue(Action.SMALL_ICON, getResource().getIcon(iconName));

	}

	protected String getResourceValue(String name, String key) {
		try {
			return getResource().getString(name + this.delimiter + key);
		} catch (Exception e) {
			return null;
		}
	}

	protected void setStringValue(String name, String key) {
		String value = getResourceValue(name, key);
		if (value != null && value.length() > 0)
			putValue(key, value);
	}

	protected void setIntValue(String name, String key) {
		String value = getResourceValue(name, key);
		if (value != null && value.length() > 0) {
				putValue(key, new Integer(value));
		}
	}

	protected void setKeyStrokeValue(String name, String key) {
		String value = getResourceValue(name, key);
		if (value != null && value.length() > 0) {
			putValue(key, KeyStroke.getKeyStroke(value));
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (actionListener != null) {
			actionListener.actionPerformed(e);
		}
	}

	public ActionListener getActionListener() {
		return actionListener;
	}

	public void setActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}

	public abstract Resources getResource();
}
