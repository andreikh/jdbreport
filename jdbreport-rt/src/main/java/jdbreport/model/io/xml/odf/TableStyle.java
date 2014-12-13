/*
 * TableStyle.java 04.11.2006
 *
 * JDBReport Generator
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
package jdbreport.model.io.xml.odf;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 */
class TableStyle {

	private String name;
	private String masterPageName;
	private boolean display;

	public TableStyle(String name, String masterPageName) {
		super();
		this.name = name;
		this.masterPageName = masterPageName;
		this.display = true;
	}

	/**
	 * @return the display
	 */
	public boolean isDisplay() {
		return display;
	}

	/**
	 * @param display
	 *            the display to set
	 */
	public void setDisplay(boolean display) {
		this.display = display;
	}

	/**
	 * @return the masterPageName
	 */
	public String getMasterPageName() {
		return masterPageName;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
