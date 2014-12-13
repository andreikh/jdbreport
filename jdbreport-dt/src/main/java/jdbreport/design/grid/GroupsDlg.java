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
import javax.swing.JPanel;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

import jdbreport.design.model.CellObject;
import jdbreport.design.model.GroupKey;
import jdbreport.grid.JReportGrid;
import jdbreport.grid.ReportResources;
import jdbreport.grid.UndoEvent;
import jdbreport.grid.UndoListener;
import jdbreport.grid.undo.AbstractGridUndo;
import jdbreport.grid.undo.UndoItem;
import jdbreport.model.DetailGroup;
import jdbreport.util.Utils;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class GroupsDlg extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel buttonPanel = null;
	private JTable groupFieldTable = null;
	private JToolBar jToolBar = null;
	private JButton addButton = null;
	private JButton delButton = null;
	private JButton okButton = null;
	private JButton cancelButton = null;
	private DetailGroup group;
	private KeyListModel keyListModel = null;
	private UndoListener undoListener;
	private JReportGrid grid;
	private JPanel bottomPanel;
	private JTextField minRowsField;
	private JTextField maxRowsField;
	private JCheckBox repeateHeaderBox;

	public GroupsDlg(Frame owner, JReportGrid grid, DetailGroup group)
			throws HeadlessException {
		super(owner, true);
		this.grid = grid;
		this.group = group;
		initialize();
	}

	public GroupsDlg(Dialog owner, JReportGrid grid, DetailGroup group)
			throws HeadlessException {
		super(owner, true);
		this.grid = grid;
		this.group = group;
		initialize();
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
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 300);
		Utils.screenCenter(this);
		this
				.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE); // Generated
		this.setTitle(Messages.getString("GroupsDlg.title"));
		this.setContentPane(getJContentPane());
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
			jContentPane.add(createKeyPanel(), java.awt.BorderLayout.CENTER);
			jContentPane.add(getJToolBar(), java.awt.BorderLayout.EAST);
			jContentPane
					.add(getRepeateHeaderBox(), java.awt.BorderLayout.NORTH);
			jContentPane.add(getBottomPanel(), java.awt.BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * To repeat a group header in top of page
	 * 
	 * @return
	 */
	private JCheckBox getRepeateHeaderBox() {
		if (repeateHeaderBox == null) {
			repeateHeaderBox = new JCheckBox(Messages
					.getString("GroupsDlg.repeateHeader")); //$NON-NLS-1$
			repeateHeaderBox.setSelected(group.isRepeateHeader());
			updateButtons();
			repeateHeaderBox.setActionCommand("repeateHeader");
			repeateHeaderBox.addActionListener(this);
		}
		return repeateHeaderBox;
	}

	private JPanel createKeyPanel() {
		JPanel keyPanel = new JPanel(new BorderLayout());
		keyPanel.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("GroupsDlg.keyTitle"))); //$NON-NLS-1$
		keyPanel
				.add(new JScrollPane(getGroupFieldTable()), BorderLayout.CENTER);
		return keyPanel;
	}

	private JPanel getBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new JPanel(new BorderLayout());
			bottomPanel.add(createRowLimitPanel(), BorderLayout.CENTER);
			bottomPanel.add(getButtonPanel(), BorderLayout.SOUTH);
		}
		return bottomPanel;
	}

	private JPanel createRowLimitPanel() {
		GridBagLayout layout = new GridBagLayout();
		JPanel rowLimitPanel = new JPanel(layout);
		rowLimitPanel.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("GroupsDlg.borderTitle"))); //$NON-NLS-1$

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(4, 8, 4, 4);
		constraints.anchor = GridBagConstraints.NORTHWEST;
		rowLimitPanel
				.add(
						new JLabel(Messages.getString("GroupsDlg.min_rows")), constraints); //$NON-NLS-1$

		constraints = new GridBagConstraints();
		constraints.insets = new Insets(4, 8, 4, 100);
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 1;
		rowLimitPanel.add(getMinRowsField(), constraints);

		constraints = new GridBagConstraints();
		constraints.insets = new Insets(4, 8, 4, 4);
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.gridy = 1;
		rowLimitPanel
				.add(
						new JLabel(Messages.getString("GroupsDlg.max_rows")), constraints); //$NON-NLS-1$

		constraints = new GridBagConstraints();
		constraints.insets = new Insets(4, 8, 4, 100);
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridy = 1;
		constraints.gridx = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		rowLimitPanel.add(getMaxRowsField(), constraints);

		return rowLimitPanel;
	}

	private JTextField getMinRowsField() {
		if (minRowsField == null) {
			minRowsField = new JTextField();
			minRowsField.setToolTipText(Messages
					.getString("GroupsDlg.min_rows_tooltip")); //$NON-NLS-1$
			minRowsField.setHorizontalAlignment(JTextField.RIGHT);
			minRowsField.addKeyListener(new NumericFieldListener());
			minRowsField.setText("" + group.getMinRowCount()); //$NON-NLS-1$
		}
		return minRowsField;
	}

	private JTextField getMaxRowsField() {
		if (maxRowsField == null) {
			maxRowsField = new JTextField();
			maxRowsField.setToolTipText(Messages
					.getString("GroupsDlg.max_rows_tooltip")); //$NON-NLS-1$
			maxRowsField.setHorizontalAlignment(JTextField.RIGHT);
			maxRowsField.addKeyListener(new NumericFieldListener());
			maxRowsField.setText("" + group.getMaxRowCount()); //$NON-NLS-1$
		}
		return maxRowsField;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getOkButton());
			buttonPanel.add(getCancelButton());
		}
		return buttonPanel;
	}

	/**
	 * This method initializes groupFieldTable
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getGroupFieldTable() {
		if (groupFieldTable == null) {
			groupFieldTable = new JTable();
			groupFieldTable.setModel(getKeyListModel());
			groupFieldTable.getColumnModel().getColumn(COLUMN_TYPE)
					.setPreferredWidth(20);
		}
		return groupFieldTable;
	}

	private KeyListModel getKeyListModel() {
		if (keyListModel == null) {
			keyListModel = new KeyListModel();
		}
		return keyListModel;
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
			addButton.setToolTipText(Messages
					.getString("GroupsDlg.add_tooltip")); //$NON-NLS-1$
			addButton.setActionCommand("addKey");
			addButton.addActionListener(this);
		}
		return addButton;
	}

	private void addKey() {
		getKeyListModel().addNewKey();
	}

	/**
	 * This method initializes delButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getDelButton() {
		if (delButton == null) {
			delButton = new JButton();
			delButton.setToolTipText(Messages
					.getString("GroupsDlg.delete_tooltip")); //$NON-NLS-1$
			delButton.setIcon(ReportResources.getInstance().getIcon("del.gif")); //$NON-NLS-1$
			delButton.setActionCommand("removeKey");
			delButton.addActionListener(this);
		}
		return delButton;
	}

	protected void deleteKey() {
		getKeyListModel().removeRow(getGroupFieldTable().getSelectedRow());
	}

	/**
	 * This method initializes okButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText(Messages.getString("GroupsDlg.ok_text")); //$NON-NLS-1$
			okButton.setActionCommand("ok");
			okButton.addActionListener(this);
		}
		return okButton;
	}

	protected void apply() {
		if (undoListener != null) {
			undoListener.pushUndo(new UndoEvent(this, new GroupUndoItem(grid,
					group)));
		}

		group.setRepeateHeader(getRepeateHeaderBox().isSelected());
		group.clearKeys();
		if (!group.isRepeateHeader()) {
			for (int i = 0; i < getKeyListModel().getRowCount(); i++) {
				group.addKey(getKeyListModel().getKey(i));
			}
		}

		String s = getMinRowsField().getText().trim();
		group.setMinRowCount(s.length() > 0 ? Integer.parseInt(s) : 0);

		s = getMaxRowsField().getText().trim();
		group.setMaxRowCount(s.length() > 0 ? Integer.parseInt(s) : 0);
	}

	/**
	 * This method initializes cancelButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText(Messages.getString("GroupsDlg.cancel_text")); //$NON-NLS-1$
			cancelButton.setActionCommand("cancel");
			cancelButton.addActionListener(this);
		}
		return cancelButton;
	}

	public void actionPerformed(ActionEvent e) {
		if ("addKey".equals(e.getActionCommand())) {
			addKey();
		} else if ("removeKey".equals(e.getActionCommand())) {
			deleteKey();
		} else if ("repeateHeader".equals(e.getActionCommand())) {
			updateButtons();
		} else if ("cancel".equals(e.getActionCommand())) {
			setVisible(false);
		} else if ("ok".equals(e.getActionCommand())) {
			apply();
			setVisible(false);
		}

	}

	private void updateButtons() {
		boolean repeateHeader = getRepeateHeaderBox().isSelected();
		getAddButton().setEnabled(!repeateHeader);
		getDelButton().setEnabled(!repeateHeader);
	}

	private static final int COLUMN_NAME = 0;
	private static final int COLUMN_TABLE = 1;
	private static final int COLUMN_TYPE = 2;

	private class KeyListModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		List<GroupKey> list = null;

		public KeyListModel() {
			super();
			for (int i = 0; i < group.getKeyCount(); i++) {
				getList().add((GroupKey) group.getKey(i).clone());
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case COLUMN_NAME:
				return String.class;
			case COLUMN_TABLE:
				return String.class;
			case COLUMN_TYPE:
				return String.class;
			}
			return Object.class;
		}

		public GroupKey getKey(int i) {
			return getList().get(i);
		}

		public void addNewKey() {
			getList().add(new GroupKey(Messages.getString("GroupsDlg.newkey"))); //$NON-NLS-1$
			fireTableRowsInserted(getList().size() - 2, getList().size() - 1);
		}

		public void removeRow(int row) {
			getList().remove(row);
			fireTableRowsDeleted(row, row);
		}

		public int getColumnCount() {
			return 3;
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {
			case COLUMN_NAME:
				return Messages.getString("GroupsDlg.column_name"); //$NON-NLS-1$
			case COLUMN_TABLE:
				return Messages.getString("GroupsDlg.column_dataset"); //$NON-NLS-1$
			case COLUMN_TYPE:
				return Messages.getString("GroupsDlg.column_type"); //$NON-NLS-1$
			}
			return null;
		}

		public int getRowCount() {
			return getList().size();
		}

		private List<GroupKey> getList() {
			if (list == null) {
				list = new ArrayList<>();
			}
			return list;
		}

		public Object getValueAt(int row, int column) {
			switch (column) {
			case COLUMN_NAME:
				return getList().get(row).getName();
			case COLUMN_TABLE:
				return getList().get(row).getDatasetID();
			case COLUMN_TYPE:
				return getList().get(row).getType() == CellObject.TYPE_VAR ? Messages
						.getString("GroupsDlg.type_var") : Messages.getString("GroupsDlg.type_field"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			return null;
		}

		@Override
		public void setValueAt(Object aValue, int row, int column) {
			switch (column) {
			case COLUMN_NAME:
				getList().get(row).setName(
						(aValue == null) ? null : aValue.toString());
				break;
			case COLUMN_TABLE:
				getList().get(row).setDatasetID(
						(aValue == null) ? null : aValue.toString());
				fireTableCellUpdated(row, COLUMN_TYPE);
				break;
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return !getRepeateHeaderBox().isSelected()
					&& (columnIndex == COLUMN_TABLE || columnIndex == COLUMN_NAME);
		}

	}

	private static class GroupUndoItem extends AbstractGridUndo {

		private int[] path = null;
		private List<GroupKey> list;

		public GroupUndoItem(JReportGrid grid, DetailGroup group) {
			super(grid, Messages.getString("GroupsDlg.groups_keys")); //$NON-NLS-1$
			fillData(group);
		}

		private void fillData(DetailGroup group) {
			list = new ArrayList<>();
			path = group.getIndexPath();
			for (int i = 0; i < group.getKeyCount(); i++) {
				list.add(group.getKey(i));
			}
		}

		public void clear() {
			super.clear();
			list = null;
		}

		public UndoItem undo() {
			DetailGroup group = (DetailGroup) getGrid().getReportModel()
					.getRowModel().getGroup(path);
			if (group == null)
				return null;
			List<GroupKey> oldList = list;
			fillData(group);
			group.clearKeys();
			for (GroupKey gk : oldList) {
				group.addKey(gk);
			}
			return super.undo();
		}

	}

}
