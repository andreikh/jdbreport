/*
 * JDBReport Designer
 * 
 * Copyright (C) 2006-2014 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.design.view.clipboard;

import jdbreport.design.model.xml.TemplCellHandler;
import jdbreport.model.Cell;
import jdbreport.view.clipboard.ClipboardParser;
import jdbreport.view.clipboard.FragmentHandler;
import jdbreport.model.io.ResourceWriter;
import jdbreport.model.io.xml.CellParser;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class TemplateClipboardParser extends ClipboardParser {

	public TemplateClipboardParser() {
		super();
	}

	public TemplateClipboardParser(FragmentHandler reportHandler, int startRow,
			int startCol) {
		super(reportHandler, startRow, startCol);
	}

	protected CellParser createCellHandler(Cell cell) {
		return new TemplCellHandler(getDefaultReportHandler(), cell, null);
	}

	protected CellParser createCellHandler() {
		return new TemplCellHandler(getDefaultReportHandler(), (ResourceWriter)null);
	}

	protected String getRootName() {
		return "DesignReport";
	}

}
