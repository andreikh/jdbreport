/*
 * StringMetrics.java
 *
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

package jdbreport.model;

/**
 * @version 2.0 14.03.2011
 * @author Andrey Kholmanskih
 * 
 */
public interface StringMetrics {

	void setStyle(CellStyle style);

	int charsWidth(char[] data, int off, int len);

	char[] toViewCharArray(Cell cell);

}
