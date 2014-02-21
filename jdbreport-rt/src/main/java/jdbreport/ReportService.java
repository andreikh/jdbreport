/*
 * Created	02.02.2011
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2011-2012 Andrey Kholmanskih. All rights reserved.
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
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
 */
package jdbreport;

import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.Map;

import jdbreport.model.ReportException;

/**
 * @author Andrey Kholmanskih
 *
 * @version	2.1 20.06.2012
 */
public interface ReportService {

	public String getMimeType(String format);
	
	byte[] getReportBuf(URL templateUrl, Map<String, Object> dataSetList, Map<Object, Object> vars, String format) throws ReportException;

	byte[] getReportBuf(URL templateUrl, Map<String, Object> dataSetList, Map<Object, Object> vars, String format, Connection connection) throws ReportException;

	void writeReport(OutputStream out, URL templateUrl, Map<String, Object> dataSetList, Map<Object, Object> vars, String format) throws ReportException;


}
