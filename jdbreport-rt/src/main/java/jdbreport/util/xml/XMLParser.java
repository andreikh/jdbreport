/*
 * Created on 25.01.2005
 *
 * Copyright (C) 2005-2006 Andrey Kholmanskih. All rights reserved.
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

	public boolean startElement(String name, final Attributes attributes)
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
	public void endElement(String name, final StringBuffer value)
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
	public void characters(StringBuffer ch) throws SAXException;

}
