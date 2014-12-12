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
package jdbreport.model.io.xml.odf;

import jdbreport.model.Cell;
import jdbreport.model.ReportModel;
import jdbreport.model.Units;
import jdbreport.model.io.xml.DefaultReaderHandler;

import jdbreport.util.xml.XMLParser;
import org.xml.sax.Attributes;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class OdsParser extends OdsReportParser {

	private boolean inBody;
	private boolean inSheet;
	private OdsStyleParser styleParser;
	private OdsSettingsParser settingsParser;
	private XMLParser metaParser;

	public OdsParser(DefaultReaderHandler reportHandler) {
		super(reportHandler);
	}

	public boolean startElement(String name, Attributes attributes) {
		if (inBody) {
			if (inSheet) {
				if (name.equals("table:table")) {
					setCurrentModel(getReportBook().add());
					getReportModel().setReportTitle(
							attributes.getValue("table:name"));
					setTableStyle(attributes.getValue("table:style-name"));
					getHandler().pushHandler(
							new OdsTableParser(getDefaultReportHandler()));
					return true;
				}
			} else if (name.equals("office:spreadsheet")) {
				inSheet = true;
				return true;
			}
		} else if (name.equals("office:body")) {
			inBody = true;
			return true;
		}
		if (name.equals("office:meta")) {
			getHandler().pushHandler(getMetaParser());
			return true;
		}
		if (name.equals("office:automatic-styles")
				|| name.equals("office:styles")
				|| name.equals("office:master-styles")) {
			getHandler().pushHandler(getStyleParser());
			return true;
		}
		if (name.equals("office:settings")) {
			getHandler().pushHandler(getSettingsParser());
			return true;
		}
		return false;
	}

	private void setTableStyle(String value) {
		if (value == null || value.length() == 0)
			return;
		ReportModel model = getReportModel();
		TableStyle tableStyle = getTableStyles().get(value);
		if (tableStyle != null) {
			model.setVisible(tableStyle.isDisplay());
			MasterPageStyle masterPageStyle = getMasterPageStyles().get(
					tableStyle.getMasterPageName());
			if (masterPageStyle != null) {
				PageStyle pageStyle = getPageStyles().get(
						masterPageStyle.getPageLayout());
				if (pageStyle != null) {
					model.getReportPage().setOrientation(
							pageStyle.getOrientation());
					model.getReportPage().setSize(pageStyle.getWidth(),
							pageStyle.getHeight(), Units.PT);
					model.getReportPage().setMargin(pageStyle.getLeft(),
							pageStyle.getTop(), pageStyle.getRight(),
							pageStyle.getBottom(), Units.PT);
				}
			}
		}
	}

	private XMLParser getMetaParser() {
		if (metaParser == null)
			metaParser = new OdsMetaParser(getDefaultReportHandler());
		return metaParser;
	}

	private OdsStyleParser getStyleParser() {
		if (styleParser == null)
			styleParser = new OdsStyleParser(getDefaultReportHandler());
		return styleParser;
	}

	private OdsSettingsParser getSettingsParser() {
		if (settingsParser == null)
			settingsParser = new OdsSettingsParser(getDefaultReportHandler());
		return settingsParser;
	}

	public void endElement(String name, StringBuffer value) {
		if (inBody) {
			if (inSheet) {
				if (name.equals("table:table")) {
					if (getReportModel().getRowCount() == 0) {
						getReportBook().remove(getCurrentModel());
						return;
					} else if (getReportModel().getRowCount() == 1
							&& getReportModel().getColumnCount() == 1) {
						Cell cell = getReportModel().getReportCell(0, 0);
						if (cell.getValue() == null
								&& cell.getPicture() == null
								&& (cell.getStyleId() == null || "Default"
										.equals(cell.getStyleId()))) {
							getReportBook().remove(getCurrentModel());
							return;
						}
					}
				}
				if (name.equals("office:spreadsheet")) {
					inSheet = false;
					return;
				}
			}
			if (name.equals("office:body")) {
				inBody = false;
			}
		}
	}

}
