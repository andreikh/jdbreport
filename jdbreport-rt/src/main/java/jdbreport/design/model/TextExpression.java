/*
 * TextExpression.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2008-2011 Andrey Kholmanskih. All rights reserved.
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
 */

package jdbreport.design.model;

import jdbreport.model.ReportException;

/**
 * @version 2.0 10.01.2011
 * @author Andrey Kholmanskih
 *
 */
public class TextExpression implements Expression {

	private String text;

	public TextExpression(String text) {
		this.text = text;
	}
	/* (non-Javadoc)
	 * @see jdbreport.design.model.Expression#getBaseName()
	 */
	public String getBaseName() {
		return null;
	}

	public String getProperty() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see jdbreport.design.model.Expression#getText()
	 */
	public String getText() {
		return text;
	}

	/* (non-Javadoc)
	 * @see jdbreport.design.model.Expression#getValue()
	 */
	public Object getValue() throws ReportException {
		return text;
	}
	
	public int getType() {
		return CellObject.TYPE_NONE;
	}
	
	/* (non-Javadoc)
	 * @see jdbreport.design.model.Expression#getFormatValue()
	 */
	public String getFormatValue() {
		return text != null ? text : "";
	}

}
