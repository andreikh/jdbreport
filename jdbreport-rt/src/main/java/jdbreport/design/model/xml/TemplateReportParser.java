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
package jdbreport.design.model.xml;

import jdbreport.design.model.GroupKey;
import jdbreport.design.model.TemplateModel;
import jdbreport.model.Cell;
import jdbreport.model.DetailGroup;
import jdbreport.model.io.ResourceReader;
import jdbreport.model.io.xml.CellParser;
import jdbreport.model.io.xml.DBReportParser;
import jdbreport.model.io.xml.DefaultReaderHandler;

import org.xml.sax.Attributes;

/**
 * @version 2.0 21.12.2009
 * @author Andrey Kholmanskih
 * 
 */
public class TemplateReportParser extends DBReportParser {

	public TemplateReportParser(DefaultReaderHandler reportHandler, ResourceReader rr) {
		super(reportHandler, rr);
	}

	public TemplateModel getTemplateModel() {
		return (TemplateModel) getReportModel();
	}

	protected CellParser createCellHandler(Cell cell) {
		return new TemplCellHandler(getDefaultReportHandler(), cell, getResourceReader());
	}

	@Override
	public boolean startElement(String name, Attributes attributes) {
		boolean result = super.startElement(name, attributes);
		if (!result) {
			if (inRows) {
				if (currentGroup instanceof DetailGroup) {
					if (name.equals("GroupKey")) {
						GroupKey key = new GroupKey(attributes.getValue("name"));
						String ds = attributes.getValue("dataset");
						if (ds != null) {
							key.setDatasetID(ds);
						}
						((DetailGroup) currentGroup).addKey(key);
						return true;
					} else if (name.equals("Limits")) {
						String min = attributes.getValue("min");
						String max = attributes.getValue("max");
						if (min != null) {
							((DetailGroup) currentGroup).setMinRowCount(Integer
									.parseInt(min));
						}
						if (max != null) {
							((DetailGroup) currentGroup).setMaxRowCount(Integer
									.parseInt(max));
						}

					}
				}
			}
		}
		return result;
	}

}
