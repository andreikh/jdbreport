/*
 * Created on 27.05.2005
 *
 * Copyright (C) 2005-2014 Andrey Kholmanskih
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
