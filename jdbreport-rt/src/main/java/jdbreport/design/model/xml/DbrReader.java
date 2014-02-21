/*
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
package jdbreport.design.model.xml;


import jdbreport.model.io.ReportReader;
import jdbreport.model.io.xml.RptReader;

/**
 * @version 2.0 20.12.2009
 * @author Andrey Kholmanskih
 * 
 */
public class DbrReader extends RptReader {

	public DbrReader() {
		super();
	}


	protected ReportReader createXMLReader() {
		return new TemplateXMLReader(this);
	}

}
