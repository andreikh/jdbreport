/*
 * Copyright (C) 2010 Andrey Kholmanskih. All rights reserved.
 *
*/
package jdbreport.design.grid;

import jdbreport.grid.JReportGrid;
import jdbreport.grid.RowHeader;

/**
 * @author Andrey Kholmanskih
 *
 * @version 1.0 19.04.2010
 */
public class TemplateRowHeader extends RowHeader {

	private static final long serialVersionUID = 1L;

	/**
	 * @param table
	 */
	public TemplateRowHeader(JReportGrid table) {
		super(table);
	}

	@Override
	public boolean getReorderingAllowed() {
		return true;
	}

	@Override
	public boolean getResizingAllowed() {
		return true;
	}

}
