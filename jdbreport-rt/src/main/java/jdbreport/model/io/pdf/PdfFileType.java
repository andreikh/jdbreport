/*
 * PdfFileType.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2011 Andrey Kholmanskih. All rights reserved.
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

	private static final String[] EXTENSIONS = { "pdf" };  //$NON-NLS-1$
	public static final String FONT_PATHS = "fontPaths"; //$NON-NLS-1$
	public static final String DEFAULT_FONT = "defaultPdfFont"; //$NON-NLS-1$
	
	private ReportWriter writer;

	public String[] getExtensions() {
		return EXTENSIONS;
	}

	public String getDescription() {
		return Messages.getString("PdfFileType.0"); //$NON-NLS-1$
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
		return "application/pdf"; //$NON-NLS-1$
	}

	public int getOrder() {
		return 60;
	}

	public int compareTo(FileType o) {
		return getOrder() - o.getOrder();
	}

	public abstract ReportWriter createPdfWriter();
	
	public abstract Collection<String> getFontPaths();

	public abstract String getDefaultFont();

	public abstract void setFontPaths(Collection<String> fontPaths);

	public abstract void setDefaultFont(String font);

	public abstract void initFontMapper();
}
