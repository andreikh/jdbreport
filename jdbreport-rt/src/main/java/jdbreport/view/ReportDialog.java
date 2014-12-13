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
import java.awt.Dialog;
import java.awt.Frame;
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
public class ReportDialog extends JDialog implements 
		TargetGrid, HeighCalculator {


	private static final long serialVersionUID = 1L;



	private ReportEditorPane reportPane = null;



	private int closeOperation = WindowConstants.DISPOSE_ON_CLOSE;

	
	protected XMLProperties properties;
	
	public ReportDialog(Dialog owner) {
		super(owner, true);
		getReportPane().readProperties();
		initialize();
		getReportPane().initProperties();
	}

	public ReportDialog(Dialog owner, File file) {
		this(owner);
		try {
			getReportBook().open(file);
		} catch (LoadReportException e) {
			Utils.showError(e);
		}
	}

	public ReportDialog(Dialog owner, String fileName) {
		this(owner);
		try {
			getReportBook().open(fileName);
		} catch (LoadReportException e) {
			Utils.showError(e);
		}
	}

	public ReportDialog(Frame owner) {
		super(owner, true);
		getReportPane().readProperties();
		initialize();
		getReportPane().initProperties();
	}

	public ReportDialog(Frame owner, File file) {
		this(owner);
		try {
			getReportBook().open(file);
		} catch (LoadReportException e) {
			Utils.showError(e);
		}
	}

	public ReportDialog(Frame owner, String fileName) {
		this(owner);
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

		});
		this.setIconImage(new ImageIcon(getClass().getResource(
				"/jdbreport/resources/logo.png")).getImage()); //$NON-NLS-1$
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
		setJMenuBar(getReportPane().createJMenuBar());
	}

	private void exitForm(WindowEvent evt) {
		if (!getReportPane().saveQuestion())
			return;
		setDefaultCloseOperation(getCloseOperation());
		getReportPane().clear();
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
		if (reportPane == null) {
			reportPane = createClientPanel();
		}
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


	

}
