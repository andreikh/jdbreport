/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2016 Andrey Kholmanskih
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
 * @version 3.1.3 23.10.2016
 *  
 * @author Andrey Kholmanskih
 *
 */
public class JDBReport {


	static {
		Utils.errorHandler = new DefaultErrorHandler();
	}
	
	/**
	 * Loads the report from file and shows ReportEditor window 
	 * 
	 * @param file report template file
	 * @return ReportEditor window
	 * @throws LoadReportException LoadReportException
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
	 * @throws LoadReportException LoadReportException
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
	 * @throws LoadReportException LoadReportException
	 */
	public static ReportEditor showReport(File file, Map<String, Object> dataSetList, Map<Object, Object> vars) throws LoadReportException {
		return showReport(file, dataSetList, vars, true);
	}
	

	/**
	 * Loads the report from url and shows ReportEditor window 
	 * 
	 * @param url report template url
	 * @return ReportEditor window
	 * @throws LoadReportException LoadReportException
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
	 * @throws LoadReportException LoadReportException
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
	 * @throws LoadReportException LoadReportException
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
	 * @throws LoadReportException LoadReportException
	 */
	public static ReportEditor showReport(File file, Map<String, Object> dataSetList,
										  Map<Object, Object> vars, boolean show) throws LoadReportException {
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
	 * @throws LoadReportException LoadReportException
	 */
	public static ReportEditor showReport(URL url, Map<String, Object> dataSetList,
										  Map<Object, Object> vars, boolean show) throws LoadReportException {
		JDBReportService reportService = new JDBReportService();

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
	
	public static ReportDialog showModalReport(Window owner, URL url, Map<String, Object> dataSetList,
											   Map<Object, Object> vars, boolean show) throws LoadReportException {
		JDBReportService reportService = new JDBReportService();
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
