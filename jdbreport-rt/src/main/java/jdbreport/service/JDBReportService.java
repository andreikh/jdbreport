/*
 * Created	02.02.2011
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2011-2012 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import jdbreport.ReportService;
import jdbreport.design.model.TemplateBook;
import jdbreport.grid.JReportGrid;
import jdbreport.model.JReportModel;
import jdbreport.model.ReportBook;
import jdbreport.model.io.FileType;
import jdbreport.model.io.LoadReportException;
import jdbreport.model.io.ReportWriter;
import jdbreport.model.io.SaveReportException;
import jdbreport.source.ArrayDataSet;
import jdbreport.source.IterableDataSet;
import jdbreport.source.IteratorDataSet;
import jdbreport.source.MapDataSet;
import jdbreport.source.ObjectDataSet;
import jdbreport.source.ReportDataSet;

/**
 * @author Andrey Kholmanskih
 * 
 * @version 2.1 20.06.2012
 */
public class JDBReportService implements ReportService {

	private JReportGrid grid;

	public byte[] getReportBuf(URL templateUrl,
			Map<String, Object> dataSetList, Map<Object, Object> vars,
			String format, Connection connection) throws LoadReportException {

		return getReportBuf(templateUrl, createDataSets(dataSetList), vars,
				format, connection);
	}

	public byte[] getReportBuf(URL templateUrl,
			Map<String, Object> dataSetList, Map<Object, Object> vars,
			String format) throws LoadReportException {

		return getReportBuf(templateUrl, createDataSets(dataSetList), vars,
				format, null);
	}

	public byte[] getReportBuf(URL templateUrl,
			Collection<ReportDataSet> dataSetList, Map<Object, Object> vars,
			String format, Connection connection) throws LoadReportException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writeReport(out, templateUrl, dataSetList, vars, format, connection);
		return out.toByteArray();
	}

	public ReportBook getReportBook(URL templateUrl,
			Map<String, Object> dataSetList, Map<Object, Object> vars)
			throws LoadReportException {
		return createReportBook(templateUrl, createDataSets(dataSetList), vars, null);
	}

	public ReportBook getReportBook(URL templateUrl,
			Collection<ReportDataSet> dataSetList, Map<Object, Object> vars)
			throws LoadReportException {
		return createReportBook(templateUrl, dataSetList, vars, null);
	}

	public void writeReport(OutputStream out, URL templateUrl,
			Map<String, Object> dataSetList, Map<Object, Object> vars,
			String format) throws LoadReportException {
		writeReport(out, templateUrl, createDataSets(dataSetList), vars, format, null);
	}

	public void writeReport(OutputStream out, URL templateUrl,
			Collection<ReportDataSet> dataSetList, Map<Object, Object> vars,
			String format, Connection connection) throws LoadReportException {
		FileType fileType = ReportBook.getFileTypeClass(format);
		if (fileType != null) {
			ReportBook book = createReportBook(templateUrl, dataSetList, vars, connection);
			convert(out, fileType, book);
		} else
			throw new LoadReportException("Unknown format");
	}

	protected void convert(OutputStream out, FileType fileType, ReportBook book)
			throws LoadReportException {
		ReportWriter writer = fileType.getWriter();
		try {
			writer.save(out, book);
		} catch (SaveReportException e) {
			throw new LoadReportException(e);
		}
	}

	public String getMimeType(String format) {
		FileType fileType = ReportBook.getFileTypeClass(format);
		if (fileType != null) {
			return fileType.getContentType();
		}
		return null;
	}

	public ReportBook createReportBook(URL templateUrl,
			Collection<ReportDataSet> dataSetList, Map<Object, Object> vars, Connection connection)
			throws LoadReportException {
		TemplateBook tbook = new TemplateBook();
		tbook.open(templateUrl);
		if (connection != null) {
			tbook.getDefaultSource().setConnection(connection);
		}
		if (vars != null) {
			for (Object key : vars.keySet())
				tbook.setVarValue(key, vars.get(key));
		}
		if (dataSetList != null) {
			for (ReportDataSet ds : dataSetList)
				tbook.addReportDataSet(ds);
		}
		if (grid == null) {
			try {
				grid = new JReportGrid(new JReportModel(tbook.getStyleList()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return tbook.createReportBook(grid);
	}

	protected Collection<ReportDataSet> createDataSets(
			Map<String, Object> dataSetList) {
		Collection<ReportDataSet> dsList = null;
		if (dataSetList != null && dataSetList.size() > 0) {
			dsList = new ArrayList<ReportDataSet>();
			for (String key : dataSetList.keySet()) {
				dsList.add(createDataSet(key, dataSetList.get(key)));
			}
		}
		return dsList;
	}

	public ReportDataSet createDataSet(String id, Object ds) {
		if (ds instanceof ReportDataSet) {
			return (ReportDataSet) ds;
		}
		if (ds instanceof Iterator) {
			return new IteratorDataSet(id, (Iterator<?>) ds);
		}
		if (ds instanceof Iterable) {
			return new IterableDataSet(id, (Iterable<?>) ds);
		}
		if (ds instanceof Map) {
			return new MapDataSet(id, (Map<String, Object>) ds);
		}
		if (ds.getClass().isArray()) {
			return new ArrayDataSet(id, (Object[]) ds);
		}
		return new ObjectDataSet(id, ds);
	}

	public byte[] convert(ReportBook book, String format)
			throws LoadReportException {
		FileType fileType = ReportBook.getFileTypeClass(format);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		if (fileType != null) {
			convert(out, fileType, book);
			return out.toByteArray();
		} else
			throw new LoadReportException("Unknown format");
	}

}
