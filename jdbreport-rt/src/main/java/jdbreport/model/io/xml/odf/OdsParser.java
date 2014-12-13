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
