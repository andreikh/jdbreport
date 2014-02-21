/*
 * TemplCellPropertiesDlg.java
 *
 * JDBReport Designer
 * 
 * Copyright (C) 2006-2010 Andrey Kholmanskih. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, write to the 
 * 
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
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
