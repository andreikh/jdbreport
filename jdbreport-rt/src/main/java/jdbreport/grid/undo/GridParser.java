/*
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2014 Andrey Kholmanskih
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
package jdbreport.grid.undo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import jdbreport.util.xml.XMLParser;
import jdbreport.view.model.JReportModel;
import jdbreport.model.ReportModel;
import jdbreport.model.io.SaveReportException;
import jdbreport.model.io.xml.CellParser;
import jdbreport.model.io.xml.DBReportParser;
import jdbreport.model.io.xml.ReportBookWriterParser;

import org.xml.sax.Attributes;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class GridParser extends ReportBookWriterParser {


	public GridParser() {
		super(null);
	}

	public GridParser(GridHandler reportHandler) {
		super(reportHandler);
	}

	public boolean startElement(String name, Attributes attributes) {
		if (name.equals("Sheet")) {
			getHandler().pushHandler(createSheetHandler());
			return true;
		}
		return false;
	}

	protected XMLParser createSheetHandler() {
		return new DBReportParser(getDefaultReportHandler(), null);
	}


	public void save(OutputStream out, JReportModel model)
			throws SaveReportException {
		try (PrintWriter fw = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(out, java.nio.charset.Charset
						.forName("UTF-8"))))) {
			save(fw, model);
		}
	}

	protected String getRootName() {
		return "reportgrid";
	}

	public void save(Writer writer, ReportModel model)
			throws SaveReportException {
		PrintWriter fw;
		if (writer instanceof PrintWriter)
			fw = (PrintWriter) writer;
		else
			fw = new PrintWriter(writer);
		fw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		fw.println("<jdbreport>");
		fw.println("<" + getRootName() + ">");
		writeSheet(fw, model);
		fw.println("</" + getRootName() + ">");
		fw.println("</jdbreport>");
	}

	protected String getSheetName() {
		return "ReportGrid";
	}


	protected CellParser createCellHandler() {
		return new CellParser(getDefaultReportHandler(), null);
	}

	public void save(File file, JReportModel model) throws SaveReportException {
		try {
			file.createNewFile();
			try (FileOutputStream out = new FileOutputStream(file)) {
				save(out, model);
			}
		} catch (IOException e) {
			throw new SaveReportException(e);
		}
	}

}
