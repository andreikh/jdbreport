/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2011 Andrey Kholmanskih. All rights reserved.
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
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
 */
package jdbreport;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import jdbreport.model.ReportBook;
import jdbreport.model.io.LoadReportException;
import jdbreport.service.JDBReportService;
import jdbreport.util.Utils;
import jdbreport.view.DefaultErrorHandler;
import jdbreport.view.ReportDialog;
import jdbreport.view.ReportEditor;


/**
 * @version 3.0 12.12.2014
 *  
 * @author Andrey Kholmanskih
 *
 */
public class JDBReport {

	private static JDBReportService reportService = new JDBReportService();
	
	static {
		Utils.errorHandler = new DefaultErrorHandler();
	}
	
	/**
	 * Loads the report from file and shows ReportEditor window 
	 * 
	 * @param file report template file
	 * @return ReportEditor window
	 * @throws LoadReportException
	 */
	public static ReportEditor showReport(File file) throws LoadReportException {
		return showReport(file, null, null, true);
	}
	
	/**
	 * Loads the report from file and shows ReportEditor window 
	 * 
	 * @param file report template file
	 * @param dataSetList the list of DataSets
	 * @return ReportEditor window
	 * @throws LoadReportException
	 */
	public static ReportEditor showReport(File file, Map<String, Object> dataSetList) throws LoadReportException {
		return showReport(file, dataSetList, null, true);
	}
	
	/**
	 * Loads the report from file and shows ReportEditor window 
	 * 
	 * @param file report template file
	 * @param dataSetList the list of DataSets
	 * @param vars the list of variables
	 * @return ReportEditor window
	 * @throws LoadReportException
	 */
	public static ReportEditor showReport(File file, Map<String, Object> dataSetList, Map<Object, Object> vars) throws LoadReportException {
		return showReport(file, dataSetList, vars, true);
	}
	

	/**
	 * Loads the report from url and shows ReportEditor window 
	 * 
	 * @param url report template url
	 * @return ReportEditor window
	 * @throws LoadReportException
	 */
	public static ReportEditor showReport(URL url) throws LoadReportException {
		return showReport(url, null, null, true);
	}
	
	/**
	 * Loads the report from url and shows ReportEditor window 
	 * 
	 * @param url  report template url
	 * @param dataSetList  the list of DataSets
	 * @return  ReportEditor window
	 * @throws LoadReportException
	 */
	public static ReportEditor showReport(URL url, Map<String, Object> dataSetList) throws LoadReportException {
		return showReport(url, dataSetList, null, true);
	}
	
	/**
	 * Loads report from url and shows ReportEditor window 
	 * @param url - report template url
	 * @param dataSetList  the list of DataSets
	 * @param vars  var list
	 * @return - ReportEditor window
	 * @throws LoadReportException
	 */
	public static ReportEditor showReport(URL url, Map<String, Object> dataSetList, Map<Object, Object> vars) throws LoadReportException {
		return showReport(url, dataSetList, vars, true);
	}
	
	/**
	 * Loads the report from file
	 * @param file  report template file
	 * @param dataSetList the list of DataSets
	 * @param vars  the list of variables
	 * @param show  if true set visible Report window
	 * @return  ReportEditor window
	 * @throws LoadReportException
	 */
	public static ReportEditor showReport(File file, Map<String, Object> dataSetList, Map<Object, Object> vars, boolean show) throws LoadReportException {
		try {
			return showReport(file.toURI().toURL(), dataSetList, vars);
		} catch (MalformedURLException e) {
			throw new LoadReportException(e);
		}
	}
	
	/**
	 * Loads the report from url
	 * @param url  report template url
	 * @param dataSetList the list of DataSets
	 * @param vars the list of variables
	 * @param show  if true set visible Report window
	 * @return  ReportEditor window 
	 * @throws LoadReportException
	 */
	public static ReportEditor showReport(URL url, Map<String, Object> dataSetList, Map<Object, Object> vars, boolean show) throws LoadReportException {
		ReportBook book = reportService.getReportBook(url, dataSetList, vars);
		ReportEditor re = new ReportEditor();
		re.setReportBook(book);
		if (show)
			re.setVisible(true);
		return re;
	}
	
	/**
	 * Shows empty ReportEditor window
	 * @return  ReportEditor window
	 */
	public static ReportEditor viewReport() {
		return viewReport(null);
	}

	/**
	 * Shows ReportEditor window with report from file
	 * @param file  report file 
	 * @return  ReportEditor window
	 */
	public static ReportEditor viewReport(File file) {
		ReportEditor re = new ReportEditor();
		if (file != null)
			re.open(file);
		re.setVisible(true);
		return re;
	}
	
	public static ReportDialog showModalReport(Window owner, URL url, Map<String, Object> dataSetList, Map<Object, Object> vars, boolean show) throws LoadReportException {
		ReportBook book = reportService.getReportBook(url, dataSetList, vars);
		ReportDialog re;
		if (owner instanceof Dialog) {
			re = new ReportDialog((Dialog)owner);
		} else {
			re = new ReportDialog((Frame)owner);
		}
		re.setReportBook(book);
		if (show)
			re.setVisible(true);
		return re;
	}

}
