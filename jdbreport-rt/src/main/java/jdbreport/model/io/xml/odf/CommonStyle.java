/*
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2014 Andrey Kholmanskih
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
	 * @return true if break_after
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
