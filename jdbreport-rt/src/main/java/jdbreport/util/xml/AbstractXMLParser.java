/*
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
package jdbreport.util.xml;

import org.xml.sax.SAXException;

/**
 * @version 1.0 06/24/06
 * @author Andrey Kholmanskih
 * 
 */
public abstract class AbstractXMLParser implements XMLParser {

	private XMLReaderHandler handler;

	public AbstractXMLParser(XMLReaderHandler handler) {
		this.handler = handler;
	}

	/**
	 * @return Returns the handler.
	 */
	public XMLReaderHandler getHandler() {
		return handler;
	}

	public void characters(StringBuffer value) throws SAXException {
	}

	public void popHandler(String name) throws SAXException {
		getHandler().popHandler(name);
	}

	public void pushHandler(XMLParser newhandler) {
		getHandler().pushHandler(newhandler);
	}

}
