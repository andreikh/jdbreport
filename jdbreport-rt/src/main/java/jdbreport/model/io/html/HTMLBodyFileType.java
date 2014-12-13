/*
 * Created 02.04.2011
 * 
 * JDBReport Generator
 * 
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
