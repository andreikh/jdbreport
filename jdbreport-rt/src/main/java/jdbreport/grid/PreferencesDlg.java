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

import java.awt.Frame;
import java.awt.HeadlessException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.BorderLayout;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.JCheckBox;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;

import jdbreport.grid.undo.PreferencesUndo;
import jdbreport.grid.undo.UndoItem;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportModel;
import jdbreport.model.io.pdf.PdfFileType;
import jdbreport.util.Utils;
import jdbreport.view.ReportEditorPane;

import java.awt.Dialog;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

/**
 * @version 3.0 13.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class PreferencesDlg extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static final String OK_COMMAND = "ok";
	private static final String CANCEL_COMMAND = "cancel";
	private static final String ADD_FONT_PATH_COMMAND = "addFontPath";
	private static final String DELETE_FONT_PATH_COMMAND = "delFontPath";
	private static final String DEFAULT_FONT_COMMAND = "defaultFont";
	public static final int CANCEL = 1;
	public static final int OK = 0;

	private JPanel jPanel = null;
	private JPanel buttonsPanel = null;
	private JTabbedPane tabbedPane = null;
	private JButton okButton = null;
	private JButton cancelButton = null;
	private JPanel sheetPanel = null;
	private JPanel jPanel2 = null;
	private JCheckBox showGridBox = null;
	private JPanel generalPanel = null;

	private int modalResult = CANCEL;
	private JCheckBox canUpdatePageBox = null;
	private JCheckBox resizingRowsBox = null;
	private JCheckBox resizingColumnsBox = null;
	private JCheckBox columnsMovedBox = null;
	private JCheckBox rowsMovedBox = null;
	private JTextField nameReportField = null;
	private JTextField nameSheetField = null;
	private JCheckBox editableBox;
	private JCheckBox visibleBox;
	private JReportGrid grid;
	private JCheckBox hideHeadersBox;
	private UndoListener undoListener;
	private JPanel spreadsheetPanel;
	private JTextField odsField;
	private JTextField excelField;
	private JPanel pdfPanel;
	private JPanel pdfToolBar;
	private JTable fontPathTable;
	private JButton addPathButton;
	private JButton delPathButton;
	private Vector<String> fontPaths;

	private JTextField defaultFontField;

	private JButton defaultFontButton;

	private JCheckBox globalPageNumberBox;

	private JRadioButton topDownButton;

	private JRadioButton leftRightButton;

	private JTextField templateField;

	private JCheckBox printThroughPdfBox;

	private ReportBook reportBook;

	private JCheckBox hideFirstHeaderBox;

	public PreferencesDlg(Frame owner, boolean modal) throws HeadlessException {
		super(owner, modal);
		initialize();
	}

	public PreferencesDlg(Dialog owner, boolean modal) throws HeadlessException {
		super(owner, modal);
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
	 */
	private void initialize() {
		this.setSize(new java.awt.Dimension(500, 450));
		this.setContentPane(getJPanel());
		this.setModal(true);
		this.setTitle(Messages.getString("PreferencesDlg.0")); //$NON-NLS-1$
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				grid = null;
			}
		});
		Utils.screenCenter(this, getOwner());
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
			jPanel.add(getButtonsPanel(), java.awt.BorderLayout.SOUTH);
			jPanel.add(getTabbedPane(), java.awt.BorderLayout.CENTER);
		}
		return jPanel;
	}

	/**
	 * This method initializes buttonsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = new JPanel(new FlowLayout(java.awt.FlowLayout.RIGHT));
			buttonsPanel.add(getOkButton());
			buttonsPanel.add(getCancelButton());
		}
		return buttonsPanel;
	}

	/**
	 * This method initializes tabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	public JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = createTabbedPane();
		}
		return tabbedPane;
	}

	protected JTabbedPane createTabbedPane() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane
				.addTab(Messages.getString("PreferencesDlg.1"), null, getGeneralPanel(), null); //$NON-NLS-1$
		if (ReportBook.pdfExists()) {
			tabbedPane
					.addTab(Messages.getString("PreferencesDlg.exportToPDF"), null, getPdfPanel(), null); //$NON-NLS-1$
		}
		return tabbedPane;
	}

	/**
	 * This method initializes okButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText(Messages.getString("PreferencesDlg.3"));//$NON-NLS-1$
			okButton.setActionCommand(OK_COMMAND);
			okButton.addActionListener(this);
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
			cancelButton.setText(Messages.getString("PreferencesDlg.4"));//$NON-NLS-1$
			cancelButton.setActionCommand(CANCEL_COMMAND);
			cancelButton.addActionListener(this);
		}
		return cancelButton;
	}

	private JPanel getPdfPanel() {
		if (pdfPanel == null) {
			pdfPanel = new JPanel(new BorderLayout());

			fontPaths = new Vector<>();
			String defaultFont = "";

			if (ReportBook.pdfExists()) {
				PdfFileType fileType = (PdfFileType) ReportBook
						.getFileTypeClass(ReportBook.PDF);
				if (fileType != null) {
					fontPaths.addAll(fileType.getFontPaths());
					defaultFont = fileType.getDefaultFont();
				}
			}

			pdfPanel.add(getPdfToolBar(), BorderLayout.EAST);

			JPanel panel = new JPanel(new BorderLayout());
			panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			panel.add(
					new JLabel(Messages.getString("PreferencesDlg.folderLabel")), BorderLayout.NORTH); //$NON-NLS-1$
			panel.add(new JScrollPane(getFontPathTable()), BorderLayout.CENTER);
			pdfPanel.add(panel, BorderLayout.CENTER);

			JPanel bottomPanel = new JPanel(new BorderLayout());

			JPanel panel2 = new JPanel(new GridBagLayout());
			panel2.setBorder(BorderFactory.createTitledBorder(Messages
					.getString("PreferencesDlg.defaultFont"))); //$NON-NLS-1$
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.insets = new Insets(4, 4, 4, 4);
			constraints.anchor = GridBagConstraints.NORTHWEST;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.weightx = 0.1;
			constraints.weighty = 0.1;

			defaultFontField = new JTextField(defaultFont != null ? defaultFont
					: ""); //$NON-NLS-1$
			panel2.add(defaultFontField, constraints);
			bottomPanel.add(panel2, BorderLayout.NORTH);

			JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel3.add(getPrintThroughPdfBox());
			bottomPanel.add(panel3, BorderLayout.CENTER);

			pdfPanel.add(bottomPanel, BorderLayout.SOUTH);

		}
		return pdfPanel;
	}

	private JTable getFontPathTable() {
		if (fontPathTable == null) {
			fontPathTable = new JTable(new AbstractTableModel() {

				private static final long serialVersionUID = 1L;

				public int getColumnCount() {
					return 1;
				}

				public int getRowCount() {
					return fontPaths.size();
				}

				public Object getValueAt(int rowIndex, int columnIndex) {
					return fontPaths.get(rowIndex);
				}

			});
			fontPathTable.setTableHeader(null);
		}
		return fontPathTable;
	}

	private JPanel getPdfToolBar() {
		if (pdfToolBar == null) {
			pdfToolBar = new JPanel(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.insets = new Insets(40, 4, 4, 4);
			constraints.anchor = GridBagConstraints.NORTHWEST;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			pdfToolBar.add(getAddPathButton(), constraints);

			constraints = new GridBagConstraints();
			constraints.insets = new Insets(4, 4, 4, 4);
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.anchor = GridBagConstraints.NORTHWEST;
			constraints.gridy = 1;

			pdfToolBar.add(getDelPathButton(), constraints);

			constraints = new GridBagConstraints();
			constraints.insets = new Insets(40, 4, 4, 4);
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.anchor = GridBagConstraints.NORTHWEST;
			constraints.gridy = 2;
			constraints.weightx = 0.1;
			constraints.weighty = 0.1;

			pdfToolBar.add(getDefaultFontButton(), constraints);

		}
		return pdfToolBar;
	}

	private JButton getDefaultFontButton() {
		if (defaultFontButton == null) {
			defaultFontButton = new JButton(
					Messages.getString("PreferencesDlg.defaultFont")); //$NON-NLS-1$
			defaultFontButton.setActionCommand(DEFAULT_FONT_COMMAND);
			defaultFontButton.addActionListener(this);
		}
		return defaultFontButton;
	}

	private JButton getDelPathButton() {
		if (delPathButton == null) {
			delPathButton = new JButton(
					Messages.getString("PreferencesDlg.delete")); //$NON-NLS-1$
			delPathButton.setActionCommand(DELETE_FONT_PATH_COMMAND);
			delPathButton.addActionListener(this);
		}
		return delPathButton;
	}

	private JButton getAddPathButton() {
		if (addPathButton == null) {
			addPathButton = new JButton(
					Messages.getString("PreferencesDlg.add")); //$NON-NLS-1$
			addPathButton.setActionCommand(ADD_FONT_PATH_COMMAND);
			addPathButton.addActionListener(this);
		}
		return addPathButton;
	}

	/**
	 * This method initializes sheetPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	protected JPanel getSheetPanel() {
		if (sheetPanel == null) {
			sheetPanel = new JPanel(new BorderLayout());
			sheetPanel.add(getJPanel2(), BorderLayout.NORTH);
		}
		return sheetPanel;
	}

	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints10.gridy = 0;
			gridBagConstraints10.weightx = 1.0;
			gridBagConstraints10.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints10.insets = new Insets(8, 4, 4, 10);
			gridBagConstraints10.gridx = 1;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints9.insets = new Insets(8, 10, 4, 4);
			gridBagConstraints9.gridy = 0;
			JLabel jLabel = new JLabel();
			jLabel.setText(Messages.getString("PreferencesDlg.name")); //$NON-NLS-1$
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.insets = new Insets(2, 10, 2, 4);
			gridBagConstraints8.gridy = 7;
			gridBagConstraints8.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints8.gridwidth = 2;
			gridBagConstraints8.gridx = 0;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.insets = new Insets(2, 10, 2, 4);
			gridBagConstraints7.gridy = 6;
			gridBagConstraints7.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints7.gridwidth = 2;
			gridBagConstraints7.gridx = 0;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.insets = new Insets(2, 10, 2, 4);
			gridBagConstraints6.gridy = 5;
			gridBagConstraints6.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints6.gridwidth = 2;
			gridBagConstraints6.gridx = 0;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.insets = new Insets(2, 10, 2, 4);
			gridBagConstraints5.gridy = 4;
			gridBagConstraints5.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints5.gridwidth = 2;
			gridBagConstraints5.gridx = 0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.insets = new Insets(2, 10, 2, 4);
			gridBagConstraints4.gridy = 3;
			gridBagConstraints4.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints4.gridwidth = 2;
			gridBagConstraints4.gridx = 0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new Insets(4, 10, 2, 4);
			gridBagConstraints3.gridy = 2;
			gridBagConstraints3.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints3.gridwidth = 2;
			gridBagConstraints3.gridx = 0;

			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.insets = new Insets(2, 10, 2, 4);
			gridBagConstraints11.gridy = 8;
			gridBagConstraints11.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints11.gridwidth = 2;

			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.insets = new Insets(2, 10, 2, 4);
			gridBagConstraints12.gridy = 9;
			gridBagConstraints12.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints12.gridwidth = 2;

			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.insets = new Insets(2, 10, 2, 4);
			gridBagConstraints13.gridy = 10;
			gridBagConstraints13.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints13.gridwidth = 2;

			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.insets = new Insets(2, 10, 2, 4);
			gridBagConstraints14.gridy = 11;
			gridBagConstraints14.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints14.gridwidth = 2;

			jPanel2 = new JPanel();
			jPanel2.setLayout(new GridBagLayout());
			jPanel2.add(getCanUpdatePageBox(), gridBagConstraints4);
			jPanel2.add(getResizingColumnsBox(), gridBagConstraints5);
			jPanel2.add(getColumnsMovedBox(), gridBagConstraints6);
			jPanel2.add(getResizingRowsBox(), gridBagConstraints7);
			jPanel2.add(getRowsMovedBox(), gridBagConstraints8);
			jPanel2.add(jLabel, gridBagConstraints9);
			jPanel2.add(getNameSheetField(), gridBagConstraints10);
			jPanel2.add(getEditableBox(), gridBagConstraints11);
			jPanel2.add(getVisibleBox(), gridBagConstraints12);
			jPanel2.add(getHideHeadersBox(), gridBagConstraints13);
			jPanel2.add(getHideFirstHeaderBox(), gridBagConstraints14);

			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.insets = new Insets(2, 10, 2, 4);
			gridBagConstraints15.gridy = 12;
			gridBagConstraints15.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints15.gridwidth = 2;
			gridBagConstraints15.weighty = 0.1;

			ButtonGroup group = new ButtonGroup();
			group.add(getTopDownButton());
			group.add(getLeftRightButton());
			JPanel panel = new JPanel(new GridLayout(2, 1));
			panel.setBorder(BorderFactory.createTitledBorder(Messages
					.getString("PreferencesDlg.print_direction")));
			panel.add(getTopDownButton());
			panel.add(getLeftRightButton());
			jPanel2.add(panel, gridBagConstraints15);
		}
		return jPanel2;
	}

	private JRadioButton getLeftRightButton() {
		if (leftRightButton == null) {
			leftRightButton = new JRadioButton(
					Messages.getString("PreferencesDlg.print_left_right"));
		}
		return leftRightButton;
	}

	private JRadioButton getTopDownButton() {
		if (topDownButton == null) {
			topDownButton = new JRadioButton(
					Messages.getString("PreferencesDlg.print_top_down")); //$NON-NLS-1$
		}
		return topDownButton;
	}

	private JCheckBox getVisibleBox() {
		if (visibleBox == null) {
			visibleBox = new JCheckBox();
			visibleBox.setText(Messages.getString("PreferencesDlg.visible")); //$NON-NLS-1$
		}
		return visibleBox;
	}

	private JCheckBox getHideFirstHeaderBox() {
		if (hideFirstHeaderBox == null) {
			hideFirstHeaderBox = new JCheckBox();
			hideFirstHeaderBox.setText(Messages.getString("PreferencesDlg.hideFirstNumber"));
		}
		return hideFirstHeaderBox;
	}

	private JCheckBox getEditableBox() {
		if (editableBox == null) {
			editableBox = new JCheckBox();
			editableBox.setText(Messages.getString("PreferencesDlg.editing"));
		}
		return editableBox;
	}

	private JCheckBox getPrintThroughPdfBox() {
		if (printThroughPdfBox == null) {
			printThroughPdfBox = new JCheckBox();
			printThroughPdfBox.setText(Messages
					.getString("PreferencesDlg.print_through_pdf")); //$NON-NLS-1$
		}
		return printThroughPdfBox;
	}

	/**
	 * This method initializes showGridBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getShowGridBox() {
		if (showGridBox == null) {
			showGridBox = new JCheckBox();
			showGridBox.setText(Messages.getString("PreferencesDlg.6")); //$NON-NLS-1$
		}
		return showGridBox;
	}

	/**
	 * This method initializes globalPageNumberBox
	 * 
	 * @return javax.swing.JCheckBox
	 * @since 1.4
	 */
	private JCheckBox getGlobalPageNumberBox() {
		if (globalPageNumberBox == null) {
			globalPageNumberBox = new JCheckBox();
			globalPageNumberBox.setText(Messages
					.getString("PreferencesDlg.globalpagenumber")); //$NON-NLS-1$
			globalPageNumberBox.setToolTipText(Messages
					.getString("PreferencesDlg.globalpagenumber_tip")); //$NON-NLS-1$
		}
		return globalPageNumberBox;
	}

	/**
	 * This method initializes generalPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getGeneralPanel() {
		if (generalPanel == null) {
			generalPanel = createGeneralPanel();
		}
		return generalPanel;
	}

	protected JPanel createGeneralPanel() {
		JPanel generalPanel = new JPanel();
		generalPanel.setLayout(new GridBagLayout());

		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints2.insets = new Insets(8, 10, 4, 4);
		generalPanel
				.add(new JLabel(Messages.getString("PreferencesDlg.name")), gridBagConstraints2); //$NON-NLS-1$

		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints1.insets = new Insets(8, 4, 4, 10);
		gridBagConstraints1.weightx = 1.0;
		generalPanel.add(getNameReportField(), gridBagConstraints1);

		GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		gridBagConstraints12.gridx = 0;
		gridBagConstraints12.gridy = 1;
		gridBagConstraints12.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints12.insets = new Insets(8, 10, 4, 4);
		generalPanel
				.add(new JLabel(Messages.getString("PreferencesDlg.template")), gridBagConstraints12); //$NON-NLS-1$

		GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
		gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints13.gridx = 1;
		gridBagConstraints13.gridy = 1;
		gridBagConstraints13.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints13.insets = new Insets(8, 4, 4, 10);
		gridBagConstraints13.weightx = 1.0;
		generalPanel.add(getTemplateField(), gridBagConstraints13);

		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints3.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints3.insets = new Insets(8, 10, 4, 4);
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.gridy = 2;
		gridBagConstraints3.gridwidth = 2;
		generalPanel.add(getShowGridBox(), gridBagConstraints3);

		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints5.insets = new Insets(4, 10, 4, 4);
		gridBagConstraints5.gridy = 3;
		gridBagConstraints5.gridwidth = 2;
		generalPanel.add(getGlobalPageNumberBox(), gridBagConstraints5);

		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints4.insets = new Insets(8, 10, 4, 10);
		gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.gridy = 4;
		gridBagConstraints4.gridwidth = 2;
		gridBagConstraints4.weighty = 0.1;
		gridBagConstraints4.weightx = 1.0;
		generalPanel.add(getSpreadsheetPanel(), gridBagConstraints4);

		return generalPanel;
	}

	protected void save() {
		pushUndo(new PreferencesUndo(grid, getReportBook()));
		ReportModel reportModel = grid.getReportModel();
		reportModel.setReportTitle(getNameSheetField().getText());
		reportModel.setHideFirstHeader(getVisibleBox().isSelected());
		reportModel.setCanUpdatePages(getCanUpdatePageBox().isSelected());
		reportModel.setRowSizing(getResizingRowsBox().isSelected());
		reportModel.setColSizing(getResizingColumnsBox().isSelected());
		reportModel.setRowMoving(getRowsMovedBox().isSelected());
		reportModel.setColMoving(getColumnsMovedBox().isSelected());
		reportModel.setEditable(getEditableBox().isSelected());
		reportModel.setVisible(getVisibleBox().isSelected());
		reportModel.setShowHeader(!getHideHeadersBox().isSelected());
		reportModel.setShowRowHeader(!getHideHeadersBox().isSelected());
		reportModel.setHideFirstHeader(getHideFirstHeaderBox().isSelected());
		reportModel.setPrintLeftToRight(getLeftRightButton().isSelected());

		getReportBook().setReportCaption(getNameReportField().getText());
		getReportBook().setShowGrid(getShowGridBox().isSelected());
		getReportBook().setGlobalPageNumber(
				getGlobalPageNumberBox().isSelected());

		if (ReportBook.pdfExists()) {
			PdfFileType fileType = (PdfFileType) ReportBook
					.getFileTypeClass(ReportBook.PDF);
			if (fileType != null) {
				fileType.setFontPaths(fontPaths);
				fileType.setDefaultFont(defaultFontField.getText());
			}
			getReportBook().setPrintThroughPdf(
					getPrintThroughPdfBox().isSelected());
		}

	}

	public String getExcelCommand() {
		return getExcelField().getText();
	}

	public void setExcelCommand(String text) {
		getExcelField().setText(text);
		getExcelField().setSelectionStart(0);
		getExcelField().setSelectionEnd(0);
	}

	public String getODSCommand() {
		return getODSField().getText();
	}

	public void setODSCommand(String text) {
		getODSField().setText(text);
		getODSField().setSelectionStart(0);
		getODSField().setSelectionEnd(0);
	}

	private JCheckBox getHideHeadersBox() {
		if (hideHeadersBox == null) {
			hideHeadersBox = new JCheckBox();
			hideHeadersBox.setText(Messages
					.getString("PreferencesDlg.hide_headers")); //$NON-NLS-1$
		}
		return hideHeadersBox;
	}

	public int getModalResult() {
		return modalResult;
	}

	public void setReportGrid(JReportGrid grid, ReportBook reportBook) {
		this.grid = grid;
		this.reportBook = reportBook;
		if (grid == null)
			return;
		ReportModel reportModel = grid.getReportModel();
		getNameReportField().setText(getReportBook().getReportCaption());
		getTemplateField().setText(
				getReportBook().getSourceTemplate() != null ? getReportBook()
						.getSourceTemplate() : "");
		getShowGridBox().setSelected(getReportBook().isShowGrid());
		getGlobalPageNumberBox().setSelected(
				getReportBook().isGlobalPageNumber());
		getNameSheetField().setText(reportModel.getReportTitle());
		getCanUpdatePageBox().setSelected(reportModel.isCanUpdatePages());
		getResizingColumnsBox().setSelected(reportModel.isColSizing());
		getResizingRowsBox().setSelected(reportModel.isRowSizing());
		getColumnsMovedBox().setSelected(reportModel.isColMoving());
		getRowsMovedBox().setSelected(reportModel.isRowMoving());
		getEditableBox().setSelected(reportModel.isEditable());
		getVisibleBox().setSelected(reportModel.isVisible());
		getHideHeadersBox().setSelected(!reportModel.isShowHeader());
		getHideFirstHeaderBox().setSelected(reportModel.isHideFirstHeader());
		getLeftRightButton().setSelected(reportModel.isPrintLeftToRight());
		getTopDownButton().setSelected(!reportModel.isPrintLeftToRight());

		if (ReportBook.pdfExists()) {
			getPrintThroughPdfBox().setSelected(
					getReportBook().isPrintThroughPdf());
		}
	}

	protected ReportBook getReportBook() {
		return reportBook;
	}

	/**
	 * This method initializes canUpdatePageBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCanUpdatePageBox() {
		if (canUpdatePageBox == null) {
			canUpdatePageBox = new JCheckBox();
			canUpdatePageBox.setText(Messages.getString("PreferencesDlg.8")); //$NON-NLS-1$
		}
		return canUpdatePageBox;
	}

	/**
	 * This method initializes resizingRowsBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getResizingRowsBox() {
		if (resizingRowsBox == null) {
			resizingRowsBox = new JCheckBox();
			resizingRowsBox.setText(Messages.getString("PreferencesDlg.9")); //$NON-NLS-1$
		}
		return resizingRowsBox;
	}

	/**
	 * This method initializes resizingColumnsBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getResizingColumnsBox() {
		if (resizingColumnsBox == null) {
			resizingColumnsBox = new JCheckBox();
			resizingColumnsBox.setText(Messages.getString("PreferencesDlg.10")); //$NON-NLS-1$
		}
		return resizingColumnsBox;
	}

	/**
	 * This method initializes columnsMovedBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getColumnsMovedBox() {
		if (columnsMovedBox == null) {
			columnsMovedBox = new JCheckBox();
			columnsMovedBox.setText(Messages.getString("PreferencesDlg.11")); //$NON-NLS-1$
		}
		return columnsMovedBox;
	}

	/**
	 * This method initializes rowsMovedBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getRowsMovedBox() {
		if (rowsMovedBox == null) {
			rowsMovedBox = new JCheckBox();
			rowsMovedBox.setText(Messages.getString("PreferencesDlg.12")); //$NON-NLS-1$
		}
		return rowsMovedBox;
	}

	/**
	 * This method initializes nameReportField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getNameReportField() {
		if (nameReportField == null) {
			nameReportField = new JTextField();
		}
		return nameReportField;
	}

	private JTextField getTemplateField() {
		if (templateField == null) {
			templateField = new JTextField();
			templateField.setEditable(false);
		}
		return templateField;
	}

	/**
	 * This method initializes nameSheetField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getNameSheetField() {
		if (nameSheetField == null) {
			nameSheetField = new JTextField();
		}
		return nameSheetField;
	}

	private JPanel getSpreadsheetPanel() {
		if (spreadsheetPanel == null) {
			spreadsheetPanel = new JPanel(new GridBagLayout());
			spreadsheetPanel.setBorder(BorderFactory.createEtchedBorder());

			GridBagConstraints constraints = new GridBagConstraints();
			constraints.anchor = GridBagConstraints.NORTHWEST;
			constraints.insets = new Insets(8, 8, 2, 4);
			JLabel label = new JLabel(Messages.getString("PreferencesDlg.13")); //$NON-NLS-1$
			spreadsheetPanel.add(label, constraints);

			constraints = new GridBagConstraints();
			constraints.anchor = GridBagConstraints.NORTHWEST;
			constraints.insets = new Insets(2, 4, 4, 4);
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.gridy = 1;
			constraints.weightx = 1.0;
			constraints.weighty = 1.0;
			spreadsheetPanel.add(getODSField(), constraints);

			if (ReportEditorPane.isWindows) {
				constraints = new GridBagConstraints();
				constraints.anchor = GridBagConstraints.NORTHWEST;
				constraints.insets = new Insets(4, 8, 2, 4);
				constraints.gridy = 2;
				JLabel label2 = new JLabel(
						Messages.getString("PreferencesDlg.14")); //$NON-NLS-1$
				spreadsheetPanel.add(label2, constraints);

				constraints = new GridBagConstraints();
				constraints.anchor = GridBagConstraints.NORTHWEST;
				constraints.insets = new Insets(2, 4, 4, 4);
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 2.0;
				constraints.weighty = 2.0;
				constraints.gridy = 3;
				spreadsheetPanel.add(getExcelField(), constraints);
			}
		}
		return spreadsheetPanel;
	}

	private JTextField getODSField() {
		if (odsField == null) {
			odsField = new JTextField();
		}
		return odsField;
	}

	private JTextField getExcelField() {
		if (excelField == null) {
			excelField = new JTextField();
		}
		return excelField;
	}

	public void actionPerformed(ActionEvent e) {
		if (CANCEL_COMMAND.equals(e.getActionCommand())) {
			setVisible(false);
			modalResult = CANCEL;
		} else if (OK_COMMAND.equals(e.getActionCommand())) {
			save();
			setVisible(false);
			modalResult = OK;
		} else if (ADD_FONT_PATH_COMMAND.equals(e.getActionCommand())) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle(Messages
					.getString("PreferencesDlg.dialogTitle")); //$NON-NLS-1$
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				if (fileChooser.getSelectedFile() != null) {
					String path = fileChooser.getSelectedFile().getPath();
					if (!fontPaths.contains(path)) {
						fontPaths.add(path);
						getFontPathTable().revalidate();
						getFontPathTable().repaint();
					}
				}
			}
		} else if (DELETE_FONT_PATH_COMMAND.equals(e.getActionCommand())) {
			int i = getFontPathTable().getSelectedRow();
			if (i >= 0 && i < fontPaths.size()) {
				fontPaths.remove(i);
				getFontPathTable().revalidate();
			}
		} else if (DEFAULT_FONT_COMMAND.equals(e.getActionCommand())) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle(Messages
					.getString("PreferencesDlg.selectDefaultFont")); //$NON-NLS-1$
			fileChooser.setFileFilter(new FileFilter() {

				@Override
				public boolean accept(File f) {
					String fileName = f.getName().toLowerCase();
					return f.isDirectory() || fileName.endsWith(".ttf") //$NON-NLS-1$
							|| fileName.endsWith(".otf") //$NON-NLS-1$
							|| fileName.endsWith(".afm") //$NON-NLS-1$
							|| fileName.endsWith(".ttc"); //$NON-NLS-1$
				}

				@Override
				public String getDescription() {
					return Messages.getString("PreferencesDlg.openDescription"); //$NON-NLS-1$
				}

			});
			if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				if (fileChooser.getSelectedFile() != null) {
					String path = fileChooser.getSelectedFile().getPath();
					defaultFontField.setText(path);
				}
			}

		}
	}
}
