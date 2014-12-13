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
package jdbreport.grid;

import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;

import jdbreport.model.Cell;

/**
 * @version 2.2 14.04.2013
 * @author Andrey Kholmanskih
 * 
 */
public class CellPropertiesPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JCheckBox noPrintBox = null;

	private JCheckBox noEditBox = null;
	private List<Cell> cells;

	private JTextField formulaField;

	/**
	 * This is the default constructor
	 */
	public CellPropertiesPanel() {
		super();
		initialize();
	}

	public void setCells(List<Cell> cells) {
		this.cells = cells;
		if (cells != null)
			initValues();
	}

	private void initialize() {
		this.setSize(300, 200);
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[]{100, 100};
		this.setLayout(layout);
		
		GridBagConstraints constr = new GridBagConstraints();
		constr.insets = new Insets(16, 16, 4, 4);
		constr.weightx = 0.1;
		constr.anchor = GridBagConstraints.NORTHWEST;
		constr.gridy = 0;
		constr.gridwidth = 2;
		this.add(getNoPrintBox(), constr);
		
		constr = new GridBagConstraints();
		constr.anchor = GridBagConstraints.NORTHWEST;
		constr.insets = new Insets(4, 16, 4, 4);
		constr.gridy = 1;
		constr.gridwidth = 2;
		this.add(getNoEditBox(), constr);
		
		constr = new GridBagConstraints();
		constr.anchor = GridBagConstraints.NORTHWEST;
		constr.insets = new Insets(4, 16, 4, 2);
		constr.gridy = 2;
		this.add(new JLabel(Messages.getString("CellPropertiesPanel.formula")), constr); //$NON-NLS-1$

		constr = new GridBagConstraints();
		constr.anchor = GridBagConstraints.NORTHWEST;
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.weighty = 0.1;
		constr.weightx = 0.1;
		constr.insets = new Insets(2, 2, 2, 4);
		constr.gridx = 1;
		constr.gridy = 2;
		this.add(getFormulaField(), constr);
	}

	private void initValues() {
		noPrintBox.setSelected(cells.get(0).isNotPrint());
		noEditBox.setSelected(!cells.get(0).isEditable());
		getFormulaField().setText(cells.get(0).getCellFormula());
	}

	private JTextField getFormulaField() {
		if (formulaField == null) {
			formulaField = new JTextField();
		}
		return formulaField;
	}

	/**
	 * This method initializes noPrintBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getNoPrintBox() {
		if (noPrintBox == null) {
			noPrintBox = new JCheckBox();
			noPrintBox.setText(Messages
					.getString("CellPropertiesPanel.notprint")); //$NON-NLS-1$
		}
		return noPrintBox;
	}

	/**
	 * This method initializes noEditBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getNoEditBox() {
		if (noEditBox == null) {
			noEditBox = new JCheckBox();
			noEditBox
					.setText(Messages.getString("CellPropertiesPanel.notedit")); //$NON-NLS-1$
		}
		return noEditBox;
	}

	public void apply() {
		for (Cell cell : cells) {
			cell.setNotPrint(noPrintBox.isSelected());
			cell.setEditable(!noEditBox.isSelected());
			cell.setCellFormula(getFormulaField().getText());
		}
	}
	
}
