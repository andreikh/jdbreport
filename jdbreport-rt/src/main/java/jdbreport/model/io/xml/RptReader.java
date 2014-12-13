/*
 * RptReader.java
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
import jdbreport.util.Utils;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class RptReader implements ReportReader, ResourceReader {

	private Map<String, Object> resMap;

	public void load(InputStream in, ReportBook reportBook)
			throws LoadReportException {
		ZipInputStream zipStream;
		if (in instanceof ZipInputStream)
			zipStream = (ZipInputStream) in;
		else
			zipStream = new ZipInputStream(in);

		resMap = new HashMap<>();
		try {
			ZipEntry entry = zipStream.getNextEntry();
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
					resMap.put(Utils.extractFileName(entry.getName()), stream.toByteArray());
				}
				entry =  zipStream.getNextEntry();
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
