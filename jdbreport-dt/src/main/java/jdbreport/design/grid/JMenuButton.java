/**
 * Created	08.02.2009
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
package jdbreport.design.grid;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * The button with the dropping menu
 * @author Andrey Kholmanskih
 *
 * @version	1.0
 */
public class JMenuButton extends JButton implements MouseListener {

	private static final long serialVersionUID = 1L;
	private JPopupMenu dropMenu;
	private boolean showMenu;
	private float menuZone = 1f / 3;

	public JMenuButton() {
		super();
	}
	
	public JMenuButton(String text) {
		super(text);
	}

	public JMenuButton(Action a) {
		super(a);
	}

	public JMenuButton(Icon icon) {
		super(icon);
	}

	public JMenuButton(String text, Icon icon) {
		super(text, icon);
	}

    protected void init(String text, Icon icon) {
    	super.init(text, icon);
    	addMouseListener(this);
    }

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}
	
	public void mousePressed(MouseEvent e) {
	       if (SwingUtilities.isLeftMouseButton(e) && dropMenu != null) {
	    	   showMenu = (e.getPoint().getX() > getWidth() * (1 - menuZone));
	       }		
	}

	/**
	 * 
	 * @param zone between 0 and 1
	 */
	public void setMenuZone(float zone) {
		menuZone  = zone;
	}
	
	public JPopupMenu getDropMenu() {
		return dropMenu;
	}

	public void setMenu(JPopupMenu menu) {
		this.dropMenu = menu;
	}

    protected void fireActionPerformed(ActionEvent event) {
    	if (!showMenu) {
    		super.fireActionPerformed(event);
    	} else {
		   dropMenu.show(this, 0, this.getHeight());
		   showMenu = false;
    	}
    }

    
}
