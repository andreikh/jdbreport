/*
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2016 Andrey Kholmanskih
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
package jdbreport.model;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class CellValueInfo {

	private Class<?> cellValueClass;
	private String rendererClass;
	private String editorClass;

	public CellValueInfo(Class<?> cellValueClass, String rendererClass,
			String editorClass) {
		this.cellValueClass = cellValueClass;
		this.rendererClass = rendererClass;
		this.editorClass = editorClass;
	}

	/**
	 * @return the cell value class
	 */
	public Class<?> getCellValueClass() {
		return cellValueClass;
	}

	/**
	 * @return the cell editor class
	 */
	public String getEditorClass() {
		return editorClass;
	}

	/**
	 * @return the cell renderer class
	 */
	public String getRendererClass() {
		return rendererClass;
	}

}
