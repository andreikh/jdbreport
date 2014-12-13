/*
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JPanel;

import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;

import jdbreport.design.grid.undo.VarUndo;
import jdbreport.design.grid.undo.VarValueUndo;
import jdbreport.design.model.SystemVar;
import jdbreport.grid.ReportResources;
import jdbreport.grid.UndoEvent;
import jdbreport.grid.UndoListener;
import jdbreport.grid.undo.UndoItem;
import jdbreport.util.Utils;

/**
 * @version 2.0 13.03.2011
 * @author Andrey Kholmanskih
 * 
 */
public class VarList extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private JToolBar jToolBar = null;
	private JButton addButton = null;
	private JButton delButton = null;
	private JTable varsTable = null;
	private Map<Object, Object> vars;
	private ArrayList<Object> list;
	private VarsModel varsModel;
	private UndoListener undoListener;

	public VarList(Frame owner, Map<Object, Object> vars)
			throws HeadlessException {
		super(owner);
		this.vars = vars;
        for (String var : SystemVar.getNames()) {
            if (!vars.containsKey(var))
                vars.put(var, null);
        }
		initialize();
	}

	public VarList(Dialog owner, Map<Object, Object> vars)
	throws HeadlessException {
		super(owner);
		this.vars = vars;
		initialize();
	}
	
	public Map<Object, Object> getVars() {
		return vars;
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(300, 260);
		this.setContentPane(getJContentPane());
		this.setTitle(Messages.getString("VarList.title")); //$NON-NLS-1$
		Utils.screenCenter(this, getOwner());
	}

	public void addUndoListener(UndoListener l) {
		undoListener = l;
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
			jContentPane.add(getJToolBar(), java.awt.BorderLayout.EAST);
			jContentPane.add(new JScrollPane(getVarsTable()),
					java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jToolBar
	 * 
	 * @return javax.swing.JToolBar
	 */
	private JToolBar getJToolBar() {
		if (jToolBar == null) {
			jToolBar = new JToolBar();
			jToolBar.setOrientation(javax.swing.JToolBar.VERTICAL);
			jToolBar.setFloatable(false);
			jToolBar.add(getAddButton());
			jToolBar.add(getDelButton());
		}
		return jToolBar;
	}

	/**
	 * This method initializes addButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setIcon(ReportResources.getInstance().getIcon("add.gif")); //$NON-NLS-1$
			addButton.setToolTipText(Messages.getString("VarList.ad_tooltip")); //$NON-NLS-1$
			addButton.addActionListener(e -> {
                String newvar = "new_var";
                if (vars.containsKey(newvar)) {
                    int i = 1;
                    while (vars.containsKey(newvar + i)) {
                        i++;
                    }
                    newvar = newvar + i;
                }
                vars.put(newvar, null);
                varsModel.updateVarList();
                varsTable.revalidate();
                pushUndo(new VarUndo(VarList.this, null, newvar));
            });
		}
		return addButton;
	}

	/**
	 * This method initializes delButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getDelButton() {
		if (delButton == null) {
			delButton = new JButton();
			delButton.setIcon(ReportResources.getInstance().getIcon("del.gif")); //$NON-NLS-1$
			delButton.setToolTipText(Messages
					.getString("VarList.delete_tooltip")); //$NON-NLS-1$
			delButton.addActionListener(e -> {
                int row = varsTable.getSelectedRow();
                if (row >= 0) {
                    Object old = list.remove(row);
                    vars.remove(old);
                    varsModel.updateVarList();
                    varsTable.revalidate();
                    varsTable.repaint();
                    pushUndo(new VarUndo(VarList.this, old, null));
                }
            });
		}
		return delButton;
	}

	/**
	 * This method initializes varsTable
	 * 
	 * @return javax.swing.JTree
	 */
	private JTable getVarsTable() {
		if (varsTable == null) {
			varsTable = new JTable() {

				private static final long serialVersionUID = 1L;

				public Component prepareEditor(TableCellEditor editor, int row,
						int column) {
					Component comp = super.prepareEditor(editor, row, column);
					comp.setFont(getFont());
					if (comp instanceof JTextComponent) {
						((JTextComponent) comp).selectAll();
					}
					return comp;
				}
			};
			varsModel = new VarsModel();
			varsTable.setModel(varsModel);
		}
		return varsTable;
	}

	public void updateVarList() {
		varsModel.updateVarList();
		varsTable.revalidate();
		varsTable.repaint();
	}

    private boolean isSystemVar(Object varName) {
        return SystemVar.find(varName.toString()) != null;
    }

	private class VarsModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;

		public VarsModel() {
			updateVarList();
		}

		public void updateVarList() {
			list = new ArrayList<>();
            for (Object o : vars.keySet()) {
                list.add(o);
            }
		}

		public int getRowCount() {
			return list != null ? list.size() : 0;
		}

		public int getColumnCount() {
			return 2;
		}

		public String getColumnName(int columnIndex) {
			if (columnIndex == 0) {
				return Messages.getString("VarList.column_name"); //$NON-NLS-1$
			} else {
				return Messages.getString("VarList.column_value"); //$NON-NLS-1$
			}
		}

		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
            return !isSystemVar(list.get(rowIndex));
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return list.get(rowIndex);
			} else {
				return vars.get(list.get(rowIndex));
			}
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				Object old = list.set(rowIndex, aValue);
				Object value = vars.get(old);
				vars.remove(old);
				vars.put(aValue, value);
				if ((old != null && !old.equals(aValue))
						|| (aValue != null && !aValue.equals(old))) {
					pushUndo(new VarUndo(VarList.this, old, aValue));
				}
			} else {
				Object old = vars.put(list.get(rowIndex), aValue);
				if ((old != null && !old.equals(aValue))
						|| (aValue != null && !aValue.equals(old))) {
					pushUndo(new VarValueUndo(VarList.this, list.get(rowIndex),
							old));
				}
			}
		}

	}

}
