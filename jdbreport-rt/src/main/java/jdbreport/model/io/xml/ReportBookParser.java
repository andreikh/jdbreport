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
package jdbreport.model.io.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.MessageFormat;

import jdbreport.model.ReportBook;
import jdbreport.model.io.ReportWriter;
import jdbreport.model.io.ResourceReader;
import jdbreport.model.io.ResourceWriter;
import jdbreport.model.io.SaveReportException;

import org.xml.sax.Attributes;

import jdbreport.util.xml.XMLCoder;
import jdbreport.util.xml.XMLParser;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class ReportBookParser extends ReportBookWriterParser implements
		ReportWriter {

	private ResourceWriter resourceWriter;
	protected ResourceReader resourceReader;
	private static final String[] extensions = { "xml" };

	public ReportBookParser(ResourceWriter rw) {
		super(null);
		this.resourceWriter = rw;
	}

	public ReportBookParser(JReportHandler reportHandler, ResourceReader rr) {
		super(reportHandler);
		this.resourceReader = rr;
	}

	public static ReportWriter createReportWriter(ResourceWriter rw) {
		return new ReportBookParser(rw);
	}

	public boolean startElement(String name, Attributes attributes) {
		if (name.equals("Styles")) {
			getHandler().pushHandler(
					new StyleReportParser(getDefaultReportHandler()));
			return true;
		}
		if (name.equals("Sheet")) {
			setCurrentModel(getReportBook().add());
			getHandler().pushHandler(createSheetHandler());
			return true;
		}
		if (name.equals("Options")) {
			if (attributes.getValue("showGrid") != null) {
				getReportBook().setShowGrid(
						Boolean.parseBoolean(attributes.getValue("showGrid")));
			}
			if (attributes.getValue("globalPageNumber") != null) {
				getReportBook().setGlobalPageNumber(
						Boolean.parseBoolean(attributes.getValue("globalPageNumber")));
			}
			return true;
		}
		return false;
	}

	/**
	 * @return handler for the parsing sheets
	 */
	protected XMLParser createSheetHandler() {
		return new DBReportParser(getDefaultReportHandler(), resourceReader);
	}


	public void save(OutputStream out, ReportBook reportBook)
			throws SaveReportException {
		PrintWriter fw = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(out, java.nio.charset.Charset
						.forName("UTF-8"))));
		save(fw, reportBook);
	}

	protected String getRootName() {
		return "DocReport";
	}

	public void save(Writer writer, ReportBook reportBook)
			throws SaveReportException {
		PrintWriter fw;
		if (writer instanceof PrintWriter)
			fw = (PrintWriter) writer;
		else
			fw = new PrintWriter(writer);
		fw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		String nameReport = "";
		if (reportBook.getReportCaption() != null) {
			nameReport = MessageFormat.format(" Name=\"{0}\" ", XMLCoder
					.replaceSpecChar(reportBook.getReportCaption()));
		}
		fw.println("<jdbreport Version=\"" + ReportBook.CURRENT_VERSION + "\""
				+ nameReport + ">");
		fw.println("<" + getRootName() + ">");
		fw.println("<Options ");
		if (!reportBook.isShowGrid()) {
			fw.println(" showGrid=\"" + reportBook.isShowGrid()
					+ "\" ");
		}
		if (!reportBook.isGlobalPageNumber()) {
			fw.println(" globalPageNumber=\"" + reportBook.isGlobalPageNumber()
					+ "\" ");
		}
		fw.println("/>");
		writeStyles(reportBook, fw);
		writeSheets(reportBook, fw);
		fw.println("</" + getRootName() + ">");
		fw.println("</jdbreport>");
		fw.flush();
	}

	protected void writeSheets(ReportBook reportBook, PrintWriter writer) throws SaveReportException {
		for (int sheetind = 0; sheetind < reportBook.size(); sheetind++) {
			writeSheet(writer, reportBook.getReportModel(sheetind));
		}
	}


	protected String getSheetName() {
		return "ReportGrid";
	}

	protected void writeStyles(ReportBook reportBook, PrintWriter fw) {
		fw.println("<Styles>");
        for (Object o : reportBook.getStyleList().keySet()) {
            StyleReportParser.save(fw, reportBook.getStyles(o));
        }
		fw.println("</Styles>");
	}


	protected CellParser createCellHandler() {
		return new CellParser(getDefaultReportHandler(), this);
	}

	public void save(File file, ReportBook reportBook)
			throws SaveReportException {
		try {
			file.createNewFile();
            try (FileOutputStream out = new FileOutputStream(file)) {
                save(out, reportBook);
            }
		} catch (IOException e) {
			throw new SaveReportException(e);
		}
	}

	public String[] getExtensions() {
		return extensions;
	}

	public String getDescription() {
		return "JDBReport Files";
	}

	public String write(String fileName, Object resource) throws SaveReportException {
		return resourceWriter.write(fileName, resource);
	}

}
