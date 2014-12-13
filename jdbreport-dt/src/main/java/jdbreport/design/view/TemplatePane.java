/*
 * TemplatePane.java
 *
 * JDBReport Designer
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

package jdbreport.design.view;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import jdbreport.design.grid.*;
import jdbreport.design.grid.DesignAction.InsertTypedRowAction;
import jdbreport.design.model.CellObject;
import jdbreport.design.model.TemplateBook;
import jdbreport.grid.CellValueChangedEvent;
import jdbreport.grid.JReportGrid;
import jdbreport.grid.PreferencesDlg;
import jdbreport.grid.ReportAction;
import jdbreport.grid.undo.CellUndoItem;
import jdbreport.model.Cell;
import jdbreport.model.CellStyle;
import jdbreport.model.CellWrap;
import jdbreport.model.DetailGroup;
import jdbreport.model.GridRect;
import jdbreport.model.Group;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportModel;
import jdbreport.model.event.CellValueChangeListener;
import jdbreport.source.JdbcReportSource;
import jdbreport.util.xml.XMLProperties;
import jdbreport.view.ReportEditor;
import jdbreport.view.ReportEditorPane;
import jdbreport.view.ReportFileFilter;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class TemplatePane extends ReportEditorPane implements
		CellValueChangeListener {

	private static final String DATASET_CHANGE_COMMAND = "dataset_change"; //$NON-NLS-1$

	private static final long serialVersionUID = 1L;

	private static final int MAX_RECENT_COUNT = 5;

	private static final String RECENT = "recent"; //$NON-NLS-1$

	private LinkedList<String> recentFiles;

	private ActionListener recentListener;

	private JPopupMenu rowTypeMenu;

	private JToolBar designBar;

	private JComboBox dsAliasBox;

	private VarList varList;

	private Action sourceAction;

	private FunctionsListEditor functionEditor;

	private Action addTitleAction;

	private Action addPageHeaderAction;

	private Action addGroupHeaderAction;

	private Action addDetailAction;

	private Action addGroupFooterAction;

	private Action addPageFooterAction;

	private Action addFooterAction;

	private Action addGroupAction;

	private Action rowTitleAction;

	private Action rowPageHeaderAction;

	private Action rowGroupHeaderAction;

	private Action rowDetailAction;

	private Action rowGroupFooterAction;

	private Action rowPageFooterAction;

	private Action rowFooterAction;

	private Action rowSimpleAction;

	private Action sumAction;

	private Action maxAction;

	private Action minAction;

	private Action avgAction;

	private Action selectFunctionAction;

	private Action varListAction;

	private JToggleButton notRepeateButton;

	private InsertTypedRowAction addGroupRowsAction;

	private Action genReportAction;

	private Action helpApiAction;

	private Action groupAction;
	private JMenu insertGroupMenu;

	private JMenu totalMenu;

	private JPopupMenu insertGroupPopupMenu;

	private JPopupMenu totalPopupMenu;

	private JMenuItem notRepeateMenuItem;

	private Action notRepeateAction;

	private JToggleButton replacementButton;

	public TemplatePane(XMLProperties properties) {
		super(properties);
		setRowTypeMenu(createRowTypeMenu());
	}

	@Override
	protected JReportGrid createReportGrid(ReportModel rm) {
		return new TemplateGrid(rm);
	}

	@Override
	public void addReportGrid(JReportGrid grid, int index) {
		super.addReportGrid(grid, index);
		grid.getRowHeader().setComponentPopupMenu(getRowTypeMenu());
		grid.addCellValueChangeListener(this);
	}

	protected JPopupMenu getRowTypeMenu() {
		return rowTypeMenu;
	}

	public void setRowTypeMenu(JPopupMenu menu) {
		this.rowTypeMenu = menu;
		getFocusedGrid().getRowHeader().setComponentPopupMenu(menu);
	}

	public TemplateBook getTemplateBook() {
		return (TemplateBook) getReportBook();
	}

	protected void showVarList() {
		getVarList().setVisible(true);
	}

	private VarList getVarList() {
		if (varList == null) {
			Window w = SwingUtilities.getWindowAncestor(this);
			if (w instanceof Frame) {
				varList = new VarList((Frame) w, getTemplateBook().getVars());
			} else {
				varList = new VarList((Dialog) w, getTemplateBook().getVars());
			}
			varList.addUndoListener(this);
		}
		return varList;
	}

	protected Action getVarListAction() {
		if (varListAction == null) {
			varListAction = new DesignAction.DesignBasedAction(
					DesignAction.VAR_LIST_ACTION) {

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					showVarList();
				}

			};
		}
		return varListAction;
	}

	private JComboBox getDSAliasBox() {
		if (dsAliasBox == null) {
			dsAliasBox = new JComboBox();
			dsAliasBox.setEditable(true);
			dsAliasBox.setToolTipText(TemplateReportResources.getInstance()
					.getString("dataset_alias_box.tooltip"));
			dsAliasBox.setMaximumSize(new Dimension(100, 22));
			dsAliasBox.setPreferredSize(new java.awt.Dimension(100, 22));
			dsAliasBox.setActionCommand(DATASET_CHANGE_COMMAND);
			dsAliasBox.addActionListener(this);

			Component editor = dsAliasBox.getEditor().getEditorComponent();
			if (editor != null) {

				editor.addKeyListener(new java.awt.event.KeyAdapter() {

					public void keyReleased(KeyEvent e) {
						if (getFocusedGrid() != null && isEnabledAction()) {
							ComboBoxEditor ce = dsAliasBox.getEditor();
							Object item = ce.getItem();
							if (item != null) {
								((TemplateGrid) getFocusedGrid())
										.setCellDsAlias(item.toString());
							}
						}
					}
				});
			}

		}
		return dsAliasBox;
	}

	
	private JToggleButton getReplacementButton() {
		if (replacementButton == null) {
			replacementButton = new JToggleButton();
			replacementButton.setIcon(TemplateReportResources.getInstance().getIcon(
					"replacement.png")); //$NON-NLS-1$
			replacementButton.setToolTipText(Messages.getString("TemplatePane.replacement")); //$NON-NLS-1$
			replacementButton.setActionCommand("formatting");
			replacementButton.addActionListener(this);
		}
		return replacementButton;
	}

	protected void setReplacement() {
		TemplateGrid grid = (TemplateGrid) getFocusedGrid();
		GridRect selectionRect = grid.getSelectionRect();
		if (selectionRect == null)
			return;
		pushUndo(new CellUndoItem(grid, Messages.getString("TemplatePane.cell_properties"))); //$NON-NLS-1$
		CellObject cell = (CellObject) grid.getSelectedCell();
		boolean replace = cell.isReplacement();
		Iterator<Cell> it = grid.getReportModel().getSelectedCells(
				selectionRect);
		while (it.hasNext()) {
			CellObject c = (CellObject) it.next();
			 c.setReplacement(!replace);
		}

	}

	public void cellValueChange(CellValueChangedEvent evt) {
		Cell eCell = getFocusedGrid().getReportModel().getOwnerReportCell(
				evt.getRow(), evt.getColumn());
		if (!eCell.isNull() && eCell instanceof CellObject) {
			CellObject cell = (CellObject) eCell;
			if (getTemplateBook().findVar(cell.getText())) {
				cell.setType(CellObject.TYPE_VAR);
			} else if (cell.getType() == CellObject.TYPE_VAR) {
				if (cell.getText().length() > 0
						&& (evt.getOldValue() == null || !getTemplateBook()
								.findVar(evt.getOldValue().toString()))) {
					int result = JOptionPane
							.showConfirmDialog(
									this,
									String.format(TemplateReportResources
											.getInstance().getString(
													"var_not_found.message"), //$NON-NLS-1$
											cell.getText()),
									TemplateReportResources.getInstance()
											.getString("var_not_found.caption"), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$
					if (result == JOptionPane.YES_OPTION) {
						getTemplateBook().setVarValue(cell.getText(), null);
						firePropertyChange("vars", null, cell.getText()); //$NON-NLS-1$
					} else
						cell.setType(CellObject.TYPE_NONE);
				} else
					cell.setType(CellObject.TYPE_NONE);
			}
		} 
	}

	@Override
	public void removeReportGrid(JReportGrid grid) {
		grid.removeCellValueChangeListener(this);
		grid.getRowHeader().setComponentPopupMenu(null);
		super.removeReportGrid(grid);
	}

	protected void fillAdvancedGridMenu(JPopupMenu menu) {
		super.fillAdvancedGridMenu(menu);
		menu.addSeparator();
		menu.add(getSelectFunctionAction());
	}

	protected JToolBar getDesignBar() {
		if (designBar == null) {
			designBar = createDesignBar();
		}
		return designBar;
	}

	protected JToolBar createDesignBar() {
		JToolBar designBar = new JToolBar(Messages
				.getString("TemplateEditor.2")); //$NON-NLS-1$
		designBar.setFloatable(false);
		designBar.setRollover(true);
		designBar.add(createTotalButton());
		designBar.add(getNotRepeateButton());
		designBar.add(getReplacementButton());
		designBar.add(getVarListAction());
		designBar.add(getSelectFunctionAction());
		designBar.addSeparator();
		designBar.add(getDSAliasBox());
		designBar.addSeparator();
		designBar.add(genReportAction);
		return designBar;
	}

	private JToggleButton getNotRepeateButton() {
		if (notRepeateButton == null) {
			notRepeateButton = new JToggleButton(getNotRepeateAction());
			notRepeateButton.setText(""); //$NON-NLS-1$
		}
		return notRepeateButton;
	}

	protected void initActions() {
		super.initActions();
		addTitleAction = new DesignAction.InsertTypedRowAction(Group.ROW_TITLE,
				this);
		addPageHeaderAction = new DesignAction.InsertTypedRowAction(
				Group.ROW_PAGE_HEADER, this);
		addGroupHeaderAction = new DesignAction.InsertTypedRowAction(
				Group.ROW_GROUP_HEADER, this);
		addDetailAction = new DesignAction.InsertTypedRowAction(
				Group.ROW_DETAIL, this);
		addGroupFooterAction = new DesignAction.InsertTypedRowAction(
				Group.ROW_GROUP_FOOTER, this);
		addGroupRowsAction = new DesignAction.InsertTypedRowAction(
				Group.ROW_NONE, this);
		addPageFooterAction = new DesignAction.InsertTypedRowAction(
				Group.ROW_PAGE_FOOTER, this);
		addFooterAction = new DesignAction.InsertTypedRowAction(
				Group.ROW_FOOTER, this);
		addGroupAction = DesignAction.createGridAction(DesignAction.INSERT_GROUP_ACTION, this);
		sumAction = new DesignAction.AgrFuncAction(CellObject.AF_SUM, this);
		maxAction = new DesignAction.AgrFuncAction(CellObject.AF_MAX, this);
		minAction = new DesignAction.AgrFuncAction(CellObject.AF_MIN, this);
		avgAction = new DesignAction.AgrFuncAction(CellObject.AF_AVG, this);
		genReportAction = new DesignAction.DesignBasedAction("gen_report") { //$NON-NLS-1$
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				generateReport();
			}
		};
		rowTitleAction = new DesignAction.SetTypeRowAction(Group.ROW_TITLE,
				this);
		rowPageHeaderAction = new DesignAction.SetTypeRowAction(
				Group.ROW_PAGE_HEADER, this);
		rowGroupHeaderAction = new DesignAction.SetTypeRowAction(
				Group.ROW_GROUP_HEADER, this);
		rowDetailAction = new DesignAction.SetTypeRowAction(Group.ROW_DETAIL,
				this);
		rowGroupFooterAction = new DesignAction.SetTypeRowAction(
				Group.ROW_GROUP_FOOTER, this);
		rowPageFooterAction = new DesignAction.SetTypeRowAction(
				Group.ROW_PAGE_FOOTER, this);
		rowFooterAction = new DesignAction.SetTypeRowAction(Group.ROW_FOOTER,
				this);
		rowSimpleAction = new DesignAction.SetTypeRowAction(Group.ROW_NONE,
				this);
	}

	/**
	 * Generates the report from this template
	 * 
	 */
	public void generateReport() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			ReportEditor re = createReportEditor();
			ReportBook newBook = getTemplateBook().createReportBook(
					re.getReportPane());
			re.setReportBook(newBook);
			Point p = this.getLocation();
			p.x += 20;
			p.y += 20;
			re.setLocation(p);
			re.setVisible(true);
		} finally {
			setCursor(Cursor.getDefaultCursor());
		}
	}

	protected ReportEditor createReportEditor() {
		return new ReportEditor();
	}

	protected Action getHelpApiAction() {
		if (helpApiAction == null) {
			helpApiAction = new DesignAction.DesignBasedAction("help_api") { //$NON-NLS-1$

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					URL url = getClass().getResource("/doc/index.html"); //$NON-NLS-1$
					showHelp(url);
				}

			};
		}
		return helpApiAction;
	}

	protected JButton createInsertRowButton() {
		JMenuButton button = new JMenuButton(ReportAction.createGridAction(
				ReportAction.INSERT_ROW_ACTION, this));
		button.setText(""); //$NON-NLS-1$
		button.setIcon(TemplateReportResources.getInstance().getIcon(
				"insrow.png")); //$NON-NLS-1$
		button.setMenu(getInsertGroupPopupMenu());
		return button;
	}

	protected JButton createTotalButton() {
		JMenuButton button = new JMenuButton(new DesignAction.AgrFuncAction(
				CellObject.AF_SUM, this));
		button.setText(""); //$NON-NLS-1$
		button.setIcon(TemplateReportResources.getInstance().getIcon(
				"total.png")); //$NON-NLS-1$
		button.setMenu(getTotalPopupMenu());
		return button;
	}

	protected void addToolBars() {
		getCoolBar().add(getStandartBar());
		getCoolBar().add(getDesignBar());
		getCoolBar().add(getFormatBar());
	}

	protected void addAlignAction(JToolBar alignBar) {
		super.addAlignAction(alignBar);
		AbstractButton autoHeightButton = getAutoHeightAction().addButton(
				new JToggleButton());
		autoHeightButton.setText(""); //$NON-NLS-1$
		alignBar.add(autoHeightButton);
	}

	protected void setGridVisible(JReportGrid source, Boolean newValue) {

	}

	protected ReportBook createDefaultReportBook() {
		return new TemplateBook();
	}

	protected FileFilter getDefaultFilter(JFileChooser fileChooser) {
		FileFilter[] filters = fileChooser.getChoosableFileFilters();
		for (FileFilter filter : filters) {
			if (filter instanceof ReportFileFilter) {
				if (((ReportFileFilter) filter).getFileType().getExtensions()[0]
						.equals(TemplateBook.JDBR)) {
					return filter;
				}
			}
		}
		return null;
	}

	protected boolean canShowGrid(ReportModel rm) {
		return true;
	}

	private void setTotalActionsEnabled(boolean enabled) {
		sumAction.setEnabled(enabled);
		minAction.setEnabled(enabled);
		maxAction.setEnabled(enabled);
		avgAction.setEnabled(enabled);
	}

	protected void cellChanged(JReportGrid rep, CellWrap cellWrap) {
		super.cellChanged(rep, cellWrap);
		Cell cell = cellWrap.getCell();
		CellStyle style = rep.getCellStyle(cell.getStyleId());
		Group group = rep.getReportModel().getRowModel().getGroup(
				cellWrap.getRow());
		setTotalActionsEnabled(group.getType() == Group.ROW_FOOTER
				|| group.getType() == Group.ROW_GROUP_FOOTER
				|| group.getType() == Group.ROW_GROUP_HEADER);
		if (group.getParent() instanceof DetailGroup) {
			getGroupAction().setEnabled(true);
		} else {
			getGroupAction().setEnabled(false);
		}

		if (((CellObject) cell).getType() == CellObject.TYPE_FIELD) {
			addToAliasBox(((CellObject) cell).getDataSetId());
		} else {
			getDSAliasBox().setSelectedItem(null);
		}
		if (group.getType() == Group.ROW_DETAIL) {
			getNotRepeateAction().setEnabled(true);
			getNotRepeateButton().setSelected(
					((CellObject) cell).isNotRepeate());
			getNotRepeateMenuItem().setSelected(
					((CellObject) cell).isNotRepeate());
		} else {
			getNotRepeateButton().setSelected(false);
			getNotRepeateMenuItem().setSelected(false);
			getNotRepeateAction().setEnabled(false);
		}
		
		getReplacementButton().setSelected(((CellObject)cell).isReplacement());
		getAutoHeightAction().setSelected(style.isAutoHeight());

	}

	private void addToAliasBox(String tableId) {
		for (int i = 0; i < getDSAliasBox().getItemCount(); i++) {
			Object o = getDSAliasBox().getItemAt(i);
			if (o != null && o.equals(tableId)) {
				getDSAliasBox().setSelectedItem(tableId);
				return;
			}
		}
		getDSAliasBox().addItem(tableId);
		getDSAliasBox().setSelectedItem(tableId);
	}

	protected void updateDsAliasBox() {
		dsAliasBox.removeAllItems();
		for (int i = 0; i < getTemplateBook().getSourcesList().size(); i++) {
			JdbcReportSource source = getTemplateBook()
					.getSourcesList().get(i);
			for (int n = 0; n < source.getDataSetCount(); n++) {
				dsAliasBox.addItem(source.getDataSet(n).getId());
			}
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("vars")) { //$NON-NLS-1$
			getVarList().updateVarList();
		} else if (evt.getPropertyName().equals("reportBook")) { //$NON-NLS-1$
			varList = null;
		} else {
			super.propertyChange(evt);
		}

	}

	protected void selectCellFunctions() {
		getFunctionsListEditor().setSelectedFunction(
				((TemplateGrid) getFocusedGrid()).getSelectedCellObject());
		getFunctionsListEditor().setVisible(true);
		if (getFunctionsListEditor().result() == FunctionsListEditor.SELECT) {
			String functionName = getFunctionsListEditor().getFunctionName();
			((TemplateGrid) getFocusedGrid()).setFunctionName(functionName);
		} else {
			if (getFunctionsListEditor().result() == FunctionsListEditor.REMOVE) {
				((TemplateGrid) getFocusedGrid()).setFunctionName(null);
			}
		}
	}

	protected FunctionsListEditor getFunctionsListEditor() {
		if (functionEditor == null) {
			Window w = SwingUtilities.getWindowAncestor(this);
			functionEditor = new FunctionsListEditor(w,
						getTemplateBook().getFunctionsList(), properties);
			functionEditor.addUndoListener(this);
		}
		return functionEditor;
	}

	public void showGroupList() {
		Group group = getFocusedGrid().getReportModel().getRowModel().getGroup(
				getFocusedGrid().getSelectedRow());
		if (group != null) {
			group = group.getParent();
		}
		if (group instanceof DetailGroup) {
			GroupsDlg dlg;

			Window w = SwingUtilities.getWindowAncestor(this);
			if (w instanceof Frame) {
				dlg = new GroupsDlg((Frame) w, getFocusedGrid(),
						(DetailGroup) group);
			} else {
				dlg = new GroupsDlg((Dialog) w, getFocusedGrid(),
						(DetailGroup) group);
			}
			dlg.addUndoListener(this);
			dlg.setVisible(true);
		}
	}

	protected void showAbout() {
		Window w = SwingUtilities.getWindowAncestor(this);
		if (w instanceof Frame) {
			new AboutDlg((Frame) w);
		} else {
			new AboutDlg((Dialog) w);
		}
	}

	protected PreferencesDlg createPreferences() {
		Window w = SwingUtilities.getWindowAncestor(this);
		if (w instanceof Frame) {
			return new TemplPrefDlg((Frame) w, true);
		} else {
			return new TemplPrefDlg((Dialog) w, true);
		}
	}

	protected JPopupMenu createRowTypeMenu() {
		JPopupMenu rowTypeMenu = new JPopupMenu();
		rowTypeMenu.add(getRowTitleAction());
		rowTypeMenu.add(getRowPageHeaderAction());
		rowTypeMenu.add(getRowGroupHeaderAction());
		rowTypeMenu.add(getRowDetailAction());
		rowTypeMenu.add(getRowGroupFooterAction());
		rowTypeMenu.add(getRowSimpleAction());
		rowTypeMenu.add(getRowPageFooterAction());
		rowTypeMenu.add(getRowFooterAction());
		return rowTypeMenu;
	}

	protected Action getSourceAction() {
		if (sourceAction == null) {
			sourceAction = new AbstractAction() {

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					JdbcSourceEditor se;
					Window w = SwingUtilities
							.getWindowAncestor(TemplatePane.this);
					if (w instanceof Frame) {
						se = new JdbcSourceEditor((Frame) w, getTemplateBook());
					} else {
						se = new JdbcSourceEditor((Dialog) w, getTemplateBook());
					}
					se.addUndoListener(TemplatePane.this);
					se.setVisible(true);
					updateDsAliasBox();
				}

			};
			sourceAction.putValue(
					"Name", Messages.getString("TemplateEditor.source")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return sourceAction;
	}

	protected Action getSelectFunctionAction() {
		if (selectFunctionAction == null) {
			selectFunctionAction = new DesignAction.DesignBasedAction(
					DesignAction.SELECT_FUNCTION_ACTION) {

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					selectCellFunctions();
				}

			};
		}
		return selectFunctionAction;
	}

	public Action getRowTitleAction() {
		return rowTitleAction;
	}

	public Action getRowPageHeaderAction() {
		return rowPageHeaderAction;
	}

	public Action getRowGroupHeaderAction() {
		return rowGroupHeaderAction;
	}

	public Action getRowDetailAction() {
		return rowDetailAction;
	}

	public Action getRowGroupFooterAction() {
		return rowGroupFooterAction;
	}

	public Action getRowSimpleAction() {
		return rowSimpleAction;
	}

	public Action getRowPageFooterAction() {
		return rowPageFooterAction;
	}

	public Action getRowFooterAction() {
		return rowFooterAction;
	}

	public Action getAddTitleAction() {
		return addTitleAction;
	}

	public Action getAddPageHeaderAction() {
		return addPageHeaderAction;
	}

	public Action getAddGroupHeaderAction() {
		return addGroupHeaderAction;
	}

	public Action getAddDetailAction() {
		return addDetailAction;
	}

	public Action getAddGroupFooterAction() {
		return addGroupFooterAction;
	}

	public Action getAddGroupRowsAction() {
		return addGroupRowsAction;
	}

	public Action getAddPageFooterAction() {
		return addPageFooterAction;
	}

	public Action getAddFooterAction() {
		return addFooterAction;
	}

	public Action getAddGroupAction() {
		return addGroupAction;
	}

	public Action getGroupAction() {
		if (groupAction == null) {
			groupAction = new DesignAction.DesignBasedAction(
					DesignAction.GROUP_KEY_ACTION) {

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					showGroupList();
				}

			};
		}
		return groupAction;
	}

	public Action getGenReportAction() {
		return genReportAction;
	}

	public Action getSumAction() {
		return sumAction;
	}

	public Action getAvgAction() {
		return avgAction;
	}

	public Action getMinAction() {
		return minAction;
	}

	public Action getMaxAction() {
		return maxAction;
	}

	protected JMenu createHelpMenu() {
		JMenu helpMenu = super.createHelpMenu();
		helpMenu.add(getHelpApiAction());
		return helpMenu;
	}

	private JMenu createDesignMenu() {
		JMenu designMenu = new JMenu();
		designMenu.setText(TemplateReportResources.getInstance().getString(
				"menu.design")); //$NON-NLS-1$
		designMenu.add(getSourceAction());
		designMenu.add(getSelectFunctionAction());
		designMenu.add(getVarListAction());
		designMenu.add(getGroupAction());
		designMenu.add(getInsertGroupMenu());
		designMenu.add(getTotalMenu());
		designMenu.add(getNotRepeateMenuItem());
		designMenu.addSeparator();
		designMenu.add(getGenReportAction());
		return designMenu;
	}

	private JMenuItem getNotRepeateMenuItem() {
		if (notRepeateMenuItem == null) {
			notRepeateMenuItem = new JCheckBoxMenuItem(getNotRepeateAction());
		}
		return notRepeateMenuItem;
	}

	private Action getNotRepeateAction() {
		if (notRepeateAction == null) {
			notRepeateAction = DesignAction.createGridAction(
					DesignAction.NOTREPEATE_ACTION, this);
			notRepeateAction.setEnabled(false);
		}
		return notRepeateAction;
	}

	private JMenu getTotalMenu() {
		if (totalMenu == null) {
			totalMenu = new JMenu();
			totalMenu.setText(TemplateReportResources.getInstance().getString(
					"menu.agrfunc")); //$NON-NLS-1$
			totalMenu.add(getSumAction());
			totalMenu.add(getMinAction());
			totalMenu.add(getMaxAction());
			totalMenu.add(getAvgAction());
		}
		return totalMenu;
	}

	private JPopupMenu getTotalPopupMenu() {
		if (totalPopupMenu == null) {
			totalPopupMenu = new JPopupMenu();
			totalPopupMenu.add(getSumAction());
			totalPopupMenu.add(getMinAction());
			totalPopupMenu.add(getMaxAction());
			totalPopupMenu.add(getAvgAction());
		}
		return totalPopupMenu;
	}

	private JMenu getInsertGroupMenu() {
		if (insertGroupMenu == null) {
			insertGroupMenu = new JMenu();
			insertGroupMenu.setText(TemplateReportResources.getInstance()
					.getString("menu.group")); //$NON-NLS-1$
			insertGroupMenu.add(getAddTitleAction());
			insertGroupMenu.add(getAddPageHeaderAction());
			insertGroupMenu.add(getAddGroupHeaderAction());
			insertGroupMenu.add(getAddDetailAction());
			insertGroupMenu.add(getAddGroupFooterAction());
			insertGroupMenu.add(getAddGroupRowsAction());
			insertGroupMenu.add(getAddPageFooterAction());
			insertGroupMenu.add(getAddFooterAction());
			insertGroupMenu.addSeparator();
			insertGroupMenu.add(getAddGroupAction());
		}
		return insertGroupMenu;
	}

	private JPopupMenu getInsertGroupPopupMenu() {
		if (insertGroupPopupMenu == null) {
			insertGroupPopupMenu = new JPopupMenu();
			insertGroupPopupMenu.add(getAddTitleAction());
			insertGroupPopupMenu.add(getAddPageHeaderAction());
			insertGroupPopupMenu.add(getAddGroupHeaderAction());
			insertGroupPopupMenu.add(getAddDetailAction());
			insertGroupPopupMenu.add(getAddGroupFooterAction());
			insertGroupPopupMenu.add(getAddGroupRowsAction());
			insertGroupPopupMenu.add(getAddPageFooterAction());
			insertGroupPopupMenu.add(getAddFooterAction());
			insertGroupPopupMenu.addSeparator();
			insertGroupPopupMenu.add(getAddGroupAction());
		}
		return insertGroupPopupMenu;
	}

	public JMenuBar createJMenuBar() {
		JMenuBar menuBar = super.createJMenuBar();
		menuBar.add(createDesignMenu(), menuBar.getComponentCount() - 1);
		return menuBar;
	}

	public String getCaption() {
		return TemplateReportResources.getInstance().getString("caption");
	}

	protected LinkedList<String> getRecentFiles() {
		if (recentFiles == null) {
			recentFiles = new LinkedList<>();
		}
		return recentFiles;
	}

	private ActionListener getRecentListener() {
		if (recentListener == null) {
			recentListener = new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					JMenuItem menuItem = (JMenuItem) e.getSource();
					String s = menuItem.getText();
					if (!saveQuestion()) {
						return;
					}
					if (!open(new File(s))) {
						getRecentFiles().remove(s);
						getFileMenu().remove(menuItem);
					} else {
						if (getRecentFiles().indexOf(s) > 0) {
							getRecentFiles().remove(s);
							getRecentFiles().addFirst(s);
						}
					}
				}

			};
		}
		return recentListener;
	}

	protected void addRecentItems(JMenu fileMenu) {
		if (recentFiles != null) {
			fileMenu.add(new JSeparator(), fileMenu.getItemCount() - 1);
			for (int i = 0; i < getRecentFiles().size(); i++) {
				addRecentItem(fileMenu, getRecentFiles().get(i));
			}
		}

	}

	private void addRecentItem(JMenu fileMenu, String fileName) {
		JMenuItem menuItem = new JMenuItem();
		menuItem.setText(fileName);
		menuItem.addActionListener(getRecentListener());
		fileMenu.add(menuItem, fileMenu.getItemCount() - 2);
	}

	protected void saveProperties() {
		if (recentFiles != null) {
			String[] files = new String[Math.min(MAX_RECENT_COUNT,
					getRecentFiles().size())];
			for (int i = 0; i < files.length; i++) {
				files[i] = getRecentFiles().get(i);
			}
			properties.put(RECENT, files);
		}
		super.saveProperties();
	}

	public void setReportBook(ReportBook reportBook) {
		if (this.getReportBook() != reportBook) {
			super.setReportBook(reportBook);
			functionEditor = null;
			varList = null;
			updateDsAliasBox();
			File file = getReportFile();
			if (file != null) {
				if (getRecentFiles().indexOf(file.getPath()) < 0) {
					getRecentFiles().addFirst(file.getPath());
					addRecentItem(getFileMenu(), file.getPath());
				}
			}
		}
	}

	protected void initProperties() {
		super.initProperties();
		Object o = properties.get(RECENT);
		if (o != null && o instanceof String[]) {
			String[] files = (String[]) o;
			for (String file : files) {
				getRecentFiles().addLast(file);
			}
			addRecentItems(getFileMenu());
		}
	}

	@Override
	protected void installListeners() {
		super.installListeners();
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (DATASET_CHANGE_COMMAND.equals(e.getActionCommand())) {
			JComboBox cb = (JComboBox) e.getSource();
			if (cb.getSelectedItem() != null)
				if (getFocusedGrid() != null
						&& isEnabledAction()
						&& (cb.isFocusOwner() || cb.getEditor()
								.getEditorComponent().isFocusOwner())) {
					Object item = cb.getSelectedItem();
					if (item != null) {
						((TemplateGrid) getFocusedGrid()).setCellDsAlias(item
								.toString());
					}
				}
		} else if ("formatting".equals(e.getActionCommand()) ){
					setReplacement();
		}else
			super.actionPerformed(e);
	}

}
