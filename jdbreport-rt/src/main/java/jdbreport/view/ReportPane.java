/*
 * ReportPane.java
 *
 * JDBReport Generator
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

package jdbreport.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.PageRanges;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableCellRenderer;

import jdbreport.grid.JReportGrid;
import jdbreport.grid.ReportAction;
import jdbreport.grid.ReportCellRenderer;
import jdbreport.grid.ReportPrintable;
import jdbreport.grid.ReportResources;
import jdbreport.grid.TargetGrid;
import jdbreport.grid.UndoEvent;
import jdbreport.grid.UndoListener;
import jdbreport.grid.ReportAction.EditRedoAction;
import jdbreport.grid.ReportAction.EditUndoAction;
import jdbreport.grid.undo.AbstractGridUndo;
import jdbreport.grid.undo.UndoItem;
import jdbreport.grid.undo.UndoList;
import jdbreport.model.Cell;
import jdbreport.model.HeightCalculator;
import jdbreport.util.finder.FindParams;
import jdbreport.util.finder.Finder;
import jdbreport.util.xml.XMLProperties;
import jdbreport.view.finder.FindPanel;
import jdbreport.view.model.JReportModel;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportModel;
import jdbreport.model.StringMetrics;
import jdbreport.model.event.ReportListEvent;
import jdbreport.model.event.ReportListListener;
import jdbreport.model.io.FileType;
import jdbreport.model.io.LoadReportException;
import jdbreport.model.io.ReportReader;
import jdbreport.model.io.ReportWriter;
import jdbreport.print.PrintPreview;
import jdbreport.util.Utils;

/**
 * @version 3.0 13.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class ReportPane extends JPanel implements ReportListListener,
		UndoListener, PropertyChangeListener, TargetGrid, HeightCalculator,
		Finder {

	private static final Logger logger = Logger.getLogger(ReportPane.class
			.getName());

	private static final String REPORT_BOOK_PROPERTY = "reportBook";

	private static final String CAPTION = "caption";

	private static final String REPORT_CAPTION_PROPERTY = "reportCaption";

	private static final String SHOW_GRID_PROPERTY = "showGrid";//$NON-NLS-1$

	private static final String VISIBLE_PROPERTY = "visible";//$NON-NLS-1$

	private static final String REPORT_TITLE_PROPERTY = "reportTitle";//$NON-NLS-1$

	private static final String REMOVE_GRID_PROPERTY = "removeGrid";//$NON-NLS-1$

	private static final String ADD_GRID_PROPERTY = "addGrid";//$NON-NLS-1$

	private static final String DEFAULT_SHEET_NAME = "Sheet";//$NON-NLS-1$

	private static final String DIRTY_PROPERTY = "dirty";//$NON-NLS-1$

	private static final long serialVersionUID = 1L;

	static String defaultLf;

	public static final String ODS_COMMAND = "ods-command";//$NON-NLS-1$

	public static final String EXCEL_COMMAND = "excel-command";//$NON-NLS-1$

	public static final String LOOK_AND_FEEL = "lookAndFeel"; //$NON-NLS-1$

	public static final String CURRENT_FILE_FILTER = "file_filtr"; //$NON-NLS-1$

	public static final String WINDOW_STATE = "window_state"; //$NON-NLS-1$

	public static final String JDBREPORT_CONF = ".jdbr.conf"; //$NON-NLS-1$

	public static final String POS_X = "pos_x"; //$NON-NLS-1$

	public static final String POS_Y = "pos_y"; //$NON-NLS-1$

	public static final String SIZE_WIDTH = "size_width"; //$NON-NLS-1$

	public static final String SIZE_HEIGHT = "size_height"; //$NON-NLS-1$

	private JScrollPane scrollPane;

	private ReportBook reportBook;

	private JReportGrid focusedGrid;

	private java.util.List<JReportGrid> reportGridList;

	private JTabbedPane tabbedPane = null;

	protected XMLProperties properties;

	private File reportFile;

	protected JPopupMenu gridMenu;

	private JPopupMenu tabMenu;

	private boolean dirty;

	private Action selectAllAction;

	private Action deleteAction;

	private Action pasteAction;

	private Action copyAction;

	private Action cutAction;

	private EditUndoAction undoAction;

	private EditRedoAction redoAction;

	private Action findAction;

	private LinkedList<UndoItem> undoStack;

	private LinkedList<UndoItem> redoStack;

	private FindPanel findPanel;

	public static String CURRENT_FILTER = ""; //$NON-NLS-1$

	public static String CURRENT_DIRECTORY_PATH = "."; //$NON-NLS-1$

	static final String CURRENT_DIRECTORY = "current_directory"; //$NON-NLS-1$

	public static String CURRENT_IMAGE_PATH = "."; //$NON-NLS-1$

	static final String CURRENT_IMAGE_DIRECTORY = "current_image_directory"; //$NON-NLS-1$

	/**
	 * To communicate errors between threads during printing.
	 */
	private Throwable printError;

	public ReportPane(XMLProperties properties) {
		this.properties = properties;
		setLayout(new BorderLayout());
		initActions();
		add(getScrollPane(), BorderLayout.CENTER);
		installListeners();
	}

	public XMLProperties getProperties() {
		return properties;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getFocusedGrid());
		}
		return scrollPane;
	}

	public int calcRowHeight(ReportModel model, Cell cell, int row, int column) {
		TableCellRenderer renderer =  getDefaultGrid()
				.getCellRenderer(cell);
		if (renderer instanceof ReportCellRenderer) {
			return ((ReportCellRenderer) renderer).getTextHeight(model, row,
					column);
		}
		return 0;
	}

	public StringMetrics getStringMetrics() {
		return getDefaultGrid().getStringMetrics();
	}

	protected void addReportGrid(ReportModel rm) {
		addReportGrid(reportGridList.size(), rm);
	}

	protected void addReportGrid(int index, ReportModel rm) {
		addReportGrid(createReportGrid(rm), index);
	}

	private void addGridActions(JReportGrid grid) {
		grid.addAction(getFindAction());
		grid.addAction(getCutAction());
		grid.addAction(getCopyAction());
		grid.addAction(getPasteAction());
		grid.addAction(getUndoAction());
		grid.addAction(getRedoAction());
	}

	private void removeGridActions(JReportGrid grid) {
		grid.removeAction(getFindAction());
		grid.removeAction(getCutAction());
		grid.removeAction(getCopyAction());
		grid.removeAction(getPasteAction());
		grid.removeAction(getUndoAction());
		grid.removeAction(getRedoAction());
	}

	public void addReportGrid(JReportGrid grid, int index) {
		addGridActions(grid);
		reportGridList.add(index, grid);
		if (reportGridList.size() > 1 || tabbedPane != null) {
			if (canShowGrid(grid.getReportModel())) {
				if (index < 0 || index > getTabbedPane().getTabCount())
					index = getTabbedPane().getTabCount();
				getTabbedPane().insertTab(
						grid.getReportModel().getReportTitle(),
						getTabbedIcon(), new JScrollPane(grid), null, index);
			}
		} else {
			if (canShowGrid(grid.getReportModel())) {
				getScrollPane().setViewportView(grid);
			} else {
				getScrollPane().removeAll();
			}
		}
		grid.setShowGrid(getReportBook().isShowGrid());
		if (grid.getReportModel().getReportTitle() == null
				|| grid.getReportModel().getReportTitle().length() == 0) {
			grid.getReportModel().setReportTitle(
					DEFAULT_SHEET_NAME + reportGridList.size());
		}
		grid.setComponentPopupMenu(getGridMenu());
		focusedGrid = null;
		addGridListeners(grid);
		firePropertyChange(ADD_GRID_PROPERTY, null, grid);
	}

	protected void addGridListeners(JReportGrid grid) {
		grid.addPropertyChangeListener(this);
		grid.addUndoListener(this);
	}

	protected void removeGridListeners(JReportGrid grid) {
		grid.removePropertyChangeListener(this);
		grid.removeUndoListener(this);
	}

	protected JReportGrid createReportGrid(ReportModel rm) {
		return new JReportGrid(rm);
	}

	protected boolean canShowGrid(ReportModel rm) {
		return rm.isVisible();
	}

	public void removeReportGrid(JReportGrid grid) {
		removeGridActions(grid);
		reportGridList.remove(grid);
		if (tabbedPane != null) {
			int index = indexOfTabbed(grid);
			if (index >= 0)
				tabbedPane.remove(index);
		}
		firePropertyChange(REMOVE_GRID_PROPERTY, grid, null);
		removeGridListeners(grid);
	}

	private JReportGrid getDefaultGrid() {
		return reportGridList.get(0);
	}

	public JReportGrid getFocusedGrid() {
		if (focusedGrid == null) {
			int i = 0;
			while (!canShowGrid(getReportGrid(i).getReportModel())
					&& i < getReportGridList().size()) {
				i++;
			}
			if (i < getReportGridList().size()) {
				setFocusedGrid(getReportGrid(i));
			} else {
				setFocusedGrid(getReportGrid(0));
			}
		}
		return focusedGrid;
	}

	/**
	 * @return the reportBook.
	 */
	public ReportBook getReportBook() {
		return reportBook;
	}

	void setFocusedGrid(JReportGrid grid) {
		if (focusedGrid != grid) {
			focusedGrid = grid;
			if (grid != null) {
				if (tabbedPane != null) {
					int index = indexOfTabbed(grid);
					if (index != getTabbedPane().getSelectedIndex())
						getTabbedPane().setSelectedIndex(index);
				}
				grid.fireCellSelectChanged();
			}
		}
	}

	protected JPopupMenu getGridMenu() {
		return gridMenu;
	}

	protected void setGridMenu(JPopupMenu gridMenu) {
		this.gridMenu = gridMenu;
		getFocusedGrid().setComponentPopupMenu(gridMenu);
	}

	public boolean moveSheet(int fromIndex, int toIndex) {
		if ((toIndex < fromIndex && toIndex >= 0)
				|| (toIndex > fromIndex && toIndex < getReportGridList().size())) {
			getReportBook().move(fromIndex, toIndex);
			setFocusedGrid(toIndex);
			getTabbedPane().setSelectedIndex(toIndex);
			return true;
		}
		return false;
	}

	public void setFocusedGrid(int index) {
		setFocusedGrid(getReportGrid(index));
	}

	protected int getFocusedIndex() {
		if (focusedGrid == null) {
			return -1;
		}

		return tabbedPane != null ? indexOfTabbed(focusedGrid) : 0;
	}

	public java.util.List<JReportGrid> getReportGridList() {
		if (reportGridList == null) {
			reportGridList = new ArrayList<>();
			createNew();
		}
		return reportGridList;
	}

	private void createNew() {
		reportBook = createDefaultReportBook();
		JReportModel rm = reportBook.getReportModel(0);
		JReportGrid reportGrid = createReportGrid(rm);
		addReportGrid(reportGrid, 0);
		setFocusedGrid(reportGrid);
		resetDirty();
	}

	public void resetDirty() {
		getUndoStack().clear();
		getRedoStack().clear();
		updateUndoRedoState();
	}

	public boolean isDirty() {
		return dirty;
	}

	private void setDirty(boolean dirty) {
		boolean oldDirty = this.dirty;
		this.dirty = dirty;
		firePropertyChange(DIRTY_PROPERTY, oldDirty, dirty);
	}

	protected ReportBook createDefaultReportBook() {
		return new ReportBook();
	}

	public JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			remove(scrollPane);
			scrollPane.removeAll();
			JReportGrid grid = getReportGrid(0);
			if (canShowGrid(grid.getReportModel())) {
				tabbedPane.addTab(grid.getReportModel().getReportTitle(),
						getTabbedIcon(), new JScrollPane(grid));
			}
			add(tabbedPane, BorderLayout.CENTER);
			tabbedPane.addChangeListener(e -> {
                if (tabbedPane.getSelectedIndex() >= 0) {
                    JScrollPane scrollPane1 = (JScrollPane) tabbedPane
                            .getSelectedComponent();
                    JReportGrid grid1 = (JReportGrid) scrollPane1
                            .getViewport().getComponent(0);
                    int index = getReportGridList().indexOf(grid1);
                    setFocusedGrid(index);
                }
            });
			tabbedPane.setComponentPopupMenu(getTabMenu());
		}
		return tabbedPane;
	}

	protected Icon getTabbedIcon() {
		return null;
	}

	protected JPopupMenu getTabMenu() {
		return tabMenu;
	}

	public void setTabMenu(JPopupMenu tabMenu) {
		this.tabMenu = tabMenu;
		if (tabbedPane != null) {
			tabbedPane.setComponentPopupMenu(tabMenu);
		}
	}

	public JReportGrid getReportGrid(int index) {
		java.util.List<JReportGrid> list = getReportGridList();
		if (index >= 0 && index < list.size())
			return list.get(index);
		return null;
	}

	public void reportAdded(ReportListEvent e) {
		int index = e.getToIndex();
		JReportModel rm = ((ReportBook) e.getSource()).getReportModel(index);
		addReportGrid(index, rm);
	}

	public void reportMoved(ReportListEvent e) {
		int toIndex = e.getToIndex();
		int fromIndex = e.getFromIndex();
		JReportGrid grid = getReportGridList().remove(fromIndex);
		getReportGridList().add(toIndex, grid);
		Component obj = getTabbedPane().getComponentAt(fromIndex);
		getTabbedPane().remove(fromIndex);
		getTabbedPane().insertTab(grid.getReportModel().getReportTitle(),
				getTabbedIcon(), obj, null, toIndex);
	}

	public void reportRemoved(ReportListEvent e) {
		int index = e.getFromIndex();
		if (index >= 0) {
			removeReportGrid(getReportGrid(index));
		}
	}

	/**
	 * Find ReportGrid in tabbedPane
	 * 
	 * @param index report index
	 * @return JReportGrid
	 */
	protected JReportGrid findReportGrid(int index) {
		if (tabbedPane != null) {
			if (index >= 0 && index < tabbedPane.getTabCount()) {
				Component c = tabbedPane.getComponentAt(index);
				if (c instanceof JReportGrid)
					return (JReportGrid) c;
				if (c instanceof Container) {
					return findGridInContainer((Container) c);
				}
			}
			return null;
		} else {
			return getReportGrid(index);
		}

	}

	private JReportGrid findGridInContainer(Container container) {
		int count = container.getComponentCount();
		for (int n = 0; n < count; n++) {
			Component child = container.getComponent(n);
			if (child instanceof JReportGrid) {
				return (JReportGrid) child;
			} else if (child instanceof Container) {
				JReportGrid result = findGridInContainer((Container) child);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	protected int indexOfTabbed(JReportGrid grid) {
		if (tabbedPane != null) {
			for (int i = 0; i < tabbedPane.getTabCount(); i++) {
				Component c = tabbedPane.getComponentAt(i);
				if (c == grid)
					return i;
				if (c instanceof Container) {
					if (containingChild((Container) c, grid))
						return i;
				}
			}
		}
		return -1;
	}

	private boolean containingChild(Container container, Object target) {
		int count = container.getComponentCount();
		for (int n = 0; n < count; n++) {
			Object child = container.getComponent(n);
			if (child == target)
				return true;
			else {
				if (child != null && child instanceof Container) {
					if (containingChild((Container) child, target))
						return true;
				}
			}
		}
		return false;
	}

	protected void insertReportBook(ReportBook book) {
		int index = getFocusedIndex() + 1;
		getReportBook().add(book, index);
		setFocusedGrid(findReportGrid(index));
	}

	public boolean loadSheet(File file, ReportReader reader) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			ReportBook book = createDefaultReportBook();
			if (reader != null)
				book.open(file, reader);
			else
				book.open(file);
			insertReportBook(book);
			return true;
		} catch (LoadReportException e) {
			Utils.showError(e);
			return false;
		} finally {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	/**
	 * Printing of all report
	 * 
	 * @return true if the print has been successful
	 * @throws PrinterException
	 * @throws HeadlessException
	 * @since 2.0
	 */
	public boolean print(Component component, List<JReportGrid> grids,
			boolean showPrintDialog, boolean interactive)
			throws PrinterException, HeadlessException {
		return print(component, grids, showPrintDialog, interactive, 0);
	}

    final Object lock = new Object();

    /**
	 * Printing of all report
	 * 
	 * @param showPrintDialog if true then show dialog
	 * @param interactive if true interactive
	 * @param numPage
	 *            default page
	 * @return true if the print has been successful
	 * @throws PrinterException
	 * @throws HeadlessException
	 * @since 2.0
	 */
	public boolean print(Component component, List<JReportGrid> grids,
			boolean showPrintDialog, boolean interactive, int numPage)
			throws PrinterException, HeadlessException {

		boolean isHeadless = GraphicsEnvironment.isHeadless();
		if (isHeadless) {
			if (showPrintDialog) {
				throw new HeadlessException("Can't show print dialog.");
			}

			if (interactive) {
				throw new HeadlessException("Can't run interactively.");
			}
		}

		final PrinterJob job = PrinterJob.getPrinterJob();

		final PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
		attr.addAll(grids.get(0).getPrintAttributes());

		attr.add(new JobName(getReportBook().getReportCaption(), null));

		if (numPage > 0) {
			attr.add(new PageRanges(numPage));
		}

		Pageable pageable = new ReportPageable(grids);
		job.setPageable(pageable);

		if (showPrintDialog && !job.printDialog(attr)) {
			return false;
		}

		if (!interactive) {
			job.print(attr);
			return true;
		}

		printError = null;


		final sun.swing.PrintingStatus printingStatus = sun.swing.PrintingStatus
                        .createPrintingStatus(component, job);

		Runnable runnable = () -> {
            try {
                job.print(attr);
            } catch (Throwable t) {
                synchronized (lock) {
                    printError = t;
                }
            } finally {
                printingStatus.dispose();
            }
        };

		Thread th = new Thread(runnable);
		th.start();

		printingStatus.showModal(true);

		Throwable pe;
		synchronized (lock) {
			pe = printError;
			printError = null;
		}

		if (pe != null) {
			if (pe instanceof PrinterAbortException) {
				return false;
			} else if (pe instanceof PrinterException) {
				throw (PrinterException) pe;
			} else if (pe instanceof RuntimeException) {
				throw (RuntimeException) pe;
			} else if (pe instanceof Error) {
				throw (Error) pe;
			}

			throw new AssertionError(pe);
		}

		return true;
	}

	/**
	 * Printing of all report
	 * 
	 * @since 1.4
	 */
	public void print() {
		if (getReportBook().isPrintThroughPdf()) {
			try {
				printPdfDocument();
			} catch (IOException e) {
				Utils.showError(e);
			}
			return;
		}
		try {
			ArrayList<JReportGrid> grids = new ArrayList<>();

			for (int i = 0; i < getReportGridList().size(); i++) {
				JReportGrid grid = getReportGrid(i);
				if (grid.getReportModel().isVisible()) {
					if (grid.isEditing()) {
						if (!grid.getCellEditor().stopCellEditing()) {
							grid.getCellEditor().cancelCellEditing();
						}
					}
					grids.add(grid);
				}
			}

			if (grids.size() > 0) {
				print(this, grids, !GraphicsEnvironment.isHeadless(),
						!GraphicsEnvironment.isHeadless());
			}
		} catch (PrinterException e1) {
			Utils.showError(e1);
		}
	}

	/**
	 * Print pdf document
	 * 
	 * @throws IOException
	 * 
	 * @since 1.4
	 */
	public void printPdfDocument() throws IOException {
		DocFlavor flavor = DocFlavor.INPUT_STREAM.PDF;

		final PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
		attr.addAll(getReportGridList().get(0).getPrintAttributes());

		attr.add(new JobName(getReportBook().getReportCaption(), null));

		final PrinterJob job1 = PrinterJob.getPrinterJob();

		if (!job1.printDialog(attr)) {
			return;
		}
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			DocPrintJob job = job1.getPrintService().createPrintJob();

			File file;
			try {
				file = File.createTempFile("rep", ".pdf");
				file.deleteOnExit();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			try {
				getReportBook().save(file);

				FileInputStream textStream = null;
				try {
					textStream = new FileInputStream(file);
				} catch (FileNotFoundException ignored) {
				}
				if (textStream == null) {
					return;
				}
				Doc myDoc = new SimpleDoc(textStream, flavor, null);

				try {
					job.print(myDoc, attr);
				} catch (PrintException ignored) {
				}
			} finally {
				file.delete();
			}
		} finally {
			setCursor(Cursor.getDefaultCursor());
		}
	}

	/**
	 * Printing of the selected sheet
	 */
	public void printSheet() {
		JReportGrid reportGrid = getFocusedGrid();
		if (reportGrid != null) {
			try {
				reportGrid.print();
			} catch (PrinterException e1) {
				Utils.showError(e1);
			}
		}
	}

	protected void preview() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			Window w = SwingUtilities.getWindowAncestor(this);
			PrintPreview pp;
			if (w instanceof Frame)
				pp = new PrintPreview((Frame) w, this);
			else
				pp = new PrintPreview((Dialog) w, this);
			pp.setVisible(true);
		} finally {
			setCursor(Cursor.getDefaultCursor());
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(REPORT_TITLE_PROPERTY)) {
			JReportGrid grid = (JReportGrid) evt.getSource();
			if (tabbedPane == null)
				return;
			int index = indexOfTabbed(grid);
			if (index >= 0)
				tabbedPane.setTitleAt(index, evt.getNewValue().toString());
        } else if (evt.getPropertyName().equals(VISIBLE_PROPERTY)) {
			setGridVisible((JReportGrid) evt.getSource(),
					(Boolean) evt.getNewValue());
		} else if (evt.getPropertyName().equals(SHOW_GRID_PROPERTY)) {
			for (JReportGrid grid : getReportGridList()) {
				grid.setShowGrid((Boolean) evt.getNewValue());
			}
		} else if (evt.getPropertyName().equals(REPORT_CAPTION_PROPERTY)) {
			String s = getCaption();
			if (evt.getNewValue() != null)
				setTitle(s + " - " + evt.getNewValue().toString()); //$NON-NLS-1$
			else
				setTitle(s);
		} else if (evt.getPropertyName().equals("rowSize")) {
			JReportGrid grid = (JReportGrid) evt.getSource();
			grid.getParent().revalidate();
		}
	}

	protected void setGridVisible(JReportGrid source, Boolean newValue) {
		if (source.isVisible() == newValue)
			return;
		if (!newValue) {
			if (tabbedPane == null) {
				source.setVisible(false);
			} else {
				int index = indexOfTabbed(source);
				if (index >= 0)
					getTabbedPane().remove(index);
			}
		} else {
			source.setVisible(true);
			int index = indexOfTabbed(source);
			if (index >= 0)
				return;
			index = getReportGridList().indexOf(source);
			if (index > getTabbedPane().getTabCount())
				index = getTabbedPane().getTabCount();
			getTabbedPane().insertTab(source.getReportModel().getReportTitle(),
					getTabbedIcon(), new JScrollPane(source), null, index);

		}
	}

	protected void installListeners() {
		reportBook.addReportListListener(this);
		reportBook.addPropertyChangeListener(this);
	}

	protected void uninstallListeners() {
		if (reportBook != null) {
			reportBook.removeReportListListener(this);
			reportBook.removePropertyChangeListener(this);
			reportBook = null;
		}
	}

	public void clear() {
		uninstallListeners();
		for (int i = reportGridList.size() - 1; i >= 0; i--)
			removeReportGrid(reportGridList.get(i));
	}

	public void saveSheet(File file, ReportWriter writer) throws IOException {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			ReportBook newReportBook = createDefaultReportBook();
			newReportBook.clear();
			newReportBook.getStyleList().putAll(getReportBook().getStyleList());
			newReportBook.add(getFocusedGrid().getReportModel());
			if (writer == null)
				newReportBook.save(file);
			else
				newReportBook.save(file, writer);
		} finally {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	public void newReport() {
		focusedGrid = null;
		getReportBook().newReport();
		resetDirty();
		this.reportFile = null;
		updateCaption();

	}

	protected void updateCaption() {
		if (getReportFile() == null) {
			if (getReportBook().getReportCaption() == null
					|| getReportBook().getReportCaption().length() == 0)
				setTitle(getCaption());
			else
				setTitle(String.format("%s - %s", getCaption(), //$NON-NLS-1$
						getReportBook().getReportCaption()));
		} else
			setTitle(String.format("%s - %s - %s", getCaption(), //$NON-NLS-1$
					getReportBook().getReportCaption(), getReportFile()
							.getAbsoluteFile()));

	}

	protected void setTitle(String caption) {
		Window w = SwingUtilities.getWindowAncestor(this);
		if (w != null) {
			if (w instanceof JFrame) {
				((JFrame) w).setTitle(caption);
			} else {
				if (w instanceof JDialog) {
					((JDialog) w).setTitle(caption);
				}
			}
		}
	}

	public String getCaption() {
		return ReportResources.getInstance().getString(CAPTION);
	}

	public void setReportBook(ReportBook reportBook) {
		if (this.reportBook != reportBook) {
			ReportBook old = this.reportBook;
			this.reportBook.clear();
			uninstallListeners();
			this.reportBook = reportBook;
			installListeners();
			for (int i = 0; i < reportBook.size(); i++) {
				addReportGrid(reportBook.getReportModel(i));
			}
			getFocusedGrid();
			firePropertyChange(REPORT_BOOK_PROPERTY, old, reportBook);
			this.reportBook.updateRowAndPageHeight(this);
			updateSheetActions();
			updateCaption();
		}
	}

	protected boolean open() {
		if (!saveQuestion())
			return false;
		JFileChooser fileChooser = createFileChooser();
		int status = fileChooser.showOpenDialog(this);
		if (status == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			FileFilter filter = fileChooser.getFileFilter();
			ReportPane.CURRENT_DIRECTORY_PATH = Utils
					.extractFilePath(fileChooser.getSelectedFile().getPath());
			ReportPane.CURRENT_FILTER = filter.getDescription();
			if (filter instanceof ReportFileFilter)
				return open(selectedFile,
						((ReportFileFilter) filter).getReader());
			else
				return open(selectedFile);
		}
		return false;
	}

	public boolean open(File file) {
		return open(file, null);
	}

	public boolean open(File file, ReportReader reader) {
		resetDirty();
		reportFile = file;
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			focusedGrid = null;
			reportBook.clear();
			ReportBook book = createDefaultReportBook();
			if (file.length() > 0) {
				if (reader != null)
					book.open(file, reader);
				else
					book.open(file);
			}
			setReportBook(book);
			updateSheetActions();
			return true;
		} catch (LoadReportException e) {
			Utils.showError(e);
			return false;
		} finally {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	protected void updateSheetActions() {
	}

	public boolean open(byte[] buf, String readerId) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			focusedGrid = null;
			reportBook.clear();
			ReportBook book = createDefaultReportBook();
			book.open(buf, readerId);
			setReportBook(book);
			return true;
		} catch (LoadReportException e) {
			Utils.showError(e);
			return false;
		} finally {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	public boolean saveAs(File file) {
		ReportWriter writer = null;
		if (file == null) {
			JFileChooser fileChooser = createSaveChooser();
			int status = fileChooser.showSaveDialog(this);
			if (status == JFileChooser.APPROVE_OPTION
					&& fileChooser.getSelectedFile() != null) {
				CURRENT_DIRECTORY_PATH = Utils
						.extractFilePath(fileChooser.getSelectedFile()
								.getPath());
				file = fileChooser.getSelectedFile();
				ReportFileFilter selectedFilter = (ReportFileFilter) fileChooser
						.getFileFilter();
				FileType fileType = selectedFilter.getFileType();
				writer = fileType.getWriter();

				String ext = Utils.getFileExtension(file);
				if (!checkExtensions(fileType, ext)) {
					file = new File(file.getPath()
							+ "." + fileType.getExtensions()[0]); //$NON-NLS-1$
				}
			}
			if (file != null && file.exists()) {
				if (JOptionPane
						.showConfirmDialog(
								this,
								Messages.getString("ReportPane.file_exists"), Messages.getString("ReportPane.save"), //$NON-NLS-1$ //$NON-NLS-2$
								JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return false;
				}
			}
		}
		if (file != null) {
			try {
				saveAs(file, writer);
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
				Utils.showError(e);
				return false;
			}
			if (file != reportFile) {
				if (reportBook.getReaderClassName(reportBook.getKeyByFile(file
						.getName())) != null) {
					reportFile = file;
					resetDirty();
				}
			} else {
				resetDirty();
			}
			return true;
		}
		return false;
	}

	private boolean checkExtensions(FileType fileType, String ext) {
		if (ext.length() == 0)
			return false;
		for (String s : fileType.getExtensions()) {
			if (s.equalsIgnoreCase(ext)) {
				return true;
			}
		}
		return false;
	}

	void saveAs(File file, ReportWriter writer) throws IOException {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			if (writer == null) {
				reportBook.save(file);
			} else {
				reportBook.save(file, writer);
			}
		} finally {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	/**
	 * Returns default filter
	 * 
	 * @param fileChooser JFileChooser
	 * @return default FileFilter
	 * @since 1.3
	 */
	protected FileFilter getDefaultFilter(JFileChooser fileChooser) {
		FileFilter defaultFilter = null;
		FileFilter[] filters = fileChooser.getChoosableFileFilters();
		for (FileFilter filter : filters) {
			if (filter.getDescription().equals(CURRENT_FILTER)) {
				return filter;
			}
			if (filter instanceof ReportFileFilter) {
				if (((ReportFileFilter) filter).getFileType().getExtensions()[0]
						.equals(ReportBook.JRPT)) {
					defaultFilter = filter;
				}
			}
		}
		return defaultFilter;
	}

	private JFileChooser createSaveChooser() {
		JFileChooser fileChooser = new JFileChooser(CURRENT_DIRECTORY_PATH);
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		fileChooser.removeChoosableFileFilter(fileChooser
				.getAcceptAllFileFilter());
		List<FileType> sortedList = new ArrayList<>();
		for (String className : getReportBook().getWriterNames()) {
			sortedList.add(getFileTypeClass(className));
		}
		Collections.sort(sortedList);
		for (FileType fileType : sortedList) {
			if (fileType.getExtensions() != null) {
				ReportFileFilter filter = new ReportFileFilter(fileType);
				fileChooser.addChoosableFileFilter(filter);
			}
		}
		FileFilter currentFilter = getDefaultFilter(fileChooser);
		if (currentFilter != null)
			fileChooser.setFileFilter(currentFilter);

		if (getReportFile() != null) {
			File file;
			int i = getReportFile().getName().lastIndexOf('.');
			if (i > 0)
				file = new File(getReportFile().getName().substring(0, i));
			else
				file = getReportFile();
			fileChooser.setSelectedFile(file);
		} else {
			String s = getReportBook().getReportCaption();
			if (s.length() > 250) {
				s = s.substring(0, 250);
			}
			fileChooser.setSelectedFile(new File(s));
		}
		return fileChooser;
	}

	public static FileType getFileTypeClass(String className) {
		try {
			if (className != null)
				return (FileType) Class.forName(className).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
        return null;
	}

	protected JFileChooser createFileChooser() {
		JFileChooser fileChooser = new JFileChooser(
				ReportPane.CURRENT_DIRECTORY_PATH);
		for (String className : getReportBook().getReaderNames()) {
			FileType reader = getFileTypeClass(className);
			ReportFileFilter filter = new ReportFileFilter(reader);
			fileChooser.addChoosableFileFilter(filter);
		}
		FileFilter currentFilter = getDefaultFilter(fileChooser);
		if (currentFilter != null)
			fileChooser.setFileFilter(currentFilter);
		return fileChooser;
	}

	public void saveSheet() {
		JFileChooser fileChooser = createSaveChooser();
		int status = fileChooser.showSaveDialog(this);
		if (status == JFileChooser.APPROVE_OPTION) {
			ReportPane.CURRENT_DIRECTORY_PATH = fileChooser.getSelectedFile()
					.getPath();
			File file = fileChooser.getSelectedFile();
			ReportFileFilter selectedFilter = (ReportFileFilter) fileChooser
					.getFileFilter();
			ReportPane.CURRENT_FILTER = selectedFilter.getDescription();
			FileType fileType = selectedFilter.getFileType();
			ReportWriter writer = fileType.getWriter();
			if (Utils.getFileExtension(file).length() == 0) {
				file = new File(file.getPath()
						+ "." + fileType.getExtensions()[0]); //$NON-NLS-1$
			}
			try {
				saveSheet(file, writer);
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
				Utils.showError(e);
			}
		}
	}

	public boolean loadSheet() {
		JFileChooser fileChooser = createFileChooser();
		int status = fileChooser.showOpenDialog(this);
		if (status == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			FileFilter filter = fileChooser.getFileFilter();
			ReportPane.CURRENT_DIRECTORY_PATH = fileChooser.getSelectedFile()
					.getPath();
			ReportPane.CURRENT_FILTER = filter.getDescription();
			if (filter instanceof ReportFileFilter)
				return loadSheet(selectedFile,
						((ReportFileFilter) filter).getReader());
			else
				return loadSheet(selectedFile, null);
		}
		return false;
	}

	protected boolean save() {
		return saveAs(reportFile);
	}

	public File getReportFile() {
		return reportFile;
	}

	public void insertReportGrid(int index, JReportGrid grid) {
		if (index < 0)
			index = getReportBook().size();
		getReportBook().removeReportListListener(this);
		try {
			getReportBook().add(index, grid.getReportModel());
			addReportGrid(grid, index);
		} finally {
			getReportBook().addReportListListener(this);
		}
	}

	public int getTabCount() {
		if (tabbedPane != null) {
			return tabbedPane.getTabCount();
		}
		return 0;
	}

	protected void initActions() {
		findAction = new ReportAction.BasedAction("find") { //$NON-NLS-1$

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				showFindPanel();
			}

		};
		selectAllAction = ReportAction.createGridAction(
				ReportAction.EDIT_SELECT_ALL_ACTION, this);
		copyAction = ReportAction.createGridAction(
				ReportAction.EDIT_COPY_ACTION, this);

		cutAction = ReportAction.createGridAction(ReportAction.EDIT_CUT_ACTION,
				this);
		pasteAction = ReportAction.createGridAction(
				ReportAction.EDIT_PASTE_ACTION, this);
		deleteAction = ReportAction.createGridAction(
				ReportAction.EDIT_DELETE_ACTION, this);
		undoAction = new ReportAction.EditUndoAction(getUndoStack()) {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				undo();
				updateUndoRedoState();
			}
		};
		redoAction = new ReportAction.EditRedoAction(getRedoStack()) {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				redo();
				updateUndoRedoState();
			}
		};

	}

	public Action getFindAction() {
		return findAction;
	}

	public Action getCutAction() {
		return cutAction;
	}

	/**
	 * @return the selectAllAction
	 */
	public Action getSelectAllAction() {
		return selectAllAction;
	}

	/**
	 * @return the deleteAction
	 */
	public Action getDeleteAction() {
		return deleteAction;
	}

	/**
	 * @return the pasteAction
	 */
	public Action getPasteAction() {
		return pasteAction;
	}

	/**
	 * @return the copyAction
	 */
	public Action getCopyAction() {
		return copyAction;
	}

	/**
	 * @return the undoAction
	 */
	public EditUndoAction getUndoAction() {
		return undoAction;
	}

	/**
	 * @return the redoAction
	 */
	public EditRedoAction getRedoAction() {
		return redoAction;
	}

	protected void showFindPanel() {
		if (findPanel == null) {
			findPanel = new FindPanel(this);
			findPanel.setIncremental(true);
			this.add(findPanel, BorderLayout.SOUTH);
			this.revalidate();
		}
		findPanel.setVisible(true);
		findPanel.requestFocus();
	}

	public boolean find(FindParams findParams) {
		Finder finder = getFocusedGrid();
		return finder != null && getFocusedGrid().find(findParams);
	}

	public boolean incrementalFind(FindParams findParams) {
		Finder finder = getFocusedGrid();
        return finder != null && getFocusedGrid().incrementalFind(findParams);
    }

	protected LinkedList<UndoItem> getUndoStack() {
		if (undoStack == null) {
			undoStack = new UndoList();
		}
		return undoStack;
	}

	private LinkedList<UndoItem> getRedoStack() {
		if (redoStack == null) {
			redoStack = new UndoList();
		}
		return redoStack;
	}

	public void pushUndo(UndoEvent evt) {
		pushUndo(evt.getUndoItem());
	}

	protected void pushUndo(UndoItem undo) {
		getUndoStack().addFirst(undo);
		updateUndoRedoState();
	}

	public void unionUndo(UndoEvent evt) {
		UndoItem item = null;
		if (getUndoStack().size() > 0)
			item = getUndoStack().getFirst();
		if (item == null || !item.equals(evt.getUndoItem()))
			pushUndo(evt);
	}

	public void undo() {
		UndoItem item = getUndoStack().poll();
		if (item != null) {
			UndoItem redoItem = item.undo();
			if (redoItem != null)
				getRedoStack().addFirst(redoItem);
			if (item instanceof AbstractGridUndo)
				setFocusedGrid(((AbstractGridUndo) item).getGrid());
		}
	}

	public void redo() {
		UndoItem item = getRedoStack().poll();
		if (item != null) {
			UndoItem undoItem = item.undo();
			if (undoItem != null)
				getUndoStack().addFirst(undoItem);
			if (item instanceof AbstractGridUndo)
				setFocusedGrid(((AbstractGridUndo) item).getGrid());
		}
	}

	public boolean hasUndo() {
		return getUndoStack().size() > 0;
	}

	public boolean hasRedo() {
		return getRedoStack().size() > 0;
	}

	public boolean isModified() {
		return isDirty() || hasUndo();
	}

	protected void updateUndoRedoState() {
		JReportGrid grid = getFocusedGrid();
		if (grid != null) {
			getUndoAction().setEnabled(hasUndo());
			getRedoAction().setEnabled(hasRedo());
		} else {
			getUndoAction().setEnabled(false);
			getRedoAction().setEnabled(false);
		}
		setDirty(hasUndo());
	}

	public boolean saveQuestion() {
		if (isModified()) {
			JFrame frame = getParentFrame();
			int result = JOptionPane.showConfirmDialog(this,
					Messages.getString("ReportEditor.14")); //$NON-NLS-1$
			if (result == JOptionPane.YES_OPTION) {
				if (!save()) {
					if (frame != null) {
						frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
					}
					return false;
				}
			} else if (result == JOptionPane.CANCEL_OPTION) {
				if (frame != null) {
					frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				}
				return false;
			}
		}
		return true;
	}

	protected JFrame getParentFrame() {
		Window w = SwingUtilities.getWindowAncestor(this);
		if (w != null && w instanceof JFrame) {
			return (JFrame) w;
		}
		return null;
	}

	private static class ReportPageable implements Pageable, Printable {

		private List<JReportGrid> gridList;
		private int numberOfPages;
		private int retVal;
		private Throwable retThrowable;
		private int[] numbers;
		private int currentGrid;
		private Printable printable;

		public ReportPageable(List<JReportGrid> grids) {
			this.gridList = grids;
			numbers = new int[gridList.size()];

			for (int i = 0; i < gridList.size(); i++) {
				JReportGrid grid = gridList.get(i);
				int pageCount = 0;
				if (grid.getReportModel().isCanUpdatePages()) {
					pageCount = grid.getReportModel().getRowModel()
							.getPageCount();
				}
				if (pageCount == 0) {
					ReportPrintable printable = (ReportPrintable) grid
							.getReportPrintable(false);
					pageCount = printable.calcCountPage(grid.getReportModel()
							.getReportPage());
				}
				numbers[i] = pageCount;
			}
			numberOfPages = 0;
			for (int number : numbers) {
				if (number == 0) {
					numberOfPages = UNKNOWN_NUMBER_OF_PAGES;
					break;
				} else {
					numberOfPages += number;
				}

			}

		}

		public int getNumberOfPages() {
			return numberOfPages;
		}

		public PageFormat getPageFormat(int pageIndex)
				throws IndexOutOfBoundsException {
			PageFormat pageFormat = null;
			int count = 0;
			for (int i = 0; i < numbers.length; i++) {
				if (numbers[i] == 0 || pageIndex < count + numbers[i]) {
					pageFormat = gridList.get(i).getReportModel()
							.getReportPage();
					break;
				}
				count += numbers[i];
			}
			return pageFormat;
		}

		public Printable getPrintable(int pageIndex)
				throws IndexOutOfBoundsException {
			return this;
		}

		private void printPage(final Graphics graphics,
				final PageFormat pageFormat, final int pageIndex)
				throws PrinterException {
			int count = 0;
			for (int i = 0; i < numbers.length; i++) {
				if (numbers[i] == 0 || pageIndex < count + numbers[i]) {
					if (printable == null || currentGrid != i) {
						currentGrid = i;
						printable = gridList.get(currentGrid)
								.getReportPrintable(false);
					}
					break;
				}
				count += numbers[i];
			}
			if (printable != null) {
				retVal = printable.print(graphics, pageFormat, pageIndex
						- count);
				if (retVal == Printable.NO_SUCH_PAGE
						&& numbers[currentGrid] == 0) {
					numbers[currentGrid] = pageIndex - count;
					retVal = -1;
					printPage(graphics, pageFormat, pageIndex);
				}
			} else {
				retVal = Printable.NO_SUCH_PAGE;
			}
		}

		public int print(final Graphics graphics, final PageFormat pageFormat,
				final int pageIndex) throws PrinterException {

			final Runnable runnable = () -> {
                try {
                    printPage(graphics, pageFormat, pageIndex);
                } catch (Throwable throwable) {
					logger.severe(throwable.toString());
					retThrowable = throwable;
				}
			};

			synchronized (runnable) {
				retVal = -1;
				retThrowable = null;

				try {
					SwingUtilities.invokeAndWait(runnable);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}

				while (retVal == -1 && retThrowable == null) {
					try {
						runnable.wait();
					} catch (InterruptedException ignored) {
						break;
					}
				}

				if (retThrowable != null) {
					if (retThrowable instanceof PrinterException) {
						throw (PrinterException) retThrowable;
					} else if (retThrowable instanceof RuntimeException) {
						throw (RuntimeException) retThrowable;
					} else if (retThrowable instanceof Error) {
						throw (Error) retThrowable;
					}

					throw new AssertionError(retThrowable);
				}

				return retVal;
			}
		}

	}
}
