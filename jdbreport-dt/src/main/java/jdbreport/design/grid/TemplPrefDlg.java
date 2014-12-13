/*
 * Created	15.02.2012
 *
 * JDBReport Designer
 * 
 * Copyright (C) 2012-2014 Andrey Kholmanskih
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
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.AbstractTableModel;

import jdbreport.design.model.ReplaceItem;
import jdbreport.design.model.TemplateBook;
import jdbreport.grid.JReportGrid;
import jdbreport.grid.PreferencesDlg;
import jdbreport.grid.ReportResources;
import jdbreport.model.ReportBook;

/**
 * @version 1.0 15.02.2012
 * 
 * @author Andrey Kholmanskih
 */
public class TemplPrefDlg extends PreferencesDlg {

	private static final String DEL_REPL = "del_repl"; //$NON-NLS-1$
	private static final String ADD_REPL = "add_repl"; //$NON-NLS-1$
	private static final long serialVersionUID = 1L;
	private JTable replaceTable;
	private ArrayList<ReplaceItem> replList = new ArrayList<ReplaceItem>();

	/**
	 * @param owner
	 * @param modal
	 * @throws HeadlessException
	 */
	public TemplPrefDlg(Frame owner, boolean modal) throws HeadlessException {
		super(owner, modal);
	}

	/**
	 * @param owner
	 * @param modal
	 * @throws HeadlessException
	 */
	public TemplPrefDlg(Dialog owner, boolean modal) throws HeadlessException {
		super(owner, modal);
	}

	protected TemplateBook getReportBook() {
		return (TemplateBook) super.getReportBook();
	}

	protected JTabbedPane createTabbedPane() {
		JTabbedPane tabbedPane = super.createTabbedPane();
		tabbedPane
				.insertTab(
						Messages.getString("PreferencesDlg.2"), null, getSheetPanel(), null, 1);//$NON-NLS-1$
		return tabbedPane;
	}

	protected JPanel createGeneralPanel() {
		JPanel generalPanel = super.createGeneralPanel();
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(0, 10, 4, 10);
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.weightx = 1.1;
		generalPanel.add(createReplacementsPanel(), gridBagConstraints);

		return generalPanel;
	}

	private JPanel createReplacementsPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(300, 200));
		panel.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("TemplPrefDlg.replacements"))); //$NON-NLS-1$
		panel.add(createReplToolBar(), BorderLayout.NORTH);
		panel.add(new JScrollPane(getReplaceTable()), BorderLayout.CENTER);
		return panel;
	}

	private JToolBar createReplToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		JButton addButton = new JButton(ReportResources.getInstance().getIcon(
				"add.gif")); //$NON-NLS-1$
		addButton.setActionCommand(ADD_REPL);
		addButton.addActionListener(this);
		toolBar.add(addButton);
		JButton delButton = new JButton(ReportResources.getInstance().getIcon(
				"remove.gif")); //$NON-NLS-1$
		delButton.setActionCommand(DEL_REPL);
		delButton.addActionListener(this);
		toolBar.add(delButton);
		return toolBar;
	}

	private JTable getReplaceTable() {
		if (replaceTable == null) {
			replaceTable = new JTable(new ReplaceTableModel());
			replaceTable.setColumnSelectionAllowed(true);
		}
		return replaceTable;
	}

	public void setReportGrid(JReportGrid grid, ReportBook reportBook) {
		super.setReportGrid(grid, reportBook);
		replList.clear();
		if (getReportBook().getReplacePatterns() != null) {
			for (ReplaceItem ri : getReportBook().getReplacePatterns()) {
				replList.add(ri.clone());
			}
		}
	}

	protected void save() {
		super.save();
		getReportBook().clearReplacePatterns();
		for (ReplaceItem item : replList) {
			if (item.getRegex() != null && item.getRegex().length() > 0) {
				getReportBook().addReplacePattern(item.getRegex(),
						item.getReplacement());
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ADD_REPL.equals(e.getActionCommand())) {
			replList.add(new ReplaceItem("", null)); //$NON-NLS-1$
			getReplaceTable().revalidate();
			getReplaceTable().repaint();
			getReplaceTable().changeSelection(replList.size() - 1, 0, false,
					false);
		} else if (DEL_REPL.equals(e.getActionCommand())) {
			int row = getReplaceTable().getSelectedRow();
			if (row >= 0 && row < replList.size()) {
				replList.remove(row);
				getReplaceTable().revalidate();
				getReplaceTable().repaint();
				if (row >= replList.size())
					row--;
				getReplaceTable().changeSelection(row, 0, false, false);
			}
		} else
			super.actionPerformed(e);
	}

	private class ReplaceTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		@Override
		public int getRowCount() {
			return replList.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ReplaceItem item = replList.get(rowIndex);
			if (columnIndex == 0) {
				return item.getRegex();
			} else {
				return item.getReplacement();
			}
		}

		@Override
		public String getColumnName(int column) {
			if (column == 0) {
				return Messages.getString("TemplPrefDlg.pattern"); //$NON-NLS-1$
			} else {
				return Messages.getString("TemplPrefDlg.replacement"); //$NON-NLS-1$
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			ReplaceItem item = replList.get(rowIndex);
			if (columnIndex == 0) {
				item.setRegex((String) aValue);
			} else {
				item.setReplacement((String) aValue);
			}
		}

	}
}
