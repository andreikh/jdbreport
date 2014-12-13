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
package jdbreport.print;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import jdbreport.grid.Consts;
import jdbreport.grid.JReportGrid;
import jdbreport.grid.ReportAction;
import jdbreport.grid.ReportPrintable;
import jdbreport.util.Utils;
import jdbreport.util.xml.XMLProperties;
import jdbreport.view.ReportPane;

import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class PrintPreview extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	

	private static final String SCALE = "preview_scale"; //$NON-NLS-1$

	private static final String PAGE_COUNT = "preview_page_count"; //$NON-NLS-1$

	protected JComboBox scaleBox;

	protected PreviewContainer previewPanel;

	private JToolBar toolBar;

	private Action printAction;

	private Action firstAction;

	private Action lastAction;

	private Action nextAction;

	private Action prevAction;

	private int scale = 50;

	private int countInPanel = 1;

	private Map<Integer, PagePreview> pageMap = new HashMap<Integer, PagePreview>();

	private int currentPage = -1;

	private int countPage = Integer.MAX_VALUE;

	private JPanel statusPanel;

	private JLabel numberLabel;

	private JComboBox countPageBox;

	private PagePreview focusedPage;

	private FocusListener focusListener;

	private int calcCountPage;

	private List<JReportGrid> gridList;

	private int[] numbers;

	private ReportPane reportPane;

	public PrintPreview(Frame owner, ReportPane reportPane) {
		super(owner, true);
		init(owner, reportPane);
	}

	public PrintPreview(Dialog owner, ReportPane reportPane) {
		super(owner, true);
		init(owner, reportPane);
	}
	
	private void init(Window owner, ReportPane reportPane) {
		this.reportPane = reportPane;
		setTitle(reportPane.getReportBook().getReportCaption());
		getContentPane().setLayout(new BorderLayout());
		ArrayList<JReportGrid> grids = new ArrayList<JReportGrid>();
		for (JReportGrid grid : reportPane.getReportGridList()) {
			if (grid.getReportModel().isVisible()) {
				grid.updatePages(0);
				grids.add(grid);
			}
		}
		this.gridList = grids;
		int focusedIndex = 0;
		numbers = new int[gridList.size()];
		for (int i = 0; i < gridList.size(); i++) {
			JReportGrid grid = gridList.get(i);
			if (grid == reportPane.getFocusedGrid()) {
				focusedIndex = i; 
			}
			int pageCount = 0;
			if (grid.getReportModel().isCanUpdatePages()) {
				pageCount = grid
					.getReportModel().getRowModel().getPageCount();
			} 
			if (pageCount == 0) {
				ReportPrintable printable = (ReportPrintable) gridList.get(i).getReportPrintable(true);
				pageCount = printable.calcCountPage(grid.getReportModel().getReportPage());
			}
			numbers[i] = pageCount;
		}

		int firstIndex = 0;
		for (int i = 0; i < focusedIndex; i++) {
			firstIndex += numbers[i];
		}
		
		focusListener = new FocusListener();
		initProperties(reportPane.getProperties());
		initialize();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.addWindowListener(new java.awt.event.WindowAdapter() {

			public void windowClosed(java.awt.event.WindowEvent e) {
				saveProperties();
			}

		});
		Utils.screenCenter(this);
		try {
			goPage(firstIndex);
			setFocusedPageNumber(firstIndex);
		} catch (PrinterException e) {
			e.printStackTrace();
		}
		setBounds(new Rectangle(0, 0, owner.getWidth(), owner.getHeight()));
		setLocation(owner.getX(), owner.getY());
	}
	
	protected void initProperties(XMLProperties properties) {
		if (properties != null) {
			scale = properties.getInt(SCALE, 50);
			countInPanel = properties.getInt(PAGE_COUNT, 1);
		}
	}

	protected void saveProperties() {
		XMLProperties properties = reportPane.getProperties();
		if (properties != null) { 
			reportPane.getProperties().put(SCALE, "" + scale); //$NON-NLS-1$
			reportPane.getProperties().put(PAGE_COUNT, "" + countInPanel); //$NON-NLS-1$
		}
	}

	private void initialize() {
		getContentPane().add(getToolBar(), BorderLayout.NORTH);
		JScrollPane scrollPane = new JScrollPane(getPreviewPanel());
		scrollPane.getVerticalScrollBar().setBlockIncrement(200);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(getStatusPanel(), BorderLayout.SOUTH);
	}

	private JPanel getStatusPanel() {
		if (statusPanel == null) {
			statusPanel = new JPanel();
			statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			statusPanel.setBorder(BorderFactory
					.createBevelBorder(BevelBorder.LOWERED));
			statusPanel.add(getNumberLabel());
		}
		return statusPanel;
	}

	private JLabel getNumberLabel() {
		if (numberLabel == null) {
			numberLabel = new JLabel();
			numberLabel.setFont(Consts.statusFont);
			updateStatus();
		}
		return numberLabel;
	}

	private void updateStatus() {
		String t = Messages.getString("PrintPreview.3"); //$NON-NLS-1$
		int current = getFocusedPageNumber();
		if (current >= 0) {
			t += " " + (current + 1);
			if (countPage < Integer.MAX_VALUE)
				t += " / " + countPage; //$NON-NLS-1$
			else if (calcCountPage > 0) {
				t += " / " + calcCountPage; //$NON-NLS-1$
			}
		}
		numberLabel.setText(t);
	}

	private int getFocusedPageNumber() {
		int current = -1;
		if (focusedPage != null) {
			current = focusedPage.getPageIndex();
		} else if (currentPage >= 0) {
			current = currentPage;
		}
		return current;
	}

	private void setFocusedPageNumber(int number) {
		for (int i = 0; i < previewPanel.getComponentCount(); i++) {
			Component comp = previewPanel.getComponent(i);
			if (comp instanceof PagePreview) {
				if (((PagePreview) comp).getPageIndex() == number) {
					setFocusPage((PagePreview) comp);
				}
			}
		}
	}
	
	private PreviewContainer getPreviewPanel() {
		if (previewPanel == null) {
			previewPanel = new PreviewContainer();
		}
		return previewPanel;
	}

	/**
	 * @param pageIndex
	 * @return
	 * @throws PrinterException
	 */
	private PagePreview printPage(int pageIndex) throws PrinterException {
		Printable printable = null;
		PageFormat pageFormat = null;
		int currentGrid = 0;
		int count = 0;
		for (int i = 0; i < numbers.length; i++) {
			if (numbers[i] == 0 || pageIndex < count + numbers[i]) {
				currentGrid = i;
				printable = gridList.get(i).getReportPrintable(true);
				pageFormat = gridList.get(i).getReportModel().getReportPage();
				if (pageFormat == null) {
					pageFormat = PrinterJob.getPrinterJob().defaultPage();
				}
				break;
			}
			count += numbers[i];
		}
		if (printable != null) {
			int widthPage = (int) pageFormat.getWidth();
			int heightPage = (int) pageFormat.getHeight();

			PagePreview pp = pageMap.get(pageIndex);
			if (pp != null) {
//				pp.setScale(scale);
				return pp;
			}
			BufferedImage img = new BufferedImage(widthPage, heightPage,
					BufferedImage.TYPE_INT_RGB);
			Graphics g = img.getGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, widthPage, heightPage);
			boolean pageExists = printable.print(g, pageFormat, pageIndex - count) == Printable.PAGE_EXISTS;
			if (pageExists) {
				pp = new PagePreview(widthPage, heightPage, scale, img,
						pageIndex);
				pageMap.put(pageIndex, pp);
				return pp;
			} else {
				if (numbers[currentGrid] == 0) {
					numbers[currentGrid] = pageIndex - count;
					return printPage(pageIndex);
				}
			}
		}
		return null;
	}

	private JToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = new JToolBar();
			toolBar.setFloatable(false);
			toolBar.add(getFirstAction());
			toolBar.add(getPrevAction());
			toolBar.add(getNextAction());
			toolBar.add(getLastAction());
			toolBar.addSeparator();
			toolBar.add(getPrintAction());
			toolBar.addSeparator();
			toolBar.add(getScaleBox());
			toolBar.addSeparator();
			toolBar.add(getCountPageBox());

			toolBar.addSeparator();
			JButton bt = new JButton(Messages.getString("PrintPreview.22")); //$NON-NLS-1$
			bt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});

			toolBar.add(bt);
		}
		return toolBar;
	}

	private JComboBox getCountPageBox() {
		if (countPageBox == null) {
			String[] count = {"1", "2", "4", "6", "8"};
			countPageBox = new JComboBox(count);
			countPageBox.setToolTipText(Messages.getString("PrintPreview.11"));
			countPageBox.setEditable(false);
			countPageBox.setSelectedItem("" + countInPanel);
			countPageBox.setMaximumSize(countPageBox.getPreferredSize());
			countPageBox.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					String str = countPageBox.getSelectedItem().toString();
					countInPanel = Integer.parseInt(str);
					if (currentPage + countInPanel >= countPage) {
						currentPage = countPage - countInPanel;
						if (currentPage < 0) currentPage = 0;
					}
					try {
						revalidatePage();
					} catch (PrinterException e1) {
						Utils.showError(e1);
					}
				}

			});
		}
		return countPageBox;
	}

	private Action getFirstAction() {
		if (firstAction == null) {
			firstAction = new ReportAction.BasedAction("preview_first") { //$NON-NLS-1$

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					try {
						firstPage();
					} catch (PrinterException e1) {
						Utils.showError(e1);
					}
				}

			};
		}
		return firstAction;
	}

	private Action getLastAction() {
		if (lastAction == null) {
			lastAction = new ReportAction.BasedAction("preview_last") { //$NON-NLS-1$

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					try {
						lastPage();
					} catch (PrinterException e1) {
						Utils.showError(e1);
					}

				}

			};
		}
		return lastAction;
	}

	private Action getNextAction() {
		if (nextAction == null) {
			nextAction = new ReportAction.BasedAction("preview_next") { //$NON-NLS-1$

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					try {
						nextPage();
					} catch (PrinterException e1) {
						Utils.showError(e1);
					}
				}

			};
		}
		
		return nextAction;
	}

	private Action getPrevAction() {
		if (prevAction == null) {
			prevAction = new ReportAction.BasedAction("preview_prev") { //$NON-NLS-1$

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					try {
						prevPage();
					} catch (PrinterException e1) {
						Utils.showError(e1);
					}
				}

			};
		}
		return prevAction;
	}

	/**
	 * 
	 */
	private JComboBox getScaleBox() {
		if (scaleBox == null) {
			String[] scales = { "25 %", "50 %", "75 %", "100 %", "125 %", "150 %" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			scaleBox = new JComboBox(scales);
			scaleBox.setSelectedItem(scale + " %");
			scaleBox.setToolTipText(Messages.getString("PrintPreview.20")); //$NON-NLS-1$
			scaleBox.setActionCommand("scale");
			scaleBox.addActionListener(this);

			scaleBox.setMaximumSize(scaleBox.getPreferredSize());
			scaleBox.setEditable(true);
		}
		return scaleBox;
	}

	public void print() {
		try {
			reportPane.print(this, gridList, true, true, currentPage + 1);
		} catch (PrinterException e1) {
			Utils.showError(e1);
		}
	}

	/**
	 * @return
	 */
	private Action getPrintAction() {
		if (printAction == null) {
			printAction = new ReportAction.BasedAction(ReportAction.PRINT_REPORT_ACTION) {

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					try {
						print();
					} finally {
						setCursor(Cursor
								.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				}

			};
		}
		return printAction;
	}

	private void goPage(int index) throws PrinterException {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			if (currentPage == index)
				return;
			currentPage = index;
			removeAllPage();
			for (int pageIndex = 0; pageIndex < countInPanel; pageIndex++) {
				PagePreview pp = printPage(currentPage + pageIndex);
				if (pp == null) {
					countPage = currentPage + pageIndex;
					break;
				}
				addPage(pp);
			}
			updateActions();
		} finally {
			setCursor(Cursor.getDefaultCursor());
		}
	}

	private void firstPage() throws PrinterException {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			if (currentPage == 0)
				return;
			currentPage = 0;
			removeAllPage();
			for (int pageIndex = 0; pageIndex < countInPanel; pageIndex++) {
				PagePreview pp = printPage(currentPage + pageIndex);
				if (pp == null) {
					countPage = currentPage + pageIndex;
					break;
				}
				addPage(pp);
			}
//			deleteNextPages();
			updateActions();
		} finally {
			setCursor(Cursor.getDefaultCursor());
		}
	}

	private void prevPage() throws PrinterException {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			if (currentPage == 0)
				return;
			currentPage -= countInPanel;
			if (currentPage < 0)
				currentPage = 0;
			removeAllPage();
			for (int pageIndex = 0; pageIndex < countInPanel; pageIndex++) {
				PagePreview pp = printPage(pageIndex + currentPage);
				if (pp == null) {
					countPage = pageIndex + currentPage;
					break;
				}
				addPage(pp);
			}
//			deleteNextPages();
			updateActions();
		} finally {
			setCursor(Cursor.getDefaultCursor());
		}
	}

	private void nextPage() throws PrinterException {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			if (currentPage + countInPanel >= countPage) {
				updateActions();
				return;
			}
			removeAllPage();
			currentPage += countInPanel;
			for (int pageIndex = 0; pageIndex < countInPanel; pageIndex++) {
				PagePreview pp = printPage(pageIndex + currentPage);
				if (pp == null) {
					countPage = pageIndex + currentPage;
					currentPage = countPage - 1;
					if (pageIndex == 0 && countPage > 0) {
						addPage(printPage(currentPage));
					}
					break;
				}
				addPage(pp);
			}
//			deletePrevPages();
			updateActions();
		} finally {
			setCursor(Cursor.getDefaultCursor());
		}
	}

	private void removeAllPage() {
		for (int i = 0; i < previewPanel.getComponentCount(); i++) {
			Component comp = previewPanel.getComponent(i);
			if (comp instanceof PagePreview) {
				((PagePreview) comp).removeMouseListener(focusListener);
			}
		}
		focusedPage = null;
		previewPanel.removeAll();
		pageMap.clear();
	}

	private void lastPage() throws PrinterException {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			if (currentPage + countInPanel >= countPage)
				return;
			removeAllPage();
			if (countPage == Integer.MAX_VALUE) {
				while (printPage(++currentPage) != null) {
					pageMap.clear();
				}
				countPage = currentPage;
			}
			currentPage = countPage - countInPanel;
			if (currentPage < 0)
				currentPage = 0;
			for (int pageIndex = 0; pageIndex < countInPanel; pageIndex++) {
				PagePreview pp = printPage(pageIndex + currentPage);
				if (pp == null) {
					countPage = pageIndex + currentPage;
					break;
				}
				addPage(pp);
			}
//			deletePrevPages();
			updateActions();
		} finally {
			setCursor(Cursor.getDefaultCursor());
		}
	}

	/**
	 * @param pp
	 */
	private void addPage(PagePreview pp) {
		previewPanel.add(pp);
		pp.addMouseListener(focusListener);
		if (focusedPage == null) {
			pp.setFocus(true);
			focusedPage = pp;
		}
	}

	private void revalidatePage() throws PrinterException {
		removeAllPage();
		for (int pageIndex = 0; pageIndex < countInPanel; pageIndex++) {
			PagePreview pp = printPage(pageIndex + currentPage);
			if (pp == null) {
				countPage = pageIndex + currentPage;
				currentPage = countPage - 1;
				break;
			}
			addPage(pp);
		}
		previewPanel.doLayout();
		previewPanel.getParent().getParent().validate();
		updateActions();
	}

	private void updateActions() {
		prevAction.setEnabled(currentPage > 0);
		firstAction.setEnabled(currentPage > 0);
		nextAction.setEnabled(currentPage + countInPanel < countPage);
		lastAction.setEnabled(currentPage + countInPanel < countPage);
		updateStatus();
		previewPanel.revalidate();
		previewPanel.repaint();
	}

	private void setFocusPage(PagePreview pp) {
	if (!pp.equals(focusedPage)) {
		pp.setFocus(true);
		if (focusedPage != null) {
			focusedPage.setFocus(false);
		}
		focusedPage = pp;
		updateStatus();
	}
	}

	private class FocusListener extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			PagePreview pp = (PagePreview) e.getSource();
			setFocusPage(pp);
		}

	}

	public void actionPerformed(ActionEvent e) {
		if ("scale".equals(e.getActionCommand())) {
			Thread runner = new Thread() {

				public void run() {
					String str = scaleBox.getSelectedItem().toString();

					if (str.endsWith("%")) //$NON-NLS-1$
						str = str.substring(0, str.length() - 1);
					str = str.trim();
					try {
						scale = Integer.parseInt(str);
					} catch (NumberFormatException ex) {
						ex.printStackTrace();
						return;
					}
				//	Component[] comps = previewPanel.getComponents();

					try {
						revalidatePage();
					} catch (PrinterException e) {
						e.printStackTrace();
					}
					/*for (int k = 0; k < comps.length; k++) {
						if (!(comps[k] instanceof PagePreview))
							continue;
						PagePreview pp = (PagePreview) comps[k];
						pp.getPageIndex();
					}
*/
				}
			};

			runner.run();
		}

	}
}