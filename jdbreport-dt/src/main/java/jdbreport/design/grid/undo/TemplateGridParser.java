/*
 * TemplateGridParser.java
 *
 * JDBReport Designer
 * 
 * Copyright (C) 2007-2009 Andrey Kholmanskih. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, write to the 
 * 
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
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
