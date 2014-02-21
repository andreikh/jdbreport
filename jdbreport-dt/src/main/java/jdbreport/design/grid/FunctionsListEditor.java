/*
 * JDBReport Designer
 * 
 * Copyright (C) 2006-2011 Andrey Kholmanskih. All rights reserved.
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

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.ImageIcon;

import and.properties.XMLProperties;


import javax.swing.JTextField;

import jdbreport.design.grid.undo.FunctionUndo;
import jdbreport.design.model.CellFunctionObject;
import jdbreport.design.model.CellObject;
import jdbreport.grid.UndoEvent;
import jdbreport.grid.UndoListener;
import jdbreport.grid.undo.UndoItem;
import jdbreport.util.Utils;

/**
 * @version 2.0 13.03.2011
 * @author Andrey Kholmanskih
 *
 */
public class FunctionsListEditor extends JDialog {

	private static final long serialVersionUID = 1L;
	public static final int SELECT = 1;
	public static final int REMOVE = 2;

	private JPanel jContentPane = null;

	private JPanel bottomPanel = null;

	private JToolBar jToolBar = null;

	private JList functionsJList = null;

	private JButton okButton = null;

	private JButton cancelButton = null;

	private JButton addButton = null;

	private JButton editButton = null;

	private JButton delButton = null;

	private Map<String, CellFunctionObject> functionList;

	private String functionName;

	private JTextField functionField = null;

	private CellObject selectedCell;

	private JButton removeButton;

	private JScrollPane scrollPane;

	private XMLProperties properties;

	private int result;
	private UndoListener undoListener;
	
	public FunctionsListEditor(Frame owner, Map<String, CellFunctionObject> functionList, XMLProperties properties) throws HeadlessException {
		super(owner, true);
		this.functionList = functionList;
		this.properties = properties;
		initialize();
	}

	public FunctionsListEditor(Dialog owner, Map<String, CellFunctionObject> functionList, XMLProperties properties) throws HeadlessException {
		super(owner, true);
		this.functionList = functionList;
		this.properties = properties;
		initialize();
	}
	
	private XMLProperties getProperties() {
		return properties;
	}
	/**
	 * This is the default constructor
	 */
	public FunctionsListEditor() {
		super();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setTitle(Messages.getString("FunctionsListEditor.title")); //$NON-NLS-1$
		this.setContentPane(getJContentPane());
		Utils.screenCenter(this, getOwner());
	}

	public void addUndoListener(UndoListener l) {
		undoListener = l;
}

public void removeUndoListener(UndoListener l) {
	if (undoListener == l)
		undoListener = null;
}

protected void pushUndo(UndoItem undo) {
	if (undoListener != null) {
		undoListener.pushUndo(new UndoEvent(this, undo));
	}
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
			jContentPane.add(getBottomPanel(), java.awt.BorderLayout.SOUTH);
			jContentPane.add(getJToolBar(), java.awt.BorderLayout.EAST);
			jContentPane.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
			jContentPane.add(getFunctionField(), java.awt.BorderLayout.NORTH);
		}
		return jContentPane;
	}

	private JScrollPane getJScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getFunctionsList());
		}
		return scrollPane;
	}

	/**
	 * This method initializes bottomPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new JPanel();
			bottomPanel.add(getOkButton(), null);
			bottomPanel.add(getRemoveButton(), null);
			bottomPanel.add(getCancelButton(), null);
		}
		return bottomPanel;
	}

	/**
	 * This method initializes jToolBar
	 * 
	 * @return javax.swing.JToolBar
	 */
	private JToolBar getJToolBar() {
		if (jToolBar == null) {
			jToolBar = new JToolBar();
			jToolBar.setFloatable(false);
			jToolBar.setOrientation(javax.swing.JToolBar.VERTICAL);
			jToolBar.add(getAddButton());
			jToolBar.add(getEditButton());
			jToolBar.add(getDelButton());
		}
		return jToolBar;
	}

	/**
	 * This method initializes functionsList
	 * 
	 * @return javax.swing.JList
	 */
	private JList getFunctionsList() {
		if (functionsJList == null) {
			functionsJList = new JList();
			functionsJList
					.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
						public void valueChanged(javax.swing.event.ListSelectionEvent e) {
							getFunctionField().setText(functionsJList.getSelectedValue() != null ? functionsJList.getSelectedValue().toString() : ""); //$NON-NLS-1$
							updateButtons();
						}
					});
			functionsJList.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if (e.getClickCount() > 1) {
						if (getOkButton().isEnabled()) 
							select();
					} else {
						getFunctionField().setText(functionsJList.getSelectedValue() != null ? functionsJList.getSelectedValue().toString() : ""); //$NON-NLS-1$
						updateButtons();
					}
				}
			});
			functionsJList.setListData(functionList.keySet().toArray());
		}
		return functionsJList;
	}

	/**
	 * This method initializes okButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText(Messages.getString("FunctionsListEditor.ok_text")); //$NON-NLS-1$
			okButton.setToolTipText(Messages.getString("FunctionsListEditor.ok_tooltip")); //$NON-NLS-1$
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					select();
				}
			});
		}
		return okButton;
	}

	/**
	 * This method initializes removeButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setText(Messages.getString("FunctionsListEditor.remove_text")); //$NON-NLS-1$
			removeButton.setToolTipText(Messages.getString("FunctionsListEditor.remove_tooltip")); //$NON-NLS-1$
			removeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					remove();
				}

			});
		}
		return removeButton;
	}
	
	/**
	 * This method initializes cancelButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText(Messages.getString("FunctionsListEditor.close_text")); //$NON-NLS-1$
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setVisible(false);
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes addButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setIcon(new ImageIcon(getClass().getResource(
					"/jdbreport/resources/add.gif"))); //$NON-NLS-1$
			addButton.setToolTipText(Messages.getString("FunctionsListEditor.add_tooltip")); //$NON-NLS-1$
			addButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					newFunction();
				}
			});
		}
		return addButton;
	}

	private boolean existsName(String name) {
		return functionList.containsKey(name);
	}

	protected String newFunctionName() {
		String name = Messages.getString("FunctionsListEditor.function"); //$NON-NLS-1$
		int n = 1;

		while (existsName(name + n)) {
			n++;
		}
		return name + n;
	}

	/**
	 * This method initializes editButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getEditButton() {
		if (editButton == null) {
			editButton = new JButton();
			editButton.setToolTipText(Messages.getString("FunctionsListEditor.edit_tooltip")); //$NON-NLS-1$
			editButton.setIcon(new ImageIcon(getClass().getResource(
					"/jdbreport/resources/edit_item.gif"))); //$NON-NLS-1$
			editButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					editFunction();
				}
			});
		}
		return editButton;
	}

	/**
	 * This method initializes delButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getDelButton() {
		if (delButton == null) {
			delButton = new JButton();
			delButton.setIcon(new ImageIcon(getClass().getResource(
					"/jdbreport/resources/del.gif"))); //$NON-NLS-1$
			delButton.setToolTipText(Messages.getString("FunctionsListEditor.delete_tooltip")); //$NON-NLS-1$
			delButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					deleteFunction();
				}
			});
		}
		return delButton;
	}

	protected void deleteFunction() {
		String key = (String) getFunctionsList().getSelectedValue();
		if (key != null) {
			CellFunctionObject cellFunction = (CellFunctionObject) functionList.remove(key);
			getFunctionsList().setListData(functionList.keySet().toArray());
			updateButtons();
			pushUndo(new FunctionUndo(functionList, cellFunction, null));
		}

	}

	private void newFunction() {
		CellFunctionObject cellFunction = new CellFunctionObject(
				newFunctionName());
		CellFunctionEditor cellFunctionEditor = new CellFunctionEditor(this, cellFunction, getProperties());
		cellFunctionEditor.setVisible(true);
		if (cellFunctionEditor.isOk()) {
			saveFunction(cellFunction);
			getFunctionsList().setSelectedValue(
					cellFunction.getFunctionName(), true);
			pushUndo(new FunctionUndo(functionList, null, cellFunction));
		}
	}

	/**
	 * @param cellFunction
	 */
	private void saveFunction(CellFunctionObject cellFunction) {
		functionList.put(cellFunction.getFunctionName(), cellFunction);
		getFunctionsList().setListData(functionList.keySet().toArray());
	}	
	
	private void editFunction() {
		String key = (String) getFunctionsList().getSelectedValue();
		if (key == null) return;
		CellFunctionObject cellFunction = (CellFunctionObject) functionList.get(key);
		if (cellFunction == null)
			return;
		CellFunctionObject old = (CellFunctionObject) cellFunction.clone(); 
		CellFunctionEditor cellFunctionEditor = new CellFunctionEditor(this, cellFunction, getProperties());
		cellFunctionEditor.setVisible(true);
		if (cellFunctionEditor.isOk()) {
			functionList.remove(key);
			saveFunction(cellFunction);
			pushUndo(new FunctionUndo(functionList, old, cellFunction));
		}
		getFunctionsList().setSelectedValue(
				cellFunction.getFunctionName(), true);
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setVisible(boolean visible) {
		if (visible) {
			functionName = null;
		}
		super.setVisible(visible);
	}

	/**
	 * This method initializes functionField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getFunctionField() {
		if (functionField == null) {
			functionField = new JTextField();
			functionField.setEditable(false);
		}
		return functionField;
	}

	/**
	 * @param selectedCell The selectedCell to set.
	 */
	public void setSelectedFunction(CellObject selectedCell) {
		result = 0;
		this.selectedCell = selectedCell;
		String selectedFunction = selectedCell.getFunctionName();
		getFunctionsList().setListData(functionList.keySet().toArray());
		getFunctionsList().setSelectedValue(selectedFunction, true);
		getFunctionField().setText(selectedFunction == null ? "" : selectedFunction); //$NON-NLS-1$
		updateButtons();
	}

	private void updateButtons() {
		getOkButton().setEnabled(selectedCell != null && getFunctionsList().getSelectedIndex() >= 0);
		getRemoveButton().setEnabled(selectedCell != null && selectedCell.getFunctionName() != null);
	}

	/**
	 * @return Returns the selected function.
	 */
	public String getSelectedFunction() {
		Object result = getFunctionsList().getSelectedValue();
		return  result!=null ? result.toString() : null;
	}

	private void select() {
		functionName = (String) getFunctionsList().getSelectedValue();
		if (functionName != null) {
			result = SELECT;
			setVisible(false);
		}
	}

	private void remove() {
		functionName = null;
		result = REMOVE;
		setVisible(false);
	}

	public int result() {
		return result;
	}

	
}
