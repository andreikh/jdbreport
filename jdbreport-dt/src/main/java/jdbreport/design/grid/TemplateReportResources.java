/*
 * JDBReport Designer
 * 
 * Copyright (C) 2006-2008 Andrey Kholmanskih. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, write to the 
 * 
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
 * 
 */
package jdbreport.design.grid;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import and.properties.Resources;

/**
 * @version 1.1 03/09/08
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
