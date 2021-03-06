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

/**
 * @version 1.3 07.08.2009
 * @author Andrey Kholmanskih
 * 
 */
public interface FileType extends Comparable<FileType>{

	String[] getExtensions();

	String getDescription();

	ReportReader getReader();

	ReportWriter getWriter();

	/**
	 * 
	 * @return MIME type
	 * @since 1.3
	 */
	String getContentType();
	
	/**
	 * 
	 * @return Sorting number in the FileChooser
	 * @since 1.3
	 */
	int getOrder();
}
