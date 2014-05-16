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
