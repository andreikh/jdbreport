/*
 * Copyright (C) 2010-2014 Andrey Kholmanskih
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
package jdbreport.design.grid;

import javax.swing.table.TableColumnModel;

import jdbreport.grid.JReportHeader;

/**
 * @author Andrey Kholmanskih
 *
 * @version 1.0 19.04.2010
 */
public class TemplateHeader extends JReportHeader {

	private static final long serialVersionUID = 1L;

	/**
	 * @param cm
	 */
	public TemplateHeader(TableColumnModel cm) {
		super(cm);
	}

	@Override
	public boolean getResizingAllowed() {
		return true;
	}

	@Override
	public boolean getReorderingAllowed() {
		return true;
	}

}
