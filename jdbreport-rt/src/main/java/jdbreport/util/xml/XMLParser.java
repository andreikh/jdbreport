/*
 * Created on 25.01.2005
 *
 * Copyright (C) 2005-2014 Andrey Kholmanskih
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
package jdbreport.util.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * 
 * 
 * Receives notification of the logical content of a document.
 * 
 * @version 1.0 06/24/06
 * @author Andrey Kholmanskih
 * 
 */
public interface XMLParser {

	/**
	 * Receives notification of the start of an element.
	 * 
	 * <p>
	 * By default, do nothing. Application writers may override this method in a
	 * subclass to take specific actions at the start of each element (such as
	 * allocating a new tree node or writing output to a file).
	 * </p>
	 * 
	 * @param name
	 *            The qualified name (with prefix)
	 * @param attributes
	 *            The attributes attached to the element. If there are no
	 *            attributes, it shall be an empty Attributes object.
	 */

	boolean startElement(String name, final Attributes attributes)
			throws SAXException;

	/**
	 * Receives notification of the end of an element.
	 * 
	 * <p>
	 * By default, do nothing. Application writers may override this method in a
	 * subclass to take specific actions at the end of each element (such as
	 * finalising a tree node or writing output to a file).
	 * </p>
	 * 
	 * @param name
	 *            The qualified name (with prefix)
	 */
	void endElement(String name, final StringBuffer value)
			throws SAXException;

	/**
	 * Receives notification of character data inside an element.
	 * 
	 * <p>
	 * By default, do nothing. Application writers may override this method to
	 * take specific actions for each chunk of character data (such as adding
	 * the data to a node or buffer, or printing it to a file).
	 * </p>
	 * 
	 */
	void characters(StringBuffer ch) throws SAXException;

}
