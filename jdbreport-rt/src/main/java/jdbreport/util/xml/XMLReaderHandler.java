/*
 * Created 06/24/06
 * 
 * Copyright (C) 2006-2012 Andrey Kholmanskih. All rights reserved.
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
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.util.LinkedList;
import java.util.List;

/**
 * @version 1.7 19.06.2012
 * @author Andrey Kholmanskih
 * 
 */
public abstract class XMLReaderHandler extends DefaultHandler {

	private StringBuffer currentValue = new StringBuffer();
	private static final StringBuffer nullValue = new StringBuffer();
	private List<XMLParser> stack = new LinkedList<XMLParser>();
	private List<StringBuffer> valueStack = new LinkedList<StringBuffer>();
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
