/*
 * JDBReport Designer
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
package jdbreport.design.grid;

import jdbreport.util.Resources;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class TemplateReportResources implements Resources {

	private static final String resourcePath = "/jdbreport/design/resources/";

	private static ResourceBundle resource = ResourceBundle.getBundle(
			"jdbreport.design.resources.Report", Locale.getDefault());

	private static TemplateReportResources resources;

	public static Resources getInstance() {
		if (resources == null) {
			resources = new TemplateReportResources();
		}
		return resources;
	}

	public Icon getIcon(String fileName) {
		if (!fileName.startsWith("/"))
			fileName = resourcePath + fileName;
		return new ImageIcon(getClass().getResource(fileName));
	}

	public ResourceBundle getResourceBungle() {
		return resource;
	}

	public String getString(String name) {
		return getResourceBungle().getString(name);
	}

}
