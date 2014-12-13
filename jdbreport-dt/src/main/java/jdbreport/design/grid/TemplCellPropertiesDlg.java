/*
 * TemplCellPropertiesDlg.java
 *
 * JDBReport Designer
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

package jdbreport.design.grid;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;

import javax.swing.JPanel;

import jdbreport.design.model.CellObject;
import jdbreport.design.model.TemplateModel;
import jdbreport.grid.CellPropertiesDlg;
import jdbreport.grid.JReportGrid;

/**
 * @version 1.4 07.04.2010
 * @author Andrey Kholmanskih
 * 
 */
public class TemplCellPropertiesDlg extends CellPropertiesDlg {

	private static final long serialVersionUID = 1L;

	private CellDataPanel dataPanel;

	public TemplCellPropertiesDlg(Frame owner, JReportGrid grid)
			throws HeadlessException {
		super(owner, grid);
	}

	public TemplCellPropertiesDlg(Dialog owner, JReportGrid grid)
			throws HeadlessException {
		super(owner, grid);
	}

	public TemplCellPropertiesDlg(Frame parent, boolean modal) {
		super(parent, modal);
	}

	@Override
	protected void addTabs() {
		super.addTabs();
		CellObject cell = null;
		if (getCells() != null && getCells().size() == 1) {
			cell = (CellObject) getCells().get(0);
			addPanel(
					Messages.getString("TemplCellPropertiesDlg.data"), getDataPanel(cell), Messages.getString("TemplCellPropertiesDlg.datatip")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	protected JPanel getDataPanel(CellObject cell) {
		if (dataPanel == null) {
			dataPanel = new CellDataPanel((TemplateModel) getModel(), cell);
		} else
			dataPanel.setCell(cell);
		return dataPanel;
	}

	@Override
	protected void updateComponents() {
		super.updateComponents();
		if (getCells().size() == 1) {
			CellObject cell = (CellObject) getCells().get(0);
			int index = getTabbedPane().indexOfComponent(getDataPanel(cell));
			if (index < 0) {
				addPanel(
						Messages.getString("TemplCellPropertiesDlg.data"), dataPanel, Messages.getString("TemplCellPropertiesDlg.datatip")); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else {
			if (dataPanel != null) {
				int index = getTabbedPane().indexOfComponent(dataPanel);
				if (index >= 0)
					getTabbedPane().remove(index);
			}
		}
	}

	protected void save() {
		super.save();
		if (dataPanel != null) {
			dataPanel.apply();
		}
	}
}
