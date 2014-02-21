/*
 * Created 02.04.2011
 * 
 * JDBReport Generator
 * 
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
package jdbreport.model.io.html;

import jdbreport.model.io.ReportWriter;

/**
 * @author Andrey Kholmanskih
 *
 * @version 1.0 02.04.2011
 */
public class HTMLBodyFileType extends HTMLFileType {

	public HTMLBodyFileType() {
	}

	public String[] getExtensions() {
		return null;
	}

	public ReportWriter getWriter() {
		if (writer == null) {
			writer = new HTMLBodyWriter();
		}
		return writer;
	}

}
