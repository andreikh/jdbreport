/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2009 Andrey Kholmanskih. All rights reserved.
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

import and.properties.XMLProperties;

/**
 * @version 1.2 02/01/09
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

	/**
	 * @return
	 */

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
