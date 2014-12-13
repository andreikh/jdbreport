/*
 * DbrFileType.java
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

package jdbreport.design.model.xml;

import jdbreport.model.io.FileType;
import jdbreport.model.io.ReportReader;
import jdbreport.model.io.ReportWriter;

/**
 * @version 1.3 07.08.2009
 * @author Andrey Kholmanskih
 *
 */
public class DbrFileType implements FileType {

	private static final String[] extensions = { "jdbr" };
	private ReportReader reader;
	private ReportWriter writer;

	public String[] getExtensions() {
		return extensions;
	}

	public String getDescription() {
		return Messages.getString("DbrFileType.1");
	}

	public ReportReader getReader() {
		if (reader == null) {
			reader = new DbrReader();
		}
		return reader;
	}

	public ReportWriter getWriter() {
		if (writer == null) {
			writer = new DbrWriter(); 
		}
		return writer;
	}

	public String getContentType() {
		return "application/jdbr";
	}

	public int getOrder() {
		return 10;
	}

	public int compareTo(FileType o) {
		return getOrder() - o.getOrder();
	}
	
}
