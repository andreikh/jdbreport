/*
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2010 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.grid.undo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import jdbreport.view.model.JReportModel;
import jdbreport.model.ReportModel;
import jdbreport.model.io.ResourceWriter;
import jdbreport.model.io.SaveReportException;
import jdbreport.model.io.xml.CellParser;
import jdbreport.model.io.xml.DBReportParser;
import jdbreport.model.io.xml.ReportBookWriterParser;

import org.xml.sax.Attributes;

import and.util.xml.XMLParser;

/**
 * @version 2.0 20.04.2010
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
		PrintWriter fw = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(out, java.nio.charset.Charset
						.forName("UTF-8"))));
		try {
			save(fw, model);
		} finally {
			fw.close();
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
		return new CellParser(getDefaultReportHandler(), (ResourceWriter)null);
	}

	public void save(File file, JReportModel model) throws SaveReportException {
		try {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			try {
				save(out, model);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			throw new SaveReportException(e);
		}
	}

}
