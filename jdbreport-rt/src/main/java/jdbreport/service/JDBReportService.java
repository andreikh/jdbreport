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

import jdbreport.GenerateProcessor;
import jdbreport.ReportService;
import jdbreport.design.model.TemplateBook;
import jdbreport.grid.JReportGrid;
import jdbreport.model.ReportException;
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
 * @version 3.1.3 15.10.2016
 */
public class JDBReportService implements ReportService {

    private static final Logger logger = Logger.getLogger(JDBReportService.class
            .getName());

    @Override
    public byte[] getReportBuf(URL templateUrl, Map<String, Object> dataSetList, Map<Object, Object> vars,
                               String format) throws ReportException {
        return getReportBuf(templateUrl, dataSetList, vars, format, null, null);
    }

    public byte[] getReportBuf(URL templateUrl, Map<String, Object> dataSetList, Map<Object, Object> vars,
                               String format, GenerateProcessor generateProcessor) throws LoadReportException {
        return getReportBuf(templateUrl, dataSetList, vars, format, null, generateProcessor);
    }

    public byte[] getReportBuf(URL templateUrl, Map<String, Object> dataSetList, Map<Object, Object> vars,
                               String format, Connection connection, GenerateProcessor generateProcessor)
            throws LoadReportException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeReport(out, templateUrl, dataSetList, vars, format, connection, generateProcessor);
        return out.toByteArray();
    }

    public ReportBook getReportBook(URL templateUrl, Map<String, Object> dataSetList, Map<Object, Object> vars)
            throws LoadReportException {
        return createReportBook(templateUrl, dataSetList, vars, null, null);
    }

    public void writeReport(OutputStream out, URL templateUrl, Map<String, Object> dataSetList,
                            Map<Object, Object> vars, String format, GenerateProcessor generateProcessor)
            throws LoadReportException {
        writeReport(out, templateUrl, dataSetList, vars, format, null, generateProcessor);
    }

    @Override
    public void writeReport(OutputStream out, URL templateUrl, Map<String, Object> dataSetList,
                            Map<Object, Object> vars, String format) throws ReportException {
        writeReport(out, templateUrl, dataSetList, vars, format, null, null);
    }

    public void writeReport(OutputStream out, URL templateUrl, Map<String, Object> dataSetList, Map<Object, Object> vars,
                            String format, Connection connection, GenerateProcessor generateProcessor)
            throws LoadReportException {
        FileType fileType = ReportBook.getFileTypeClass(format);
        if (fileType != null) {
            ReportBook book = createReportBook(templateUrl, dataSetList, vars, connection, generateProcessor);
            convert(out, fileType, book);
        } else
            throw new LoadReportException("Unknown format");
    }

    public String getMimeType(String format) {
        FileType fileType = ReportBook.getFileTypeClass(format);
        if (fileType != null) {
            return fileType.getContentType();
        }
        return null;
    }


    protected ReportBook createReportBook(URL templateUrl, Map<String, Object> dataSets, Map<Object, Object> vars,
                                       Connection connection, GenerateProcessor generateProcessor)
            throws LoadReportException {
        TemplateBook tbook = createTemplateBook(templateUrl, dataSets, vars, connection);
        if (generateProcessor != null)
            generateProcessor.beforeGenerate(tbook);
        ReportBook book = createReportBook(tbook);
        if (generateProcessor != null)
            generateProcessor.afterGenerate(book);
        return book;
    }

    protected TemplateBook createTemplateBook(URL templateUrl, Map<String, Object> dataSets,
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

    protected ReportBook createReportBook(TemplateBook tbook) {
        JReportGrid grid = null;
        try {
            grid = new JReportGrid(new JReportModel(tbook.getStyleList()));
        } catch (Exception e) {
            logger.log(Level.FINE, "Failed create grid " + e.getMessage());
        }

        return tbook.createReportBook(grid);
    }

    protected ReportDataSet createDataSet(String id, Object ds) {
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

    protected byte[] convert(ReportBook book, String format)
            throws LoadReportException {
        FileType fileType = ReportBook.getFileTypeClass(format);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (fileType != null) {
            convert(out, fileType, book);
            return out.toByteArray();
        } else
            throw new LoadReportException("Unknown format");
    }

    private Collection<ReportDataSet> createDataSets(
            Map<String, Object> dataSetList) {
        Collection<ReportDataSet> dsList = new ArrayList<>();
        if (dataSetList != null && dataSetList.size() > 0) {
            for (String key : dataSetList.keySet()) {
                dsList.add(createDataSet(key, dataSetList.get(key)));
            }
        }
        return dsList;
    }

    private void convert(OutputStream out, FileType fileType, ReportBook book) throws LoadReportException {
        ReportWriter writer = fileType.getWriter();
        try {
            writer.save(out, book);
        } catch (SaveReportException e) {
            throw new LoadReportException(e);
        }
    }

}
