/*
 * JDBReport Generator
 *
 * Copyright (C) 2008-2018 Andrey Kholmanskih
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
package jdbreport.view;

import jdbreport.actions.ToggleAction;
import jdbreport.grid.*;
import jdbreport.grid.NumericDlg.Orientation;
import jdbreport.grid.ReportAction.BasedAction;
import jdbreport.grid.undo.AbstractGridUndo;
import jdbreport.grid.undo.UndoItem;
import jdbreport.model.*;
import jdbreport.model.event.CellSelectListener;
import jdbreport.model.io.pdf.PdfFileType;
import jdbreport.model.io.pdf.ReportFont;
import jdbreport.model.svg.SVGValue;
import jdbreport.util.HelpWindow;
import jdbreport.util.Utils;
import jdbreport.util.xml.XMLProperties;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Andrey Kholmanskih
 * @version 3.1.3 30.06.2018
 */
public class ReportEditorPane extends ReportPane implements CellSelectListener,
        ActionListener {

    private static final String LF_CHANGE_COMMAND = "lf_change";
    private static final String FONT_SIZE_COMMAND = "font_size";
    private static final String SHOW_BORDER_DLG_COMMAND = "show_border_dlg";

    private static final Logger logger = Logger.getLogger(ReportEditorPane.class
            .getName());

    private static final long serialVersionUID = 1L;

    private static boolean excel_exists = true;

    private static boolean ods_exists = true;

    private BasedAction rowHeightAction;

    private BasedAction columnWidthAction;

    private ToggleAction alignLeftAction;

    private ToggleAction alignRightAction;

    private ToggleAction alignCenterAction;

    private ToggleAction alignTopAction;

    private ToggleAction alignVCenterAction;

    private ToggleAction alignBottomAction;

    private Action cellEditorAction;

    private Action cellPropertyAction;

    private Action backgroundAction;

    private ToggleAction boldAction;

    private ToggleAction underlineAction;

    private ToggleAction italicAction;

    private ToggleAction justifyAction;

    private JToolBar formatBar;

    private JPanel fontPanel;

    private JComboBox<Integer> fontSizeBox = null;

    private JComboBox<String> fontNameBox;

    private int enableAction = 0;

    private JPanel coolBar = null;

    private JToolBar standartBar;

    private ToggleAction autoHeightAction;

    private ToggleAction unionCellAction;

    private FontCellRenderer fontCellRenderer = null;

    private ToggleAction rowBreakAction;

    private ToggleAction columnBreakAction;

    private Action propertiesAction;

    private Action addSheetAction;

    private Action delSheetAction;

    private Action loadSheetAction;

    private Action saveSheetAction;

    private Action moveLeftSheetAction;

    private Action moveRightSheetAction;

    private Action openAction = null;

    protected Action saveAction = null;

    private Action saveAsAction = null;

    private Action printAction;

    private Action previewAction;

    private Action export2CalcAction;

    private JMenu imageMenu;

    private Action imageAction;

    private ToggleAction scaleImageAction;

    private Action printSheetAction;

    private JCheckBoxMenuItem scaleImageItem;

    private Action pageSetupAction;

    private Action deleteImageAction;

    private JLabel decimalLabel;

    private Action incDecimalAction;

    private Action decDecimalAction;

    private ToggleAction roundAction;

    private PreferencesDlg preferences;

    private JButton undoButton;

    private JButton redoButton;

    private Action aboutAction;

    private Action newAction;

    private Action exitAction;

    private JMenu editMenu;

    private JMenu fileMenu;

    private JMenu lfMenu;

    private JMenu helpMenu;

    private JMenu sheetMenu;

    private JButton borderButton;

    private BorderDialog borderDialog;

    protected String excelCommand;

    protected String odsCommand;

    private Action saveImageAction;
    private HelpWindow helpWindow;

    public static final boolean isWindows = System.getProperty("os.name")
            .startsWith("Windows");

    public ReportEditorPane(XMLProperties properties) {
        super(properties);
        initialize();
    }

    private void initialize() {
        setGridMenu(createGridMenu());
        add(getCoolBar(), java.awt.BorderLayout.NORTH);
        setTabMenu(createTabMenu());
        addGridListeners(getFocusedGrid());
    }

    protected JPopupMenu createTabMenu() {
        JPopupMenu tabMenu = new JPopupMenu();
        tabMenu.add(addSheetAction);
        tabMenu.add(delSheetAction);
        tabMenu.add(loadSheetAction);
        tabMenu.add(saveSheetAction);
        tabMenu.addSeparator();
        tabMenu.add(moveLeftSheetAction);
        tabMenu.add(moveRightSheetAction);
        tabMenu.addSeparator();
        tabMenu.add(printSheetAction);
        return tabMenu;
    }

    protected JPopupMenu createGridMenu() {
        JPopupMenu gridMenu = new JPopupMenu();
        fillGridMenu(gridMenu);
        gridMenu.addPopupMenuListener(new PopupMenuListener() {

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                Cell cell = getFocusedGrid().getSelectedCell();
                getUnionCellAction().setSelected(cell.isSpan());
                getScaleImageItem().setState(cell.isScaleIcon());
                boolean imageExists = cell.getPicture() != null
                        || cell.getValue() instanceof SVGValue;
                getDeleteImageAction().setEnabled(imageExists);
                getSaveImageAction().setEnabled(imageExists);
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
            }

        });

        return gridMenu;
    }

    protected void fillGridMenu(JPopupMenu menu) {
        menu.add(cellPropertyAction);
        menu.add(cellEditorAction);
        menu.add(createUnionCellItem());
        menu.add(getImageMenu());
        menu.addSeparator();
        menu.add(rowHeightAction);
        menu.add(columnWidthAction);
        fillAdvancedGridMenu(menu);
        menu.addSeparator();
        menu.add(getCutAction());
        menu.add(getCopyAction());
        menu.add(getPasteAction());
        menu.add(getDeleteAction());
    }

    protected void fillAdvancedGridMenu(JPopupMenu menu) {

    }

    public void setRowHeight() {
        int height = getFocusedGrid().getReportModel().getRowHeight(
                getFocusedGrid().getSelectedRow());
        int unit = properties.getInt("Units", NumericDlg.PX);
        NumericDlg dlg;
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof Frame) {
            dlg = new NumericDlg((Frame) w, ReportResources.getInstance()
                    .getString("row_height_dialog.caption"), height,
                    unit, Orientation.Vertical);
        } else {
            dlg = new NumericDlg((Dialog) w, ReportResources.getInstance()
                    .getString("row_height_dialog.caption"), height,
                    unit, Orientation.Vertical);
        }
        dlg.setVisible(true);
        if (dlg.getResult() == NumericDlg.OK) {
            height = (int) dlg.getValue(NumericDlg.PX);
            int minRow = getFocusedGrid().getSelectionModel()
                    .getMinSelectionIndex();
            int maxRow = getFocusedGrid().getSelectionModel()
                    .getMaxSelectionIndex();
            getFocusedGrid().setRowsHeight(minRow, maxRow, height);
        }
        properties.put("Units", dlg.getUnits());
    }

    public void setColumnWidth() {
        int width = getFocusedGrid().getReportModel().getColumnWidth(
                getFocusedGrid().getSelectedColumn());
        int unit = properties.getInt("Units", NumericDlg.PX);
        NumericDlg dlg;
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof Frame) {
            dlg = new NumericDlg((Frame) w, ReportResources.getInstance()
                    .getString("column_width_dialog.caption"), width,
                    unit, Orientation.Horizontal);
        } else {
            dlg = new NumericDlg((Dialog) w, ReportResources.getInstance()
                    .getString("column_width_dialog.caption"), width,
                    unit, Orientation.Horizontal);
        }
        dlg.setVisible(true);
        if (dlg.getResult() == NumericDlg.OK) {
            width = (int) dlg.getValue(NumericDlg.PX);
            int minColumn = getFocusedGrid().getSelectedColumn();
            int maxColumn = minColumn
                    + getFocusedGrid().getSelectedColumnCount() - 1;
            getFocusedGrid().setColumnsWidth(minColumn, maxColumn, width);
        }
        properties.put("Units", dlg.getUnits());
    }

    protected void initActions() {
        super.initActions();
        cellEditorAction = ReportAction.createGridAction(
                ReportAction.CELL_EDITOR_ACTION, this);
        cellPropertyAction = ReportAction.createGridAction(
                ReportAction.CELL_PROPERTY_ACTION, this);
        backgroundAction = ReportAction.createGridAction(
                ReportAction.BACKGROUND_ACTION, this);
        pageSetupAction = ReportAction.createGridAction(
                ReportAction.PAGE_SETUP_ACTION, this);
        printAction = new ReportAction.BasedAction(
                ReportAction.PRINT_REPORT_ACTION) {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                print();
            }

        };
        previewAction = new ReportAction.BasedAction(
                ReportAction.PREVIEW_ACTION) {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                preview();
            }

        };

        export2CalcAction = new Export2CalcAction();

        rowHeightAction = new ReportAction.BasedAction("row_height") {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                setRowHeight();
            }

        };
        columnWidthAction = new ReportAction.BasedAction("column_width") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                setColumnWidth();
            }

        };
        aboutAction = new ReportAction.BasedAction("help_about") {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                showAbout();
            }

        };
        addSheetAction = new ReportAction.BasedAction("add_sheet") {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                int index = getReportBook().add();
                setFocusedGrid(index);
                updateSheetActions();
                pushUndo(new AddSheetUndo(getFocusedGrid()));
            }

        };
        delSheetAction = new ReportAction.BasedAction("del_sheet") {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(ReportEditorPane.this,
                        Messages.getString("ReportEditorPane.del_sheet")) == JOptionPane.YES_OPTION) {
                    JReportGrid grid = getFocusedGrid();
                    ReportModel model = grid.getReportModel();
                    int index = indexOfTabbed(grid);
                    getReportBook().remove(model);
                    updateSheetActions();
                    pushUndo(new DelSheetUndo(grid, index));
                }
            }

        };
        delSheetAction.setEnabled(false);
        loadSheetAction = new ReportAction.BasedAction("load_sheet") {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                loadSheet();
            }

        };
        saveSheetAction = new ReportAction.BasedAction("save_sheet") {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                saveSheet();
            }

        };
        moveLeftSheetAction = new ReportAction.BasedAction("left_sheet") {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                moveLeftSheet();
            }
        };
        moveRightSheetAction = new ReportAction.BasedAction("right_sheet") {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                moveRightSheet();
            }
        };
        printSheetAction = new ReportAction.BasedAction("print_sheet") {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                printSheet();
            }
        };
    }

    protected void showAbout() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof Frame) {
            new AboutDlg((Frame) w);
        } else {
            new AboutDlg((Dialog) w);
        }
    }

    /**
     * This method initializes coolBar
     *
     * @return javax.swing.JPanel
     */
    protected JPanel getCoolBar() {
        if (coolBar == null) {
            coolBar = createCoolBar();
            addToolBars();
        }
        return coolBar;
    }

    protected JPanel createCoolBar() {
        return new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)) {

            private static final long serialVersionUID = 1L;

            public Dimension getPreferredSize() {
                int h = 16;
                int w = 16;
                int n = getComponentCount();
                if (n == 0)
                    return new Dimension(w, h);
                Component comp;
                for (int i = 0; i < n; i++) {
                    comp = getComponent(i);
                    h = Math.max(h, comp.getLocation().y
                            + (int) comp.getPreferredSize().getHeight());
                    w = Math.max(w, comp.getLocation().x
                            + (int) comp.getPreferredSize().getWidth());
                }
                Insets ins = getInsets();
                return new Dimension(w + ins.left + ins.right, h + ins.top
                        + ins.bottom);
            }

        };
    }

    protected void addToolBars() {
        coolBar.add(getStandartBar(), null);
        coolBar.add(getFormatBar(), null);
    }

    protected BorderDialog getBorderDialog() {
        if (borderDialog == null) {
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w instanceof Frame) {
                borderDialog = new BorderDialog((Frame) w);
            } else {
                borderDialog = new BorderDialog((Dialog) w);
            }
            addBorderAction(borderDialog);
            borderDialog.pack();
            Point p = new Point(0, borderButton.getHeight());
            SwingUtilities.convertPointToScreen(p, borderButton);
            borderDialog.setLocation(p.x, p.y);
        }
        return borderDialog;
    }

    protected JToolBar getFormatBar() {
        if (formatBar == null) {
            formatBar = new JToolBar(Messages.getString("ReportEditor.11"));
            formatBar.setRollover(true);
            formatBar.setFloatable(false);
            formatBar.add(getFontPanel());
            formatBar.addSeparator();
            formatBar.add(createBoldButton());
            formatBar.add(createItalicButton());
            formatBar.add(createUnderlineButton());
            formatBar.add(backgroundAction);
            formatBar.addSeparator();
            addAlignAction(formatBar);
            formatBar.addSeparator();

            borderButton = new JButton(new ImageIcon(Objects.requireNonNull(getClass().getResource(
                    "/jdbreport/resources/brd.png"))));
            borderButton.setToolTipText(Messages.getString("ReportEditor.13"));
            borderButton.setActionCommand(SHOW_BORDER_DLG_COMMAND);
            borderButton.addActionListener(this);
            formatBar.add(borderButton);
            formatBar.addSeparator();
            addFormatAction(formatBar);
            formatBar.add(createRoundButton());
        }
        return formatBar;
    }

    protected JMenu getImageMenu() {
        if (imageMenu == null) {
            imageMenu = new JMenu(ReportResources.getInstance().getString(
                    "menu.image"));
            imageMenu.add(getImageAction());
            imageMenu.add(getScaleImageItem());
            imageMenu.add(getDeleteImageAction());
            imageMenu.add(getSaveImageAction());
        }
        return imageMenu;
    }

    protected void exportToCalc() {
        File tmpFile;
        try {
            if (ods_exists) {
                String ext = ".ods";
                tmpFile = File.createTempFile("~rpt", null);
                tmpFile.delete();
                tmpFile = new File(Utils.changeFileExtension(
                        tmpFile.getPath(), ext));
                saveAs(tmpFile, null);
                ProcessBuilder pb = createProcess(getODSCommand(),
                        tmpFile.getPath());
                try {
                    pb.start();
                    return;
                } catch (IOException e) {
                    ods_exists = false;
                    odsCommand = null;
                    e.printStackTrace();
                }
                tmpFile.delete();
            }

            if (!isWindows)
                excel_exists = false;
            if (excel_exists) {
                String ext = ReportBook.fileTypeExists(ReportBook.XLS) ? ".xls"
                        : ".xml";
                tmpFile = File.createTempFile("~rpt", null);
                tmpFile.delete();
                tmpFile = new File(Utils.changeFileExtension(
                        tmpFile.getPath(), ext));
                saveAs(tmpFile, null);
                ProcessBuilder pb = createProcess(getExcelCommand(),
                        tmpFile.getPath());
                try {
                    pb.start();
                    return;
                } catch (IOException e) {
                    excel_exists = false;
                    excelCommand = null;
                    e.printStackTrace();
                }
                tmpFile.delete();
            }
            setEnabled(ods_exists || excel_exists);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    protected void showProperties() {
        if (preferences == null) {
            preferences = createPreferences();
        }
        preferences.setODSCommand(getODSCommand());
        preferences.setExcelCommand(getExcelCommand());
        preferences.setReportGrid(getFocusedGrid(), getReportBook());
        preferences.addUndoListener(this);
        preferences.setVisible(true);
        if (preferences.getModalResult() == PreferencesDlg.OK) {
            setODSCommand(preferences.getODSCommand());
            setExcelCommand(preferences.getExcelCommand());
            getFocusedGrid().repaint();
        }
        preferences.removeUndoListener(this);
    }

    protected PreferencesDlg createPreferences() {
        PreferencesDlg dlg;
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof Frame) {
            dlg = new PreferencesDlg((Frame) w, true);
        } else {
            dlg = new PreferencesDlg((Dialog) w, true);
        }
        return dlg;
    }

    protected void addAlignAction(JToolBar alignBar) {
        AbstractButton leftButton = getAlignLeftAction().addButton(
                new JToggleButton());
        leftButton.setText("");
        alignBar.add(leftButton);

        AbstractButton centerButton = getAlignCenterAction().addButton(
                new JToggleButton());
        centerButton.setText("");
        alignBar.add(centerButton);

        AbstractButton rightButton = getAlignRightAction().addButton(
                new JToggleButton());
        rightButton.setText("");
        alignBar.add(rightButton);

        AbstractButton justifyButton = getJustifyAction().addButton(
                new JToggleButton());
        justifyButton.setText("");
        alignBar.add(justifyButton);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(leftButton);
        buttonGroup.add(centerButton);
        buttonGroup.add(rightButton);
        buttonGroup.add(justifyButton);

        AbstractButton topButton = getAlignTopAction().addButton(
                new JToggleButton());
        topButton.setText("");
        alignBar.add(topButton);

        AbstractButton vcenterButton = getAlignVCenterAction().addButton(
                new JToggleButton());
        vcenterButton.setText("");
        alignBar.add(vcenterButton);

        AbstractButton bottomButton = getAlignBottomAction().addButton(
                new JToggleButton());
        bottomButton.setText("");
        alignBar.add(bottomButton);

        ButtonGroup buttonvGroup = new ButtonGroup();
        buttonvGroup.add(topButton);
        buttonvGroup.add(vcenterButton);
        buttonvGroup.add(bottomButton);

        alignBar.addSeparator();

        AbstractButton mergeCellButton = getUnionCellAction().addButton(
                new JToggleButton());
        mergeCellButton.setText("");
        alignBar.add(mergeCellButton);
    }

    private JCheckBoxMenuItem getScaleImageItem() {
        if (scaleImageItem == null) {
            scaleImageItem = new JCheckBoxMenuItem(getScaleImageAction());
        }
        return scaleImageItem;
    }

    private AbstractButton createItalicButton() {
        AbstractButton button = getItalicAction()
                .addButton(new JToggleButton());
        button.setText("");
        return button;
    }

    protected AbstractButton createUnionCellItem() {
        return getUnionCellAction().addButton(new JCheckBoxMenuItem());
    }

    protected AbstractButton createRowBreakItem() {
        return getRowBreakAction().addButton(new JCheckBoxMenuItem());
    }

    protected AbstractButton createColumnBreakItem() {
        return getColumnBreakAction().addButton(new JCheckBoxMenuItem());
    }

    private AbstractButton createUnderlineButton() {
        AbstractButton button = getUnderlineAction().addButton(
                new JToggleButton());
        button.setText("");
        return button;
    }

    protected Action getPageSetupAction() {
        return pageSetupAction;
    }

    protected Action getPrintAction() {
        return printAction;
    }

    protected Action getPreviewAction() {
        return previewAction;
    }

    protected Action getOpenAction() {
        if (openAction == null) {
            openAction = new ReportAction.BasedAction(ReportAction.OPEN_ACTION) {

                private static final long serialVersionUID = 1L;

                public void actionPerformed(ActionEvent e) {
                    open();
                }

            };
        }
        return openAction;
    }

    protected Action getImageAction() {
        if (imageAction == null) {
            imageAction = ReportAction.createGridAction(
                    ReportAction.INSERT_IMAGE_ACTION, this);
        }
        return imageAction;
    }

    protected Action getSaveImageAction() {
        if (saveImageAction == null) {
            saveImageAction = ReportAction.createGridAction(
                    ReportAction.SAVE_IMAGE_ACTION, this);
        }
        return saveImageAction;
    }

    protected ToggleAction getScaleImageAction() {
        if (scaleImageAction == null) {
            scaleImageAction = ReportAction.createGridToggleAction(
                    ReportAction.SCALE_IMAGE_ACTION, this);
        }
        return scaleImageAction;
    }

    protected Action getDeleteImageAction() {
        if (deleteImageAction == null) {
            deleteImageAction = ReportAction.createGridAction(
                    ReportAction.DELETE_IMAGE_ACTION, this);
        }
        return deleteImageAction;
    }

    protected Action getSaveAction() {
        if (saveAction == null) {
            saveAction = new ReportAction.BasedAction(ReportAction.SAVE_ACTION) {

                private static final long serialVersionUID = 1L;

                public void actionPerformed(ActionEvent e) {
                    save();
                }

            };
            saveAction.setEnabled(false);
        }
        return saveAction;
    }

    protected Action getPropertiesAction() {
        if (propertiesAction == null) {
            propertiesAction = new ReportAction.BasedAction("properties") {

                private static final long serialVersionUID = 1L;

                public void actionPerformed(ActionEvent e) {
                    showProperties();
                }

            };
        }
        return propertiesAction;
    }

    private ToggleAction getAlignLeftAction() {
        if (alignLeftAction == null) {
            alignLeftAction = ReportAction.createGridToggleAction(
                    ReportAction.ALIGN_LEFT_ACTION, this);
        }
        return alignLeftAction;
    }

    private ToggleAction getAlignRightAction() {
        if (alignRightAction == null) {
            alignRightAction = ReportAction.createGridToggleAction(
                    ReportAction.ALIGN_RIGHT_ACTION, this);
        }
        return alignRightAction;
    }

    private ToggleAction getAlignCenterAction() {
        if (alignCenterAction == null) {
            alignCenterAction = ReportAction.createGridToggleAction(
                    ReportAction.ALIGN_CENTER_ACTION, this);
        }
        return alignCenterAction;
    }

    private ToggleAction getAlignTopAction() {
        if (alignTopAction == null) {
            alignTopAction = ReportAction.createGridToggleAction(
                    ReportAction.ALIGN_TOP_ACTION, this);
        }
        return alignTopAction;
    }

    private ToggleAction getAlignVCenterAction() {
        if (alignVCenterAction == null) {
            alignVCenterAction = ReportAction.createGridToggleAction(
                    ReportAction.ALIGN_VCENTER_ACTION, this);
        }
        return alignVCenterAction;
    }

    private ToggleAction getAlignBottomAction() {
        if (alignBottomAction == null) {
            alignBottomAction = ReportAction.createGridToggleAction(
                    ReportAction.ALIGN_BOTTOM_ACTION, this);
        }
        return alignBottomAction;
    }

    private void updateBorderActions(CellStyle style) {
        if (borderDialog != null) {
            borderDialog.updateActions(style);
        }
    }

    private AbstractButton createBoldButton() {
        AbstractButton button = getBoldAction().addButton(new JToggleButton());
        button.setText("");
        return button;
    }

    private ToggleAction getBoldAction() {
        if (boldAction == null)
            boldAction = ReportAction.createGridToggleAction(
                    ReportAction.FONT_BOLD_ACTION, this);
        return boldAction;
    }

    private FontCellRenderer getFontCellRenderer() {
        if (fontCellRenderer == null) {
            fontCellRenderer = new FontCellRenderer();
        }
        return fontCellRenderer;
    }

    protected ToggleAction getUnionCellAction() {
        if (unionCellAction == null) {
            unionCellAction = ReportAction.createGridToggleAction(
                    ReportAction.CELL_UNION_ACTION, this);
        }
        return unionCellAction;
    }

    protected ToggleAction getAutoHeightAction() {
        if (autoHeightAction == null) {
            autoHeightAction = ReportAction.createGridToggleAction(
                    ReportAction.CELL_AUTOHEIGHT_ACTION, this);
        }
        return autoHeightAction;
    }


    protected ToggleAction getRowBreakAction() {
        if (rowBreakAction == null) {
            rowBreakAction = ReportAction.createGridToggleAction(
                    ReportAction.ROW_BREAK_ACTION, this);
        }
        return rowBreakAction;
    }

    protected ToggleAction getColumnBreakAction() {
        if (columnBreakAction == null) {
            columnBreakAction = ReportAction.createGridToggleAction(
                    ReportAction.COLUMN_BREAK_ACTION, this);
        }
        return columnBreakAction;
    }

    protected ToggleAction getRoundAction() {
        if (roundAction == null) {
            roundAction = ReportAction.createGridToggleAction(
                    ReportAction.ROUND_TO_SIGNIFICANT_ACTION, this);
        }
        return roundAction;
    }

    private AbstractButton createRoundButton() {
        AbstractButton button = getRoundAction()
                .addButton(new JToggleButton());
        button.setText("");
        return button;
    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getFontPanel() {
        if (fontPanel == null) {
            FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 4, 2);
            fontPanel = new JPanel(layout);
            fontPanel.setMaximumSize(new Dimension(210, 30));
            fontPanel.setPreferredSize(new java.awt.Dimension(210, 30));
            fontPanel.add(getFontNameBox());
            fontPanel.add(getFontSizeBox());
        }
        return fontPanel;
    }

    /**
     * This method initializes fontSizeBox
     *
     * @return javax.swing.JComboBox
     */
    private JComboBox<Integer> getFontSizeBox() {
        if (fontSizeBox == null) {
            fontSizeBox = new JComboBox<>();
            fontSizeBox.setPreferredSize(new Dimension(46, 22));
            fontSizeBox.setEditable(true);
            fontSizeBox.setActionCommand(FONT_SIZE_COMMAND);
            fontSizeBox.addActionListener(this);
            for (int i = 8; i <= 18; i++) {
                fontSizeBox.addItem(i);
            }
            for (int i = 20; i <= 36; i = i + 2) {
                fontSizeBox.addItem(i);
            }
            fontSizeBox.addItem(40);
            fontSizeBox.addItem(44);
            fontSizeBox.addItem(48);
            fontSizeBox.addItem(56);
            fontSizeBox.addItem(64);
            fontSizeBox.addItem(72);
        }
        return fontSizeBox;
    }

    private JComboBox<String> getFontNameBox() {
        if (fontNameBox == null) {
            fontNameBox = new JComboBox<>();
            fontNameBox.setMaximumSize(new Dimension(150, 22));
            fontNameBox.setPreferredSize(new java.awt.Dimension(150, 22));

            new Thread(() -> {
                GraphicsEnvironment ge = GraphicsEnvironment
                        .getLocalGraphicsEnvironment();
                String[] fonts = ge.getAvailableFontFamilyNames();
                for (String font : fonts) {
                    SwingUtilities.invokeLater(() -> fontNameBox.addItem(font));
                    try {
                        java.lang.Thread.sleep(50);
                    } catch (Exception ignore) {
                    }
                }

            }).start();

            fontNameBox.addPopupMenuListener(new PopupMenuListener() {

                private boolean visible;

                public void popupMenuCanceled(PopupMenuEvent e) {
                    visible = false;
                }

                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    if (visible && getFocusedGrid() != null
                            && isEnabledAction()) {
                        JComboBox<?> cb = (JComboBox<?>) e.getSource();
                        getFocusedGrid().setFontName(
                                String.valueOf(cb.getSelectedItem()));
                    }
                }

                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    visible = true;
                }

            });
            fontNameBox.setRenderer(getFontCellRenderer());
        }
        return fontNameBox;
    }

    private void addFormatAction(JToolBar toolBar) {
        incDecimalAction = new ReportAction.IncDecimalsAction(this) {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                updateDecimalLabel();
            }

        };
        toolBar.add(incDecimalAction);

        decDecimalAction = new ReportAction.DecDecimalsAction(this) {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                updateDecimalLabel();
            }

        };
        toolBar.add(decDecimalAction);
        toolBar.addSeparator();
        toolBar.add(getDecimalLabel());
        toolBar.addSeparator();
    }

    private JLabel getDecimalLabel() {
        if (decimalLabel == null) {
            decimalLabel = new JLabel();
            decimalLabel.setFont(Consts.statusFont);
        }
        return decimalLabel;
    }

    private ToggleAction getJustifyAction() {
        if (justifyAction == null) {
            justifyAction = ReportAction.createGridToggleAction(
                    ReportAction.ALIGN_JUSTIFY_ACTION, this);
        }
        return justifyAction;
    }

    private ToggleAction getItalicAction() {
        if (italicAction == null) {
            italicAction = ReportAction.createGridToggleAction(
                    ReportAction.FONT_ITALIC_ACTION, this);
        }
        return italicAction;
    }

    private ToggleAction getUnderlineAction() {
        if (underlineAction == null) {
            underlineAction = ReportAction.createGridToggleAction(
                    ReportAction.FONT_UNDERLINE_ACTION, this);
        }
        return underlineAction;
    }

    /**
     * implements CellSelectListener
     */
    public void cellSelectedChange(CellSelectChangedEvent evt) {
        disableAction();
        try {
            JReportGrid rep = (JReportGrid) evt.getSource();
            CellWrap cellWrap = rep.getReportModel().getCellWrap(evt.getRow(),
                    evt.getColumn());
            cellChanged(rep, cellWrap);
        } finally {
            enableAction();
        }
    }

    private void setExcelCommand(String text) {
        this.excelCommand = text;
    }

    private String getExcelCommand() {
        if (excelCommand == null || excelCommand.length() == 0) {
            excelCommand = "excel.exe";
            for (int i = 13; i >= 10; i--) {
                File file = new File("c:/Program Files/Microsoft Office/OFFICE"
                        + i + "/excel.exe");
                if (file.exists()) {
                    excelCommand = file.getPath();
                    break;
                }
            }
        }
        return excelCommand;
    }

    private void setODSCommand(String text) {
        this.odsCommand = text;
    }

    private String getODSCommand() {
        if (odsCommand == null || odsCommand.length() == 0) {
            if (isWindows) {
                odsCommand = "scalc.exe";
                {
                    File file = new File("c:/Program Files/OpenOffice.org 3"
                            + "/program/scalc.exe");
                    if (file.exists()) {
                        odsCommand = file.getPath();
                        return odsCommand;
                    }
                }
            } else
                odsCommand = "openoffice.org -calc";
        }
        return odsCommand;
    }

    protected JToolBar getStandartBar() {
        if (standartBar == null) {
            standartBar = new JToolBar(Messages.getString("ReportEditor.10"));
            standartBar.setRollover(true);
            standartBar.setFloatable(false);

            standartBar.add(getOpenAction());
            standartBar.add(getSaveAction());
            standartBar.addSeparator();
            standartBar.add(printAction);
            standartBar.add(previewAction);
            standartBar.add(pageSetupAction);
            standartBar.addSeparator();
            standartBar.add(export2CalcAction);
            standartBar.addSeparator();
            standartBar.add(getCutAction());
            standartBar.add(getCopyAction());
            standartBar.add(getPasteAction());
            standartBar.addSeparator();
            undoButton = standartBar.add(getUndoAction());
            redoButton = standartBar.add(getRedoAction());
            standartBar.addSeparator();
            addColRowAction(standartBar);

        }
        return standartBar;
    }

    protected JButton createInsertRowButton() {
        JButton button = new JButton(ReportAction.createGridAction(
                ReportAction.INSERT_ROW_ACTION, this));
        button.setText("");
        return button;
    }

    private void addColRowAction(JToolBar toolBar) {
        toolBar.add(createInsertRowButton());
        toolBar.add(ReportAction.createGridAction(
                ReportAction.INSERT_COLUMN_ACTION, this));
        toolBar.addSeparator();
        toolBar.add(ReportAction.createGridAction(
                ReportAction.REMOVE_ROW_ACTION, this));
        toolBar.add(ReportAction.createGridAction(
                ReportAction.REMOVE_COLUMN_ACTION, this));
    }

    protected void cellChanged(JReportGrid rep, CellWrap cellWrap) {
        Cell cell = cellWrap.getCell();
        CellStyle style = rep.getCellStyle(cell.getStyleId());
        getBoldAction().setSelected(style.isBold());
        getItalicAction().setSelected(style.isItalic());
        getUnderlineAction().setSelected(style.isUnderline());
        int fontIndex = -1;
        for (int i = 0; i < getFontNameBox().getItemCount(); i++) {
            if (getFontNameBox().getItemAt(i).equals(style.getFamily())) {
                fontIndex = i;
                break;
            }
        }

        if (fontIndex >= 0)
            getFontNameBox().setSelectedItem(style.getFamily());
        else
            getFontNameBox().setSelectedItem(rep.getFont().getFamily());

        Integer size = style.getSize();
        getFontSizeBox().setSelectedItem(size);
        getFontSizeBox().getEditor().setItem(size);
        getAlignLeftAction().setSelected(
                style.getHorizontalAlignment() == CellStyle.LEFT);
        getAlignRightAction().setSelected(
                style.getHorizontalAlignment() == CellStyle.RIGHT);
        getAlignCenterAction().setSelected(
                style.getHorizontalAlignment() == CellStyle.CENTER);
        getJustifyAction().setSelected(
                style.getHorizontalAlignment() == CellStyle.JUSTIFY);
        getAlignTopAction().setSelected(
                style.getVerticalAlignment() == CellStyle.TOP);
        getAlignBottomAction().setSelected(
                style.getVerticalAlignment() == CellStyle.BOTTOM);
        getAlignVCenterAction().setSelected(
                style.getVerticalAlignment() == CellStyle.CENTER);
        if (cell.isSpan()) {
            unionCellAction.setEnabled(true);
            unionCellAction.setSelected(true);
        } else {
            unionCellAction.setSelected(false);
            unionCellAction.setEnabled(rep.getSelectedColumnCount() > 1
                    || rep.getSelectedRowCount() > 1);
        }
        getRowBreakAction().setSelected(
                rep.getReportModel().getRowModel().getRow(cellWrap.getRow())
                        .isPageBreak());
        if (cellWrap.getColumn() >= 0) {
            getColumnBreakAction().setSelected(
                    ((ReportColumn) rep.getColumnModel().getColumn(
                            cellWrap.getColumn())).isPageBreak());
        }
        updateUndoRedoState();
        updateBorderActions(style);
        getRoundAction().setSelected(style.isRoundToSignificant());
        updateDecimalLabel(style);
    }

    protected boolean isEnabledAction() {
        return enableAction == 0;
    }

    protected void enableAction() {
        if (enableAction > 0)
            enableAction--;
    }

    protected void disableAction() {
        enableAction++;
    }

    protected void moveLeftSheet() {
        int index = getFocusedIndex();
        if (moveSheet(index, index - 1)) {
            pushUndo(new MoveSheetUndo(index, index - 1));
        }
    }

    protected void moveRightSheet() {
        int index = getFocusedIndex();
        if (moveSheet(index, index + 1)) {
            pushUndo(new MoveSheetUndo(index, index + 1));
        }
    }

    public boolean loadSheet() {
        int index = getFocusedIndex() + 1;
        int oldcount = getReportGridList().size();
        boolean result = super.loadSheet();
        if (result) {
            int count = getReportGridList().size() - oldcount;
            JReportGrid[] grids = new JReportGrid[count];
            for (int i = 0; i < count; i++) {
                grids[i] = getReportGridList().get(i + index);
            }
            pushUndo(new LoadSheetUndo(index, grids));
            updateSheetActions();
        }
        return result;
    }

    @Override
    protected void updateSheetActions() {
        delSheetAction.setEnabled(getTabCount() > 1);
    }

    private void updateDecimalLabel() {
        JReportGrid grid = getFocusedGrid();
        if (grid != null) {
            Cell cell = grid.getSelectedCell();
            CellStyle style = grid.getCellStyle(cell.getStyleId());
            updateDecimalLabel(style);
        }
    }

    private ProcessBuilder createProcess(String programm, String tmpFile) {
        StringTokenizer tokenizer = new StringTokenizer(programm);
        java.util.List<String> command = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            command.add(tokenizer.nextToken());
        }
        command.add(tmpFile);
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(command);
        return pb;
    }

    private void updateDecimalLabel(CellStyle style) {
        int n = style.getDecimal();
        StringBuilder d = new StringBuilder();
        if (n >= 0) {
            d = new StringBuilder("0.");
            for (int i = 1; i <= n; i++)
                d.append("0");
        }
        decimalLabel.setText(d.toString());
        incDecimalAction.setEnabled(n < 15);
        decDecimalAction.setEnabled(n >= 0);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        switch (evt.getPropertyName()) {
            case "addGrid":
                updateSheetActions();
                break;
            case "removeGrid":
                updateSheetActions();
                break;
            case "dirty":
                updateUndoRedoState();
                break;
        }

    }

    protected void addGridListeners(JReportGrid grid) {
        super.addGridListeners(grid);
        grid.addCellSelectListener(this);
    }

    protected void removeGridListeners(JReportGrid grid) {
        super.removeGridListeners(grid);
        grid.removeCellSelectListener(this);
    }

    protected void addBorderAction(BorderDialog bar) {
        bar.add(ReportAction.createGridAction(ReportAction.BRD_CLEAR_ACTION,
                this), -1);
        bar.add(new ReportAction.BorderAction(this), Border.LINE_BORDER);
        bar.add(new ReportAction.BorderLeftAction(this), Border.LINE_LEFT);
        bar.add(new ReportAction.BorderTopAction(this), Border.LINE_TOP);
        bar.add(new ReportAction.BorderRightAction(this), Border.LINE_RIGHT);
        bar.add(new ReportAction.BorderBottomAction(this), Border.LINE_BOTTOM);
        bar.add(new ReportAction.BorderMiddleAction(this), Border.LINE_MIDDLE);
        bar.add(ReportAction.createGridToggleAction(
                ReportAction.BRD_GRID_ACTION, this), Border.LINE_GRID);
    }

    protected void updateUndoRedoState() {
        super.updateUndoRedoState();
        JReportGrid grid = getFocusedGrid();
        if (undoButton != null) {
            undoButton.setToolTipText(getUndoAction().getToolTipText());
            redoButton.setToolTipText(getRedoAction().getToolTipText());
            if (grid == null) {
                undoButton.setToolTipText(getUndoAction().getValue("Name")
                        .toString());
                redoButton.setToolTipText(getRedoAction().getValue("Name")
                        .toString());
            }
            getSaveAction().setEnabled(isModified());
        }
    }

    private static boolean failedDecodeFont = false;

    private static class FontCellRenderer extends JTextPane implements
            ListCellRenderer {
        private static final long serialVersionUID = -889837983681353498L;


        public FontCellRenderer() {
            setOpaque(true);
        }

        public void invalidate() {
        }

        public void validate() {
        }

        public void revalidate() {
        }

        public void repaint(long tm, int x, int y, int width, int height) {
        }

        public void repaint(Rectangle r) {
        }

        public void repaint() {
        }

        public void firePropertyChange(String propertyName, boolean oldValue,
                                       boolean newValue) {
        }

        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            setText(value.toString());
            try {
                if (!failedDecodeFont)
                    setFont(Font.decode(value + "-plain-14"));
            } catch (Exception ignore) {
                failedDecodeFont = true;
            }
            setBackground(isSelected ? Color.BLUE : Color.white);
            setForeground(isSelected ? Color.white : Color.black);
            return this;
        }
    }

    private class Export2CalcAction extends ReportAction.BasedAction {

        private static final long serialVersionUID = 1L;

        public Export2CalcAction() {
            super(ReportAction.EXPORT_TO_CALC_ACTION);
            this.setEnabled(ods_exists || excel_exists);
        }

        public void actionPerformed(ActionEvent e) {
            exportToCalc();
        }

    }

    private class LoadSheetUndo implements UndoItem {

        protected GridRect selectedRect;
        private boolean added;
        private boolean hasUndo = false;
        private JReportGrid[] grids;
        private final int index;

        public LoadSheetUndo(int index, JReportGrid[] grids) {
            this.index = index;
            this.grids = grids;
            added = true;
            initSelectedGrid(grids[0]);
        }

        protected void initSelectedGrid(JReportGrid grid) {
            selectedRect = grid.getSelectionRect();
        }

        public UndoItem undo() {
            if (added) {
                for (JReportGrid grid : grids) {
                    getReportBook().remove(grid.getReportModel());
                }
                setFocusedGrid(findReportGrid(index > 0 ? index - 1 : 0));
            } else {
                int i = index;
                for (JReportGrid grid : grids) {
                    insertReportGrid(i++, grid);
                }
                selectRect();
            }
            added = !added;
            hasUndo = true;
            return this;
        }

        protected void selectRect() {
            setFocusedGrid(grids[0]);
            if (selectedRect != null) {
                grids[0].setSelectedRect(selectedRect);
                grids[0].repaint();
            }
        }

        public String getDescription() {
            return "Load sheets";
        }

        public void clear() {
            grids = null;
            selectedRect = null;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(grids);
            result = prime * result + (hasUndo ? 1231 : 1237);
            result = prime * result + index;
            result = prime * result
                    + ((selectedRect == null) ? 0 : selectedRect.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            LoadSheetUndo other = (LoadSheetUndo) obj;
            if (!Arrays.equals(grids, other.grids))
                return false;
            if (hasUndo != other.hasUndo)
                return false;
            if (index != other.index)
                return false;
            if (selectedRect == null) {
                return other.selectedRect == null;
            } else return selectedRect.equals(other.selectedRect);
        }

    }

    private class AddSheetUndo extends AbstractGridUndo {

        private boolean added;

        public AddSheetUndo(JReportGrid grid) {
            super(grid, "Add sheet");
            added = true;
        }

        public UndoItem undo() {
            if (added)
                getReportBook().remove(getGrid().getReportModel());
            else {
                insertReportGrid(-1, getGrid());
            }
            added = !added;
            return super.undo();
        }

    }

    private class DelSheetUndo extends AbstractGridUndo {

        private boolean deleted;
        private final int index;

        public DelSheetUndo(JReportGrid grid, int index) {
            super(grid, Messages.getString("ReportEditorPane.1"));
            this.index = index;
            deleted = true;
        }

        public UndoItem undo() {
            if (!deleted)
                getReportBook().remove(getGrid().getReportModel());
            else {
                insertReportGrid(index, getGrid());
            }
            deleted = !deleted;
            return super.undo();
        }

    }

    private class MoveSheetUndo implements UndoItem {

        private int fromIndex;
        private int toIndex;

        public MoveSheetUndo(int fromIndex, int toIndex) {
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
        }

        public void clear() {
        }

        public String getDescription() {
            return Messages.getString("ReportEditorPane.0");
        }

        public UndoItem undo() {
            moveSheet(toIndex, fromIndex);
            int tmp = toIndex;
            toIndex = fromIndex;
            fromIndex = tmp;
            return this;
        }

    }

    protected Action getAddSheetAction() {
        return addSheetAction;
    }

    protected Action getDelSheetAction() {
        return delSheetAction;
    }

    protected Action getLoadSheetAction() {
        return loadSheetAction;
    }

    protected Action getSaveSheetAction() {
        return saveSheetAction;
    }

    protected Action getMoveLeftSheetAction() {
        return moveLeftSheetAction;
    }

    protected Action getMoveRightSheetAction() {
        return moveRightSheetAction;
    }

    protected Action getPrintSheetAction() {
        return printSheetAction;
    }

    protected Action getAboutAction() {
        return aboutAction;
    }

    private JMenu getFormatMenu() {
        JMenu formatMenu = new JMenu();
        formatMenu.setText(ReportResources.getInstance().getString(
                "menu.format"));
        formatMenu.add(createUnionCellItem());
        formatMenu.add(createRowBreakItem());
        formatMenu.add(createColumnBreakItem());
        return formatMenu;
    }

    private JMenu getHelpMenu() {
        if (helpMenu == null) {
            helpMenu = createHelpMenu();
            helpMenu.addSeparator();
            helpMenu.add(getAboutAction());
        }
        return helpMenu;
    }

    protected JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu(Messages.getString("ReportEditor.8"));
        JMenuItem menuItem = new JMenuItem(
                Messages.getString("ReportEditorPane.user_guide"));
        menuItem.addActionListener(this);
        menuItem.setActionCommand("help");
        helpMenu.add(menuItem);
        return helpMenu;
    }

    public JMenu getLfMenu() {
        lfMenu = new JMenu();
        lfMenu.setText(ReportResources.getInstance().getString("menu.lf"));
        LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
        for (LookAndFeelInfo look : looks) {
            JMenuItem item = new JCheckBoxMenuItem(look.getName());
            item.setActionCommand(LF_CHANGE_COMMAND);
            item.addActionListener(this);
            if (item.getText().equals(defaultLf))
                item.setSelected(true);
            lfMenu.add(item);
        }
        return lfMenu;
    }

    protected boolean saveAs() {
        boolean result = saveAs(null);
        if (result)
            updateCaption();
        return result;
    }

    protected Action getSaveAsAction() {
        if (saveAsAction == null) {
            saveAsAction = new ReportAction.BasedAction(
                    ReportAction.SAVE_AS_ACTION) {

                private static final long serialVersionUID = 1L;

                public void actionPerformed(ActionEvent e) {
                    saveAs();
                }

            };
        }
        return saveAsAction;
    }

    protected JMenu createFileMenu() {
        JMenu fileMenu = new JMenu();
        fileMenu.setText(ReportResources.getInstance().getString("menu.file"));
        fileMenu.add(getNewAction());
        fileMenu.add(getOpenAction());
        fileMenu.add(getSaveAction());
        fileMenu.add(getSaveAsAction());
        fileMenu.addSeparator();
        fileMenu.add(getPageSetupAction());
        fileMenu.add(getPrintAction());
        fileMenu.add(getPreviewAction());
        fileMenu.addSeparator();
        fileMenu.add(getPropertiesAction());
        fileMenu.addSeparator();
        fileMenu.add(getExitAction());
        return fileMenu;
    }

    protected Action getExitAction() {
        if (exitAction == null) {
            exitAction = new ReportAction.BasedAction(ReportAction.EXIT_ACTION) {

                private static final long serialVersionUID = 1L;

                public void actionPerformed(ActionEvent e) {
                    Window w = SwingUtilities
                            .getWindowAncestor(ReportEditorPane.this);
                    if (w != null && saveQuestion()) {
                        w.dispose();
                    }
                }

            };
        }
        return exitAction;
    }

    public JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = createFileMenu();
        }
        return fileMenu;
    }

    protected JMenu getEditMenu() {
        if (editMenu == null) {
            editMenu = new JMenu(ReportResources.getInstance().getString(
                    "menu.edit"));
            editMenu.addMenuListener(new MenuListener() {

                public void menuCanceled(MenuEvent e) {
                }

                public void menuDeselected(MenuEvent e) {
                }

                public void menuSelected(MenuEvent e) {
                    editMenu.getItem(0).setText(
                            getUndoAction().getValue("Name") + " "
                                    + getUndoAction().getDescription());
                    editMenu.getItem(1).setText(
                            getRedoAction().getValue("Name") + " "
                                    + getRedoAction().getDescription());
                }

            });
            editMenu.add(getUndoAction());
            editMenu.add(getRedoAction());
            editMenu.addSeparator();
            editMenu.add(getCutAction());
            editMenu.add(getCopyAction());
            editMenu.add(getPasteAction());
            editMenu.addSeparator();
            editMenu.add(getDeleteAction());
            editMenu.add(getSelectAllAction());
            editMenu.addSeparator();
            editMenu.add(getFindAction());
        }
        return editMenu;
    }

    public JMenuBar createJMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(getFileMenu());
        menuBar.add(getEditMenu());
        menuBar.add(getSheetMenu());
        menuBar.add(getFormatMenu());
        menuBar.add(getHelpMenu());
        return menuBar;
    }

    private JMenu getSheetMenu() {
        if (sheetMenu == null) {
            sheetMenu = new JMenu();
            sheetMenu.setText(ReportResources.getInstance().getString(
                    "menu.sheet"));
            sheetMenu.add(getAddSheetAction());
            sheetMenu.add(getDelSheetAction());
            sheetMenu.add(getLoadSheetAction());
            sheetMenu.add(getSaveSheetAction());
            sheetMenu.addSeparator();
            sheetMenu.add(getMoveLeftSheetAction());
            sheetMenu.add(getMoveRightSheetAction());
            sheetMenu.addSeparator();
            sheetMenu.add(getPrintSheetAction());
        }
        return sheetMenu;
    }

    private static String findLookAndFeel(String name) {
        LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
        for (LookAndFeelInfo look : looks) {
            if (look.getName().equalsIgnoreCase(name))
                return look.getClassName();
        }
        return null;
    }

    protected Action getNewAction() {
        if (newAction == null) {
            newAction = new ReportAction.BasedAction(ReportAction.NEW_ACTION) {

                private static final long serialVersionUID = 1L;

                public void actionPerformed(ActionEvent e) {
                    if (!saveQuestion())
                        return;
                    newReport();
                }

            };
        }
        return newAction;
    }

    public void changeLookAndFeel() {
        if (!"".equals(defaultLf)) {
            String className = findLookAndFeel(defaultLf);
            if (className != null)
                try {
                    UIManager.setLookAndFeel(className);
                    SwingUtilities.updateComponentTreeUI(getRootPane());
                } catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException e1) {
                    logger.log(Level.SEVERE, e1.getMessage(), e1);
                }
        }
    }

    protected void readProperties() {
        try {
            if (properties == null) {
                properties = new XMLProperties();
                String confFile = System.getProperty("user.home") + "/"
                        + JDBREPORT_CONF;
                properties.load(confFile);
            }
            CURRENT_DIRECTORY_PATH = properties.getString(
                    ReportPane.CURRENT_DIRECTORY, ".");
            CURRENT_FILTER = properties.getString(CURRENT_FILE_FILTER, "");
            CURRENT_IMAGE_PATH = properties.getString(
                    ReportPane.CURRENT_IMAGE_DIRECTORY, ".");
            defaultLf = properties.getString(LOOK_AND_FEEL, "");
            excelCommand = properties.getString(EXCEL_COMMAND, "");
            odsCommand = properties.getString(ODS_COMMAND, "");
            if (ReportBook.pdfExists()) {
                PdfFileType fileType = (PdfFileType) ReportBook.getFileTypeClass(ReportBook.PDF);
                if (fileType != null) {
                    String paths = properties.getString(PdfFileType.FONT_PATHS, "");
                    String font = properties.getString(PdfFileType.DEFAULT_FONT, "");
                    if (paths.length() > 0) {
                        List<String> fonts = new ArrayList<>();
                        StringTokenizer st = new StringTokenizer(paths, ";");
                        while (st.hasMoreTokens()) {
                            String path = st.nextToken();
                            if (path.length() > 0) {
                                fonts.add(path);
                            }
                        }
                        ReportFont.setFontPaths(fonts);
                    }
                    ReportFont.setDefaultFont(font);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void initBounds() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof JFrame) {
            JFrame frame = (JFrame) w;
            int state = properties.getInt(WINDOW_STATE, Frame.NORMAL);
            if (state == Frame.MAXIMIZED_BOTH) {
                frame.setExtendedState(state);
            }
            if (state == Frame.NORMAL || frame.getExtendedState() != state
                    || getWidth() == 0) {
                Rectangle r = frame.getBounds();
                r.x = properties.getInt(POS_X, r.x);
                r.y = properties.getInt(POS_Y, r.y);
                r.width = properties.getInt(SIZE_WIDTH, 800);
                r.height = properties.getInt(SIZE_HEIGHT, 600);
                if (r.width == 0 || r.height == 0) {
                    r.width = 640;
                    r.height = 460;
                }
                frame.setBounds(r);
            }
        } else if (w instanceof Dialog) {
            Dialog dialog = (Dialog) w;
            Rectangle r = dialog.getBounds();
            r.x = properties.getInt(POS_X, r.x);
            r.y = properties.getInt(POS_Y, r.y);
            r.width = properties.getInt(SIZE_WIDTH, 800);
            r.height = properties.getInt(SIZE_HEIGHT, 600);
            if (r.width == 0 || r.height == 0) {
                r.width = 640;
                r.height = 460;
            }
            dialog.setBounds(r);
        }
    }

    protected void saveProperties() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof Frame) {
            Frame frame = (Frame) w;
            if ((frame.getExtendedState() & Frame.ICONIFIED) == 0)
                properties.put(WINDOW_STATE, "" + frame.getExtendedState());
            if (frame.getState() == Frame.NORMAL) {
                Rectangle r = frame.getBounds();
                if (r.width > 0 && r.height > 0) {
                    properties.put(POS_X, r.x);
                    properties.put(POS_Y, r.y);
                    properties.put(SIZE_WIDTH, r.width);
                    properties.put(SIZE_HEIGHT, r.height);
                }
            }
        } else if (w instanceof Dialog) {
            Dialog dialog = (Dialog) w;
            Rectangle r = dialog.getBounds();
            if (r.width > 0 && r.height > 0) {
                properties.put(POS_X, r.x);
                properties.put(POS_Y, r.y);
                properties.put(SIZE_WIDTH, r.width);
                properties.put(SIZE_HEIGHT, r.height);
            }
        }
        if (ReportPane.CURRENT_DIRECTORY_PATH != null)
            properties.put(ReportPane.CURRENT_DIRECTORY,
                    ReportPane.CURRENT_DIRECTORY_PATH);
        if (ReportPane.CURRENT_FILTER != null
                && ReportPane.CURRENT_FILTER.length() > 0)
            properties.put(CURRENT_FILE_FILTER, ReportPane.CURRENT_FILTER);
        if (ReportPane.CURRENT_IMAGE_PATH != null)
            properties.put(ReportPane.CURRENT_IMAGE_DIRECTORY,
                    ReportPane.CURRENT_IMAGE_PATH);
        if (defaultLf != null && defaultLf.length() > 0) {
            properties.put(LOOK_AND_FEEL, defaultLf);
        }
        properties.put(EXCEL_COMMAND, excelCommand == null ? "" : excelCommand);
        properties.put(ODS_COMMAND, odsCommand == null ? "" : odsCommand);
        if (ReportBook.pdfExists()) {
            PdfFileType fileType = (PdfFileType) ReportBook.getFileTypeClass(ReportBook.PDF);
            if (fileType != null) {

                StringBuilder paths = new StringBuilder();
                for (String path : ReportFont.getFontPaths()) {
                    paths.append(path).append(";");
                }
                properties.put(PdfFileType.FONT_PATHS, paths.toString());
                properties.put(PdfFileType.DEFAULT_FONT,
                        ReportFont.getDefaultFont() == null ? ""
                                : ReportFont.getDefaultFont());

            }
        }
    }

    protected void writeProperties() {
        saveProperties();
        properties.save();
    }

    protected void initProperties() {
        initBounds();
    }

    @Override
    protected void installListeners() {
        super.installListeners();
    }

    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
    }

    protected void showHelp() {
        URL url = getClass().getResource(
                Messages.getString("ReportEditorPane.help_url"));
        if (url != null) {
            getHelpWindow().setUrl(url);
            getHelpWindow().setTitle(
                    Messages.getString("ReportEditorPane.help_title"));
        }
        getHelpWindow().setVisible(true);
    }

    public HelpWindow getHelpWindow() {
        if (helpWindow == null) {
            helpWindow = new HelpWindow(properties, null);
        }
        return helpWindow;
    }

    protected void showHelp(URL url) {
        getHelpWindow().setUrl(url);
        getHelpWindow().setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (SHOW_BORDER_DLG_COMMAND.equals(e.getActionCommand())) {
            getBorderDialog().setVisible(true);
            CellStyle style = getReportBook().getStyles(
                    getFocusedGrid().getSelectedCell().getStyleId());
            updateBorderActions(style);
        } else if (FONT_SIZE_COMMAND.equals(e.getActionCommand())) {
            if (getFocusedGrid() != null && isEnabledAction()) {
                JComboBox<?> cb = (JComboBox<?>) e.getSource();
                if (cb.getSelectedItem() != null) {
                    getFocusedGrid().setFontSize(
                            (Integer) cb.getSelectedItem());
                }
            }
        } else if (LF_CHANGE_COMMAND.equals(e.getActionCommand())) {
            JMenuItem item = (JMenuItem) e.getSource();
            String className = findLookAndFeel(item.getText());
            if (className != null)
                try {
                    for (int n = 0; n < lfMenu.getItemCount(); n++) {
                        ((JMenuItem) lfMenu.getMenuComponents()[n])
                                .setSelected(false);
                    }
                    item.setSelected(true);
                    UIManager.setLookAndFeel(className);
                    Window w = SwingUtilities.getWindowAncestor(this);
                    SwingUtilities.updateComponentTreeUI(w);
                    getCoolBar().invalidate();
                    defaultLf = item.getText();
                } catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException e1) {
                    logger.log(Level.SEVERE, e1.getMessage(), e1);
                }

        } else if ("help".equals(e.getActionCommand())) {
            showHelp();
        }

    }

}
