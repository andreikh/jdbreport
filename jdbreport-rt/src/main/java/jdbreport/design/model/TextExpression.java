/*
 * TextExpression.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2008-2014 Andrey Kholmanskih
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
