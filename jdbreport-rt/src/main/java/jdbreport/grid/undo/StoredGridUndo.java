/*
 * StoredGridUndo.java
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import jdbreport.grid.JReportGrid;
import jdbreport.model.io.SaveReportException;

/**
 * @version 3.0 13.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public abstract class StoredGridUndo extends AbstractGridUndo {

	protected static final int MAX_SIZE = 1024 * 128;
	protected static final int MAX_CELLS = 4000;

	protected String fileName;
	protected String buffer;

	public StoredGridUndo(JReportGrid grid, String descr) {
		super(grid, descr);
	}

	/**
	 * @throws IOException
	 * @throws SaveReportException
	 */
	protected void saveReport() throws SaveReportException, IOException {
		buffer = null;
		GridParser writer = getGrid().createGridWriter();
		if (getGrid().getRowCount() * getGrid().getColumnCount() > MAX_CELLS) {
			writeToFile(writer);
		} else {
			StringWriter pw;
			pw = new StringWriter();
			writer.save(pw, getGrid().getReportModel());
			buffer = pw.getBuffer().toString();
			if (buffer.length() > MAX_SIZE)
				saveToFile();
		}
	}

	protected Writer createWriter(File file) throws UnsupportedEncodingException, FileNotFoundException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
	}
	
	protected Reader createReader(File file) throws UnsupportedEncodingException, FileNotFoundException {
		return new InputStreamReader(new FileInputStream(file), "UTF-8");
	}
	
	private void saveToFile() throws IOException {
		File file = File.createTempFile("jdbr", null);
		file.deleteOnExit();
		Writer writer = createWriter(file);
		writer.write(buffer);
		writer.close();
		buffer = null;
		fileName = file.getPath();
	}

	private void writeToFile(GridParser writer) throws IOException,
			SaveReportException {
		File file = File.createTempFile("jdbr", null);
		file.deleteOnExit();
		try (Writer pw = createWriter(file)) {
			writer.save(pw, getGrid().getReportModel());
			fileName = file.getPath();
		}
	}

	public void clear() {
		super.clear();
		if (fileName != null) {
			File file = new File(fileName);
			file.delete();
			fileName = null;
		}
		buffer = null;
	}

}
