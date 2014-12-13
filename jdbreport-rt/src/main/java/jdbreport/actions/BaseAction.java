/*
 * Created on 30.06.2005
 *
 * 
 * Copyright (C) 2005-2014 Andrey Kholmanskih
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
