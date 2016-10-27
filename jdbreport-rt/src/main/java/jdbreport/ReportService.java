/*
 * Created	02.02.2011
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2011-2016 Andrey Kholmanskih
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
package jdbreport;

import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.Map;

import jdbreport.model.ReportException;

/**
 * @author Andrey Kholmanskih
 *
 * @version	3.1.3 23.10.2016
 */
public interface ReportService {

	String getMimeType(String format);

	byte[] getReportBuf(URL templateUrl, Map<String, Object> dataSetList, Map<Object, Object> vars, String format)
			throws ReportException;

	byte[] getReportBuf(URL templateUrl, Map<String, Object> dataSetList, Map<Object, Object> vars, String format,
						GenerateProcessor generateProcessor)
			throws ReportException;

	byte[] getReportBuf(URL templateUrl, Map<String, Object> dataSetList, Map<Object, Object> vars, String format,
						Connection connection, GenerateProcessor generateProcessor) throws ReportException;

	void writeReport(OutputStream out, URL templateUrl, Map<String, Object> dataSetList, Map<Object, Object> vars,
					 String format, GenerateProcessor generateProcessor) throws ReportException;

	void writeReport(OutputStream out, URL templateUrl, Map<String, Object> dataSetList, Map<Object, Object> vars,
					 String format) throws ReportException;

}
