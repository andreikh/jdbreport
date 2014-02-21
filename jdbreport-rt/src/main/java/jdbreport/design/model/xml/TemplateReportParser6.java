/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2008 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.design.model.xml;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.Properties;

import jdbreport.design.model.GroupKey;
import jdbreport.design.model.TemplateBook;
import jdbreport.design.model.TemplateModel;
import jdbreport.model.Cell;
import jdbreport.model.DetailGroup;
import jdbreport.model.Group;
import jdbreport.model.io.xml.DBReportParser6;
import jdbreport.model.io.xml.DefaultReaderHandler;
import jdbreport.source.JdbcDataSet;
import jdbreport.source.JdbcReportSource;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import and.util.xml.XMLParser;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class TemplateReportParser6 extends DBReportParser6 {

	private boolean inDataSets;
	private boolean inDataSet;
	private JdbcDataSet ds;
	private boolean inDsItem;
	private JdbcReportSource source;
	private boolean inGroupKey;
	private boolean inGroupItem;
	private GroupKey currentKey;
	private int currentKeyIndex;

	public TemplateReportParser6(DefaultReaderHandler reportHandler) {
		super(reportHandler);
	}

	public boolean startElement(String name, Attributes attributes) {
		if (inDataSet) {
			if (name.equals("ID")) {
				return true;
			}
			if (name.equals("query")) {
				return true;
			}
			if (name.equals("LinkID")) {
				return true;
			}
			if (name.equals("item")) {
				inDsItem = true;
				return true;
			}
		}
		if (inDataSets) {
			if (name.equals("DataSet")) {
				inDataSet = true;
				ds = new JdbcDataSet();
				return true;
			}
			if (name.equals("Properties")) {
				Properties p = new Properties();
				for (int i = 0; i < attributes.getLength(); i++) {
					p.put(attributes.getQName(i), attributes.getValue(i));
				}
				source.setProperties(p);
				return true;
			}
		}
		if (name.equals("vars")) {
			return true;
		}
		if (name.equals("DataSetList") && !inDataSets) {
			inDataSets = true;
			source = new JdbcReportSource();
			return true;
		}
		if (inGroupItem) {
			if ((name.equals("text")) || (name.equals("GroupIndex"))) {
				return true;
			}
			if (name.equals("prop")) {
				String s = attributes.getValue("tableID");
				if (s != null) {
					currentKey.setDatasetID(s);
				}
				return true;
			}
		}
		if (inGroupKey && name.equals("item")) {
			inGroupItem = true;
			currentKey = new GroupKey();
			return true;
		}
		if (name.equals("GroupKey")) {
			inGroupKey = true;
			return true;
		}
		boolean result = super.startElement(name, attributes);
		return result;
	}

	public void endElement(String name, StringBuffer value) throws SAXException {
		if (inDataSet) {
			if (name.equals("ID")) {
				try {
					int n = Integer.parseInt(value.toString());
					ds.setId("" + n);
					return;
				} catch (Exception e) {
				}
				ds.setId(value.toString());
				return;
			}
			if (name.equals("query")) {
				ds.setQuery(value.toString());
				return;
			}
			if (name.equals("LinkID")) {
				ds.setMasterId(value.toString());
				return;
			}
			if (inDsItem) {
				if (name.equals("item")) {
					inDsItem = false;
				}
				return;
			}
			if (name.equals("DataSet")) {
				inDataSet = false;
				source.add(ds);
				return;
			}
			return;
		}
		if (inDataSets && name.equals("DataSetList")) {
			inDataSets = false;
			getTemplateReportBook().addSource(source);
			return;
		}
		if (name.equals("vars")) {
			StringReader sr = new StringReader(value.toString());
			LineNumberReader lr = new LineNumberReader(sr);
			String varName;
			String varValue;
			try {
				String var = lr.readLine();
				while (var != null) {
					int i = var.indexOf('=');
					if (i > 0) {
						varName = var.substring(0, i);
						varValue = var.substring(i + 1);
					} else {
						varName = var;
						varValue = "";
					}

					getTemplateReportBook().setVarValue(varName, varValue);
					var = lr.readLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		if (inGroupItem) {
			if (name.equals("item")) {
				inGroupItem = false;
				Group group = getGroup(currentKeyIndex);
				if (group != null) {
					((DetailGroup) group).addKey(currentKey);
				}
				currentKey = null;
				return;
			}
			if (name.equals("text")) {
				currentKey.setName(value.toString());
				return;
			}
			if (name.equals("GroupIndex")) {
				currentKeyIndex = Integer.parseInt(value.toString());
				return;
			}
		}
		if (inGroupKey && name.equals("GroupKey")) {
			inGroupKey = false;
			return;
		}
		super.endElement(name, value);
	}

	public TemplateBook getTemplateReportBook() {
		return (TemplateBook) getReportBook();
	}

	public TemplateModel getTemplateModel() {
		return (TemplateModel) getReportModel();
	}

	protected XMLParser createCellHandler(Cell cell) {
		return new TemplCellHandler6(getDefaultReportHandler(), cell);
	}

}
