/*
 * BackupItem.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2011 Andrey Kholmanskih. All rights reserved.
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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import jdbreport.grid.JReportGrid;
import jdbreport.model.io.SaveReportException;
import jdbreport.util.Utils;

/**
 * @version 2.0 30.05.2011
 * @author Andrey Kholmanskih
 * 
 */
public class BackupItem extends StoredGridUndo {

	public BackupItem(JReportGrid grid, String descr) throws SaveReportException, IOException   {
			super(grid, descr);
			saveReport();
		}

	public UndoItem undo() {
		if (fileName == null && buffer == null) {
			return null;
		}
		try {
			String oldFileName = fileName;
			String oldBuffer = buffer;
			saveReport();
			Reader reader;
			File file = null;
			if (oldFileName != null) {
				file = new File(oldFileName);
				reader = createReader(file);
			} else {
				reader = new StringReader(oldBuffer);
			}
			getGrid().startUpdate();
			try {
				getGrid().getReportModel().getRowModel().removeRows();
				getGrid().getReportModel().setColumnCount(0);
				loadGrid(reader);
			} finally {
				getGrid().endUpdate();
				if (file != null) {
					file.delete();
				}
			}
			return super.undo();
		} catch (Exception e) {
			fileName = null;
			buffer = null;
			Utils.showError(e);
		}
		return null;
	}

	public void loadGrid(Reader reader) throws ParserConfigurationException,
			SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser;
		saxParser = factory.newSAXParser();
		org.xml.sax.helpers.DefaultHandler handler = getGrid()
				.createGridHandler(saxParser.getXMLReader());
		saxParser.parse(new InputSource(reader), handler);
	}

}
