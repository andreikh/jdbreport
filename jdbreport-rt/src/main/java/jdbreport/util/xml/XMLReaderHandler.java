/*
 * Created 06/24/06
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
package jdbreport.util.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.util.LinkedList;
import java.util.List;

/**
 * @version 3.0 13.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public abstract class XMLReaderHandler extends DefaultHandler {

	private StringBuffer currentValue = new StringBuffer();
	private static final StringBuffer nullValue = new StringBuffer();
	private List<XMLParser> stack = new LinkedList<>();
	private List<StringBuffer> valueStack = new LinkedList<>();
	protected XMLParser handler;
	protected boolean isParse = false;
	protected String rootName;
	protected String version;
	private XMLReader reader;

	public XMLReaderHandler() {
		super();
	}

	public XMLReaderHandler(XMLReader reader) {
		this();
		this.reader = reader;
	}

	public String getVersion() {
		return version;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		pushCurrentValue();
	}

	private void pushCurrentValue() {
		valueStack.add(currentValue);
		currentValue = null;
	}

	private void popCurrentValue() {
		currentValue = valueStack.remove(valueStack.size() - 1);
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (handler != null) {
			handler.endElement(qName, currentValue == null ? nullValue
					: currentValue);
			popCurrentValue();
		}

	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (isParse) {
			if (currentValue == null)
				currentValue = new StringBuffer();
			currentValue.append(ch, start, length);
			if (handler != null) {
				handler.characters(currentValue);
			}
		}
	}

	public void pushHandler(XMLParser newhandler) {
		if (this.handler != null)
			stack.add(this.handler);
		this.handler = newhandler;
	}

	public void popHandler(String name) throws SAXException {
		if (stack.size() == 0)
			return;
		handler = stack.remove(stack.size() - 1);
		handler.endElement(name, null);
	}

	/**
	 * @param reader
	 *            The reader to set.
	 */
	public void setReader(XMLReader reader) {
		this.reader = reader;
	}

	/**
	 * @return Returns the reader.
	 */
	public XMLReader getReader() {
		return reader;
	}

}
