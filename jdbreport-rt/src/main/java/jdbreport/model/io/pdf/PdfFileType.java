/*
 * PdfFileType.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2011-2014 Andrey Kholmanskih
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

package jdbreport.model.io.pdf;

import java.util.Collection;

import jdbreport.model.io.FileType;
import jdbreport.model.io.ReportReader;
import jdbreport.model.io.ReportWriter;

/**
 * @version 2.0 07.06.2011
 * @author Andrey Kholmanskih
 * 
 */
public abstract class PdfFileType implements FileType {

	private static final String[] EXTENSIONS = { "pdf" };
	public static final String FONT_PATHS = "fontPaths";
	public static final String DEFAULT_FONT = "defaultPdfFont";
	
	private ReportWriter writer;

	public String[] getExtensions() {
		return EXTENSIONS;
	}

	public String getDescription() {
		return Messages.getString("PdfFileType.0");
	}

	public ReportReader getReader() {
		return null;
	}

	public ReportWriter getWriter() {
		if (writer == null) {
			writer = createPdfWriter();
		}
		return writer;
	}


	public String getContentType() {
		return "application/pdf";
	}

	public int getOrder() {
		return 60;
	}

	public int compareTo(FileType o) {
		return getOrder() - o.getOrder();
	}

	public abstract ReportWriter createPdfWriter();
	

	public abstract void initFontMapper();
}
