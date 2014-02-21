/*
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2008 Andrey Kholmanskih. All rights reserved.
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


/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 *
 *Base class for ColumnStyle and RowStyle 
 */
abstract class CommonStyle implements Cloneable {

	public enum Break {auto, column, page}

	private Break break_;
	private boolean after;

	public CommonStyle(Break brk_, boolean after) {
		this.break_ = brk_;
		this.after = after;
	}
	/**
	 * column break before or after a table column 
	 * @return enum Break - auto, column or page   
	 */
	public Break getBreak() {
		return break_;
	}

	/**
	 * If true getBreak() returns break_before properties else
	 * returns break_after
	 * @return 
	 */
	public boolean isAfter() {
		return after; 
	}

	public Object clone()  {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

}
