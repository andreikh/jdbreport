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
package jdbreport.design.model.xml;

import jdbreport.design.model.GroupKey;
import jdbreport.model.Cell;
import jdbreport.model.DetailGroup;
import jdbreport.model.io.ResourceReader;
import jdbreport.model.io.xml.CellParser;
import jdbreport.model.io.xml.DBReportParser;
import jdbreport.model.io.xml.DefaultReaderHandler;

import org.xml.sax.Attributes;

/**
 * @version 3.1 15.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class TemplateReportParser extends DBReportParser {

	public TemplateReportParser(DefaultReaderHandler reportHandler, ResourceReader rr) {
		super(reportHandler, rr);
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
							key.setDataSetID(ds);
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
