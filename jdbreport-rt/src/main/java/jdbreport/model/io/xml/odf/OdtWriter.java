/*
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2014 Andrey Kholmanskih
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
