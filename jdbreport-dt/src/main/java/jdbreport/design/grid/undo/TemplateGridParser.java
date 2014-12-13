/*
 * TemplateGridParser.java
 *
 * JDBReport Designer
 * 
 * Copyright (C) 2007-2014 Andrey Kholmanskih
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

package jdbreport.design.grid.undo;

import java.io.PrintWriter;

import jdbreport.design.model.GroupKey;
import jdbreport.design.model.xml.TemplCellHandler;
import jdbreport.design.model.xml.TemplateReportParser;
import jdbreport.grid.undo.GridHandler;
import jdbreport.grid.undo.GridParser;
import jdbreport.model.DetailGroup;
import jdbreport.model.Group;
import jdbreport.model.ReportModel;
import jdbreport.model.io.SaveReportException;
import jdbreport.model.io.xml.CellParser;
import jdbreport.util.xml.XMLParser;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 *
 */
public class TemplateGridParser extends GridParser {

	public TemplateGridParser(GridHandler reportHandler) {
		super(reportHandler);
	}

	public TemplateGridParser() {
		super(null);
	}

	protected XMLParser createSheetHandler() {
		return new TemplateReportParser(getDefaultReportHandler(), null);
	}

	protected CellParser createCellHandler() {
		return new TemplCellHandler(getDefaultReportHandler(), null);
	}

	protected String getSheetName() {
		return "DesReportGrid";
	}
	
	@Override
	protected void writeGroupChild(PrintWriter writer, Group group,
			ReportModel model) throws SaveReportException {
		if (group instanceof DetailGroup) {
			DetailGroup dGroup = (DetailGroup) group;
			
			for (int i = 0; i < dGroup.getKeyCount(); i++) {
				GroupKey key = dGroup.getKey(i);
				if (key.getName() != null) {
					String dsId;
					if (key.getDatasetID() != null)
						dsId = " dataset=\"" + key.getDatasetID() + "\"";
					else
						dsId = "";
					writer.println("<GroupKey name=\"" + key.getName() + "\""
							+ dsId + " />");
				}
			}
			
			if (dGroup.getMinRowCount() > 0 || dGroup.getMaxRowCount() > 0) {
				writer.println("<Limits min=\"" + dGroup.getMinRowCount() + "\" max=\""
						+ dGroup.getMaxRowCount() + "\" />");   
			}
		}
		super.writeGroupChild(writer, group, model);
	}


}
