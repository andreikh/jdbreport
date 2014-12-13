/*
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

package jdbreport.view;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * @version 1.0 08.09.2009
 * @author Andrey Kholmanskih
 *
 */
public class ImagePreview extends JComponent implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	ImageIcon icon = null;
	File file = null;

	public ImagePreview(JFileChooser fc) {
		setPreferredSize(new Dimension(100, 50));
		fc.addPropertyChangeListener(this);
	}

	public void loadImage() {
		if (file == null) {
			icon = null;
			return;
		}

		ImageIcon tmpIcon = new ImageIcon(file.getPath());
		int w = getPreferredSize().width - 10 ;
		if (tmpIcon.getIconWidth() > w) {
			icon = new ImageIcon(tmpIcon.getImage().getScaledInstance(
					 w, -1, Image.SCALE_DEFAULT));
		} else {
			icon = tmpIcon;
		}
	}

	public void propertyChange(PropertyChangeEvent e) {
		boolean change = false;
		String propertyName = e.getPropertyName();

		if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(propertyName)) {
			file = null;
			change = true;

		} else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(propertyName)) {
			file = (File) e.getNewValue();
			change = true;
		}

		if (change) {
			icon = null;
			if (isShowing()) {
				loadImage();
				repaint();
			}
		}
	}

	protected void paintComponent(Graphics g) {
		if (icon == null)
			loadImage();

		if (icon != null) {
			int x = getWidth() / 2 - icon.getIconWidth() / 2;
			int y = getHeight() / 2 - icon.getIconHeight() / 2;

			if (y < 0)
				y = 0;

			if (x < 5)
				x = 5;

			icon.paintIcon(this, g, x, y);
		}
	}
}
