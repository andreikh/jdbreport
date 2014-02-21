/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2010 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.design.model.xml;

import jdbreport.design.model.CellObject;
import jdbreport.model.Cell;
import jdbreport.model.io.xml.CellParser6;
import jdbreport.model.io.xml.DefaultReaderHandler;

import org.xml.sax.Attributes;

/**
 * @version 1.4 26.08.2010
 * @author Andrey Kholmanskih
 * 
 */
public class TemplCellHandler6 extends CellParser6 {

	protected static final String EXTPROP = "extProp";

	protected boolean inExtProp;

	protected CellObject cellObject;

	protected static final String[] AGR_FUNC_NAME = { "None", "Sum", "Max", "Min" };

	public TemplCellHandler6(DefaultReaderHandler reportHandler) {
		super(reportHandler);
	}

	public TemplCellHandler6(DefaultReaderHandler reportHandler, Cell cell) {
		super(reportHandler, cell);
		assert (cell instanceof CellObject);
		cellObject = (CellObject) cell;
	}

	boolean inExprList;

	@Override
	public boolean startElement(String name, Attributes attributes) {
		if (inExtProp) {
			if (name.equals("exprlist")) {
				inExprList = true;
				return false;
			}
			if (!inExprList) {
				if (name.equals("prop")) {
					String s = null;

					s = attributes.getValue("tableID");
					if (s != null && s.trim().length() > 0) {
						cellObject.setDataSetId(s);
						String text = cellObject.getFieldName();
						if (text != null)
							cellObject.setFieldName(text.toUpperCase());
					}
					s = attributes.getValue("type");
					if (s != null && s.length() > 0) {
						cellObject.setType(Integer.parseInt(s));
					}
					return false;
				}
				if (name.equals("agr-func")) {
					return true;
				}
				if (name.equals("norep")) {
					return true;
				}
				if (name.equals("function-name")) {
					return true;
				}
			}
			return false;
		}
		if (name.equals(EXTPROP)) {
			inExtProp = true;
			inExprList = false;
			return true;
		}
		return super.startElement(name, attributes);
	}

	@Override
	public void endElement(String name, StringBuffer value) {

		if (name.equals(EXTPROP)) {
			inExtProp = false;
			return;
		}
		if (inExtProp) {
			if (!inExprList) {
				if (name.equals("agr-func")) {
					for (int i = 1; i < AGR_FUNC_NAME.length; i++) {
						if (AGR_FUNC_NAME[i].equals(value.toString()))
							cellObject.setTotalFunction(i);
					}
					return;
				}
				if (name.equals("norep")) {
					cellObject.setNotRepeate(Boolean.parseBoolean(value
							.toString()));
					return;
				}
				if (name.equals("function-name")) {
					cellObject.setFunctionName(value.toString());
					return;
				}
			}
			if (name.equals("exprlist")) {
				inExprList = false;
				return;
			}
		}
		super.endElement(name, value);
	}

}
