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
package jdbreport.model.io.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import jdbreport.model.ReportBook;
import jdbreport.model.io.LoadReportException;
import jdbreport.model.io.ReportReader;
import jdbreport.model.io.ResourceReader;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @version 2.0 20.12.2009
 * @author Andrey Kholmanskih
 * 
 */
public class XMLReportReader implements ReportReader {

	private ResourceReader resourceReader;

	public XMLReportReader(ResourceReader rr) {
		super();
		this.resourceReader = rr;
	}

	protected ResourceReader getResourceReader() {
		return resourceReader;
	}
	
	protected org.xml.sax.helpers.DefaultHandler createHandler(
			ReportBook reportBook, XMLReader reader) {
		return new JReportHandler(reportBook, reader, getResourceReader());
	}

	public void load(InputStream in, ReportBook reportBook)
			throws LoadReportException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser;
		try {
			saxParser = factory.newSAXParser();
			org.xml.sax.helpers.DefaultHandler handler = createHandler(
					reportBook, saxParser.getXMLReader());
			reportBook.clear();
			saxParser.parse(in, handler);
		} catch (ParserConfigurationException e) {
			throw new LoadReportException(e);
		} catch (SAXException e) {
			throw new LoadReportException(e);
		} catch (IOException e) {
			throw new LoadReportException(e);
		}
	}

	public void load(Reader reader, ReportBook reportBook)
			throws LoadReportException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser;
		try {
			saxParser = factory.newSAXParser();
			org.xml.sax.helpers.DefaultHandler handler = createHandler(
					reportBook, saxParser.getXMLReader());
			reportBook.clear();
			saxParser.parse(new InputSource(reader), handler);
		} catch (ParserConfigurationException e) {
			throw new LoadReportException(e);
		} catch (SAXException e) {
			throw new LoadReportException(e);
		} catch (IOException e) {
			throw new LoadReportException(e);
		}
	}

}
