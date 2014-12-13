/*
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
package jdbreport.model.io;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

import jdbreport.model.ReportBook;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public interface ReportWriter extends ResourceWriter {

	public void save(OutputStream out, ReportBook reportBook)
			throws SaveReportException;

	public void save(Writer writer, ReportBook reportBook)
			throws SaveReportException;

	public void save(File file, ReportBook reportBook)
			throws SaveReportException;
	
	
}
