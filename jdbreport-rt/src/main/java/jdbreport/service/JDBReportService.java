/*
 * Created	02.02.2011
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2011-2016 Andrey Kholmanskih
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
package jdbreport.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jdbreport.ReportService;
import jdbreport.design.model.TemplateBook;
import jdbreport.grid.JReportGrid;
import jdbreport.view.model.JReportModel;
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
 * @version 3.1.3 13.10.2016
 */
public class JDBReportService implements ReportService {

    private static final Logger logger = Logger.getLogger(JDBReportService.class
            .getName());


    public byte[] getReportBuf(URL templateUrl, Map<String, Object> dataSetList, Map<Object, Object> vars,
                               String format) throws LoadReportException {
        return getReportBuf(templateUrl, dataSetList, vars, format, null);
    }

    public byte[] getReportBuf(URL templateUrl, Map<String, Object> dataSetList, Map<Object, Object> vars,
                               String format, Connection connection) throws LoadReportException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeReport(out, templateUrl, dataSetList, vars, format, connection);
        return out.toByteArray();
    }

    public ReportBook getReportBook(URL templateUrl, Map<String, Object> dataSetList, Map<Object, Object> vars)
            throws LoadReportException {
        return createReportBook(templateUrl, dataSetList, vars, null);
    }

    public void writeReport(OutputStream out, URL templateUrl, Map<String, Object> dataSetList,
                            Map<Object, Object> vars, String format) throws LoadReportException {
        writeReport(out, templateUrl, dataSetList, vars, format, null);
    }

    public void writeReport(OutputStream out, URL templateUrl, Map<String, Object> dataSetList, Map<Object, Object> vars,
                            String format, Connection connection) throws LoadReportException {
        FileType fileType = ReportBook.getFileTypeClass(format);
        if (fileType != null) {
            ReportBook book = createReportBook(templateUrl, dataSetList, vars, connection);
            convert(out, fileType, book);
        } else
            throw new LoadReportException("Unknown format");
    }

    protected void convert(OutputStream out, FileType fileType, ReportBook book) throws LoadReportException {
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

    public ReportBook createReportBook(URL templateUrl, Map<String, Object> dataSets, Map<Object, Object> vars,
                                       Connection connection) throws LoadReportException {
        TemplateBook tbook = createTemplateBook(templateUrl, dataSets, vars, connection);
        return createReportBook(tbook);
    }

    public TemplateBook createTemplateBook(URL templateUrl, Map<String, Object> dataSets,
                                           Map<Object, Object> vars, Connection connection) throws LoadReportException {
        if (templateUrl == null) throw new LoadReportException("URL is null");
        TemplateBook tbook = new TemplateBook();
        tbook.open(templateUrl);
        if (connection != null) {
            tbook.getDefaultSource().setConnection(connection);
        }
        if (vars != null) {
            for (Object key : vars.keySet())
                tbook.setVarValue(key, vars.get(key));
        }
        if (dataSets != null) {
            for (ReportDataSet ds : createDataSets(dataSets))
                tbook.addReportDataSet(ds);
        }
        return tbook;
    }

    public ReportBook createReportBook(TemplateBook tbook) {
        JReportGrid grid = null;
        try {
            grid = new JReportGrid(new JReportModel(tbook.getStyleList()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return tbook.createReportBook(grid);
    }

    protected Collection<ReportDataSet> createDataSets(
            Map<String, Object> dataSetList) {
        Collection<ReportDataSet> dsList = null;
        if (dataSetList != null && dataSetList.size() > 0) {
            dsList = new ArrayList<>();
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
            return new MapDataSet(id, (Map) ds);
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
