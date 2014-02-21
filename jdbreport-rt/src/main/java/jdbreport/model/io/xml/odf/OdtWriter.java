/*
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2009 Andrey Kholmanskih. All rights reserved.
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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import jdbreport.model.io.ReportWriter;

/**
 * @version 1.3 10.05.2009
 * @author Andrey Kholmanskih
 * 
 */
public class OdtWriter extends OdsWriter {

	public OdtWriter() {
		super();
	}

	protected ReportWriter getStylesWriter() {
		return new OdtStylesWriter();
	}

	protected void writeManifest(OutputStream stream) {
		PrintWriter fw = new PrintWriter(new OutputStreamWriter(stream,
				java.nio.charset.Charset.forName("UTF-8"))); //$NON-NLS-1$
		try {
			fw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); //$NON-NLS-1$
			fw
					.println("<manifest:manifest xmlns:manifest=\"urn:oasis:names:tc:opendocument:xmlns:manifest:1.0\">"); //$NON-NLS-1$
			fw
					.println("<manifest:file-entry manifest:media-type=\"application/vnd.oasis.opendocument.text\" manifest:full-path=\"/\"/>"); //$NON-NLS-1$
			writeIconsManifest(fw);
			writeManifestFiles(fw);
			fw.println("</manifest:manifest>"); //$NON-NLS-1$
		} finally {
			fw.flush();
		}
	}

	protected void mimeWrite(OutputStream stream) {
		PrintWriter fw = new PrintWriter(new OutputStreamWriter(stream,
				java.nio.charset.Charset.forName("UTF-8"))); //$NON-NLS-1$
		fw.print("application/vnd.oasis.opendocument.text"); //$NON-NLS-1$
		fw.flush();
	}

	protected ReportWriter getContentWriter() {
		return new OdtContentWriter(this);
	}

}
