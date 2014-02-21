/*
 * Copyright (C) 2010 Andrey Kholmanskih. All rights reserved.
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
