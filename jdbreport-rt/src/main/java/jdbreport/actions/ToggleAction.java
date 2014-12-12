/*
 * ToggleAction.java
 *
 * Copyright (C) 2007-2010 Andrey Kholmanskih. All rights reserved.
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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public abstract class ToggleAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	
	private boolean selected;
	private List<ButtonModel> buttonModels = new ArrayList<ButtonModel>();

	public ToggleAction(String name, ActionListener l) {
		super(name, l);
	}

	public ToggleAction(String name, String delimiter, ActionListener l) {
		super(name, delimiter, l);
	}

	public ToggleAction(String name) {
		super(name);
	}

	public ToggleAction(String name, String delimiter) {
		super(name, delimiter);
	}

	public AbstractButton addButton(AbstractButton button) {
		button.setAction(this);
		addButtonModel(button.getModel());
		return button;
	}

	public void addButtonModel(ButtonModel model) {
		if (buttonModels.indexOf(model) < 0)
			buttonModels.add(model);
	}

	public void removeButtonModel(ButtonModel model) {
		buttonModels.remove(model);
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(boolean selected) {
		boolean oldValue = this.selected;

		if (oldValue != selected) {
			this.selected = selected;
			for (int i = 0; i < buttonModels.size(); i++) {
				ButtonModel buttonModel = buttonModels.get(i);
				buttonModel.setSelected(selected);
			}
			firePropertyChange("selected", Boolean.valueOf(oldValue), Boolean
					.valueOf(selected));
		}
	}

	public void setEnabled(boolean newValue) {
		boolean oldValue = this.enabled;

		if (oldValue != newValue) {
			super.setEnabled(newValue);
			for (int i = 0; i < buttonModels.size(); i++) {
				ButtonModel buttonModel = buttonModels.get(i);
				buttonModel.setEnabled(newValue);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		setSelected(!selected);
	}

}
