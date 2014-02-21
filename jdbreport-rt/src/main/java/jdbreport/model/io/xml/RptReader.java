/*
 * RptReader.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2009 Andrey Kholmanskih. All rights reserved.
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

package jdbreport.model.io.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jdbreport.model.ReportBook;
import jdbreport.model.io.LoadReportException;
import jdbreport.model.io.ReportReader;
import jdbreport.model.io.ResourceReader;

/**
 * @version 2.0 20.12.2009
 * @author Andrey Kholmanskih
 * 
 */
public class RptReader implements ReportReader, ResourceReader {

	private Map<String, Object> resMap;

	public void load(InputStream in, ReportBook reportBook)
			throws LoadReportException {
		ZipInputStream zipStream = null;
		if (in instanceof ZipInputStream)
			zipStream = (ZipInputStream) in;
		else
			zipStream = new ZipInputStream(in);

		resMap = new HashMap<String, Object>();
		try {
			ZipEntry entry = (ZipEntry) zipStream.getNextEntry();
			if (entry == null)
				throw new LoadReportException(Messages.getString("RptReader.0")); //$NON-NLS-1$
			while (entry != null) {
				if (!entry.isDirectory()) {
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
						int l = 65536;
						byte[] buf = new byte[l];
						do {
							l = zipStream.read(buf);
							if (l > 0)
								stream.write(buf, 0, l);
						} while (l > 0);
					resMap.put(and.util.Utilities.extractFileName(entry.getName()), stream.toByteArray());
				}
				entry = (ZipEntry) zipStream.getNextEntry();
			}
			
			ReportReader reader = createXMLReader();
			InputStream is = getResource("repgrid.xml");
			if (is == null) {
				for (String key : resMap.keySet()) {
					is = getResource(key);
					break;
				}
			}
			reader.load(is, reportBook);
		} catch (IOException e) {
			throw new LoadReportException(e);
		}
		resMap = null;
	}

	public void load(Reader reader, ReportBook reportBook)
			throws LoadReportException {
		throw new LoadReportException(Messages.getString("RptReader.1")); //$NON-NLS-1$
	}

	public InputStream getResource(String id) {
		byte[] buf = (byte[]) resMap.get(id);
		if (buf != null) {
			return new ByteArrayInputStream(buf);
		}
		return null;
	}

	protected ReportReader createXMLReader() {
		return new XMLReportReader(this);
	}
}
