/*
 * JDBReport Generator
 * 
 * Copyright (C) 2009-2014 Andrey Kholmanskih
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
 * @version 1.0 19.12.2009
 * @author Andrey Kholmanskih
 * @since 2.0
 */
public interface ResourceWriter {

	/**
	 * Write of resources
	 * @param fileName file name
	 * @param resource resource object
	 * @return the filename
	 * @throws SaveReportException SaveReportException
	 * @since 2.0
	 */
	String write(String fileName, Object resource) throws SaveReportException;

}
