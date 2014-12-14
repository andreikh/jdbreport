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
package jdbreport.view;

import java.io.*;
import java.awt.event.*;

import javax.swing.*;

import javax.swing.ImageIcon;
import javax.swing.WindowConstants;

import jdbreport.grid.JReportGrid;
import jdbreport.grid.TargetGrid;
import jdbreport.model.Cell;
import jdbreport.model.HeightCalculator;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportModel;
import jdbreport.model.StringMetrics;
import jdbreport.model.io.LoadReportException;
import jdbreport.util.Utils;
import jdbreport.util.xml.XMLProperties;

/**
 * @version 3.0 13.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class ReportEditor extends JFrame implements 
		TargetGrid, HeightCalculator {

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
			this.setTitle(getReportPane().getCaption() + " - "
					+ getReportBook().getReportCaption());
		this.addWindowListener(new java.awt.event.WindowAdapter() {

			public void windowClosing(java.awt.event.WindowEvent e) {
				exitForm();
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

	private void exitForm() {
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
