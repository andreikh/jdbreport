/*
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

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.JLabel;

import jdbreport.design.grid.dialogs.DBSourceDialog;
import jdbreport.design.model.TemplateBook;
import jdbreport.grid.ReportResources;
import jdbreport.grid.UndoEvent;
import jdbreport.grid.UndoListener;
import jdbreport.grid.undo.UndoItem;
import jdbreport.source.JdbcDataSet;
import jdbreport.source.JdbcReportSource;
import jdbreport.source.JdbcSource;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class JdbcSourceEditor extends JDialog {

	private static final long serialVersionUID = 1L;

	private boolean dirty;

	private TemplateBook reportBook;

	private JPanel jPanel = null;

	private JPanel buttonPanel = null;

	private JButton okButton = null;

	private JButton cancelButton = null;

	private JButton applyButton = null;

	private JPanel leftPanel = null;

	private JList sourcesJList = null;

	private JSplitPane splitPane = null;

	private JPanel topPanel = null;

	private JPanel bottomPanel = null;

	private JTable jTable = null;

	private JTextPane jTextPane = null;

	private Vector<JdbcReportSource> sourceList;

	private QueryModel queryModel;

	private JToolBar toolPanel = null;

	private JButton addButton = null;

	private JButton delButton = null;

	private JToolBar sourceBar = null;

	private JButton addSourceButton = null;

	private JButton delSourceButton = null;

	private JButton editSourceButton = null;

	private JButton upButton = null;

	private JButton downButton = null;

	private UndoListener undoListener;

	public JdbcSourceEditor(Frame frame, TemplateBook reportBook) {
		super(frame, true);
		initialize();
		this.reportBook = reportBook;
		initSourceList(reportBook.getSourcesList());
		getSourcesJList().setListData(sourceList);
		if (sourceList.size() > 0) {
			sourcesJList.setSelectedIndex(0);
			if (getJTable().getRowCount() > 0)
				getJTable().changeSelection(0, 0, false, false);
		}
	}

	public JdbcSourceEditor(Dialog frame, TemplateBook reportBook) {
		super(frame, true);
		initialize();
		this.reportBook = reportBook;
		initSourceList(reportBook.getSourcesList());
		getSourcesJList().setListData(sourceList);
		if (sourceList.size() > 0) {
			sourcesJList.setSelectedIndex(0);
			if (getJTable().getRowCount() > 0)
				getJTable().changeSelection(0, 0, false, false);
		}
	}
	
	private void initSourceList(List<JdbcReportSource> list) {
		sourceList = new Vector<JdbcReportSource>();
		Iterator<JdbcReportSource> it = list.iterator();
		while (it.hasNext()) {
			sourceList.add((JdbcReportSource) it.next().clone());
		}
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this
				.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setBounds(new java.awt.Rectangle(100, 100, 518, 321));
		this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
		this.setContentPane(getJPanel());
		this.setTitle(Messages.getString("JdbcSourceEditor.title")); //$NON-NLS-1$
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				if (isDirty()) {
					if (JOptionPane
							.showConfirmDialog(
									JdbcSourceEditor.this,
									Messages
											.getString("JdbcSourceEditor.save_query"), //$NON-NLS-1$
									Messages
											.getString("JdbcSourceEditor.save_title"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { //$NON-NLS-1$
						apply();
					}
				}
			}
		});
	}

	/**
	 * @return Returns the reportBook.
	 */
	protected TemplateBook getReportBook() {
		return reportBook;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout());
			jPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			jPanel.add(getLeftPanel(), java.awt.BorderLayout.WEST);
			jPanel.add(getSplitPane(), java.awt.BorderLayout.CENTER);
			jPanel.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
		}
		return jPanel;
	}

	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getOkButton());
			buttonPanel.add(getCancelButton());
			buttonPanel.add(getApplyButton());
		}
		return buttonPanel;
	}

	/**
	 * This method initializes okButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText(Messages.getString("JdbcSourceEditor.ok_text")); //$NON-NLS-1$
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (apply()) {
						setVisible(false);
					}
				}
			});
		}
		return okButton;
	}

	/**
	 * This method initializes cancelButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText(Messages
					.getString("JdbcSourceEditor.cancel_text")); //$NON-NLS-1$
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setVisible(false);
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes applyButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getApplyButton() {
		if (applyButton == null) {
			applyButton = new JButton();
			applyButton.setText(Messages
					.getString("JdbcSourceEditor.apply_text")); //$NON-NLS-1$
			applyButton.setEnabled(false);
			applyButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					apply();
				}
			});
		}
		return applyButton;
	}

	protected boolean apply() {
		pushUndo(new JdbcSourceUndo(reportBook));
		reportBook.getSourcesList().clear();
		for (JdbcReportSource source : getSourceList()) {
			reportBook.getSourcesList().add((JdbcReportSource) source.clone());
		}
		setDirty(false);
		return true;
	}

	/**
	 * This method initializes leftPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getLeftPanel() {
		if (leftPanel == null) {
			JLabel jLabel = new JLabel();
			jLabel
					.setText(Messages
							.getString("JdbcSourceEditor.label_dataset")); //$NON-NLS-1$
			leftPanel = new JPanel();
			leftPanel.setLayout(new BorderLayout());
			leftPanel.add(new JScrollPane(getSourcesJList()),
					java.awt.BorderLayout.CENTER);
			leftPanel.add(jLabel, java.awt.BorderLayout.NORTH);
			leftPanel.add(getJToolBar(), java.awt.BorderLayout.SOUTH);
		}
		return leftPanel;
	}

	/**
	 * This method initializes sourcesJList
	 * 
	 * @return javax.swing.JList
	 */
	private JList getSourcesJList() {
		if (sourcesJList == null) {
			sourcesJList = new JList();
			sourcesJList
					.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
						public void valueChanged(
								javax.swing.event.ListSelectionEvent e) {
							int selectedIndex = sourcesJList.getSelectedIndex();
							if (selectedIndex >= 0) {
								JdbcReportSource source = getSourceList().get(
										selectedIndex);
								List<JdbcDataSet> list = source
										.getDataSetList();
								if (list != getQueryModel().getQueryList()) {
									getQueryModel().setQueryList(list);
									if (list.size() > 0) {
										jTable.getSelectionModel()
												.setAnchorSelectionIndex(0);
										getJTextPane().setText(
												((JdbcDataSet) list.get(0))
														.getQuery()); //$NON-NLS-1$
										getJTextPane().setSelectionStart(0);
										getJTextPane().setSelectionEnd(0);
									} else {
										jTable.getSelectionModel()
												.setAnchorSelectionIndex(-1);
										getJTextPane().setText(""); //$NON-NLS-1$
									}
								}
							} else {
								getQueryModel().setQueryList(null);
							}
							jTable.revalidate();
							jTable.repaint();
						}
					});
			sourcesJList.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if (e.getClickCount() > 1) {
						showSourceEditor();
					}
				}

			});
		}
		return sourcesJList;
	}

	private void showSourceEditor() {
		try {
			int selectedIndex = sourcesJList.getSelectedIndex();
			if (selectedIndex >= 0) {
				DBSourceDialog dlg = new DBSourceDialog(
						(Frame) this.getOwner(), (JdbcSource) getSourceList()
								.get(selectedIndex));
				dlg.setVisible(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane();
			splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
			splitPane.setDividerLocation(120);
			splitPane.setDividerSize(4);
			splitPane.setBottomComponent(getBottomPanel());
			splitPane.setTopComponent(getTopPanel());
		}
		return splitPane;
	}

	/**
	 * This method initializes topPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTopPanel() {
		if (topPanel == null) {
			topPanel = new JPanel();
			topPanel.setLayout(new BorderLayout());
			topPanel.add(new JScrollPane(getJTable()),
					java.awt.BorderLayout.CENTER);
			topPanel.add(getToolPanel(), java.awt.BorderLayout.EAST);
		}
		return topPanel;
	}

	/**
	 * This method initializes bottomPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new JPanel();
			bottomPanel.setLayout(new BorderLayout());
			bottomPanel.add(new JScrollPane(getJTextPane()),
					java.awt.BorderLayout.CENTER);
		}
		return bottomPanel;
	}

	/**
	 * This method initializes jTable
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable();
			jTable.setModel(getQueryModel());
			jTable
					.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			jTable.getSelectionModel().addListSelectionListener(
					new ListSelectionListener() {

						public void valueChanged(ListSelectionEvent e) {
							int i = jTable.getSelectedRow();
							upButton.setEnabled(i > 0);
							downButton.setEnabled(i > -1
									&& i < (jTable.getRowCount() - 1));
							if (i >= 0) {
								JdbcDataSet ds = getQueryModel().getDataSet(i);
								if (ds != null) {
									jTextPane.setText(ds.getQuery());
								} else
									jTextPane.setText(""); //$NON-NLS-1$
							} else
								jTextPane.setText(""); //$NON-NLS-1$
						}

					});
		}
		return jTable;
	}

	private QueryModel getQueryModel() {
		if (queryModel == null) {
			queryModel = new QueryModel();
		}
		return queryModel;
	}

	/**
	 * This method initializes jTextPane
	 * 
	 * @return javax.swing.JTextPane
	 */
	private JTextPane getJTextPane() {
		if (jTextPane == null) {
			jTextPane = new JTextPane();
			jTextPane.addKeyListener(new java.awt.event.KeyAdapter() {

				public void keyReleased(java.awt.event.KeyEvent e) {
					JdbcDataSet ds = getCurrentDataSet();
					if (ds != null) {
						ds.setQuery(jTextPane.getText());
						setDirty(true);
					}
				}
			});
		}
		return jTextPane;
	}

	private JdbcDataSet getCurrentDataSet() {
		if (jTable.getSelectedRow() >= 0) {
			return (JdbcDataSet) getQueryModel().getQueryList().get(
					jTable.getSelectedRow());
		}
		return null;
	}

	/**
	 * @param dirty
	 *            The dirty to set.
	 */
	private void setDirty(boolean dirty) {
		this.dirty = dirty;
		applyButton.setEnabled(dirty);
	}

	/**
	 * @return Returns the dirty.
	 */
	private boolean isDirty() {
		return dirty;
	}

	/**
	 * @return Returns the sourceList.
	 */
	public List<JdbcReportSource> getSourceList() {
		return sourceList;
	}

	private class QueryModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		private static final int COLUMN_MASTER_ALIAS = 1;
		private static final int COLUMN_QUERY_ALIAS = 0;
		private List<JdbcDataSet> queryList;

		public QueryModel() {
			super();
		}

		public int getRowCount() {
			return getQueryList() == null ? 0 : getQueryList().size();
		}

		public int getColumnCount() {
			return 2;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (getQueryList() != null) {
				JdbcDataSet ds = getDataSet(rowIndex);
				switch (columnIndex) {
				case COLUMN_QUERY_ALIAS:
					return ds.getId();
				case COLUMN_MASTER_ALIAS:
					return ds.getMasterId();
				}
			}
			return null;
		}

		public JdbcDataSet getDataSet(int rowIndex) {
			return getQueryList() != null ? getQueryList().get(
					rowIndex) : null;
		}

		public String getColumnName(int column) {
			switch (column) {
			case COLUMN_QUERY_ALIAS:
				return Messages.getString("JdbcSourceEditor.column_id");
			case COLUMN_MASTER_ALIAS:
				return Messages.getString("JdbcSourceEditor.column_master");
			}
			return "";
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return (getQueryList() != null);
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			JdbcDataSet ds = getDataSet(rowIndex);
			switch (columnIndex) {
			case COLUMN_QUERY_ALIAS:
				ds.setId(aValue.toString());
				break;
			case COLUMN_MASTER_ALIAS:
				ds.setMasterId(aValue.toString());
				break;
			}
			setDirty(true);
		}

		/**
		 * @param queryList
		 *            The queryList to set.
		 */
		public void setQueryList(List<JdbcDataSet> queryList) {
			this.queryList = queryList;
		}

		/**
		 * @return Returns the queryList.
		 */
		public List<JdbcDataSet> getQueryList() {
			return queryList;
		}

		public void swap(int i, int j) {
			if (i >= 0 && j >= 0) {
				JdbcDataSet tmp = queryList.get(i);
				queryList.set(i, queryList.get(j));
				queryList.set(j, tmp);
			}
		}

	}

	/**
	 * This method initializes toolPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JToolBar getToolPanel() {
		if (toolPanel == null) {
			toolPanel = new JToolBar(JToolBar.VERTICAL);
			toolPanel.setRollover(true);
			toolPanel.setFloatable(false);
			toolPanel.add(getAddButton(), null);
			toolPanel.add(getDelButton(), null);
			toolPanel.add(getUpButton());
			toolPanel.add(getDownButton());
		}
		return toolPanel;
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
					.getString("JdbcSourceEditor.add_tooltip")); //$NON-NLS-1$
			addButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JdbcDataSet ds = new JdbcDataSet();
					getQueryModel().getQueryList().add(ds);
					getQueryModel().fireTableDataChanged();
					delButton.setEnabled(getQueryModel().getRowCount() > 0);
					setDirty(true);
				}
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
			delButton.setIcon(ReportResources.getInstance().getIcon("del.gif")); // /$NON-NLS-1$
			delButton.setToolTipText(Messages
					.getString("JdbcSourceEditor.delete_tooltip")); // Generated
			// //$NON-NLS-1$
			delButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int i = jTable.getSelectedRow();
					if (i >= 0) {
						getQueryModel().getQueryList().remove(i);
						getQueryModel().fireTableDataChanged();
					}
					delButton.setEnabled(getQueryModel().getRowCount() > 0);
					setDirty(true);
				}
			});
		}
		return delButton;
	}

	/**
	 * This method initializes sourceBar
	 * 
	 * @return javax.swing.JToolBar
	 */
	private JToolBar getJToolBar() {
		if (sourceBar == null) {
			sourceBar = new JToolBar();
			sourceBar.setRollover(true);
			sourceBar.setFloatable(false);
			sourceBar.add(getAddSourceButton());
			sourceBar.add(getEditSourceButton());
			sourceBar.add(getDelSourceButton());
		}
		return sourceBar;
	}

	/**
	 * This method initializes addSourceButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddSourceButton() {
		if (addSourceButton == null) {
			addSourceButton = new JButton();
			addSourceButton.setIcon(ReportResources.getInstance().getIcon(
					"add.gif"));
			addSourceButton.setToolTipText(Messages
					.getString("JdbcSourceEditor.addsource_tooltip"));
			addSourceButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							addNewSource();
						}
					});
		}
		return addSourceButton;
	}

	protected void addNewSource() {
		JdbcReportSource src = new JdbcReportSource();
		try {
			src.setAlias(Messages.getString("JdbcSourceEditor.new_source"));
			DBSourceDialog dlg = new DBSourceDialog(this.getOwner(),
					src);
			dlg.setVisible(true);
			if (dlg.getExitCode() == DBSourceDialog.OK) {
				sourceList.add(src);
				sourcesJList.setListData(sourceList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method initializes delSourceButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getDelSourceButton() {
		if (delSourceButton == null) {
			delSourceButton = new JButton();
			delSourceButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							deleteSource();
						}
					});
			delSourceButton.setToolTipText(Messages
					.getString("JdbcSourceEditor.deletesource_tooltip")); //$NON-NLS-1$
			delSourceButton.setIcon(ReportResources.getInstance().getIcon(
					"del.gif")); //$NON-NLS-1$
		}
		return delSourceButton;
	}

	protected void deleteSource() {
		if (sourceList.size() > 0) {
			if (sourcesJList.getSelectedIndex() >= 0) {
				sourceList.remove(sourcesJList.getSelectedIndex());
				sourcesJList.setListData(sourceList);
			}
		}
	}

	/**
	 * This method initializes editSourceButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getEditSourceButton() {
		if (editSourceButton == null) {
			editSourceButton = new JButton();
			editSourceButton.setIcon(ReportResources.getInstance().getIcon(
					"edit_item.gif")); //$NON-NLS-1$
			editSourceButton.setToolTipText(Messages
					.getString("JdbcSourceEditor.editsource_tooltip")); //$NON-NLS-1$
			editSourceButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							showSourceEditor();
						}
					});
		}
		return editSourceButton;
	}

	/**
	 * This method initializes upButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getUpButton() {
		if (upButton == null) {
			upButton = new JButton();
			upButton.setIcon(ReportResources.getInstance().getIcon("prev.gif")); //$NON-NLS-1$
			upButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int i = jTable.getSelectedRow();
					if (i > 0) {
						getQueryModel().swap(i, i - 1);
						getQueryModel().fireTableDataChanged();
						jTable.changeSelection(i - 1, 0, false, false);
						setDirty(true);
					}
				}
			});
		}
		return upButton;
	}

	/**
	 * This method initializes downButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getDownButton() {
		if (downButton == null) {
			downButton = new JButton();
			downButton.setIcon(ReportResources.getInstance()
					.getIcon("next.gif")); //$NON-NLS-1$
			downButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int i = jTable.getSelectedRow();
					if (i < getQueryModel().getRowCount() - 1) {
						getQueryModel().swap(i, i + 1);
						getQueryModel().fireTableDataChanged();
						jTable.changeSelection(i + 1, 0, false, false);
						setDirty(true);
					}
				}
			});
		}
		return downButton;
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

	private static class JdbcSourceUndo implements UndoItem {

		private TemplateBook reportBook;
		List<JdbcReportSource> list = new ArrayList<>();

		public JdbcSourceUndo(TemplateBook reportBook) {
			this.reportBook = reportBook;
			this.list.addAll(reportBook.getSourcesList());
		}

		public void clear() {
			reportBook = null;
			list = null;
		}

		public String getDescription() {
			return "Data Source";
		}

		public UndoItem undo() {
			List<JdbcReportSource> tmpList = list;
			list = new ArrayList<>();
			list.addAll(reportBook.getSourcesList());

			reportBook.getSourcesList().clear();
			reportBook.getSourcesList().addAll(tmpList);
			return this;
		}

	}

}
