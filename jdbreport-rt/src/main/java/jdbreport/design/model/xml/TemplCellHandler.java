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
package jdbreport.design.model.xml;

import java.io.PrintWriter;

import jdbreport.design.model.CellObject;
import jdbreport.model.Cell;
import jdbreport.model.ReportModel;
import jdbreport.model.io.ResourceReader;
import jdbreport.model.io.ResourceWriter;
import jdbreport.model.io.SaveReportException;
import jdbreport.model.io.xml.CellParser;
import jdbreport.model.io.xml.DefaultReaderHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Andrey Kholmanskih
 * @version 3.0 13.12.2014
 */
public class TemplCellHandler extends CellParser {

    protected static final String EXTPROP = "extProp";

    protected static final String REPLACE = "replace";

    protected boolean inExtProp;

    protected CellObject cellObject;

    protected static final String[] AGR_FUNC_NAME = {"None", "Sum", "Max",
            "Min", "Avg"};

    public TemplCellHandler(DefaultReaderHandler reportHandler, ResourceWriter resWriter) {
        super(reportHandler, resWriter);
    }

    public TemplCellHandler(DefaultReaderHandler reportHandler, Cell cell, ResourceReader rr) {
        super(reportHandler, cell, rr);
        assert (cell instanceof CellObject);
        cellObject = (CellObject) cell;
    }

    @Override
    public boolean startElement(String name, Attributes attributes)
            throws SAXException {
        if (inExtProp) {
            if (name.equals("prop")) {
                String s;
                s = attributes.getValue("tableID");
                if (s != null && s.length() > 0) {
                    cellObject.setDataSetId(s);
                }
                s = attributes.getValue("type");
                if (s != null && s.length() > 0) {
                    cellObject.setType(Integer.parseInt(s));
                }
                return false;
            }
            return name.equals("agr-func") || name.equals("norep") || name.equals("function-name")
                    || name.equals(REPLACE);
        }
        if (name.equals(EXTPROP)) {
            inExtProp = true;
            return true;
        }
        return super.startElement(name, attributes);
    }

    @Override
    public void endElement(String name, StringBuffer value) {

        if (name.equals(EXTPROP)) {
            inExtProp = false;
            return;
        }
        if (inExtProp) {
            if (name.equals("agr-func")) {
                for (int i = 1; i < AGR_FUNC_NAME.length; i++) {
                    if (AGR_FUNC_NAME[i].equals(value.toString()))
                        cellObject.setTotalFunction(i);
                }
                return;
            }
            if (name.equals("norep")) {
                cellObject
                        .setNotRepeate(Boolean.parseBoolean(value.toString()));
                return;
            }
            if (name.equals("function-name")) {
                cellObject.setFunctionName(value.toString());
                return;
            }
            if (name.equals(REPLACE)) {
                cellObject.setReplacement(Boolean.parseBoolean(value.toString()));
                return;
            }
        }
        super.endElement(name, value);
    }

    @Override
    protected void writeElements(PrintWriter writer, ReportModel model,
                                 Cell cell, int row, int column) throws SaveReportException {
        super.writeElements(writer, model, cell, row, column);
        writeCellObject(writer, (CellObject) cell);
    }

    protected void writeCellObject(PrintWriter writer, CellObject cell) {
        if (cell.getType() <= 1 && cell.getFunctionName() == null
                && cell.getTotalFunction() == CellObject.AF_NONE
                && !cell.isNotRepeate() && !cell.isReplacement())
            return;
        writer.println("<" + EXTPROP + ">");
        writeExtProperty(writer, cell);
        writer.println("</" + EXTPROP + ">");
    }

    protected void writeExtProperty(PrintWriter writer, CellObject cell) {
        StringBuilder prop = null;
        if (cell.getType() > 1) {
            prop = new StringBuilder("type=\"");
            prop.append("").append(cell.getType());
            prop.append('"');
            if (cell.getDataSetId() != null) {
                prop.append(" tableID=\"");
                prop.append(cell.getDataSetId());
                prop.append('"');
            }
        }
        if (prop != null) {
            writer.println("<prop " + prop + "/>");
        }
        if (cell.getTotalFunction() != CellObject.AF_NONE) {
            writer.println("<agr-func>"
                    + AGR_FUNC_NAME[cell.getTotalFunction()] + "</agr-func>");
        }
        if (cell.isNotRepeate()) {
            writer.println("<norep>true</norep>");
        }
        if (cell.getFunctionName() != null) {
            writer.println("<function-name>" + cell.getFunctionName()
                    + "</function-name>");
        }
        if (cell.isReplacement()) {
            writer.println("<" + REPLACE + ">true</" + REPLACE + ">");
        }
    }

}
