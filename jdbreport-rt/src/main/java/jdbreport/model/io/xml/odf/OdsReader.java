/*
 * OdsReader.java
 *
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

package jdbreport.model.io.xml.odf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import jdbreport.model.ReportBook;
import jdbreport.model.ReportModel;
import jdbreport.model.io.LoadReportException;
import jdbreport.model.io.ReportReader;

import org.xml.sax.SAXException;

/**
 * @version 3.0 22.02.2014
 * @author Andrey Kholmanskih
 * 
 */
public class OdsReader implements ReportReader {

	public void load(Reader reader, ReportBook reportBook)
			throws LoadReportException {
		throw new LoadReportException(Messages.getString("OdsReader.0")); //$NON-NLS-1$
	}

	public void load(InputStream in, ReportBook reportBook)
			throws LoadReportException {
		ZipInputStream zipStream;
		if (in instanceof ZipInputStream)
			zipStream = (ZipInputStream) in;
		else
			zipStream = new ZipInputStream(in);

		String tmpPath = null;
		try {
			File file = File.createTempFile("dbr", "1"); //$NON-NLS-1$ //$NON-NLS-2$
			tmpPath = file.getPath();
			file.delete();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (tmpPath == null || tmpPath.trim().length() == 0)
			tmpPath = "~jdbr.tmp"; //$NON-NLS-1$
		File path = new File(tmpPath);
		if (!path.mkdir())
			throw new LoadReportException(Messages.getString("OdsReader.4")); //$NON-NLS-1$
		ArrayList<File> files = new ArrayList<>();
		try {
			try {
				ZipEntry entry =  zipStream.getNextEntry();
				if (entry == null)
					throw new LoadReportException(Messages
							.getString("OdsReader.5")); //$NON-NLS-1$
				while (entry != null) {
					File file = new File(path.getAbsolutePath() + '/'
							+ entry.getName());
					files.add(file);
					if (!entry.isDirectory()) {
						try {
							file.createNewFile();
						} catch (IOException e) {
							file.getParentFile().mkdirs();
						}
                        try (OutputStream stream = new FileOutputStream(file)) {
                            int l = 65536;
                            byte[] buf = new byte[l];
                            do {
                                l = zipStream.read(buf);
                                if (l > 0)
                                    stream.write(buf, 0, l);
                            } while (l > 0);
                        }
					} else
						file.mkdirs();
					entry = zipStream.getNextEntry();
				}
				zipStream.close();
				OdsContentHandler handler = new OdsContentHandler(reportBook,
						path.getPath());
				reportBook.clear();
				String mime = null;
                for (File file1 : files) {
                    if ("mimetype".equals(file1.getName())) { //$NON-NLS-1$
                        FileReader reader = new FileReader(file1);
                        char[] text = new char[1024];
                        int l = reader.read(text);
                        mime = new String(text, 0, l);
                        if (!"application/vnd.oasis.opendocument.spreadsheet" //$NON-NLS-1$
                                .equals(mime)) {
                            throw new LoadReportException(Messages
                                    .getString("OdsReader.unknow") //$NON-NLS-1$
                                    + mime);
                        }
                        break;
                    }
                }
				if (mime == null)
					throw new LoadReportException(Messages
							.getString("OdsReader.unknow2")); //$NON-NLS-1$

                for (File file : files) {
                    if ("meta.xml".equals(file.getName())) { //$NON-NLS-1$
                        parseFile(file, handler);
                        break;
                    }
                }
                for (File file : files) {
                    if ("styles.xml".equals(file.getName())) { //$NON-NLS-1$
                        parseFile(file, handler);
                        break;
                    }
                }
                for (File file : files) {
                    if ("content.xml".equals(file.getName())) { //$NON-NLS-1$
                        parseFile(file, handler);
                        removeDoubleBorders(reportBook);
                        break;
                    }
                }
                for (File file : files) {
                    if ("settings.xml".equals(file.getName())) { //$NON-NLS-1$
                        parseFile(file, handler);
                        break;
                    }
                }

			} catch (IOException e) {
				throw new LoadReportException(e);
			}
		} finally {
			deletePath(path);
		}
	}

	private void removeDoubleBorders(ReportBook reportBook) {
		for (ReportModel model : reportBook) {
			model.getRowModel().endUpdate();
			reportBook.removeDoubleBorders(model);
		}
	}

	private void parseFile(File file, OdsContentHandler handler)
			throws LoadReportException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = factory.newSAXParser();
			handler.setReader(saxParser.getXMLReader());
			FileInputStream stream = new FileInputStream(file);
			saxParser.parse(stream, handler);
		} catch (ParserConfigurationException | IOException | SAXException e) {
			throw new LoadReportException(e);
		}
    }

	void deletePath(File path) {
		if (path.isDirectory()) {
			File[] list = path.listFiles();
            if (list != null)
            for (File aList : list) {
                if (aList.isDirectory()) {
                    deletePath(aList);
                }
                aList.delete();
            }
		}
		path.delete();
	}

}
