/*
 * JDBReport Designer
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
package jdbreport.design.view.clipboard;

import jdbreport.model.ReportModel;
import jdbreport.util.xml.XMLParser;
import jdbreport.view.clipboard.FragmentHandler;

import org.xml.sax.XMLReader;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class TemplateFragmentHandler extends FragmentHandler {

	public TemplateFragmentHandler(ReportModel model, XMLReader reader,
			int startRow, int startCol) {
		super(model, reader, startRow, startCol);
	}

	protected XMLParser getClipboardHandler() {
		return new TemplateClipboardParser(this, startRow, startCol);
	}

}
