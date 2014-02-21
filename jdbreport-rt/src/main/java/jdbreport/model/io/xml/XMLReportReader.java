/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2009 Andrey Kholmanskih. All rights reserved.
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
 * Andrey Kholmanskih
 * support@jdbreport.com
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
