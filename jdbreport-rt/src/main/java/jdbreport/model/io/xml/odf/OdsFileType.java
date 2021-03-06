/*
 * OdsFileType.java
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

import jdbreport.model.io.FileType;
import jdbreport.model.io.ReportReader;
import jdbreport.model.io.ReportWriter;

/**
 * @version 1.3 07.08.2009
 * @author Andrey Kholmanskih
 * 
 */
public class OdsFileType implements FileType {

	private final static String[] fileExtensions = { "ods" };
	private ReportReader reader;
	private ReportWriter writer;

	public String[] getExtensions() {
		return fileExtensions;
	}

	public String getDescription() {
		return Messages.getString("OdsFileType.1");
	}

	public ReportReader getReader() {
		if (reader == null) {
			reader = new OdsReader();
		}
		return reader;
	}

	public ReportWriter getWriter() {
		if (writer == null) {
			writer = new OdsWriter();
		}
		return writer;
	}

	public String getContentType() {
		return "application/vnd.oasis.opendocument.spreadsheet";
	}

	public int getOrder() {
		return 20;
	}

	public int compareTo(FileType o) {
		return getOrder() - o.getOrder();
	}

}
