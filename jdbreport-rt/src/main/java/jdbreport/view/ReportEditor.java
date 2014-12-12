/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2010 Andrey Kholmanskih. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, write to the 
 *
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
 */
package jdbreport.view;

import java.io.*;
import java.awt.event.*;

import javax.swing.*;

import javax.swing.ImageIcon;
import javax.swing.WindowConstants;

import jdbreport.grid.JReportGrid;
import jdbreport.grid.TargetGrid;
import jdbreport.model.Cell;
import jdbreport.model.HeighCalculator;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportModel;
import jdbreport.model.StringMetrics;
import jdbreport.model.io.LoadReportException;
import jdbreport.util.Utils;
import jdbreport.util.xml.XMLProperties;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class ReportEditor extends JFrame implements 
		TargetGrid, HeighCalculator {

	private static final long serialVersionUID = 7713663646863597198L;
	
	private ReportEditorPane reportPane = null;

	private int closeOperation = WindowConstants.DISPOSE_ON_CLOSE;

	protected XMLProperties properties;
	
	public ReportEditor() {
		super();
		reportPane = createClientPanel();
		getReportPane().readProperties();
		initialize();
		getReportPane().initProperties();
//		jdbreport.helper.ColorValue.registerValue();
	}

	public ReportEditor(File file) {
		this();
		try {
			getReportBook().open(file);
		} catch (LoadReportException e) {
			Utils.showError(e);
		}
	}

	public ReportEditor(String fileName) {
		this();
		try {
			getReportBook().open(fileName);
		} catch (LoadReportException e) {
			Utils.showError(e);
		}
	}

	public JReportGrid getFocusedGrid() {
		return getReportPane().getFocusedGrid();
	}

	protected void initialize() {
		setDefaultCloseOperation(getCloseOperation());
		this.setContentPane(getReportPane());
		initMenu();

		if (getReportBook().getReportCaption() == null
				|| getReportBook().getReportCaption().length() == 0)
			this.setTitle(getReportPane().getCaption());
		else
			this.setTitle(getReportPane().getCaption() + " - " //$NON-NLS-1$
					+ getReportBook().getReportCaption());
		this.addWindowListener(new java.awt.event.WindowAdapter() {

			public void windowClosing(java.awt.event.WindowEvent e) {
				exitForm(e);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				if (getReportPane() != null) {
					getReportPane().clear();
				}
				setContentPane(new JPanel());
				reportPane = null;
				setJMenuBar(null);
			}

		});
		this.setIconImage(new ImageIcon(getClass().getResource(
				getLogoImage())).getImage()); //$NON-NLS-1$
	}

	protected String getLogoImage() {
		return "/jdbreport/resources/logo.png";
	}

	/**
	 * @param closeOperation
	 *            the closeOperation to set
	 */
	public void setCloseOperation(int closeOperation) {
		this.closeOperation = closeOperation;
	}

	/**
	 * @return the closeOperation
	 */
	public int getCloseOperation() {
		return closeOperation;
	}

	protected void initMenu() {
		JMenuBar menuBar = getReportPane().createJMenuBar();
		menuBar.add(getReportPane().getLfMenu(), menuBar.getComponentCount() - 1);
		setJMenuBar(menuBar);
	}

	/**
	 * @return
	 */

	private void exitForm(WindowEvent evt) {
		if (!getReportPane().saveQuestion())
			return;
		setDefaultCloseOperation(getCloseOperation());
		getReportPane().writeProperties();
	}


	public boolean open(File file) {
		return getReportPane().open(file);
	}

	public boolean open(byte[] buf, String readerId) {
		return getReportPane().open(buf, readerId);
	}

	/**
	 * This method initializes reportPane
	 * 
	 * @return javax.swing.JPanel
	 */
	public ReportEditorPane getReportPane() {
		return reportPane;
	}

	protected ReportEditorPane createClientPanel() {
		return new ReportEditorPane(properties);
	}


	public void setReportBook(ReportBook reportBook) {
		getReportPane().setReportBook(reportBook);
	}

	/**
	 * @return the reportBook.
	 */
	public ReportBook getReportBook() {
		return getReportPane().getReportBook();
	}


	public File getReportFile() {
		return getReportPane().getReportFile();
	}

	public int calcRowHeight(ReportModel model, Cell cell, int row, int column) {
		return getReportPane().calcRowHeight(model, cell, row, column);
	}

	public StringMetrics getStringMetrics() {
		return getReportPane().getStringMetrics();
	}

	public static void main(String[] args) {
		Utils.errorHandler = DefaultErrorHandler.getInstance();
		ReportEditor re = new ReportEditor();
		re.getReportPane().changeLookAndFeel();

		re.setCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		if (args.length > 0) {
			try {
				File file = new File(args[0]);
				if (file.exists())
					re.open(file);
			} catch (Exception e) {
				Utils.showError(e);
			}
		}
		re.setVisible(true);
	}

	

}
