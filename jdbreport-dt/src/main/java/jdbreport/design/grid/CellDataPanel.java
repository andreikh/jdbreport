/*
 * CellDataPanel.java
 *
 * Created on 01.11.2006, 18:54
 * 
 * JDBReport Designer
 * 
 * Copyright (C) 2006-2012 Andrey Kholmanskih. All rights reserved.
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
 * 
 */

package jdbreport.design.grid;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import jdbreport.design.model.CellObject;
import jdbreport.design.model.TemplateModel;
import jdbreport.source.JdbcReportSource;

/**
 * @version 2.0 18.04.2012
 * 
 * @author Andrey Kholmanskih
 */
public class CellDataPanel extends javax.swing.JPanel {

	private static final String EMPTY_STRING = "";
	private static final long serialVersionUID = 1L;
	private TemplateModel model;
	private CellObject cell;
	private boolean varChange;
	private boolean dataSetChange;
	private boolean functionChange;
	private boolean fieldChange;

	public CellDataPanel(TemplateModel model, CellObject cell) {
		initComponents();
		this.model = model;
		setCell(cell);
	}

	public void setCell(CellObject cell) {
		this.cell = cell;
		if (cell != null) {
			initValues();
		}
		
		varChange = false;
		dataSetChange = false;
		fieldChange = false;
		functionChange = false;
	}

	private void initValues() {
		varBox.removeAllItems();
		varBox.addItem(EMPTY_STRING); //$NON-NLS-1$
		for (Object var : model.getVars().keySet()) {
			varBox.addItem(var);
		}
		dataSetBox.removeAllItems();
		dataSetBox.addItem(EMPTY_STRING); //$NON-NLS-1$
		for (int i = 0; i < model.getSourcesList().size(); i++) {
			JdbcReportSource source = (JdbcReportSource) model.getSourcesList()
					.get(i);
			for (int n = 0; n < source.getDataSetCount(); n++) {
				dataSetBox.addItem(source.getDataSet(n).getId());
			}
		}

		fieldBox.removeAllItems();
		fieldBox.addItem(EMPTY_STRING); //$NON-NLS-1$

		functionsBox.removeAllItems();
		functionsBox.addItem(EMPTY_STRING); //$NON-NLS-1$
		for (String name : model.getFunctionsList().keySet()) {
			functionsBox.addItem(name);
		}

		varButton.setSelected(false);
		fieldButton.setSelected(false);

		if (cell.getType() == CellObject.TYPE_VAR) {
			updateBox(true);
			varBox.setSelectedItem(cell.getValue());
			varButton.setSelected(true);
		} else if (cell.getType() == CellObject.TYPE_FIELD) {
			fieldButton.setSelected(true);
			updateBox(false);
			dataSetBox.setSelectedItem(cell.getDataSetId());
			fieldBox.setSelectedItem(cell.getValue());
		}
		functionsBox.setSelectedItem(cell.getFunctionName());
	}

	private void initComponents() {
		javax.swing.ButtonGroup buttonGroup1;
		java.awt.GridBagConstraints gridBagConstraints;

		buttonGroup1 = new javax.swing.ButtonGroup();
		varButton = new javax.swing.JRadioButton();
		fieldButton = new javax.swing.JRadioButton();
		varBox = new javax.swing.JComboBox();
		varBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				varChange = true;
			}

		});

		fieldBox = new javax.swing.JComboBox();
		fieldBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (fieldBox.getItemCount() > 0) {
					fieldChange = true;
				}
			}

		});

		functionsBox = new javax.swing.JComboBox();
		functionsBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				functionChange = true;
			}

		});

		dataSetBox = new javax.swing.JComboBox();
		dataSetBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dataSetChange = true;
			}

		});

		setLayout(new java.awt.GridBagLayout());

		buttonGroup1.add(varButton);
		varButton.setText(Messages.getString("CellDataPanel.4")); //$NON-NLS-1$
		varButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0,
				0, 0));
		varButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		varButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				varButtonActionPerformed(evt);
			}
		});


		buttonGroup1.add(fieldButton);
		fieldButton.setText(Messages.getString("CellDataPanel.5")); //$NON-NLS-1$
		fieldButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0,
				0, 0));
		fieldButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		fieldButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				fieldButtonActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 4, 4);
		add(varButton, gridBagConstraints);

		varBox.setEnabled(false);
		gridBagConstraints.gridx = 1;
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 2, 4, 20);
		add(varBox, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 4);
		add(fieldButton, gridBagConstraints);

		dataSetBox.setEditable(true);
		dataSetBox.setEnabled(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 20);
		add(dataSetBox, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(4, 10, 36, 4);
		add(new JLabel(Messages.getString("CellDataPanel.19")), gridBagConstraints);//$NON-NLS-1$

		fieldBox.setEditable(true);
		fieldBox.setEnabled(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new java.awt.Insets(4, 2, 36, 20);
		add(fieldBox, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 4);
		add(new JLabel(Messages.getString("CellDataPanel.14")), gridBagConstraints); //$NON-NLS-1$

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 20);
		add(functionsBox, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 0.1;
		gridBagConstraints.weighty = 0.1;
		add(new JLabel(), gridBagConstraints);

	}

	private void fieldButtonActionPerformed(java.awt.event.ActionEvent evt) {
		updateBox(false);
	}

	private void varButtonActionPerformed(java.awt.event.ActionEvent evt) {
		updateBox(true);
	}

	private void updateBox(boolean isvar) {
		varBox.setEnabled(isvar);
		fieldBox.setEnabled(!isvar);
		dataSetBox.setEnabled(!isvar);
	}

	public void apply() {
		if (varButton.isSelected()) {
			if (varChange && varBox.getSelectedItem() != null
					&& varBox.getSelectedItem().toString().length() > 0) {
				cell.setValue(varBox.getSelectedItem());
				cell.setType(CellObject.TYPE_VAR);
			}
			cell.setDataSetId(null);
		} else {
			if (fieldButton.isSelected()) {
				if (fieldChange) {
					if (fieldBox.getSelectedItem() != null
							&& fieldBox.getSelectedItem().toString().length() > 0) {
						cell.setValue(fieldBox.getSelectedItem());
					}
				}
				if (dataSetChange) {
					cell.setDataSetId((String) dataSetBox.getSelectedItem());
				}
			}
		}
		if (functionChange) {
			if (functionsBox.getSelectedItem() != null) {
				cell.setFunctionName(functionsBox.getSelectedItem().toString());
			} else {
				cell.setFunctionName(null);
			}
		}
	}

	private javax.swing.JComboBox dataSetBox;
	private javax.swing.JComboBox fieldBox;
	private javax.swing.JRadioButton fieldButton;
	private javax.swing.JComboBox functionsBox;
	private javax.swing.JComboBox varBox;
	private javax.swing.JRadioButton varButton;

}
