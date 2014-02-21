/*
 * OdsReader.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2008 Andrey Kholmanskih. All rights reserved.
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
 * @version 1.1 03/09/08
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
		ZipInputStream zipStream = null;
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
		ArrayList<File> files = new ArrayList<File>();
		try {
			try {
				ZipEntry entry = (ZipEntry) zipStream.getNextEntry();
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
						OutputStream stream = new FileOutputStream(file);
						try {
							int l = 65536;
							byte[] buf = new byte[l];
							do {
								l = zipStream.read(buf);
								if (l > 0)
									stream.write(buf, 0, l);
							} while (l > 0);
						} finally {
							stream.close();
						}
					} else
						file.mkdirs();
					entry = (ZipEntry) zipStream.getNextEntry();
				}
				zipStream.close();
				OdsContentHandler handler = new OdsContentHandler(reportBook,
						path.getPath());
				reportBook.clear();
				String mime = null;
				for (int n = 0; n < files.size(); n++) {
					if ("mimetype".equals(files.get(n).getName())) { //$NON-NLS-1$
						FileReader reader = new FileReader(files.get(n));
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

				for (int n = 0; n < files.size(); n++) {
					if ("meta.xml".equals(files.get(n).getName())) { //$NON-NLS-1$
						parseFile(files.get(n), handler);
						break;
					}
				}
				for (int n = 0; n < files.size(); n++) {
					if ("styles.xml".equals(files.get(n).getName())) { //$NON-NLS-1$
						parseFile(files.get(n), handler);
						break;
					}
				}
				for (int n = 0; n < files.size(); n++) {
					if ("content.xml".equals(files.get(n).getName())) { //$NON-NLS-1$
						parseFile(files.get(n), handler);
						removeDoubleBorders(reportBook);
						break;
					}
				}
				for (int n = 0; n < files.size(); n++) {
					if ("settings.xml".equals(files.get(n).getName())) { //$NON-NLS-1$
						parseFile(files.get(n), handler);
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
		} catch (ParserConfigurationException e) {
			throw new LoadReportException(e);
		} catch (SAXException e) {
			throw new LoadReportException(e);
		} catch (IOException e) {
			throw new LoadReportException(e);
		}
	}

	void deletePath(File path) {
		if (path.isDirectory()) {
			File[] list = path.listFiles();
			for (int i = 0; i < list.length; i++) {
				if (list[i].isDirectory()) {
					deletePath(list[i]);
				}
				list[i].delete();
			}
		}
		path.delete();
	}

}
