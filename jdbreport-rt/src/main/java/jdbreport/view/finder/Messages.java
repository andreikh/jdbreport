/*
 * Created on 27.05.2005
 *
 * Copyright (C) 2005-2006 Andrey Kholmanskih. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, write to the 
 *
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 * 
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
 */
package jdbreport.view.finder;

import jdbreport.util.Resources;

import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @version 1.0 06/24/06
 * @author Andrey Kholmanskih
 *
 */
public class Messages implements Resources {

    private static final String ICONS_PATH= "/jdbreport/view/finder/";
    
	public static ResourceBundle resource = 
	    	ResourceBundle.getBundle("jdbreport.view.finder.messages",
	    	        Locale.getDefault());
	
	private static Resources res = new Messages(); 

    public Messages() {
        super();
    }

    public static Resources getResources() {
        return res;
    }
    
    public String getString(String name) {
        return resource.getString(name);
    }
    
    public Icon getIcon(String fileName) {
        return new ImageIcon(resource.getClass().getResource(ICONS_PATH+fileName));
    }

    public ResourceBundle getResourceBungle() {
        return resource;
    }
}