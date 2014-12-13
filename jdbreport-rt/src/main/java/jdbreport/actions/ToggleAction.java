/*
 * ToggleAction.java
 *
 * Copyright (C) 2007-2014 Andrey Kholmanskih
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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrey Kholmanskih
 * @version 3.0 12.12.2014
 */
public abstract class ToggleAction extends BaseAction {

    private static final long serialVersionUID = 1L;

    private boolean selected;
    private List<ButtonModel> buttonModels = new ArrayList<>();

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

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        boolean oldValue = this.selected;

        if (oldValue != selected) {
            this.selected = selected;
            for (ButtonModel buttonModel : buttonModels) {
                buttonModel.setSelected(selected);
            }
            firePropertyChange("selected", oldValue, selected);
        }
    }

    public void setEnabled(boolean newValue) {
        boolean oldValue = this.enabled;

        if (oldValue != newValue) {
            super.setEnabled(newValue);
            for (ButtonModel buttonModel : buttonModels) {
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
