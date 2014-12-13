/*
 * BackupItem.java
 *
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
