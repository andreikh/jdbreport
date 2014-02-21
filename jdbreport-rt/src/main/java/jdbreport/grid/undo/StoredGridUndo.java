/*
 * StoredGridUndo.java
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
 * @version 2.0 30.05.2011
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
			StringWriter pw = null;
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
		Writer pw = createWriter(file);
		try {
			writer.save(pw, getGrid().getReportModel());
			fileName = file.getPath();
		} finally {
			pw.close();
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
