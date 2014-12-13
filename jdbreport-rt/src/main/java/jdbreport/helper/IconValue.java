/*
 * IconValue.java
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


/**
 * @version 1.2 02/13/09
 * @author Andrey Kholmanskih
 * 
 */
public class IconValue extends AbstractImageValue<ImageIcon> {

	private static final long serialVersionUID = 1L;

	public static void registerValue() {
		ImageValue.registerValue();
	}

	public IconValue() {

	}

	public IconValue(ImageIcon icon) {
		this();
		this.icon = icon;
	}


	public ImageIcon getValue() {
		return icon;
	}

	public void setValue(ImageIcon icon) {
		this.icon = icon;
	}

	public void setValue(Image image) {
		this.icon = new ImageIcon(image);
	}

}
