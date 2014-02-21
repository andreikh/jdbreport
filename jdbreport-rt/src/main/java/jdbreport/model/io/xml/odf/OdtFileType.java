/*
 * OdsFileType.java
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

package jdbreport.model.io.xml.odf;

import jdbreport.model.io.FileType;
import jdbreport.model.io.ReportReader;
import jdbreport.model.io.ReportWriter;

/**
 * @version 1.3 07.08.2009
 * @author Andrey Kholmanskih
 * 
 */
public class OdtFileType implements FileType {

	private final static String[] fileExtensions = { "odt" }; //$NON-NLS-1$
	private ReportWriter writer;

	public String[] getExtensions() {
		return fileExtensions;
	}

	public String getDescription() {
		return Messages.getString("OdtFileType.descr"); //$NON-NLS-1$
	}

	public ReportReader getReader() {
		return null;
	}

	public ReportWriter getWriter() {
		if (writer == null) {
			writer = new OdtWriter();
		}
		return writer;
	}

	public String getContentType() {
		return "application/vnd.oasis.opendocument.text"; //$NON-NLS-1$
	}

	public int getOrder() {
		return 30;
	}

	public int compareTo(FileType o) {
		return getOrder() - o.getOrder();
	}

}
