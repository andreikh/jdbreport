/*
 * ImageValue.java
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2009-2014 Andrey Kholmanskih
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

package jdbreport.helper;

import java.awt.Image;

import javax.swing.ImageIcon;

import jdbreport.model.ReportCell;

/**
 * @version 1.2 02/13/09
 * @author Andrey Kholmanskih
 * 
 */
public class ImageValue extends AbstractImageValue<Image> {

	private static final long serialVersionUID = 1L;

	public static void registerValue() {
		ReportCell.setDefaultCellValueClass(Image.class, ImageValue.class,
				ImageReportRenderer.class.getName(),
				jdbreport.grid.NullCellEditor.class.getName());
		ReportCell.setDefaultCellValueClass(ImageIcon.class, IconValue.class,
				ImageReportRenderer.class.getName(),
				jdbreport.grid.NullCellEditor.class.getName());
	}

	public ImageValue() {

	}

	public ImageValue(Image image) {
		this();
		this.icon = new ImageIcon(image);
	}

	public Image getValue() {
		return icon.getImage();
	}

	public void setValue(Image image) {
		this.icon = new ImageIcon(image);
	}


}
